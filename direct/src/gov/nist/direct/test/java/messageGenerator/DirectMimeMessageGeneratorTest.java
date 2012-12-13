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
Authors: Frederic de Vaulx
		Diane Azais
		Julien Perugini
 */


package gov.nist.direct.test.java.messageGenerator;

import static org.junit.Assert.*;

import java.util.Arrays;

import gov.nist.direct.bouncycastleCodeExamples.CreateEncryptedMail;
import gov.nist.direct.bouncycastleCodeExamples.CreateLargeEncryptedMail;
import gov.nist.direct.bouncycastleCodeExamples.ReadLargeEncryptedMail;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.direct.utils.Utils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

import org.junit.Test;

public class DirectMimeMessageGeneratorTest {
	ErrorRecorder er = new TextErrorRecorderModif();
	

	String certFilename = "C://workspace_toolkit_test/toolkit/direct/src/gov/nist/direct/test/resources/certificates/dazais(NIST).p12";
	String certFilenameCER = "C://workspace_toolkit_test/toolkit/direct/src/gov/nist/direct/test/resources/certificates/diane_startSSL.cer";
	//"target/test-classes/dazais(NIST).p12";
	//String certFilename = "target/test-classes/certificates/dazais(NIST).p12";
	String password = "";
	//String inputFile = "gov/nist/direct/test/messageToSign.txt";
	String inputFile = "C://workspace_toolkit_test/toolkit/direct/src/gov/nist/direct/test/resources/ccda/CCD.sample.xml";
			//"C://workspace_toolkit_test/toolkit/direct/src/gov/nist/direct/test/resources/messages/IHE_XDM.zip";
			//"target/test-classes/messages/IHE_XDM.zip";
	//String outputFile = "target/test-classes/output/outputMessage.txt";
	String attachment = "C:\\workspace_toolkit_test/toolkit/direct/src/gov/nist/direct/test/resources/ccda/CCD.sample.xml";
			//"target/test-classes/ccda/CCD.sample.xml";
	String outputFile = "C://workspace_toolkit_test/toolkit/direct/src/gov/nist/direct/test/output/outputMimeMsg.txt";
	String outputFile2 = "C://workspace_toolkit_test/toolkit/direct/src/gov/nist/direct/test/output/outputMimeMsg2.txt";
			//"/gov/nist/direct/test/output/mimeMessageWithAttach.txt";
	String outputDecryptedFile = "C://workspace_toolkit_test/toolkit/direct/src/gov/nist/direct/test/output/outputDecryptedMimeMsg.txt";
	
	

	@Test
	/**
	 * Creates a signed encrypted multipart (S/MIME) message using classes derived from the Bouncycastle library
	 */
	public void testCreateDirectMessage(){
		
				CreateLargeEncryptedMail createmail = new CreateLargeEncryptedMail( certFilename,  password,  inputFile,  outputFile);
				
				try {
					createmail.createLargeEncryptedMail();
				} catch (Exception e) {
					er.err("", "Problem when generating a Direct Message", "", "", "");
					e.printStackTrace();
				}
				assertFalse(er.hasErrors());
	}
	

	@Test
	/**
	 * Creates a signed encrypted multipart (S/MIME) message using classes derived from the Bouncycastle library
	 */
	public void testCannotCreateDirectMessageFromCER(){
		
				CreateEncryptedMail createmail = new CreateEncryptedMail( certFilenameCER,  "",  outputFile2);
				
				try {
					createmail.createEncryptedMail();
				} catch (Exception e) {
					er.err("", "Problem when generating a Direct Message", "", "", "");
					e.printStackTrace();
				}
				assertTrue(er.hasErrors());
	}
	

	@Test
	/**
	 * Reads a signed encrypted message using classes derived from the Bouncycastle library
	 */
	public void testReadDirectMessage() {
		String MIMEinput = outputFile;
		
		ReadLargeEncryptedMail readmail = new ReadLargeEncryptedMail(MIMEinput,  certFilename,  password,  outputDecryptedFile);
		
		try {
			readmail.readLargeEncryptedMail();
		} catch (Exception e) {
			er.err("", "Problem when reading a Direct Message", "", "", "");
			e.printStackTrace();
		}
		assertFalse(er.hasErrors());
		
}
	
	
	@Test
	/**
	 * Tests that the resulting message, once decrypted, is equivalent to the one that was initially encrypted
	 */
	public void testGenerateReadCycle() {
		byte[] input = Utils.getMessage(inputFile);
		byte[] output = Utils.getMessage(outputDecryptedFile);
		assertTrue(Arrays.equals(input, output));
		
}
	

	
	
	
	}
