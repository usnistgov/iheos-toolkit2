package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RepOrchestrationRequest;
import gov.nist.toolkit.services.client.RepOrchestrationResponse;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.BuildRepTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRepTestOrchestrationRequest;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
class BuildRepTestOrchestrationButton extends AbstractOrchestrationButton {
    private ConformanceTestTab testTab;
    private Panel initializationPanel;
    private TestContext testContext;
    private TestContextView testContextView;
    private FlowPanel initializationResultsPanel = new FlowPanel();
    public static List<ActorAndOption> ACTOR_OPTIONS = new ArrayList<>();
    static {
        ACTOR_OPTIONS = java.util.Arrays.asList(
                new ActorAndOption("rep", "", "Required", false),
                new ActorAndOption("rep", XUA_OPTION, "XUA Option", false)
        );
    }

    BuildRepTestOrchestrationButton(ConformanceTestTab testTab, TestContext testContext, TestContextView testContextView, Panel initializationPanel, String label) {
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;
        this.testContext = testContext;
        this.testContextView = testContextView;

        setParentPanel(initializationPanel);
        build();
        panel().add(initializationResultsPanel);
    }

    public void orchestrate() {
        String msg = testContext.verifyTestContext();
        if (msg != null) {
            testTab.getMainView().clearLoadingMessage();
            testContextView.launchDialog(msg);
            return;
        }

        initializationResultsPanel.clear();

        RepOrchestrationRequest request = new RepOrchestrationRequest();
        SiteSpec sutSiteSpec = testContext.getSiteUnderTest().siteSpec();
        if (isSaml()) {
            setSamlAssertion(sutSiteSpec);
        }
        request.setSutSite(sutSiteSpec);
        request.setUserName(testTab.getCurrentTestSession());
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseExistingSimulator(!isResetRequested());

        new BuildRepTestOrchestrationCommand(){
            @Override
            public void onComplete(RawResponse rawResponse) {
                if (handleError(rawResponse, RepOrchestrationResponse.class)) {
                    testTab.getMainView().clearLoadingMessage();
                    return;
                }
                RepOrchestrationResponse orchResponse = (RepOrchestrationResponse) rawResponse;
                testTab.setRepOrchestrationResponse(orchResponse);

                initializationResultsPanel.add(new HTML("Initialization Complete"));

                if (testContext.getSiteUnderTest() != null) {
                    initializationResultsPanel.add(new SiteDisplay("System Under Test Configuration", testContext.getSiteUnderTest()));
                }

                handleMessages(initializationResultsPanel, orchResponse);

                if (orchResponse.getSupportSite() != null) {
                    initializationResultsPanel.add(new SiteDisplay("Supporting Environment Configuration", orchResponse.getSupportSite()));
                }

                initializationResultsPanel.add(new HTML("<br />"));
                initializationResultsPanel.add(new HTML("Patient ID: " + orchResponse.getPid().toString()));
                initializationResultsPanel.add(new HTML("<br />"));

                initializationResultsPanel.add(new HTML("<h3>Configure your Repository to forward Register transactions to the above Register endpoint." +
                        "Then Reset Testing Environment (above) with Reset to properly initialize the testing environment (Patient ID is needed).</h3><hr />"));

                // test will be run out of support site so pass it back to conformance test tab
                testTab.setSiteToIssueTestAgainst(orchResponse.getSupportSite().siteSpec());

                testTab.displayTestCollection(testTab.getMainView().getTestsPanel());
            }
        }.run(new BuildRepTestOrchestrationRequest(ClientUtils.INSTANCE.getCommandContext(),request));
    }

}
