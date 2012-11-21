package gov.nist.direct.test.java.validation.impl.validationRequiredHeaders;

import static org.junit.Assert.assertTrue;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.direct.validation.impl.DirectMimeMessageValidatorFacade;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

import org.junit.Test;

public class DirectMessageValidatorResentFields {
	// DTS 197, Resent-fields, Required
	@Test
	public void testResentFields() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		String[] resentFields = {"from", "to", "date", "resent-date", "resent-from", "resent-to", "content-type", "content-disposition"};
		validator.validateResentFields(er, resentFields);
		assertTrue(!er.hasErrors());
	}
	
	@Test
	public void testResentFields2() {
		ErrorRecorder er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		String[] resentFields = {"from", "to", "date", "resent-date", "resent-from", "content-type", "resent-to", "content-disposition"};
		validator.validateResentFields(er, resentFields);
		assertTrue(er.hasErrors());
	}
	
}
