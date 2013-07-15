package gov.nist.toolkit.errorrecording.client;

public class CCDAValidationReportItem {

	private String msg;
	private String dts;
	private String resource;
	private String status;

	public CCDAValidationReportItem(String entire_msg, String resource, ValidatorErrorItem.ReportingLevel level) {
		this.resource = "";
		if(entire_msg.contains("|")) {
			String[] split_msg = entire_msg.split("\\|", 4);
			this.dts = split_msg[0];
			this.msg = split_msg[1];
			if(split_msg.length > 2) {
				this.msg += " | " + split_msg[2];
			}
			if(split_msg.length == 4) {
				this.resource = split_msg[3];
			}
		} else {
			this.dts = "";
			this.msg = entire_msg;
			this.resource = resource;
		}
		switch (level) {
		case DETAIL:
			status = "Success";
			break;

		case WARNING:
			status = "Warning";
			break;

		case ERROR:
			status = "Error";
			break;

		default:
			status = "Error";
			break;
		}
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getDts() {
		return dts;
	}

	public void setDts(String dts) {
		this.dts = dts;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String ressource) {
		this.resource = ressource;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
