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


package gov.nist.direct.test.java.validation.impl.validationRequiredHeaders;

import static org.junit.Assert.assertTrue;
import gov.nist.direct.directValidator.impl.DirectMimeMessageValidatorFacade;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import org.junit.Test;

public class DirectMessageValidatorContentTypeTest {
	// DTS 133a, Content-Type, Required
	// Result: Success
	@Test
	public void testContentTypeName() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentType(er, "application/pkcs7-mime");
		assertTrue(!er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testContentTypeName2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentType(er, "application");   // Not a valid name
		assertTrue(er.hasErrors());
	}
		
		
	// DTS 133b, Content-Type, Required
	// Result: Success
	@Test
	public void testContentTypeName3() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentType2(er, "multipart/signed");
		assertTrue(!er.hasErrors());
	}
		
	// Result: Fail
	@Test
	public void testContentTypeName4() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentType2(er, "multipart");  // Not valid
		assertTrue(er.hasErrors());
	}
	
	// DTS 160, Content Type Miclag, Required
	// Result: Success
	@Test
	public void testContentTypeMicalg() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTypeMicalg(er, "sha-1");
		assertTrue(!er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testContentTypeMicalg2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTypeMicalg(er, "sha-2");  // Not valid
		assertTrue(er.hasErrors());
	}
	
	// DTS 205, Content Type Protocol, Required
	// Result: Success
	@Test
	public void testContentTypeProtocol() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTypeProtocol(er, "\"application/pkcs7-signature\"");
		assertTrue(!er.hasErrors());
	}
					
	// Result: Fail
	@Test
	public void testContentTypeProtocol2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTypeProtocol(er, "application");  // Not valid
		assertTrue(er.hasErrors());
	}
	
	// DTS 206, Content-Transfer-Encoding, Required
	// Result: Success
	@Test
	public void testContentTransferEncoding() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTransferEncoding(er, "base64");
		assertTrue(!er.hasErrors());
	}
					
	// Result: Fail
	@Test
	public void testContentTransferEncoding2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTransferEncoding(er, "base");  // Not valid
		assertTrue(er.hasErrors());
	}
}
