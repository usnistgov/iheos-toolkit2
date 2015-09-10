package gov.nist.toolkit.xdstools2.client.tabs.testRunnerTab;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.StringSort;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.tabs.testRunnerTab.TestDetailsModel.Status;
import gov.nist.toolkit.xdstools2.client.tabs.testRunnerTab.TestRunnerView.RunButton;
import gov.nist.toolkit.xdstools2.client.tabs.testRunnerTab.TestRunnerView.ViewLogButton;
import gov.nist.toolkit.xdstools2.client.widgets.SiteSelectionWidget.SiteSelectionWidget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TestRunnerPresenter {

	interface TestDisplay {
		
		void setActorSelections(Collection<String> actors);
		VerticalPanel getSiteSelectionPanel();
		void initTestGrid(List<TestDetailsModel> models);
		List<ViewLogButton> getViewLogButtons();
		List<RunButton> getRunButtons();
		TextBox getPatientIdBox();
		void setErrorDisplay(String error);
		void clearErrorDisplay();
		/**
		 * Update the changable parts of a test row
		 * @param model
		 * @return ViewLogButton if newly created
		 */
		ViewLogButton updateTestRow(TestDetailsModel model);
		
		HasChangeHandlers getActorList();
		int getSelectedActorRow();

	}
			
	TestDisplay display;
	ToolkitServiceAsync toolkitService;
	TabContainer container;
	SiteSelectionWidget siteSelectionWidget = null;
	/**
	 * Test id ==> Test description
	 */
	Map<String, String> testCollectionMap;  // name => description for selected actor
	Map<String, Result> results = new HashMap<String, Result>();            // testId => results
	// map of testName => section name list
	Map<String, List<String>> sectionMap = new HashMap<String, List<String>>();

	
	String currentActorCode = null;
	String currentActorDescription = null;
	
	TestRunnerPresenter(TabContainer container, TestDisplay d, ToolkitServiceAsync toolkitService) {
		display = d;
		this.toolkitService = toolkitService;
		this.container = container;
		
		bind();
		
		loadActorNames();
		
	}
	
	void bind() {
		
			display.getActorList().addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					int selected = display.getSelectedActorRow();
					if (selected == -1)
						return;
					actorSelected(selected);
					
					addSiteSelectionWidget();
					
					loadTestResults();
				}
				
			});
			
	}

	void bindAfterInitTestGrid() {
		for (ViewLogButton b : display.getViewLogButtons()) {
			
			b.addClickHandler(new ViewLogClickHandler(b));
			
		}
		
		for (RunButton b : display.getRunButtons()) {
			b.addClickHandler(new RunButtonClickHandler(b));
		}
		
	}
	
	class ViewLogClickHandler implements ClickHandler {
		ViewLogButton b;
		
		ViewLogClickHandler(ViewLogButton b) { this.b = b; }
		
		@Override
		public void onClick(ClickEvent event) {
			MetadataInspectorTab itab = new MetadataInspectorTab();
			itab.setResults(asList(results.get(b.testId)));
			itab.setSiteSpec((siteSelectionWidget == null) ? null : siteSelectionWidget.getSelectedSiteSpec());
			itab.setToolkitService(toolkitService);
			itab.onTabLoad(container, true, null);
		}
		
	}
	
	SiteSpec getSelectedSiteSpec() {
		return siteSelectionWidget.getSelectedSiteSpec();
	}
	
	class RunButtonClickHandler implements ClickHandler {
		RunButton runButton;
		
		RunButtonClickHandler(RunButton runButton) {
			this.runButton = runButton;
		}

		@Override
		public void onClick(ClickEvent event) {

				run();
		}
		

		void run() {
			display.clearErrorDisplay();
			
			List<String> selectedSections = null;

			Map<String, String> parms = new HashMap<String, String>();
			String pid = display.getPatientIdBox().getText();
			if (pid != null && !pid.equals("")) { 
				pid = pid.trim();
				parms.put("$patientid$", pid);
			} else {
				new PopupMessage("Patient ID must be entered");
				return;
			}
			
			String testSessionName = container.getTestSessionState().getTestSessionName();
			
			SiteSpec siteSpec = getSelectedSiteSpec();
			if (siteSpec == null) {
				new PopupMessage("Site must be selected");
				return;
			}
			siteSpec.isTls = siteSelectionWidget.isTlsSelected();
			
			String testId = runButton.model.testId;
			toolkitService.runMesaTest(testSessionName, siteSpec, testId, 
					selectedSections, parms, true, queryCallback);
		}
		
		protected AsyncCallback<List<Result>> queryCallback = new AsyncCallback<List<Result>> () {

			public void onFailure(Throwable caught) {
				new PopupMessage(caught.getMessage());
//				resultPanel.clear();
//				resultPanel.add(addHTML("<font color=\"#FF0000\">" + "Error running validation: " + caught.getMessage() + "</font>"));
			}

			public void onSuccess(List<Result> newResults) {
				TestDetailsModel model = runButton.model;
				
				model.passed = TestDetailsModel.Status.Passed;
				
				for (Result result : newResults) {
					String testId = model.testId;
					result.setTestName(testId);
					results.put(testId, result);
					String assertn = "";
					for (AssertionResult ar : result.assertions.assertions) {
						String assertion = ar.assertion.replaceAll("\n", "<br />");
						if (ar.status) {
//							resultPanel.add(addHTML(assertion));
						} else {
							if (assertion.contains("EnvironmentNotSelectedException"))
								display.setErrorDisplay("Environment Not Selected");
							else if (assertion.contains("TestSessionNotSelectedException")) {
								display.setErrorDisplay("TestSession Not Selected");
							}
							else
								assertn = assertn + " " + assertion;
							
							model.passed = TestDetailsModel.Status.Failed;
						}
					}
					display.setErrorDisplay(assertn);
					model.timestamp = result.timestamp;
					model.hasBeenRun = true;
				}
				
				ViewLogButton viewLogButton = display.updateTestRow(model);
				if (viewLogButton != null)
					viewLogButton.addClickHandler(new ViewLogClickHandler(viewLogButton));
				
//				getInspectButton().setEnabled(true);
//				getGoButton().setEnabled(true);
			}

		};

	}
		
	List<Result> asList(Result result) {
		List<Result> l = new ArrayList<Result>();
		l.add(result);
		return l;
	}
	
	/*
	 * Actor selection
	 */

	Map<String, String> actorNamesAndDescriptions;
	Collection<String> actorDescriptions;

	void actorSelected(int selected) {
		Object[] descriptions =  actorDescriptions.toArray();
		Object desc = descriptions[selected];
		
		System.out.println("Selected actor " + desc);
		
		for (String name : actorNamesAndDescriptions.keySet()) {
			String description = actorNamesAndDescriptions.get(name);
			if (description.equals(desc)) {
				currentActorCode = name;
				currentActorDescription = description;
				break;
			}
		}
		
		System.out.println("currentActor is " + currentActorDescription + " (" + currentActorCode + ")");
	}
	
	void loadActorNames() {
		toolkitService.getCollectionNames("actorcollections", new AsyncCallback<Map<String, String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getCollectionNames: " + caught.getMessage());
			}

			public void onSuccess(Map<String, String> actorCollectionMap) {
				actorNamesAndDescriptions = actorCollectionMap;
				
				actorDescriptions = actorCollectionMap.values();

				display.setActorSelections(actorDescriptions);
				
			}
		});
	}
	
	/*
	 * Site selection
	 */
	
	
	void addSiteSelectionWidget() {
		siteSelectionWidget = new SiteSelectionWidget(
				new CoupledTransactions(), // couplings
				ActorType.findActor(currentActorCode),
				toolkitService
				);
		
		VerticalPanel ssp = display.getSiteSelectionPanel();
		ssp.clear();
		ssp.add(siteSelectionWidget.getTopPanel());
	}
	
	/*
	 * Test selection 
	 */
	
	boolean testResultsLoaded = false;
	boolean sectionNamesLoaded = false;
	
	void loadTestResults() {
		if (currentActorCode == null)
			return;

		toolkitService.getCollection("actorcollections", currentActorCode, new AsyncCallback<Map<String, String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getCollection(actorcollections): " + currentActorCode + " -----  " + caught.getMessage());
			}

			public void onSuccess(Map<String, String> result) {
				testCollectionMap = result;
				
				loadAllSectionNames(asList(testCollectionMap.keySet()));

				String testSessionName = container.getTestSessionState().getTestSessionName();
				
				toolkitService.getTestResults(asList(testCollectionMap.keySet()), testSessionName, new AsyncCallback<Map<String, Result>>() {

					@Override
					public void onFailure(Throwable caught) {
						new PopupMessage("getTestResults(): " + caught.getMessage());
					}

					@Override
					public void onSuccess(Map<String, Result> newResults) {
						for (String testId : newResults.keySet()) {
							results.put(testId, newResults.get(testId));
						}
						
						testResultsLoaded = true;
						
						if (testResultsLoaded && sectionNamesLoaded)
							displayTestResults();
					}
					
				});
			}
		});
		
		
		
	}
	
	List<TestDetailsModel> buildTestDetailsModels() {
		List<TestDetailsModel> models = new ArrayList<TestDetailsModel>();
		
		for (String testId : new StringSort().sort(testCollectionMap.keySet())) {
			Result result = results.get(testId);
			TestDetailsModel m = new TestDetailsModel();
			m.testId = testId;
			m.testDescription = testCollectionMap.get(testId);
			m.isInitiator = false;
			
			m.timestamp = "";
			m.hasBeenRun = false;
			m.passed = Status.NA;
			if (result != null) {
				m.timestamp = result.timestamp;
				m.hasBeenRun = true;
				m.passed = (result.passed()) ? Status.Passed : Status.Failed;
			}
			
			
			m.sections = sectionMap.get(testId);
			
			models.add(m);
		}
		
		return models;
		
	}
	

	void displayTestResults() {
		List<TestDetailsModel> models = buildTestDetailsModels();
		
		display.initTestGrid(models);
		
		bindAfterInitTestGrid();

	}
	
	
	void loadAllSectionNames(List<String> testNames) {
		for (String name : testNames) loadSectionNames(name);
	}
	
	void loadSectionNames(String testName) {
		final String test = testName;
		
		toolkitService.getTestIndex(testName, new AsyncCallback<List<String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getTestIndex: " + caught.getMessage());
			}

			public void onSuccess(List<String> sections) {
				sectionMap.put(test, sections);
				
				sectionNamesLoaded = true;
				
				if (testResultsLoaded && sectionNamesLoaded)
					displayTestResults();

			}

		});
	}


	
	/*
	 * Utilities
	 */
	
	boolean isEmpty(String a) { return a == null || a.equals(""); }
	
	List<String> asList(Collection<String> c ) {
		List<String> l = new ArrayList<String>();
		for (String s : c) l.add(s);
		return l;
	}

}
