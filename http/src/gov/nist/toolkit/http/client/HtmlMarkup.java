package gov.nist.toolkit.http.client;

import com.google.gwt.user.client.ui.HTML;

public class HtmlMarkup {
	
	public static String bold(String msg) {
		return "<b>" + msg + "</b>";
	}

	public static HTML html(String msg) {
		HTML h = new HTML();
		h.setHTML(msg);
		return h;
	}

	public static String red(String msg) {
		return "<font color=\"#FF0000\">" + msg  + "</font>";
	}

	public static String red(String txt, boolean isRed) {
		if (isRed)
			return red(txt);
		return txt;
	}

	public static String h3(String msg) {
		return "<h3>" + msg + "</h3>";
	}

	public static String h2(String msg) {
		return "<h2>" + msg + "</h2>";
	}

	public static HTML text(String html) {
		HTML h = new HTML();
		h.setHTML(html);
		return h;
	}
	
	public static String h4(String txt) {
		return "<h4>" + txt + "</h4>";
	}

	public static String h5(String txt) {
		return "<h5>" + txt + "</h5>";
	}


}
