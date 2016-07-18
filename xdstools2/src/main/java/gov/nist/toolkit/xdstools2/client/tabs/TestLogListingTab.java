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

	private TestLogListingTab me;
	private final FlowPanel actorPanel = new FlowPanel();
	private final FlowPanel testsPanel = new FlowPanel();
	private final TabBar tabBar = new TabBar();

	// Testable actors
	private List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs;
	// testId ==> overview
	private final Map<String, TestOverviewDTO> testOverviews = new HashMap<>();

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
				if (event.changeType == TestSessionChangedEvent.ChangeType.SELECT) {
					loadTestCollections("reg");
				}
			}
		});

		tabBar.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> selectionEvent) {
				int i = selectionEvent.getSelectedItem();
				loadTestCollection(testCollectionDefinitionDAOs.get(i).getCollectionID());
			}
		});

		loadTestCollections("reg");

	}

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

	private void loadTestCollections(String collectionName) {
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

//		loadTestCollection(collectionName);
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
					new Image("icons2/correct-32.png")
					:
					new Image("icons2/cancel-32.png"));
		}
		Image play = new Image("icons2/play-32.png");
		play.setTitle("Run");
		header.add(play);
		Image delete = new Image("icons2/remove-32.png");
		delete.setTitle("Delete Log");
		header.add(delete);

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
