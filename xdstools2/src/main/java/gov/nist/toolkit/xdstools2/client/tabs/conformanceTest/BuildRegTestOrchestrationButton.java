package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.client.PifType;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RegOrchestrationRequest;
import gov.nist.toolkit.services.client.RegOrchestrationResponse;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.BuildRegTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.OrchestrationSupportTestsDisplay;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRegTestOrchestrationRequest;


/**
 * Build Registry tests orchestration
 */
public class BuildRegTestOrchestrationButton extends AbstractOrchestrationButton {
    private ConformanceTestTab testTab;
    private Panel initializationPanel;
    private TestContext testContext;
    private TestContextView testContextView;
    private FlowPanel initializationResultsPanel = new FlowPanel();
    private RadioButton noFeed = new RadioButton("pidFeedGroup", "No Patient Identity Feed");
    private RadioButton v2Feed = new RadioButton("pidFeedGroup", "V2 Patient Identity Feed");
    private PifType pifType;

    BuildRegTestOrchestrationButton(ConformanceTestTab testTab, TestContext testContext, TestContextView testContextView, Panel initializationPanel, String label, PifType pifType) {
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;
        this.testContext = testContext;
        this.testContextView = testContextView;
        this.pifType = pifType;

        setParentPanel(initializationPanel);

        FlowPanel pidFeedPanel = new FlowPanel();
        pidFeedPanel.add(noFeed);
        pidFeedPanel.add(v2Feed);

        // Restore pifType from Orchestration properties previously saved
        // 1.
        if (PifType.V2.equals(pifType)) {
            v2Feed.setChecked(true);
        } else { // Default to NoFeed otherwise.
            noFeed.setChecked(true);
        }

        setCustomPanel(pidFeedPanel);
        build();
        panel().add(initializationResultsPanel);
        
        /** TODO - KM turned off**/
        //setCustomPanel(pidFeedPanel);
        /** TODO - End KM turned off**/
        /** TODO - KM turned off**/
        //build();
        /** TODO - End KM turned off**/
        /** TODO - KM turned off**/
        //panel().add(initializationResultsPanel);
        /** TODO - End KM turned off**/

    }

    public void orchestrate() {
        String msg = testContext.verifyTestContext();
        if (msg != null) {
            testTab.getMainView().clearLoadingMessage();
            testContextView.launchDialog(msg);
            return;
        }

        initializationResultsPanel.clear();
        testTab.getMainView().showLoadingMessage("Initializing...");

        TestSession testSession = new TestSession(testTab.getCurrentTestSession());
        final RegOrchestrationRequest request = new RegOrchestrationRequest();
        request.selfTest(isSelfTest());
        request.setPifType((v2Feed.isChecked()) ? PifType.V2 : PifType.NONE);
        request.setUseTls(isTls());
        request.setTestSession(testSession);
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseExistingState(!isResetRequested());
        SiteSpec sutSiteSpec = (testContext.getSiteUnderTest() == null) ? null : testContext.getSiteUnderTest().siteSpec();
        if (isSaml()) {
            setSamlAssertion(sutSiteSpec);
        }
        request.setRegistrySut(sutSiteSpec);

        testTab.setSiteToIssueTestAgainst(sutSiteSpec);

        new BuildRegTestOrchestrationCommand(){
            @Override
            public void onComplete(RawResponse rawResponse) {
                if (handleError(rawResponse, RegOrchestrationResponse.class)) {
                    testTab.getMainView().clearLoadingMessage();
                    return;
                }
                final RegOrchestrationResponse orchResponse = (RegOrchestrationResponse) rawResponse;
                testTab.setRegOrchestrationResponse(orchResponse);

                if (PifType.V2.equals(request.getPifType())) {
                    initializationResultsPanel.add(new HTML("<p>Initialization complete</p>"));
                } else if (PifType.NONE.equals(request.getPifType())) {
                    initializationResultsPanel.add(new HTML("<p style='color:orange'>Initialization partially complete: there are two additional steps below for you to complete.</p>"));
                } else {
                    initializationResultsPanel.add(new HTML("<p style='color:red'>Initialization Error: Unknown pifType.</p>"));
                }

                if (testContext.getSiteUnderTest() != null) {
                    initializationResultsPanel.add(new SiteDisplay("System Under Test Configuration", testContext.getSiteUnderTest()));
                }

                initializationResultsPanel.add(new HTML("<h2>Supporting Environment Configuration</h2>"));
                handleMessages(initializationResultsPanel, orchResponse);

                if (PifType.NONE.equals(request.getPifType())) {
                    initializationResultsPanel.add(new HTML("<h3>1. On your system, manually perform the Patient Identity Feed for these PIDs as shown below</h3>"));
                }
                HTML patientIdForRegisterTests = new HTML("Patient ID for Register tests: " + orchResponse.getRegisterPid().toString());
//                patientIdForRegisterTests.setStyleName("patientIdTextMc"); // CSS Marker class used only For UI testing purposes
                initializationResultsPanel.add(patientIdForRegisterTests);
                initializationResultsPanel.add(new HTML("Alternate Patient ID for Register tests: " + orchResponse.getRegisterAltPid().toString()));
                initializationResultsPanel.add(new HTML("Patient ID for Stored Query tests: " + orchResponse.getSqPid().toString()));
                initializationResultsPanel.add(new HTML("Patient ID for MPQ tests: " + orchResponse.getMpq1Pid().toString()));
                initializationResultsPanel.add(new HTML("Patient ID for MPQ tests: " + orchResponse.getMpq2Pid().toString()));

                if (PifType.NONE.equals(request.getPifType())) {
                    initializationResultsPanel.add(new HTML("<h3>2. Run these utility tests manually to fully initialize the Testing Environment</h3>"));
                }

                // Display tests run as part of orchestration - so links to their logs are available
                initializationResultsPanel.add(new OrchestrationSupportTestsDisplay(orchResponse, testContext, testContextView, testTab, testTab ));


                initializationResultsPanel.add(new HTML("<br />"));

                testTab.displayTestCollection(testTab.getMainView().getTestsPanel());

            }
        }.run(new BuildRegTestOrchestrationRequest(ClientUtils.INSTANCE.getCommandContext(),request));
    }


}
