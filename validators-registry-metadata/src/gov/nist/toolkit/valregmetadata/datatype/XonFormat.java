package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valregmetadata.field.ValidatorCommon;

public class XonFormat extends FormatValidator {

	public XonFormat(ErrorRecorder er, String context, String resource) {
		super(er, context, resource);
	}

	String[] parts;
	String xresource = "ITI TF-3: Table 4.1-3 (XON)";
	
	public void validate(String input) {
		parts = input.split("\\^");
		if (x1().equals(""))
			err(input, "XON.1 Organization Name required", xresource);
		String x10 = x10();
		if (valued(x10) && !isOid(x10)) {
			if (x6_2().equals(""))
				err(input, "XON 6.2 Assigning Authority Universal Id is required if XON.10 is valued and not an OID", xresource);
		if (valued(x10) && !isOid(x10))
			if (!x6_3().equals("ISO"))
				err(input, "XON 6.3 Assigning Authority Universal Id Type is required since XON.10 is valued and not an OID - it must have value ISO - found [" + x6_3() + "] instead", xresource);
		}
	}
	
	boolean isOid(String x) { return ValidatorCommon.is_oid(x, true); }
	
	boolean valued(String x) { return !x.equals(""); }
	
	String x1()   { return get(0); }
	String x6_2() { return get(6,2); }
	String x6_3() { return get(6,3); }
	String x10()  { return get(10); }
	
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
