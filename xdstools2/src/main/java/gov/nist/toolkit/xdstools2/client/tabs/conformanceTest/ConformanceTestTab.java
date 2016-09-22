package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.interactiondiagram.client.events.DiagramClickedEvent;
import gov.nist.toolkit.interactiondiagram.client.events.DiagramPartClickedEventHandler;
import gov.nist.toolkit.interactiondiagram.client.widgets.InteractionDiagram;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.services.client.AbstractOrchestrationResponse;
import gov.nist.toolkit.services.client.RegOrchestrationResponse;
import gov.nist.toolkit.services.client.RepOrchestrationResponse;
import gov.nist.toolkit.session.client.SectionOverviewDTO;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.event.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.OrchestrationButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConformanceTestTab extends ToolWindow implements TestRunner, SiteManager, TestsHeaderView.Controller {

	private final ConformanceTestTab me;
	private final FlowPanel toolPanel = new FlowPanel();   // Outer-most panel for the tool
	private final FlowPanel initializationPanel = new FlowPanel();
	private final FlowPanel testsPanel = new FlowPanel();  // panel for displaying tests
	private final TabBar tabBar = new TabBar();            // tab bar at the top for selecting actor types
	private final FlowPanel sitesPanel = new FlowPanel();
	private String currentSiteName = null;
	private HTML testSessionDescription = new HTML();
	private FlowPanel testSessionDescriptionPanel = new FlowPanel();

	private AbstractOrchestrationResponse orchestrationResponse;  // can be any of the following - contains common elements
	private RepOrchestrationResponse repOrchestrationResponse;
    private RegOrchestrationResponse regOrchestrationResponse;

    private String currentActorTypeId;
	private String currentActorTypeDescription;
	private Site siteUnderTest = null;
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
		toolPanel.add(sitesPanel);
		toolPanel.add(tabBar);
		toolPanel.add(initializationPanel);
		toolPanel.add(testsPanel);
	}

	private void addTestOverview(TestOverviewDTO dto) {
		testOverviewDTOs.put(dto.getName(), dto);
	}

	private void removeTestOverview(TestOverviewDTO dto) {
		testOverviewDTOs.remove(dto.getName());
	}


	private TestsHeaderView testsHeaderView = new TestsHeaderView(this);
	private final TestStatistics testStatistics = new TestStatistics();


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
	protected Widget buildUI() {
		return null;
	}

	@Override
	protected void bindUI() {

	}

	@Override
	public void onTabLoad(boolean select, String eventName) {
		displayTestSessionDisplay();
		testSessionDescription.addClickHandler(new TestSessionClickHandler());
		testSessionDescriptionPanel.setStyleName("with-rounded-border");
		testSessionDescriptionPanel.add(testSessionDescription);

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

		tabBar.addSelectionHandler(actorSelectionHandler);

		// Initial load of tests in a test session
		loadTestCollections();

		// Register the Diagram clicked event handler
		Xdstools2.getEventBus().addHandler(DiagramClickedEvent.TYPE, new DiagramPartClickedEventHandler() {
			@Override
			public void onClicked(TestInstance testInstance, InteractionDiagram.DiagramPart part) {
				if (InteractionDiagram.DiagramPart.RequestConnector.equals(part)) {
					List<TestInstance> testInstances = new ArrayList<>();
					testInstances.add(testInstance);
					displayInspectorTab(testInstances);
				}
			}
		});
	}

	@Override
	public Site getSiteUnderTest() {
		return siteUnderTest;
	}

	private void initializeTestSession() {
		if (initTestSession != null) {
			setCurrentTestSession(initTestSession);
			initTestSession = null;
		}
		if (getCurrentTestSession() == null || getCurrentTestSession().equals("")) {
			displayTestSessionDisplay();
			return;
		}
		getToolkitServices().getAssignedSiteForTestSession(getCurrentTestSession(), new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable throwable) {
				new PopupMessage("getAssignedSiteForTestSession failed: " + throwable.getMessage());
			}

			@Override
			public void onSuccess(String s) {
				setSiteName(s);
				displayTestSessionDisplay();
			}
		});
	}

	private void displayTestSessionDisplay() {
		testSessionDescription.setHTML("Test Context<br />" +
				"Environment: " + getEnvironmentSelection() + "<br />" +
				"TestSesson: " + getCurrentTestSession() + "<br />" +
				"SUT: " + getSiteName());
	}

	String verifyConformanceTestEnvironment() {
		String msg;
		msg = verifyEnvironmentSelection();
		if (msg != null) return msg;

		msg = verifyTestSession();
		if (msg != null) return msg;

		msg = verifySite();
		if (msg != null) return msg;

		return null;
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
		if (getSiteName() != null) return null;
		return "System under test must be selected before you proceed.";
	}

	@Override
	public String getSiteName() {
		return currentSiteName;
	}

	@Override
	public void setSiteName(String site) {
		currentSiteName = site;
//		getToolkitServices().getSite(site, new AsyncCallback<Site>() {
		if (site == null) return;
		getToolkitServices().getSite(site, new AsyncCallback<Site>() {
			@Override
			public void onFailure(Throwable throwable) {
				new PopupMessage("getSiteName threw error: " + throwable.getMessage());
			}

			@Override
			public void onSuccess(Site site) {
				siteUnderTest = site;
			}
		});
	}

	@Override
	public void update() {
		displayTestSessionDisplay();
	}

	private class TestSessionClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent clickEvent) {
			launchTestEnvironmentDialog(null);
		}
	}

	void launchTestEnvironmentDialog(String msg) {
		TestEnvironmentDialog dialog = new TestEnvironmentDialog(me, me, msg);
		int left = Window.getClientWidth()/ 3;
		int top = Window.getClientHeight()/ 20;
		dialog.setPopupPosition(left, top);
		dialog.show();
	}

	// actor selection changes
	private SelectionHandler<Integer> actorSelectionHandler = new SelectionHandler<Integer>() {
		@Override
		public void onSelection(SelectionEvent<Integer> selectionEvent) {
			int i = selectionEvent.getSelectedItem();
			currentActorTypeId = testCollectionDefinitionDAOs.get(i).getCollectionID();
			currentActorTypeDescription = testCollectionDefinitionDAOs.get(i).getCollectionTitle();
			displayTestCollection();
		}
	};

	public void displayActor(String actorTypeName) {
		currentActorTypeId = actorTypeName;
		currentActorTypeDescription = getDescriptionForTestCollection(currentActorTypeId);
		displayTestCollection();
	}

	public void showActorTypeSelection() {
		for (int i=0; i<tabBar.getTabCount(); i++) {
			if (currentActorTypeDescription.equals(tabBar.getTitle())) {
				tabBar.selectTab(i);
				return;
			}
		}
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

	// selection of site to be tested
	private void buildSiteSelector() {
		SiteSelectionComponent siteSelectionComponent = new SiteSelectionComponent(null, getCurrentTestSession());
		siteSelectionComponent.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> siteSelected) {
				currentSiteName = siteSelected.getValue();
			}
		});
		sitesPanel.add(siteSelectionComponent.asWidget());
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
				initializeTestSession();
			}
		});
	}

	private boolean isRepSut() {
		return currentActorTypeId != null && ActorType.REPOSITORY.getShortName().equals(currentActorTypeId);
	}

    private boolean isRegSut() {
        return currentActorTypeId != null && ActorType.REGISTRY.getShortName().equals(currentActorTypeId);
    }

	private HTML loadingMessage;

	// load test results for a single test collection (actor type) for a single site
	private void displayTestCollection() {
		testDisplays.clear();  // so they reload
		testsPanel.clear();

		loadingMessage = new HTML("Initializing...");
		loadingMessage.setStyleName("loadingMessage");
		testsPanel.add(loadingMessage);

		initializationPanel.clear();

		orchestrationInitialization();

		// what tests are in the collection
		getToolkitServices().getCollectionMembers("actorcollections", currentActorTypeId, new AsyncCallback<List<String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getTestlogListing: " + caught.getMessage());
			}

			public void onSuccess(List<String> testIds) {
				List<TestInstance> testInstances = new ArrayList<>();
				for (String testId : testIds) testInstances.add(new TestInstance(testId));
				testsPerActor.put(currentActorTypeId, testInstances);
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
				testsPanel.clear();
                testsPanel.add(testsHeaderView.asWidget());
                testStatistics.setTestCount(testOverviews.size());
                for (TestOverviewDTO testOverview : testOverviews) {
                    addTestOverview(testOverview);
                    displayTest(testOverview);
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

    private void displayIsolatedTests(List<TestInstance> testInstances) {
        // results (including logs) for a collection of tests
        getToolkitServices().getTestsOverview(getCurrentTestSession(), testInstances, new AsyncCallback<List<TestOverviewDTO>>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("getTestOverview: " + caught.getMessage());
            }

            public void onSuccess(List<TestOverviewDTO> testOverviews) {
                testsPanel.add(testsHeaderView.asWidget());
                for (TestOverviewDTO testOverview : testOverviews) {
                    addTestOverview(testOverview);
                    displayTest(testOverview);
                }
            }

        });
    }



    private OrchestrationButton orchInit = null;

	private void orchestrationInitialization() {
		if (isRepSut()) {
			orchInit = new BuildRepTestOrchestrationButton(this, initializationPanel, "Initialize Test Environment");
			initializationPanel.add(orchInit.panel());
		}
        else if (isRegSut()) {
            orchInit = new BuildRegTestOrchestrationButton(this, initializationPanel, "Initialize Test Environment");
            initializationPanel.add(orchInit.panel());
        }
        else {
			sitetoIssueTestAgainst = new SiteSpec(siteUnderTest.getName());
		}

	}

	private void displayTestCollectionsTabBar() {
		if (tabBar.getTabCount() == 0) {
			for (TestCollectionDefinitionDAO def : testCollectionDefinitionDAOs) {
				tabBar.addTab(def.getCollectionTitle());
			}
		}
	}

	// header and body per test
	// key is testOverview.getName()
	private Map<String, TestDisplay> testDisplays = new HashMap<>();
	class TestDisplay {
		TestDisplayHeader header = new TestDisplayHeader();
		FlowPanel body = new FlowPanel();
		DisclosurePanel panel = new DisclosurePanel(header);

		TestDisplay(String name) {
			header.fullWidth();
			panel.setWidth("100%");
			panel.add(body);
			testDisplays.put(name, this);
		}
	}

	private boolean testDisplayExists(String name) { return testDisplays.containsKey(name); }

	private TestDisplay buildTestDisplay(String name) {
		if (testDisplayExists(name)) return testDisplays.get(name);
		return new TestDisplay(name);
	}

	private void displayTest(TestOverviewDTO testOverview) {
		boolean isNew = !testDisplayExists(testOverview.getName());
		TestDisplay testDisplay = buildTestDisplay(testOverview.getName());
		TestDisplayHeader header = testDisplay.header;
		FlowPanel body = testDisplay.body;

		if (isNew) {
			testsPanel.add(testDisplay.panel);
		}

		header.clear();
		body.clear();

		if (testOverview.isRun()) {
			if (testOverview.isPass()) header.setBackgroundColorSuccess();
			else header.setBackgroundColorFailure();
		} else header.setBackgroundColorNotRun();

		HTML testHeader = new HTML("Test: " + testOverview.getName() + " - " +testOverview.getTitle());
		testHeader.addStyleName("test-title");
		header.add(testHeader);
		header.add(new HTML(testOverview.getLatestSectionTime()));
		if (testOverview.isRun()) {
			Image status = (testOverview.isPass()) ?
					new Image("icons2/correct-24.png")
					:
					new Image("icons/ic_warning_black_24dp_1x.png");
			status.addStyleName("right");
			header.add(status);
		}

		Image play = new Image("icons2/play-24.png");
		play.setTitle("Run");
		play.addClickHandler(new RunClickHandler(testOverview.getTestInstance()));
		header.add(play);
		if (testOverview.isRun()) {
			Image delete = new Image("icons2/garbage-24.png");
			delete.addStyleName("right");
			delete.addClickHandler(new DeleteClickHandler(testOverview.getTestInstance()));
			delete.setTitle("Delete Log");
			header.add(delete);

			Image inspect = new Image("icons2/visible-32.png");
			inspect.addStyleName("right");
			inspect.addClickHandler(new InspectClickHandler(testOverview.getTestInstance()));
			inspect.setTitle("Inspect results");
			header.add(inspect);
		}

		body.add(new HTML("<p><b>Description:</b></p>"));
		body.add(new HTML(testOverview.getDescription()));

		// display an interaction sequence diagram
		if (testOverview.isRun()) {
			displayInteractionDiagram(testOverview, body);
		}

		// display sections within test
		body.add(new HTML("<p><b>Sections:</b></p>"));
		displaySections(testOverview, body);

		if (!isNew)
			updateTestsOverviewHeader();

	}

	private void displayInteractionDiagram(TestOverviewDTO testResultDTO, FlowPanel body) {
		InteractionDiagram diagram = new InteractionDiagram(Xdstools2.getEventBus(), testResultDTO);
		if (diagram!=null && diagram.hasMeaningfulDiagram()) {
			body.add(new HTML("<p><b>Interaction Sequence:</b></p>"));
			body.add(diagram);
		}
	}

	private class RunClickHandler implements ClickHandler {
		TestInstance testInstance;

		RunClickHandler(TestInstance testInstance) {
			this.testInstance = testInstance;
		}

		@Override
		public void onClick(ClickEvent clickEvent) {
			clickEvent.preventDefault();
			clickEvent.stopPropagation();

			runTest(testInstance, null);
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
		public void onDone(TestInstance testInstance) {
			if (tests.size() == 0) return;
			TestInstance next = tests.get(0);
			tests.remove(0);
			runTest(next, this);
		}
	}

	private class DeleteClickHandler implements ClickHandler {
		TestInstance testInstance;

		DeleteClickHandler(TestInstance testInstance) {
			this.testInstance = testInstance;
		}

		@Override
		public void onClick(ClickEvent clickEvent) {
			clickEvent.preventDefault();
			clickEvent.stopPropagation();
			getToolkitServices().deleteSingleTestResult(getCurrentTestSession(), testInstance, new AsyncCallback<TestOverviewDTO>() {
				@Override
				public void onFailure(Throwable throwable) {
					new PopupMessage(throwable.getMessage());
				}

				@Override
				public void onSuccess(TestOverviewDTO testOverviewDTO) {
					displayTest(testOverviewDTO);
					removeTestOverview(testOverviewDTO);
					updateTestsOverviewHeader();
				}
			});
		}
	}

	@Override
	public DeleteAllClickHandler getDeleteAllClickHandler() {
		return new DeleteAllClickHandler(currentActorTypeId);
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
						displayTest(testOverviewDTO);
						removeTestOverview(testOverviewDTO);
						updateTestsOverviewHeader();
					}
				});
			}
		}
	}

	private class InspectClickHandler implements ClickHandler {
		TestInstance testInstance;

		InspectClickHandler(TestInstance testInstance) {
			this.testInstance = testInstance;
		}

		@Override
		public void onClick(ClickEvent clickEvent) {
			clickEvent.preventDefault();
			clickEvent.stopPropagation();

			List<TestInstance> testInstances = new ArrayList<>();
			testInstances.add(testInstance);
			displayInspectorTab(testInstances);
		}
	}

	private void displayInspectorTab(List<TestInstance> testInstances) {
		getToolkitServices().getTestResults(testInstances, getCurrentTestSession(), new AsyncCallback<Map<String, Result>>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage(throwable.getMessage());
            }

			@Override
			public void onSuccess(Map<String, Result> resultMap) {
				MetadataInspectorTab itab = new MetadataInspectorTab();
				itab.setResults(resultMap.values());
				itab.setSiteSpec(new SiteSpec(currentSiteName));
//					itab.setToolkitService(me.toolkitService);
				itab.onTabLoad(true, "Insp");
			}
		});
	}

	// display sections within test
	private void displaySections(TestOverviewDTO testOverview, FlowPanel parent) {
//		parent.clear();
		for (String sectionName : testOverview.getSectionNames()) {
			SectionOverviewDTO sectionOverview = testOverview.getSectionOverview(sectionName);
			parent.add(new TestSectionComponent(getCurrentTestSession(), new TestInstance(testOverview.getName()), sectionOverview, this).asWidget());
		}
	}

	// if testInstance contains a sectionName then run that section, otherwise run entire test.
	public void runTest(final TestInstance testInstance, final TestDone testDone) {
		Map<String, String> parms = new HashMap<>();

        if (ActorType.REPOSITORY.getShortName().equals(currentActorTypeId) && repOrchestrationResponse != null) {
            parms.put("$patientid$", repOrchestrationResponse.getPid().asString());
        }
        else if (ActorType.REGISTRY.getShortName().equals(currentActorTypeId) && regOrchestrationResponse != null) {
            parms.put("$patientid$", regOrchestrationResponse.getPid().asString());
        }
		else { // mostly for early debugging
            parms.put("$patientid$", "P20160907182617.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO");
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
					displayTest(testOverviewDTO);
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

	public RepOrchestrationResponse getRepOrchestrationResponse() {
		return repOrchestrationResponse;
	}

    //
    // These are managed as a group.  Each must set orchestrationResponse
    //
	void setRepOrchestrationResponse(RepOrchestrationResponse repOrchestrationResponse) {
		this.repOrchestrationResponse = repOrchestrationResponse;
        this.orchestrationResponse = repOrchestrationResponse;
	}

    void setRegOrchestrationResponse(RegOrchestrationResponse regOrchestrationResponse) {
        this.regOrchestrationResponse = regOrchestrationResponse;
        this.orchestrationResponse = regOrchestrationResponse;
    }

    //
    // End of group
    //

    public String getWindowShortName() {
		return "testloglisting";
	}

	private SiteSpec getSitetoIssueTestAgainst() {
		return sitetoIssueTestAgainst;
	}

	void setSitetoIssueTestAgainst(SiteSpec sitetoIssueTestAgainst) {
		this.sitetoIssueTestAgainst = sitetoIssueTestAgainst;
	}

	public void setInitTestSession(String initTestSession) {
		this.initTestSession = initTestSession;
	}
}
