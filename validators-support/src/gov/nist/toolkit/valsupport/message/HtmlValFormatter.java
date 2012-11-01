package gov.nist.toolkit.valsupport.message;

import gov.nist.toolkit.valsupport.client.ValFormatter;


public class HtmlValFormatter implements ValFormatter {

	static int MAXROWS = 3000;
	String[][] tbl = new String[MAXROWS][3];
	int row = 0;
	
	public String toHtml() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("<table>");
		
		for (int r=0; r<row; r++) {
			buf.append("<tr>");
			
			for (int c=0; c<3; c++) {
				buf.append("<td>");
				buf.append(tbl[r][c]);
				buf.append("</td>");
			}
			
			buf.append("</tr>");
		}
		
		buf.append("</table>");
		return buf.toString();
	}
	
	public void addCell(String msg, int col) {
		tbl[row][col] = msg;
	}

	public void setCell(String msg, int row, int col) {
		tbl[row][col] = msg;
	}

	public void hr() {
		for (int i=0; i<3; i++) {
			addCell("<hr/>", i);
		}
		row++;
	}

	public void clearResults() {
	}

	public void setDetail(String detail) {
		addCell(detail, 0);
	}

	public void detail(String detail) {
		setDetail(detail);
	}
	
	public void detailAsError(String detail) {
		setDetail(red(detail));
	}
	
	public void addError(String detail) {
		detailAsError(detail);
		incRow();
	}
	
	public void detailAsWarning(String detail) {
		setDetail(blue(detail));
	}
	
	public void setReference(String ref) {
		addCell(ref, 2);
	}
	
	public void reference(String ref) {
		setReference(ref);
	}

	public void setStatus(String status) {
		addCell(status, 1);
	}
	
	public void status(String status) {
		setStatus(status);
	}

	public String red(String msg) {
		return "<font color=\"#FF0000\">" + msg  + "</font>";
	}

	public String bold(String msg) {
		return "<b>" + msg + "</b>";
	}

	public String h2(String msg) {
		return "<h2>" + msg + "</h2>";
	}

	public String h3(String msg) {
		return "<h3>" + msg + "</h3>";
	}

	public void incRow() {
		row++;
		detail("");
		reference("");
		status("");
	}

	public int getRow() {
		return row;
	}

	@Override
	public String blue(String msg) {
		return "<font color=\"#0000FF\">" + msg  + "</font>";
	}

}
