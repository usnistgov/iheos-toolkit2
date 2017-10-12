package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actortransaction.client.ActorOption;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RecOrchestrationRequest;
import gov.nist.toolkit.services.client.RecOrchestrationResponse;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.BuildRecTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.OrchestrationSupportTestsDisplay;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRecTestOrchestrationRequest;

/**
 *
 */
public class BuildRecTestOrchestrationButton extends AbstractOrchestrationButton {
    private ConformanceTestTab testTab;
    private Panel initializationPanel;
    private TestContext testContext;
    private ActorOption actorOption;
    private TestContextView testContextView;
    private FlowPanel initializationResultsPanel = new FlowPanel();

    BuildRecTestOrchestrationButton(ConformanceTestTab testTab, TestContext testContext, TestContextView testContextView, Panel initializationPanel, String label, ActorOption actorOption) {
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;
        this.testContext = testContext;
        this.testContextView = testContextView;
        this.actorOption = actorOption;

        setParentPanel(initializationPanel);

        build();
        panel().add(initializationResultsPanel);
    }


    /**
     * MHD orchestration requirements:
     *
     * Document Source SUT
     * Build Registry/Repository sim
     * Build XDS_on_FHIR_Recipient simproxy
     * Link simproxy to sim
     * advertise the details of both
     *
     */
    @Override
    public void orchestrate() {
        final boolean testingAClient = actorOption.optionId.equals(ActorType.XDS_on_FHIR_Recipient.getOption());
        String msg = testContext.verifyTestContext(testingAClient);
        if (msg != null) {
            testContextView.launchDialog(msg);
            return;
        }

        initializationResultsPanel.clear();
        testTab.getMainView().showLoadingMessage("Initializing...");

        RecOrchestrationRequest request = new RecOrchestrationRequest(actorOption);
        request.setUserName(testTab.getCurrentTestSession());
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseExistingState(!isResetRequested());
        request.getActorOption().copyFrom(testTab.getCurrentActorOption());
        if (!testingAClient) {
            SiteSpec sutSiteSpec = testContext.getSiteUnderTest().siteSpec();
            if (isSaml()) {
                setSamlAssertion(sutSiteSpec);
            }
            request.setRegistrySut(sutSiteSpec);

            testTab.setSiteToIssueTestAgainst(sutSiteSpec);
        }

        new BuildRecTestOrchestrationCommand(){
            @Override
            public void onComplete(RawResponse rawResponse) {
                if (handleError(rawResponse, RecOrchestrationResponse.class)) {
                    testTab.getMainView().clearLoadingMessage();
                    return;
                }
                final RecOrchestrationResponse orchResponse = (RecOrchestrationResponse) rawResponse;
                testTab.setOrchestrationResponse(orchResponse);
                testTab.setRecOrchestrationResponse(orchResponse);

                initializationResultsPanel.add(new HTML("Initialization Complete"));

                if (testContext.getSiteUnderTest() != null && !testingAClient) {
                    initializationResultsPanel.add(new SiteDisplay("System Under Test Configuration", testContext.getSiteUnderTest()));
                }

                initializationResultsPanel.add(new HTML("<h2>Supporting Environment Configuration</h2>"));

                handleMessages(initializationResultsPanel, orchResponse);

                // Display tests run as part of orchestration - so links to their logs are available
                initializationResultsPanel.add(new OrchestrationSupportTestsDisplay(orchResponse, testContext, testContextView, testTab ));

                initializationResultsPanel.add(new HTML("<br />"));

                initializationResultsPanel.add(new HTML("Patient ID for all tests: " + orchResponse.getRegisterPid().toString()));
                initializationResultsPanel.add(new HTML("<br />"));

                testTab.displayTestCollection(testTab.getMainView().getTestsPanel());
            }
        }.run(new BuildRecTestOrchestrationRequest(ClientUtils.INSTANCE.getCommandContext(),request));
    }

}
