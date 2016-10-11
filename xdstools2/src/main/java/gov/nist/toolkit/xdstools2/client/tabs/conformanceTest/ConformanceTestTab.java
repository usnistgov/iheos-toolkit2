package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TabBar;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.interactiondiagram.client.events.DiagramClickedEvent;
import gov.nist.toolkit.interactiondiagram.client.events.DiagramPartClickedEventHandler;
import gov.nist.toolkit.interactiondiagram.client.widgets.InteractionDiagram;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.services.client.AbstractOrchestrationResponse;
import gov.nist.toolkit.services.client.RepOrchestrationResponse;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.session.client.sort.TestSorter;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.event.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs.BuildIGTestOrchestrationButton;
import gov.nist.toolkit.xdstools2.client.widgets.LaunchInspectorClickHandler;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All Conformance tests will be run out of here
 */
public class ConformanceTestTab extends ToolWindow implements TestRunner, TestsHeaderView.Controller {
	// ActorType => list of options
	private static final Map<String, List<ActorAndOptions>> actorOptions;
	static {
		actorOptions = new HashMap<>();
		actorOptions.put("ig",
				java.util.Arrays.asList(
						new ActorAndOptions("ig", "", "Required"),
						new ActorAndOptions("ig", "ad", "Affinity Domain Option")));
	};

	private final ConformanceTestTab me;
	private final FlowPanel toolPanel = new FlowPanel();   // Outer-most panel for the tool
	private final FlowPanel initializationPanel = new FlowPanel();
	private final FlowPanel testsPanel = new FlowPanel();  // panel for displaying tests
	private final TabBar actorTabBar = new TabBar();            // tab bar at the top for selecting actor types
	private final OptionsTabBar optionsTabBar = new OptionsTabBar(actorOptions);
	private final FlowPanel sitesPanel = new FlowPanel();
//	private String currentSiteName = null;
	private HTML testSessionDescription = new HTML();
	private FlowPanel testSessionDescriptionPanel = new FlowPanel();
	private TestsHeaderView testsHeaderView = new TestsHeaderView(this);
	private final TestStatistics testStatistics = new TestStatistics();
	private TestDisplayGroup testDisplayGroup;
	private TestContext testContext = new TestContext(this);
	private TestContextDisplay testContextDisplay = new TestContextDisplay(this, testSessionDescription, testContext);

	private AbstractOrchestrationResponse orchestrationResponse;  // can be any of the following - contains common elements
	private RepOrchestrationResponse repOrchestrationResponse;
	private ActorOption currentActorOption = new ActorOption("none");
//    private String currentActorTypeId;
	private String currentActorTypeDescription;
//	private Site siteUnderTest = null;
	private SiteSpec sitetoIssueTestAgainst = null;

	// stuff that needs delayed setting when launched via activity
	private String initTestSession = null;

	// Testable actors
	private List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs;
	// testname ==> results
	private Map<String, TestOverviewDTO> testOverviewDTOs = new HashMap<>();
	// for each actor type id, the list of tests for it
	private Map<String, List<TestInstance>> testsPerActor = new HashMap<>();



	public ConformanceTestTab() {
		me = this;
		testContext.setTestContextDisplay(testContextDisplay);
		testDisplayGroup = new TestDisplayGroup(testContext, testContextDisplay, this);
		toolPanel.getElement().getStyle().setMargin(4, Style.Unit.PX);
		toolPanel.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
		testsPanel.getElement().getStyle().setMarginRight(4, Style.Unit.PX);
		toolPanel.add(sitesPanel);
		toolPanel.add(actorTabBar);
		toolPanel.add(optionsTabBar);
		toolPanel.add(initializationPanel);
		toolPanel.add(testsPanel);
	}

	@Override
	public void onTabLoad(boolean select, String eventName) {
		testContextDisplay.updateTestingContextDisplay();
		testSessionDescription.addClickHandler(testContextDisplay);
		testSessionDescriptionPanel.setStyleName("with-rounded-border");
		testSessionDescriptionPanel.add(testSessionDescription);
		testSessionDescriptionPanel.getElement().getStyle().setMarginLeft(2, Style.Unit.PX);

		addEast(testSessionDescriptionPanel);
		registerTab(select, eventName);

		tabTopPanel.add(toolPanel);


		// Reload if the test session changes
		Xdstools2.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
			@Override
			public void onTestSessionChanged(TestSessionChangedEvent event) {
				if (event.changeType == TestSessionChangedEvent.ChangeType.SELECT) {
					loadTestCollections();
				}
			}
		});

		actorTabBar.addSelectionHandler(new ActorSelectionHandler());
		optionsTabBar.addSelectionHandler(new OptionSelectionHandler());

		// Initial load of tests in a test session
		loadTestCollections();

		// Register the Diagram clicked event handler
		Xdstools2.getEventBus().addHandler(DiagramClickedEvent.TYPE, new DiagramPartClickedEventHandler() {
			@Override
			public void onClicked(TestInstance testInstance, InteractionDiagram.DiagramPart part) {
				if (InteractionDiagram.DiagramPart.RequestConnector.equals(part)
						|| InteractionDiagram.DiagramPart.ResponseConnector.equals(part)) {
					new LaunchInspectorClickHandler(testInstance, getCurrentTestSession(), new SiteSpec(testContext.getSiteName())).onClick(null);
//					launchInspectorTab(testInstance, getCurrentTestSession());
				}
			}
		});
	}

	public void removeTestDetails(TestInstance testInstance) {
		testOverviewDTOs.remove(testInstance.getId());
		updateTestsOverviewHeader();
	}

	private void addTestOverview(TestOverviewDTO dto) {
		testOverviewDTOs.put(dto.getName(), dto);
	}

	private void removeTestOverview(TestOverviewDTO dto) {
		testOverviewDTOs.remove(dto.getName());
	}

	/**
	 * currentActorTypeDescription is initialized late so calling this when it is available
	 * updates the display since it could not be constructed correctly at first.
	 * This must be called after testCollectionDefinitionDAOs is initialized.
	 */
	private void updateTestsOverviewHeader() {

		// Build statistics
		testStatistics.clear();
		if (testOverviewDTOs != null) {
			for (TestOverviewDTO testOverview : testOverviewDTOs.values()) {
				if (testOverview.isRun()) {
					if (testOverview.isPass()) {
						testStatistics.addSuccessful();
					} else {
						testStatistics.addWithError();
					}
				}
			}
		}

		// Display header with statistics
		testsHeaderView.update(testStatistics, currentActorTypeDescription + " - " + getCurrentOptionTitle());
	}

	private String getCurrentOptionTitle() {
		List<ActorAndOptions> aos =  actorOptions.get(currentActorOption.actorTypeId);
		if (aos != null) {
			for (ActorAndOptions ao : aos) {
				if (ao.getOptionId().equals(currentActorOption.optionId))
					return ao.getOptionTitle();
			}
		}
		return "Required";
	}

	@Override
	public String getTitle() { return "Conformance Tests"; }

    /**
     *
     */
	private void initializeTestingContext() {
		if (initTestSession != null) {
			setCurrentTestSession(initTestSession);
			initTestSession = null;
		}
		if (getCurrentTestSession() == null || getCurrentTestSession().equals("")) {
			testContextDisplay.updateTestingContextDisplay();
			return;
		}
		getToolkitServices().getAssignedSiteForTestSession(getCurrentTestSession(), new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable throwable) {
				new PopupMessage("getAssignedSiteForTestSession failed: " + throwable.getMessage());
			}

			@Override
			public void onSuccess(String s) {
				testContext.setSiteName(s);
				testContextDisplay.updateTestingContextDisplay();
			}
		});
	}

	String verifyTestContext() {
		String msg;
		msg = verifyEnvironmentSelection();
		if (msg != null) return msg;

		msg = verifyTestSession();
		if (msg != null) return msg;

		msg = verifySite();
		if (msg != null) return msg;

		return null;  // good
	}

	private String verifyEnvironmentSelection() {
		if (getEnvironmentSelection() != null) return null;
		return "Environment must be selected before you proceed.";
	}

	private String verifyTestSession() {
		if (getCurrentTestSession() != null) return null;
		return "Test Session must be selected before you proceed.";
	}

	private String verifySite() {
		if (testContext.getSiteName() != null) return null;
		return "System under test must be selected before you proceed.";
	}

	// actor type selection changes
	private class ActorSelectionHandler implements SelectionHandler<Integer> {

		@Override
		public void onSelection(SelectionEvent<Integer> selectionEvent) {
			int i = selectionEvent.getSelectedItem();
			String newActorTypeId = testCollectionDefinitionDAOs.get(i).getCollectionID();
			if (!newActorTypeId.equals(currentActorOption.actorTypeId)) {
				orchestrationResponse = null;  // so we know orchestration not set up
				currentActorOption = new ActorOption(newActorTypeId);
				optionsTabBar.display(newActorTypeId);
			}
		}
	}



	private class OptionSelectionHandler implements SelectionHandler<Integer> {

		@Override
		public void onSelection(SelectionEvent<Integer> selectionEvent) {
			int i = selectionEvent.getSelectedItem();
			List<ActorAndOptions> options = actorOptions.get(currentActorOption.actorTypeId);
			if (i < options.size()) {
				currentActorOption.setOptionId(options.get(i).getOptionId());
			}
 			changeDisplayedActorType(currentActorOption);
		}
	}

	public void changeDisplayedActorType(ActorOption actorOption) {
		currentActorOption = actorOption;
		currentActorTypeDescription = getDescriptionForTestCollection(currentActorOption.actorTypeId);
		displayTestCollection();
	}

	private String getDescriptionForTestCollection(String collectionId) {
		if (testCollectionDefinitionDAOs == null) return "not initialized";
		for (TestCollectionDefinitionDAO dao : testCollectionDefinitionDAOs) {
			if (dao.getCollectionID().equals(collectionId)) {
				return dao.getCollectionTitle();
			}
		}
		return "???";
	}

	// load tab bar with actor types
	private void loadTestCollections() {
		// TabBar listing actor types
		getToolkitServices().getTestCollections("actorcollections", new AsyncCallback<List<TestCollectionDefinitionDAO>>() {
			@Override
			public void onFailure(Throwable throwable) { new PopupMessage("getTestCollections: " + throwable.getMessage()); }

			@Override
			public void onSuccess(List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs) {
				me.testCollectionDefinitionDAOs = testCollectionDefinitionDAOs;
				displayTestCollectionsTabBar();
				currentActorTypeDescription = getDescriptionForTestCollection(currentActorOption.actorTypeId);
				updateTestsOverviewHeader();

				// This is a little wierd being here. This depends on initTestSession
				// which is set AFTER onTabLoad is run so run here - later in the initialization
				// initTestSession is set from ConfActorActivity
				initializeTestingContext();
			}
		});
	}



	private HTML loadingMessage;

	private class RefreshTestCollectionClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent clickEvent) {
			displayTestCollection();
		}
	}

	// load test results for a single test collection (actor type) for a single site
	private void displayTestCollection() {
		testDisplayGroup.clear();  // so they reload
		testsPanel.clear();

		loadingMessage = new HTML("Initializing...");
		loadingMessage.setStyleName("loadingMessage");
		testsPanel.add(loadingMessage);

		initializationPanel.clear();

		displayOrchestrationHeader();

		// what tests are in the collection
		currentActorOption.loadTests(new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable throwable) {
				new PopupMessage("getTestlogListing: " + throwable.getMessage());
			}

			@Override
			public void onSuccess(List<String> testIds) {
				List<TestInstance> testInstances = new ArrayList<>();
				for (String testId : testIds) testInstances.add(new TestInstance(testId));
				testStatistics.clear();
				testStatistics.setTestCount(testIds.size());
//				testsPerActor.put(currentActorTypeId, testInstances);
				loadingMessage.setHTML("Loading...");
				displayTests(testInstances);
			}
		});
	}

    private void displayTests(List<TestInstance> testInstances) {
        // results (including logs) for a collection of tests
        getToolkitServices().getTestsOverview(getCurrentTestSession(), testInstances, new AsyncCallback<List<TestOverviewDTO>>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("getTestOverview: " + caught.getMessage());
            }

            public void onSuccess(List<TestOverviewDTO> testOverviews) {

				// sort tests by dependencies and alphabetically
				// save in testsPerActor so they run in this order as well
				List<TestInstance> testInstances1 = new ArrayList<>();
				testOverviews = new TestSorter().sort(testOverviews);
				for (TestOverviewDTO dto : testOverviews) {
					testInstances1.add(dto.getTestInstance());
				}
				testsPerActor.put(currentActorOption.actorTypeId, testInstances1);

				testsPanel.clear();
                testsPanel.add(testsHeaderView.asWidget());
				testStatistics.clear();
                testStatistics.setTestCount(testOverviews.size());
                for (TestOverviewDTO testOverview : testOverviews) {
                    addTestOverview(testOverview);
//                    displayTest(testsPanel, testDisplayGroup, testOverview);
					TestDisplay testDisplay = testDisplayGroup.display(testOverview);
					testDisplay.display(testOverview);
					testsPanel.add(testDisplay);
                }
                updateTestsOverviewHeader();

                getToolkitServices().getAutoInitConformanceTesting(new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean)
                            orchInit.handleClick(null);   // auto init orchestration
                    }
                });

            }

        });
    }

    private AbstractOrchestrationButton orchInit = null;

	private void displayOrchestrationHeader() {
		String label = "Initialize Test Environment";
		if (currentActorOption.isRep()) {
			orchInit = new BuildRepTestOrchestrationButton(this, testContext, testContextDisplay, initializationPanel, label);
			initializationPanel.add(orchInit.panel());
		}
        else if (currentActorOption.isReg()) {
            orchInit = new BuildRegTestOrchestrationButton(this, testContext, testContextDisplay, initializationPanel, label);
            initializationPanel.add(orchInit.panel());
        }
        else if (currentActorOption.isRg()) {
            orchInit = new BuildRgTestOrchestrationButton(this, initializationPanel, label, testContext, testContextDisplay, this);
            initializationPanel.add(orchInit.panel());
        }
		else if (currentActorOption.isIg()) {
			orchInit = new BuildIGTestOrchestrationButton(this, initializationPanel, label, testContext, testContextDisplay, this, false);
			initializationPanel.add(orchInit.panel());
		}
        else {
            if (testContext.getSiteUnderTest() != null)
			    sitetoIssueTestAgainst = testContext.getSiteUnderTestAsSiteSpec();
		}

	}


	private void displayTestCollectionsTabBar() {
		if (actorTabBar.getTabCount() == 0) {
			for (TestCollectionDefinitionDAO def : testCollectionDefinitionDAOs) {
				actorTabBar.addTab(def.getCollectionTitle());
			}
		}
	}

	@Override
	public RunAllClickHandler getRunAllClickHandler() {
		return new RunAllClickHandler(currentActorOption.actorTypeId);
	}

	private class RunAllClickHandler implements ClickHandler, TestDone {
		String actorTypeId;
		List<TestInstance> tests = new ArrayList<TestInstance>();

		RunAllClickHandler(String actorTypeId ) {
			this.actorTypeId = actorTypeId;
		}

		@Override
		public void onClick(ClickEvent clickEvent) {
			clickEvent.preventDefault();
			clickEvent.stopPropagation();

			for (TestInstance testInstance : testsPerActor.get(actorTypeId))
				tests.add(testInstance);
			onDone(null);
		}

		@Override
		public void onDone(TestInstance unused) {
			testsHeaderView.showRunningMessage(true);
			if (tests.size() == 0) {
				testsHeaderView.showRunningMessage(false);
				return;
			}
			TestInstance next = tests.get(0);
			tests.remove(0);
			runTest(next, this);
		}
	}

	@Override
	public DeleteAllClickHandler getDeleteAllClickHandler() {
		return new DeleteAllClickHandler(currentActorOption.actorTypeId);
	}

	@Override
	public ClickHandler getRefreshTestCollectionClickHandler() {
		return new RefreshTestCollectionClickHandler();
	}

	private class DeleteAllClickHandler implements ClickHandler {
		String actorTypeId;

		DeleteAllClickHandler(String actorTypeId) {
			this.actorTypeId = actorTypeId;
		}

		@Override
		public void onClick(ClickEvent clickEvent) {
			clickEvent.preventDefault();
			clickEvent.stopPropagation();
			List<TestInstance> tests = testsPerActor.get(actorTypeId);
			for (TestInstance testInstance : tests) {
				getToolkitServices().deleteSingleTestResult(getCurrentTestSession(), testInstance, new AsyncCallback<TestOverviewDTO>() {
					@Override
					public void onFailure(Throwable throwable) {
						new PopupMessage(throwable.getMessage());
					}

					@Override
					public void onSuccess(TestOverviewDTO testOverviewDTO) {
						testDisplayGroup.display(testOverviewDTO);
//						displayTest(testsPanel, testDisplayGroup, testOverviewDTO);
						removeTestOverview(testOverviewDTO);
						updateTestsOverviewHeader();
					}
				});
			}
		}
	}

	ClickHandler getInspectClickHandler(TestInstance testInstance) {
		return new LaunchInspectorClickHandler(testInstance, getCurrentTestSession(), new SiteSpec(testContext.getSiteName()));
	}

	// if testInstance contains a sectionName then run that section, otherwise run entire test.
	public void runTest(final TestInstance testInstance, final TestDone testDone) {
		Map<String, String> parms = new HashMap<>();

		if (orchestrationResponse == null) {
			new PopupMessage("Initialize Test Environment before running tests.");
			return;
		}

        if (ActorType.REPOSITORY.getShortName().equals(currentActorOption.actorTypeId)) {
            parms.put("$patientid$", repOrchestrationResponse.getPid().asString());
        }

        if (getSitetoIssueTestAgainst() == null) {
            new PopupMessage("Test Setup must be initialized");
            return;
        }

		try {
			getToolkitServices().runTest(getEnvironmentSelection(), getCurrentTestSession(), getSitetoIssueTestAgainst(), testInstance, parms, true, new AsyncCallback<TestOverviewDTO>() {
				@Override
				public void onFailure(Throwable throwable) {
					new PopupMessage(throwable.getMessage());
				}

				@Override
				public void onSuccess(TestOverviewDTO testOverviewDTO) {
					// returned status of entire test
					testDisplayGroup.display(testOverviewDTO);
					addTestOverview(testOverviewDTO);
					updateTestsOverviewHeader();
					// Schedule next test to be run
					if (testDone != null)
						testDone.onDone(testInstance);
				}
			});
		} catch (Exception e) {
			new PopupMessage(e.getMessage());
		}

	}

	public void setRepOrchestrationResponse(RepOrchestrationResponse repOrchestrationResponse) {
		this.repOrchestrationResponse = repOrchestrationResponse;
		setOrchestrationResponse(repOrchestrationResponse);
	}

	public void setOrchestrationResponse(AbstractOrchestrationResponse repOrchestrationResponse) {
        this.orchestrationResponse = repOrchestrationResponse;
	}

    public String getWindowShortName() {
		return "testloglisting";
	}

	private SiteSpec getSitetoIssueTestAgainst() {
		return sitetoIssueTestAgainst;
	}

	public void setSitetoIssueTestAgainst(SiteSpec sitetoIssueTestAgainst) {
		this.sitetoIssueTestAgainst = sitetoIssueTestAgainst;
	}

	public void setInitTestSession(String initTestSession) {
		this.initTestSession = initTestSession;
	}
}
