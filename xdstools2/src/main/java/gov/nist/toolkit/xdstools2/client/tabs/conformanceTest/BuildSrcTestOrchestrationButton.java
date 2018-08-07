package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actortransaction.shared.ActorOption;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.SrcOrchestrationRequest;
import gov.nist.toolkit.services.client.SrcOrchestrationResponse;
import gov.nist.toolkit.xdstools2.client.command.command.BuildSrcTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.OrchestrationSupportTestsDisplay;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildSrcTestOrchestrationRequest;

/**
 *
 */
public class BuildSrcTestOrchestrationButton  extends AbstractOrchestrationButton {
    private ConformanceTestTab testTab;
    private Panel initializationPanel;
    private TestContext testContext;
    private ActorOption actorOption;
    private TestContextView testContextView;
    private FlowPanel initializationResultsPanel = new FlowPanel();
    private boolean testingAClient = true;

    BuildSrcTestOrchestrationButton(ConformanceTestTab testTab, TestContext testContext, TestContextView testContextView, Panel initializationPanel, String label, ActorOption actorOption) {
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

        SrcOrchestrationRequest request = new SrcOrchestrationRequest(actorOption);
        request.setTestSession(new TestSession(testTab.getCurrentTestSession()));
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseExistingState(!isResetRequested());
        request.setUseTls(isTls());
        request.getActorOption().copyFrom(testTab.getCurrentActorOption());

        testTab.setSiteToIssueTestAgainst(null);

        new BuildSrcTestOrchestrationCommand() {
            @Override
            public void onComplete(RawResponse rawResponse) {
                if (handleError(rawResponse, SrcOrchestrationResponse.class)) {
                    testTab.getMainView().clearLoadingMessage();
                    return;
                }
                final SrcOrchestrationResponse orchResponse = (SrcOrchestrationResponse) rawResponse;
                testTab.setOrchestrationResponse(orchResponse);
                testTab.setSrcOrchestrationResponse(orchResponse);

                initializationResultsPanel.add(new HTML("Initialization Complete"));

                initializationResultsPanel.add(new HTML("<h2>System Under Test</h2>"));
                initializationResultsPanel.add(new HTML("<p>System under test is an MHD Document Source so no endpoint or other configuration information is available."));


                initializationResultsPanel.add(new HTML("<h2>Supporting Environment Configuration</h2>"));
                initializationResultsPanel.add(new HTML("This test environment waits for the SUT to send transactions based on the test requirements below. Each test is really a validation of the content sent to " +
                        "simulators supporting the test.  Content validation for each test is done by clicking on the validation button <img src=\"icons2/validate-16.png\">. The validation is done by searching " +
                        "the simulator logs for content matching the test requirements.  Each message sent is graded.  As long as a single message passes the validation requirements, the " +
                        "test passes. Details of the search and validation can be viewed by opening the test (clicking on its bar)." +
                        "<p>The supporting environment is made up of three linked simulators:"));

                initializationResultsPanel.add(new HTML("<hr />"));

                HTML documentation;
                documentation = new HTML("Accepts MHD Provide Document Bundle transactions. Per the MHD profile, there are no prescribed actions beyond validation and " +
                "returning a proper status. For testing purposes, the MHD Document Recipient is bound to an XDS Document Source which forwards the content, in XDS syntax, to " +
                "an XDS Repository and Registry. In the MHD profile this functionality is described under the XDS on FHIR option." +
                "<p>This simulator holds the logs for the MHD transaction Provide Document Bundle.");

                initializationResultsPanel.add(new SiteDisplay("MHD Document Recipient", documentation, orchResponse.getSimProxySite()));

                initializationResultsPanel.add(new HTML("<hr />"));
                documentation = new HTML("This simulator holds the logs for the Provide and Register transaction sent to the Document Repository.");

                initializationResultsPanel.add(new SiteDisplay("XDS Document Source", documentation, orchResponse.getSimProxyBeSite()));

                initializationResultsPanel.add(new HTML("<hr />"));
                documentation = new HTML("Accepts XDS Provide and Register transactions from XDS Document Source.");

                initializationResultsPanel.add(new SiteDisplay("XDS Document Repository/Registry", documentation, orchResponse.getRegrepSite()));

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
        }.run(new BuildSrcTestOrchestrationRequest(ClientUtils.INSTANCE.getCommandContext(), request));


    }
}
