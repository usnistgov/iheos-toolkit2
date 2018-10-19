package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actortransaction.shared.ActorOption;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.client.DocAdminOrchestrationRequest;
import gov.nist.toolkit.services.client.DocAdminOrchestrationResponse;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.command.command.BuildDocAdminTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.OrchestrationSupportTestsDisplay;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildDocAdminTestOrchestrationRequest;

/**
 *
 */
public class BuildDocAdminTestOrchestrationButton extends AbstractOrchestrationButton {
    protected ConformanceTestTab testTab;
    protected Panel initializationPanel;
    protected TestContext testContext;
    protected ActorOption actorOption;
    protected TestContextView testContextView;
    protected FlowPanel initializationResultsPanel = new FlowPanel();
    protected boolean testingAClient = true;

    BuildDocAdminTestOrchestrationButton(ConformanceTestTab testTab, TestContext testContext, TestContextView testContextView, Panel initializationPanel, String label, ActorOption actorOption) {
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

        DocAdminOrchestrationRequest request = new DocAdminOrchestrationRequest(actorOption);
        request.setTestSession(new TestSession(testTab.getCurrentTestSession()));
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseExistingState(!isResetRequested());
        request.setUseTls(isTls());
        request.getActorOption().copyFrom(testTab.getCurrentActorOption());

        testTab.setSiteToIssueTestAgainst(null);

        orchestrate(request);
    }

    void orchestrate(DocAdminOrchestrationRequest request) {
        new BuildDocAdminTestOrchestrationCommand() {
            @Override
            public void onComplete(RawResponse rawResponse) {
                if (handleError(rawResponse, DocAdminOrchestrationResponse.class)) {
                    testTab.getMainView().clearLoadingMessage();
                    return;
                }
                final DocAdminOrchestrationResponse orchResponse = (DocAdminOrchestrationResponse) rawResponse;
                testTab.setOrchestrationResponse(orchResponse);

                initializationResultsPanel.add(new HTML("Initialization Complete"));

                initializationResultsPanel.add(new HTML("<h2>System Under Test</h2>"));
                initializationResultsPanel.add(new HTML("<p>System under test is an XDS Document Administrator so no endpoint or other configuration information is available."));

                initializationResultsPanel.add(new HTML("<h2>Supporting Environment Configuration</h2>"));
                initializationResultsPanel.add(new HTML("This test environment waits for the SUT to send transactions based on the test requirements below. Each test is really a validation of the content sent to " +
                        "simulators supporting the test.  Content validation for each test is done by clicking on the validation button <img src=\"icons2/validate-16.png\">. The validation is done by searching " +
                        "the simulator logs for content matching the test requirements.  Each message sent is graded.  As long as a single message passes the validation requirements, the " +
                        "test passes. Details of the search and validation can be viewed by opening the test (clicking on its bar)." +
                        "<p>The supporting environment is made up of a Registry simulator:"));

                initializationResultsPanel.add(new HTML("<hr />"));

                HTML documentation;
                documentation = new HTML("Accepts XDS Register and XDS Stored Query transactions from XDS Document Administrator.");

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
        }.run(new BuildDocAdminTestOrchestrationRequest(ClientUtils.INSTANCE.getCommandContext(), request));
    }

}
