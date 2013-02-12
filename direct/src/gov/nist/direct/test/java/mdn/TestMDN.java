package gov.nist.direct.test.java.mdn;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import gov.nist.direct.mdn.generate.MDNGenerator;
import gov.nist.direct.messageProcessor.MessageProcessorInterface;
import gov.nist.direct.messageProcessor.direct.directImpl.MimeMessageParser;
import gov.nist.direct.messageProcessor.utils.MessageDispatchUtils;
import gov.nist.direct.utils.Utils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.mailet.base.mail.MimeMultipartReport;
import org.junit.Test;

import com.google.gwt.dev.jjs.ast.JField;
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
		
		signCert = Utils.getByteFile(signingCert);
		encCert = Utils.getByteFile(encryptionCert);
		
		MimeMessage mdn = null;
		mdn = MDNGenerator.createSignedAndEncrypted("ack", "starugh-stateline.com", "NHIN Direct Security Agent", null,
				"externUser1@starugh-stateline.com", "<9501051053.aa04167@IETF.CNR I.Reston.VA.US>", 
				Disposition.COMPILE_TIME_CONSTANT, "test@test.com", "test2@test.com", "Test MDN", encCert, signCert, "mhunter");
		
		
			// Creates a new MimeMessage message using the MimeMultipartReport contents
			Properties props = System.getProperties();
		    Session session = Session.getDefaultInstance(props, null);
			try {
				Utils.printToFile(mdn, "MDNFile.txt");
		
				assertTrue(MessageDispatchUtils.isMDN(er, mdn));
			} catch (MessagingException e) {
				e.printStackTrace();
			}	
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