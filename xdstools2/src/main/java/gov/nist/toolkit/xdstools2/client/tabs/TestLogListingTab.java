package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.SectionOverviewDTO;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.components.TestSectionComponent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestLogListingTab extends GenericQueryTab {
	final protected ToolkitServiceAsync toolkitService = GWT
			.create(ToolkitService.class);

	TestLogListingTab me;
	final FlowPanel actorPanel = new FlowPanel();
	final FlowPanel testsPanel = new FlowPanel();
	final TabBar tabBar = new TabBar();

	// Testable actors
	List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs;
	// testId ==> overview
	final Map<String, TestOverviewDTO> testOverviews = new HashMap<>();

	public TestLogListingTab() {
		super(new GetDocumentsSiteActorManager());
		me = this;
		actorPanel.add(tabBar);
		actorPanel.add(testsPanel);
	}

	@Override
	public String getTitle() { return "TestLog Listing"; }

	@Override
	public void onTabLoad(boolean select, String eventName) {

		registerTab(select, eventName);

		tabTopPanel.add(actorPanel);

		Xdstools2.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
			@Override
			public void onTestSessionChanged(TestSessionChangedEvent event) {
				if (event.changeType == TestSessionChangedEvent.ChangeType.SELECT)
					load("reg");
			}
		});

		tabBar.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> selectionEvent) {
				int i = selectionEvent.getSelectedItem().intValue();
				displayTestCollection(testCollectionDefinitionDAOs.get(i).getCollectionID());
			}
		});

		load("reg");

	}

	static final int TEST_NAME_COL = 0;
	static final int SECTION_NAME_COL = 1;
	static final int STATUS_COL = 2;

	void load(String collectionName) {
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

		displayTestCollection(collectionName);

	}

	private void displayTestCollection(final  String collectionName) {
		toolkitService.getCollectionMembers("actorcollections", collectionName, new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable throwable) { new PopupMessage("getCollectionMembers: " + throwable.getMessage()); }

			@Override
			public void onSuccess(final List<String> definedTestIds) {
				toolkitService.getCollectionMembers("actorcollections", collectionName, new AsyncCallback<List<String>>() {

					public void onFailure(Throwable caught) {
						new PopupMessage("getTestlogListing: " + caught.getMessage());
					}

					public void onSuccess(List<String> testIds) {
						testsPanel.clear();
						List<TestInstance> testInstances = new ArrayList<>();
						for (String testId : testIds) testInstances.add(new TestInstance(testId));
							toolkitService.getLogsContent(getCurrentTestSession(), testInstances, new AsyncCallback<List<TestOverviewDTO>>() {

								public void onFailure(Throwable caught) {
									new PopupMessage("getLogContent: " + caught.getMessage());
								}

								public void onSuccess(List<TestOverviewDTO> testOverviews) {
									for (TestOverviewDTO testOverview : testOverviews) {
										me.testOverviews.put(testOverview.getName(), testOverview);
										displayTest(testOverview);
									}
								}

							});
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

	private void displayTest(TestOverviewDTO testOverview) {
		HorizontalFlowPanel header = new HorizontalFlowPanel();
		if (testOverview.isRun()) {
			if (testOverview.isPass())
				header.setBackgroundColorSuccess();
			else
				header.setBackgroundColorFailure();
		} else
			header.setBackgroundColorNotRun();
		header.fullWidth();
		header.add(new HTML("Test: " + testOverview.getName()));
		header.add(new HTML(testOverview.getTitle()));
		if (testOverview.isRun()) {
			header.add((testOverview.isPass()) ?
					new Image("icons/ic_done_black_24dp_1x.png")
					:
					new Image("icons/ic_warning_black_24dp_1x.png"));
		}
		FlowPanel body = new FlowPanel();

		body.add(new HTML(testOverview.getDescription()));

		displaySections(testOverview, body);

		DisclosurePanel panel = new DisclosurePanel(header);
		panel.setWidth("100%");
		panel.add(body);
		testsPanel.add(panel);
	}

	private void displaySections(TestOverviewDTO testOverview, FlowPanel parent) {
		for (String sectionName : testOverview.getSectionNames()) {
            SectionOverviewDTO sectionOverview = testOverview.getSectionOverview(sectionName);

            parent.add(new TestSectionComponent(sectionOverview).asWidget());
        }
	}

	public String getWindowShortName() {
		return "testloglisting";
	}
}
