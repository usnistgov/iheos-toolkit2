package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actorfactory.SimulatorProperties;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.services.client.IgOrchestrationRequest;
import gov.nist.toolkit.services.client.IgOrchestrationResponse;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.ReportableButton;

/**
 *
 */
class BuildIGTestOrchestrationButton extends ReportableButton {
    private IGTestTab testTab;
    boolean includeIG;

    BuildIGTestOrchestrationButton(IGTestTab testTab, Panel topPanel, String label, boolean includeIG) {
        super(topPanel, label);
        this.testTab = testTab;
        this.includeIG = includeIG;
    }

    public void handleClick(ClickEvent event) {
        if (GenericQueryTab.empty(testTab.getCurrentTestSession())) {
            new PopupMessage("Must select test session first");
            return;
        }
        IgOrchestrationRequest request = new IgOrchestrationRequest();
        request.setUserName(testTab.getCurrentTestSession());
        request.setIncludeLinkedIG(includeIG);
        testTab.toolkitService.buildIgTestOrchestration(request, new AsyncCallback<RawResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleError(throwable);
            }

            @Override
            public void onSuccess(RawResponse rawResponse) {
                if (handleError(rawResponse, IgOrchestrationResponse.class)) return;
                IgOrchestrationResponse orchResponse = (IgOrchestrationResponse) rawResponse;

                testTab.rgConfigs = orchResponse.getSimulatorConfigs();

                panel().add(new HTML("<h2>Generated Environment</h2>"));
                FlexTable table = new FlexTable();
                panel().add(table);
                int row = 0;

                table.setHTML(row++, 0, "<h3>Patient IDs</h3>");

                table.setText(row, 0, "Single document Patient ID");
                table.setText(row++, 1, orchResponse.getOneDocPid().asString());

                table.setText(row, 0, "Two document Patient ID");
                table.setText(row++, 1, orchResponse.getTwoDocPid().asString());

                table.setText(row, 0, "Two RGs Patient ID");
                table.setText(row++, 1, orchResponse.getTwoRgPid().asString());

                table.setHTML(row++, 0, "<h3>Simulators</h3>");

                for (SimulatorConfig config : testTab.rgConfigs) {
                    table.setWidget(row, 0, new HTML("<h3>Simulator ID</h3>"));
                    table.setWidget(row++, 1, new HTML(config.getId().toString()));

                    table.setText(row, 0, "homeCommunityId");
                    table.setWidget(row++, 1, new HTML(config.get(SimulatorProperties.homeCommunityId).asString()));

                    table.setText(row, 0, "Responding Gateway");
                    table.setText(row, 1, "Query");
                    table.setText(row++, 2, config.getConfigEle(SimulatorProperties.xcqEndpoint).asString());

                    table.setText(row, 1, "Retrieve");
                    table.setText(row++, 2, config.getConfigEle(SimulatorProperties.xcrEndpoint).asString());

                    table.setText(row, 0, "Repository");
                    table.setText(row, 1, "Provide and Register");
                    table.setText(row++, 2, config.getConfigEle(SimulatorProperties.pnrEndpoint).asString());

                    table.setText(row, 1, "Retrieve");
                    table.setText(row++, 2, config.getConfigEle(SimulatorProperties.retrieveEndpoint).asString());

                    table.setText(row, 0, "Registry");
                    table.setText(row, 1, "Register");
                    table.setText(row++, 2, config.getConfigEle(SimulatorProperties.registerEndpoint).asString());

                    table.setText(row, 1, "Query");
                    table.setText(row++, 2, config.getConfigEle(SimulatorProperties.storedQueryEndpoint).asString());

                    panel().add(testTab.addTestEnvironmentInspectorButton(config.getId().toString()));
                }

                // generate log launcher buttons
//                    panel().add(addTestEnvironmentInspectorButton(rgConfigs.get(0).getId().toString()));
                panel().add(testTab.testSelectionManager.buildLogLauncher(testTab.rgConfigs));

                testTab.genericQueryTab.reloadTransactionOfferings();
            }
        });
    }
}
