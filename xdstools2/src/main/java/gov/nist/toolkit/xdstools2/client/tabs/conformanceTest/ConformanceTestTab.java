package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabBar;
import gov.nist.toolkit.actortransaction.shared.ActorOption;
import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.actortransaction.shared.IheItiProfile;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.interactiondiagram.client.events.DiagramClickedEvent;
import gov.nist.toolkit.interactiondiagram.client.events.DiagramPartClickedEventHandler;
import gov.nist.toolkit.interactiondiagram.client.widgets.InteractionDiagram;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.services.client.*;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.session.client.sort.TestSorter;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.testenginelogging.client.QuickScanLogAttribute;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.xdstools2.client.NotifyOnDelete;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.command.command.*;
import gov.nist.toolkit.xdstools2.client.event.testContext.TestContextChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testContext.TestContextChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs.BuildIGTestOrchestrationButton;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallbackT;
import gov.nist.toolkit.xdstools2.client.widgets.LaunchInspectorClickHandler;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;
import gov.nist.toolkit.xdstools2.shared.command.request.*;

import java.util.*;

import static gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.TestContext.NONE;

/**
 * All Conformance tests will be run out of here
 */
public class ConformanceTestTab extends ToolWindow implements TestRunner, TestTarget, Controller, NotifyOnDelete {

	private TestsHeaderView testsHeaderView = new TestsHeaderView(this);
	private TestContextView testContextView;

	private TestDisplayGroup testDisplayGroup;
	private TestContext testContext = new TestContext(this, new SiteSelectionValidatorImpl());

	private AbstractOrchestrationResponse orchestrationResponse;
	private RepOrchestrationResponse repOrchestrationResponse;
	private RecOrchestrationResponse recOrchestrationResponse;
	private RegOrchestrationResponse regOrchestrationResponse;
	private SrcOrchestrationResponse srcOrchestrationResponse;
	private FhirSupportOrchestrationResponse fhirSupportOrchestrationResponse;

	private final TestStatistics testStatistics = new TestStatistics();

	private final ActorOptionConfig currentActorOption = new ActorOptionConfig();
	private String currentActorTypeDescription;
	private SiteSpec siteToIssueTestAgainst = null;

	// Descriptions of current test list
	private List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs;

	// Test results
	// test ==> results
	// this contains all tests independent of whether they have been run.
	private Map<TestInstance, TestOverviewDTO> testOverviewDTOs = new HashMap<>();

	// for each actor type id, the list of tests for it
	private Map<ActorOptionConfig, List<TestInstance>> testsPerActorOption = new HashMap<>();

	private ConformanceTestMainView mainView;
	private AbstractOrchestrationButton orchInit = null;

	private ConformanceToolMenu conformanceToolMenu = new ConformanceToolMenu() {
		@Override
		public void onMenuSelect(TabConfig actor, Map<String,TabConfig> target) {
			mainView.getTabBarPanel().setVisible(true);

			currentActorOption.setLaunchedFromMenu(true);

			currentActorOption.setTabConfig(actor);
			currentActorOption.setActorTypeId(null);
			currentActorOption.setProfileId(null);
			currentActorOption.setOptionId(null);

			if (target.get("actor")!=null)
				currentActorOption.setActorTypeId(target.get("actor").getTcCode());
			if (target.get("profile")!=null)
				currentActorOption.setProfileId(IheItiProfile.find(target.get("profile").getTcCode()));
			if (target.get("option")!=null)
				currentActorOption.setOptionId(target.get("option").getTcCode());

			GWT.log(currentActorOption.getActorTypeId() + " " + currentActorOption.getProfileId() + " " + currentActorOption.getOptionId());

			updateDisplayedActorAndOptionType();
		}
		@Override
		CommandContext getCommandContext() {
			return ConformanceTestTab.this.getCommandContext();
		}

		@Override
		void updateTestStatistics(Map<ActorOptionConfig, List<TestInstance>> testsPerActorOption, Map<TestInstance, TestOverviewDTO> testOverviewDTOs, TestStatistics testStatistics, ActorOptionConfig actorOption) {
			Collection<TestOverviewDTO> items = testOverviews(testsPerActorOption, testOverviewDTOs, actorOption);
			resetStatistics(testStatistics,items.size());
			for (TestOverviewDTO testOverview : items) {
				if (testOverview.isRun()) {
					if (testOverview.isPass()) {
						testStatistics.addSuccessful();
					} else {
						testStatistics.addWithError();
					}
				}
			}
		}
	};


	public ConformanceTestTab(double east, double west) {
		super(east, west);
	}

	public ConformanceTestTab() {
		super(10.0, 0.0);
		if (ClientUtils.INSTANCE.getCurrentTestSession().equals(TestSession.CAT_TEST_SESSION)) {
			notLoadedReason = "Conformance Tool cannot be run in " + TestSession.CAT_TEST_SESSION.getValue() + " Test Session";
			return;
		}
		mainView = new ConformanceTestMainView(this);
		testContextView = new TestContextView(this, mainView.getTestSessionDescription(), testContext, new SiteSelectionValidatorImpl());
		testContext.setTestContextView(testContextView);
		testDisplayGroup = new TestDisplayGroup(testContext, testContextView, this, this);
	}

	private String notLoadedReason = null;

	@Override
	public boolean loaded() {
		return notLoadedReason == null;
	}

	@Override
	public String notLoadedReason() {
		return notLoadedReason;
	}


	// if we dont arrange to remove the TestSession change handler then the TestSession
	// manager will pop up even after the tab is deleted
	private HandlerRegistration testSessionChangedHandler = null;

	@Override
	public void onDelete() {
		// remove existing test session change handler
		if (testSessionChangedHandler != null) {
			GWT.log("Unregister TestSession change handler for Conformance Tool");
			testSessionChangedHandler.removeHandler();
			testSessionChangedHandler = null;
		}
	}

	@Override
	public void onTabLoad(final boolean select, String eventName) {
		testContextView.updateTestingContextDisplay();
		mainView.getTestSessionDescription().addClickHandler(testContextView);

		addEast(mainView.getTestSessionDescriptionPanel());
		registerDeletableTab(select, eventName, this);

		tabTopPanel.add(mainView.getToolPanel());

		mainView.getActorTabBar().addSelectionHandler(new ActorSelectionHandler());
		mainView.getProfileTabBar().addSelectionHandler(new ProfileSelectionHandler());
		mainView.getOptionsTabBar().addSelectionHandler(new OptionSelectionHandler());

		mainView.getIndexAnchor().addStyleName("iconbutton");
		mainView.getIndexAnchor().setTitle("Overview of Actor/Profile/Option");
		mainView.getIndexAnchor().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				mainView.getTabBarPanel().setVisible(false);
				mainView.getInitializationPanel().clear();
				mainView.getTestsPanel().clear();
				mainView.getActorTabBar().selectTab(-1);
				mainView.getProfileTabBar().clear();
				mainView.getOptionsTabBar().clear();
				currentActorOption.setActorTypeId(null);
				currentActorOption.setProfileId(null);
				currentActorOption.setOptionId(null);

				conformanceToolMenu.displayMenu(mainView.getTestsPanel());
			}
		});

		loadTestCollections();

		if (testSessionChangedHandler == null) {
			// Reload if the test session changes
			testSessionChangedHandler = ClientUtils.INSTANCE.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
				@Override
				public void onTestSessionChanged(final TestSessionChangedEvent event) {
						if (event.getChangeType() == TestSessionChangedEvent.ChangeType.SELECT) {
							testContextView.updateTestingContextDisplay();
							// Filter out extraneous events which cause confusing duplicate loads
							if (!"SignInSelector".equals(event.getEventSource())
							  && !"TestContextDialog".equals(event.getEventSource()) // TestContext fires both TestSessionChanged and TestContextChanged
								) {
								GWT.log("Test session changed: " + event.getValue());
								loadTestCollections();
							} else {
								GWT.log("Ignored " + event.getEventSource());
							}
						}
				}
			});
		}

		// Register the Diagram RequestConnector clicked event handler
		ClientUtils.INSTANCE.getEventBus().addHandler(DiagramClickedEvent.TYPE, new DiagramPartClickedEventHandler() {
			@Override
			public void onClicked(TestInstance testInstance, InteractionDiagram.DiagramPart part) {
				if (InteractionDiagram.DiagramPart.RequestConnector.equals(part)
						|| InteractionDiagram.DiagramPart.ResponseConnector.equals(part)) {
					new LaunchInspectorClickHandler(testInstance, getCurrentTestSession(), new SiteSpec(testContext.getSiteName(), getTestSession())).onClick(null);
				}
			}
		});

		// This is needed to only refresh the status index page
//		if (false) {
			ClientUtils.INSTANCE.getEventBus().addHandler(TestContextChangedEvent.TYPE, new TestContextChangedEventHandler() {
				@Override
				public void onTestContextChanged(TestContextChangedEvent event) {
					testContextView.updateTestingContextDisplay();
					if (currentActorOption.getActorTypeId()==null) { // Menu mode has no actor
						loadTestCollections();
					} else {
						updateDisplayedActorAndOptionType();
					}
//					testContextView.updateTestingContextDisplay();
//				    loadTestCollections();
//					if (getInitTestSession()==null) {
//						displayMenu(mainView.getTestsPanel());
//						if (updateDisplayedActorAndOptionType()) { // . Check if currentactoroptin is properly set (ok if profile & option is null)
//							initializeTestDisplay(mainView.getTestsPanel());
//						}
//					}
				}
			});
//		}

	}



	private List<TestOverviewDTO> testOverviews(Map<ActorOptionConfig, List<TestInstance>> testsPerActorOption, Map<TestInstance, TestOverviewDTO> testOverviewDTOs, ActorOptionConfig actorOption) {
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
		boolean selfTest = false;
		if (orchInit != null) {
			selfTest = orchInit.isSelfTest();
		}

		boolean externalStart = getOptionTabConfig(currentActorOption).getExternalStart();
		return !externalStart || selfTest;
	}

	private TabConfig getOptionTabConfig(ActorOptionConfig actorOption) {
		TabConfig tabConfig = actorOption.getTabConfig();
		TabConfig profiles = tabConfig.getFirstChildTabConfig();
		for (TabConfig profile : profiles.getChildTabConfigs())  {
			if (actorOption.getProfileId().toString().equals(profile.getTcCode())) {
				TabConfig options = profile.getFirstChildTabConfig();

				for (TabConfig option : options.getChildTabConfigs()) {
					if (actorOption.getOptionId().equals(option.getTcCode())) {
					    return option;
					}
				}
			}
		}
		return null;
	}

	private boolean allowValidate() {
		boolean selfTest = isSelfTest();
		boolean externalStart = getOptionTabConfig(currentActorOption).getExternalStart();
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
		updateTestsOverviewHeader(testsPerActorOption, testOverviewDTOs, testStatistics, currentActorOption);
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
	private void updateTestsOverviewHeader(Map<ActorOptionConfig, List<TestInstance>> testsPerActorOption, Map<TestInstance, TestOverviewDTO> testOverviewDTOs, TestStatistics testStatistics, ActorOptionConfig actorOption) {
		conformanceToolMenu.updateTestStatistics(testsPerActorOption, testOverviewDTOs, testStatistics, actorOption);

		// Display testStatus with statistics
		testsHeaderView.allowRun(allowRun());
		testsHeaderView.update(testStatistics, currentActorTypeDescription + " - " + getCurrentOptionTitle());
	}




	private String getCurrentOptionTitle() {
		return getOptionTabConfig(currentActorOption).getLabel();
	}

	@Override
	public String getTitle() { return "Conformance Tests"; }

	/**
	 *
	 */
	private void initializeTestingContext() {

//		if (getCurrentTestSession() == null || getCurrentTestSession().equals("")) {
//			testContextView.updateTestingContextDisplay();
//			return;
//		}

		if (siteToIssueTestAgainst != null && !(siteToIssueTestAgainst.getName()=="" || siteToIssueTestAgainst.getName()==null)) {
			new GetSiteCommand() {
				@Override
				public void onFailure(Throwable throwable) {
                    showPopupMessage("System " + siteToIssueTestAgainst + " does not exist.");
					testContext.setCurrentSiteSpec(null);
					testContext.setSiteUnderTest(null);
					testContextView.updateTestingContextDisplay();
					updateDisplayedActorAndOptionType();
				}
				@Override
				public void onComplete(Site result) {
					testContext.setSiteUnderTest(result);
					testContext.setCurrentSiteSpec(result.getName());
					testContextView.updateTestingContextDisplay();
					updateDisplayedActorAndOptionType();
				}
			}.run(new GetSiteRequest(ClientUtils.INSTANCE.getCommandContext(), siteToIssueTestAgainst.name));
		} else {
			new GetAssignedSiteForTestSessionCommand() {
				@Override
				public void onFailure(Throwable throwable) {
					showPopupMessage("GetAssignedSiteForTestSessionCommand failed: Unable to determine if SUT has been assigned in Test Context.");
				}

				@Override
				public void onComplete(final String result) {
					testContext.setCurrentSiteSpec(result);
					testContextView.updateTestingContextDisplay();

					if ((result == null) || NONE.equals(result)) {
						updateDisplayedActorAndOptionType();
						return;
					}

					new GetSiteCommand() {
						@Override
						public void onFailure(Throwable throwable) {
                            showPopupMessage("System " + result + " does not exist.");
							testContext.setCurrentSiteSpec(null);
							testContext.setSiteUnderTest(null);
							testContextView.updateTestingContextDisplay();
							updateDisplayedActorAndOptionType();
						}

						@Override
						public void onComplete(Site result) {
							testContext.setSiteUnderTest(result);
							updateDisplayedActorAndOptionType();
						}
					}.run(new GetSiteRequest(ClientUtils.INSTANCE.getCommandContext(), result));
				}
			}.run(getCommandContext());
		}
	}

	private void resetStatistics(TestStatistics testStatistics, int testcount) {
		testStatistics.clear();
		testStatistics.setTestCount(testcount);
	}

	// . Show profiles
	// actor type selection changes
	private class ActorSelectionHandler implements SelectionHandler<Integer> {

		@Override
		public void onSelection(SelectionEvent<Integer> selectionEvent) {
			// 3. Draw out all actor tabs (profile & option)
			int i = selectionEvent.getSelectedItem();
			String newActorTypeId = new ActorOption(TestCollectionDefinitionDAO.getNonOption(testCollectionDefinitionDAOs).get(i).getCollectionID()).actorTypeId;
				orchestrationResponse = null;  // so we know orchestration not set up
				currentActorOption.setActorTypeId(newActorTypeId);

				setCurrentActorTabConfig(newActorTypeId);
				currentActorOption.setProfileId(null);
				currentActorOption.setOptionId("");
				refreshActorView(newActorTypeId);
		}
	}

	protected void setCurrentActorTabConfig(String newActorTypeId) {
		for (TabConfig tabConfig : conformanceToolMenu.getTabConfig().getChildTabConfigs()) {
            if (tabConfig.getTcCode().equals(newActorTypeId)) {
                currentActorOption.setTabConfig(tabConfig);
            }
        }
	}

	protected void refreshActorView(String newActorTypeId) {
		getMainView().getInitializationPanel().clear();
		getMainView().getTestsPanel().clear();
		mainView.getProfileTabBar().clear();
		mainView.getOptionsTabBar().clear();
		mainView.getProfileTabBar().display(conformanceToolMenu.getTabConfig(), "Profiles", newActorTypeId);
		selectProfileTab();
		GWT.log("actor was refreshed.");
	}

	// TODO: make this like ActorSelectionHandler, where all sub-options get cleared. Do this for Option handler as well. Move method, and call it from the updateDisplayedActorAndOptionType
	private class ProfileSelectionHandler implements SelectionHandler<Integer> {
		@Override
		public void onSelection(SelectionEvent<Integer> selectionEvent) {
			int i = selectionEvent.getSelectedItem();

			GWT.log("profile was selected. Profile tab index is: " + i);

			TabConfig profiles = currentActorOption.getTabConfig().getFirstChildTabConfig();
			if ("Profiles".equals(profiles.getLabel())) {
				TabConfig profile = profiles.getChildTabConfigs().get(i);
				currentActorOption.setProfileId(IheItiProfile.find(profile.getTcCode()));
				getMainView().getInitializationPanel().clear();
				getMainView().getTestsPanel().clear();
				mainView.getOptionsTabBar().clear();
				mainView.getOptionsTabBar().display(currentActorOption.getTabConfig(), "Options", profile.getTcCode());
				selectOptionTab();
			}
		}
	}

	private class OptionSelectionHandler implements SelectionHandler<Integer> {
		@Override
		public void onSelection(SelectionEvent<Integer> selectionEvent) {
		    getMainView().getInitializationPanel().clear();
			getMainView().getTestsPanel().clear();
			int i = selectionEvent.getSelectedItem();

			GWT.log("option was selected.");

			TabConfig profiles = currentActorOption.getTabConfig().getFirstChildTabConfig();
			if ("Profiles".equals(profiles.getLabel())) {
				for (TabConfig profile : profiles.getChildTabConfigs()) {
					if (profile.getTcCode().equals(currentActorOption.getProfileId().toString())) {
						TabConfig options =  profile.getFirstChildTabConfig();
						if ("Options".equals(options.getLabel())) {
							TabConfig option = options.getChildTabConfigs().get(i);
							currentActorOption.setOptionId(option.getTcCode());
							currentActorTypeDescription = getDescriptionForTestCollection(currentActorOption.actorTypeId);

							displayTestingPanel(mainView.getTestsPanel());
							orchInit.setXuaOption(orchInit.XUA_OPTION.equals(currentActorOption.getOptionId()));
						}

					}
				}
			}


		}
	}

	// for use by ConfActorActivity
	private boolean updateDisplayedActorAndOptionType() {
	    if (currentActorOption!=null && currentActorOption.getActorTypeId()!=null && !"".equals(currentActorOption.getActorTypeId())) {
			currentActorTypeDescription = getDescriptionForTestCollection(currentActorOption.actorTypeId);

			int idx = 0;
			boolean foundSelectedActorTab = false;
			for (TestCollectionDefinitionDAO tcd : TestCollectionDefinitionDAO.getNonOption(testCollectionDefinitionDAOs)) {
				if (currentActorOption.getActorTypeId().equals(new ActorOption(tcd.getCollectionID()).actorTypeId)) {
					foundSelectedActorTab = true;
					break;
				}
				idx++;
			}

			if (foundSelectedActorTab) {
				getMainView().getActorTabBar().selectTab(idx, false);

				if (currentActorOption.getTabConfig() == null)
					setCurrentActorTabConfig(currentActorOption.getActorTypeId());

				refreshActorView(currentActorOption.getActorTypeId());
			}

			return foundSelectedActorTab;
		}
		return false;
	}

	private void selectProfileTab() {
		// If profile is not provided and there is only one profile, select it.
		UserDefinedTabBar profileTabBar = getMainView().getProfileTabBar();
		if (profileTabBar!=null && currentActorOption.getProfileId()==null) {
            if (profileTabBar.getTabConfigs().size()>0) {
                 currentActorOption.setProfileId(IheItiProfile.find(profileTabBar.getTabConfigs().get(0).getTcCode()));
            }
        }

		if (currentActorOption.getProfileId()!=null) {
			selectTab(currentActorOption.getProfileId().toString(), profileTabBar);
		}
	}

	private void selectOptionTab() {
		mainView.getOptionsTabBar().clear();
		mainView.getOptionsTabBar().display(currentActorOption.getTabConfig(), "Options", currentActorOption.getProfileId().toString());

		// If option is not provided, automatically select the first tab.
		String optionCode = currentActorOption.getOptionId();
		if (optionCode==null) {
            if (mainView.getOptionsTabBar().getTabConfigs().size()>0) {
                currentActorOption.setOptionId(mainView.getOptionsTabBar().getTabConfigs().get(0).getTcCode());
            }
        }

		if (currentActorOption.getOptionId()!=null) {
            selectTab(currentActorOption.getOptionId(), getMainView().getOptionsTabBar());
        }
	}

	private void selectTab(String tcCode, UserDefinedTabBar tabBar) {
		List<TabConfig> tabConfigs = tabBar.getTabConfigs();
		int idx;
		for (idx = 0; idx < tabConfigs.size(); idx++) {
            if (tcCode.equals( tabConfigs.get(idx).getTcCode())) {
                tabBar.selectTab(idx);
                /* When the profile is selected with FireEvents,
                we cannot differentiate between a/p/o select from OverviewMenu
                or when User selects the profile tab (which should clear the option flag here). */
            }
        }
	}


	private String getDescriptionForTestCollection(String actorTypeId) {
		if (testCollectionDefinitionDAOs == null) return "not initialized";
		for (TestCollectionDefinitionDAO dao : testCollectionDefinitionDAOs) {
			if (new ActorOption(dao.getCollectionID()).actorTypeId.equals(actorTypeId)) { return dao.getCollectionTitle(); }
		}
		return "???";
	}

	// load tab bar with actor types
	private void loadTestCollections() {
			new GetPrunedTabConfigCommand() {
				@Override
				public void onComplete(UserTestCollection userTestCollection) {
//					GWT.log("In loadTestCollections.");
					testCollectionDefinitionDAOs = userTestCollection.getTestCollectionDefinitionDAOs();
					conformanceToolMenu.setTabConfig(userTestCollection.getTabConfig());
					// Initial load of tests in a test session
					displayActors();

				}
			}.run(new GetTabConfigRequest(ClientUtils.INSTANCE.getCommandContext(),"ConfTests"));
	}

	private void displayActors() {
		// Finally display
		boolean actorIsSelected = currentActorOption!=null && currentActorOption.getActorTypeId()!=null && !"".equals(currentActorOption.getActorTypeId());
		getMainView().getTabBarPanel().setVisible(actorIsSelected);

		displayActorsTabBar(mainView.getActorTabBar());

		// 2. Write the site map here
		if (currentActorOption==null || currentActorOption.getActorTypeId()==null) { // Only display the menu when actor is not selected.
			boolean result = conformanceToolMenu.displayMenu(mainView.getTestsPanel());
			mainView.getActorpanel().setVisible(result);
		}

		currentActorTypeDescription = getDescriptionForTestCollection(currentActorOption.actorTypeId);

		initializeTestingContext();
	}



	private HTML loadingMessage;

	private class RefreshTestCollectionHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent clickEvent) {
			initializeTestDisplay(mainView.getTestsPanel());
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
//		mainView.showLoadingMessage("Initializing...");
		testsHeaderView.showSelfTestWarning(isSelfTest());

		new AutoInitConformanceTestingCommand() {
			@Override
			public void onComplete(Boolean result) {
				if (result)
					orchInit.handleClick(null);   // auto init orchestration
//				else {
//					displayTestCollection(getMainView().getTestsPanel());
//				}
			}

			@Override
			public void onFailure(Throwable throwable) {
				Window.alert(throwable.toString());
			}
		}.run(getCommandContext());
	}

	// load test results for a single test collection (actor/option) for a single site
	public void displayTestCollection(final Panel testsPanel) {

		// what tests are in the collection
		currentActorOption.loadTests(new AsyncCallback<List<TestInstance>>() {
			@Override
			public void onFailure(Throwable throwable) {
				showPopupMessage("getTestlogListing: " + throwable.getMessage());
			}

			@Override
			public void onSuccess(List<TestInstance> testInstances) {
				displayTests(testsPanel, testInstances, allowRun());
			}
		});
	}


	private void displayTests(final Panel testsPanel, List<TestInstance> testInstances, boolean allowRun) {
		final Map<String, String> parms = initializeTestParameters();
		// results (including logs) for a collection of tests

		testDisplayGroup.allowRun(allowRun);
		testDisplayGroup.allowValidate(allowValidate());

		GetTestsOverviewRequest tor = new GetTestsOverviewRequest(getCommandContext(), testInstances, new QuickScanLogAttribute[]{QuickScanLogAttribute.IS_RUN,QuickScanLogAttribute.IS_PASS,QuickScanLogAttribute.HL7TIME, QuickScanLogAttribute.IS_TLS, QuickScanLogAttribute.SITE, QuickScanLogAttribute.TEST_DEPENDENCIES});
		mainView.showLoadingMessage("Loading...");
        new GetActorTestProgressCommand() {
			@Override
			public void onFailure(Throwable throwable) {
				mainView.clearLoadingMessage();
				super.onFailure(throwable);
			}

			@Override
			public void onComplete(List<TestOverviewDTO> testOverviews) {
				// sort tests by dependencies and alphabetically
				// save in testsPerActorOption so they run in this order as well
                try {
					List<TestInstance> testInstances1 = new ArrayList<>();
					testOverviews = new TestSorter().sort(testOverviews);
					for (TestOverviewDTO dto : testOverviews) {
						testInstances1.add(dto.getTestInstance());
					}
					testsPerActorOption.put(currentActorOption, testInstances1);

					testsPanel.clear();
					testsHeaderView.allowRun(allowRun());
					testsPanel.add(testsHeaderView.asWidget());
					for (final TestOverviewDTO testOverview : testOverviews) {
						updateTestOverview(testOverview);

						final TestDisplay testDisplay = testDisplayGroup.display(testOverview, null); // Null diagram: No diagram will be available for Not-run test status.
						testsPanel.add(testDisplay.asWidget());
						InteractionDiagramDisplay diagramDisplay = new InteractionDiagramDisplay(
								null,
								testContext.getTestSession(),
								getSiteToIssueTestAgainst(),
								((testContext.getSiteUnderTestAsSiteSpec() != null) ? testContext.getSiteUnderTestAsSiteSpec().getName() : ""),
								currentActorOption,
								getTestInstancePatientId(testOverview.getTestInstance(), parms));
								// Lazy loading of TestOverviewDTO until it is opened.
								HandlerRegistration openTestBarHReg = testDisplay.getView().addOpenHandler(new TestBarOpenHandler(testDisplay, testOverview, getCommandContext(), diagramDisplay
								, new SimpleCallbackT<TestOverviewDTO>(){public void run(TestOverviewDTO t){}} // a -> updateTestOverview(a).
								));
						testDisplay.getView().setOpenTestBarHReg(openTestBarHReg);
					}
					updateTestsOverviewHeader(testsPerActorOption, testOverviewDTOs, testStatistics, currentActorOption);
				} finally {
					mainView.clearLoadingMessage();
				}
			}
		}.run(tor);
	}



	private static String getPatientIdStr(Map<String, String> parms) {
		return (parms!=null)?parms.get("$patientid$"):null;
	}

	private void displayOrchestrationHeader(Panel initializationPanel) {
		String label = "Initialize Test Environment";
		ActorOptionConfig currentActorOption = this.currentActorOption;
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
			orchInit = new BuildRecTestOrchestrationButton(this, testContext, testContextView, initializationPanel, label, currentActorOption);
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
		else if (currentActorOption.isSrc() && currentActorOption.isMhd()) {
			orchInit = new BuildSrcTestOrchestrationButton(this, testContext, testContextView, initializationPanel, label, currentActorOption);
			orchInit.addSelfTestClickHandler(new RefreshTestCollectionHandler());
			initializationPanel.add(orchInit.panel());
		}
		else if (currentActorOption.isFhirSupport()) {
			orchInit = new BuildFhirSupportOrchestrationButton(this, testContext, testContextView, initializationPanel, label, currentActorOption);
			orchInit.addSelfTestClickHandler(new RefreshTestCollectionHandler());
			initializationPanel.add(orchInit.panel());
		}
		else {
			if (testContext.getSiteUnderTest() != null)
				siteToIssueTestAgainst = testContext.getSiteUnderTestAsSiteSpec();
		}
	}

	private void displayActorsTabBar(TabBar actorTabBar) {
		if (actorTabBar.getTabCount() == 0 && conformanceToolMenu.getTabConfig()!=null) {
			for (TabConfig tabConfig : conformanceToolMenu.getTabConfig().getChildTabConfigs()) {
				if (tabConfig.isVisible()) {
					actorTabBar.addTab(tabConfig.getLabel());
				}
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
			showPopupMessage("Test Id needs be suffixed with '_username' like so: testId_username.");
			return null;
		}
	}

	@Override
	public DeleteAllClickHandler getDeleteAllClickHandler() {
		return new DeleteAllClickHandler(currentActorOption);
	}

	@Override
	public ClickHandler getRefreshTestCollectionClickHandler() {
		return new RefreshTestCollectionHandler();
	}

	private class DeleteAllClickHandler implements ClickHandler {
		ActorOptionConfig actorOption;

		DeleteAllClickHandler(ActorOptionConfig actorOption) {
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
						InteractionDiagramDisplay diagramDisplay = new InteractionDiagramDisplay(testOverviewDTO, testContext.getTestSession(), getSiteToIssueTestAgainst(), testContext.getSiteUnderTestName(),actorOption,null);
						testDisplayGroup.display(testOverviewDTO, diagramDisplay);
						updateTestsOverviewHeader(testsPerActorOption, testOverviewDTOs, testStatistics, actorOption);
					}
				}.run(new DeleteSingleTestRequest(getCommandContext(),testInstance));
			}
		}
	}

	ClickHandler getInspectClickHandler(TestInstance testInstance) {
		showPopupMessage("ti=" + testInstance.toString() + " ts=" + getCurrentTestSession() + " sn=" + testContext.getSiteName());
		return new LaunchInspectorClickHandler(testInstance, getCurrentTestSession(), new SiteSpec(testContext.getSiteName(), getTestSession()));
	}

	/**
	 * if testInstance contains a sectionName then run that section, otherwise run entire test.
	 * TestInstance.sutInitiated indicates whether one or more sections have to be initiated by the SUT
	 * @param sectionInstance
	 * @param sectionDone
	 */
	public void runSection(final TestInstance sectionInstance, final TestIterator sectionDone) {
		Map<String, String> parms = initializeTestParameters();
		final String patientId = getTestInstancePatientId(sectionInstance, parms);

		if (parms == null) return;

		try {
			new RunTestCommand(){
				@Override
				public void onComplete(TestOverviewDTO testOverviewDTO) {
					InteractionDiagramDisplay diagramDisplay = new InteractionDiagramDisplay(testOverviewDTO, testContext.getTestSession(), getSiteToIssueTestAgainst(), testContext.getSiteUnderTestName(),currentActorOption,patientId);
					// returned testStatus of entire test
					testDisplayGroup.display(testOverviewDTO, diagramDisplay);
					Collection<TestOverviewDTO> overviews = updateTestOverview(testOverviewDTO);
					updateTestsOverviewHeader(testsPerActorOption, testOverviewDTOs, testStatistics, currentActorOption);
					// Schedule next section to be run
					if (sectionDone != null)
						sectionDone.onDone(sectionInstance);
				}
			}.run(new RunTestRequest(getCommandContext(),getSiteToIssueTestAgainst(),sectionInstance,parms,true));
		} catch (Exception e) {
			showPopupMessage(e.getMessage());
		}
	}




	public void runTest(final TestInstance testInstance, final Map<String, String> sectionParms, final TestIterator testIterator) {

		getSiteToIssueTestAgainst().setTls(orchInit.isTls());

		if (orchInit.isSaml()) {
			if (testIterator == null /* Signifies individual test runner */) {
				Map<String,String> tkPropMap = ClientUtils.INSTANCE.getTkPropMap();
				String stsActorName = null;
				String stsTpName = null;
				if (tkPropMap!=null) {
					stsActorName = tkPropMap.get("Sts_ActorName");
					stsTpName = tkPropMap.get("Sts_TpName");
				} else {
					showPopupMessage("Error reading tkPropMap cache.");
				}
				// STS SAML assertion
				// This has to be here because we need to retrieve the assertion just in time before the test executes. Any other way will be confusing to debug and more importantly the assertion will not be fresh.
				// Interface can be refactored to support mulitple run methods such as runTest[WithSamlOption] and runTest.
				TestInstance stsTestInstance = new TestInstance(stsTpName, TestSession.DEFAULT_TEST_SESSION);
				stsTestInstance.setSection("samlassertion-issue");
				SiteSpec stsSpec =  new SiteSpec(stsActorName, TestSession.DEFAULT_TEST_SESSION);
				Map<String, String> params = new HashMap<>();
				String xuaUsername = "valid";
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

							runTestInstance(testInstance, null, null);
						}
					}.run(new GetStsSamlAssertionRequest(getCommandContext(),xuaUsername,stsTestInstance,stsSpec,params));
				} catch (Exception ex) {
					showPopupMessage("runTestInstance: Client call failed: getStsSamlAssertion: " + ex.toString());
				}
			} else {
				// Reuse SAML when running the entire Actor test collection OR as set by the Xua option
				runTestInstance(testInstance, null, testIterator);
			}

		} else {
			// No SAML
			getSiteToIssueTestAgainst().setSaml(false);
			runTestInstance(testInstance, sectionParms, testIterator);
		}
	}

	private void runTestInstance(final TestInstance testInstance, final Map<String, String> sectionParms, final TestIterator testIterator) {
		Map<String, String> parms = initializeTestParameters();
		final String patientId = getTestInstancePatientId(testInstance, parms);

		setPatientId(parms, patientId);

		if (sectionParms != null) {
			for (String name : sectionParms.keySet()) {
				parms.put(name, sectionParms.get(name));
			}
		}
		if (parms == null) return;
		try {
			new RunTestCommand(){
				@Override
				public void onComplete(TestOverviewDTO testOverviewDTO) {
				    InteractionDiagramDisplay diagramDisplay = new InteractionDiagramDisplay(testOverviewDTO, testContext.getTestSession(), getSiteToIssueTestAgainst(), testContext.getSiteUnderTestName(),currentActorOption,patientId);
					// returned testStatus of entire test
					testDisplayGroup.display(testOverviewDTO, diagramDisplay);
					updateTestOverview(testOverviewDTO);
					updateTestsOverviewHeader(testsPerActorOption, testOverviewDTOs, testStatistics, currentActorOption);
					// Schedule next test to be run
					if (testIterator != null)
						testIterator.onDone(testInstance);
				}
			}.run(new RunTestRequest(getCommandContext(),getSiteToIssueTestAgainst(),testInstance, parms,true));
		} catch (Exception e) {
			showPopupMessage(e.getMessage());
		}

	}

	private void setPatientId(Map<String, String> parms, String patientId) {
		if (patientId != null && !patientId.equals(""))
		parms.put("$patientid$",patientId);
	}

	/**
	 * this tags the patient id with the test number so the contents in the registry can be tracked
	 * @param testInstance
	 * @param parms
	 * @return
	 */
	private String getTestInstancePatientId(TestInstance testInstance, Map<String, String> parms) {
		if (false && ActorType.REPOSITORY.getShortName().equals(currentActorOption.actorTypeId)) {
			return testInstance.getId() + "_" + getPatientIdStr(parms);
		} else
			return getPatientIdStr(parms);
	}

	private Map<String, String> initializeTestParameters() {
		Map<String, String> parms = new HashMap<>();
		ActorOptionConfig currentActorOption = this.currentActorOption;

		if (orchestrationResponse == null) {
			showPopupMessage("Initialize Test Environment before running tests.");
			return null;
		}

		if (ActorType.REPOSITORY.getActorCode().equals(currentActorOption.actorTypeId)) {
			setPatientId(parms, repOrchestrationResponse.getPid().asString());
		}

		if (ActorType.DOCUMENT_RECIPIENT.getActorCode().equals(currentActorOption.actorTypeId)) {
			Pid pid = recOrchestrationResponse.getRegisterPid();
			if (pid != null)
				setPatientId(parms, pid.asString());
		}

		if (orchInit instanceof BuildRecTestOrchestrationButton) {
			BuildRecTestOrchestrationButton but = (BuildRecTestOrchestrationButton) orchInit;
			parms.put("format", ((BuildRecTestOrchestrationButton) orchInit).getFormat());
		}

		if (ActorType.REGISTRY.getActorCode().equals(currentActorOption.actorTypeId)) {
			setPatientId(parms, regOrchestrationResponse.getRegisterPid().asString());
		}

		if (getSiteToIssueTestAgainst() == null && !getOptionTabConfig(currentActorOption).isExternalStart()) {
			showPopupMessage("Test Setup must be initialized  [site=" + getSiteToIssueTestAgainst() +
			" actorOptionConfig=[" + currentActorOption + "]]");
			return parms;
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

	void setSrcOrchestrationResponse(SrcOrchestrationResponse srcOrchestrationResponse) {
		this.srcOrchestrationResponse = srcOrchestrationResponse;
		setOrchestrationResponse(srcOrchestrationResponse);
	}

	void setRegOrchestrationResponse(RegOrchestrationResponse regOrchestrationResponse) {
		this.regOrchestrationResponse = regOrchestrationResponse;
		setOrchestrationResponse(regOrchestrationResponse);
	}

	void setFhirSupportOrchestrationResponse(FhirSupportOrchestrationResponse fhirSupportOrchestrationResponse) {
		this.fhirSupportOrchestrationResponse = fhirSupportOrchestrationResponse;
		setOrchestrationResponse(fhirSupportOrchestrationResponse);
	}

	public void setOrchestrationResponse(AbstractOrchestrationResponse repOrchestrationResponse) {
		this.orchestrationResponse = repOrchestrationResponse;
	}

	public String getWindowShortName() {
		return "testloglisting";
	}

	@Override
	public SiteSpec getSiteToIssueTestAgainst() {
		return (siteToIssueTestAgainst == null ? new SiteSpec("gov/nist/toolkit/installation/shared", getTestSession()) : siteToIssueTestAgainst);
	}

	public void setSiteToIssueTestAgainst(SiteSpec siteToIssueTestAgainst) {
		this.siteToIssueTestAgainst = siteToIssueTestAgainst;
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
						showPopupMessage("System under test does not implement a " + actorType.getName());
				}
			};
		}
	}

	public ConformanceTestMainView getMainView() {
		return mainView;
	}

	@Override
	public ActorOptionConfig getCurrentActorOption() { return currentActorOption; }

	public TestSession getTestSession() {
		return new TestSession(getCurrentTestSession());
	}
}
