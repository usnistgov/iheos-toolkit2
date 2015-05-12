package gov.nist.toolkit.xdstools2.client.tabs.testRunnerTab;

import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class TestRunnerView implements TestRunnerPresenter.TestDisplay {
	ListBox selectActorList = new ListBox();
	ListBox selectTestList = new ListBox();
	final String chooseSelection = "-- Choose --";
	VerticalPanel topPanel;
	VerticalPanel siteSelectionPanel = new VerticalPanel();
	VerticalPanel testGridPanel = new VerticalPanel();
	FlexTable testGrid;
	TextBox patientIdBox = new TextBox();
	HTML errorDisplay = new HTML();
	Button clearErrorButton = new Button("Clear");
	HorizontalPanel errorDisplayPanel = new HorizontalPanel();
	HorizontalPanel controlPanel1 = new HorizontalPanel();
	TestRunnerSectionsView sectionsView;

		
	void build(String eventName, boolean select, TabContainer container, VerticalPanel topPanel, ToolkitServiceAsync toolkitService) {
		this.topPanel = topPanel;
		
		HTML title = new HTML();
		title.setHTML("<h2>" + eventName + "</h2>");
		topPanel.add(title);

		topPanel.add(new HTML("<h3>Test Environment</h3>"));
		
		// Actor Selection
		HorizontalPanel selectActorPanel = new HorizontalPanel();
		topPanel.add(selectActorPanel);
		
		HTML selectTestCollectionLabel = new HTML();
		selectTestCollectionLabel.setText("Select Actor Name: ");
		selectActorPanel.add(selectTestCollectionLabel);
		
		selectActorPanel.add(selectActorList);
		
		topPanel.add(siteSelectionPanel);
		
		// Patient ID
		HorizontalPanel patientIdPanel = new HorizontalPanel();
		topPanel.add(patientIdPanel);
		
		HTML patientIdLabel = new HTML();
		patientIdLabel.setText("Patient ID");
		patientIdPanel.add(patientIdLabel);

		patientIdBox.setWidth("400px");
		patientIdPanel.add(patientIdBox);
		
		topPanel.add(patientIdPanel);
		
		topPanel.add(new HTML("<h3>Tests</h3>"));
		
		topPanel.add(controlPanel1);
		
		errorDisplayPanel.add(clearErrorButton);
		errorDisplayPanel.add(errorDisplay);
		errorDisplayPanel.setVisible(false);
		clearErrorButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				errorDisplayPanel.setVisible(false);
				clearErrorButton.setVisible(false);
				errorDisplay.setHTML("");
			}
			
		});
		
		topPanel.add(errorDisplayPanel);

		
		topPanel.add(testGridPanel);
		
		sectionsView = new TestRunnerSectionsView();
		topPanel.add(sectionsView.build());
	}
	

	@Override
	public void setActorSelections(Collection<String> nameDisp) {
		selectActorList.clear();
		selectActorList.addItem(chooseSelection, "");
		
		for (String name : nameDisp) {
			selectActorList.addItem(name);
		}

	}

	@Override
	public HasChangeHandlers getActorList() {
		return selectActorList;
	}

	@Override
	public int getSelectedActorRow() {
		// hide the chooseSelection first row
		return selectActorList.getSelectedIndex() - 1;
	}


	@Override
	public VerticalPanel getSiteSelectionPanel() {
		return siteSelectionPanel;
	}
	
	List<RunButton> runButtons = new ArrayList<RunButton>();
	
	class RunButton extends Button {
		TestDetailsModel model;
		
		public RunButton(String caption, TestDetailsModel model) {
			super(caption);
			this.model = model;
			runButtons.add(this);
		}
	}
	
	List<ViewLogButton> viewLogButtons = new ArrayList<ViewLogButton>();

	class ViewLogButton extends Button {
		String testId;
		
		public ViewLogButton(String caption, String testId) {
			super(caption);
			this.testId = testId;
			viewLogButtons.add(this);
		}
	}

	@Override
	public void initTestGrid(List<TestDetailsModel> details) {
		testGridPanel.clear();
		testGrid = new FlexTable();

		String[] captions = {
				"Test",
//				"Test Description",
//				"System under test is",
//				"Sections",
				"Run?",
				"Timestamp",
				"Passed",
				"View Log"
				};
			
		int row=0;
		for (int i=0; i<captions.length; i++) {
			testGrid.setWidget(row, i, new HTML(captions[i]));
			testGrid.getCellFormatter().setStyleName(row, i, "my-table-header");
		}
		row++;
		
		boolean makeGrey = false;
		for (TestDetailsModel model : details) {
			insertTestRow(model, row, makeGrey);
			row++;
			makeGrey = !makeGrey;
		}
		

		testGridPanel.add(testGrid);
		
	}
	
	public void insertTestRow(TestDetailsModel model, int row, boolean makeGrey) {
		model.displayRow = row;
		model.isGrey = makeGrey;

		testGrid.setHTML(row, 0, model.testId + "<br />" + model.testDescription);
//		testGrid.setText  (row, 0, model.testId + "\n" + model.testDescription);
		if (model.isGrey) testGrid.getCellFormatter().setStyleName(model.displayRow, 0, "my-table-band");
		
//		testGrid.setText  (row, 1, model.testDescription);
//		if (model.isGrey) testGrid.getCellFormatter().setStyleName(model.displayRow, 1, "my-table-band");

//		testGrid.setText  (row, 2, (model.isInitiator) ? "Initiator" : "Responder");
//		if (model.isGrey) testGrid.getCellFormatter().setStyleName(model.displayRow, 2, "my-table-band");

		
		updateTestRow(model);
		
	}
	
	@Override
	public ViewLogButton updateTestRow(TestDetailsModel model) {
		Widget w;
		ViewLogButton viewLogButton = null;
		Button b;
		
		int col = 1;   // 3
		
//		//open
//		b = new Button("Open");
//		w = b;
//		testGrid.setWidget(model.displayRow, col, w);
//		if (model.isGrey) testGrid.getCellFormatter().setStyleName(model.displayRow, col, "my-table-band");
//		col++;
		
		// Run
		try {
			w = testGrid.getWidget(model.displayRow, col);
			if (model.isGrey) testGrid.getCellFormatter().setStyleName(model.displayRow, col, "my-table-band");
			b = (Button) w;
			b.setText("Run");
		} catch (Exception e) {
			w = new RunButton("Run", model);
			testGrid.setWidget(model.displayRow, col, w);
			if (model.isGrey) testGrid.getCellFormatter().setStyleName(model.displayRow, col, "my-table-band");
		}
		col++;
		
		// timestamp
		testGrid.setText  (model.displayRow, col, (model.hasBeenRun) ? model.timestamp : "");
		if (model.isGrey) testGrid.getCellFormatter().setStyleName(model.displayRow, col, "my-table-band");
		col++;

		// pass/fail
		testGrid.setText  (model.displayRow, col, model.passed.toString());
		if (model.passed == TestDetailsModel.Status.Failed) 
			testGrid.getCellFormatter().setStyleName(model.displayRow, col, "my-table-error");
		else if (model.passed == TestDetailsModel.Status.NA) 
			testGrid.getCellFormatter().setStyleName(model.displayRow, col, "my-table-not-run");
		else if (model.isGrey) 
			testGrid.getCellFormatter().setStyleName(model.displayRow, col, "my-table-band");
		else
			testGrid.getCellFormatter().setStyleName(model.displayRow, col, "my-table-noband");
		col++;

		// view log
		try {
			w = testGrid.getWidget(model.displayRow, col);
			if (! (w instanceof ViewLogButton))
				throw new Exception("");
		} catch (Exception e) {
			if (model.hasBeenRun) {
				viewLogButton = new ViewLogButton("View Log", model.testId);
				testGrid.setWidget(model.displayRow, col, viewLogButton);
				if (model.isGrey) testGrid.getCellFormatter().setStyleName(model.displayRow, col, "my-table-band");
			} else {
				testGrid.setWidget(model.displayRow, 6, new HTML(""));
				if (model.isGrey) testGrid.getCellFormatter().setStyleName(model.displayRow, col, "my-table-band");
			}
		}
		col++;
//		
		return viewLogButton;
	}


	@Override
	public List<ViewLogButton> getViewLogButtons() {
		return viewLogButtons;
	}


	@Override
	public List<RunButton> getRunButtons() {
		return runButtons;
	}


	@Override
	public TextBox getPatientIdBox() {
		return patientIdBox;
	}


	@Override
	public void setErrorDisplay(String error) {
		if (error == null || error.equals(""))
			clearErrorDisplay();
		else {
			errorDisplay.setHTML("<font color=\"#FF0000\">" + error + "</font>");
			errorDisplayPanel.setVisible(true);
			clearErrorButton.setVisible(true);
		}
	}


	@Override
	public void clearErrorDisplay() {
		errorDisplayPanel.setVisible(false);
		clearErrorButton.setVisible(false);
		errorDisplay.setHTML("");
	}



}
