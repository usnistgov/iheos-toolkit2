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

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import gov.nist.direct.mdn.generate.MDNGenerator;
import gov.nist.direct.messageProcessor.mdn.mdnImpl.MDNMessageProcessor;
import gov.nist.direct.utils.Utils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorder;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.junit.Test;

import com.google.gwt.dev.jjs.ast.JField.Disposition;

public class TestMDN {
	
	ErrorRecorder er = new TextErrorRecorder();
	
		
	/**
	 * Checks that an MDN acknowledgment can be successfully generated and is detected as being an MDN.
	 */
	@Test
	public void testMDNGeneration(){

		String signingCert = "direct/src/gov/nist/direct/test/resources/certificates/mhunter.p12";
		byte[] signCert = null;
		String encryptionCert = "direct/src/gov/nist/direct/test/resources/certificates/mhunter.cer";
		byte[] encCert = null;
		String password = "mhunter";

		signCert = Utils.getByteFile(signingCert);
		encCert = Utils.getByteFile(encryptionCert);

		MimeMessage mdn = null;
		mdn = MDNGenerator.createSignedAndEncrypted("ack", "starugh-stateline.com", "NHIN Direct Security Agent", null,
				"externUser1@starugh-stateline.com", "<9501051053.aa04167@IETF.CNR I.Reston.VA.US>", 
				Disposition.COMPILE_TIME_CONSTANT, "test@test.com", "test2@test.com", "Test MDN", encCert, signCert, password);


		// Creates a new MimeMessage message using the MimeMultipartReport contents
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props, null);
		Utils.printToFile(mdn, "MDNFile.txt");

		MDNMessageProcessor processor = new MDNMessageProcessor();
		ErrorRecorder er = new GwtErrorRecorder();
		byte[] mdnMessage = Utils.getByteFile("MDN.txt");
		processor.processMDNMessage(er, mdnMessage, signCert, password, new ValidationContext());

		System.out.println(er);

		assertTrue(!er.hasErrors());
	}

	
	
	/**
	 * Checks that an MDN message know to be correct is successfully validated.
	 */
	@Test
	public void testMDNValidation(){
		
	}
	
	
	/**
	 * Checks that an MDN acknowledgment can be successfully generated and validated.
	 */
	@Test
	public void testMDNGenerationValidationCycle(){
		
	}
	
	
	
	
	
}