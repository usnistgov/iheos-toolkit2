package gov.nist.toolkit.xdstools2.client.tabs.testRunnerTab;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TestRunnerSectionsView implements TestRunnerSectionsPresenter.SectionsDisplay {
	HorizontalPanel controlPanel2 = new HorizontalPanel();
	String sectionsHeader = "Sections of Test ";
	HTML sectionsHeaderLabel = new HTML();
	VerticalPanel mainPanel = new VerticalPanel();
	VerticalPanel testGridPanel = new VerticalPanel();
	FlexTable testGrid;
	String testName;
	List<String> sections;
	TestDetailsModel model;
	
	TestRunnerSectionsView() {
	}

	VerticalPanel build() {
		
		mainPanel.setVisible(false);
		
		mainPanel.add(sectionsHeaderLabel);
		
		mainPanel.add(controlPanel2);
		
		mainPanel.add(testGridPanel);

		return mainPanel;
	}
	
	void setTestName(String testName) { 
		this.testName = testName; 
		setSectionsHeader(testName);
	}
	
	void setSections(List<String> sections) { this.sections = sections; }
	
	void setSectionsHeader(String testName) {
		sectionsHeaderLabel.setHTML("<h3>" + sectionsHeader + testName + "</h3>");
	}
	
	void setModel(TestDetailsModel model) { 
		this.model = model;
		initTestGrid(model);
	}
	
	void show() {
		mainPanel.setVisible(true);
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

	public void initTestGrid(TestDetailsModel Model) {
		testGridPanel.clear();
		testGrid = new FlexTable();

		String[] captions = {"Section",
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
		
//		boolean makeGrey = false;
//		for (TestDetailsModel model : details) {
//			insertTestRow(model, row, makeGrey);
//			row++;
//			makeGrey = !makeGrey;
//		}
		

		testGridPanel.add(testGrid);
		
	}
	
	public void insertTestRow(TestDetailsModel model, int row, boolean makeGrey) {
		model.displayRow = row;
		model.isGrey = makeGrey;

		testGrid.setText  (row, 0, model.testId);
		if (model.isGrey) testGrid.getCellFormatter().setStyleName(model.displayRow, 0, "my-table-band");
		
		testGrid.setText  (row, 1, model.testDescription);
		if (model.isGrey) testGrid.getCellFormatter().setStyleName(model.displayRow, 1, "my-table-band");

		testGrid.setText  (row, 2, (model.isInitiator) ? "Initiator" : "Responder");
		if (model.isGrey) testGrid.getCellFormatter().setStyleName(model.displayRow, 2, "my-table-band");

		
		updateTestRow(model);
		
	}
	
	@Override
	public ViewLogButton updateTestRow(TestDetailsModel model) {
		Widget w;
		ViewLogButton viewLogButton = null;
		
		int col = 3;
		
		//open
		w = new Button("Open...");
		testGrid.setWidget(model.displayRow, col, w);
		if (model.isGrey) testGrid.getCellFormatter().setStyleName(model.displayRow, col, "my-table-band");
		col++;
		
		// Run
		try {
			w = testGrid.getWidget(model.displayRow, col);
			if (model.isGrey) testGrid.getCellFormatter().setStyleName(model.displayRow, col, "my-table-band");
			Button b = (Button) w;
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
	public void initTestGrid(List<TestDetailsModel> details) {
		// TODO Auto-generated method stub
		
	}

}
