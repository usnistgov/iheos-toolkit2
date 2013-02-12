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

public class DirectMessageValidatorContentTest {
	
	// DTS 161-194, Content-Disposition filename, Optional
	// Result: Success
	@Test
	public void testContentDispositionFilename() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentDispositionFilename(er, "smime.p7s");
		assertTrue(!er.hasErrors());
	}

	// Result: Success
	@Test
	public void testContentDispositionFilename2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentDispositionFilename(er, "smime.p7z");
		assertTrue(!er.hasErrors());
	}

	// Result: Success
	@Test
	public void testContentDispositionFilename3() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentDispositionFilename(er, "smime.p7c");
		assertTrue(!er.hasErrors());
	}

	// Result: Fail
	@Test
	public void testContentDispositionFilename4() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentDispositionFilename(er, "smimesmime.p7z"); // More than 8 characters
		assertTrue(!er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testContentDispositionFilename5() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentDispositionFilename(er, "test.p7z"); // Not smime
		assertTrue(!er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testContentDispositionFilename6() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentDispositionFilename(er, "smime"); // No extension
		assertTrue(!er.hasErrors());
	}
	
	// DTS 134-143, Content-Id, Optional
	@Test
	public void testContentId() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentId(er, "<872d3f8f-b025-4847-b470-33f8427734b1@example.jaxws.sun.com>");
		assertTrue(!er.hasErrors());
	}

	// Result: Fail
	@Test
	public void testContentId2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentId(er, "<872d3f8f-b025-4847-b470-33f8427734b1.example.jaxws.sun.com>");  // No @
		assertTrue(er.hasErrors());
	}
	
	// DTS 135-142-144, Content-Description, Optional
	// Result: Success
	@Test
	public void testContentDescription() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentDescription(er, "");   // No check needed
		assertTrue(!er.hasErrors());
	}
	
	// DTS 136-148-157, Content-Transfer-Encoding, Optional
	// Result: Success
	@Test
	public void testContentTransfertEncoding() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTransferEncodingOptional(er, "7bit", "multipart/signed");
		assertTrue(!er.hasErrors());
	}
	
	// Result: Success
	@Test
	public void testContentTransfertEncoding2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTransferEncodingOptional(er, "8bit", "multipart/mixed");
		assertTrue(!er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testContentTransfertEncoding3() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTransferEncodingOptional(er, "binary", "text/plain");
		assertTrue(er.hasErrors());
	}
	
	// Result: Success
	@Test
	public void testContentTransfertEncoding4() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTransferEncodingOptional(er, "quoted-printable", "text/plain");
		assertTrue(!er.hasErrors());
	}
	
	// Result: Success
	@Test
	public void testContentTransfertEncoding5() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTransferEncodingOptional(er, "base-64", "applicaion/zip");
		assertTrue(!er.hasErrors());
	}
	
	// Result: Success
	@Test
	public void testContentTransfertEncoding6() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTransferEncodingOptional(er, "7-bit transfert encoding", "text/xml");
		assertTrue(!er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testContentTransfertEncoding7() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentTransferEncodingOptional(er, "7bit", "application/octet-stream");
		assertTrue(er.hasErrors());
	}
	
	// DTS 138-149, Content-*, Optional
	// Result: Success
	@Test
	public void testContentAll() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentAll(er, "content-new");
		assertTrue(!er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testContentAll2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentAll(er, "message-content-new");   // Don't begin by content-
		assertTrue(er.hasErrors());
	}
	
	
}
