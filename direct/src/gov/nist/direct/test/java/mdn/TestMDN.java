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


package gov.nist.direct.test.java.mdn;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Properties;

import gov.nist.direct.config.DirectConfigManager;
import gov.nist.direct.directGenerator.impl.UnwrappedMessageGenerator;
import gov.nist.direct.mdn.MDNStandard;
import gov.nist.direct.mdn.MDNUtils;
import gov.nist.direct.mdn.generate.MDNGenerator;
import gov.nist.direct.mdn.notifications.NotificationType;
import gov.nist.direct.messageProcessor.direct.directImpl.DirectMimeMessageProcessor;
import gov.nist.direct.messageProcessor.mdn.mdnImpl.MDNMessageProcessor;
import gov.nist.direct.test.java.Utils4Test;
import gov.nist.direct.utils.ParseUtils;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.direct.utils.Utils;
import gov.nist.direct.utils.ValidationUtils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;
import gov.nist.toolkit.testengine.smtp.SMTPAddress;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorder;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.apache.mailet.base.mail.MimeMultipartReport;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import com.google.gwt.dev.jjs.ast.JField.Disposition;

public class TestMDN {
	
	ErrorRecorder er = new TextErrorRecorderModif();
	static Logger logger = Logger.getLogger(TestMDN.class);
	String NO_HEADER = "No header";
	
		
	
@Test
/**
 * Adapted from Direct RI because it is the only code example publicly available
 * @throws Exception
 */
    public void testCreate_withGeneralAttributes() throws Exception
    {
            final String disp = "automatic-action/MDN-sent-automatically;processed";
           // final MdnGateway gateway = new MdnGateway("junitGateway");
            
            // Creating MDN. Construction:
            // String humanText, String reporting_UA_name, String reporting_UA_product,
			// String original_recipient, String final_recipient, String original_message_id,
			// String disposition
            MimeMultipartReport report = MDNGenerator.create("test", "junitUA", "junitProduct", "sender@send.com", 
                            "final@final.com", "12345", disp);
           
        	
            assertNotNull(report);
            
            InternetHeaders headers = MDNUtils.getNotificationFieldsAsHeaders(report);
            logger.debug(headers.getHeader("original-message-id"));
            logger.info(headers.getHeader("original-message-id"));
            logger.info(headers.getHeader(MDNStandard.Headers.ReportingAgent, ","));
            
            
            assertTrue(headers.getHeader(MDNStandard.Headers.ReportingAgent, ",").startsWith("junitUA"));
            assertTrue(headers.getHeader(MDNStandard.Headers.ReportingAgent, ",").endsWith("junitProduct"));
            assertEquals("rfc822; sender@send.com", headers.getHeader(MDNStandard.Headers.OriginalRecipient, ","));
            assertEquals("rfc822; final@final.com", headers.getHeader(MDNStandard.Headers.FinalRecipient, ","));
            assertTrue(headers.getHeader(MDNStandard.Headers.Gateway, ",").endsWith("junitGateway"));
            assertTrue(headers.getHeader(MDNStandard.Headers.Disposition, ",").endsWith(NotificationType.Processed.toString()));    
            
            BodyPart part0 = report.getBodyPart(0);
            Object obj = part0.getContent();
            assertEquals("test", obj);
    }
	
	
	/**
	 * Checks that an MDN acknowledgment can be successfully generated and is detected as being an MDN.
	 */
	@Test
	public void testMDNGenerationInResponseToDirect(){
		String directMessagePath = Utils4Test.getDIRECT_MESSAGE_PATH_1();
		String signingCert = Utils4Test.getSIGNING_CERT_PATH_1();
		String encryptionCert = Utils4Test.getENCRYPTION_CERT_PATH_1();
		String password = Utils4Test.getPASSWORD_1();

		// create original Direct message
		MimeMessage directMsg = Utils.getMimeMessage(directMessagePath);
		try {
			System.out.println(directMsg.getContentType());
		} catch (MessagingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		// get certificates
		byte[] signCert = null;
		byte[] encCert = null;
		signCert = Utils.getByteFile(signingCert);
		encCert = Utils.getByteFile(encryptionCert);

		// decrypt original Direct message
		DirectMimeMessageProcessor proc = new DirectMimeMessageProcessor();
		byte[] inputDirectMessage = Utils.getByteFile(directMessagePath);
		ValidationContext vc = new ValidationContext();
		proc.processAndValidateDirectMessage(er, inputDirectMessage, signCert, password, vc);
		MimeMessage decryptedMsg = proc.getDecryptedMessage();
		try {
			decryptedMsg.writeTo(new FileOutputStream("decryptedmdn.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String humantext = "test";

		//File attachmentContentFile
		
		
		// get reporting_UA_name
		String reporting_UA_name = "";
		
		// get reporting_UA_product
		String reporting_UA_product = "";
		
		// get original_recipient
		//directMsg.getRecipients(arg0)
		String original_recipient = "";
		
		// get final_recipient
		String final_recipient = "";
		
		// get original_message_id
		String origMsgId = "";
		try {
			if (directMsg.getMessageID() != null && directMsg.getMessageID() != ""){
				origMsgId = Utils.trimEmailAddress(directMsg.getMessageID());
			} else {
				origMsgId = NO_HEADER;
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// get disposition - we write it in
		String disposition = "automatic-action/MDN-sent-automatically;processed";
		
		// get from - this is us
		String from = "ttt@nist.gov";
		
		
		// get to - this is the Direct msg "From"
		String to ="";
		try {
			to = ParseUtils.getFromAddress(directMsg);
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		// get subject  - doesn't need to throw an error message
		String subject = "";
		try {
			subject = directMsg.getSubject();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create a new MDN
		MDNGenerator gen = new MDNGenerator();
		MimeMessage mdn;
		mdn = gen.createEncryptedUnwrappedMDN(humantext, reporting_UA_name, reporting_UA_product, original_recipient, final_recipient, origMsgId, disposition, from, to, subject, encCert, signCert, password);
		if (mdn == null) System.out.println("mdn null");

		// Print MDN to file
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			mdn.writeTo(new FileOutputStream("MDNFile.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		// Validate MDN
		MDNMessageProcessor processor = new MDNMessageProcessor();
		ErrorRecorder er = new GwtErrorRecorder();
		byte[] mdnMessage = Utils.getByteFile("MDNFile.txt");
		processor.processMDNMessage(er, mdnMessage, signCert, password, new ValidationContext());

		logger.debug(er);

		assertTrue(!er.hasErrors());
	}

	
//	
//	/**
//	 * Checks that an MDN message know to be correct is successfully validated.
//	 */
//	@Test
//	public void testMDNValidation(){
//		
//	}
//	
//	
//	/**
//	 * Checks that an MDN acknowledgment can be successfully generated and validated.
//	 */
//	@Test
//	public void testMDNGenerationValidationCycle(){
//		
//	}
//	
	
	

	
	
}