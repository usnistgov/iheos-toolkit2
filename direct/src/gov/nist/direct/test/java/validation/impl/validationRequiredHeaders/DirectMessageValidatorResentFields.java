package gov.nist.direct.test.java.validation.impl.validationRequiredHeaders;

import static org.junit.Assert.assertTrue;
import gov.nist.direct.directValidator.impl.DirectMimeMessageValidatorFacade;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

import org.junit.Test;

public class DirectMessageValidatorResentFields {
	// DTS 197, Resent-fields, Required
	// Result: Success
	@Test
	public void testResentFields() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		String[] resentFields = {"from", "to", "date", "resent-date", "resent-from", "resent-to", "content-type", "content-disposition"};
		validator.validateResentFields(er, resentFields, false);
		assertTrue(!er.hasErrors());
	}
	
	// Result: Fail
	@Test
	public void testResentFields2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		String[] resentFields = {"from", "to", "date", "resent-date", "resent-from", "content-type", "resent-to", "content-disposition"};
		validator.validateResentFields(er, resentFields, false);
		assertTrue(er.hasErrors());
	}
	
}
