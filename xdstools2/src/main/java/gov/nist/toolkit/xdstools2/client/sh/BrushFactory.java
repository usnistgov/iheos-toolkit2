package gov.nist.toolkit.xdstools2.client.sh;

import com.google.gwt.core.client.JavaScriptObject;

public class BrushFactory extends JavaScriptObject {

	protected BrushFactory() {
	}

	public static native BrushFactory get()/*-{
		return $wnd.brushfactory;
	}-*/;

	public static native JavaScriptObject newXmlBrush() /*-{
		return new $wnd.SyntaxHighlighter.brushes.Xml();
	}-*/;
	
	public static native JavaScriptObject newPlainBrush() /*-{
		return new $wnd.SyntaxHighlighter.brushes.Plain();
	}-*/;
	
	public static native JavaScriptObject newCssBrush() /*-{
		return new $wnd.SyntaxHighlighter.brushes.CSS();
	}-*/;
	
	/**
	 * 
	 * Template:
	 * 	public static native JavaScriptObject newCssBrush() C{
		return new $wnd.SyntaxHighlighter.brushes.xxxx();
	}C;
	 * 
	 * Look in the shBrushY.js file to get the brush id:
	 * 
	 * 	Brush.prototype	= new SyntaxHighlighter.Highlighter();
	Brush.aliases	= ...

	SyntaxHighlighter.brushes.[xxxx]
	 */
}
