package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.ErrorRecorder;

public class XcnFormat extends FormatValidator {

	public XcnFormat(ErrorRecorder er, String context, String resource) {
		super(er, context, resource);
	}

	// parts
	// 1 - ID
	// 2 - Last Name
	// 3 - First Name
	// 4 - Second and Further Given Names
	// 5 - Suffix
	// 6 - Prefix
	// 7 - 
	// 8 - 
	// 9 - Assigning Authority
	
	String[] parts;
	String xresource = "ITI TF-3: Table 4.1-3 (XCN)";
	public void validate(String input) {
		parts = input.split("\\^");
		
		if ((id().equals("") && ( lastName().equals("")  || firstName().equals(""))))
			err(input, "Either name or an identifier shall be present", xresource);
		
		if ((!id().equals("") && !aa().equals(""))) {
			CxFormat cx = new CxFormat(er, context + " id and Assigning Authority components", xresource);
			cx.validate(id() + "^^^" + aa());
		}
	}
	
	String id()              { String x = get(1); if (x == null) x = ""; return x; }
	String lastName()        { String x = get(2); if (x == null) x = ""; return x; }
	String firstName()       { String x = get(3); if (x == null) x = ""; return x; }
	String otherGivenNames() { String x = get(4); if (x == null) x = ""; return x; }
	String suffix()          { String x = get(5); if (x == null) x = ""; return x; }
	String prefix()          { String x = get(6); if (x == null) x = ""; return x; }
	String aa()              { String x = get(9); if (x == null) x = ""; return x; }

	
	// i is the index (1 is first)
	String get(int i) {
		i--;
		if (i < parts.length) return parts[i];
		return null;
	}
}
