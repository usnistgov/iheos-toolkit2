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


package gov.nist.direct.messageProcessor.mdn.mdnImpl;

import gov.nist.direct.logger.LogPathsSingleton;
import gov.nist.direct.logger.MessageLogManager;
import gov.nist.direct.mdn.MDNValidator;
import gov.nist.direct.mdn.impl.MDNValidatorImpl;
import gov.nist.direct.mdn.validate.ProcessMDN;
import gov.nist.direct.messageProcessor.cert.CertificateLoader;
import gov.nist.direct.messageProcessor.direct.directImpl.DirectMimeMessageProcessor;
import gov.nist.direct.messageProcessor.direct.directImpl.MimeMessageParser;
import gov.nist.direct.messageProcessor.direct.directImpl.WrappedMessageProcessor;
import gov.nist.direct.utils.ParseUtils;
import gov.nist.direct.utils.Utils;
import gov.nist.direct.utils.ValidationSummary;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorder;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.util.Store;

/**
 * Parses an MDN message for a message ID. It looks up the ID in the list of timestamps and calculates the time offset between sending and reception.
 * If the offset is less than a predetermined limit AND the MDN is valid (send it to the MDNvalidator), then the test is successful.
 * 
 * @author dazais
 *
 */
public class MDNMessageProcessor {
	static Logger logger = Logger.getLogger(DirectMimeMessageProcessor.class);

	private final String BC = BouncyCastleProvider.PROVIDER_NAME;
	private byte[] directCertificate;
	private String password;
	ValidationContext vc = new ValidationContext();
	private ValidationSummary validationSummary = new ValidationSummary();
	WrappedMessageProcessor wrappedParser = new WrappedMessageProcessor();
	private int partNumber;
	ErrorRecorder mainEr;
	boolean encrypted;
	boolean signed;
	LogPathsSingleton ls;

	private String MDN_STATUS;

	public MDNMessageProcessor(){

		// New ErrorRecorder for the MDN validation summary
		mainEr = new GwtErrorRecorder();
		ls = LogPathsSingleton.getLogStructureSingleton();


	}

	// message is parsed multiple times in following calls. TODO
	public void processMDNMessage(ErrorRecorder er, byte[] inputDirectMessage, byte[] _directCertificate, String _password, ValidationContext vc) {
		directCertificate = _directCertificate;
		password = _password;
		this.vc = vc;

		this.encrypted = false;
		this.signed = false;


		// --------- Validate MDN and encryption ---------
		MimeMessage mm = MimeMessageParser.parseMessage(mainEr, inputDirectMessage);

		try {
			this.processPart(er, mm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MDNValidator mdnv = new MDNValidatorImpl();
		mdnv.validateMDNSignatureAndEncryption(er, signed, encrypted);

		// Check validation status
		MDN_STATUS = "NON VALID";
		if (!er.hasErrors())  MDN_STATUS = "VALID";

		// Check MDN properties (Date received, Sender, compare to original Direct message)
		checkMdnMessageProperties(er, inputDirectMessage, _directCertificate, _password, vc);
		System.out.println("checkMdnMessageProperties");


		// need to delete regularly outdated message logs from the singleton.



	}




	/**
	 * Checks MDN message properties and coherence compared to the initial Direct Message
	 * (Date received, Sender, compare to original Direct message)
	 */
	public void checkMdnMessageProperties(ErrorRecorder er, byte[] inputDirectMessage, byte[] _directCertificate, String _password, ValidationContext vc){


		// Set the part number to 1
		partNumber = 1;

		// Parse the message to see if it is wrapped
		wrappedParser.messageParser(er, inputDirectMessage, _directCertificate, _password);

		logger.debug("ValidationContext is " + vc.toString());

		MimeMessage m = MimeMessageParser.parseMessage(mainEr, inputDirectMessage);


		// Get MDN sender name (username)
		String _username = ParseUtils.searchHeaderSimple((Part)m, "from");

		// Get MDN message ID 
		String _messageID = ParseUtils.searchHeaderSimple((Part)m, "message-id");

		// Get  reception time - Logging system date instead of SUT sender date contained in headers
		Date date = new Date();
		// String date = ParseUtils.searchHeaderSimple((Part)m, "date");

		// Write MDN info to existing Direct log
		String messageID = Utils.rawFromHeader(_messageID);
		String username = Utils.rawFromHeader(_username);
		MessageLogManager.logMDN(m, MDN_STATUS, username, "DIRECT_SEND", "MDN", messageID, date.toString());
		//Address[] addr = ((MimeMessage) p).getFrom();
		//username = (addr[0]).toString();
		//MessageLog.logMDN(m, MDN_STATUS, username, "DIRECT_SEND", "MDN", messageID, date.toString());


		// Compares reception time for the MDN to send time for the original Direct message.
		//		try {
		//			receiveDate = ValidationUtils.parseDate(date);
		//
		//			SendHistorySingleton sendHistory = SendHistorySingleton.getSendHistory();
		//			Date sendDate = sendHistory.getMessageSendTime(messageID);
		//
		//			int timeOffset = TimerUtils.getTimeDifference(receiveDate, sendDate);
		//			if (timeOffset <= TimerUtils.getACCEPTED_DELAY_FOR_MDN_RECEPTION()){
		//			} else {
		//				// message that an mdn was received but delay was too long
		//				//er.err(null, "MDN processing", "The MDN was received after the authorized delay had expired. The delay is "+ TimerUtils.getACCEPTED_DELAY_FOR_MDN_RECEPTION(),  timeOffset);
		//			}
		//
		//		} catch (ParseException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		System.out.println("Done.");

	}




	public void processPart(ErrorRecorder er, Part p) throws Exception {

		if (p == null)
			return;

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

		}  else if (p.isMimeType("message/disposition-notification")) {
			// Validate MDN
			ProcessMDN mdnv = new ProcessMDN();
			mdnv.validate(er, p);

		} else if (p.isMimeType("application/octet-stream")) {
			//System.out.println("CCDA Content");

		} else if (p.isMimeType("multipart/signed")) {

			SMIMESigned s = new SMIMESigned((MimeMultipart)p.getContent());

			//
			// verify signature
			//
			verifySignature(er, s);
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
			//System.out.println("Unrecognized part");

		}
	}

	public Part processSMIMEEnvelope(ErrorRecorder er, Part p, InputStream certificate, String password) {

		CertificateLoader certLoader = null;
		RecipientId     recId = null;		

		try {
			certLoader = new CertificateLoader(certificate, password);
			recId = new JceKeyTransRecipientId(certLoader.getX509Certificate());
		} catch (KeyStoreException e1) {
			er.err("0", "Error in keystore creation", "", "", "Certificate file");
		} catch (NoSuchProviderException e1) {
			er.err("0", "Error in keystore creation NoSuchProviderException", "", "", "Certificate file");
		} catch (NoSuchAlgorithmException e1) {
			er.err("0", "Error in loading certificate NoSuchAlgorithmException", "", "", "Certificate file");
		} catch (CertificateException e1) {
			er.err("0", "Error in loading certificate CertificateException", "", "", "Certificate file");
		} catch (IOException e1) {
			er.err("0", "Error in loading certificate IOException (decryption)", "", "", "Certificate file");
		} catch (Exception e1) {
			er.err("0", "Cannot load the certificate (decryption). Probably wrong format certificate file", "", "", "Certificate file");
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
			er.err("0", "Error un-enveloping message body: " + ExceptionUtil.exception_details(e1), "", "", "Certificate file");
		} catch (CMSException e1) {
			e1.printStackTrace();
			er.err("0", "Error un-enveloping message body: " + ExceptionUtil.exception_details(e1), "", "", "Certificate file");
		} catch (Exception e1) {
			er.err("0", "Error with the certificate: Unable to decrypt message maybe it is the wrong certificate", "", "", "Certificate file");
		}

		this.encrypted = true;

		return res;
	}

	/**
	 * verify the signature (assuming the cert is contained in the message)
	 */
	private void verifySignature(ErrorRecorder er, SMIMESigned s) throws Exception{
		//
		// certificates and crls passed in the signature
		//
		Store certs = s.getCertificates();

		//
		// SignerInfo blocks which contain the signatures
		//
		SignerInformationStore  signers = s.getSignerInfos();

		Collection c = signers.getSigners();
		Iterator it = c.iterator();

		//
		// check each signer
		//
		while (it.hasNext()) {
			SignerInformation   signer = (SignerInformation)it.next();
			Collection certCollection = certs.getMatches(signer.getSID());

			Iterator certIt = certCollection.iterator();
			X509Certificate cert = null;
			try {
				cert = new JcaX509CertificateConverter().setProvider(BC).getCertificate((X509CertificateHolder)certIt.next());
			} catch (Exception e) {
				break;
			}

			this.signed = true; 

		}
	}



}
