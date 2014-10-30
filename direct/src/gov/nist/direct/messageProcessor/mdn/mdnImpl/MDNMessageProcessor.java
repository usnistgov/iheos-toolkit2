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
import gov.nist.direct.mdn.validate.MDNValidator;
import gov.nist.direct.mdn.validate.MDNValidatorImpl;
import gov.nist.direct.mdn.validate.ProcessMDN;
import gov.nist.direct.messageProcessor.cert.CertificateLoader;
import gov.nist.direct.messageProcessor.direct.directImpl.DirectMimeMessageProcessor;
import gov.nist.direct.messageProcessor.direct.directImpl.MimeMessageParser;
import gov.nist.direct.messageProcessor.direct.directImpl.WrappedMessageProcessor;
import gov.nist.direct.utils.ParseUtils;
import gov.nist.direct.utils.Utils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorder;
import org.apache.log4j.Logger;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.util.Store;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * Parses an MDN message for a message ID. It looks up the ID in the list of timestamps and calculates the time offset between sending and reception.
 * If the offset is less than a predetermined limit AND the MDN is valid (send it to the MDNvalidator), then the test is successful.
 * 
 * @author dazais
 *
 */
public class MDNMessageProcessor {
	static Logger logger = Logger.getLogger(DirectMimeMessageProcessor.class);
	
	private static String NO_ORIGINAL_MSG_ID = "No original msg-id";
	private static String NO_MDN_MESSAGE_ID = "No MDN message-id";
	private static String STATUS_VALID = "Valid";
	private static String STATUS_NOT_VALID = "Not valid";
	private static String STATUS_NOT_SPECIFIED = "Status not specified";

	
	private String decryptedMdn = ""; // contains the full decrypted message
	private String dispositionField = "";
	private String msgID = "";
	private Date mdnDate = null;
	private final String BC = BouncyCastleProvider.PROVIDER_NAME;
	private byte[] directCertificate;
	private String password;;
	ValidationContext vc = new ValidationContext();
	WrappedMessageProcessor wrappedParser = new WrappedMessageProcessor();
	ErrorRecorder mainEr;
	boolean encrypted;
	boolean signed;
	LogPathsSingleton ls;

	private String MDN_STATUS;

	public MDNMessageProcessor(){
		// New ErrorRecorder for the MDN validation summary
		mainEr = new GwtErrorRecorder();
		ls = LogPathsSingleton.getLogStructureSingleton();
		MDN_STATUS = "NOT SPECIFIED";


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
			msgID = mm.getMessageID();
			mdnDate = mm.getSentDate();
		} catch (Exception e) {
			er.error("No DTS", "MDN Processing", "Error Processing MDN", "", "-");
			e.printStackTrace();
		}
		
		// Validate MDN Signature and Encryption
		MDNValidator mdnv = new MDNValidatorImpl();
		mdnv.validateMDNSignatureAndEncryption(er, signed, encrypted);

		
		// Check validation status
		MDN_STATUS = STATUS_NOT_VALID;
		if (!er.hasErrors())  MDN_STATUS = STATUS_VALID;
		System.out.println("mdn validation status: " + MDN_STATUS);
		
		// Logs MDN message - still ENCRYPTED
		logMDNMessage(mm); // logMDNMessage(mm, disposition);

        // Parse the message to see if it is wrapped
        wrappedParser.messageParser(er, inputDirectMessage, _directCertificate, _password);

        logger.debug("ValidationContext is " + vc.toString());

        // Parses message - what does it do? needed?
       // MimeMessage m = MimeMessageParser.parseMessage(mainEr, inputDirectMessage);
       

	}





	public void logMDNMessage(MimeMessage m) {

        // Get MDN sender name (username)
       // String _username = ParseUtils.searchHeaderSimple((Part)m, "from");
        // String username = Utils.rawFromHeader(_username);

		
        // Get MDN message ID 
        String mdnMessageId = "";
		try {
			if (m.getMessageID() == null || m.getMessageID().equals("")){
				String _mdnMessageId = m.getMessageID();
				mdnMessageId = Utils.rawFromHeader(_mdnMessageId);
			} else {
				mdnMessageId = NO_MDN_MESSAGE_ID;
			}
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
			
        // Get  reception time - Logging system date instead of SUT sender date contained in headers
        Date date = new Date();

        
        // Get Original Direct Message-ID from MDN
        String origMessageID;
        String _inResponseToMessageID = ParseUtils.searchHeaderSimple((Part)m, "original-message-id");
        		
        if (_inResponseToMessageID == null || _inResponseToMessageID.equals("")) {
        	        origMessageID = NO_ORIGINAL_MSG_ID;
        		} else {
        			origMessageID = Utils.rawFromHeader(_inResponseToMessageID);
        		}
        
		
		// Get original Direct message validation status as described in MDN
		String origDirectMsgValidationStatus = "";
		try {
			if (m.getDisposition() != null && m.getDisposition() != ""){
				String disp = m.getDisposition();
				if (disp.contains("processed")){ // we hope this is code for "a valid Direct message"
					origDirectMsgValidationStatus = STATUS_VALID; 
				} else {
					origDirectMsgValidationStatus = STATUS_NOT_VALID; 
				}
			} else { // Disposition is null - shouldn't happen anyway
				origDirectMsgValidationStatus = STATUS_NOT_SPECIFIED;
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        //MessageLogManager.logMDN(m, MDN_STATUS, origDirectMsgValidationStatus, "DIRECT_SEND", "MDN", origMessageID, date, mdnMessageId);

		
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
			concat(p.toString());
			

		} else if (p.isMimeType("text/xml")) {
			//concat(p.toString());

			//System.out.println("Text/xml");

		} else if (p.isMimeType("message/rfc822")) {
			this.processPart(er, (Part)p.getContent());

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
			ProcessMDN mdnv = new ProcessMDN(er, p);
			mdnv.validate(er);
			dispositionField = mdnv.getDispositionField();

		} else if (p.isMimeType("application/octet-stream")) {
			//System.out.println("CCDA Content");

		} else if (p.isMimeType("multipart/signed")) {
			//System.out.println("multipart");

			SMIMESigned s = new SMIMESigned((MimeMultipart)p.getContent());
			//concat(s.toString());

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
			er.error("No DTS", "Certificate File", "Error in keystore creation", e1.getMessage(), "-");
		} catch (NoSuchProviderException e1) {
			er.error("No DTS", "Certificate File", "Error in keystore creation", e1.getMessage(), "-");
		} catch (NoSuchAlgorithmException e1) {
			er.error("No DTS", "Certificate File", "Error in keystore creation", e1.getMessage(), "-");
		} catch (CertificateException e1) {
			er.error("No DTS", "Certificate File", "Error in keystore creation", e1.getMessage(), "-");
		} catch (IOException e1) {
			er.error("No DTS", "Certificate File", "Error in keystore creation", e1.getMessage(), "-");
		} catch (Exception e1) {
			er.error("No DTS", "Certificate File", "Error in keystore creation", e1.getMessage(), "-");
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
			er.error("No DTS", "Certificate File", "Error un-enveloping message body", e1.getMessage(), "-");
		} catch (CMSException e1) {
			e1.printStackTrace();
			er.err("No DTS", "Certificate File", "Error un-enveloping message body", e1.getMessage(), "-");
		} catch (Exception e1) {
			er.err("No DTS", "Certificate File", "Error un-enveloping message body", e1.getMessage(), "-");
		}

		this.encrypted = true;

		// Create the decrypted MDN by logging successive body parts
		//	concat(res.toString());
			//System.out.println("Could not log MimeMultipart body parts once the message was decrypted.");
		
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
			try {
				new JcaX509CertificateConverter().setProvider("BC").getCertificate((X509CertificateHolder)certIt.next());
			} catch (Exception e) {
				er.error("No DTS", "Signature", "Cannot extract the signing certificate", "", "-");
				break;
			}

			this.signed = true; 

		}
	}

	
	/**
	 * concatenes strings from message Parts once decrypted
	 * @param str
	 * @return
	 */
	String concat(String str){
		if (str != null){
			return decryptedMdn.concat(str);
		} else {
			System.out.println("Some of the message parts were empty.");
			return "";
		}
	}

	

}
