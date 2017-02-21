package gov.nist.toolkit.errorrecording.gwt.client;

public class XDMValidationReportItem {
	private String msg;
	private String status;

	public XDMValidationReportItem(String entire_msg, GwtValidatorErrorItem.ReportingLevel level) {

		this.msg = entire_msg;

		switch (level) {
		case DETAIL:
			status = "ok";
			break;

		case WARNING:
			status = "Warning";
			break;

		case ERROR:
			status = "error";
			break;
			
		case SECTIONHEADING:
			status = "";
			break;

		default:
			status = "ok";
			break;
		}
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
