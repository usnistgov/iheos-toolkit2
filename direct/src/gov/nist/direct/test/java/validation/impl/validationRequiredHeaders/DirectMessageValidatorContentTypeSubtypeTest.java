package gov.nist.direct.test.java.validation.impl.validationRequiredHeaders;

import static org.junit.Assert.*;
import gov.nist.direct.directValidator.impl.DirectMimeMessageValidatorFacade;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

import org.junit.Test;

public class DirectMessageValidatorContentTypeSubtypeTest {

	// DTS 191, Content-Type Subtype, Required
	// Result: Success
	@Test
	public void testContentType() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentType(er, "plain/text");
		assertTrue(!er.hasErrors());
	}

	// Result: Fail
	@Test
	public void testContentType2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateContentType(er, "text");
		assertTrue(er.hasErrors());
	}

}
