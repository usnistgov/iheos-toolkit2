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


package gov.nist.direct.test.java.validation.impl.validationOptionalHeaders;

import static org.junit.Assert.assertTrue;
import gov.nist.direct.directValidator.impl.DirectMimeMessageValidatorFacade;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
 

import org.junit.Test;

public class DirectMessageValidatorResentFieldTest {
	
	// DTS 110, Resent-to, Optional
	// Result: Success
	@Test
	public void testResentTo() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateResentTo(er, "test@test.com", false);
		assertTrue(!er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testResentTo2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateResentTo(er, "test.test.com", false);   // Not valid, not an e-mail address
		assertTrue(er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testResentTo3() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateResentTo(er, "", false);  // Not present
		assertTrue(!er.hasErrors());
	}
	
	// DTS 111, Resent-cc, Optional
	// Result: Success
	@Test
	public void testResentCc() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateResentCc(er, "test@test.com", false);
		assertTrue(!er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testResentCc2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateResentCc(er, "test.test.com", false);  // Not valid, not an e-mail address
		assertTrue(er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testResentCc3() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateResentCc(er, "", false);  // Not present
		assertTrue(!er.hasErrors());
	}
	
	// DTS 112, Resent-bcc, Optional
	// Result: Success
	@Test
	public void testResentBcc() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateResentBcc(er, "test@test.com", false);
		assertTrue(!er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testResentBcc2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateResentBcc(er, "test.test.com", false);  // Not valid, not an e-mail address
		assertTrue(er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testResentBcc3() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateResentBcc(er, "", false);  // Not present
		assertTrue(!er.hasErrors());
	}
	
	
		
}
