package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.services.client.IigOrchestrationRequest;
import gov.nist.toolkit.services.client.IigOrchestrationResponse;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.simcommon.shared.config.SimulatorConfigElement;
import gov.nist.toolkit.xdstools2.client.command.command.BuildIIGTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildIigTestOrchestrationRequest;

/**
 * Handles "Build Test Environment" Button for IIG Test Orchestration
 */
class BuildIIGTestOrchestrationButton extends AbstractOrchestrationButton {
   private IIGTestTab testTab;
   boolean includeIIG;

   public BuildIIGTestOrchestrationButton(IIGTestTab testTab, Panel topPanel, String label, boolean includeIIG) {
      super(topPanel, label);
      this.testTab = testTab;
      this.includeIIG = includeIIG;
   }

   @SuppressWarnings("unused")
   @Override
   public void orchestrate() {
      if (GenericQueryTab.empty(testTab.getCurrentTestSession())) {
         new PopupMessage("Must select test session first");
         return;
      }
      IigOrchestrationRequest request = new IigOrchestrationRequest();
      request.setUserName(testTab.getCurrentTestSession());
      // request.setIncludeLinkedIIG(includeIIG);
      new BuildIIGTestOrchestrationCommand(){
         @Override
         public void onComplete(RawResponse rawResponse) {
            if (handleError(rawResponse, IigOrchestrationResponse.class)) return;
            IigOrchestrationResponse orchResponse = (IigOrchestrationResponse) rawResponse;

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
      }.run(new BuildIigTestOrchestrationRequest(ClientUtils.INSTANCE.getCommandContext(),request));
   }
   @SuppressWarnings("javadoc")
   public enum Orchestra {
      
      rig_a ("Responding Imaging Gateway A", ActorType.RESPONDING_IMAGING_GATEWAY, new SimulatorConfigElement[] {
         new SimulatorConfigElement(SimulatorProperties.homeCommunityId, ParamType.TEXT, "urn:oid:1.3.6.1.4.1.21367.13.70.101"),
         new SimulatorConfigElement(SimulatorProperties.imagingDocumentSources, ParamType.SELECTION, new String[] {"${user}__ids_a1","${user}__ids_a2"}, true)}),
      
      ids_a1 ("Imaging Document Source A1", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] {
         new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.101"),
         new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "ids-community-A")}),
      
      ids_a2 ("Imaging Document Source A2", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] {
         new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.101.1"),
         new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "ids-community-B")}),
      
      rig_b ("Responding Imaging Gateway B", ActorType.RESPONDING_IMAGING_GATEWAY, new SimulatorConfigElement[] {
         new SimulatorConfigElement(SimulatorProperties.homeCommunityId, ParamType.TEXT, "urn:oid:1.3.6.1.4.1.21367.13.70.102"),
         new SimulatorConfigElement(SimulatorProperties.imagingDocumentSources, ParamType.SELECTION, new String[] {"${user}__ids_b1"}, true)}),
      
      ids_b1 ("Imaging Document Source B1", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] {
         new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.102"),
         new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "ids-community-A")}),
      
      rig_c ("Responding Imaging Gateway C", ActorType.RESPONDING_IMAGING_GATEWAY, new SimulatorConfigElement[] {
         new SimulatorConfigElement(SimulatorProperties.homeCommunityId, ParamType.TEXT, "urn:oid:1.3.6.1.4.1.21367.13.70.103"),
         new SimulatorConfigElement(SimulatorProperties.imagingDocumentSources, ParamType.SELECTION, new String[] {"${user}__ids_c1"}, true)}),
      
      ids_c1 ("Imaging Document Source C1", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] {
         new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.103"),
         new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "ids-community-C")}),
      
      simulator_iig ("Simulated IIG SUT", ActorType.INITIATING_IMAGING_GATEWAY, new SimulatorConfigElement[] {
         new SimulatorConfigElement(SimulatorProperties.respondingImagingGateways, ParamType.SELECTION, new String[] {"${user}__rig_a","${user}__rig_b","${user}__rig_c"}, true),
         
      });      
      
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
