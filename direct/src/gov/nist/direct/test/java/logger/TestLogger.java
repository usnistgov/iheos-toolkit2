package gov.nist.direct.test.java.logger;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.AssertFalse;

import gov.nist.direct.client.MessageLog;
import gov.nist.direct.logger.LogPathsSingleton;
import gov.nist.direct.logger.MessageLogManager;
import gov.nist.direct.logger.UserLog;
import gov.nist.direct.messageProcessor.direct.directImpl.DirectMimeMessageProcessor;
import gov.nist.direct.test.java.messageGenerator.DirectMimeMessageGeneratorTest;
import gov.nist.direct.test.java.messageProcessor.impl.UnwrappedMessageValidationTest;
import gov.nist.direct.utils.Utils;

import org.junit.Test;

public class TestLogger {


	
	@Test
	public void testWriteDirectLog(){
		LogPathsSingleton ls = LogPathsSingleton.getLogStructureSingleton();
		
		UnwrappedMessageValidationTest testClass = new UnwrappedMessageValidationTest();
		MimeMessage msg = testClass.createTestMimeMessage();
		System.out.println("TEST: created MIME message.");
		
		DirectMimeMessageProcessor mp = new DirectMimeMessageProcessor();
		mp.logDirectMessage(msg);
		System.out.println("TEST: logged MIME message.");
		
		
		Address[] addr = null;
		String logPath = "";
		try {
			addr = ((MimeMessage) msg).getFrom();
			String _username = (addr[0]).toString();
			String username = Utils.rawFromHeader(_username);
			logPath = ls.getDirectMessageLogPath(ls.getDIRECT_SEND_FOLDER(), ls.getDIRECT_MESSAGE_FOLDER(), username, msg.getMessageID());
			
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File f = new File(logPath);
		assertTrue(f.exists());
	}
	
	
	@Test
	public void testWriteMDNLog(){
	
	}
	
	@Test
	public void testReadLog() {
		UserLog ul = new UserLog();
		List<MessageLog> list = ul.readUserLogs("mhunter@5amsolutions.com");
		
		System.out.println("Writing log reader results");
		// display on console
		while (list.iterator().hasNext()){
			list.iterator().next().toString();
		}
		assertFalse(list.isEmpty());
	}

}
