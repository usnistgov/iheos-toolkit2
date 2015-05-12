package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.TabbedWindow;

import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TextViewerTab extends TabbedWindow {
	boolean escapeHTML = true;
	
	public TextViewerTab(boolean escapeHTML) {
		this.escapeHTML = escapeHTML;
	}
	
	public TextViewerTab() {}

	public void onTabLoad(TabContainer container, boolean select) {
	}
	
	List<Result> results;
	
	public void setResult(List<Result> results) { this.results = results; }

	public void onTabLoad(TabContainer container, boolean select, String eventName) {		
		topPanel = new VerticalPanel();
		container.addTab(topPanel, "Viewer", select);
		topPanel.setWidth("100%");
		addCloseButton(container,topPanel, null, null);

		HTML title = new HTML();
		title.setHTML("<h2>Text Viewer</h2>");
		topPanel.add(title);

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
				topPanel.add(er);
			}

			if (text != null) {
				HTML stuff = new HTML();
				stuff.setHTML(text.replaceAll("<", "&lt;").replaceAll("\\n", "<br />"));
				topPanel.add(stuff);
			}
		}

	}

	public void onTabLoad(TabContainer container, boolean select, String text, String titleString) {		
		topPanel = new VerticalPanel();
		container.addTab(topPanel, titleString, select);
		topPanel.setWidth("100%");
		addCloseButton(container,topPanel, null, null);

		HTML title = new HTML();
		title.setHTML("<h2>" + titleString + "</h2>");
		topPanel.add(title);


		if (text != null) {
			HTML stuff = new HTML();
			if (escapeHTML)
				stuff.setHTML(text.replaceAll("<", "&lt;").replaceAll(" ", "&nbsp;").replaceAll("\\n", "<br />"));
			else
				stuff.setHTML(text);
			topPanel.add(stuff);
		}
	}

	public String getWindowShortName() {
		return "txtviewer";
	}

}
