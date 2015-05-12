package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valregmetadata.field.ValidatorCommon;

public class XtnFormat extends FormatValidator {

	public XtnFormat(ErrorRecorder er, String context, String resource) {
		super(er, context, resource);
	}

	String[] parts;
	String xresource = "ITI TF-3: Table 4.1-3 (XTN)";
	
	public void validate(String input) {
		parts = input.split("\\^");
		if (!"Internet".equals(xtn_3()))
			err(input, "XTN.3 Type of telecommunications address must be 'Internet'", xresource);
		String directAddr = xtn_4();
		if (directAddr == null || directAddr.equals("")) {
			err(input, "XTN.4 Telecommunications address must be present", xresource);
			return;
		}
		if (directAddr.indexOf('@') == -1)
			err(input, "XTN.4 Telecommunications address be email address - no @ found", xresource);
	}
	
	boolean isOid(String x) { return ValidatorCommon.is_oid(x, true); }
	
	boolean valued(String x) { return !x.equals(""); }
	
	String xtn_3()   { return get(2); }
	String xtn_4() { return get(3); }
	
	String get(int i) {
		if (parts.length <= i)
			return "";
		return parts[i];
	}
	
	String get(int i, int j) {
		String x = get(i);
		if (x.equals(""))
			return "";
		String[] xparts = x.split("&");
		if (xparts.length <= j)
			return "";
		return xparts[j];
	}

}
