package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RigOrchestrationRequest;
import gov.nist.toolkit.services.client.RigOrchestrationResponse;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.BuildRigTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRigTestOrchestrationRequest;

/**
 * Created by smm on 10/9/16.
 */

public class BuildRIGTestOrchestrationButton extends AbstractOrchestrationButton {
    private ConformanceTestTab testTab;
    private Panel initializationPanel;
    private FlowPanel initializationResultsPanel = new FlowPanel();
    private TestContext testContext;
    private TestContextView testContextView;

    BuildRIGTestOrchestrationButton(ConformanceTestTab testTab, TestContext testContext, TestContextView testContextView, Panel initializationPanel, String label) {
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;
        this.testContext = testContext;
        this.testContextView = testContextView;

        HTML instructions = new HTML(
                "<p>" +
                        "The System Under Test (SUT) is a Responding Imaging Gateway. " +
                        "The diagram below shows the test environment with the SUT in orange. " +
                        "The test software creates and configures the simulators in the diagram. " +
                "</p>" +
                "<p>"  +
                        "You need to configure your Responding Imaging Gateway to communicate with " +
                        "the Imaging Document Source simulators shown in the diagram. " +
                        "The tables immediately below describe the environment at a high level with " +
                        "values for homeCommunityID's and repositoryUniqueID's. " +
                        "We use fixed values for homeCommunityID’s. " +
                "</p>" +
                        "<table border=\"1\">" +
                        "<tr><th>Community / System</th><th>homeCommunityID</th></tr>" +
                        "<tr bgcolor=\"#FFA500\"><td>Under Test: Responding Imaging Gateway</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.201</td></tr>" +
                        "<tr bgcolor=\"#FFFFFF\"><td>Initiating Imaging Gateway (Simulator)</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.202</td></tr>" +
                        "</table>" +
                "<br/>" +
                        "<table border=\"1\">" +
                        "<tr><th>Imaging Document Source (Simulator)</th><th>Repository Unique ID</th></tr>" +
                        "<tr><td>E</td><td>1.3.6.1.4.1.21367.13.71.201.1</td></tr>" +
                        "<tr><td>F</td><td>1.3.6.1.4.1.21367.13.71.201.2</td></tr>" +
                        "<tr><td>G</td><td>1.3.6.1.4.1.21367.13.71.201.3</td></tr>" +
                        "<tr><td>Unknown (do not configure in your Responding Imaging Gateway; <br/>used to test error conditions)</td>" +
                        "<td>1.3.6.1.4.1.21367.13.71.201.2.999</td></tr>" +
                        "</table>" +

                        "<p>" +
                        "After you have initialized the test environment, you should see the full set of configuration " +
                        "parameters needed to configure and test your system. " +
                        "</p>" +
                        "<p>"  +
                        "Note that your Initiating Imaging Gateway only communicates with the Responding Imaging Gateway simulators. " +
                        "Your system will not connect directly to any of the Imaging Document Source simulators." +
                        "</p>" +
                "<p>"  +
                        "Tests are run using three DICOM transfer syntaxes. The UIDs for these are:" +
                        "<ul><li>1.2.840.10008.1.2.1</li>" +
                        "<li>1.2.840.10008.1.2.4.50</li>" +
                        "<li>1.2.840.10008.1.2.4.70</li></ul>" +
                "</p>" +
                "<p>"  +
                        "In some cases, the Imaging Document Source simulator will respond to RAD-69 retrieve requests " +
                        "with images that are encoded with a requested transfer syntax. " +
                        "In other cases, the Imaging Document Source simulator will have the image " +
                        "but will not be able to supply it in the requested transfer syntax." +
                "</p>"
        );

        initializationPanel.add(instructions);

        setSystemDiagramUrl("diagrams/RIGdiagram.png");

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

        RigOrchestrationRequest request = new RigOrchestrationRequest();
        request.setUserName(testTab.getCurrentTestSession());
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseExistingState(!isResetRequested());
        SiteSpec siteSpec = new SiteSpec(testContext.getSiteName());
        /*
        if (isSaml()) {
            setSamlAssertion(siteSpec);
        }
        */
        request.setSiteUnderTest(siteSpec);

        testTab.setSiteToIssueTestAgainst(siteSpec);

        new BuildRigTestOrchestrationCommand(){
            @Override
            public void onComplete(RawResponse rawResponse) {
                if (handleError(rawResponse, RigOrchestrationResponse.class)) return;
                RigOrchestrationResponse orchResponse = (RigOrchestrationResponse) rawResponse;
                testTab.setOrchestrationResponse(orchResponse);

                initializationResultsPanel.add(new HTML("Initialization Complete"));

                if (testContext.getSiteUnderTest() != null) {
                    initializationResultsPanel.add(new HTML("<h2>System Under Test Configuration</h2>"));
                    initializationResultsPanel.add(new HTML("Site: " + testContext.getSiteUnderTest().getName()));
                    FlexTable table = new FlexTable();
                    int row = 0;
                    table.setText(row, 0, "Home Community ID: ");
                    try {
                        table.setText(row++, 1, testContext.getSiteUnderTest().getHome());
                        //        .getRawEndpoint(TransactionType.PROVIDE_AND_REGISTER, false, false));
                    } catch (Exception e) {
                        initializationResultsPanel.add(new HTML("Exception:Display SUT home: " + e.getMessage()));
                    }

                    table.setText(row, 0, "Retrieve Img Doc Set: ");
                    try {
                        String ep = testContext.getSiteUnderTest().getRawEndpoint(TransactionType.RET_IMG_DOC_SET_GW, false, false);
                        table.setText(row++, 1, ep);
                    } catch (Exception e) {
                        initializationResultsPanel.add(new HTML("Exception:Display SUT endpoint: " + e.getMessage()));
                    }
                    initializationResultsPanel.add(table);
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
                            if (prop.isList()) value = prop.asList().toString();
                            table.setWidget(row++ , 2, new HTML(value));
                        }
                    } catch (Exception e) {
                        initializationResultsPanel.add(new HTML("<h3>exception " + o.name() + " " + e.getMessage() + "/h3>"));
                    }
                }
                initializationResultsPanel.add(table);

                initializationResultsPanel.add(new HTML("<p>Configure your " +
                        "Responding Imaging Gateway SUT to forward Retrieve Imaging " +
                        "Document Set Requests to these Imaging Document Sources<hr/>"));
            }
        }.run(new BuildRigTestOrchestrationRequest(ClientUtils.INSTANCE.getCommandContext(),request));
    } @SuppressWarnings("javadoc")
    public enum Orchestra {
       
       ids_e ("Imaging Document Source E", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] {
          new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.201.1"),
          new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "xca-dataset-e")}),
       
       ids_f ("Imaging Document Source F", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] {
          new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.201.2"),
          new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "xca-dataset-f")}),
       
       ids_g ("Imaging Document Source G", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] {
          new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.201.3"),
          new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "xca-dataset-g")}),
       
       simulator_rig ("Simulated RIG SUT", ActorType.RESPONDING_IMAGING_GATEWAY, new SimulatorConfigElement[] {
          new SimulatorConfigElement(SimulatorProperties.homeCommunityId, ParamType.TEXT, "urn:oid:1.3.6.1.4.1.21367.13.70.201"),
          new SimulatorConfigElement(SimulatorProperties.imagingDocumentSources, ParamType.SELECTION, new String[] {"${user}__ids_e","${user}__ids_f","${user}__ids_g"}, true)}),
          
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
             case RESPONDING_IMAGING_GATEWAY:
                return new String[] {
                   SimulatorProperties.homeCommunityId,
                   SimulatorProperties.xcirEndpoint,
                   // SimulatorProperties.xcirTlsEndpoint,
                   SimulatorProperties.imagingDocumentSources,
                };
             case IMAGING_DOC_SOURCE:
                return new String[] {
                   SimulatorProperties.idsRepositoryUniqueId,
                   SimulatorProperties.idsrEndpoint,
                   //SimulatorProperties.idsrTlsEndpoint,
                   SimulatorProperties.idsImageCache,
                };
             case INITIATING_IMAGING_GATEWAY:
                return new String[] {
                   SimulatorProperties.idsrIigEndpoint,
                   //SimulatorProperties.idsrTlsEndpoint,
                   SimulatorProperties.respondingImagingGateways,
                };
                default:
          }
          return new String[0];
       }
       
    } // EO Orchestra enum


}
