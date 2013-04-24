/**
 This software was developed at the National Institute of Standards and Technology by employees
of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
United States Code this software is not subject to copyright protection and is in the public domain.
This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
modified freely provided that any derivative works bear some notice that they are derived from it, and any
modified versions bear some notice that they have been modified.

Project: NWHIN-DIRECT
Authors: William Majurski
		 Frederic de Vaulx
		 Diane Azais
		 Julien Perugini
		 Antoine Gerardin
		
 */


package gov.nist.direct.messageProcessor.direct.directImpl;

import gov.nist.direct.messageProcessor.cert.CertificateLoader;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.SMIMEUtil;

public class WrappedMessageProcessor {
	
	private boolean wrapped;
	private boolean isMDN;
	
	private final String BC = BouncyCastleProvider.PROVIDER_NAME;
	private byte[] directCertificate;
	private String password;
	
	static Logger logger = Logger.getLogger(WrappedMessageProcessor.class);

	public WrappedMessageProcessor() {
		wrapped = false;
		isMDN = false;
	}
	
	public void messageParser(ErrorRecorder er, byte[] inputDirectMessage, byte[] _directCertificate, String _password) {
		directCertificate = _directCertificate;
		password = _password;
		
		// MessageProcessorInterface<MimeMessage> parser = new MimeMessageParser();
		MimeMessageParser parser = new MimeMessageParser();
		MimeMessage mm = MimeMessageParser.parseMessage(er, inputDirectMessage);
		try {
			processPart(er, mm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void processPart(ErrorRecorder er, Part p) throws Exception {
		
		if (p == null) {
			logger.debug("Null part");
			return;
		}
		//er.detail("Processing Part");
		// If the Part is a Message then first validate the Envelope
		if (p instanceof Message){
			System.out.println("Message");
		}
		
		for (Enumeration<Header> en=p.getAllHeaders(); en.hasMoreElements(); ) {
			Header hdr = en.nextElement();
			logger.debug("HDR:" + hdr.getName() + " -> " + hdr.getValue());
		}
		logger.info("mimeType=" + p.getContentType());

		/*
		 * Using isMimeType to determine the content type avoids
		 * fetching the actual content data until we need it.
		 */
		if (p.isMimeType("text/plain")) {
			//System.out.println("Text/plain");

		} else if (p.isMimeType("text/xml")) {
			//System.out.println("Text/xml");

		} else if (p.isMimeType("message/rfc822")) {
			System.out.println("Message/rfc822");
			this.wrapped = true;
			Object o = p.getContent();
			if (o instanceof Part) {
				logger.debug("rfc822 contains part");
				processPart(er, (Part) o);
			}

		} else if (p.isMimeType("application/pkcs7-signature"+"  Content Name: "+p.getContent().getClass().getName())) {
			//System.out.println("Signature");
			
		} else if (p.isMimeType("application/pkcs7-mime")) {
			//System.out.println("Encrypted message");
			this.processPart(er, processSMIMEEnvelope(er, p, new ByteArrayInputStream(directCertificate), password));

		} else if (p.isMimeType("application/x-pkcs7-signature")) {
			//System.out.println("Signature");

		} else if (p.isMimeType("application/x-pkcs7-mime")) {
			//System.out.println("Encrypted");

		} else if (p.isMimeType("application/zip")) {
			//System.out.println("XDM Content");

		}  else if (p.isMimeType("application/x-zip-compressed")) {
			//System.out.println("XDM Content");

		} else if (p.isMimeType("application/octet-stream")) {
			//System.out.println("CCDA Content");

		} else if (p.isMimeType("multipart/signed")) {
			
			SMIMESigned s = new SMIMESigned((MimeMultipart)p.getContent());

			//
			// extract the content
			//
			this.processPart(er, s.getContent());

		} else if (p.isMimeType("multipart/*")) {
			//System.out.println("Multipart/mixed");

			MimeMultipart mp = (MimeMultipart)p.getContent();
			int count = mp.getCount();
			for (int i = 0; i < count; i++){
				this.processPart(er, mp.getBodyPart(i));	
			}

		} else if (p.isMimeType("multipart/report")) {
			this.isMDN = true;
		} else if (p.isMimeType("message/disposition-notification")) {
			this.isMDN = true;
		} else {
			System.out.println("Unrecognized part");

		}
	}
	
	public Part processSMIMEEnvelope(ErrorRecorder er, Part p, InputStream certificate, String password) {
		
		CertificateLoader certLoader = null;
		RecipientId     recId = null;		
		
		try {
			certLoader = new CertificateLoader(certificate, password);
			recId = new JceKeyTransRecipientId(certLoader.getX509Certificate());
		} catch (KeyStoreException e1) {
			er.error("No DTS", "Certificate File", "Error in keystore creation KeyStoreException", e1.getMessage(), "-");
		} catch (NoSuchProviderException e1) {
			er.error("No DTS", "Certificate File", "Error in keystore creation NoSuchProviderException", e1.getMessage(), "-");
		} catch (NoSuchAlgorithmException e1) {
			er.error("No DTS", "Certificate File", "Error in keystore creation NoSuchAlgorithmException", e1.getMessage(), "-");
		} catch (CertificateException e1) {
			er.error("No DTS", "Certificate File", "Error in keystore creation CertificateException", e1.getMessage(), "-");
		} catch (IOException e1) {
			er.error("No DTS", "Certificate File", "Error in keystore creation IOException", e1.getMessage(), "-");
		} catch (Exception e1) {
			er.error("No DTS", "Certificate File", "Probably wrong format file or wrong password", e1.getMessage(), "-");
		}


		SMIMEEnveloped m = null;
		try {
			m = new SMIMEEnveloped((MimeMessage)p);
		} catch (MessagingException e1) {
			e1.printStackTrace();

		} catch (CMSException e1) {
			e1.printStackTrace();
			
		}
		RecipientInformationStore   recipients = m.getRecipientInfos();
		RecipientInformation        recipient = recipients.get(recId);

		MimeBodyPart res = null;
		try {
			res = SMIMEUtil.toMimeBodyPart(recipient.getContent(new JceKeyTransEnvelopedRecipient(certLoader.getPrivateKey()).setProvider("BC")));
		} catch (SMIMEException e1) {
			e1.printStackTrace();
			er.error("No DTS", "Certificate File", "Error un-enveloping message body SMIMEException", e1.getMessage(), "-");
		} catch (CMSException e1) {
			e1.printStackTrace();
			er.error("No DTS", "Certificate File", "Error un-enveloping message body CMSException", e1.getMessage(), "-");
		} catch (Exception e1) {
			er.error("No DTS", "Certificate File", "Probably wrong format file or wrong certificate", e1.getMessage(), "-");
		}

		return res;
	}
	
	public boolean getWrapped() {
		return this.wrapped;
	}
	
	public boolean getIsMDN() {
		return this.isMDN;
	}
	
}
