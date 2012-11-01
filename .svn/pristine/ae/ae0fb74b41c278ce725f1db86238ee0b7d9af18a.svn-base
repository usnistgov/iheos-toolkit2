package gov.nist.toolkit.xdstools2.client.tabs.directRegistrationTab;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;

public class Field {
	public enum Color { BLACK, RED };
	
	String id;
	String name;
	HTML nameHtml;
	TextBox textBox = new TextBox();
	Color color = Color.BLACK;
	public int row;
	public int col;
	boolean hasError = false;
	
	static String DEFAULTWIDTH = "20em";
	static String DEFAULTHEIGHT = "1em";
		
	public Field(Builder builder, String id, String name) {
		this.id = id;
		this.name = name;
		this.textBox.setWidth(DEFAULTWIDTH);
		this.textBox.setHeight(DEFAULTHEIGHT);
		this.nameHtml = new HTML(name);
		
		builder.add(this);
	}
	
	public Field(Builder builder, String id, String name, String width, String height) {
		this.id = id;
		this.name = name;
		this.textBox.setWidth(width);
		this.textBox.setHeight(height);
		this.nameHtml = new HTML(name);
		
		builder.add(this);
	}
	
	public void nameHtml(String name) {
		if (nameHtml == null)
			nameHtml = new HTML(name);
		else
			nameHtml.setHTML(name);
	}
	
	public void nameHtml(HTML h) {
		nameHtml = h;
	}
	
	public TextBox textBox() {
		return textBox;
	}
	
	public String value() {
		return textBox().getText();
	}
	
	public void error() {
		if (!hasError) {
			nameHtml(Display.asRed(name));
			hasError = true;
		}
	}
	
	public void clear() {
		if (hasError) {
			nameHtml(Display.asBlack(name));
			hasError = false;
		}
	}
	
}
