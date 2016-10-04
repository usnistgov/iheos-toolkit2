package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import gov.nist.toolkit.services.client.PifType;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RegOrchestrationRequest;
import gov.nist.toolkit.services.client.RegOrchestrationResponse;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.OrchestrationSupportTestsDisplay;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;


/**
 *
 */
public class BuildRegTestOrchestrationButton extends AbstractOrchestrationButton {
    private ConformanceTestTab testTab;
    private Panel initializationPanel;
    private TestContext testContext;
    private TestContextDisplay testContextDisplay;
    private FlowPanel initializationResultsPanel = new FlowPanel();
    private RadioButton noFeed = new RadioButton("pidFeedGroup", "No Patient Identity Feed");
    private RadioButton v2Feed = new RadioButton("pidFeedGroup", "V2 Patient Identitfy Feed");

    BuildRegTestOrchestrationButton(ConformanceTestTab testTab, TestContext testContext, TestContextDisplay testContextDisplay, Panel initializationPanel, String label) {
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;
        this.testContext = testContext;
        this.testContextDisplay = testContextDisplay;

        setParentPanel(initializationPanel);
        setLabel(label);
        setResetLabel("Reset");

        FlowPanel pidFeedPanel = new FlowPanel();
        pidFeedPanel.add(noFeed);
        pidFeedPanel.add(v2Feed);
        v2Feed.setChecked(true);

        setCustomPanel(pidFeedPanel);
        build();
        panel().add(initializationResultsPanel);

    }

    @Override
    public void handleClick(ClickEvent clickEvent) {
        String msg = testTab.verifyTestContext();
        if (msg != null) {
            testContextDisplay.launchDialog(msg);
            return;
        }

        initializationResultsPanel.clear();

        RegOrchestrationRequest request = new RegOrchestrationRequest();
        request.setPifType((v2Feed.isChecked()) ? PifType.V2 : PifType.NONE);
        request.setUserName(testTab.getCurrentTestSession());
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseExistingSimulator(!isResetRequested());
        SiteSpec sutSiteSpec = testContext.getSiteUnderTest().siteSpec();
        request.setRegistrySut(sutSiteSpec);

        testTab.setSitetoIssueTestAgainst(sutSiteSpec);


        ClientUtils.INSTANCE.getToolkitServices().buildRegTestOrchestration(request, new AsyncCallback<RawResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleError(throwable);
            }

            @Override
            public void onSuccess(RawResponse rawResponse) {
                if (handleError(rawResponse, RegOrchestrationResponse.class)) return;
                final RegOrchestrationResponse orchResponse = (RegOrchestrationResponse) rawResponse;
                testTab.setOrchestrationResponse(orchResponse);

                initializationResultsPanel.add(new HTML("Initialization Complete"));

                if (testContext.getSiteUnderTest() != null) {
                    initializationResultsPanel.add(new SiteDisplay("System Under Test Configuration", testContext.getSiteUnderTest()));
                }

                initializationResultsPanel.add(new HTML("<h2>Supporting Environment Configuration</h2>"));

                handleMessages(initializationResultsPanel, orchResponse);

                // Display tests run as part of orchestration - so links to their logs are available
                initializationResultsPanel.add(new OrchestrationSupportTestsDisplay(orchResponse, testContext, testContextDisplay, testTab ));

                initializationResultsPanel.add(new HTML("<br />"));

                initializationResultsPanel.add(new HTML("Patient ID for Register tests: " + orchResponse.getRegisterPid().toString()));
                initializationResultsPanel.add(new HTML("Patient ID for Stored Query tests: " + orchResponse.getSqPid().toString()));
                initializationResultsPanel.add(new HTML("Patient ID for MPQ tests: " + orchResponse.getMpq1Pid().toString()));
                initializationResultsPanel.add(new HTML("Patient ID for MPQ tests: " + orchResponse.getMpq2Pid().toString()));
                initializationResultsPanel.add(new HTML("<br />"));

            }
        });

    }
}
