package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import gov.nist.toolkit.results.client.TestInstance;
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

	StackLayoutPanel stackPanel = new StackLayoutPanel(Style.Unit.EM);

	public TestLogListingTab() {
		super(new GetDocumentsSiteActorManager());
	}

	@Override
	public String getTitle() { return "TestLog Listing"; }

	@Override
	public void onTabLoad(boolean select, String eventName) {

		registerTab(select, eventName);

		useRawPanel(stackPanel);

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
		stackPanel.clear();
		toolkitService.getTestlogListing(getCurrentTestSession(), new AsyncCallback<List<TestInstance>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getTestlogListing: " + caught.getMessage());
			}

			public void onSuccess(List<TestInstance> testInstances) {

				for (TestInstance testInstance : testInstances) {
					toolkitService.getLogContent(getCurrentTestSession(), testInstance, new AsyncCallback<TestOverviewDTO>() {

						public void onFailure(Throwable caught) {
							new PopupMessage("getLogContent: " + caught.getMessage());
						}

						public void onSuccess(TestOverviewDTO testOverview) {
							HorizontalFlowPanel header = new HorizontalFlowPanel();
							header.add(new HTML("Test: " + testOverview.getName()));
							header.add(new HTML(testOverview.getTitle()));
							header.add((testOverview.isPass()) ?
									new Image("icons/ic_done_black_24dp_1x.png")
									:
									new Image("icons/ic_warning_black_24dp_1x.png"));

							FlowPanel body = new FlowPanel();
							for (String sectionName : testOverview.getSectionNames()) {
								HorizontalFlowPanel row = new HorizontalFlowPanel();
								row.add(new HTML("Section: " + sectionName));
								row.add((testOverview.getSectionOverview(sectionName).isPass()) ?
										new Image("icons/ic_done_black_24dp_1x.png")
										:
										new Image("icons/ic_warning_black_24dp_1x.png"));
								body.add(row);
							}

							stackPanel.add(body, header, 3);
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
