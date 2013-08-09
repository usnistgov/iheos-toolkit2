package gov.nist.toolkit.errorrecording.client;


import org.apache.commons.lang.StringEscapeUtils;

public class ValidationReportItem {
	
	private String name;
	private String status;
	private String dts;
	private String found;
	private String expected;
	private String rfc_name;
	private String color;
	private int type;
	private String rfc_link;
	
	//public enum ReportingLevel { SECTIONHEADING, SUCCESS, ERROR, WARNING,  INFO, CONTENT};
	
	public ValidationReportItem(ValidatorErrorItem.ReportingLevel type, String name, String dts, String found, String expected, String rfc) {
		this.name = StringEscapeUtils.escapeHtml(name);
		switch(type) {
		
		case SECTIONHEADING:
			this.type = 0;
			this.status = "";
			this.color = "color: black;font-weight:bold;";
			break;
		
		case D_SUCCESS:
			this.type = 1;
			this.status = "Success";
			this.color = "color: green";
			break;

		case D_ERROR:
			this.type = 1;
			this.status = "Error";
			this.color = "color: red";
			break;
			
		case D_WARNING:
			this.type = 1;
			this.status = "Warning";
			this.color = "color: blue";
			break;
			
		case D_INFO:
			this.type = 1;
			this.status = "Info";
			this.color = "color: purple";
			break;
			
		case DETAIL:
			this.type = 2;
			this.status = "Content";
			this.color = "color:black";
			break;
			
		default:
			this.type = 1;
			this.status = "";
			this.color = "color: black;";
			break;

		}
		this.dts = StringEscapeUtils.escapeHtml(dts);
		//this.found = StringEscapeUtils.escapeHtml(found);
		this.found = found;
		this.expected = StringEscapeUtils.escapeHtml(expected);
		rfc = StringEscapeUtils.escapeHtml(rfc);
		if(rfc.contains(";")) {
			String[] rfcSplit = rfc.split(";");
			this.rfc_name = rfcSplit[0];
			this.rfc_link = rfcSplit[1];
		} else {
			this.rfc_name = rfc;
			this.rfc_link = "";
		}

	}
	
	public ValidationReportItem(String name, String content) {
		this.name = StringEscapeUtils.escapeHtml(name);
		//this.found = content;
		this.found = StringEscapeUtils.escapeHtml(content);
		//this.found = this.found.replace(" ", "&nbsp;");
		//this.found = this.found.replace("\n", "<br />\n");
		this.type = 2;
		
		if(this.found.contains("attachment=") && this.found.contains("filename=")) {
			this.found = this.found.replace("attachment=", "");
			this.found = this.found.replace("filename=", "");
			this.name = this.found.split(";")[0];
			this.found = this.found.split(";")[1];
		}
		this.status = "Content";
		this.color = "color:black";
		this.dts = "";
		this.expected = "";
		this.rfc_name = "";
		this.rfc_link = "";
	}
	
	public ValidationReportItem(String name) {
		//this.name = StringEscapeUtils.escapeHtml(name);
		this.name = name;
		this.name = this.name.replace(" ", "&nbsp;");
		this.name = this.name.replace("\n", "<br />\n");
		this.type = 3;
		this.status = "Detail";
		this.color = "color:black";
		this.dts = "";
		this.found = "";
		this.expected = "";
		this.rfc_name = "";
		this.rfc_link = "";
		
		if(this.name.contains(";anchor=")) {
			String anchor = "";
			anchor = this.name.split(";anchor=")[1];
			this.name = this.name.split(";anchor=")[0];
			this.name = "<span id=\"" + anchor + "\">" + this.name + "</div>";
		}
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDts() {
		return dts;
	}

	public void setDts(String dts) {
		this.dts = dts;
	}

	public String getFound() {
		return found;
	}

	public void setFound(String found) {
		this.found = found;
	}

	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}

	public String getRfc_name() {
		return rfc_name;
	}

	public void setRfc_name(String rfc_name) {
		this.rfc_name = rfc_name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getRfc_link() {
		return rfc_link;
	}

	public void setRfc_link(String rfc_link) {
		this.rfc_link = rfc_link;
	}

}
