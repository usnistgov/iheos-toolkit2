package gov.nist.toolkit.errorrecording.client;

public class XDMValidationReportItem {
	private String msg;
	private String status;
	private String color;
	private boolean hasError;
	private String bold;

	public XDMValidationReportItem(String entire_msg, ValidatorErrorItem.ReportingLevel level) {

		this.msg = entire_msg;
		this.color = "black";
		this.hasError = false;
		this.bold = "normal";

		switch (level) {
		case DETAIL:
			status = "ok";
			break;

		case WARNING:
			status = "Warning";
			this.color = "blue";
			break;

		case ERROR:
			status = "error";
			this.color = "red";
			this.hasError = true;
			break;
			
		case SECTIONHEADING:
			status = "";
			this.bold = "bold";
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
	
	public String getColor() {
		return this.color;
	}
	
	public boolean hasError() {
		return this.hasError;
	}
	
	public String getBold() {
		return this.bold;
	}

}
