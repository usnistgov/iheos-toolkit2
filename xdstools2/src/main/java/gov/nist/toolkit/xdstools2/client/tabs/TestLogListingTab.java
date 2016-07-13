package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testenginelogging.client.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.List;

public class TestLogListingTab extends GenericQueryTab {
	final protected ToolkitServiceAsync toolkitService = GWT
			.create(ToolkitService.class);

	FlexTable grid = new FlexTable();
	StackLayoutPanel stackPanel = new StackLayoutPanel(Style.Unit.EM);
	boolean isPrivateTesting = false;


	public TestLogListingTab() {
		super(new GetDocumentsSiteActorManager());
	}

	@Override
	public void onTabLoad(boolean select, String eventName) {

		registerTab(select, eventName);

		HTML title = new HTML();
		title.setHTML("<h2>TestLog Listing</h2>");
		tabTopPanel.add(title);

		tabTopPanel.add(grid);
		tabTopPanel.add(stackPanel);

		Xdstools2.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
			@Override
			public void onTestSessionChanged(TestSessionChangedEvent event) {
				if (event.changeType == TestSessionChangedEvent.ChangeType.SELECT)
					loadGrid();
			}
		});

//		loadGrid();
		loadStackPanel();
	}

	static final int TEST_NAME_COL = 0;
	static final int SECTION_NAME_COL = 1;
	static final int STATUS_COL = 2;

	void loadStackPanel() {
		stackPanel.clear();
		toolkitService.getTestlogListing(getCurrentTestSession(), new AsyncCallback<List<TestInstance>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getTestlogListing: " + caught.getMessage());
			}

			public void onSuccess(List<TestInstance> testInstances) {


				for (TestInstance testInstance : testInstances) {
					toolkitService.getLogContent(getCurrentTestSession(), testInstance, new AsyncCallback<TestOverviewDTO>() {

						public void onFailure(Throwable caught) {
							new PopupMessage("getLogsForTest: " + caught.getMessage());
						}

						public void onSuccess(TestOverviewDTO testOverview) {
							FlowPanel header = new FlowPanel();
							header.add(new HTML(testOverview.getName()));
							header.add((testOverview.isPass()) ?
									new Image("icons/ic_done_black_24dp_1x.png")
									:
									new Image("icons/ic_warning_black_24dp_1x.png"));

							FlowPanel body = new FlowPanel();
							for (String sectionName : testOverview.getSectionNames()) {
								FlowPanel row = new FlowPanel();
								row.add(new HTML(sectionName));
								row.add((testOverview.getSectionOverview(sectionName).isPass()) ?
										new Image("icons/ic_done_black_24dp_1x.png")
										:
										new Image("icons/ic_warning_black_24dp_1x.png"));
								body.add(row);
							}

							stackPanel.add(body, header, 4);
						}

					});
				}
			}

		});
	}

	void loadGrid() {
		grid.clear();
		toolkitService.getTestlogListing(getCurrentTestSession(), new AsyncCallback<List<TestInstance>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getTestlogListing: " + caught.getMessage());
			}

			public void onSuccess(List<TestInstance> testInstances) {


				for (TestInstance testInstance : testInstances) {
					toolkitService.getLogContent(getCurrentTestSession(), testInstance, new AsyncCallback<TestOverviewDTO>() {

						public void onFailure(Throwable caught) {
							new PopupMessage("getLogsForTest: " + caught.getMessage());
						}

						public void onSuccess(TestOverviewDTO testOverview) {
							int row = grid.getRowCount();
							grid.setWidget(row, TEST_NAME_COL, new HTML(testOverview.getName()));
							grid.setWidget(row, STATUS_COL, (testOverview.isPass()) ?
									new Image("icons/ic_done_black_24dp_1x.png")
									:
									new Image("icons/ic_warning_black_24dp_1x.png")
							);
							for (String sectionName : testOverview.getSectionNames()) {
								row++;
								grid.setWidget(row, SECTION_NAME_COL, new HTML(sectionName));
								grid.setWidget(row, STATUS_COL,
										(testOverview.getSectionOverview(sectionName).isPass()) ?
												new Image("icons/ic_done_black_24dp_1x.png")
												:
												new Image("icons/ic_warning_black_24dp_1x.png")
								);
							}
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
