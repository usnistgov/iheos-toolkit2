package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.registrymetadata.client.Document;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.RegistryObject;
import gov.nist.toolkit.results.client.*;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.TabbedWindow;

import java.util.List;
import java.util.Map;

public class MetadataEditorTab extends TabbedWindow {

	// loaded content organized two ways:
	//    results - organized as a list - shows history of queries
	//    combinedMetadata - raw contents loaded in inspector
	List<Result> results;
	MetadataCollection combinedMetadata;

	VerticalPanel historyPanel;
	VerticalPanel detailPanel;
	VerticalPanel structPanel;
	List<Document> allDocs;
	Map<String, Document> docMap;  //key is uid
	boolean freezeStructDisplay = false;
	boolean enableActions = true;

	EditDisplay editDisplay;
	
	SiteSpec siteSpec;
	RegistryObject ro;
	
	public void setSiteSpec(SiteSpec ss) { siteSpec = ss; }
	public void setRegistryObject(RegistryObject ro) { this.ro = ro; }

	
	@Override
	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		topPanel = new VerticalPanel();
		container.addTab(topPanel, "Editor", select);
		topPanel.setWidth("100%");
		addCloseButton(container,topPanel, null, siteSpec);

		HTML title = new HTML();
		title.setHTML("<h2>Metadata Editor</h2>");
		topPanel.add(title);

		HorizontalPanel hpanel = new HorizontalPanel();
		topPanel.add(hpanel);
		topPanel.setCellWidth(hpanel, "100%");
		hpanel.setBorderWidth(1);

//		historyPanel = new VerticalPanel();
//		hpanel.add(historyPanel);
//		hpanel.setCellWidth(historyPanel, "30%");

		detailPanel = new VerticalPanel();
//		detailPanel.add(HyperlinkFactory.addHTML("<h3>Detail</h3>"));
		hpanel.add(detailPanel);

		structPanel = new VerticalPanel();
//		structPanel.add(HyperlinkFactory.addHTML("<h3>Structure</h3>"));
		hpanel.add(structPanel);

		if (siteSpec == null)
			enableActions = false;

		editDisplay = new EditDisplay(detailPanel, structPanel,this);
		editDisplay.displayDetail(ro);
	}


	HTML redAsHTML(String in) {
		HTML h = new HTML();
		h.setHTML("<font color=\"#FF0000\">" + in + "</font>");
		return h;
	}


	String redAsText(String in) {
		return "<font color=\"#FF0000\">" + in + "</font>";
	}

	HTML redAsHTML(String in, boolean condition) {
		if (!condition)
			return redAsHTML(in);
		HTML h = new HTML();
		h.setText(in);
		return h;
	}
	
	String redAsText(String in, boolean condition) {
		if (!condition)
			return redAsText(in);
		return in;
	}

	void replace(TreeItem old, TreeItem neww) {
		TreeItem parent = old.getParentItem();
		int oldIndex = parent.getChildIndex(old);
		parent.removeItem(old);
		parent.insertItem(oldIndex, neww);
		neww.setState(true);  // open
	}


	void error(String msg) {
		detailPanel.clear();
		detailPanel.add(HyperlinkFactory.addHTML("<font color=\"#FF0000\">" + msg + "</font>"));
	}

	//	protected AsyncCallback<TestLogs> logLoaderCallback = new AsyncCallback<TestLogs> () {
	//
	//		public void onFailure(Throwable caught) {
	//			error(caught.getMessage());
	//		}
	//
	//		public void onSuccess(TestLogs testLogs) {
	//			if (testLogs.assertionResult != null && testLogs.assertionResult.status == false) {
	//				error(testLogs.assertionResult.assertion);
	//			} else  {
	//				installTestLogs(testLogs);
	//				showHistoryOrContents();
	//			}
	//		}
	//
	//	};

	void installTestLogs(TestLogs testLogs) {
		Result result = findResultbyLogId(testLogs);
		if (result == null) {
			return;
		}

		for (TestLog testLog : testLogs.logs) {
			String stepName = testLog.stepName;
			StepResult stepResult = result.findStep(stepName);
			if (stepResult == null) {
				new PopupMessage("Received log for step " + stepName + " which does not exist in client");
				return;
			}
			stepResult.setTestLog(testLog);
		}

	}

	Result findResultbyLogId(TestLogs testLogs) {
		if (testLogs.testInstance == null)
			return null;
		for (Result result : results) {
			if (testLogs.testInstance.equals(result.logId)) {
				return result;
			}
		}
		return null;
	}


	class TextDisplay implements ClickHandler {
		String text;

		TextDisplay(String text) {
			this.text = text;
		}

		public void onClick(ClickEvent event) {
			detailPanel.clear();
			detailPanel.add(HyperlinkFactory.addHTML(text));
		}

	}


	public String getWindowShortName() {
		return "metadataeditor";
	}









}
