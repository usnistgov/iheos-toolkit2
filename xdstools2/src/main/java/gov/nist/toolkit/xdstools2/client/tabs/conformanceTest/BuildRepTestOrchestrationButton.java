package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RepOrchestrationRequest;
import gov.nist.toolkit.services.client.RepOrchestrationResponse;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.ReportableButton;

import static gov.nist.toolkit.xdstools2.client.ToolWindow.toolkitService;

/**
 *
 */
class BuildRepTestOrchestrationButton extends ReportableButton {
    private ConformanceTestTab testTab;
    private Panel initializationPanel;

    BuildRepTestOrchestrationButton(ConformanceTestTab testTab, Panel initializationPanel, String label) {
        super(initializationPanel, label);
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;
    }

    @Override
    public void handleClick(ClickEvent clickEvent) {
        String msg = testTab.verifyConformanceTestEnvironment();
        if (msg != null) {
            testTab.launchTestEnvironmentDialog(msg);
            return;
        }

        // clear previous display
//        clean();

        RepOrchestrationRequest request = new RepOrchestrationRequest();
        request.setUserName(testTab.getCurrentTestSession());
        request.setEnvironmentName(testTab.getEnvironmentSelection());

        toolkitService.buildRepTestOrchestration(request, new AsyncCallback<RawResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleError(throwable);
            }

            @Override
            public void onSuccess(RawResponse rawResponse) {
                if (handleError(rawResponse, RepOrchestrationResponse.class)) return;
                RepOrchestrationResponse orchResponse = (RepOrchestrationResponse) rawResponse;
                testTab.setRepOrchestrationResponse(orchResponse);
                panel().add(new HTML("<h2>Generated Environment</h2>"));

                if (orchResponse.getMessage().length() > 0) {
                    panel().add(new HTML("<h3>" + orchResponse.getMessage().replaceAll("\n", "<br />")  + "</h3>"));
                }

                FlexTable table = new FlexTable();
                panel().add(table);

                int row = 0;

                table.setHTML(row++, 0, "<h3>Supporting Registry Configuration</h3>");
                table.setText(row, 0, "Register");
                table.setText(row++, 1, orchResponse.getRegConfig().getConfigEle(SimulatorProperties.registerEndpoint).asString());
                table.setText(row, 0, "Query");
                table.setText(row++, 1, orchResponse.getRegConfig().getConfigEle(SimulatorProperties.storedQueryEndpoint).asString());

                panel().add(new HTML("<p>Configure your Repository to forward Register transactions to the above endpoint.<hr />"));

            }


        });
    }


}
