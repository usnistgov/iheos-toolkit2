package gov.nist.toolkit.valregmetadata.top;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;


//this gets invoked from both Validator.java and directly from Repository.  Should optimize the implementation so that codes.xml
//gets cached in memory.
public class CodeValidation extends CodeValidationBase {
//	RegistryErrorListGenerator rel;       all errors to go through the ErrorRecorder interface
	ErrorRecorder er;
	boolean is_submit;
	boolean xds_b;
	boolean runable = true;

//	public CodeValidation(Metadata m, boolean is_submit, boolean xds_b, RegistryErrorListGenerator rel) throws XdsInternalException {
//		super(1);
//
//		this.m = m;
////		this.rel = rel;
//		this.is_submit = is_submit;
//		this.xds_b = xds_b;
//
//		er = rel;    // all errors to go through the ErrorRecorder interface
//	}
	
	public CodeValidation(Metadata m, ValidationContext vc, ErrorRecorder er)  {
		super();
		this.m = m;
		this.er = er;
		is_submit = true;
		xds_b = true;
		try {
			setValidationContext(vc);
		} catch  (XdsInternalException e) {
			er.err(XdsErrorCode.Code.XDSRegistryError, e);
			runable = false;
		}
	}

	// this is used for easy access to mime lookup
	public CodeValidation() throws XdsInternalException {
		super();
	}
	
	public void run() {
		if (runable) run(er);
	}

}


