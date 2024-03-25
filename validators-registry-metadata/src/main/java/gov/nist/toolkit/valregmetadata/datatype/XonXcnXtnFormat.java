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
		int barCount = count(input, '|');

/*
		if (barCount != 2)
 */
		if  (! (parts.length > 0 && parts.length <= 3))
			err(input, "Format is XON|XCN|XTN and at least one of these values is required", xresource);

		String xon = null;
		String xcn = null;
		String xtn = null;

		if (parts.length > 0)
			xon = parts[0];
		if (parts.length > 1)
			xcn = parts[1];
		if (parts.length == 3)
			xtn = parts[2];

		if (xon == null) xon = "";
		if (xcn == null) xcn = "";
		if (xtn == null) xtn = "";

		if (xon.equals("") && xcn.equals("") && xtn.equals("")) {
			err(input, "Format is XON|XCN|XTN and at least one of these values is required to be non-empty.", xresource);
		}

		if (!xon.equals(""))
			new XonFormat(er, context + ": intendedRecipient(Organization Name)", resource).validate(xon);
		
		if (!xcn.equals(""))
			new XcnFormat(er, context + ": intendedRecipient(Extended Person Name)", resource).validate(xcn);

		if (!xtn.equals(""))
			new XtnFormat(er, context + ": intendedRecipient(Telecommunication)", resource).validate(xtn);
	}

	private int count(String input, char c) {
		int count = 0;
		for (int i=0; i<input.length(); i++ ) if (input.charAt(i) == c) count++;
		return count;
	}

}
