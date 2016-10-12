package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.services.client.AbstractOrchestrationResponse;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.*;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.List;

/**
 * Display results of running tests that initialize SUT or orchestration support actors
 */
public class OrchestrationSupportTestsDisplay extends FlowPanel {

//    public OrchestrationSupportTestsDisplay(final AbstractOrchestrationResponse orchResponse, final String testSession, final SiteSpec siteSpec) {
    public OrchestrationSupportTestsDisplay(final AbstractOrchestrationResponse orchResponse, final TestContext testContext, final TestContextView testContextView, final TestRunner testRunner) {
        ClientUtils.INSTANCE.getToolkitServices().getTestsOverview(testContext.getTestSession(), orchResponse.getTestInstances(), new AsyncCallback<List<TestOverviewDTO>>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("getTestOverview: " + caught.getMessage());
            }

            public void onSuccess(List<TestOverviewDTO> testOverviews) {
                add(new HTML("Utilities run to initialize environment"));
                TestDisplayGroup orchGroup = new TestDisplayGroup(testContext, testContextView, testRunner);
                orchGroup.setAllowRun(false);
                orchGroup.setAllowDelete(false);
                for (TestOverviewDTO testOverview : testOverviews) {
                    TestDisplay testDisplay = orchGroup.display(testOverview);
                    add(testDisplay);
                }
            }

        });

    }
}
