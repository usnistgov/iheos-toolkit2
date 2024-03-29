package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import gov.nist.toolkit.actortransaction.shared.ActorOption;
import gov.nist.toolkit.actortransaction.shared.IheItiProfile;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RecOrchestrationRequest;
import gov.nist.toolkit.services.client.RecOrchestrationResponse;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.BuildRecTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;
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
    private boolean testingAClient = false;

    private String formatGroup = "Format Group";
    private RadioButton xmlFormatRadioButton = new RadioButton(formatGroup, "XML");
    private RadioButton jsonFormatRadioButton = new RadioButton(formatGroup, "JSON");

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

    String getFormat() {
        return (jsonFormatRadioButton.isChecked()) ? "json" : "xml";
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
//        for (OptionType optionType : ActorType.XDS_on_FHIR_Recipient.getOptions()) {
//           if (optionType.equals(actorOption.optionId)) {
//               testingAClient = true;
//               break;
//           }
//        }

        String msg = testContext.verifyTestContext(testingAClient);
        if (msg != null) {
            testTab.getMainView().clearLoadingMessage();
            testContextView.launchDialog(msg);
            return;
        }


        initializationResultsPanel.clear();
        testTab.getMainView().showLoadingMessage("Initializing...");

        RecOrchestrationRequest request = new RecOrchestrationRequest(actorOption);
        request.setTestSession(new TestSession(testTab.getCurrentTestSession()));
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseTls(isTls());
        request.setUseExistingState(!isResetRequested());
        request.getActorOption().copyFrom(testTab.getCurrentActorOption());
        if (!testingAClient && testContext.getSiteUnderTest() != null) {
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

                initializationResultsPanel.add(new HTML("Initialization complete"));

                initializationResultsPanel.add(new HTML("<h2>Format</h2>"));

                jsonFormatRadioButton.setChecked(true);
                HorizontalFlowPanel formatPanel = new HorizontalFlowPanel();
                formatPanel.add(jsonFormatRadioButton);
                formatPanel.add(xmlFormatRadioButton);
                initializationResultsPanel.add(formatPanel);

                initializationResultsPanel.add(new HTML("<h2>Organization</h2>" +
                "The MHD Recipient/Responder must be configured as a single system in toolkit (one FHIR Base Address). " +
                "except that the Provide Document Bundle transaction has a separate configuration so it can have a " +
                "different URL."));

                if (testContext.getSiteUnderTest() != null) {
                    initializationResultsPanel.add(new SiteDisplay("System Under Test Configuration", testContext.getSiteUnderTest()));
                }

                if (orchResponse.getRRSite() != null) {
                    HTML seDoc = new HTML("<p>This is a Registry and Repository that should receive resulting " +
                    "Provide and Register transactions. <b>Configure your MHD Document Recipient/XDS Document Source " +
                    "and your MHD Document Responder/XDS Document Consumer to forward XDS requests to these endpoints.</b>");
                    initializationResultsPanel.add(new SiteDisplay("Supporting Environment Configuration", seDoc, orchResponse.getRRSite()));
                }


                if (actorOption.profileId == IheItiProfile.MHD)
                    BuildFhirSupportOrchestrationButton.supportingFhirServerConfigUI(initializationResultsPanel, orchResponse.getSupportResponse(), true);




                handleMessages(initializationResultsPanel, orchResponse);

                // Display tests run as part of orchestration - so links to their logs are available
                if (actorOption.profileId != IheItiProfile.MHD)
                    initializationResultsPanel.add(new OrchestrationSupportTestsDisplay(orchResponse, testContext, testContextView, testTab, testTab ));

                initializationResultsPanel.add(new HTML("<br />"));

                if (orchResponse.hasAdditionalDocumentation())
                    initializationResultsPanel.add(new HTML(orchResponse.getAdditionalDocumentation()));


//                initializationResultsPanel.add(new HTML("Patient ID for all tests: " + orchResponse.getRegisterPid().toString()));
//                initializationResultsPanel.add(new HTML("<br />"));

                testTab.displayTestCollection(testTab.getMainView().getTestsPanel());
            }
        }.run(new BuildRecTestOrchestrationRequest(ClientUtils.INSTANCE.getCommandContext(),request));
    }

}
