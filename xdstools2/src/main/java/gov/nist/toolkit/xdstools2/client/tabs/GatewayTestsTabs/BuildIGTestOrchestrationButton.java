package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.services.client.IgOrchestrationRequest;
import gov.nist.toolkit.services.client.IgOrchestrationResponse;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;

/**
 *
 */
class BuildIGTestOrchestrationButton extends AbstractOrchestrationButton {
    private IGTestTab testTab;
    private boolean includeIG;

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
        ClientUtils.INSTANCE.getToolkitServices().buildIgTestOrchestration(request, new AsyncCallback<RawResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleError(throwable);
            }

            @Override
            public void onSuccess(RawResponse rawResponse) {
                if (handleError(rawResponse, IgOrchestrationResponse.class)) return;
                IgOrchestrationResponse orchResponse = (IgOrchestrationResponse) rawResponse;

                testTab.rgConfigs = orchResponse.getSimulatorConfigs();

                panel().add(new HTML("<h2>Test Environment</h2>"));
                FlexTable table = new FlexTable();
                panel().add(table);
                int row = 0;

                Widget w;

                table.setWidget(row, 0, new HTML("<h3>Test data pattern</h3>"));
                table.setWidget(row++, 1, new HTML("<h3>Patient ID</h3>"));

//                table.setWidget(row++, 0, new HTML("Each Patient is configured with records to support a different test environment."));

                table.setWidget(row, 0, new HTML("Single document in Community 1"));
                table.setWidget(row++, 1, new HTML(orchResponse.getOneDocPid().asString()));

                table.setWidget(row, 0, new HTML("Two documents in Community 1"));
                table.setWidget(row++, 1, new HTML(orchResponse.getTwoDocPid().asString()));

                table.setWidget(row, 0, new HTML("Both Communities have documents"));
                table.setWidget(row++, 1, new HTML(orchResponse.getTwoRgPid().asString()));

                table.setWidget(row, 0, new HTML("Community 1 returns XDSRegistryError, Community 2 a single DocumentEntry"));
                table.setWidget(row++, 1, new HTML(orchResponse.getUnknownPid().asString()));

                table.setWidget(row++, 0, new HTML("<h3>Simulators</h3>"));

                int i=1;
                for (SimulatorConfig config : testTab.rgConfigs) {
                    table.setWidget(row, 0, new HTML("<h3>Community " + i++ + "</h3>"));
                    HorizontalPanel community = new HorizontalPanel();
                    community.add(new HTML(config.getId().toString()));
                    community.add(testTab.addTestEnvironmentInspectorButton(config.getId().toString(), "Test Data"));
                    community.add(testTab.testSelectionManager.buildLogLauncher(config.getId().toString(), "Simulator Log"));
                    table.setWidget(row++, 1, community);

                    table.setWidget(row, 0, new HTML("homeCommunityId"));
                    table.setWidget(row++, 1, new HTML(config.get(SimulatorProperties.homeCommunityId).asString()));

                    table.setWidget(row, 0, new HTML("repositoryUniqueId"));
                    table.setWidget(row++, 1, new HTML(config.get(SimulatorProperties.repositoryUniqueId).asString()));


                    table.setWidget(row++, 0, new HTML("Endpoints"));

                    table.setWidget(row, 0, new HTML("Responding Gateway"));
                    table.setWidget(row, 1, new HTML("Query"));
                    table.setWidget(row++, 2, new HTML(config.getConfigEle(SimulatorProperties.xcqEndpoint).asString()));

                    table.setWidget(row, 1, new HTML("Retrieve"));
                    table.setWidget(row++, 2, new HTML(config.getConfigEle(SimulatorProperties.xcrEndpoint).asString()));

                    table.setWidget(row, 0, new HTML("Repository"));

                    table.setWidget(row, 1, new HTML("Provide and Register"));
                    table.setWidget(row++, 2, new HTML(config.getConfigEle(SimulatorProperties.pnrEndpoint).asString()));

                    table.setWidget(row, 1, new HTML("Retrieve"));
                    table.setWidget(row++, 2, new HTML(config.getConfigEle(SimulatorProperties.retrieveEndpoint).asString()));

                    table.setWidget(row, 0, new HTML("Registry"));
                    table.setWidget(row, 1, new HTML("Register"));
                    table.setWidget(row++, 2, new HTML(config.getConfigEle(SimulatorProperties.registerEndpoint).asString()));

                    table.setWidget(row, 1, new HTML("Query"));
                    table.setWidget(row++, 2, new HTML(config.getConfigEle(SimulatorProperties.storedQueryEndpoint).asString()));

//                    panel().display(testTab.addTestEnvironmentInspectorButton(config.getId().toString()));
                }

                // generate log launcher buttons
//                panel().display(testTab.testSelectionManager.buildLogLauncher(testTab.rgConfigs));

                testTab.genericQueryTab.reloadTransactionOfferings();
            }
        });
    }

    Widget light(Widget w) {
        w.getElement().getStyle().setProperty("backgroundColor", "#f0f0f0");
        return w;
    }

    Widget dark(Widget w) {
        w.getElement().getStyle().setProperty("backgroundColor", "#d3d3d3");
        return w;
    }
}
