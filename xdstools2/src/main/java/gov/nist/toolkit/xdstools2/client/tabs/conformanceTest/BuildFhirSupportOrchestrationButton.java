package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actortransaction.client.ActorOption;
import gov.nist.toolkit.services.client.FhirSupportOrchestrationRequest;
import gov.nist.toolkit.services.client.FhirSupportOrchestrationResponse;
import gov.nist.toolkit.services.client.PatientDef;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.command.command.BuildFhirSupportOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.widgets.OrchestrationSupportTestsDisplay;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;

public class BuildFhirSupportOrchestrationButton extends AbstractOrchestrationButton {
    private ConformanceTestTab testTab;
    private Panel initializationPanel;
    private TestContext testContext;
    private ActorOption actorOption;
    private TestContextView testContextView;
    private FlowPanel initializationResultsPanel = new FlowPanel();

    BuildFhirSupportOrchestrationButton(ConformanceTestTab testTab, TestContext testContext, TestContextView testContextView, Panel initializationPanel, String label, ActorOption actorOption) {
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
        String msg = testContext.verifyTestContext(true);
        if (msg != null) {
            testContextView.launchDialog(msg);
            return;
        }

        initializationResultsPanel.clear();

        FhirSupportOrchestrationRequest request = new FhirSupportOrchestrationRequest();
        request.setUserName(testTab.getCurrentTestSession());
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseExistingState(!isResetRequested());
        request.getActorOption().copyFrom(testTab.getCurrentActorOption());

        testTab.setSiteToIssueTestAgainst(null);

        new BuildFhirSupportOrchestrationCommand() {
            @Override
            public void onComplete(RawResponse rawResponse) {
                if (handleError(rawResponse, FhirSupportOrchestrationResponse.class)) {
                    testTab.getMainView().clearLoadingMessage();
                    return;
                }
                final FhirSupportOrchestrationResponse response = (FhirSupportOrchestrationResponse) rawResponse;
                testTab.setOrchestrationResponse(response);
                testTab.setFhirSupportOrchestrationResponse(response);

                initializationResultsPanel.add(new HTML("Initialization Complete"));

                initializationResultsPanel.add(new HTML("<h2>Supporting FHIR Server</h2>"));
                initializationResultsPanel.add(new HTML("<p>A supporting FHIR server has been created..."));

                initializationResultsPanel.add(new HTML("<h2>Patients</h2>"));
                FlexTable table = new FlexTable();
                table.setBorderWidth(2);
                table.addStyleName("border-collapse");
                table.addStyleName("wheat");

                int row=0;
                table.setWidget(row, 0, new HTML("Patient ID"));
                table.setWidget(row, 1, new HTML("First Name"));
                table.setWidget(row, 2, new HTML("Last Name"));
                table.setWidget(row, 3, new HTML("FHIR Resource"));

                for (PatientDef pat : response.getPatients()) {
                    row++;
                    table.setWidget(row, 0, new HTML(pat.pid));
                    table.setWidget(row, 1, new HTML(pat.given));
                    table.setWidget(row, 2, new HTML(pat.family));
                    table.setWidget(row, 3, new HTML(pat.url));
                }

                initializationResultsPanel.add(table);

                handleMessages(initializationResultsPanel, response);

                // Display tests run as part of orchestration - so links to their logs are available
                initializationResultsPanel.add(new OrchestrationSupportTestsDisplay(response, testContext, testContextView, testTab , testTab));

                initializationResultsPanel.add(new HTML("<br />"));

//                initializationResultsPanel.add(new HTML("Patient ID for all tests: " + orchResponse.getRegisterPid().toString()));
//                initializationResultsPanel.add(new HTML("<br />"));

                testTab.displayTestCollection(testTab.getMainView().getTestsPanel());
                testTab.getMainView().clearLoadingMessage();

            }
        }.run(request);

    }
}
