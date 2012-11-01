package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.ErrorRecorder;

import org.apache.axiom.om.OMElement;

public class AnyFormat extends FormatValidator {

	public AnyFormat(ErrorRecorder er, String context, String resource) {
		super(er, context, resource);
	}

	public void validate(String input) {
	}
	
	public void validate(OMElement input) {
	}

}
