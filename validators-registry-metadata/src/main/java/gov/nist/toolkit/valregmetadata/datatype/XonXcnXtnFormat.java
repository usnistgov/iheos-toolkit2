package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.ErrorRecorder;

/**
 * 
 * @author bill
 *
 */
public class XonXcnXtnFormat extends FormatValidator {

	public XonXcnXtnFormat(ErrorRecorder er, String context, String resource) {
		super(er, context, resource);
	}

	String xresource = "ITI TF-3: Table 4.1-6 (intendedRecipient) and " +
					"XDR and XDM for Direct Messaging Specification";

	public void validate(String input) {
		String[] parts = input.split("\\|");
		
		if (parts.length > 3)
			err(input, "Format is XON|XCN|XTN  where XTN is required", xresource);
		
		if (parts.length != 3 || parts[2].equals("")) 
			err(input, "Either Organization Name (XON format) or Extended Person Name (XCN) shall be present", xresource);

		if (parts.length > 0 && !parts[0].equals(""))
			new XonFormat(er, context + ": intendedRecipient(Organization Name)", resource).validate(parts[0]);
		
		if (parts.length > 1 && !parts[1].equals(""))
			new XcnFormat(er, context + ": intendedRecipient(Extended Person Name)", resource).validate(parts[1]);

		if (parts.length > 2 && !parts[2].equals(""))
			new XtnFormat(er, context + ": intendedRecipient(Telecommunication)", resource).validate(parts[2]);
	}

}
