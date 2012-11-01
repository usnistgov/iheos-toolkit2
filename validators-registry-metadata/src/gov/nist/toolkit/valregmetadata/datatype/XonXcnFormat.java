package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.ErrorRecorder;

public class XonXcnFormat extends FormatValidator {

	public XonXcnFormat(ErrorRecorder er, String context, String resource) {
		super(er, context, resource);
	}

	String xresource = "ITI TF-3: Table 4.1-6 (intendedRecipient)";

	public void validate(String input) {
		String[] parts = input.split("\\|");
		
		if (parts.length > 2)
			err(input, "Format is XON|XCN  where either XON or XCN must be present", xresource);
		
		if (parts.length == 2 && parts[0].equals("") && parts[1].equals("")) 
			err(input, "Either Organization Name (XON format) or Extended Person Name (XCN) shall be present", xresource);

		if (!parts[0].equals(""))
			new XonFormat(er, context + ": intendedRecipient(Organization Name)", resource).validate(parts[0]);
		
		if (parts.length > 1 && !parts[1].equals(""))
			new XcnFormat(er, context + ": intendedRecipient(Extended Person Name)", resource).validate(parts[1]);
	}
}
