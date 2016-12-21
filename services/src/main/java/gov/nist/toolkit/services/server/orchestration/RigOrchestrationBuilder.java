/**
 * 
 */
package gov.nist.toolkit.services.server.orchestration;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.services.client.*;
import gov.nist.toolkit.services.server.RawResponseBuilder;
import gov.nist.toolkit.services.server.ToolkitApi;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;

/**
 * Build environment for Testing Initiating Imaging Gateway SUT.
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class RigOrchestrationBuilder { 
   static Logger log = Logger.getLogger(IigOrchestrationBuilder.class);   

   static final String sutSimulatorName = "simulator_rig";

   Session session;
   RigOrchestrationRequest request;
   ToolkitApi api;
   Util util;
   List<SimulatorConfig> simConfigs = new ArrayList<>();
   SimulatorConfig sutSimulatorConfig = null;
   
   public RigOrchestrationBuilder(ToolkitApi api, Session session, RigOrchestrationRequest request) {
      this.api = api;
      this.session = session;
      this.request = request;
      this.util = new Util(api);
   }
   
   public RawResponse buildTestEnvironment() {
      
      String user = request.getUserName();
      String env = request.getEnvironmentName();
      
      try {
         for (Orchestra sim : Orchestra.values()) {
            SimulatorConfig simConfig = null;
            SimId simId = new SimId(user, sim.name(), sim.actorType.getName(), env);
            if (!request.isUseExistingSimulator() || !api.simulatorExists(simId)) {
            api.deleteSimulatorIfItExists(simId);
            log.debug("Creating " + simId.toString());
            simConfig = api.createSimulator(simId).getConfig(0);
            // plug our special parameter values
            for (SimulatorConfigElement chg : sim.elements) {
               chg.setEditable(true);
               // lists of simulators may need specific user plugged in
               if (chg.isList()) {
                     List <String> list = chg.asList();
                     for (int i = 0; i < list.size(); i++ ) {
                        String s = list.get(i);
                        s = StringUtils.replace(s, "${user}", user);
                        list.set(i, s);
                     }
                     chg.setValue(list);
               }
               simConfig.replace(chg);
            }
            api.saveSimulator(simConfig);
            } else {
               simConfig = api.getConfig(simId);
            }
            if (sim.name().equals(sutSimulatorName)) sutSimulatorConfig = simConfig;
            simConfigs.add(simConfig);
         }

         RigOrchestrationResponse response = new RigOrchestrationResponse();
         response.setRigSimulatorConfig(sutSimulatorConfig);
         response.setSimulatorConfigs(simConfigs);
         return response;

      } catch (Exception e) {
         return RawResponseBuilder.build(e);
      }
   } // EO build test environment
   @SuppressWarnings("javadoc")
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
