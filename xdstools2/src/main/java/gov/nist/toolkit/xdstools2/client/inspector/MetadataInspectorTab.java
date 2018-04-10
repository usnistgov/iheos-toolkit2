package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.AssertionResults;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.results.client.TestLog;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Supports the test results inspector tab 
 */
public class MetadataInspectorTab extends ToolWindow implements IsWidget {
   
   /* 
    * Main panels below tab title display.
    * History/Contents/Difference control panel
    * Detail display relative to control panel
    */
	VerticalPanel historyPanel;
	VerticalPanel detailPanel;
	VerticalPanel structPanel;
	boolean freezeStructDisplay = false;

	/*
	 * Radio Buttons in history panel to select display.
	 */
	RadioButton selectHistory = null;
	RadioButton selectContents = null;
//	RadioButton selectDiff = null;
//	ListBox groupByListBox;
//	HorizontalPanel groupByPanel = new HorizontalPanel();

	// Data for test being displayed.
	DataModel data;
	DataNotification dataNotification;
	Logger logger = Logger.getLogger("");

	List<Tree> treeList = new ArrayList<>();
	TreeItem currentSelectedTreeItem;
	MetadataObject comparableMetadata;

	// main panel
	HorizontalPanel hpanel = new HorizontalPanel();
	Collection<Result> results;
	private SiteSpec siteSpec;


	public MetadataInspectorTab() {
	}

	public MetadataInspectorTab(boolean isWidget) {
		super(isWidget);
	}

	public void setDataNotification(DataNotification dataNotification) {
		this.dataNotification = dataNotification;
	}

	public DataNotification getDataNotification() {
		return dataNotification;
	}

	/**
    * Add test results to DataModel and redisplay history
    * @param results to add
    */
   public void addToHistory(Collection<Result> results) {
		for (Result result : results)
			addToHistory(result);
	}

	/**
	 * Add test result to DataModel and redisplay history
	 * @param result to add
	 */
	public void addToHistory(Result result) {
		if (result != null) {
			if (data.results == null)
				data.results = new ArrayList<Result>();
			data.results.add(result);
		}
		data.buildCombined();
		showHistoryOrContents();
		if (dataNotification!=null) {
			dataNotification.onAddToHistory(data.combinedMetadata);
		}
	}

	public void setResults(Collection<Result> results) { this.results = results; }
	public void setSiteSpec(SiteSpec ss) { siteSpec = ss; }

	@Override
	public void onTabLoad(boolean select, String eventName) {

		logger.log(Level.INFO, "onTabLoad Inspector ");

		preInit();
		tabTitle(select);
		init();
	}

	private void tabTitle(boolean select) {
		//		data.toolkitService = toolkitService;
		registerTab(select, "Inspector");
		tabTopPanel.setWidth("100%");

		HTML title = new HTML();
		title.setHTML("<h2>Inspector</h2>");
		tabTopPanel.add(title);

		tabTopPanel.add(hpanel);
//		tabTopPanel.setCellWidth(hpanel, "100%");
	}

	/**
	 * Is there any metadata content in Result steps
	 * @param results
	 * @return true if any metadata exists anywhere, false otherwise.
	 */
	boolean hasContents(Collection<Result> results) {
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
		/* else if (isDiff())
			new DiffDisplay(this, data.combinedMetadata).showDiff(historyPanel);
		*/
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
		treeList.clear();

		HTML title = new HTML();
		title.setHTML("<h3>Contents</h3>");
		historyPanel.add(title);

		addHistoryContentsSelector(historyPanel);

		selectHistory.setValue(false);
		selectContents.setValue(true);
//		selectDiff.setValue(false);

		if (data.combinedMetadata != null) {

			Tree contentTree = new Tree();
			treeList.add(contentTree);
			if (dataNotification!=null) {
				addTreeSelectionHandler(contentTree);
			}

			new ListingDisplay(this, data, new TreeThing(contentTree), null, null).listing();

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
	
//	boolean isDiff() {
//		return selectDiff.getValue();
//	}

	/**
	 * Add radio button group "History, Contents, Diff" to passed panel
	 * @param panel to add to
	 */
	void addHistoryContentsSelector(VerticalPanel panel) {
		FlexTable ft = new FlexTable();

		selectHistory = new RadioButton("historyContents", "History");
		selectContents = new RadioButton("historyContents", "Contents");
//		selectDiff = new RadioButton("historyContents", "Diff");

		selectHistory.addClickHandler(new HistorySelectChange());
		selectContents.addClickHandler(new HistorySelectChange());
//		selectDiff.addClickHandler(new HistorySelectChange());

		ft.setWidget(0, 0, selectHistory);
		ft.setWidget(0, 1, selectContents);
//		ft.setWidget(0, 2, selectDiff);

		panel.add(ft);

		/*
		groupByPanel.clear();
		groupByPanel.getElement().getStyle().setMargin(2, Style.Unit.PX);
		groupByPanel.add(new HTML("Group by"));
		groupByPanel.add(groupByListBox);
		groupByPanel.add(new HTML("&nbsp;"));
		groupByPanel.add(new InformationLink("Help with GroupBy feature", "Inspector-GroupBy-feature").asWidget()); // Todo.
		panel.add(groupByPanel);
		*/
	}

	class HistorySelectChange implements ClickHandler {

		public void onClick(ClickEvent event) {
//			groupByPanel.setVisible(selectHistory.getValue().booleanValue());
			showHistoryOrContents();
			if (dataNotification!=null) {
				if (currentSelectedTreeItem!=null && currentSelectedTreeItem.getUserObject()!=null && (currentSelectedTreeItem.getUserObject() instanceof  MetadataObjectWrapper))
				dataNotification.onHistoryContentModeChanged((MetadataObjectWrapper)currentSelectedTreeItem.getUserObject());
			}
		}
	}

   /**
    * Return passed text as HTML in red font
    * @param in text
    * @return HTML, with color="#FF0000"
    */
	HTML redAsHTML(String in) {
		HTML h = new HTML();
		h.setHTML("<font color=\"#FF0000\">" + in + "</font>");
		return h;
	}

	/**
	 * Return passed text as text in red font
	 * @param in text to process
	 * @return String {@code <font color="#FF0000">in</font>}
	 */
	String redAsText(String in) {
		return "<font color=\"#FF0000\">" + in + "</font>";
	}
	/**
    * Return passed text as HTML in red font, if condition not met (that is,
    * passed condition is false)
    * @param in text text to process
    * @param condition test
    * @return HTML with color="#FF0000" if condition is false otherwise with
    * no color attribute.
    */
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

   /**
    * Show the history display in the history/contents/difference panel
    */
	void showHistory() {
		historyPanel.clear();
		treeList.clear();

		// History panel title
		HTML title = new HTML();
		title.setHTML("<h3>History</h3>");
		historyPanel.add(title);
      // Radio button group
		addHistoryContentsSelector(historyPanel);
      // set history button on, others off
		selectHistory.setValue(true);
		selectContents.setValue(false);
//		selectDiff.setValue(false);

		if (data.results == null)
			return;

      // Pass tests, displaying history results for each test.
		for (Result res : data.results) {
			AssertionResults ares = res.assertions;
			// Tree widget created for each test
			Tree historyTree = new Tree();
			treeList.add(historyTree);
			if (dataNotification!=null) {
				addTreeSelectionHandler(historyTree);
			}
			// tree element for test with test id & time stamp, red if test failed
			TreeItem historyElement =
			   new TreeItem(redAsHTML(res.testInstance.getId()
					   + ((res.testInstance.getSection() != null) ? ":" + res.testInstance.getSection() : "")
					   + "   (" + res.timestamp + ")",
					   !ares.isFailed()));
			historyTree.addItem(historyElement);

			int stepCount = res.stepResults.size();
			
         // Pass steps for test
			for (StepResult stepResult : res.stepResults) {

				TreeItem stepTreeItem;
				/* If there is more than one step in the test, each step has a
				 * step element in the tree, listing the step and section names,
				 * in red if the step failed. If only one step, the test element
				 * is used as the step element.
				 */
				if (stepCount > 1) {
					stepTreeItem = new TreeItem(redAsHTML("Step: " + stepResult.section + "/" + stepResult.stepName, stepResult.status));
					historyElement.addItem(stepTreeItem);
				} else {
					stepTreeItem = historyElement;
				}

				/* New datamodel copied from test one, but using metadata and
				 * documents from this step
				 */
				DataModel dm = new DataModel(data);
				dm.combinedMetadata = stepResult.getMetadata(); 
				dm.allDocs = stepResult.documents;

				/*
				String selectedGroupByValue = groupByListBox.getValue(groupByListBox.getSelectedIndex());
				if ("(none)".equals(selectedGroupByValue)) {
					new ListingDisplay(this, dm, new TreeThing(stepTreeItem)).listing();
				} else if ("homeCommunityId".equals(selectedGroupByValue)) {
					new HcIdListingDisplay(this, dm, new TreeThing(stepTreeItem), stepResult);
				} else if ("repositoryId".equals(selectedGroupByValue)) {
					new RepositoryIdListingDisplay(this, dm, new TreeThing(stepTreeItem)).listing();
				}
				*/

				QueryOrigin queryOrigin = new QueryOrigin(res.logId.getId().toString(), stepResult.section, stepResult.stepName);
				new ListingDisplay(this, dm, new TreeThing(stepTreeItem), res.logId, queryOrigin).listing();

				if (data.enableActions && stepResult.toBeRetrieved.size() > 0) {
					ObjectRefs ors = stepResult.nextNObjectRefs(10);
					TreeItem getNextItem = new TreeItem(HyperlinkFactory.getDocuments(this, stepResult, ors, "Action: Get Full Metadata for next " + ors.objectRefs.size(), false, siteSpec));
					stepTreeItem.addItem(getNextItem);
				}


				if (stepResult.getTestLog() != null) {
					buildLogMenu(stepResult, stepTreeItem);
				}
			} // EO loop through test steps

			if (res.stepResults.size() > 0) {
				StepResult firstStepResult = res.stepResults.get(0);
				if (firstStepResult.getTestLog() == null) {

					RawLogLoader ll = new RawLogLoader(this, res.logId, res.stepResults);
//					ll.loadTestLogs();
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

		} // EO loop  through tests
	} // EO showHistory method
	

	void buildLogMenu(StepResult stepResult, TreeItem stepTreeItem) {
		TreeItem ti;
		TreeItem logsItem = new TreeItem();
		logsItem.setText("logs");
		stepTreeItem.addItem(logsItem);

		TestLog stepLog = stepResult.getTestLog();

		ti = new TreeItem();
		ti.setHTML("status : " + ((stepLog.status) ? "pass" : "<font color=\"#FF0000\">fail</font>"));
		logsItem.addItem(ti);
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
		TreeItem logsItem = new TreeItem();
		logsItem.setText("logs");
		//		stepTreeItem.addItem(logsItem);

		TestLog stepLog = stepResult.getTestLog();

		TreeItem ti;
		ti = new TreeItem();
		ti.setHTML("status : " + ((stepLog.status) ? "pass" : "<font color=\"#FF0000\">fail</font>"));
		logsItem.addItem(ti);

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
		if (testLogs.testInstance == null)
			return null;
		for (Result result : data.results) {
			if (testLogs.testInstance.equals(result.logId)) {
				return result;
			}
		}
		return null;
	}



	public String getWindowShortName() {
		return "metadatainspector";
	}

	



	boolean isEmpty(String b) { return b == null || b.equals(""); }


	public SiteSpec getSiteSpec() {
		return siteSpec;
	}

 	public void preInit() {

		data = new DataModel();

		data.siteSpec = siteSpec;

//		GWT.log("In MetadataInsp siteSpec is " + siteSpec.name);

		if (siteSpec == null)
			data.enableActions = false;
	}

	public MetadataCollection init() {
		treeList.clear();
		hpanel.clear();
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

		/*
		groupByListBox = new ListBox();
		groupByListBox.addItem("(none)"); // No group by
		groupByListBox.addItem("homeCommunityId"); // hcId groups object types
		groupByListBox.addItem("repositoryId");		// Object type groups repositoryId
		groupByListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent changeEvent) {
				showHistoryOrContents();
			}
		});
		*/


		if (results!=null) {
			addToHistory(results);
		} else {
			data.buildCombined();
			showHistoryOrContents();
		}

		if (!data.enableActions) {
			if (selectHistory != null) selectHistory.setEnabled(false);
			if (selectContents != null) selectContents.setEnabled(true);
//			if (selectDiff != null) selectDiff.setEnabled(false);
//			if (groupByListBox != null) groupByListBox.setEnabled(false);

			if (results.size() == 1 && !hasContents(results))
				showAssertions(results.iterator().next());
			else
				showHistory();
		}

		return data.combinedMetadata;
	}

	public List<Tree> getTreeList() {
		return treeList;
	}

	public void showHistory(boolean show) {
		if (!show) {
		    historyPanel.removeFromParent();
		} else if (!hpanel.getWidget(0).equals(historyPanel)) {
			hpanel.insert(historyPanel, 0);
		}

	}

	public void showStructure(boolean show) {
		if (!show) {
			structPanel.removeFromParent();
		} else if (hpanel.getWidgetCount()>0 && !hpanel.getWidget(hpanel.getWidgetCount()-1).equals(structPanel)) {
			hpanel.add(structPanel);
		}
	}

	public DataModel getData() {
		return new DataModel(data);
	}

	public void setData(DataModel data) {
		this.data = data;
	}

	private void addTreeSelectionHandler(final Tree t) {
			t.addSelectionHandler(new SelectionHandler<TreeItem>() {
				@Override
				public void onSelection(SelectionEvent<TreeItem> selectionEvent) {
					TreeItem selectedItem = (TreeItem)selectionEvent.getSelectedItem();
					TreeItem itemToSelect = selectedItem;
					if (selectedItem.getUserObject()==null) {
						// If available, get the parent of child as selected, when something like Action:Metadataupdate was selected.
						TreeItem parent = selectedItem.getParentItem();
						if (parent.getUserObject() != null && parent.getUserObject() instanceof MetadataObjectWrapper) {
							itemToSelect = parent;
						} else {
							// Return if no parent to select
							return;
						}
					}
					// Already at the parent level
					if (currentSelectedTreeItem!=null) {
						currentSelectedTreeItem.getWidget().removeStyleName("insetBorder");
					}
					itemToSelect.getWidget().addStyleName("insetBorder");
					currentSelectedTreeItem = itemToSelect;
					if (itemToSelect.getUserObject()!=null && itemToSelect.getUserObject() instanceof MetadataObjectWrapper) {
						MetadataObjectWrapper userObject = (MetadataObjectWrapper) itemToSelect.getUserObject();
						dataNotification.onObjectSelected(userObject);
						if (itemToSelect.getWidget() !=null && itemToSelect.getWidget() instanceof Hyperlink) {
							((Hyperlink) itemToSelect.getWidget()).fireEvent(new ClickEvent() {
							});
						}
					}
				}
			});
	}

	public TreeItem getCurrentSelectedTreeItem() {
		return currentSelectedTreeItem;
	}

	public void setCurrentSelectedTreeItem(TreeItem currentSelectedTreeItem) {
		this.currentSelectedTreeItem = currentSelectedTreeItem;
	}

	public MetadataObject getComparableMetadata() {
		return comparableMetadata;
	}

	public void setComparableMetadata(MetadataObject comparableMetadata) {
		this.comparableMetadata = comparableMetadata;
	}

	@Override
	public Widget asWidget() {
	    return hpanel;
	}
}
