/**
 * 
 */
package gov.nist.toolkit.services.server.orchestration;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.client.*;
import gov.nist.toolkit.services.server.RawResponseBuilder;
import gov.nist.toolkit.services.server.ToolkitApi;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Build environment for Testing RSNA Edge Device SUT.
 * 
 * @author Matt Kelsey / MIR WUSTL IHE Development Project <a
 * href="mailto:kelseym@mir.wustl.edu">kelseym@mir.wustl.edu</a>
 *
 */
public class EdgeSrv5OrchestrationBuilder {
   static Logger log = Logger.getLogger(EdgeSrv5OrchestrationBuilder.class);

   static final String sutSimulatorName = "simulator_edge5";

   Session session;
   EdgeSrv5OrchestrationRequest request;
   ToolkitApi api;
   Util util;
   List<SimulatorConfig> simConfigs = new ArrayList<>();
   SimulatorConfig sutSimulatorConfig = null;

   public EdgeSrv5OrchestrationBuilder(ToolkitApi api, Session session, EdgeSrv5OrchestrationRequest request) {
      this.api = api;
      this.session = session;
      this.request = request;
      this.util = new Util(api);
   }
   
   public RawResponse buildTestEnvironment() {

      TestSession testSession = request.getTestSession();
      String env = request.getEnvironmentName();

      try {
         for (Orchestra sim : Orchestra.values()) {
            SimulatorConfig simConfig = null;
            SimId simId = new SimId(testSession, sim.name(), sim.actorType.getName(), env);
            if (!request.isUseExistingState()) {
               log.debug("Deleting: " + simId);
               api.deleteSimulatorIfItExists(simId);
            }
            if (!api.simulatorExists(simId)) {
               log.debug("Creating " + simId.toString());
               simConfig = api.createSimulator(simId).getConfig(0);
               // plug our special parameter values
               for (SimulatorConfigElement chg : sim.elements) {
                  chg.setEditable(true);
                  // lists of simulators may need specific user plugged in
                  if (chg.hasList()) {
                     List <String> list = chg.asList();
                     for (int i = 0; i < list.size(); i++ ) {
                        String s = list.get(i);
                        s = StringUtils.replace(s, "${user}", testSession.getValue());
                        list.set(i, s);
                     }
                     chg.setStringListValue(list);
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

} // EO Orchestration builder
