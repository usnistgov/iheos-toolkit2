package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.SectionOverviewDTO;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.event.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.tabs.TestDisclosureManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestLogListingTab extends ToolWindow {
	final protected ToolkitServiceAsync toolkitService = GWT
			.create(ToolkitService.class);

	private final TestLogListingTab me;
	private final FlowPanel toolPanel = new FlowPanel();
	private final FlowPanel testsPanel = new FlowPanel();
	private final TabBar tabBar = new TabBar();
	private final FlowPanel sitesPanel = new FlowPanel();
	private final TestDisclosureManager testDisclosureManager = new TestDisclosureManager();
	private String currentActorTypeName;
	private String currentSiteName = null;

	// Testable actors
	private List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs;
	// testId ==> overview
	private final Map<String, TestOverviewDTO> testOverviews = new HashMap<>();

	public TestLogListingTab() {
		me = this;
		toolPanel.add(sitesPanel);
		toolPanel.add(tabBar);
		toolPanel.add(testsPanel);
	}

	@Override
	public String getTitle() { return "Conformance Tests"; }

	@Override
	public void onTabLoad(boolean select, String eventName) {

		registerTab(select, eventName);

		tabTopPanel.add(toolPanel);

		Xdstools2.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
			@Override
			public void onTestSessionChanged(TestSessionChangedEvent event) {
				if (event.changeType == TestSessionChangedEvent.ChangeType.SELECT) {
					loadTestCollections();
				}
			}
		});

		buildSiteSelector();

		tabBar.addSelectionHandler(actorSelectionHandler);

		loadTestCollections();
	}

	private SelectionHandler<Integer> actorSelectionHandler = new SelectionHandler<Integer>() {
		@Override
		public void onSelection(SelectionEvent<Integer> selectionEvent) {
			int i = selectionEvent.getSelectedItem();
			currentActorTypeName = testCollectionDefinitionDAOs.get(i).getCollectionID();
			loadTestCollection(currentActorTypeName);
		}
	};

	private void buildSiteSelector() {
		SiteSelectionComponent siteSelectionComponent = new SiteSelectionComponent(null, getCurrentTestSession());
		siteSelectionComponent.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> siteSelected) {
				currentSiteName = siteSelected.getValue();
				new PopupMessage("Site " + currentSiteName + " selected.");
			}
		});
		sitesPanel.add(siteSelectionComponent.asWidget());
	}

	private ValueChangeHandler siteChangeHandler = new ValueChangeHandler() {
		@Override
		public void onValueChange(ValueChangeEvent valueChangeEvent) {

		}
	};

	private String getSelectedTestCollection() {
		int selected = tabBar.getSelectedTab();
		if (selected < testCollectionDefinitionDAOs.size()) {
			String name = testCollectionDefinitionDAOs.get(selected).getCollectionID();
			return name;
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

	private void loadTestCollections() {
		// TabBar listing actor types
		toolkitService.getTestCollections("actorCollections", new AsyncCallback<List<TestCollectionDefinitionDAO>>() {
			@Override
			public void onFailure(Throwable throwable) { new PopupMessage("getTestCollections: " + throwable.getMessage()); }

			@Override
			public void onSuccess(List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs) {
				me.testCollectionDefinitionDAOs = testCollectionDefinitionDAOs;
				displayTestCollectionsTabBar();
			}
		});
	}

	private void loadTestCollection(final String collectionName) {
		toolkitService.getCollectionMembers("actorcollections", collectionName, new AsyncCallback<List<String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getTestlogListing: " + caught.getMessage());
			}

			public void onSuccess(List<String> testIds) {
				testsPanel.clear();
				List<TestInstance> testInstances = new ArrayList<>();
				for (String testId : testIds) testInstances.add(new TestInstance(testId));
				toolkitService.getTestsOverview(getCurrentTestSession(), testInstances, new AsyncCallback<List<TestOverviewDTO>>() {

					public void onFailure(Throwable caught) {
						new PopupMessage("getTestOverview: " + caught.getMessage());
					}

					public void onSuccess(List<TestOverviewDTO> testOverviews) {
						testFlowPanels.clear();
						for (TestOverviewDTO testOverview : testOverviews) {
							me.testOverviews.put(testOverview.getName(), testOverview);
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

	private Map<String, HorizontalFlowPanel> testHeaders = new HashMap<>();
	// one panel per test
	private Map<String, FlowPanel> testBodies = new HashMap<>();

	private void displayTest(TestOverviewDTO testOverview) {
		HorizontalFlowPanel header = new HorizontalFlowPanel();
		testHeaders.put(testOverview.getName(), header);
		if (testOverview.isRun()) {
			if (testOverview.isPass())
				header.setBackgroundColorSuccess();
			else
				header.setBackgroundColorFailure();
		} else
			header.setBackgroundColorNotRun();
		header.fullWidth();
		HTML testHeader = new HTML("Test: " + testOverview.getName() + " - " +testOverview.getTitle());
		testHeader.addStyleName("test-title");
		header.add(testHeader);
		if (testOverview.isRun()) {
			Image status = (testOverview.isPass()) ?
					new Image("icons2/correct-24.png")
					:
					new Image("icons2/cancel-24.png");
			status.addStyleName("right");
			header.add(status);
		}
		Image play = new Image("icons2/play-24.png");
		play.setTitle("Run");
		header.add(play);
		Image delete = new Image("icons2/garbage-24.png");
		delete.setTitle("Delete Log");
		header.add(delete);

		FlowPanel body = new FlowPanel();

		body.add(new HTML(testOverview.getDescription()));

		displaySections(testOverview, body);

		DisclosurePanel panel = new DisclosurePanel(header);
		testDisclosureManager.add(testOverview.getName(), panel);
		panel.setWidth("100%");
		panel.add(body);
		testsPanel.add(panel);
	}

	private void displaySections(TestOverviewDTO testOverview, FlowPanel parent) {
		parent.clear();
		for (String sectionName : testOverview.getSectionNames()) {
			SectionOverviewDTO sectionOverview = testOverview.getSectionOverview(sectionName);
			parent.add(new TestSectionComponent(toolkitService, getCurrentTestSession(), new TestInstance(testOverview.getName()), sectionOverview).asWidget());
		}
	}

	private void runTest(final TestInstance testInstance, SiteSpec siteSpec) {
		Map<String, String> parms = new HashMap<>();
		parms.put("$patientid$", pidTextBox.getValue().trim());
		List<String> selectedSections = new ArrayList<>();

		try {
			toolkitService.runTest(getCurrentTestSession(), siteSpec, testInstance, selectedSections, parms, true, new AsyncCallback<TestOverviewDTO>() {
				@Override
				public void onFailure(Throwable throwable) {
					new PopupMessage(throwable.getMessage());
				}

				@Override
				public void onSuccess(TestOverviewDTO testOverviewDTO) {
					HorizontalFlowPanel header = testHeaders.get(testInstance.getId());
					if (testOverviewDTO.isPass())
						header.setBackgroundColorSuccess();
					else
						header.setBackgroundColorFailure();

				}
			});
		} catch (Exception e) {
			new PopupMessage(e.getMessage());
		}

	}



	public String getWindowShortName() {
		return "testloglisting";
	}
}
