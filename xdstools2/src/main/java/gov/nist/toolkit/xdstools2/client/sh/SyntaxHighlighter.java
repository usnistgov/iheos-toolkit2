package gov.nist.toolkit.xdstools2.client.sh;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * This is based off of the open source Js library by Alex G.
 *
 */
public class SyntaxHighlighter extends JavaScriptObject {

	protected SyntaxHighlighter() {
	}

	public native static BrushFactory get()/*-{
		return $wnd.scripthighligher;
	}-*/;

	public static native String highlight(String code, JavaScriptObject brush,
			boolean toolbar) /*-{
		var params = {};
		params['toolbar'] = toolbar;
		brush.init(params);
		return brush.getHtml(code);
	}-*/;
}
