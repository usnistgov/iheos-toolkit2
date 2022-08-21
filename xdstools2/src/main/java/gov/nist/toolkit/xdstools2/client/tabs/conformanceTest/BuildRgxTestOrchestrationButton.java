package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.client.PifType;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RgxOrchestrationRequest;
import gov.nist.toolkit.services.client.RgxOrchestrationResponse;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.BuildRGXTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.OrchestrationSupportTestsDisplay;
//import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRgxTestOrchestrationRequest;

/**
 * Build orchestration for testing a Responding Gateway
 * This code id tied to the button that launches it.
 */
public class BuildRgxTestOrchestrationButton extends AbstractOrchestrationButton {
    private ConformanceTestTab testTab;
    private TestContext testContext;
    private TestContextView testContextView;
    private TestRunner testRunner;
    private Panel initializationPanel;
    private FlowPanel initializationResultsPanel = new FlowPanel();
    private RadioButton noFeed = new RadioButton("rgpidFeedGroup", "No Patient Identity Feed");
    private RadioButton v2Feed = new RadioButton("rgpidFeedGroup", "V2 Patient Identity Feed");

    private String systemTypeGroup = "RG System Type Group";
    private RadioButton exposed = new RadioButton(systemTypeGroup, "Exposed Registry/Repository");
    private RadioButton external = new RadioButton(systemTypeGroup, "External Registry/Repository");
    private boolean isExposed() { return exposed.getValue(); }
    boolean isExternal() { return external.getValue(); }
    private boolean usingExposedRR() { return exposed.getValue(); }
    private boolean isOnDemand;
    private PifType pifType;

    BuildRgxTestOrchestrationButton(ConformanceTestTab testTab, Panel initializationPanel, String label, TestContext testContext, TestContextView testContextView, TestRunner testRunner, PifType pifType) {
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;
        this.testContext = testContext;
        this.testContextView = testContextView;
        this.testRunner = testRunner;
        this.pifType = pifType;



        // Restore pifType from Orchestration properties previously saved
        // 1.
        if (PifType.V2.equals(pifType)) {
            v2Feed.setChecked(true);
        } else { // Default to NoFeed otherwise.
            noFeed.setChecked(true);
        }


        isOnDemand = testTab.getCurrentActorOption().isOnDemand();

        //
        // Disable selections that are not yet supported
        //
        external.setEnabled(false);

        setParentPanel(initializationPanel);

        FlowPanel customPanel = new FlowPanel();

            HTML instructions = new HTML(
                    "<p>" +
                            "The system under test is a Responding Gateway. " +
                            "The tests in this area of the conformance tool make no assumptions about the implementation behind the system under test. " +
                            "If you want to test using the traditional assumptions about Repository/Registry backing, " +
                            "you should select the conformance tests for the traditional Responding Gateway actor (and not this one). " +
                    "</p><p>" +
                            "You will initialize your Responding Gateway and corresponding backend systems with required test data. " +
                            "This XDS Toolkit will provide no transactions to supply that data (ADT messages, documents)." +
                    "</p><p>" +
                            "Select the Responding Gateway to be tested from the Test Context located to the right. " +
                            "If your Responding Gateway is not listed, you will need to add configuration as a new test site. " +
                    "</p><p>" +
                            "Each test will send Cross Gateway Query and/or Retrieve transactions to your Responding Gateway. " +
                            "The Supporting Environment Configuration has one test: RG.Init. " +
                            "The content/function of RG.Init depends on the Responding Gateway tests that are coupled with it. " +
                            "This framework does not make any assumptions about the initialization setup. " +
                    "</p>"
            );
            customPanel.add(instructions);


/*            Panel systemTypePanel = new HorizontalPanel();
            systemTypePanel.add(exposed);
            systemTypePanel.add(external);
            exposed.setChecked(true);
            Panel editExposedSystemConfigPanel = new HorizontalPanel();
            systemTypePanel.add(editExposedSystemConfigPanel);
            final Button editExposedSiteButton = new Button("Edit Site Configuration");
            editExposedSystemConfigPanel.add(editExposedSiteButton);
            editExposedSiteButton.setVisible(false);
            editExposedSiteButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {

                }
            });

            customPanel.add(systemTypePanel);
            customPanel.add(new HTML("<br />"));

            // Patient Identity feed to registry
            customPanel.add(noFeed);
            customPanel.add(v2Feed);
            customPanel.add(new HTML("<br />"));
*/
        setCustomPanel(customPanel);

        build(!isOnDemand);
        panel().add(initializationResultsPanel);

    }

    public void orchestrate() {
        String msg = testContext.verifyTestContext();
        if (msg != null) {
            testTab.getMainView().clearLoadingMessage();
            testContextView.launchDialog(msg);
            return;
        }

        /*
        if (!isExposed() && !isExternal() && !isOnDemand) {
            new PopupMessage("Must select Exposed or External Registry/Repository");
            return;
        }
         */

        testTab.getMainView().showLoadingMessage("Initializing...");
        final RgxOrchestrationRequest request = new RgxOrchestrationRequest();
//        request.setOnDemand(isOnDemand);  // much of the rest is ignored if this is true
        request.setTestSession(new TestSession(testTab.getCurrentTestSession()));
//        request.setUseTls(isTls());
        //TODO Fix
        request.setUseTls(false);
//        request.setUseExposedRR(usingExposedRR());
        request.setUseSimAsSUT(false);
//        request.setPifType((v2Feed.isChecked()) ? PifType.V2 : PifType.NONE);

        //TODO FIX this assumption
//        request.setPifType(PifType.NONE);
        request.setPifType(PifType.V2);


        request.setTestSession(new TestSession(testTab.getCurrentTestSession()));
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseExistingState(!isResetRequested());
        SiteSpec siteSpec = new SiteSpec(testContext.getSiteName(), new TestSession(testTab.getCurrentTestSession()));
        /*
        if (isSaml()) {
            setSamlAssertion(siteSpec);
        }
        */
        request.setSiteUnderTest(siteSpec);

        testTab.setSiteToIssueTestAgainst(siteSpec);

        initializationResultsPanel.clear();

        new BuildRGXTestOrchestrationCommand(){
            @Override
            public void onComplete(RawResponse rawResponse) {
                if (handleError(rawResponse, RgxOrchestrationResponse.class)) {
                    testTab.getMainView().clearLoadingMessage();
                    return;
                }
                RgxOrchestrationResponse orchResponse = (RgxOrchestrationResponse) rawResponse;
                testTab.setOrchestrationResponse(orchResponse);

                /*
                if (PifType.V2.equals(request.getPifType())) {
                    initializationResultsPanel.add(new HTML("<p>Initialization complete</p>"));
                } else if (PifType.NONE.equals(request.getPifType())) {
                    initializationResultsPanel.add(new HTML("<p style='color:orange'>Initialization partially complete: there are two additional steps below for you to complete.</p>"));
                } else {
                    initializationResultsPanel.add(new HTML("<p style='color:red'>Initialization Error: Unknown pifType.</p>"));
                }
                */

                if (testContext.getSiteUnderTest() != null) {
                    initializationResultsPanel.add(new SiteDisplay("System Under Test Configuration", testContext.getSiteUnderTest()));
                }

                initializationResultsPanel.add(new HTML("<h2>Supporting Environment Configuration</h2>"));
                initializationResultsPanel.add(new HTML("System: None"));
                handleMessages(initializationResultsPanel, orchResponse);


                /*
                if (PifType.NONE.equals(request.getPifType())) {
                    initializationResultsPanel.add(new HTML("<h3>1. On your system, manually perform the Patient Identity Feed for these PIDs as shown below</h3>"));
                }

                FlexTable table = new FlexTable();
                displayPIDs(table, orchResponse, 0);
                initializationResultsPanel.add(table);
                 */


                /*
                if (PifType.NONE.equals(request.getPifType())) {
                    initializationResultsPanel.add(new HTML("<h3>2. Run these utility tests manually to fully initialize the Testing Environment</h3>"));
                } else {
                    initializationPanel.add(new HTML("<br/>"));
                }
                 */

                initializationResultsPanel.add(new OrchestrationSupportTestsDisplay(orchResponse, testContext, testContextView, testRunner, testTab ));
                initializationResultsPanel.add(new HTML("<br />"));

                testTab.displayTestCollection(testTab.getMainView().getTestsPanel());
            }
        }.run(new BuildRgxTestOrchestrationRequest(ClientUtils.INSTANCE.getCommandContext(),request));
    }

    private int displayPIDs(FlexTable table, RgxOrchestrationResponse response, int row) {
        table.setHTML(row++, 0, "<h3>Patient IDs</h3>");
        table.setText(row, 0, "Patient ID");
        String pidStr = response.getSimplePid()!=null?response.getSimplePid().asString():"";
        table.setText(row++, 1, pidStr);
        return row;
    }
}
