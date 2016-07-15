package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.SectionOverviewDTO;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.List;

public class TestLogListingTab extends GenericQueryTab {
	final protected ToolkitServiceAsync toolkitService = GWT
			.create(ToolkitService.class);

	FlowPanel layout = new FlowPanel();

	public TestLogListingTab() {
		super(new GetDocumentsSiteActorManager());
	}

	@Override
	public String getTitle() { return "TestLog Listing"; }

	@Override
	public void onTabLoad(boolean select, String eventName) {

		registerTab(select, eventName);

		tabTopPanel.add(layout);

		Xdstools2.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
			@Override
			public void onTestSessionChanged(TestSessionChangedEvent event) {
				if (event.changeType == TestSessionChangedEvent.ChangeType.SELECT)
					loadStackPanel();
			}
		});

//		loadGrid();
		loadStackPanel();
	}

	static final int TEST_NAME_COL = 0;
	static final int SECTION_NAME_COL = 1;
	static final int STATUS_COL = 2;

	void loadStackPanel() {
		layout.clear();
		toolkitService.getTestlogListing(getCurrentTestSession(), new AsyncCallback<List<TestInstance>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getTestlogListing: " + caught.getMessage());
			}

			public void onSuccess(List<TestInstance> testInstances) {

				for (TestInstance testInstance : testInstances) {
//					stackPanel.setHeight((40*testInstances.size() + 200)+"px");

					toolkitService.getLogContent(getCurrentTestSession(), testInstance, new AsyncCallback<TestOverviewDTO>() {

						public void onFailure(Throwable caught) {
							new PopupMessage("getLogContent: " + caught.getMessage());
						}

						public void onSuccess(TestOverviewDTO testOverview) {
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
							for (String sectionName : testOverview.getSectionNames()) {
								SectionOverviewDTO sectionOverview = testOverview.getSectionOverview(sectionName);

								HorizontalFlowPanel sHeader = new HorizontalFlowPanel();
								HTML sectionLabel = new HTML("Section: " + sectionName);
								if (sectionOverview.isPass())
									sHeader.addStyleName("testOverviewHeaderSuccess");
								else
									sHeader.addStyleName("testOverviewHeaderFail");
								sHeader.add(sectionLabel);
								sHeader.add((sectionOverview.isPass()) ?
										new Image("icons/ic_done_black_24dp_1x.png")
										:
										new Image("icons/ic_warning_black_24dp_1x.png"));
								DisclosurePanel sPanel = new DisclosurePanel(sHeader);
								sPanel.add(new HTML("Blah Blah Blah"));

								HorizontalFlowPanel row = new HorizontalFlowPanel();
								row.add(sPanel);

								body.add(row);
							}

							DisclosurePanel panel = new DisclosurePanel(header);
							panel.setWidth("100%");
							panel.add(body);
							layout.add(panel);
						}

					});
				}
			}

		});
	}


	public String getWindowShortName() {
		return "testloglisting";
	}

}
