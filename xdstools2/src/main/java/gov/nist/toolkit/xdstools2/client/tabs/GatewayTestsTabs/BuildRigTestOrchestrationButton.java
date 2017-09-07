package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RigOrchestrationRequest;
import gov.nist.toolkit.services.client.RigOrchestrationResponse;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.xdstools2.client.command.command.BuildRigTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildRigTestOrchestrationRequest;

/**
 * Handles "Build Test Environment" Button for RIG Test Orchestration
 */
class BuildRIGTestOrchestrationButton extends AbstractOrchestrationButton {
   private RIGTestTab testTab;
   boolean includeRIG;

   public BuildRIGTestOrchestrationButton(RIGTestTab testTab, Panel topPanel, String label, boolean includeRIG) {
      super(topPanel, label);
      this.testTab = testTab;
      this.includeRIG = includeRIG;
   }

   @SuppressWarnings("unused")
   @Override
   public void orchestrate() {
      if (GenericQueryTab.empty(testTab.getCurrentTestSession())) {
         new PopupMessage("Must select test session first");
         return;
      }
      RigOrchestrationRequest request = new RigOrchestrationRequest();
      request.setUserName(testTab.getCurrentTestSession());
     //  request.setIncludeLinkedRIG(includeRIG);
      new BuildRigTestOrchestrationCommand(){
         @Override
         public void onComplete(RawResponse rawResponse) {
            if (handleError(rawResponse, RigOrchestrationResponse.class)) return;
            RigOrchestrationResponse orchResponse = (RigOrchestrationResponse) rawResponse;

            testTab.rgConfigs = orchResponse.getSimulatorConfigs();

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
               for (SimulatorConfig config : testTab.rgConfigs) {
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
               hp.add(testTab.testSelectionManager.buildLogLauncher(sim.getId().toString(), "Simulator Log"));
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
      }.run(new BuildRigTestOrchestrationRequest(ClientUtils.INSTANCE.getCommandContext(),request));

   }
   @SuppressWarnings("javadoc")
   public enum Orchestra {
      
      ids_e ("Imaging Document Source E", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] {
         new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.201.1"),
         new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "ids-dataset-E")}),
      
      ids_f ("Imaging Document Source F", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] {
         new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.201.2"),
         new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "ids-dataset-F")}),
      
      ids_g ("Imaging Document Source G", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] {
         new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.201.3"),
         new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "ids-dataset-G")}),
      
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
                  //SimulatorProperties.idsImageCache,
               };
            case INITIATING_IMAGING_GATEWAY:
               return new String[] {
                  SimulatorProperties.idsrEndpoint,
                  //SimulatorProperties.idsrTlsEndpoint,
                  SimulatorProperties.respondingImagingGateways,
               };
               default:
         }
         return new String[0];
      }
      
   } // EO Orchestra enum
}
