/**
 * 
 */
package gov.nist.toolkit.services.server.orchestration;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.client.RSNAEdgeOrchestrationRequest;
import gov.nist.toolkit.services.client.RSNAEdgeOrchestrationResponse;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.server.RawResponseBuilder;
import gov.nist.toolkit.services.server.ToolkitApi;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Build environment for Testing RSNA Edge Device SUT.
 * 
 * @author Matt Kelsey / MIR WUSTL IHE Development Project <a
 * href="mailto:kelseym@mir.wustl.edu">kelseym@mir.wustl.edu</a>
 *
 */
public class RSNAEdgeOrchestrationBuilder {
   static Logger log = Logger.getLogger(RSNAEdgeOrchestrationBuilder.class);

   static final String sutSimulatorName = "simulator_rsnaedge";

   Session session;
   RSNAEdgeOrchestrationRequest request;
   ToolkitApi api;
   Util util;
   List<SimulatorConfig> simConfigs = new ArrayList<>();
   SimulatorConfig sutSimulatorConfig = null;

   public RSNAEdgeOrchestrationBuilder(ToolkitApi api, Session session, RSNAEdgeOrchestrationRequest request) {
      this.api = api;
      this.session = session;
      this.request = request;
      this.util = new Util(api);
   }
   
   public RawResponse buildTestEnvironment() {

      String supportIdName = "rr";
      TestSession testSession = request.getTestSession();
      String env = request.getEnvironmentName();
      
      try {

         SimId supportId = new SimId(testSession, supportIdName, ActorType.REPOSITORY_REGISTRY.getName(), env);
         api.deleteSimulatorIfItExists(supportId);
         SimulatorConfig supportSimConfig = api.createSimulator(supportId).getConfig(0);

         SimulatorConfigElement idsEle;
         idsEle = supportSimConfig.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED);
         idsEle.setBooleanValue(false);
         idsEle = supportSimConfig.getConfigEle(SimulatorProperties.repositoryUniqueId);
         idsEle.setStringValue("urn:oid:1.3.6.1.4.1.21367.13.70.101");

         api.saveSimulator(supportSimConfig);

         RSNAEdgeOrchestrationResponse response = new RSNAEdgeOrchestrationResponse();
         response.setSimulatorConfig(supportSimConfig);
         return response;

      } catch (Exception e) {
         return RawResponseBuilder.build(e);
      }
   } // EO build test environment

} // EO IigOrchestration builder
