package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.client.IdsOrchestrationRequest;
import gov.nist.toolkit.services.client.IdsOrchestrationResponse;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.command.command.BuildIdsTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildIdsTestOrchestrationRequest;


/**
 *
 */
class BuildIDSTestOrchestrationButton extends AbstractOrchestrationButton {
    private IDSTestTab testTab;
    boolean includeIG;

    BuildIDSTestOrchestrationButton(IDSTestTab testTab, Panel topPanel, String label, boolean includeIG) {
        super(topPanel, label);
        this.testTab = testTab;
        this.includeIG = includeIG;
    }

    public void orchestrate() {
        if (GenericQueryTab.empty(testTab.getCurrentTestSession())) {
            new PopupMessage("Must select test session first");
            return;
        }
        IdsOrchestrationRequest request = new IdsOrchestrationRequest();
        request.setTestSession(new TestSession(testTab.getCurrentTestSession()));
        new BuildIdsTestOrchestrationCommand(){
            @Override
            public void onComplete(RawResponse rawResponse) {
                if (handleError(rawResponse, IdsOrchestrationResponse.class)) return;
                IdsOrchestrationResponse orchResponse = (IdsOrchestrationResponse) rawResponse;

                testTab.rrConfig = orchResponse.getRRConfig();

                panel().add(new HTML("<h2>Test Environment</h2>"));
                FlexTable table = new FlexTable();
                panel().add(table);
                int row = 0;

                Widget w;



                table.setWidget(row, 0, new HTML("<h3>Test data pattern</h3>"));

                SimulatorConfig config = testTab.rrConfig;

                table.setWidget(row++, 0, new HTML("<h4>Repository UID</h4>"));

                table.setWidget(row, 0, new HTML("repositoryUniqueId"));
                table.setWidget(row++, 1, new HTML(config.get(SimulatorProperties.repositoryUniqueId).asString()));


                table.setWidget(row++, 0, new HTML("Endpoints"));

                table.setWidget(row, 1, new HTML("Provide and Register"));
                table.setWidget(row++, 2, new HTML(config.getConfigEle(SimulatorProperties.pnrEndpoint).asString()));

                table.setWidget(row, 1, new HTML("Provide and Register TLS"));
                table.setWidget(row++, 2, new HTML(config.getConfigEle(SimulatorProperties.pnrTlsEndpoint).asString()));

                testTab.genericQueryTab.reloadTransactionOfferings();
            }
        }.run(new BuildIdsTestOrchestrationRequest(ClientUtils.INSTANCE.getCommandContext(),request));
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
