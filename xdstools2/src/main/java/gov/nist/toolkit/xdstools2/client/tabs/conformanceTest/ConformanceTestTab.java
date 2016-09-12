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
import gov.nist.toolkit.interactiondiagram.client.events.DiagramClickedEvent;
import gov.nist.toolkit.interactiondiagram.client.events.DiagramPartClickedEventHandler;
import gov.nist.toolkit.interactiondiagram.client.widgets.InteractionDiagram;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConformanceTestTab extends ToolWindow implements TestRunner, SiteManager {

	private final ConformanceTestTab me;
	private final FlowPanel toolPanel = new FlowPanel();   // Outer-most panel for the tool
	private final FlowPanel initializationPanel = new FlowPanel();
	private final FlowPanel testsPanel = new FlowPanel();  // panel for displaying tests
	private final TabBar tabBar = new TabBar();            // tab bar at the top for selecting actor types
	private final FlowPanel sitesPanel = new FlowPanel();
	private String currentSiteName = null;
	private HTML testSessionDescription = new HTML();
	private FlowPanel testSessionDescriptionPanel = new FlowPanel();
	private RepOrchestrationResponse repOrchestrationResponse;
	private String currentActorTypeName;
	private Site siteUnderTest = null;
	private SiteSpec sitetoIssueTestAgainst = null;

	// Testable actors
	private List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs;
	// testId ==> overview
//	private final Map<String, TestOverviewDTO> testOverviews = new HashMap<>();

	public ConformanceTestTab() {
		me = this;
		toolPanel.add(sitesPanel);
		toolPanel.add(tabBar);
		toolPanel.add(initializationPanel);
		toolPanel.add(testsPanel);
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
		updateTestSessionDisplay();
		testSessionDescription.addClickHandler(new TestSessionClickHandler());
		testSessionDescriptionPanel.setStyleName("with-rounded-border");
		testSessionDescriptionPanel.add(testSessionDescription);

		addEast(testSessionDescriptionPanel);
		initializeTestSession();
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

		// List of sites
//		buildSiteSelector();

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
		if (getCurrentTestSession() == null || getCurrentTestSession().equals("")) {
			updateTestSessionDisplay();
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
				updateTestSessionDisplay();
			}
		});
	}

	private void updateTestSessionDisplay() {
		testSessionDescription.setHTML("Test Session<br />" +
				"Name: " + getCurrentTestSession() + "<br />" +
				"Environment: " + getEnvironmentSelection() + "<br />" +
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
		return "Site under test must be selected before you proceed.";
	}

	@Override
	public String getSiteName() {
		return currentSiteName;
	}

	@Override
	public void setSiteName(String site) {
		currentSiteName = site;
		getToolkitServices().getSite(site, new AsyncCallback<Site>() {
			@Override
			public void onFailure(Throwable throwable) {
//				new PopupMessage("getSiteName threw error: " + throwable.getMessage());
			}

			@Override
			public void onSuccess(Site site) {
				siteUnderTest = site;
			}
		});
	}

	@Override
	public void update() {
		updateTestSessionDisplay();
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
		int top = Window.getClientHeight()/ 6;
		dialog.setPopupPosition(left, top);
		dialog.show();
	}

	// actor selection changes
	private SelectionHandler<Integer> actorSelectionHandler = new SelectionHandler<Integer>() {
		@Override
		public void onSelection(SelectionEvent<Integer> selectionEvent) {
			int i = selectionEvent.getSelectedItem();
			currentActorTypeName = testCollectionDefinitionDAOs.get(i).getCollectionID();
			loadTestCollection();
		}
	};

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
			}
		});
	}

	private boolean isRepSut() {
		return currentActorTypeName != null && ActorType.REPOSITORY.getShortName().equals(currentActorTypeName);
	}

	// load test results for a single test collection (actor type) for a single site
	private void loadTestCollection() {
		testDisplays.clear();  // so they reload
		testsPanel.clear();

		initializationPanel.clear();

		if (isRepSut()) {
			initializationPanel.add(new BuildRepTestOrchestrationButton(this, initializationPanel, "Initialize Test Environment").panel());
		} else {
			sitetoIssueTestAgainst = new SiteSpec(siteUnderTest.getName());
		}

		// what tests are in the collection
		getToolkitServices().getCollectionMembers("actorcollections", currentActorTypeName, new AsyncCallback<List<String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getTestlogListing: " + caught.getMessage());
			}

			public void onSuccess(List<String> testIds) {
				List<TestInstance> testInstances = new ArrayList<>();
				for (String testId : testIds) testInstances.add(new TestInstance(testId));

				// results (including logs) for a collection of tests
				getToolkitServices().getTestsOverview(getCurrentTestSession(), testInstances, new AsyncCallback<List<TestOverviewDTO>>() {

					public void onFailure(Throwable caught) {
						new PopupMessage("getTestOverview: " + caught.getMessage());
					}

					public void onSuccess(List<TestOverviewDTO> testOverviews) {
						testsPanel.add(new HTML("<h2>Tests</h2>"));
						for (TestOverviewDTO testOverview : testOverviews) {
//							me.testOverviews.put(testOverview.getName(), testOverview);
							displayTest(testOverview);
						}
					}

				});
			}
		});

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
	private class TestDisplay {
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

			runTest(testInstance);
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
				}
			});
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
	public void runTest(final TestInstance testInstance) {
		Map<String, String> parms = new HashMap<>();
		if (repOrchestrationResponse != null) {
			parms.put("$patientid$", repOrchestrationResponse.getPid().asString());
		} else {
			parms.put("$patientid$", "P20160907182617.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO");
		}

		try {
			// Site is support site since it has the supporting Registry sim and as part of orchestration we added
			// the Repository Pnr and Ret transactions
			// was currentSiteName
			getToolkitServices().runTest(getEnvironmentSelection(), getCurrentTestSession(), sitetoIssueTestAgainst, testInstance, parms, true, new AsyncCallback<TestOverviewDTO>() {
				@Override
				public void onFailure(Throwable throwable) {
					new PopupMessage(throwable.getMessage());
				}

				@Override
				public void onSuccess(TestOverviewDTO testOverviewDTO) {
					// returned status of entire test
					displayTest(testOverviewDTO);

				}
			});
		} catch (Exception e) {
			new PopupMessage(e.getMessage());
		}

	}

	public RepOrchestrationResponse getRepOrchestrationResponse() {
		return repOrchestrationResponse;
	}

	public void setRepOrchestrationResponse(RepOrchestrationResponse repOrchestrationResponse) {
		this.repOrchestrationResponse = repOrchestrationResponse;
	}



	public String getWindowShortName() {
		return "testloglisting";
	}

	public SiteSpec getSitetoIssueTestAgainst() {
		return sitetoIssueTestAgainst;
	}

	public void setSitetoIssueTestAgainst(SiteSpec sitetoIssueTestAgainst) {
		this.sitetoIssueTestAgainst = sitetoIssueTestAgainst;
	}
}
