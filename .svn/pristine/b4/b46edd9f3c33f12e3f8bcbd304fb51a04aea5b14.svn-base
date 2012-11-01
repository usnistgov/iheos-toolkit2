package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;

import org.apache.axiom.om.OMElement;

public abstract class FormatValidator {
	ErrorRecorder er;
	String context;
	String resource;
	
	public FormatValidator(ErrorRecorder er, String context, String resource) {
		this.er = er;
		this.context = context;
		this.resource = resource;
	}
	
	public void validate(String input) throws FormatValidatorCalledIncorrectlyException {
		throw new FormatValidatorCalledIncorrectlyException("Called incorrectly");
	}
	
	public void validate(OMElement input) throws FormatValidatorCalledIncorrectlyException {
		throw new FormatValidatorCalledIncorrectlyException("Called incorrectly");
	}
	
	protected String getResource(String specialResource) {
		if (resource == null)
			return specialResource;
		else if (specialResource == null)
			return resource;
		else
			return resource + " and " + specialResource;

	}
	
	protected void err(String input, String error, String additionalResource) {
		er.err(XdsErrorCode.Code.XDSRegistryMetadataError, context + ": " + input + " : " + error, this, getResource(additionalResource));
	}
}
