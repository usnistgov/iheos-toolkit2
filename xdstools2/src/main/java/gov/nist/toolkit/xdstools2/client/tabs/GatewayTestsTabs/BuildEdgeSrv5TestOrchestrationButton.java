package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.client.*;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.BuildEdgeSrv5TestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.command.command.BuildRigTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs.BuildRIGTestOrchestrationButton;
import gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs.EdgeSrv5TestTab;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildEdgeSrv5TestOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRigTestOrchestrationRequest;

public class BuildEdgeSrv5TestOrchestrationButton extends AbstractOrchestrationButton {
    private EdgeSrv5TestTab testTab;
    boolean includeES5;

    public BuildEdgeSrv5TestOrchestrationButton(EdgeSrv5TestTab testTab, Panel topPanel, String label, boolean includeES5 ) {
        super(topPanel, label);
        this.testTab = testTab;
        this.includeES5 = includeES5;
    }

    @SuppressWarnings("unused")
    @Override
    public void orchestrate() {
        if (GenericQueryTab.empty(testTab.getCurrentTestSession())) {
            new PopupMessage("Must select test session first");
            return;
        }
        EdgeSrv5OrchestrationRequest request = new EdgeSrv5OrchestrationRequest();
        request.setTestSession(new TestSession(testTab.getCurrentTestSession()));

        new BuildEdgeSrv5TestOrchestrationCommand(){
            @Override
            public void onComplete(RawResponse rawResponse) {
                if (handleError(rawResponse, EdgeSrv5OrchestrationResponse.class)) return;
                EdgeSrv5OrchestrationResponse orchResponse = (EdgeSrv5OrchestrationResponse) rawResponse;

                testTab.setRgConfigs(orchResponse.getSimulatorConfigs());

                panel().add(new HTML("<h2>Test Environment</h2>"));
                FlexTable table = new FlexTable();
                panel().add(table);
                int row = 0;

                table.setWidget(row++ , 0, new HTML("<h3>Simulators</h3>"));

                int i = 1;
                // Pass through simulators in order of Orchestra enum
                for (Orchestra o : Orchestra.values()) {
                    // Get matching simulator config
                    SimulatorConfig sim = null;
                    for (SimulatorConfig config : testTab.getRgConfigs()) {
                        if (config.getId().getId().equals(o.name())) {
                            sim = config;
                            break;
                        }
                    }
                    if (sim == null) {
                        new PopupMessage("Internal error: Simulator " + o.name() + " not found");
                        continue;
                    }

                    // First row: title, sim id, test data and log buttons
                    table.setWidget(row, 0, new HTML("<h3>" + o.title + "</h3>"));
                    HorizontalPanel hp = new HorizontalPanel();
                    hp.add(new HTML(sim.getId().toString()));
                    hp.add(testTab.addTestEnvironmentInspectorButton(sim.getId().toString(), "Test Data"));
                    hp.add(testTab.getTestSelectionManager().buildLogLauncher(sim.getId().toString(), "Simulator Log"));
                    table.setWidget(row++ , 1, hp);

                    // Property rows, based on ActorType and Orchestration enum
                    for (String property : o.getDisplayProps()) {
                        table.setWidget(row, 1, new HTML(property));
                        SimulatorConfigElement prop = sim.get(property);
                        String value = prop.asString();
                        if (prop.hasList()) value = prop.asList().toString();
                        table.setWidget(row++ , 2, new HTML(value));
                    }
                    testTab.genericQueryTab.reloadTransactionOfferings();

                } // pass Orchestration
            }
        }.run(new BuildEdgeSrv5TestOrchestrationRequest(ClientUtils.INSTANCE.getCommandContext(),request));

    }

    @SuppressWarnings("javadoc")
    public enum Orchestra {

        ch_reg ("Clearinghouse Registry", ActorType.REGISTRY, new SimulatorConfigElement[] { }),
        ch_rep ("Clearinghouse Repository", ActorType.REPOSITORY, new SimulatorConfigElement[] { }),
        ch_ids ("Clearinghouse Registry", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] { }),
        ;

        public final String title;
        public final ActorType actorType;
        public final SimulatorConfigElement[] elements;

        Orchestra (String title, ActorType actorType, SimulatorConfigElement[] elements) {
            this.title = title;
            this.actorType = actorType;
            this.elements = elements;
        }

        public ActorType getActorType() {
            return actorType;
        }
        public SimulatorConfigElement[] getElements() {
            return elements;
        }

        public String[] getDisplayProps() {
            // TODO
            switch (actorType) {
                case REGISTRY:
                case REPOSITORY:
                case IMAGING_DOC_SOURCE:
                default:
            }
            return new String[0];
        }
    }
}
