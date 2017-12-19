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
	public boolean soaped = false;  // has this error been added to SOAP Reg error list?
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

	public boolean isError() {
		return level == ReportingLevel.ERROR || level == ReportingLevel.D_ERROR;
	}

	public boolean isErrorOrContext() {
		return isError() || completion == ReportingCompletionType.ERROR;
	}

	public String getReportable() {
		StringBuilder buf = new StringBuilder();
		if (hasDTS()) buf.append(dts).append(" : ");
		if (this.code != null && !code.equals(XdsErrorCode.Code.NoCode))
			buf.append(code.name() + " : ");
		buf.append(msg.trim());
		if (hasExpected() || hasFound() )
			buf.append('\n').append("Expected [").append(expected).append("]  ").append("Found [ ").append(found).append("]\n");

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

	public String toString() {
		StringBuffer buf = new StringBuffer();

//		String sep = "    &&&&    ";
//		String sp = "    ";
		
		switch (level) {
		case SECTIONHEADING:
			buf.append("SECTIONHEADING\n");
			add(buf);
//			buf.append(caps(getMsg())).append('[').append(completion).append(']').append("\n");
			break;

		case CHALLENGE:
			buf.append("CHALLENGE\n");
			add(buf);
//				buf.append(sp).append(getMsg()).append('[').append(completion).append(']').append("\n");
			break;

		case EXTERNALCHALLENGE:
			buf.append("EXTERNALCHALLENGE\n");
			add(buf);
//			buf.append(sp).append(getMsg()).append('[').append(completion).append(']').append("\n");
			break;

		case DETAIL:
//			buf.append("DETAIL\n");
			add(buf);
//			buf.append(sp).append(getMsg()).append('[').append(completion).append(']').append("\n");
			break;

		case ERROR:
			case D_ERROR:
				buf.append("ERROR\n");
				add(buf);
//			buf
//			.append("ERROR: ")
//			.append(getMsg())
//			.append(sep)
//			.append(resource).append('[').append(completion).append(']').append("\n");
			break;

		case WARNING:
			case D_WARNING:
				buf.append("WARNING\n");
				add(buf);
//			buf
//			.append("WARNING: ")
//			.append(getMsg())
//					.append(sep)
//			.append(resource).append('[').append(completion).append(']').append("\n");
				break;

			case D_INFO:
				buf.append("INFO\n");
				add(buf);
//			buf.append("INFO: ").append(getMsg()).append("\n");
		}

		return buf.toString();
	}

	void add(StringBuffer buf) {
		String sp = "    ";
		buf.append(sp);
		if (hasDTS()) buf.append(dts.trim()).append(sp);
		buf.append(getReportable().trim()).append('[').append(completion).append(']').append("\n");
	}

	boolean hasDTS() { return dts != null && !"".equals(dts); }
	
	String caps(String in) {
		return in.toUpperCase();
	}
}
