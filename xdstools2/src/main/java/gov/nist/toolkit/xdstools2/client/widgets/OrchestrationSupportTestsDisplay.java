package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.services.client.AbstractOrchestrationResponse;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.*;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.List;

/**
 * Display results of running tests that initialize SUT or orchestration support actors
 */
public class OrchestrationSupportTestsDisplay extends FlowPanel {

//    public OrchestrationSupportTestsDisplay(final AbstractOrchestrationResponse orchResponse, final String testSession, final SiteSpec siteSpec) {
    public OrchestrationSupportTestsDisplay(final AbstractOrchestrationResponse orchResponse, final TestContext testContext, final TestContextDisplay testContextDisplay, final TestRunner testRunner) {
        ClientUtils.INSTANCE.getToolkitServices().getTestsOverview(testContext.getTestSession(), orchResponse.getTestInstances(), new AsyncCallback<List<TestOverviewDTO>>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("getTestOverview: " + caught.getMessage());
            }

            public void onSuccess(List<TestOverviewDTO> testOverviews) {
                add(new HTML("Utilities run to initialize environment"));
                TestDisplayGroup orchGroup = new TestDisplayGroup(testContext, testContextDisplay, testRunner);
                orchGroup.setAllowRun(false);
                orchGroup.setAllowDelete(false);
                for (TestOverviewDTO testOverview : testOverviews) {
                    TestDisplay testDisplay = orchGroup.display(testOverview);
                    add(testDisplay);
                }
//                for (TestOverviewDTO testOverview : testOverviews) {
//                    MessageItem item = orchResponse.getItemForTest(testOverview.getTestInstance());
//                    HorizontalFlowPanel orchTest = new HorizontalFlowPanel();
//                    orchTest.getElement().getStyle().setHeight(32, Style.Unit.PX);
//                    orchTest.add(new HTML(testOverview.getName() + " - " + testOverview.getTitle()));
//                    Image inspect = new Image("icons2/visible-32.png");
//                    inspect.addStyleName("right");
//                    inspect.addClickHandler(new LaunchInspectorClickHandler(testOverview.getTestInstance(), testSession, siteSpec));
////                    inspect.addClickHandler(testTab.getInspectClickHandler(testOverview.getTestInstance()));
//                    inspect.setTitle("Inspect results");
//
//                    if (item.isSuccess()) {
//                        Image status = new Image("icons2/correct-16.png");
//                        status.addStyleName("right");
//                        status.setTitle("Success");
//                        orchTest.add(status);
//                    } else {
//                        Image status = new Image("icons/ic_warning_black_24dp_1x.png");
//                        status.addStyleName("right");
//                        status.setTitle("Failure");
//                        orchTest.add(status);
//                    }
//
//                    if (item.isSuccess())
//                        orchTest.setStyleName("testOverviewHeaderSuccess");
//                    else
//                        orchTest.setStyleName("testOverviewHeaderFail");
//                    orchTest.add(inspect);
//
//                    add(orchTest);
//                }

            }

        });

    }
}
