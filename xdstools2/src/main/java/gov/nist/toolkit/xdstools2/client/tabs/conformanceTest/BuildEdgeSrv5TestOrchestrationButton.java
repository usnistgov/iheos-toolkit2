package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.EdgeSrv5OrchestrationRequest;
import gov.nist.toolkit.services.client.EdgeSrv5OrchestrationResponse;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.BuildEdgeSrv5TestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildEdgeSrv5TestOrchestrationRequest;

public class BuildEdgeSrv5TestOrchestrationButton extends AbstractOrchestrationButton {
    private ConformanceTestTab testTab;
    private Panel initializationPanel;
    private FlowPanel initializationResultsPanel = new FlowPanel();
    private TestContext testContext;
    private TestContextView testContextView;

    public BuildEdgeSrv5TestOrchestrationButton(ConformanceTestTab testTab, TestContext testContext, TestContextView testContextView,
                                         Panel initializationPanel, String label) {
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;
        this.testContext = testContext;
        this.testContextView = testContextView;

        HTML instructions = new HTML(
                "<p>" +
                        "The System Under Test (SUT) is a Version 5 Edge Server. " +
                        "The diagram below shows the test environment with the SUT in orange. " +
                        "The test software creates and configures the simulators in the diagram. " +
                        "</p>" +

                        "<p>" +
                        "After you have initialized the test environment, you should see the full set of configuration " +
                        "parameters needed to configure and test your system. " +
                        "</p>" +

                        "<p>"  +
                        "You need to configure your Edge Server to communicate with the " +
                        "simulators shown in the diagram. " +
                        "</p>");

        initializationPanel.add(instructions);

        setSystemDiagramUrl("diagrams/Edge5diagram.png");

        setParentPanel(initializationPanel);
        setLabel(label);
        setResetLabel("Reset");
        build();
        panel().add(initializationResultsPanel);

    }

    public void orchestrate() {
        String msg = testContext.verifyTestContext();
        if (msg != null) {
            testContextView.launchDialog(msg);
            return;
        }

        initializationResultsPanel.clear();
        testTab.getMainView().showLoadingMessage("Initializing...");

        EdgeSrv5OrchestrationRequest request = new EdgeSrv5OrchestrationRequest();
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

        new BuildEdgeSrv5TestOrchestrationCommand(){
            @Override
            public void onComplete(RawResponse rawResponse) {
                if (handleError(rawResponse, EdgeSrv5OrchestrationResponse.class)) {
                    testTab.getMainView().clearLoadingMessage();
                    return;
                }
                EdgeSrv5OrchestrationResponse orchResponse = (EdgeSrv5OrchestrationResponse) rawResponse;
                testTab.setOrchestrationResponse(orchResponse);

                initializationResultsPanel.add(new HTML("Initialization Complete"));

                if (testContext.getSiteUnderTest() != null) {
                    initializationResultsPanel.add(new HTML("<h2>System Under Test Configuration</h2>"));
                    initializationResultsPanel.add(new HTML("Site: " + testContext.getSiteUnderTest().getName()));
                    // TODO
                }


                initializationResultsPanel.add(new HTML("<h2>Generated Environment</h2>"));

                FlexTable table = new FlexTable();
                int row = 0;
                // Pass through simulators in Orchestra enum order
                for (Orchestra o : Orchestra.values()) {
                    // get matching simulator config
                    SimulatorConfig sim = null;
                    for (SimulatorConfig c : orchResponse.getSimulatorConfigs()) {
                        if (c.getId().getId().equals(o.name())) {
                            sim = c;
                            break;
                        }
                    }
                    if (sim == null) continue;

                    try {
                        // First row: title, sim id, test data and log buttons
                        table.setWidget(row, 0, new HTML("<h3>" + o.title + "</h3>"));
                        table.setText(row++ , 1, sim.getId().toString());

                        // Property rows, based on ActorType and Orchestration enum
                        for (String property : o.getDisplayProps()) {
                            table.setWidget(row, 1, new HTML(property));
                            SimulatorConfigElement prop = sim.get(property);
                            String value = prop.asString();
                            if (prop.hasList()) value = prop.asList().toString();
                            table.setWidget(row++ , 2, new HTML(value));
                        }
                    } catch (Exception e) {
                        initializationResultsPanel.add(new HTML("<h3>exception " + o.name() + " " + e.getMessage() + "/h3>"));
                    }
                }
                initializationResultsPanel.add(table);


                testTab.displayTestCollection(testTab.getMainView().getTestsPanel());
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
