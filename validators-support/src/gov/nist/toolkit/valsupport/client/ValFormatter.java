package gov.nist.toolkit.valsupport.client;

public interface ValFormatter {

	void addCell(String msg, int col);
	void setCell(String msg, int row, int col);
	void hr();
	void clearResults();
	void setColSpan(int col, int colSpan);
	void setDetail(String detail);
	void setDTS(String dts);
	void setFound(String found);
	void setReference(String ref);
	void setStatus(String status);
	void setName(String name);
	void setExpected(String expected);
	void setRFC(String rfc);
	String red(String msg);
	String green(String msg);
	String purple(String msg);
	String blue(String msg);
	String bold(String msg);
	String h2(String msg);
	String h3(String msg);
	String rfc_link(String msg);
	String html_link(String msg, String url);
	void incRow();
	int getRow();
}
