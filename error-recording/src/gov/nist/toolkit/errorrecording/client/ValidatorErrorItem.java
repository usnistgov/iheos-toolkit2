package gov.nist.toolkit.errorrecording.client;


import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Encodes a single error or status along with some metadata about the error/status so
 * that a nice gui interface can be built.
 * @author bill
 *
 */
public class ValidatorErrorItem implements IsSerializable {
	public enum ReportingLevel implements IsSerializable { SECTIONHEADING, CHALLENGE, EXTERNALCHALLENGE, DETAIL, ERROR, WARNING, D_SUCCESS, D_INFO, D_ERROR, D_WARNING};
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
	
	public ValidatorErrorItem() {} // For GWT
	
	public String getCodeString() {
		if (code != XdsErrorCode.Code.NoCode)
			return code.toString();
		if (codeString != null)
			return codeString;
		return code.toString();
	}
	
	public void setCode(XdsErrorCode.Code code) {
		this.code = code;
	}
	
	public void setCode(String code) {
		this.code = XdsErrorCode.Code.NoCode;
		codeString = code;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		String sep = "    &&&&    ";
		
		switch (level) {
		case SECTIONHEADING:
			buf.append("\n");
			buf.append(caps(msg));
			break;

		case CHALLENGE:
				buf.append("    ").append(msg);
			break;

		case EXTERNALCHALLENGE:
			buf.append("    ").append(msg);
			break;

		case DETAIL:
			buf.append("        ").append(msg);
			break;

		case ERROR:
			buf
			.append("ERROR: ")
			.append(msg)
			.append(sep)
			.append(resource);
			break;

		case WARNING:
			buf
			.append("WARNING: ")
			.append(msg)
			.append(sep)
			.append(resource);
		}

		buf.append("\n");
		
		return buf.toString();
	}
	
	String caps(String in) {
		return in.toUpperCase();
	}
}
