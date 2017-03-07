package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabBar;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.interactiondiagram.client.events.DiagramClickedEvent;
import gov.nist.toolkit.interactiondiagram.client.events.DiagramPartClickedEventHandler;
import gov.nist.toolkit.interactiondiagram.client.widgets.InteractionDiagram;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.services.client.AbstractOrchestrationResponse;
import gov.nist.toolkit.services.client.RecOrchestrationResponse;
import gov.nist.toolkit.services.client.RegOrchestrationResponse;
import gov.nist.toolkit.services.client.RepOrchestrationResponse;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.session.client.sort.TestSorter;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.command.command.AutoInitConformanceTestingCommand;
import gov.nist.toolkit.xdstools2.client.command.command.DeleteSingleTestCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetAssignedSiteForTestSessionCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetSiteCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetStsSamlAssertionCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestCollectionsCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestsOverviewCommand;
import gov.nist.toolkit.xdstools2.client.command.command.RunTestCommand;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs.BuildIGTestOrchestrationButton;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.LaunchInspectorClickHandler;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.DeleteSingleTestRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetCollectionRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetStsSamlAssertionRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestsOverviewRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.RunTestRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All Conformance tests will be run out of here
 */
public class ConformanceTestTab extends ToolWindow implements TestRunner, TestTarget, TestsHeaderView.Controller {

	private final ConformanceTestTab me;

	private TestsHeaderView testsHeaderView = new TestsHeaderView(this);
	private TestContextView testContextView;

	private TestDisplayGroup testDisplayGroup;
	private TestContext testContext = new TestContext(this, new SiteSelectionValidatorImpl());

	private AbstractOrchestrationResponse orchestrationResponse;
	private RepOrchestrationResponse repOrchestrationResponse;
	private RecOrchestrationResponse recOrchestrationResponse;
	private RegOrchestrationResponse regOrchestrationResponse;

	private final TestStatistics testStatistics = new TestStatistics();

	private ActorOption currentActorOption = new ActorOption("none");
	private String currentActorTypeDescription;
	private SiteSpec siteToIssueTestAgainst = null;

	// stuff that needs delayed setting when launched via activity
	private String initTestSession = null;

	// Descriptions of current test list
	private List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs;

	// Test results
	// test ==> results
	// this contains all tests independent of whether they have been run.
	private Map<TestInstance, TestOverviewDTO> testOverviewDTOs = new HashMap<>();

	// for each actor type id, the list of tests for it
	private Map<ActorOption, List<TestInstance>> testsPerActorOption = new HashMap<>();

	private ConformanceTestMainView mainView;
	private AbstractOrchestrationButton orchInit = null;

	public ConformanceTestTab() {
		super(10.0, 0.0);
		me = this;
		mainView = new ConformanceTestMainView(this, new OptionsTabBar());
		testContextView = new TestContextView(this, mainView.getTestSessionDescription(), testContext, new SiteSelectionValidatorImpl());
		testContext.setTestContextView(testContextView);
		testDisplayGroup = new TestDisplayGroup(testContext, testContextView, this);
	}

	@Override
	public void onTabLoad(final boolean select, String eventName) {
		testContextView.updateTestingContextDisplay();
		mainView.getTestSessionDescription().addClickHandler(testContextView);

		addEast(mainView.getTestSessionDescriptionPanel());
		registerTab(select, eventName);

		tabTopPanel.add(mainView.getToolPanel());

		// Reload if the test session changes
		ClientUtils.INSTANCE.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
			@Override
			public void onTestSessionChanged(TestSessionChangedEvent event) {
				if (event.getChangeType() == TestSessionChangedEvent.ChangeType.SELECT) {
					loadTestCollections();
				}
			}
		});

		mainView.getActorTabBar().addSelectionHandler(new ActorSelectionHandler());
		mainView.getOptionsTabBar().addSelectionHandler(new OptionSelectionHandler());

		// Initial load of tests in a test session
		loadTestCollections();

		// Register the Diagram clicked event handler
		ClientUtils.INSTANCE.getEventBus().addHandler(DiagramClickedEvent.TYPE, new DiagramPartClickedEventHandler() {
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

	private List<TestOverviewDTO> testOverviews(ActorOption actorOption) {
		List<TestInstance> testsForThisActorOption = testsPerActorOption.get(actorOption);
		List<TestOverviewDTO> overviews = new ArrayList<>();
		for (TestOverviewDTO dto : testOverviewDTOs.values()) {
			if (testsForThisActorOption.contains(dto.getTestInstance())) {
				overviews.add(dto);
			}
		}

		return overviews;
	}

	private boolean allowRun() {
		ActorAndOption aao = ActorOptionManager.actorDetails(currentActorOption);
		boolean selfTest = false;
		if (orchInit != null) {
			selfTest = orchInit.isSelfTest();
		}
		boolean externalStart = aao != null && aao.isExternalStart();
		return !externalStart || selfTest;
	}

	private boolean showValidate() {
		ActorAndOption aao = ActorOptionManager.actorDetails(currentActorOption);
		boolean selfTest = isSelfTest();
		boolean externalStart = aao != null && aao.isExternalStart();
		return externalStart && !selfTest;
	}

	private boolean isSelfTest() {
		if (orchInit != null) {
			return orchInit.isSelfTest();
		}
		return false;
	}

	public void removeTestDetails(TestInstance testInstance) {
		TestOverviewDTO dto = testOverviewDTOs.get(testInstance);
		if (dto != null)
			dto.setRun(false);
//		testOverviewDTOs.remove(testInstance);
		updateTestsOverviewHeader(currentActorOption);
	}

	// overwrites existing status
	private Collection<TestOverviewDTO> updateTestOverview(TestOverviewDTO dto) {
		testOverviewDTOs.put(dto.getTestInstance(), dto);
		return testOverviewDTOs.values();
	}

	/**
	 * currentActorTypeDescription is initialized late so calling this when it is available
	 * updates the build since it could not be constructed correctly at first.
	 * This must be called after testCollectionDefinitionDAOs is initialized.
	 */
	private void updateTestsOverviewHeader(ActorOption actorOption) {
		Collection<TestOverviewDTO> items = testOverviews(actorOption);
		resetStatistics(items.size());
		for (TestOverviewDTO testOverview : items) {
			if (testOverview.isRun()) {
				if (testOverview.isPass()) {
					testStatistics.addSuccessful();
				} else {
					testStatistics.addWithError();
				}
			}
		}

		// Display testStatus with statistics
		testsHeaderView.allowRun(allowRun());
		testsHeaderView.update(testStatistics, currentActorTypeDescription + " - " + getCurrentOptionTitle());
	}

	private String getCurrentOptionTitle() {
		ActorAndOption aao =  ActorOptionManager.actorDetails(currentActorOption);
		if (aao != null) {
			if (aao.getOptionId().equals(currentActorOption.optionId))
				return aao.getOptionTitle();
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
			testContextView.updateTestingContextDisplay();
			return;
		}
		new GetAssignedSiteForTestSessionCommand(){
			@Override
			public void onComplete(String result) {
				testContext.setSiteName(result);
				testContextView.updateTestingContextDisplay();
			}
		}.run(getCommandContext());
	}

	private void resetStatistics(int testcount) {
		testStatistics.clear();
		testStatistics.setTestCount(testcount);
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
				mainView.getOptionsTabBar().display(newActorTypeId);
			}
		}
	}

	private class OptionSelectionHandler implements SelectionHandler<Integer> {

		@Override
		public void onSelection(SelectionEvent<Integer> selectionEvent) {
			int i = selectionEvent.getSelectedItem();
			List<String> optionIds = ActorOptionManager.optionIds(currentActorOption.actorTypeId);
			if (i < optionIds.size()) {
				currentActorOption.setOptionId(optionIds.get(i));
			}
			changeDisplayedActorAndOptionType(currentActorOption);
			orchInit.setXuaOption(orchInit.XUA_OPTION.equals(currentActorOption.getOptionId()));
		}
	}

	// for use by ConfActorActivity
	public void changeDisplayedActorAndOptionType(ActorOption actorOption) {
		currentActorOption = actorOption;
		currentActorTypeDescription = getDescriptionForTestCollection(currentActorOption.actorTypeId);
		displayTestingPanel(mainView.getTestsPanel());
	}

	private String getDescriptionForTestCollection(String collectionId) {
		if (testCollectionDefinitionDAOs == null) return "not initialized";
		for (TestCollectionDefinitionDAO dao : testCollectionDefinitionDAOs) {
			if (dao.getCollectionID().equals(collectionId)) { return dao.getCollectionTitle(); }
		}
		return "???";
	}

	// load tab bar with actor types
	private void loadTestCollections() {
		// TabBar listing actor types
		new GetTestCollectionsCommand() {
			@Override
			public void onComplete(List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs) {
				me.testCollectionDefinitionDAOs = testCollectionDefinitionDAOs;
				displayActorsTabBar(mainView.getActorTabBar());
				currentActorTypeDescription = getDescriptionForTestCollection(currentActorOption.actorTypeId);
//				updateTestsOverviewHeader();

				// This is a little wierd being here. This depends on initTestSession
				// which is set AFTER onTabLoad is run so run here - later in the initialization
				// initTestSession is set from ConfActorActivity
				initializeTestingContext();
			}
		}.run(new GetCollectionRequest(getCommandContext(), "actorcollections"));
	}


	private class RefreshTestCollectionHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent clickEvent) {
			initializeTestDisplay(mainView.getTestsPanel());
//			displayTestCollection(mainView.getTestsPanel());
		}
	}

	// This includes Testing Environment and initialization sections
	private void displayTestingPanel(final Panel testsPanel) {

		mainView.getInitializationPanel().clear();
		displayOrchestrationHeader(mainView.getInitializationPanel());

		initializeTestDisplay(testsPanel);
//		displayTestCollection(testsPanel);
	}

	private void initializeTestDisplay(Panel testsPanel) {
		testDisplayGroup.clear();  // so they reload
		testsPanel.clear();

//		loadingMessage = new HTML("Initializing...");
//		loadingMessage.setStyleName("loadingMessage");
//		testsPanel.add(loadingMessage);
        mainView.showLoadingMessage("Initializing...");
		testsHeaderView.showSelfTestWarning(isSelfTest());

		new AutoInitConformanceTestingCommand() {
			@Override
			public void onComplete(Boolean result) {
				if (result)
					orchInit.handleClick(null);   // auto init orchestration
				else
					displayTestCollection(getMainView().getTestsPanel());
			}
		}.run(getCommandContext());

	}

	// load test results for a single test collection (actor/option) for a single site
	public void displayTestCollection(final Panel testsPanel) {

		// what tests are in the collection
		currentActorOption.loadTests(new AsyncCallback<List<TestInstance>>() {
			@Override
			public void onFailure(Throwable throwable) {
				new PopupMessage("getTestlogListing: " + throwable.getMessage());
			}

			@Override
			public void onSuccess(List<TestInstance> testInstances) {
				mainView.showLoadingMessage("Loading...");
				displayTests(testsPanel, testInstances, allowRun());
			}
		});
	}


	private void displayTests(final Panel testsPanel, final List<TestInstance> testInstances, boolean allowRun) {
		// results (including logs) for a collection of tests

		testDisplayGroup.allowRun(allowRun);
		testDisplayGroup.showValidate(showValidate());


		new GetTestsOverviewCommand() {
			@Override
			public void onComplete(List<TestOverviewDTO> testOverviews) {
				// sort tests by dependencies and alphabetically
				// save in testsPerActorOption so they run in this order as well
				List<TestInstance> testInstances1 = new ArrayList<>();
				testOverviews = new TestSorter().sort(testOverviews);
				for (TestOverviewDTO dto : testOverviews) {
					testInstances1.add(dto.getTestInstance());
				}
				testsPerActorOption.put(currentActorOption, testInstances1);

				testsPanel.clear();
				testsHeaderView.allowRun(allowRun());
				testsPanel.add(testsHeaderView.asWidget());
//                testStatistics.clear();
//                testStatistics.setTestCount(testOverviews.size());
				for (TestOverviewDTO testOverview : testOverviews) {
					updateTestOverview(testOverview);
					TestDisplay testDisplay = testDisplayGroup.display(testOverview);
					// Require late-binding of diagram due to orchestration place holders
					testDisplay.getView().setInteractionDiagram(new InteractionDiagramDisplay(testOverview, testContext.getTestSession(), getSiteToIssueTestAgainst(), testContext.getSiteUnderTestAsSiteSpec().getName()));
					testsPanel.add(testDisplay.asWidget());
				}
				updateTestsOverviewHeader(currentActorOption);

				mainView.clearLoadingMessage();
			}
		}.run(new GetTestsOverviewRequest(getCommandContext(), testInstances, getTestContext().getCurrentSiteSpec(), getTestContext().getSiteUnderTest()));
	}



	private void displayOrchestrationHeader(Panel initializationPanel) {
		String label = "Initialize Test Environment";
		if (currentActorOption.isRep()) {
			orchInit = new BuildRepTestOrchestrationButton(this, testContext, testContextView, initializationPanel, label);
			orchInit.addSelfTestClickHandler(new RefreshTestCollectionHandler());
			initializationPanel.add(orchInit.panel());
		}
		else if (currentActorOption.isReg()) {
			orchInit = new BuildRegTestOrchestrationButton(this, testContext, testContextView, initializationPanel, label);
			orchInit.addSelfTestClickHandler(new RefreshTestCollectionHandler());
			initializationPanel.add(orchInit.panel());
		}
		else if (currentActorOption.isRec()) {
			orchInit = new BuildRecTestOrchestrationButton(this, testContext, testContextView, initializationPanel, label);
			orchInit.addSelfTestClickHandler(new RefreshTestCollectionHandler());
			initializationPanel.add(orchInit.panel());
		}
		else if (currentActorOption.isRg()) {
			orchInit = new BuildRgTestOrchestrationButton(this, initializationPanel, label, testContext, testContextView, this);
			orchInit.addSelfTestClickHandler(new RefreshTestCollectionHandler());
			initializationPanel.add(orchInit.panel());
		}
		else if (currentActorOption.isIg()) {
			orchInit = new BuildIGTestOrchestrationButton(this, initializationPanel, label, testContext, testContextView, this, false, currentActorOption);
			orchInit.addSelfTestClickHandler(new RefreshTestCollectionHandler());
			initializationPanel.add(orchInit.panel());
		}
		else if (currentActorOption.isInitiatingImagingGatewaySut()) {
			orchInit = new BuildIIGTestOrchestrationButton(this, testContext, testContextView, initializationPanel, label);
			orchInit.addSelfTestClickHandler(new RefreshTestCollectionHandler());
			initializationPanel.add(orchInit.panel());
		}
		else if (currentActorOption.isRespondingingImagingGatewaySut()) {
			orchInit = new BuildRIGTestOrchestrationButton(this, testContext, testContextView, initializationPanel, label);
			orchInit.addSelfTestClickHandler(new RefreshTestCollectionHandler());
			initializationPanel.add(orchInit.panel());
		}
		else if (currentActorOption.isImagingDocSourceSut()) {
			orchInit = new BuildIDSTestOrchestrationButton(this, testContext, testContextView, initializationPanel, label);
			orchInit.addSelfTestClickHandler(new RefreshTestCollectionHandler());
			initializationPanel.add(orchInit.panel());
		}
		else if (currentActorOption.isIDC()) {
			orchInit = new BuildIDCTestOrchestrationButton(this, testContext, testContextView, this, initializationPanel, label);
			orchInit.addSelfTestClickHandler(new RefreshTestCollectionHandler());
			initializationPanel.add(orchInit.panel());
		}
		else if (currentActorOption.isEdgeServerSut()) {
			// TODO not implemented yet.
		}
		else {
			if (testContext.getSiteUnderTest() != null)
				siteToIssueTestAgainst = testContext.getSiteUnderTestAsSiteSpec();
		}
	}

	private void displayActorsTabBar(TabBar actorTabBar) {
		if (actorTabBar.getTabCount() == 0) {
			for (TestCollectionDefinitionDAO def : testCollectionDefinitionDAOs) {
				actorTabBar.addTab(def.getCollectionTitle());
			}
		}
	}

	@Override
	public RunTestsClickHandler getRunAllClickHandler() {
		return new RunTestsClickHandler(this, (TestTarget)this, testsHeaderView, orchInit, testsPerActorOption.get(currentActorOption));
	}


	/**
	 * The special XUA tests - for generating bad XUA assertions - are encoded in the test
	 * name.  When working with SAML, you ask an STS for a user and a bunch of conditions.
	 * When working with Gazelle, the username requested controls the generation of bad assertions.
	 * Each bad assertion named for the flaw it contains.
	 * @param testInstance
	 * @return XUA username from test ID
	 */
	String getXuaUsernameFromTestplan(TestInstance testInstance) {
		String testId = testInstance.getId();
		String[] parts = testId.split("_");
		if (parts.length>1) {
			return parts[parts.length-1];
		} else {
			new PopupMessage("Test Id needs to be in this format: actor_option_Xuausername.");
			return null;
		}
	}

//	private class RunAllSectionsClickHandler implements ClickHandler, TestIterator {
//		TestInstance testInstance;
//		List<TestInstance> sections = new ArrayList<TestInstance>();  // One TestInstance per section
//
//		RunAllSectionsClickHandler(TestInstance testInstance) { this.testInstance = testInstance; }
//
//		@Override
//		public void onClick(ClickEvent clickEvent) {
//			clickEvent.preventDefault();
//			clickEvent.stopPropagation();
//
//			new GetTestSectionsDAOsCommand(){
//				@Override
//				public void onComplete(List<SectionDefinitionDAO> sectionDefinitionDAOs) {
//					sections.clear();
//					for (SectionDefinitionDAO dao : sectionDefinitionDAOs) {
//						TestInstance ti = testInstance.copy();
//						ti.setSection(dao.getSectionName());
//						ti.setSutInitiated(dao.isSutInitiated());
//					}
//					onDone(null);
//				}
//			}.run(new GetTestSectionsDAOsRequest(getCommandContext(),testInstance));
//		}
//
//		@Override
//		public void onDone(TestInstance unused) {
//			testsHeaderView.showRunningMessage(true);
//			if (sections.size() == 0) {
//				testsHeaderView.showRunningMessage(false);
//				return;
//			}
//			TestInstance next = sections.get(0);
//			sections.remove(0);
//			runSection(next, this);
//		}
//	}

	@Override
	public DeleteAllClickHandler getDeleteAllClickHandler() {
		return new DeleteAllClickHandler(currentActorOption);
	}

	@Override
	public ClickHandler getRefreshTestCollectionClickHandler() {
		return new RefreshTestCollectionHandler();
	}

	private class DeleteAllClickHandler implements ClickHandler {
		ActorOption actorOption;

		DeleteAllClickHandler(ActorOption actorOption) {
			this.actorOption = actorOption;
		}

		@Override
		public void onClick(ClickEvent clickEvent) {
			clickEvent.preventDefault();
			clickEvent.stopPropagation();
			List <TestInstance> tests = testsPerActorOption.get(actorOption);
			for (TestInstance testInstance : tests) {
				new DeleteSingleTestCommand(){
					@Override
					public void onComplete(TestOverviewDTO testOverviewDTO) {
						updateTestOverview(testOverviewDTO);
						TestDisplay testDisplay = testDisplayGroup.display(testOverviewDTO);
						// Require late-binding of diagram due to orchestration place holders
						testDisplay.getView().setInteractionDiagram(new InteractionDiagramDisplay(testOverviewDTO, testContext.getTestSession(), getSiteToIssueTestAgainst(), testContext.getSiteUnderTestAsSiteSpec().getName()));
						updateTestsOverviewHeader(actorOption);
					}
				}.run(new DeleteSingleTestRequest(getCommandContext(),testInstance));
			}
		}
	}

	ClickHandler getInspectClickHandler(TestInstance testInstance) {
		new PopupMessage("ti=" + testInstance.toString() + " ts=" + getCurrentTestSession() + " sn=" + testContext.getSiteName());
		return new LaunchInspectorClickHandler(testInstance, getCurrentTestSession(), new SiteSpec(testContext.getSiteName()));
	}

	/**
	 * if testInstance contains a sectionName then run that section, otherwise run entire test.
	 * TestInstance.sutInitiated indicates whether one or more sections have to be initiated by the SUT
	 * @param sectionInstance
	 * @param sectionDone
	 */
	public void runSection(final TestInstance sectionInstance, final TestIterator sectionDone) {
		Map<String, String> parms = initializeTestParameters();

		if (parms == null) return;

		try {
			new RunTestCommand(){
				@Override
				public void onComplete(TestOverviewDTO testOverviewDTO) {
					// returned testStatus of entire test
					testDisplayGroup.display(testOverviewDTO);
					// Require late-binding of diagram due to orchestration place holders
//						testDisplay.getView().setInteractionDiagram(new InteractionDiagramDisplay(testOverview, testContext.getTestSession(), getSiteToIssueTestAgainst(), testContext.getSiteUnderTestAsSiteSpec().getName()));
					Collection<TestOverviewDTO> overviews = updateTestOverview(testOverviewDTO);
					updateTestsOverviewHeader(currentActorOption);
					// Schedule next section to be run
					if (sectionDone != null)
						sectionDone.onDone(sectionInstance);
				}
			}.run(new RunTestRequest(getCommandContext(),getSiteToIssueTestAgainst(),sectionInstance,parms,true));
		} catch (Exception e) {
			new PopupMessage(e.getMessage());
		}
	}




	public void runTest(final TestInstance testInstance, final TestIterator testIterator) {

		getSiteToIssueTestAgainst().setTls(orchInit.isTls());

		if (orchInit.isSaml()) {
			if (testIterator == null /* Signifies individual test runner */) {
				// STS SAML assertion
				// This has to be here because we need to retrieve the assertion just in time before the test executes. Any other way will be confusing to debug and more importantly the assertion will not be fresh.
				// Interface can be refactored to support mulitple run methods such as runTest[WithSamlOption] and runTest.
				TestInstance stsTestInstance = new TestInstance("GazelleSts");
				stsTestInstance.setSection("samlassertion-issue");
				SiteSpec stsSpec =  new SiteSpec("GazelleSts");
				Map<String, String> params = new HashMap<>();
				String xuaUsername = "Xuagood";
				if (orchInit.isXuaOption()) {
					xuaUsername = getXuaUsernameFromTestplan(testInstance);
				}
				params.put("$saml-username$",xuaUsername);
				try {
					new GetStsSamlAssertionCommand(){
						@Override
						public void onComplete(String result) {
							getSiteToIssueTestAgainst().setSaml(true);
							getSiteToIssueTestAgainst().setStsAssertion(result);

							runTestInstance(testInstance, null);
						}
					}.run(new GetStsSamlAssertionRequest(getCommandContext(),xuaUsername,stsTestInstance,stsSpec,params));
				} catch (Exception ex) {
					new PopupMessage("runTestInstance: Client call failed: getStsSamlAssertion: " + ex.toString());
				}
			} else {
				// Reuse SAML when running the entire Actor test collection OR as set by the Xua option
				runTestInstance(testInstance, testIterator);
			}

		} else {
			// No SAML
			getSiteToIssueTestAgainst().setSaml(false);
			runTestInstance(testInstance, testIterator);
		}
	}

	private void runTestInstance(final TestInstance testInstance, final TestIterator testIterator) {
		Map<String, String> parms = initializeTestParameters();
		if (parms == null) return;
		try {
			new RunTestCommand(){
				@Override
				public void onComplete(TestOverviewDTO testOverviewDTO) {
					// returned testStatus of entire test
					TestDisplay testDisplay = testDisplayGroup.display(testOverviewDTO);
					// Require late-binding of diagram due to orchestration place holders
					testDisplay.getView().setInteractionDiagram(new InteractionDiagramDisplay(testOverviewDTO, testContext.getTestSession(), getSiteToIssueTestAgainst(), testContext.getSiteUnderTestAsSiteSpec().getName()));
					updateTestOverview(testOverviewDTO);
					updateTestsOverviewHeader(currentActorOption);
					// Schedule next test to be run
					if (testIterator != null)
						testIterator.onDone(testInstance);
				}
			}.run(new RunTestRequest(getCommandContext(),getSiteToIssueTestAgainst(),testInstance,parms,true));
		} catch (Exception e) {
			new PopupMessage(e.getMessage());
		}

	}

	private Map<String, String> initializeTestParameters() {
		Map<String, String> parms = new HashMap<>();

		if (orchestrationResponse == null) {
			new PopupMessage("Initialize Test Environment before running tests.");
			return null;
		}

		if (ActorType.REPOSITORY.getShortName().equals(currentActorOption.actorTypeId)) {
			parms.put("$patientid$", repOrchestrationResponse.getPid().asString());
		}

		if (ActorType.DOCUMENT_RECIPIENT.getShortName().equals(currentActorOption.actorTypeId)) {
			parms.put("$patientid$", recOrchestrationResponse.getRegisterPid().asString());
		}

		if (ActorType.REGISTRY.getShortName().equals(currentActorOption.actorTypeId)) {
			parms.put("$patientid$", regOrchestrationResponse.getRegisterPid().asString());
		}

		if (getSiteToIssueTestAgainst() == null) {
			new PopupMessage("Test Setup must be initialized");
			return null;
		}
		return parms;
	}

	class SelfTestValueChangeHandler implements ValueChangeHandler<Boolean> {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {

		}
	}

	void setRepOrchestrationResponse(RepOrchestrationResponse repOrchestrationResponse) {
		this.repOrchestrationResponse = repOrchestrationResponse;
		setOrchestrationResponse(repOrchestrationResponse);
	}



	void setRecOrchestrationResponse(RecOrchestrationResponse recOrchestrationResponse) {
		this.recOrchestrationResponse = recOrchestrationResponse;
		setOrchestrationResponse(recOrchestrationResponse);
	}

	void setRegOrchestrationResponse(RegOrchestrationResponse regOrchestrationResponse) {
		this.regOrchestrationResponse = regOrchestrationResponse;
		setOrchestrationResponse(regOrchestrationResponse);
	}

	public void setOrchestrationResponse(AbstractOrchestrationResponse repOrchestrationResponse) {
		this.orchestrationResponse = repOrchestrationResponse;
	}

	public String getWindowShortName() {
		return "testloglisting";
	}

	public SiteSpec getSiteToIssueTestAgainst() {
		return siteToIssueTestAgainst;
	}

	public void setSiteToIssueTestAgainst(SiteSpec siteToIssueTestAgainst) {
		this.siteToIssueTestAgainst = siteToIssueTestAgainst;
	}

	public void setInitTestSession(String initTestSession) {
		this.initTestSession = initTestSession;
	}

	public TestContext getTestContext() {
		return testContext;
	}

	class SiteSelectionValidatorImpl implements  SiteSelectionValidator {

		@Override
		public void validate(SiteSpec siteSpec) {
			final String actorTypeId = currentActorOption.actorTypeId;
			final ActorType actorType = ActorType.findActor(actorTypeId);
			if (actorType == null) {
				// no actor type has been selected yet - no problem
				return;
			}

			new GetSiteCommand() {
				@Override
				public void onComplete(Site site) {
					if (!site.hasActor(actorType))
						new PopupMessage("System under test does not implement a " + actorType.getName());
				}
			};
		}
	}

	public ConformanceTestMainView getMainView() {
		return mainView;
	}
}
