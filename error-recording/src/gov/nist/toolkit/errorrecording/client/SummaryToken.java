package gov.nist.toolkit.errorrecording.client;

import org.apache.commons.lang.StringEscapeUtils;

public class SummaryToken {

	private String name;
	private int status;
	private int offset;
	
	public SummaryToken(String name, int status) {
		//this.name = StringEscapeUtils.escapeHtml(name);
		this.name = name;
		this.status = status;
		this.offset = 1;
		int numberOfHyphens = 0;
		if(name.startsWith("-")) {
			int i = 0;
			while(name.charAt(i) == '-') {
				numberOfHyphens++;
				i++;
				this.name = this.name.substring(1);
			}
		}
		offset = (numberOfHyphens/5)+1;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getAbsoluteOff(int offset) {
		return Math.abs(offset);
	}
}
