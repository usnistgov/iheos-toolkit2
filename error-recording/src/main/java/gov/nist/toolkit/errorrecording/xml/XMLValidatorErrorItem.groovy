package gov.nist.toolkit.errorrecording.xml;

import com.google.gwt.user.client.rpc.IsSerializable
import gov.nist.toolkit.errorrecording.common.XdsErrorCode
import gov.nist.toolkit.errorrecording.xml.assertions.structures.ReportingLevel

/**
 * Encodes a single error or status along with some metadata about the error/status so
 * that a nice gui interface can be built.
 * @author bill
 *
 */
public class XMLValidatorErrorItem implements IsSerializable {
	//public enum ReportingLevel implements IsSerializable { SECTIONHEADING, CHALLENGE, EXTERNALCHALLENGE, DETAIL, ERROR, WARNING, D_SUCCESS, D_INFO, D_ERROR, D_WARNING};
	public enum ReportingCompletionType implements IsSerializable { ERROR, WARNING, OK };

	public ReportingLevel level;
	public boolean summaryPart = false;
	public String msg = "";
	public String resource = "";
	public String location = "";
	public String dts = "";
	public String name = "";
	public String found = "";
	public String expected = "";
	public String rfc = "";
	public String status = "";
	XdsErrorCode.Code code = XdsErrorCode.Code.NoCode;
	String codeString = null;
	public ReportingCompletionType completion = ReportingCompletionType.OK;

	public XMLValidatorErrorItem() {} // For GWT

	/**
	 * Converts an XDS error code into a String
	 * @return
     */
	public String getCodeString() {
		if (code != XdsErrorCode.Code.NoCode)
			return code.toString();
		if (codeString != null)
			return codeString;
		return code.toString();
	}

	public boolean isError() {
		return level == ReportingLevel.ERROR || level == ReportingLevel.D_ERROR;
	}

	/**
	 * Returns true when the current validation level either is an error, or has an error in the current section (?)
	 * @return
     */
	public boolean isErrorOrContext() {
		return isError() || completion == ReportingCompletionType.ERROR;
	}

	/**
	 * Generates an error message formulated as "Expected ..., found ...".
	 * @return
     */
	public String getReportable() {
		StringBuilder buf = new StringBuilder();
		if (hasDTS()) buf.append(dts).append(" : ");
		if (this.code != null && !code.equals(XdsErrorCode.Code.NoCode))
			buf.append(code.name() + " : ");
		buf.append(msg.trim());
		if (hasExpected() || hasFound() )
			buf.append('\n').append("Expected [").append(expected).append("]  ").append("Found [ ").append("]\n");

		return buf.toString();
	}

	boolean hasExpected() { return !expected.equals(""); }
	boolean hasFound() { return !found.equals(""); }

	public void setCode(XdsErrorCode.Code code) {
		this.code = code;
	}
	
	public void setCode(String code) {
		this.code = XdsErrorCode.Code.NoCode;
		codeString = code;
	}

	String getMsg() { return msg.trim(); }

	/**
	 * Generates a String output from a paragraph of text validation output. Adds level headers to the sections.
	 * @return
     */
	public String toString() {
		//TODO see if there is a simpler method to initialize buffer here
		def buf = ''
		//def bufRecord //= new XmlSlurper().parseText(buf)


		switch (level) {
		case ReportingLevel.SECTIONHEADING:
			def element = '''<SectionHeading></SectionHeading>'''
			//def newRecord = new XmlSlurper().parseText(element)
			buf = buf + element
			// -- old --
			//buf.append("SECTIONHEADING\n");
			//add(buf);
			break;

		case ReportingLevel.CHALLENGE:
			buf.appendNode("CHALLENGE\n");
			add(buf);
			break;

		case ReportingLevel.EXTERNALCHALLENGE:
			buf.append("EXTERNALCHALLENGE\n");
			add(buf);
			break;

		case ReportingLevel.DETAIL:
			buf.append("DETAIL\n");
			add(buf);
			break;

		case ReportingLevel.ERROR:
			case ReportingLevel.D_ERROR:
				buf.append("ERROR\n");
				add(buf);
			break;

		case ReportingLevel.WARNING:
			case ReportingLevel.D_WARNING:
				buf.append("WARNING\n");
				add(buf);
				break;

			case ReportingLevel.D_INFO:
				buf.append("INFO\n");
				add(buf);

		}

		// Returns the parsed form, that still needs to be converted later to readable XML.
		return buf.toString();
	}

	/**
	 * Add to the output? Not obvious what it does exactly
	 * @param buf
     */
	void add(StringBuffer buf) {
		String sp = "    ";
		buf.append(sp);
		if (hasDTS()) buf.append(dts.trim()).append(sp);
		buf.append(getReportable().trim()).append('[').append(completion).append(']').append("\n");
	}

	boolean hasDTS() { return dts != null && !"".equals(dts); }
	
	/*String caps(String in) {
		return in.toUpperCase();
	}*/
}
