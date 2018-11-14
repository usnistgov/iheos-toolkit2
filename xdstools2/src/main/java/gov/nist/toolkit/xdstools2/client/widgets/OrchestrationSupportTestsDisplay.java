package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.services.client.AbstractOrchestrationResponse;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestsOverviewCommand;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.*;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestsOverviewRequest;

import java.util.List;

/**
 * Display results of running tests that initialize SUT or orchestration support actors
 */
public class OrchestrationSupportTestsDisplay extends FlowPanel {

    public OrchestrationSupportTestsDisplay(final AbstractOrchestrationResponse orchResponse, final TestContext testContext, final TestContextView testContextView, final TestRunner testRunner, final Controller controller) {
        new GetTestsOverviewCommand(){
            @Override
            public void onComplete(List<TestOverviewDTO> testOverviews) {
                add(new HTML("Utilities run to initialize environment"));
                TestDisplayGroup orchGroup = new TestDisplayGroup(testContext, testContextView, testRunner, controller);
                orchGroup.allowRun(true);
                orchGroup.allowDelete(false);
                boolean hasTests = false;
                for (TestOverviewDTO testOverview : testOverviews) {
                    if (testOverview.getName() != null && !testOverview.getName().equals("")) {
                        final TestDisplay testDisplay = orchGroup.add(testOverview);
                        testDisplay.addExtraStyle("orchestrationTestMc");
                        // Lazy loading of TestOverviewDTO until it is opened.
                        HandlerRegistration openTestBarHReg = testDisplay.getView().addOpenHandler(new TestBarOpenHandler(testDisplay, testOverview, ClientUtils.INSTANCE.getCommandContext(), null,null
                        ));
                        testDisplay.getView().setOpenTestBarHReg(openTestBarHReg);
                        add(testDisplay.asWidget());
                        testDisplay.display(testOverview, null);
                        hasTests = true;
                    }
                }
                if (!hasTests) {
                    add(new HTML("No Orchestration steps configured"));
                }
            }
        }.run(new GetTestsOverviewRequest(ClientUtils.INSTANCE.getCommandContext(),orchResponse.getTestInstances()));
    }
}
