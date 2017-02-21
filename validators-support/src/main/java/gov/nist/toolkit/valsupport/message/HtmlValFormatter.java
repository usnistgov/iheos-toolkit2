package gov.nist.toolkit.valsupport.message;

import java.util.ArrayList;

import gov.nist.toolkit.errorrecording.gwt.client.ErrorRecorderAdapter;
import gov.nist.toolkit.errorrecording.gwt.client.GWTValidationStepResult;
import gov.nist.toolkit.errorrecording.gwt.client.GwtValidatorErrorItem;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValFormatter;


public class HtmlValFormatter implements ValFormatter {

	static int MAXROWS = 3000;
	String[][] tbl = new String[MAXROWS][6];
	int[] colspan = new int[MAXROWS];
	int row = 0;
	
	public HtmlValFormatter() {
		for(int i=0;i<this.colspan.length;i++) {
			this.colspan[i] = 0;
		}
	}
	
	public String toHtmlTemplate(MessageValidationResults results) {
		ArrayList<GwtValidatorErrorItem> er = new ArrayList<GwtValidatorErrorItem>();
		
		for(GWTValidationStepResult res : results.getResults()) {
			for(GwtValidatorErrorItem erIt : res.er) {
				er.add(erIt);
			}
		}
		ErrorRecorderAdapter erAd = new ErrorRecorderAdapter(er);
		
		return erAd.toHTML(); 
	}
	
	
	public String toHtml() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("<table>");
		
		for (int r=0; r<row; r++) {
			buf.append("<tr>");

			if(this.colspan[r] == 0) {
				for (int c=0; c<6; c++) {
					buf.append("<td>");
					buf.append(tbl[r][c]);
					buf.append("</td>");
				}
			} else {
				buf.append("<td colspan=\"" + this.colspan[r] + "\">");
				buf.append(tbl[r][0]);
				buf.append("</td>");
				buf.append("<td>");
				buf.append(tbl[r][1]);
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
		for (int i=0; i<6; i++) {
			addCell("<hr/>", i);
		}
		row++;
	}
	
	public void setRow(int row) {
		this.row = row;
	}

	public void clearResults() {
	}

	// Detail column
	public void setDetail(String detail) {
		addCell(detail, 0);
	}
	
	public void detail(String detail) {
		setDetail(detail);
	}
	
	// DTS column
	public void setDTS(String dts) {
		addCell(dts, 2);
	}
	
	public void dts(String dts) {
		setDTS(dts);
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

	// Reference column
	public void setReference(String ref) {
		addCell(ref, 2);
	}
	
	public void reference(String ref) {
		setReference(ref);
	}

	// Found column
	public void setFound(String found) {
		addCell(found, 3);
	}
	
	public void found(String found) {
		setFound(found);
	}
	
	// Name Column
	public void setName(String name) {
		addCell(name, 0);
	}
	
	public void name(String name) {
		setStatus(name);
	}

	// Expected column
	public void setExpected(String expected) {
		addCell(expected, 4);
	}
	
	public void expected(String expected) {
		setExpected(expected);
	}
	
	// RFC Column
	public void setRFC(String rfc) {
		addCell(rfc, 5);
	}
	
	public void rfc(String rfc) {
		setRFC(rfc);
	}
	
	// Status Column
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
	
	@Override
	public String rfc_link(String msg) {
		String res = "";
		if(msg.contains(";")) {
			String[] msgSplit = msg.split(";");
			for(int i=0 ; i < msgSplit.length ; i=i+2) {
				res += html_link(msgSplit[i], msgSplit[i+1]);
			}
			if(res.equals("")) {
				res = msg;
			}
		} else {
			res = msg;
		}
		return res;
	}
	
	public String html_link(String msg, String url) {
		return "<a href=\"" + url + "\" target=\"_blank\">"+ msg + "</a><br>";
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

	@Override
	public String green(String msg) {
		return "<font color=\"#66CD00\">" + msg  + "</font>";
	}

	@Override
	public String purple(String msg) {
		return "<font color=\"#551A8B\">" + msg  + "</font>";
	}

	@Override
	public void setColSpan(int col, int colSpan) {
		this.colspan[col] = 5;	
	}

}
