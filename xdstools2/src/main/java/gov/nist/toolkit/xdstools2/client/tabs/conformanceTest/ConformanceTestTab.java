package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.session.client.sort.TestSorter;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.xdstools2.client.command.command.AutoInitConformanceTestingCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestsOverviewCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs.BuildIGTestOrchestrationButton;
import gov.nist.toolkit.xdstools2.client.widgets.LaunchInspectorClickHandler;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestsOverviewRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All Conformance tests will be run out of here
 */
public class ConformanceTestTab extends ToolWindow implements TestRunner, TestsHeaderView.Controller {

	private final ConformanceTestTab me;
	private final FlowPanel toolPanel = new FlowPanel();   // Outer-most panel for the tool
	private final FlowPanel initializationPanel = new FlowPanel();
	private final FlowPanel testsPanel = new FlowPanel();  // panel for displaying tests
	private final TabBar tabBar = new TabBar();            // tab bar at the top for selecting actor types
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
    private String currentActorTypeId;
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
		toolPanel.add(tabBar);
		toolPanel.add(initializationPanel);
		toolPanel.add(testsPanel);
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
		testsHeaderView.update(testStatistics, currentActorTypeDescription);
	}

	@Override
	public String getTitle() { return "Conformance Tests"; }

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
				if (event.getChangeType() == TestSessionChangedEvent.ChangeType.SELECT) {
					loadTestCollections();
				}
			}
		});

		tabBar.addSelectionHandler(actorSelectionHandler);

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

	// actor selection changes
	private SelectionHandler<Integer> actorSelectionHandler = new SelectionHandler<Integer>() {
		@Override
		public void onSelection(SelectionEvent<Integer> selectionEvent) {
			int i = selectionEvent.getSelectedItem();
            String newActorTypeId = testCollectionDefinitionDAOs.get(i).getCollectionID();
            if (!newActorTypeId.equals(currentActorTypeId)) {
                orchestrationResponse = null;  // so we know orchestration not set up
                changeDisplayedActorType(newActorTypeId);
            }
		}
	};

	public void changeDisplayedActorType(String actorTypeName) {
		currentActorTypeId = actorTypeName;
		currentActorTypeDescription = getDescriptionForTestCollection(currentActorTypeId);
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

	// update things when site selection changes
	private ValueChangeHandler siteChangeHandler = new ValueChangeHandler() {
		@Override
		public void onValueChange(ValueChangeEvent valueChangeEvent) {

		}
	};

	private String getSelectedTestCollection() {
		int selected = tabBar.getSelectedTab();
		if (selected < testCollectionDefinitionDAOs.size()) {
			return testCollectionDefinitionDAOs.get(selected).getCollectionID();
		}
		return null;
	}

	private int getTestCollectionIndex(String name) {
		int i = 0;
		for (TestCollectionDefinitionDAO def : testCollectionDefinitionDAOs) {
			if (def.getCollectionID().equals(name))
				return i;
			i++;
		}
		return -1;
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
				currentActorTypeDescription = getDescriptionForTestCollection(currentActorTypeId);
				updateTestsOverviewHeader();

				// This is a little wierd being here. This depends on initTestSession
				// which is set AFTER onTabLoad is run so run here - later in the initialization
				// initTestSession is set from ConfActorActivity
				initializeTestingContext();
			}
		});
	}

	private boolean isRepSut() {
		return currentActorTypeId != null && ActorType.REPOSITORY.getShortName().equals(currentActorTypeId);
	}

    private boolean isRgSut() {
        return currentActorTypeId != null && ActorType.RESPONDING_GATEWAY.getShortName().equals(currentActorTypeId);
    }

	private boolean isIgSut() {
		return currentActorTypeId != null && ActorType.INITIATING_GATEWAY.getShortName().equals(currentActorTypeId);
	}

	private boolean isRegSut() {
        return currentActorTypeId != null && ActorType.REGISTRY.getShortName().equals(currentActorTypeId);
    }

	private HTML loadingMessage;

	class RefreshTestCollectionClickHandler implements ClickHandler {

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
		getToolkitServices().getCollectionMembers("actorcollections", currentActorTypeId, new AsyncCallback<List<String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getTestlogListing: " + caught.getMessage());
			}

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
        new GetTestsOverviewCommand(){
            @Override
            public void onComplete(List<TestOverviewDTO> testOverviews) {
                // sort tests by dependencies and alphabetically
                // save in testsPerActor so they run in this order as well
                List<TestInstance> testInstances1 = new ArrayList<>();
                testOverviews = new TestSorter().sort(testOverviews);
                for (TestOverviewDTO dto : testOverviews) {
                    testInstances1.add(dto.getTestInstance());
                }
                testsPerActor.put(currentActorTypeId, testInstances1);

                testsPanel.clear();
                testsPanel.add(testsHeaderView.asWidget());
                testStatistics.clear();
                testStatistics.setTestCount(testOverviews.size());
                for (TestOverviewDTO testOverview : testOverviews) {
                    addTestOverview(testOverview);
                    TestDisplay testDisplay = testDisplayGroup.display(testOverview);
                    testDisplay.display(testOverview);
                    testsPanel.add(testDisplay);
                }
                updateTestsOverviewHeader();
                new AutoInitConformanceTestingCommand(){

                    @Override
                    public void onComplete(Boolean result) {
                        if (result)
                            orchInit.handleClick(null);   // auto init orchestration
                    }
                }.run(getCommandContext());
            }
        }.run(new GetTestsOverviewRequest(ClientUtils.INSTANCE.getCommandContext(),testInstances));
    }

    private void displayIsolatedTests(List<TestInstance> testInstances) {
        // results (including logs) for a collection of tests
        new GetTestsOverviewCommand(){
            @Override
            public void onComplete(List<TestOverviewDTO> testOverviews) {
                testsPanel.add(testsHeaderView.asWidget());
                for (TestOverviewDTO testOverview : testOverviews) {
                    addTestOverview(testOverview);
                    testDisplayGroup.display(testOverview);
                }
            }
        }.run(new GetTestsOverviewRequest(getCommandContext(),testInstances));
    }

    private AbstractOrchestrationButton orchInit = null;

	private void displayOrchestrationHeader() {
		String label = "Initialize Test Environment";
		if (isRepSut()) {
			orchInit = new BuildRepTestOrchestrationButton(this, testContext, testContextDisplay, initializationPanel, label);
			initializationPanel.add(orchInit.panel());
		}
        else if (isRegSut()) {
            orchInit = new BuildRegTestOrchestrationButton(this, testContext, testContextDisplay, initializationPanel, label);
            initializationPanel.add(orchInit.panel());
        }
        else if (isRgSut()) {
            orchInit = new BuildRgTestOrchestrationButton(this, initializationPanel, label, testContext, testContextDisplay, this);
            initializationPanel.add(orchInit.panel());
        }
		else if (isIgSut()) {
			orchInit = new BuildIGTestOrchestrationButton(this, initializationPanel, label, testContext, testContextDisplay, this, false);
			initializationPanel.add(orchInit.panel());
		}
        else {
            if (testContext.getSiteUnderTest() != null)
			    sitetoIssueTestAgainst = testContext.getSiteUnderTestAsSiteSpec();
		}

	}

	private void displayTestCollectionsTabBar() {
		if (tabBar.getTabCount() == 0) {
			for (TestCollectionDefinitionDAO def : testCollectionDefinitionDAOs) {
				tabBar.addTab(def.getCollectionTitle());
			}
		}
	}

	@Override
	public RunAllClickHandler getRunAllClickHandler() {
		return new RunAllClickHandler(currentActorTypeId);
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
		return new DeleteAllClickHandler(currentActorTypeId);
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

        if (ActorType.REPOSITORY.getShortName().equals(currentActorTypeId)) {
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
