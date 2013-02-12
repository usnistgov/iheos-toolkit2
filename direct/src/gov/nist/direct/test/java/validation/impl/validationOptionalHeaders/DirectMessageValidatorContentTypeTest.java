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

public class DirectMessageValidatorContentTypeTest {
	
	
	// DTS 201, Content-Type Name, Optional
	// Result: Success
	@Test
	public void testContentTypeName() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTypeNameOptional(er, "name=smime.p7m");
		assertTrue(!er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testContentTypeName2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTypeNameOptional(er, "");   // Not present
		assertTrue(er.hasErrors());
	}
		
		
	// DTS 202, Content-Type S/MIME-Type, Optional
	// Result: Success
	@Test
	public void testContentTypeSMIMEType() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTypeSMIMETypeOptional(er, "enveloped-data");
		assertTrue(!er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testContentTypeSMIMEType2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTypeSMIMETypeOptional(er, "enveloped");  // Not valid
		assertTrue(er.hasErrors());
	}
	
	// DTS 203, Content Disposition, Optional
	// Result: Success
	@Test
	public void testContentDisposition() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentDispositionOptional(er, "smime.p7m");
		assertTrue(!er.hasErrors());
	}

	// Result: Fail
	@Test
	public void testContentDisposition2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentDispositionOptional(er, "smime");  // Not valid
		assertTrue(!er.hasErrors());
	}


}
