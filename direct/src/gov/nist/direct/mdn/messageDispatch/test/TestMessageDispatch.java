package gov.nist.direct.mdn.messageDispatch.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nist.direct.mdn.messageDispatch.MessageDispatchUtils;
import gov.nist.direct.messageParser.MessageParser;
import gov.nist.direct.messageParser.impl.MimeMessageParser;
import gov.nist.direct.utils.Utils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

public class TestMessageDispatch {
	
	ErrorRecorder er = new TextErrorRecorder();
	
		
	@Test
	public void testIsMDN(){
		String mdnMessageURL = "C://Workspace/TTT/toolkit/direct/src/gov/nist/direct/test/resources/messages/mdn/MDNMessage.txt";
		byte[] mdnMsg = Utils.getMessage(mdnMessageURL);
		MessageParser<MimeMessage> parser = new MimeMessageParser();
		MimeMessage mdnMime = parser.parseMessage(er, mdnMsg);
		
		try {
			assertTrue(MessageDispatchUtils.isMDN(mdnMime));
		} catch (MessagingException e) {
			e.printStackTrace();
		}
}
	
	
	@Test
	public void testDirectMessageIsNotMDN(){
		String directMessageURL = "C://Workspace/TTT/toolkit/direct/src/gov/nist/direct/test/resources/messages/signed.eml";
		byte[] directMsg = Utils.getMessage(directMessageURL);
		MessageParser<MimeMessage> parser = new MimeMessageParser();
		MimeMessage directMime = parser.parseMessage(er, directMsg);
		
		try {
			assertFalse(MessageDispatchUtils.isMDN(directMime));
		} catch (MessagingException e) {
			e.printStackTrace();
		}
}
	
	@Test
	public void testBadMDNMessageIsNotMDN(){
		String badMDNMessageURL = "C://Workspace/TTT/toolkit/direct/src/gov/nist/direct/test/resources/messages/mdn/MDNMessage_bad.txt";
		byte[] badMdnMsg = Utils.getMessage(badMDNMessageURL);
		MessageParser<MimeMessage> parser = new MimeMessageParser();
		MimeMessage badMdnMime = parser.parseMessage(er, badMdnMsg);
		
		try {
			assertFalse(MessageDispatchUtils.isMDN(badMdnMime));
		} catch (MessagingException e) {
			e.printStackTrace();
		}
}
	
	
	
}
