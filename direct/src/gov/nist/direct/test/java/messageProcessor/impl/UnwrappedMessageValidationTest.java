package gov.nist.direct.test.java.messageProcessor.impl;

import static org.junit.Assert.*;
import gov.nist.direct.messageParser.MessageParser;
import gov.nist.direct.messageParser.impl.DirectMimeMessageProcessor;
import gov.nist.direct.messageParser.impl.MimeMessageParser;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.direct.utils.ValidationSummary;
import gov.nist.direct.validation.impl.ProcessEnvelope;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.mail.internet.MimeMessage;

import org.junit.Test;

import com.sun.org.apache.bcel.internal.classfile.Field;

public class UnwrappedMessageValidationTest {

	String unwrappedDirectMessage = "direct/src/gov/nist/direct/test/java/messageProcessor/impl/UnwrappedDirectMessage.txt";
	String wrappedDirectMessage = "direct/src/gov/nist/direct/test/java/messageProcessor/impl/WrappedDirectMessage.txt";
	String decrypted_unwrappedDirectMessage = "direct/src/gov/nist/direct/test/java/messageProcessor/impl/decrypted_UnwrappedDirectMessage.txt";
	String decrypted_wrappedDirectMessage = "direct/src/gov/nist/direct/test/java/messageProcessor/impl/decrypted_UnwrappedDirectMessage.txt";
	
	@Test
	/**
	 * Check the validation method
	 */
	public void testValidationEncryptedUnwrappedDirectMessage(){
		ErrorRecorder er = new TextErrorRecorderModif();
		
		File unwrapped = new File(unwrappedDirectMessage);
		byte[] unwrappedMessage = new byte[(int) unwrapped.length()];
		try {
			FileInputStream fileInputStream = new FileInputStream(unwrapped);
			fileInputStream.read(unwrappedMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		MessageParser<MimeMessage> parser = new MimeMessageParser();
		MimeMessage mm = parser.parseMessage(er, unwrappedMessage);
		
		ProcessEnvelope proEnv = new ProcessEnvelope();
		try {
			proEnv.validateMimeEntity(er, mm, new ValidationSummary(), 0);
			proEnv.validateMessageHeader(er, mm, new ValidationSummary(), 0, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(!er.hasErrors());
		
		
	}
	
	@Test
	/**
	 * Check the decrypted message validation method
	 */
	public void testValidationDecryptedUnwrappedDirectMessage() {
		ErrorRecorder er = new TextErrorRecorderModif();
		
		File unwrapped = new File(decrypted_unwrappedDirectMessage);
		byte[] unwrappedMessage = new byte[(int) unwrapped.length()];
		try {
			FileInputStream fileInputStream = new FileInputStream(unwrapped);
			fileInputStream.read(unwrappedMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		MessageParser<MimeMessage> parser = new MimeMessageParser();
		MimeMessage mm = parser.parseMessage(er, unwrappedMessage);
		
		DirectMimeMessageProcessor processor = new DirectMimeMessageProcessor();
		try {
			processor.processPart(er, mm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(!er.hasErrors());
		
		
		
	}
	
	@Test
	/**
	 * Check the validation method
	 */
	public void testValidationEncryptedWrappedDirectMessage(){
		ErrorRecorder er = new TextErrorRecorderModif();
		
		File wrapped = new File(wrappedDirectMessage);
		byte[] wrappedMessage = new byte[(int) wrapped.length()];
		try {
			FileInputStream fileInputStream = new FileInputStream(wrapped);
			fileInputStream.read(wrappedMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		MessageParser<MimeMessage> parser = new MimeMessageParser();
		MimeMessage mm = parser.parseMessage(er, wrappedMessage);
		
		ProcessEnvelope proEnv = new ProcessEnvelope();
		try {
			proEnv.validateMimeEntity(er, mm, new ValidationSummary(), 0);
			proEnv.validateMessageHeader(er, mm, new ValidationSummary(), 0, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(!er.hasErrors());
		
		
	}
	
	@Test
	/**
	 * Check the decrypted message validation method
	 */
	public void testValidationDecryptedWrappedDirectMessage() {
		ErrorRecorder er = new TextErrorRecorderModif();
		
		File wrapped = new File(decrypted_wrappedDirectMessage);
		byte[] wrappedMessage = new byte[(int) wrapped.length()];
		try {
			FileInputStream fileInputStream = new FileInputStream(wrapped);
			fileInputStream.read(wrappedMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		MessageParser<MimeMessage> parser = new MimeMessageParser();
		MimeMessage mm = parser.parseMessage(er, wrappedMessage);
		
		DirectMimeMessageProcessor processor = new DirectMimeMessageProcessor();
		try {
			processor.processPart(er, mm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(!er.hasErrors());
		
		
		
	}
	
	
}
