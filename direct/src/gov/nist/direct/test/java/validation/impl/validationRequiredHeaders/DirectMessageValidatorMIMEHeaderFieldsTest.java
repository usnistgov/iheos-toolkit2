package gov.nist.direct.test.java.validation.impl.validationRequiredHeaders;

import static org.junit.Assert.*;
import gov.nist.direct.directValidator.impl.DirectMimeMessageValidatorFacade;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

import org.junit.Test;

public class DirectMessageValidatorMIMEHeaderFieldsTest {

	
	// DTS 190, All Mime Header Fields, Required
	// Result: Success
	@Test
	public void testAllMimeHeaderFields() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateAllMimeHeaderFields(er, "attachment; filename=smime.p7m");
		assertTrue(!er.hasErrors());
	}
	
	// Result: Success
	@Test
	public void testAllMimeHeaderFields2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateAllMimeHeaderFields(er, "attachment; comment:\"test comment (comment)\"; filename=smime.p7m");
		assertTrue(!er.hasErrors());
	}

	// Result: Fail
	@Test
	public void testAllMimeHeaderFields3() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateAllMimeHeaderFields(er, "attachment(comment); filename=smime.p7m");
		assertTrue(!er.hasErrors());
	}

	// Result: Fail
	@Test
	public void testAllMimeHeaderFields4() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateAllMimeHeaderFields(er, "attachment; filename=smime.p7m (comment)");
		assertTrue(!er.hasErrors());
	}

}
