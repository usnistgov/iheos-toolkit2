package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.ToolWindow;

import java.util.List;

public class TextViewerTab extends ToolWindow {
	boolean escapeHTML = true;
	
	public TextViewerTab(boolean escapeHTML) {
		this.escapeHTML = escapeHTML;
	}

	public TextViewerTab() {}

	public void onTabLoad(TabContainer container, boolean select) {
	}
	
	List<Result> results;
	
	public void setResult(List<Result> results) { this.results = results; }

	@Override
	public void onTabLoad(boolean select, String eventName) {
		registerTab(select, "Viewer");
		tabTopPanel.setWidth("100%");

		HTML title = new HTML();
		title.setHTML("<h2>Text Viewer</h2>");
		tabTopPanel.add(title);

		if (results.size() > 0) {
			Result result = results.get(0); 
			String text = result.getText();

			if (result.assertions.isFailed()) {
				StringBuffer buf = new StringBuffer();
				for (AssertionResult ar : result.assertions.getAssertions()) {
					buf.append(ar.assertion).append("\n");
				}
				HTML er = new HTML();
				er.setText(buf.toString());
				tabTopPanel.add(er);
			}

			if (text != null) {
				HTML stuff = new HTML();
				stuff.setHTML(text.replaceAll("<", "&lt;").replaceAll("\\n", "<br />"));
				tabTopPanel.add(stuff);
			}
		}

	}

	public void onTabLoad(boolean select, String text, String titleString) {
		registerTab(select, titleString);
		tabTopPanel.setWidth("100%");

		HTML title = new HTML();
		title.setHTML("<h2>" + titleString + "</h2>");
		tabTopPanel.add(title);


		if (text != null) {
			HTML stuff = new HTML();
			if (escapeHTML)
				stuff.setHTML(text.replaceAll("<", "&lt;").replaceAll(" ", "&nbsp;").replaceAll("\\n", "<br />"));
			else
				stuff.setHTML(text);
			tabTopPanel.add(stuff);
		}
	}

	public String getWindowShortName() {
		return "txtviewer";
	}

}
