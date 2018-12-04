package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actortransaction.shared.ActorOption;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.client.IsrOrchestrationRequest;
import gov.nist.toolkit.services.client.IsrOrchestrationResponse;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.command.command.BuildIsrTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.OrchestrationSupportTestsDisplay;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildIsrTestOrchestrationRequest;

/**
 *
 */
public class BuildIsrTestOrchestrationButton extends AbstractOrchestrationButton {
    protected ConformanceTestTab testTab;
    protected Panel initializationPanel;
    protected TestContext testContext;
    protected ActorOption actorOption;
    protected TestContextView testContextView;
    protected FlowPanel initializationResultsPanel = new FlowPanel();
    protected boolean testingAClient = true;

    BuildIsrTestOrchestrationButton(ConformanceTestTab testTab, TestContext testContext, TestContextView testContextView, Panel initializationPanel, String label, ActorOption actorOption) {
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;
        this.testContext = testContext;
        this.testContextView = testContextView;
        this.actorOption = actorOption;

        setParentPanel(initializationPanel);

        build();
        panel().add(initializationResultsPanel);
    }

    @Override
    public void orchestrate() {
        String msg = testContext.verifyTestContext(testingAClient);
        if (msg != null) {
            testContextView.launchDialog(msg);
            return;
        }

        initializationResultsPanel.clear();

        IsrOrchestrationRequest request = new IsrOrchestrationRequest(actorOption);
        request.setTestSession(new TestSession(testTab.getCurrentTestSession()));
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseExistingState(!isResetRequested());
        request.setUseTls(isTls());
        request.getActorOption().copyFrom(testTab.getCurrentActorOption());

        testTab.setSiteToIssueTestAgainst(null);

        orchestrate(request);
    }

    void orchestrate(IsrOrchestrationRequest request) {
        new BuildIsrTestOrchestrationCommand() {
            @Override
            public void onComplete(RawResponse rawResponse) {
                if (handleError(rawResponse, IsrOrchestrationResponse.class)) {
                    testTab.getMainView().clearLoadingMessage();
                    return;
                }
                final IsrOrchestrationResponse orchResponse = (IsrOrchestrationResponse) rawResponse;
                testTab.setOrchestrationResponse(orchResponse);

                initializationResultsPanel.add(new HTML("Initialization complete"));

                initializationResultsPanel.add(new HTML("<h2>System Under Test</h2>"));
                initializationResultsPanel.add(new HTML("<p>System under test is an Integrated Source/Repository so no endpoint or other configuration information is available."));

                initializationResultsPanel.add(new HTML("<h2>Supporting Environment Configuration</h2>"));
                initializationResultsPanel.add(new HTML("This test environment waits for the SUT to send transactions based on the test requirements below. Each test is really a validation of the content sent to " +
                        "the simulator supporting the test.  Content validation for each test is done by clicking on the validation button <img src=\"icons2/validate-16.png\">. The validation is done by searching " +
                        "the simulator logs for content matching the test requirements.  Each message sent is graded.  As long as a single message passes the validation requirements, the " +
                        "test passes. Details of the search and validation can be viewed by opening the test (clicking on its bar)." +
                        "<p>The supporting environment is made up of a Registry simulator:"));

                initializationResultsPanel.add(new HTML("<hr />"));

                HTML documentation;
                documentation = new HTML("Accepts XDS Register transaction from an ISR.");

                initializationResultsPanel.add(new SiteDisplay("XDS Document Registry", documentation, orchResponse.getRegSite()));

                handleMessages(initializationResultsPanel, orchResponse);

                // Display tests run as part of orchestration - so links to their logs are available
                initializationResultsPanel.add(new OrchestrationSupportTestsDisplay(orchResponse, testContext, testContextView, testTab , testTab));

                initializationResultsPanel.add(new HTML("<br />"));

                if (orchResponse.hasAdditionalDocumentation())
                    initializationResultsPanel.add(new HTML(orchResponse.getAdditionalDocumentation()));

//                initializationResultsPanel.add(new HTML("Patient ID for all tests: " + orchResponse.getRegisterPid().toString()));
//                initializationResultsPanel.add(new HTML("<br />"));

                testTab.displayTestCollection(testTab.getMainView().getTestsPanel());
                testTab.getMainView().clearLoadingMessage();
            }
        }.run(new BuildIsrTestOrchestrationRequest(ClientUtils.INSTANCE.getCommandContext(), request));
    }

}
