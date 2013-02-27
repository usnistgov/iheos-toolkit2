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

import static org.junit.Assert.fail;
import gov.nist.direct.messageParser.DirectMessageProcessor;
import gov.nist.direct.messageParser.impl.DirectMimeMessageProcessor;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.direct.utils.Utils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import org.junit.Test;

public class DirectMimeMessageProcessorTest {

	@Test
	public void testProcessAndValidateDirectMessage() {
		ErrorRecorder er = new TextErrorRecorderModif();

		String messageUnderTestPath = "target/test-classes/messages/signed-encrypted-xdm.eml";
		byte[] messageUnderTest = null;
		
		String certificatePath = "target/test-classes/certificates/mhunter.p12";
		byte[] certificate = null;
		
		String certificatePassword = "mhunter";
		
		// Uses either a normal string parser or an http parser.
		messageUnderTest = Utils.getMessage(messageUnderTestPath);
		certificate = Utils.getMessage(certificatePath);
		
		DirectMessageProcessor messageValidator = new DirectMimeMessageProcessor();
		messageValidator.processAndValidateDirectMessage(er, messageUnderTest, certificate, certificatePassword, new ValidationContext());
		
		er.detail("\n#############################");
		System.out.println(er);
		er.detail("#############################");
		
	}

	@Test
	public void testProcessPart() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessEnvelope() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessText() {
		fail("Not yet implemented");
	}

}
