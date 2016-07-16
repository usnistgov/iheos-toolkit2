package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.SectionOverviewDTO;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.List;

public class TestLogListingTab extends GenericQueryTab {
	final protected ToolkitServiceAsync toolkitService = GWT
			.create(ToolkitService.class);

	TestLogListingTab me;
	FlowPanel layout = new FlowPanel();
	List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs;

	public TestLogListingTab() {
		super(new GetDocumentsSiteActorManager());
		me = this;
	}

	@Override
	public String getTitle() { return "TestLog Listing"; }

	@Override
	public void onTabLoad(boolean select, String eventName) {

		registerTab(select, eventName);

		tabTopPanel.add(layout);

		toolkitService.getTestCollections("actorCollections", new AsyncCallback<List<TestCollectionDefinitionDAO>>() {
			@Override
			public void onFailure(Throwable throwable) { new PopupMessage("getTestCollections: " + throwable.getMessage()); }

			@Override
			public void onSuccess(List<TestCollectionDefinitionDAO> testCollectionDefinitionDAOs) {
				me.testCollectionDefinitionDAOs = testCollectionDefinitionDAOs;
				TabBar tabBar = new TabBar();
				for (TestCollectionDefinitionDAO def : testCollectionDefinitionDAOs) {
					tabBar.addTab(def.getCollectionTitle());
				}
				layout.add(tabBar);
			}
		});

		Xdstools2.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
			@Override
			public void onTestSessionChanged(TestSessionChangedEvent event) {
				if (event.changeType == TestSessionChangedEvent.ChangeType.SELECT)
					load("reg");
			}
		});

		load("reg");

	}

	static final int TEST_NAME_COL = 0;
	static final int SECTION_NAME_COL = 1;
	static final int STATUS_COL = 2;

	void load(String collectionName) {
		layout.clear();

		toolkitService.getCollectionMembers("actorcollections", collectionName, new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable throwable) { new PopupMessage("getCollectionMembers: " + throwable.getMessage()); }

			@Override
			public void onSuccess(final List<String> definedTestIds) {
				toolkitService.getTestlogListing(getCurrentTestSession(), new AsyncCallback<List<TestInstance>>() {

					public void onFailure(Throwable caught) {
						new PopupMessage("getTestlogListing: " + caught.getMessage());
					}

					public void onSuccess(List<TestInstance> testInstances) {

						for (TestInstance testInstance : testInstances) {
							if (!definedTestIds.contains(testInstance.getId())) continue;
							toolkitService.getLogContent(getCurrentTestSession(), testInstance, new AsyncCallback<TestOverviewDTO>() {

								public void onFailure(Throwable caught) {
									new PopupMessage("getLogContent: " + caught.getMessage());
								}

								public void onSuccess(TestOverviewDTO testOverview) {
									buildTest(testOverview);
								}

							});
						}
					}

				});

			}
		});

	}

	private void buildTest(TestOverviewDTO testOverview) {
		HorizontalFlowPanel header = new HorizontalFlowPanel();
		if (testOverview.isPass())
            header.setBackgroundColorSuccess();
        else
            header.setBackgroundColorFailure();
		header.fullWidth();
		header.add(new HTML("Test: " + testOverview.getName()));
		header.add(new HTML(testOverview.getTitle()));
		header.add((testOverview.isPass()) ?
                new Image("icons/ic_done_black_24dp_1x.png")
                :
                new Image("icons/ic_warning_black_24dp_1x.png"));

		FlowPanel body = new FlowPanel();

		body.add(new HTML(testOverview.getDescription()));

		buildSection(testOverview, body);

		DisclosurePanel panel = new DisclosurePanel(header);
		panel.setWidth("100%");
		panel.add(body);
		layout.add(panel);
	}

	private void buildSection(TestOverviewDTO testOverview, FlowPanel parent) {
		for (String sectionName : testOverview.getSectionNames()) {
            SectionOverviewDTO sectionOverview = testOverview.getSectionOverview(sectionName);

            HorizontalFlowPanel header = new HorizontalFlowPanel();
            HTML sectionLabel = new HTML("Section: " + sectionName);
            if (sectionOverview.isPass())
                header.addStyleName("testOverviewHeaderSuccess");
            else
                header.addStyleName("testOverviewHeaderFail");
            header.add(sectionLabel);
            header.add((sectionOverview.isPass()) ?
                    new Image("icons/ic_done_black_24dp_1x.png")
                    :
                    new Image("icons/ic_warning_black_24dp_1x.png"));
            DisclosurePanel panel = new DisclosurePanel(header);
            panel.add(new HTML("Blah Blah Blah"));

            HorizontalFlowPanel body = new HorizontalFlowPanel();
            body.add(panel);

            parent.add(body);
        }
	}


	public String getWindowShortName() {
		return "testloglisting";
	}

}
