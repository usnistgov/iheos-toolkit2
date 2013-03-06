package gov.nist.direct.test.java.logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nist.direct.client.MessageLog;
import gov.nist.direct.logger.LogPathsSingleton;
import gov.nist.direct.logger.UserLog;
import gov.nist.direct.mdn.validate.MDNValidator;
import gov.nist.direct.mdn.validate.MDNValidatorImpl;
import gov.nist.direct.messageProcessor.direct.directImpl.DirectMimeMessageProcessor;
import gov.nist.direct.messageProcessor.mdn.mdnImpl.MDNMessageProcessor;
import gov.nist.direct.test.java.messageProcessor.impl.UnwrappedMessageValidationTest;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.direct.utils.Utils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

import java.io.File;
import java.util.List;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

/**
 * @author dazais
 *
 */
public class TestLogger {
	MimeMessage msg;

	
	@Test
	public void testWriteDirectLog(){
		
		LogPathsSingleton ls = LogPathsSingleton.getLogStructureSingleton();
		
		UnwrappedMessageValidationTest testClass = new UnwrappedMessageValidationTest();
		 msg = testClass.createTestDirectMessage();
		System.out.println("TEST: created DIRECT message.");
		
		DirectMimeMessageProcessor mp = new DirectMimeMessageProcessor();
		mp.logDirectMessage(msg);
		System.out.println("TEST: logged DIRECT message.");
		
		
		Address[] addr = null;
		String logPath = "";
		try {
			String username = getUsername(msg);
			String _msgId = msg.getMessageID();
			String msgId = Utils.rawFromHeader(_msgId);
			logPath = ls.getDirectMessageLogPath(ls.getDIRECT_SEND_FOLDER(), ls.getDIRECT_MESSAGE_FOLDER(), username, msgId);
			System.out.println("logPath" +logPath);
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File f = new File(logPath);
		assertTrue(f.exists());
	}
	
	
	@Test
	/**
	 * tests unwrapped mdn unencrypted
	 */
	public void testWriteMDNLog(){
	LogPathsSingleton ls = LogPathsSingleton.getLogStructureSingleton();
	ErrorRecorder er = new TextErrorRecorderModif();
	String MDN_STATUS;
	
//	String signingCert = "direct/src/gov/nist/direct/test/resources/certificates/mhunter.p12";
//	byte[] signCert = null;
//	String encryptionCert = "direct/src/gov/nist/direct/test/resources/certificates/mhunter.cer";
//	byte[] encCert = null;
//	String password = "mhunter";
//
//	signCert = Utils.getByteFile(signingCert);
//	encCert = Utils.getByteFile(encryptionCert);
//		
		UnwrappedMessageValidationTest testClass = new UnwrappedMessageValidationTest();
		 MimeMessage mdn = testClass.createTestMDN();
		System.out.println("TEST: created MDN message.");
		
		boolean signed = false;
		boolean encrypted = false;
		
		MDNValidator mdnv = new MDNValidatorImpl();
		mdnv.validateMDNSignatureAndEncryption(er, signed, encrypted);

		// Check validation status
		MDN_STATUS = "NON VALID";
		if (!er.hasErrors())  MDN_STATUS = "VALID";
		
		MDNMessageProcessor mp = new MDNMessageProcessor();
		mp.logMDNMessage(mdn);
		System.out.println("TEST: logged MDN message.");
		
		
//		Address[] addr = null;
//		String logPath = "";
//		try {
//			String username = getOriginalSenderName(msg);
//			String msgId = 
//			logPath = ls.getDirectMessageLogPath(ls.getDIRECT_SEND_FOLDER(), ls.getDIRECT_MESSAGE_FOLDER(), username, msgId);
//			System.out.println("logPath" +logPath);
//			
//		} catch (MessagingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		File f = new File(logPath);
//		assertTrue(f.exists());
//	}
//	}
	}
	
	@Test
	public void testReadDirectLog() {
//	List<MessageLog> readLog = new UserLog().readUserLogs(username);
//		
//		// test display
//		System.out.println("Testing display");
//		System.out.println(readLog.toString());
		
		UnwrappedMessageValidationTest testClass = new UnwrappedMessageValidationTest();
		 msg = testClass.createTestDirectMessage();
		System.out.println("TEST: created DIRECT message.");
		String username = getUsername(msg);
		
		System.out.println("TEST: reading DIRECT logs.");
		UserLog ul = new UserLog();
		List<MessageLog> list = ul.readUserLogs(username);
		
		System.out.println("TEST: Writing log reader results");
		// display on console
		for (int i=0; i<list.size();i++){
		//	list.get(i).toString(); - doesnt work
			System.out.println(list.get(i).toString());
		}
		assertFalse(list.isEmpty());
	}
	
	
	@Test
	public void testReadMdnLog() {
		
		UnwrappedMessageValidationTest testClass = new UnwrappedMessageValidationTest();
		 msg = testClass.createTestMDN();
		System.out.println("TEST: created MDN message.");
		String username = getUsername(msg);
		
		System.out.println("TEST: reading MDN logs.");
		UserLog ul = new UserLog();
		List<MessageLog> list = ul.readUserLogs(username);
		
		System.out.println("TEST: Writing log reader results");
		// display on console
		for (int i=0; i<list.size();i++){
		//	list.get(i).toString(); - doesnt work
			System.out.println(list.get(i).toString());
		}
		assertFalse(list.isEmpty());
	}
	
	
	//------ Service functions -------
	
	
	private String getUsername(MimeMessage msg){
		Address[] addr = null;
		try {
			addr = ((MimeMessage) msg).getFrom();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String _username = (addr[0]).toString();
		 return Utils.rawFromHeader(_username);
		
	}
	
	private String getOriginalSenderName(MimeMessage msg){
		Address[] addr = null;
		try {
			addr = ((MimeMessage) msg).getReplyTo();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String _username = (addr[0]).toString();
		 return Utils.rawFromHeader(_username);
		
	}

}
