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

package gov.nist.direct.test.java.messageProcessor.impl;

import static org.junit.Assert.assertTrue;
import gov.nist.direct.directValidator.impl.ProcessEnvelope;
import gov.nist.direct.messageProcessor.direct.directImpl.DirectMimeMessageProcessor;
import gov.nist.direct.messageProcessor.direct.directImpl.MimeMessageParser;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.direct.utils.ValidationSummary;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.mail.internet.MimeMessage;

import org.junit.Test;

public class UnwrappedMessageValidationTest {
	ErrorRecorder er;

	String unwrappedDirectMessage = "direct/src/gov/nist/direct/test/java/messageProcessor/impl/UnwrappedDirectMessage.txt";
	String wrappedDirectMessage = "direct/src/gov/nist/direct/test/java/messageProcessor/impl/WrappedDirectMessage.txt";
	String decrypted_unwrappedDirectMessage = "direct/src/gov/nist/direct/test/java/messageProcessor/impl/decrypted_UnwrappedDirectMessage.txt";
	String decrypted_wrappedDirectMessage = "direct/src/gov/nist/direct/test/java/messageProcessor/impl/decrypted_UnwrappedDirectMessage.txt";
	String unwrappedMDNMessage = "direct/src/gov/nist/direct/test/resources/mdnMessages/RIexamples/MDNMessage.txt";
	
	public UnwrappedMessageValidationTest(){
		er = new TextErrorRecorderModif();
	}
	
	
	@Test
	/**
	 * Check the validation method
	 */
	public void testValidationEncryptedUnwrappedDirectMessage(){
		MimeMessage mm = createTestDirectMessage();
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
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(unwrapped);
			fileInputStream.read(unwrappedMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileInputStream.close();
			} catch (IOException e) {}
		}
		
		MimeMessage mm = MimeMessageParser.parseMessage(er, unwrappedMessage);
		
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
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(wrapped);
			fileInputStream.read(wrappedMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileInputStream.close();
			} catch (IOException e) {}
		}
		
		MimeMessage mm = MimeMessageParser.parseMessage(er, wrappedMessage);
		
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
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(wrapped);
			fileInputStream.read(wrappedMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileInputStream.close();
			} catch (IOException e) {}
		}
		
		MimeMessage mm = MimeMessageParser.parseMessage(er, wrappedMessage);
		
		DirectMimeMessageProcessor processor = new DirectMimeMessageProcessor();
		try {
			processor.processPart(er, mm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(!er.hasErrors());
		
		
		
	}
	
	
	public MimeMessage createTestDirectMessage(){
		return createMessage(unwrappedDirectMessage);
	}
		
	
	public MimeMessage createTestMDN(){
		return createMessage(unwrappedMDNMessage);
	}
		
		
		private MimeMessage createMessage(String filename){
		File unwrapped = new File(filename);
		byte[] unwrappedMessage = new byte[(int) unwrapped.length()];
		FileInputStream fileInputStream = null;
		try {
			 fileInputStream = new FileInputStream(unwrapped);
			fileInputStream.read(unwrappedMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
			fileInputStream.close();
			} catch (IOException e) {}
		}
		
		MimeMessage mm = MimeMessageParser.parseMessage(er, unwrappedMessage);
		return mm;
	}
	

	
}
