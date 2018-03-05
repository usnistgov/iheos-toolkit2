package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.ActorOption;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.client.FhirSupportOrchestrationRequest;
import gov.nist.toolkit.services.client.FhirSupportOrchestrationResponse;
import gov.nist.toolkit.services.client.PatientDef;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.command.command.BuildFhirSupportOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.widgets.OrchestrationSupportTestsDisplay;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;

import static gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.OrchSupport.buildTable;
import static gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.OrchSupport.tableHeader;

public class BuildFhirSupportOrchestrationButton extends AbstractOrchestrationButton {
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
        request.setTestSession(new TestSession(testTab.getCurrentTestSession()));
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

                supportingFhirServerConfigUI(initializationResultsPanel, response, false);

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

    public static void supportingFhirServerConfigUI(FlowPanel initializationResultsPanel, FhirSupportOrchestrationResponse response, boolean explain) {
        explain = true;
        initializationResultsPanel.add(new HTML("<h2>Supporting FHIR Server</h2>"));
        initializationResultsPanel.add(new HTML("<p>A supporting FHIR server has been created..."));
        if (explain) {
            initializationResultsPanel.add(new HTML("<p>" +
                    "This server is managed within the Conformance Tool under the fake Actor type FHIR Support. This server is run as a " +
                    "Toolkit simulator so it can also be found in the Simulator Manager tool." +
                    "<p>The Provide Document Bundle transaction requires a reference to a Patient Resource. It " +
                    "cannot be included in the bundle. It must be a reference to an external server. " +
                    "Below are the Patient resources currently loaded in this server. " +
                    "Several are included in the default configuration. More can be " +
                    "added but only the default set are reference by included tests. " +
                    "This server can be reset (deleted, re-created, and loaded) from its " +
                    "Conformance Tool tab or from any Conformnace Tool tab that references it by " +
                    "resetting the testing environment (controls provided at the top of the tool)." +
                    "<p>This server can only manage Patient resources and offers only a limitited set of " +
                    "query parameters:" +
                    "<ul>" +
                    "<li>family name and given name in one query" +
                    "<li>identifier (system|id)" +
                    "</ul>"
            ));
        }

        initializationResultsPanel.add(new HTML("<h2>Patients</h2>"));
        FlexTable table = buildTable();

        int row=0;
        table.setWidget(row, 0, tableHeader("Patient ID"));
        table.setWidget(row, 1, tableHeader("First Name"));
        table.setWidget(row, 2, tableHeader("Last Name"));
        table.setWidget(row, 3, tableHeader("FHIR Resource"));

        for (PatientDef pat : response.getPatients()) {
            row++;
            table.setWidget(row, 0, new HTML(pat.pid));
            table.setWidget(row, 1, new HTML(pat.given));
            table.setWidget(row, 2, new HTML(pat.family));
            table.setWidget(row, 3, new HTML(pat.url));
        }

        initializationResultsPanel.add(table);
    }

}
