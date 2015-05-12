package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.AssertionResults;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.results.client.TestLog;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.TabbedWindow;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MetadataInspectorTab extends TabbedWindow {

	TabContainer container;
	VerticalPanel historyPanel;
	VerticalPanel detailPanel;
	VerticalPanel structPanel;
	boolean freezeStructDisplay = false;

	RadioButton selectHistory = null;
	RadioButton selectContents = null;
	RadioButton selectDiff = null;
	
	DataModel data;

	public void addToHistory(List<Result> results) {
		for (Result result : results)
			addToHistory(result);
	}

	public void addToHistory(Result result) {
		if (result != null) {
			if (data.results == null)
				data.results = new ArrayList<Result>();
			data.results.add(result);
		}
		data.buildCombined();
		showHistoryOrContents();
	}
	
	HorizontalPanel hpanel;
	
	ToolkitServiceAsync toolkitService;
	List<Result> results;
	SiteSpec siteSpec;
	
	public void setToolkitService(ToolkitServiceAsync tsa) { toolkitService = tsa; }
	public void setResults(List<Result> results) { this.results = results; }
	public void setSiteSpec(SiteSpec ss) { siteSpec = ss; }

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		this.container = container;
		
		data = new DataModel();
		
		data.siteSpec = siteSpec;
		
		if (siteSpec == null)
			data.enableActions = false;

		data.toolkitService = toolkitService;
		topPanel = new VerticalPanel();
		container.addTab(topPanel, "Inspector", select);
		topPanel.setWidth("100%");
		addCloseButton(container,topPanel, null, siteSpec);

		HTML title = new HTML();
		title.setHTML("<h2>Inspector</h2>");
		topPanel.add(title);

		hpanel = new HorizontalPanel();
		topPanel.add(hpanel);
		topPanel.setCellWidth(hpanel, "100%");
		hpanel.setBorderWidth(1);

		historyPanel = new VerticalPanel();
		hpanel.add(historyPanel);
		hpanel.setCellWidth(historyPanel, "30%");

		detailPanel = new VerticalPanel();
		detailPanel.add(HyperlinkFactory.addHTML("<h3>Detail</h3>"));
		hpanel.add(detailPanel);

		structPanel = new VerticalPanel();
		structPanel.add(HyperlinkFactory.addHTML("<h3>Structure</h3>"));
		hpanel.add(structPanel);



		addToHistory(results);

		if (!data.enableActions) {
			if (selectHistory != null) selectHistory.setEnabled(false);
			if (selectContents != null) selectContents.setEnabled(true);
			if (selectDiff != null) selectDiff.setEnabled(false);
			
			if (results.size() == 1 && !hasContents(results))
				showAssertions(results.get(0));
			else 
				showHistory();
		}
	}
	
	boolean hasContents(List<Result> results) {
		for (Result result : results) {
			if (result.hasContent()) return true;
		}
		return false;
	}

	static HTML historyCaption = HyperlinkFactory.addHTML("<h3>History</h3>");
	static HTML contentsCaption = HyperlinkFactory.addHTML("<h3>Contents</h3>");

	protected AsyncCallback<Result> metdataLoadCallback = new AsyncCallback<Result>() {

		public void onFailure(Throwable caught) {
			showMessage(caught);
		}

		public void onSuccess(Result result) {
			addToHistory(result);
		}

	};

	void showHistoryOrContents() {
		if (isHistory())
			showHistory();
		else if (isDiff())
			new DiffDisplay(this, data.combinedMetadata).showDiff(historyPanel);
		else
			showContents();
	}
	
	
	void showAssertions(Result result) {
		historyPanel.clear();
//		detailPanel.setVisible(false);
//		structPanel.setVisible(false);
		detailPanel.removeFromParent();
		structPanel.removeFromParent();

		historyPanel.add(new HTML("<h3>Assertions</h3>"));
		
		StringBuffer buf = new StringBuffer();
		for (AssertionResult ar : result.assertions.assertions) {
			if (!isEmpty(ar.assertion))
				buf.append(redAsText(htmlize(ar.assertion), ar.status)).append("<br />");
			if (!isEmpty(ar.info))
				buf.append(redAsText(htmlize(ar.info), ar.status)).append("<br />");
		}
		historyPanel.add(new HTML(buf.toString()));
		
	}
	
	String htmlize(String s) {
		return s.replaceAll("\\n", "<br />");
	}
	
	void showContents() {
		historyPanel.clear();

		HTML title = new HTML();
		title.setHTML("<h3>Contents</h3>");
		historyPanel.add(title);

		addHistoryContentsSelector(historyPanel);

		selectHistory.setValue(false);
		selectContents.setValue(true);
		selectDiff.setValue(false);

		if (data.combinedMetadata != null) {

			Tree contentTree = new Tree();

			new ListingDisplay(this, data, new TreeThing(contentTree)).listing(container);

			historyPanel.add(contentTree);
		}
	}

	boolean isHistory() {
		if (selectContents == null)
			return true;
		if (selectContents.getValue())
			return false;
		if (selectHistory.getValue())
			return true;
		return false;
	}
	
	boolean isDiff() {
		return selectDiff.getValue();
	}

	void addHistoryContentsSelector(VerticalPanel panel) {
		FlexTable ft = new FlexTable();

		selectHistory = new RadioButton("historyContents", "History");
		selectContents = new RadioButton("historyContents", "Contents");
		selectDiff = new RadioButton("historyContents", "Diff");

		selectHistory.addClickHandler(new HistorySelectChange());
		selectContents.addClickHandler(new HistorySelectChange());
		selectDiff.addClickHandler(new HistorySelectChange());

		ft.setWidget(0, 0, selectHistory);
		ft.setWidget(0, 1, selectContents);
		ft.setWidget(0, 2, selectDiff);

		panel.add(ft);
	}

	class HistorySelectChange implements ClickHandler {

		public void onClick(ClickEvent event) {
			showHistoryOrContents();
		}

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


	void showHistory() {
		historyPanel.clear();
		HTML title = new HTML();
		title.setHTML("<h3>History</h3>");
		historyPanel.add(title);

		addHistoryContentsSelector(historyPanel);

		selectHistory.setValue(true);
		selectContents.setValue(false);
		selectDiff.setValue(false);

		if (data.results == null)
			return;

		for (Result res : data.results) {
			AssertionResults ares = res.assertions;
			Tree historyTree = new Tree();
			TreeItem historyElement = new TreeItem(redAsHTML(res.testName + "   (" + res.timestamp + ")", !ares.isFailed()));
			historyTree.addItem(historyElement);
			
//			if (data.results.size() == 1) 
//				historyElement.setState(true);


			int stepCount = res.stepResults.size();

			for (StepResult stepResult : res.stepResults) {

				TreeItem stepTreeItem;
				if (stepCount > 1) {
					stepTreeItem = new TreeItem(redAsHTML("Step: " + stepResult.section + "/" + stepResult.stepName, stepResult.status));
					historyElement.addItem(stepTreeItem);
				} else {
					stepTreeItem = historyElement;
				}

				DataModel dm = new DataModel(data);
				dm.combinedMetadata = stepResult.getMetadata(); 
				dm.allDocs = stepResult.documents;
				
				new ListingDisplay(this, dm, new TreeThing(stepTreeItem)).listing(container);
				
//				listing(stepResult.getMetadata(), stepResult.documents, new TreeThing(stepTreeItem));

				if (data.enableActions && stepResult.toBeRetrieved.size() > 0) {
					ObjectRefs ors = stepResult.nextNObjectRefs(10);
					TreeItem getNextItem = new TreeItem(HyperlinkFactory.getDocuments(this, stepResult, ors, "Action: Get Full Metadata for next " + ors.objectRefs.size(), false));
					stepTreeItem.addItem(getNextItem);
				}


				if (stepResult.getTestLog() != null) {
					buildLogMenu(stepResult, stepTreeItem);
				}
			}

			if (res.stepResults.size() > 0) {
				StepResult firstStepResult = res.stepResults.get(0);
				if (firstStepResult.getTestLog() == null) {

					RawLogLoader ll = new RawLogLoader(this, res.logId, res.stepResults);
					TreeItem loadLogs = new TreeItem(HyperlinkFactory.link("load logs", ll));
					ll.setLoadLogsTreeItem(loadLogs);

					historyElement.addItem(loadLogs);
				}

			}

//			if (data.enableActions) {
				TreeItem asserts = new TreeItem(HyperlinkFactory.link(this, ares, redAsText("assertions", !ares.isFailed())));
				historyElement.addItem(asserts);
//				if (ares.isFailed())
//					asserts.setState(true);
//			}

			historyPanel.add(historyTree);
			if (data.results.size() == 1) {
				historyElement.setSelected(true);
				historyElement.setState(true);
			}

		}
	}

	void buildLogMenu(StepResult stepResult, TreeItem stepTreeItem) {
		TreeItem logsItem = new TreeItem("logs");
		stepTreeItem.addItem(logsItem);

		TestLog stepLog = stepResult.getTestLog();

		logsItem.addItem(new TreeItem("status : " + ((stepLog.status) ? "pass" : "<font color=\"#FF0000\">fail</font>")));
		logsItem.addItem(new TreeItem(HyperlinkFactory.link("endpoint", new TextDisplay(this, stepLog.endpoint))));
		logsItem.addItem(new TreeItem(HyperlinkFactory.link("request", new TextDisplay(this, stepLog.inputMetadata))));
		logsItem.addItem(new TreeItem(HyperlinkFactory.link("response", new TextDisplay(this, stepLog.result))));
		logsItem.addItem(new TreeItem(HyperlinkFactory.link("request header", new TextDisplay(this, stepLog.outHeader))));
		logsItem.addItem(new TreeItem(HyperlinkFactory.link("response header", new TextDisplay(this, stepLog.inHeader))));
		if (stepLog.errors != null && !stepLog.errors.equals(""))
			logsItem.addItem(new TreeItem(HyperlinkFactory.link("<font color=\"#FF0000\">errors</font>", new TextDisplay(this, stepLog.errors))));
		logsItem.addItem(new TreeItem(HyperlinkFactory.link("full log", new TextDisplay(this, stepLog.log))));
	}

	void expandLogMenu(StepResult stepResult, TreeItem loadLogsTreeItem) {
		TreeItem logsItem = new TreeItem("logs");
		//		stepTreeItem.addItem(logsItem);

		TestLog stepLog = stepResult.getTestLog();

		logsItem.addItem(new TreeItem("status : " + ((stepLog.status) ? "pass" : "<font color=\"#FF0000\">fail</font>")));
		logsItem.addItem(new TreeItem(HyperlinkFactory.link("endpoint", new TextDisplay(this, stepLog.endpoint))));
		logsItem.addItem(new TreeItem(HyperlinkFactory.link("request", new TextDisplay(this, stepLog.inputMetadata))));
		logsItem.addItem(new TreeItem(HyperlinkFactory.link("response", new TextDisplay(this, stepLog.result))));
		logsItem.addItem(new TreeItem(HyperlinkFactory.link("request header", new TextDisplay(this, stepLog.outHeader))));
		logsItem.addItem(new TreeItem(HyperlinkFactory.link("response header", new TextDisplay(this, stepLog.inHeader))));
		if (stepLog.errors != null && !stepLog.errors.equals(""))
			logsItem.addItem(new TreeItem(HyperlinkFactory.link("<font color=\"#FF0000\">errors</font>", new TextDisplay(this, stepLog.errors))));
		logsItem.addItem(new TreeItem(HyperlinkFactory.link("full log", new TextDisplay(this, stepLog.log))));

		replace(loadLogsTreeItem, logsItem);

	}

	void replace(TreeItem old, TreeItem neww) {
		TreeItem parent = old.getParentItem();
		int oldIndex = parent.getChildIndex(old);
		parent.removeItem(old);
		parent.insertItem(oldIndex, neww);
		neww.setSelected(true);
		neww.setState(true);  // open
	}

	void error(String msg) {
		detailPanel.clear();
		detailPanel.add(HyperlinkFactory.addHTML("<font color=\"#FF0000\">" + msg + "</font>"));
	}

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
		if (testLogs.logId == null)
			return null;
		for (Result result : data.results) {
			if (testLogs.logId.equals(result.logId)) {
				return result;
			}
		}
		return null;
	}



	public String getWindowShortName() {
		return "metadatainspector";
	}

	



	boolean isEmpty(String b) { return b == null || b.equals(""); }




}
