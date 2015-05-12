package gov.nist.toolkit.valregmetadata.field;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valregmetadata.object.Classification;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.XdsInternalException;


import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;


//this gets invoked from both Validator.java and directly from Repository.  Should optimize the implementation so that codes.xml
//gets cached in memory.
public class CodeValidation2 extends CodeValidationBase {
	ValidationContext vc;

	public CodeValidation2(Metadata m, ValidationContext vc)  {
		super();
		this.m = m;
		this.vc = vc;

		try {
			loadCodes();
		} catch (XdsInternalException e) {
			startUpError = e;
		}
	}

	// this is used for easy access to mime lookup
	public CodeValidation2() throws XdsInternalException {
		super(1);
	}




}


