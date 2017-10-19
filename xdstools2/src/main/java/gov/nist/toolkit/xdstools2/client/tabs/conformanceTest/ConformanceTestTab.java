package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabBar;
import gov.nist.toolkit.actortransaction.client.ActorOption;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.IheItiProfile;
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
import gov.nist.toolkit.xdstools2.client.command.command.AutoInitConformanceTestingCommand;
import gov.nist.toolkit.xdstools2.client.command.command.DeleteSingleTestCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetAssignedSiteForTestSessionCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetSiteCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetStsSamlAssertionCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetTabConfigCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestCollectionsCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestsOverviewCommand;
import gov.nist.toolkit.xdstools2.client.command.command.RunTestCommand;
import gov.nist.toolkit.xdstools2.client.event.testContext.TestContextChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testContext.TestContextChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs.BuildIGTestOrchestrationButton;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.LaunchInspectorClickHandler;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.DeleteSingleTestRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetCollectionRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSiteRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetStsSamlAssertionRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTabConfigRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestsOverviewRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.RunTestRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.TestContext.NONE;

/**
 * All Conformance tests will be run out of here
 */
public class ConformanceTestTab extends ToolWindowWithMenu implements TestRunner, TestTarget, TestsHeaderView.Controller {

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

	private final ActorOptionConfig currentActorOption = new ActorOptionConfig("none");
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
	private Map<ActorOptionConfig, List<TestInstance>> testsPerActorOption = new HashMap<>();

	private ConformanceTestMainView mainView;
	private AbstractOrchestrationButton orchInit = null;

	public ConformanceTestTab() {
		super(10.0, 0.0);
		me = this;
		mainView = new ConformanceTestMainView(this);
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
				    if (mainView.getActorTabBar().getSelectedTab()>-1) {
						loadTestCollections();
						updateDisplayedActorAndOptionType();
					}
				}
			}
		});

		mainView.getActorTabBar().addSelectionHandler(new ActorSelectionHandler());
		mainView.getProfileTabBar().addSelectionHandler(new ProfileSelectionHandler());
		mainView.getOptionsTabBar().addSelectionHandler(new OptionSelectionHandler());

		mainView.getMenuImage().addStyleName("iconbutton");
		mainView.getMenuImage().setTitle("Index");
		mainView.getMenuImage().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				mainView.getTabBarPanel().setVisible(false);
				getMainView().getInitializationPanel().clear();
				getMainView().getTestsPanel().clear();
				mainView.getActorTabBar().selectTab(-1);
				mainView.getProfileTabBar().clear();
				mainView.getOptionsTabBar().clear();

				displayMenu(mainView.getTestsPanel());
			}
		});


		// 1. Get the tabConfig here {
		new GetTabConfigCommand() {
			@Override
			public void onComplete(TabConfig tabConfig) {
				ConformanceTestTab.super.tabConfig = tabConfig;
				// Retrofit tab config into actoroptionmanager



				// Initial load of tests in a test session
				loadTestCollections();
			}
		}.run(new GetTabConfigRequest("ConfTests"));
		// }

		// Register the Diagram RequestConnector clicked event handler
		ClientUtils.INSTANCE.getEventBus().addHandler(DiagramClickedEvent.TYPE, new DiagramPartClickedEventHandler() {
			@Override
			public void onClicked(TestInstance testInstance, InteractionDiagram.DiagramPart part) {
				if (InteractionDiagram.DiagramPart.RequestConnector.equals(part)
						|| InteractionDiagram.DiagramPart.ResponseConnector.equals(part)) {
					new LaunchInspectorClickHandler(testInstance, getCurrentTestSession(), new SiteSpec(testContext.getSiteName())).onClick(null);
				}
			}
		});

		// This is needed to only refresh the status index page
//		if (false) {
			ClientUtils.INSTANCE.getEventBus().addHandler(TestContextChangedEvent.TYPE, new TestContextChangedEventHandler() {
				@Override
				public void onTestContextChanged(TestContextChangedEvent event) {
					if (getInitTestSession()==null) {
//						displayMenu(mainView.getTestsPanel());
//						if (updateDisplayedActorAndOptionType()) { // . Check if currentactoroptin is properly set (ok if profile & option is null)
//							initializeTestDisplay(mainView.getTestsPanel());
//						}
					}
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

	private boolean showValidate() {
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
		updateTestStatistics(testsPerActorOption, testOverviewDTOs, testStatistics, actorOption);

		// Display testStatus with statistics
		testsHeaderView.allowRun(allowRun());
		testsHeaderView.update(testStatistics, currentActorTypeDescription + " - " + getCurrentOptionTitle());
	}

	private void updateTestStatistics(Map<ActorOptionConfig, List<TestInstance>> testsPerActorOption, Map<TestInstance, TestOverviewDTO> testOverviewDTOs, TestStatistics testStatistics, ActorOptionConfig actorOption) {
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

	public void setTestStatistics(final HTML statsBar,  final ActorOptionConfig actorOption) {
		final TestStatistics testStatistics = new TestStatistics();

	    final Map<TestInstance, TestOverviewDTO> myTestOverviewDTOs = new HashMap<>();
		final Map<ActorOptionConfig, List<TestInstance>> myTestsPerActorOption = new HashMap<>();

		String loadImgHtmlStr = "<img style=\"width:10px;height:14px;border:1px solid white;float:left;margin-right:2px;\" src=\"icons2/ajax-loader.gif\"/>";

		statsBar.setHTML(loadImgHtmlStr);

		actorOption.loadTests(new AsyncCallback<List<TestInstance>>() {
			@Override
			public void onFailure(Throwable throwable) {
				statsBar.setVisible(false);
				new PopupMessage("getTestStatistics: " + throwable.getMessage());
			}

			@Override
			public void onSuccess(List<TestInstance> testInstances) {
				myTestsPerActorOption.put(actorOption, testInstances);

				new GetTestsOverviewCommand() {
					@Override
					public void onComplete(List<TestOverviewDTO> testOverviews) {
						for (TestOverviewDTO testOverview : testOverviews) {
							myTestOverviewDTOs.put(testOverview.getTestInstance(), testOverview);
						}

						updateTestStatistics(myTestsPerActorOption, myTestOverviewDTOs, testStatistics, actorOption);
						String htmlStr = "<div style=\"width:10px;height:14px;border:1px solid;float:left;margin-right:2px;";
						if (testStatistics.getTestCount()>0 && testStatistics.getNotRun() != testStatistics.getTestCount()) { // Don't show anything if not run is 100%
							htmlStr += "border-color:black;\">\n";
							if (testStatistics.getSuccesses()==testStatistics.getTestCount()) {
								htmlStr += "<div style=\"background-color:cyan;height:100%\"></div>\n";
							} else if (testStatistics.getFailures()==testStatistics.getTestCount()) {
								htmlStr += "<div style=\"background-color:coral;height:100%\"></div>\n";
							} else {
								float ts[] = new float[3];
								ts[0] = (float)testStatistics.getNotRun() / (float)testStatistics.getTestCount();
								ts[1] = (float)testStatistics.getSuccesses() / (float)testStatistics.getTestCount();
								ts[2] = (float)testStatistics.getFailures() / (float)testStatistics.getTestCount();

								// Boost small values below $boostVal to make more visible
								float adjustedVal = 0.0F;
								float boostVal = .25F;
								for (int idx=0; idx < ts.length; idx++) {
									if (ts[idx]>0 && ts[idx]<boostVal) {
										adjustedVal  += (boostVal-ts[idx]);
										ts[idx] = boostVal;
									}
								}
								// Compensate for boosting from the majority index
								if (adjustedVal >0.0F) {
									int majorityIdx = -1;
									for (int idx = 0; idx < ts.length; idx++) {
										if (ts[idx] >= .33F) {
											if (majorityIdx==-1)  {
												majorityIdx = idx;
											} else {
												if (ts[idx]>ts[majorityIdx]) {
													majorityIdx=idx;
												}
											}
										}
									}
									if (majorityIdx>-1)
										ts[majorityIdx] -= adjustedVal ;
								}


								htmlStr +=
										((testStatistics.getNotRun() > 0) ?
												"<div style=\"background-color:white;height:" + ts[0]*100F + "%;\"></div>\n" : "") +
										(testStatistics.getSuccesses()>0?
												"<div style=\"background-color:cyan;height:" + ts[1]*100F  + "%;\"></div>\n"	:"") +
										(testStatistics.getFailures()>0?
												"<div style=\"background-color:coral;height:" + ts[2]*100F + "%;\"></div>\n" :"");
							}
							htmlStr += "</div>\n";
							statsBar.setHTML(htmlStr);
						} else {
							htmlStr += "border-color:white;\">\n";
							htmlStr += "<div style=\"background-color:white;height:100%\"></div>\n";
							statsBar.setHTML(htmlStr);
						}
					}
				}.run(new GetTestsOverviewRequest(getCommandContext(), testInstances));

			}
		});


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
		if (initTestSession != null) {
			setCurrentTestSession(initTestSession);
		}
		if (getCurrentTestSession() == null || getCurrentTestSession().equals("")) {
			testContextView.updateTestingContextDisplay();
			return;
		}
		new GetAssignedSiteForTestSessionCommand(){
			@Override
			public void onComplete(final String result) {
				testContext.setCurrentSiteSpec(result);
				testContextView.updateTestingContextDisplay();

				if (result == null) return;
				if (result.equals(NONE)) return;
				new GetSiteCommand(){
					@Override
					public void onFailure(Throwable throwable) {
						new PopupMessage("System " + result + " does not exist.");
						testContext.setCurrentSiteSpec(null);
						testContext.setSiteUnderTest(null);
						testContextView.updateTestingContextDisplay();
					}
					@Override
					public void onComplete(Site result) {
					    testContext.setSiteUnderTest(result);
						// Tool was launched via Activity URL
						if (getInitTestSession()!=null) {
							updateDisplayedActorAndOptionType();
							setInitTestSession(null);
						}
					}
				}.run(new GetSiteRequest(ClientUtils.INSTANCE.getCommandContext(),result));
			}
		}.run(getCommandContext());
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
				if (getInitTestSession()==null) {
					currentActorOption.setProfileId(null);
					currentActorOption.setOptionId("");
				}

				setCurrentActorTabConfig(newActorTypeId);
				refreshActorView(newActorTypeId);
		}
	}

	protected void setCurrentActorTabConfig(String newActorTypeId) {
		for (TabConfig tabConfig : ConformanceTestTab.super.tabConfig.getChildTabConfigs()) {
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
		mainView.getProfileTabBar().display(ConformanceTestTab.super.tabConfig, "Profiles", newActorTypeId);
		selectProfileTab();
		GWT.log("actor was refreshed. init session: " + (getInitTestSession()==null));
	}

	private class ProfileSelectionHandler implements SelectionHandler<Integer> {
		@Override
		public void onSelection(SelectionEvent<Integer> selectionEvent) {
			int i = selectionEvent.getSelectedItem();


			TabConfig profiles = currentActorOption.getTabConfig().getFirstChildTabConfig();
			if ("Profiles".equals(profiles.getLabel())) {
				TabConfig profile = profiles.getChildTabConfigs().get(i);
				currentActorOption.setProfileId(IheItiProfile.find(profile.getTcCode()));
				getMainView().getInitializationPanel().clear();
				getMainView().getTestsPanel().clear();
				mainView.getOptionsTabBar().clear();
				mainView.getOptionsTabBar().display(currentActorOption.getTabConfig(), "Options", profile.getTcCode());
				GWT.log("profile was selected. init session: " + (getInitTestSession()==null));
				if (getInitTestSession()==null) {
					currentActorOption.setOptionId(null);
				}
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

			GWT.log("option was selected. init session: " + (getInitTestSession()==null));

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
		currentActorTypeDescription = getDescriptionForTestCollection(currentActorOption.actorTypeId);

		int idx=0;
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

			if (currentActorOption.getTabConfig()==null)
				setCurrentActorTabConfig(currentActorOption.getActorTypeId());

		    refreshActorView(currentActorOption.getActorTypeId());
		}

		return foundSelectedActorTab;

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
		// TabBar listing actor types
		new GetTestCollectionsCommand() {
			@Override
			public void onComplete(List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs) {

			    // Sort according to the user defined tab config
				me.testCollectionDefinitionDAOs =  new ArrayList<>();
				for (TabConfig tabConfig : ConformanceTestTab.super.tabConfig.getChildTabConfigs()) {
					for (TestCollectionDefinitionDAO tcd : testCollectionDefinitionDAOs) {
					    if (tabConfig.getTcCode().equals(new ActorOption(tcd.getCollectionID()).actorTypeId)) {
					    	me.testCollectionDefinitionDAOs.add(tcd);
					    	break;
						}
					}
				}


				if (getInitTestSession()!=null) {
					getMainView().getTabBarPanel().setVisible(true);
				}


				displayActorsTabBar(mainView.getActorTabBar());
				// 2. Write the site map here

				if (getInitTestSession()==null && mainView.getActorTabBar()!=null && mainView.getActorTabBar().getSelectedTab()==-1) { // Only display the menu when actor is not selected.
						displayMenu(mainView.getTestsPanel());
				}


				currentActorTypeDescription = getDescriptionForTestCollection(currentActorOption.actorTypeId);
//				updateTestsOverviewHeader();

				// This is a little wierd being here. This depends on initTestSession
				// which is set AFTER onTabLoad is run so run here - later in the initialization
				// initTestSession is set from ConfActorActivity
				initializeTestingContext();
			}
		}.run(new GetCollectionRequest(getCommandContext(), "actorcollections"));
	}

	@Override
	public void onMenuSelect(TabConfig actor, Map<String,TabConfig> target) {
		mainView.getTabBarPanel().setVisible(true);

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

	private HTML loadingMessage;

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
				else {
					displayTestCollection(getMainView().getTestsPanel());
				}
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
				new PopupMessage("getTestlogListing: " + throwable.getMessage());
			}

			@Override
			public void onSuccess(List<TestInstance> testInstances) {
				mainView.showLoadingMessage("Loading...");
				displayTests(testsPanel, testInstances, allowRun());
				mainView.clearLoadingMessage();
			}
		});
	}


	private void displayTests(final Panel testsPanel, List<TestInstance> testInstances, boolean allowRun) {
		final Map<String, String> parms = initializeTestParameters();
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
					InteractionDiagramDisplay diagramDisplay = new InteractionDiagramDisplay(
							testOverview,
							testContext.getTestSession(),
							getSiteToIssueTestAgainst(),
							((testContext.getSiteUnderTestAsSiteSpec() != null) ? testContext.getSiteUnderTestAsSiteSpec().getName() : ""),
							currentActorOption,
							getTestInstancePatientId(testOverview.getTestInstance(), parms));
					TestDisplay testDisplay = testDisplayGroup.display(testOverview, diagramDisplay);
					testsPanel.add(testDisplay.asWidget());
				}
				updateTestsOverviewHeader(testsPerActorOption, testOverviewDTOs, testStatistics, currentActorOption);

				mainView.clearLoadingMessage();

			}
		}.run(new GetTestsOverviewRequest(getCommandContext(), testInstances));
	}

	private static String getPatientIdStr(Map<String, String> parms) {
		return (parms!=null)?parms.get("$patientid$"):null;
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
		else {
			if (testContext.getSiteUnderTest() != null)
				siteToIssueTestAgainst = testContext.getSiteUnderTestAsSiteSpec();
		}
	}

	private void displayActorsTabBar(TabBar actorTabBar) {
		if (actorTabBar.getTabCount() == 0) {
			for (TestCollectionDefinitionDAO def : testCollectionDefinitionDAOs) {
				if (!def.isOption())
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
			new PopupMessage("Test Id needs be suffixed with '_Xuausername': TestId_Xuausername.");
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
						InteractionDiagramDisplay diagramDisplay = new InteractionDiagramDisplay(testOverviewDTO, testContext.getTestSession(), getSiteToIssueTestAgainst(), testContext.getSiteUnderTestAsSiteSpec().getName(),actorOption,null);
						testDisplayGroup.display(testOverviewDTO, diagramDisplay);
						updateTestsOverviewHeader(testsPerActorOption, testOverviewDTOs, testStatistics, actorOption);
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
		final String patientId = getTestInstancePatientId(sectionInstance, parms);

		if (parms == null) return;

		try {
			new RunTestCommand(){
				@Override
				public void onComplete(TestOverviewDTO testOverviewDTO) {
					InteractionDiagramDisplay diagramDisplay = new InteractionDiagramDisplay(testOverviewDTO, testContext.getTestSession(), getSiteToIssueTestAgainst(), testContext.getSiteUnderTestAsSiteSpec().getName(),currentActorOption,patientId);
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
			new PopupMessage(e.getMessage());
		}
	}




	public void runTest(final TestInstance testInstance, final Map<String, String> sectionParms, final TestIterator testIterator) {

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

							runTestInstance(testInstance, null, null);
						}
					}.run(new GetStsSamlAssertionRequest(getCommandContext(),xuaUsername,stsTestInstance,stsSpec,params));
				} catch (Exception ex) {
					new PopupMessage("runTestInstance: Client call failed: getStsSamlAssertion: " + ex.toString());
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
				    InteractionDiagramDisplay diagramDisplay = new InteractionDiagramDisplay(testOverviewDTO, testContext.getTestSession(), getSiteToIssueTestAgainst(), testContext.getSiteUnderTestAsSiteSpec().getName(),currentActorOption,patientId);
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
			new PopupMessage(e.getMessage());
		}

	}

	private String setPatientId(Map<String, String> parms, String patientId) {
		return parms.put("$patientid$",patientId);
	}

	private String getTestInstancePatientId(TestInstance testInstance, Map<String, String> parms) {
		if (ActorType.REPOSITORY.getShortName().equals(currentActorOption.actorTypeId)) {
			return testInstance.getId() + "_" + getPatientIdStr(parms);
		} else
			return getPatientIdStr(parms);
	}

	private Map<String, String> initializeTestParameters() {
		Map<String, String> parms = new HashMap<>();

		if (orchestrationResponse == null) {
			new PopupMessage("Initialize Test Environment before running tests.");
			return null;
		}

		if (ActorType.REPOSITORY.getShortName().equals(currentActorOption.actorTypeId)) {
			setPatientId(parms, repOrchestrationResponse.getPid().asString());
		}

		if (ActorType.DOCUMENT_RECIPIENT.getShortName().equals(currentActorOption.actorTypeId)) {
			setPatientId(parms, recOrchestrationResponse.getRegisterPid().asString());
		}

		if (ActorType.REGISTRY.getShortName().equals(currentActorOption.actorTypeId)) {
			setPatientId(parms, regOrchestrationResponse.getRegisterPid().asString());
		}

		if (getSiteToIssueTestAgainst() == null && !currentActorOption.getTabConfig().isExternalStart()) {
			new PopupMessage("Test Setup must be initialized  [site=" + getSiteToIssueTestAgainst() +
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

	@Override
	public SiteSpec getSiteToIssueTestAgainst() {
		return siteToIssueTestAgainst;
	}

	public void setSiteToIssueTestAgainst(SiteSpec siteToIssueTestAgainst) {
		this.siteToIssueTestAgainst = siteToIssueTestAgainst;
	}

	public void setInitTestSession(String initTestSession) {
		this.initTestSession = initTestSession;
	}

	public String getInitTestSession() {
		return initTestSession;
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

	@Override
	public ActorOptionConfig getCurrentActorOption() { return currentActorOption; }

}
