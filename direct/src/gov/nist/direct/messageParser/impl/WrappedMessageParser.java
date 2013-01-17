package gov.nist.direct.messageParser.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import gov.nist.direct.messageParser.MessageParser;
import gov.nist.direct.validation.MessageValidatorFacade;
import gov.nist.direct.validation.impl.DirectMimeMessageValidatorFacade;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

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

public class WrappedMessageParser {
	
	private boolean wrapped;
	
	private final String BC = BouncyCastleProvider.PROVIDER_NAME;
	private byte[] directCertificate;
	private String password;
	
	public WrappedMessageParser() {
		wrapped = false;
	}
	
	public void messageParser(ErrorRecorder er, byte[] inputDirectMessage, byte[] _directCertificate, String _password) {
		directCertificate = _directCertificate;
		password = _password;
		
		MessageParser<MimeMessage> parser = new MimeMessageParser();
		MimeMessage mm = parser.parseMessage(er, inputDirectMessage);
		try {
			processPart(er, mm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void processPart(ErrorRecorder er, Part p) throws Exception {
		
		if (p == null)
			return;
		//er.detail("Processing Part");
		// If the Part is a Message then first validate the Envelope
		if (p instanceof Message){
			System.out.println("Message");
		}

		/*
		 * Using isMimeType to determine the content type avoids
		 * fetching the actual content data until we need it.
		 */
		if (p.isMimeType("text/plain")) {
			//System.out.println("Text/plain");

		} else if (p.isMimeType("text/xml")) {
			//System.out.println("Text/xml");

		} else if (p.isMimeType("message/rfc822")) {
			//System.out.println("Message/rfc822");
			this.wrapped = true;

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

		} else {
			System.out.println("Unrecognized part");

		}
	}
	
	public Part processSMIMEEnvelope(ErrorRecorder er, Part p, InputStream certificate, String password) {
		//
		// Open the key store
		//
		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance("PKCS12", "BC");
		} catch (KeyStoreException e1) {
			er.err("0", "Error in keystore creation", "", "", "Certificate file");
		} catch (NoSuchProviderException e1) {
			er.err("0", "Error in keystore creation NoSuchProviderException", "", "", "Certificate file");
		}

		// Message Validator
		MessageValidatorFacade msgValidator = new DirectMimeMessageValidatorFacade();

		try {
			if(password == null) {
				password="";
			} 
			ks.load(certificate, password.toCharArray());
		} catch (NoSuchAlgorithmException e1) {
			er.err("0", "Error in loading certificate NoSuchAlgorithmException", "", "", "Certificate file");
		} catch (CertificateException e1) {
			er.err("0", "Error in loading certificate CertificateException", "", "", "Certificate file");
		} catch (IOException e1) {
			er.err("0", "Error in loading certificate IOException (decryption)", "", "", "Certificate file");
		} catch (Exception e1) {
			er.err("0", "Cannot load the certificate (decryption). Probably wrong format certificate file", "", "", "Certificate file");
		}

		@SuppressWarnings("rawtypes")
		Enumeration e = null;
		try {
			e = ks.aliases();
		} catch (KeyStoreException e1) {
			er.err("0", "Error in loading certificate KeyStoreException", "", "", "Certificate file");
		}
		String      keyAlias = null;

		if (e != null) {
			while (e.hasMoreElements())
			{
				String  alias = (String)e.nextElement();

				try {
					if (ks.isKeyEntry(alias))
					{
						keyAlias = alias;
					}
				} catch (KeyStoreException e1) {
					er.err("0", "Error in loading certificate. Cannot load alias", "", "", "Certificate file");
				}
			}
		}

		//
		// find the certificate for the private key and generate a 
		// suitable recipient identifier.
		//
		X509Certificate cert = null;
		try {
			cert = (X509Certificate)ks.getCertificate(keyAlias);
		} catch (KeyStoreException e1) {
		}
		RecipientId     recId = new JceKeyTransRecipientId(cert);

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
			PrivateKey pkey = (PrivateKey)ks.getKey(keyAlias, null);
			res = SMIMEUtil.toMimeBodyPart(recipient.getContent(new JceKeyTransEnvelopedRecipient(pkey).setProvider("BC")));
		} catch (UnrecoverableKeyException e1) {
			e1.printStackTrace();
			er.err("0", "Error un-enveloping message body: " + ExceptionUtil.exception_details(e1), "", "", "Certificate file");
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
			er.err("0", "Error un-enveloping message body: " + ExceptionUtil.exception_details(e1), "", "", "Certificate file");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
			er.err("0", "Error un-enveloping message body: " + ExceptionUtil.exception_details(e1), "", "", "Certificate file");
		} catch (SMIMEException e1) {
			e1.printStackTrace();
			er.err("0", "Error un-enveloping message body: " + ExceptionUtil.exception_details(e1), "", "", "Certificate file");
		} catch (CMSException e1) {
			e1.printStackTrace();
			er.err("0", "Error un-enveloping message body: " + ExceptionUtil.exception_details(e1), "", "", "Certificate file");
		} catch (Exception e1) {
			er.err("0", "Error with the certificate: Unable to decrypt message maybe it is the wrong certificate", "", "", "Certificate file");
		}

		return res;
	}
	
	public boolean getWrapped() {
		return this.wrapped;
	}
	
}
