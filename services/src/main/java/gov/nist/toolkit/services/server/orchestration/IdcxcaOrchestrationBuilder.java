/**
 * 
 */
package gov.nist.toolkit.services.server.orchestration;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.results.shared.SiteBuilder;
import gov.nist.toolkit.services.client.*;
import gov.nist.toolkit.services.server.RawResponseBuilder;
import gov.nist.toolkit.services.server.ToolkitApi;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Build environment for testing Imaging Document Consumer in XCA SUT.
 */
public class IdcxcaOrchestrationBuilder {
   static Logger log = Logger.getLogger(IdsOrchestrationBuilder.class);

   public static final String rrSimulatorName = "rr";

   Session session;
   IdcxcaOrchestrationRequest request;
   ToolkitApi api;
   Util util;
   List <SimulatorConfig> simConfigs = new ArrayList <>();
   SimulatorConfig sutSimulatorConfig = null;
   SimulatorConfig rrSimulatorConfig = null;

   public IdcxcaOrchestrationBuilder(ToolkitApi api, Session session, IdcxcaOrchestrationRequest request) {
      this.api = api;
      this.session = session;
      this.request = request;
      this.util = new Util(api);
   }

   public RawResponse buildTestEnvironment() {

      String user = request.getUserName();
      String env = request.getEnvironmentName();
      IdcxcaOrchestrationResponse response = new IdcxcaOrchestrationResponse();

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
               if (chg.hasList()) {
                  List <String> list = chg.asList();
                  for (int i = 0; i < list.size(); i++ ) {
                     String s = list.get(i);
                     s = StringUtils.replace(s, "${user}", user);
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
            if (sim.name().equals(rrSimulatorName)) rrSimulatorConfig = simConfig;
            simConfigs.add(simConfig);
         }

         response.setRRConfig(rrSimulatorConfig);
         response.setSimulatorConfigs(simConfigs);

         TestInstance initTest =
            TestInstanceManager.initializeTestInstance(request.getUserName(), new TestInstance("idc_init"));
         MessageItem initMsgItem = response.addMessage(initTest, true, "");
         try {
            util.submit(request.getUserName(), SiteBuilder.siteSpecFromSimId(rrSimulatorConfig.getId()), initTest);
         } catch (Exception e) {
            initMsgItem.setMessage("Initialization of " + rrSimulatorConfig.getId() + " failed:\n" + e.getMessage());
            initMsgItem.setSuccess(false);
         }

         return response;

      } catch (Exception e) {
         return RawResponseBuilder.build(e);
      }
   } // EO build test environment

   public enum Orchestra {

      ids("Imaging Document Source", ActorType.IMAGING_DOC_SOURCE,
         new SimulatorConfigElement[] {
            new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT,
               "1.3.6.1.4.1.21367.102.1.1"),
            new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "idc-dataset-a") }),

      rr("Repository Registry", ActorType.REPOSITORY_REGISTRY,
         new SimulatorConfigElement[] {
            new SimulatorConfigElement(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, ParamType.BOOLEAN,
               false),
            new SimulatorConfigElement(SimulatorProperties.repositoryUniqueId, ParamType.TEXT,
               "1.3.6.1.4.1.21367.13.71.101.1") }),

      rig_a ("Responding Imaging Gateway A", ActorType.RESPONDING_IMAGING_GATEWAY, new SimulatorConfigElement[] {
              new SimulatorConfigElement(SimulatorProperties.homeCommunityId, ParamType.TEXT, "urn:oid:1.3.6.1.4.1.21367.13.70.101"),
              new SimulatorConfigElement(SimulatorProperties.imagingDocumentSources, ParamType.SELECTION, new String[] {"${user}__ids_a1","${user}__ids_a2"}, true)}),

      ids_a1 ("Imaging Document Source A1", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] {
              new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.101"),
              new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "xca-dataset-a1")}),

      ids_a2 ("Imaging Document Source A2", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] {
              new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.101.1"),
              new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "xca-dataset-a2")}),

      rig_b ("Responding Imaging Gateway B", ActorType.RESPONDING_IMAGING_GATEWAY, new SimulatorConfigElement[] {
              new SimulatorConfigElement(SimulatorProperties.homeCommunityId, ParamType.TEXT, "urn:oid:1.3.6.1.4.1.21367.13.70.102"),
              new SimulatorConfigElement(SimulatorProperties.imagingDocumentSources, ParamType.SELECTION, new String[] {"${user}__ids_b1"}, true)}),

      ids_b1 ("Imaging Document Source B1", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] {
              new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.102"),
              new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "xca-dataset-b")}),

      rig_c ("Responding Imaging Gateway C", ActorType.RESPONDING_IMAGING_GATEWAY, new SimulatorConfigElement[] {
              new SimulatorConfigElement(SimulatorProperties.homeCommunityId, ParamType.TEXT, "urn:oid:1.3.6.1.4.1.21367.13.70.103"),
              new SimulatorConfigElement(SimulatorProperties.imagingDocumentSources, ParamType.SELECTION, new String[] {"${user}__ids_c1"}, true)}),

      ids_c1 ("Imaging Document Source C1", ActorType.IMAGING_DOC_SOURCE, new SimulatorConfigElement[] {
              new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.103"),
              new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "xca-dataset-c")}),

      iig ("Initiating Imaging Gateway", ActorType.INITIATING_IMAGING_GATEWAY, new SimulatorConfigElement[] {
              new SimulatorConfigElement(SimulatorProperties.respondingImagingGateways, ParamType.SELECTION, new String[] {"${user}__rig_a","${user}__rig_b","${user}__rig_c"}, true)}),

      simulator_idc ("Simulated IDC-XCAI SUT", ActorType.IMAGING_DOC_CONSUMER_XCA, new SimulatorConfigElement[] {});

      public final String title;
      public final ActorType actorType;
      public final SimulatorConfigElement[] elements;

      Orchestra(String title, ActorType actorType, SimulatorConfigElement[] elements) {
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

      /**
       * @return array of Simulator Property names which should be displayed in
       * Conformance testing for this type of actor.
       */
      public String[] getDisplayProps() {
         switch (actorType) {
            case REPOSITORY_REGISTRY:
               return new String[] { SimulatorProperties.retrieveEndpoint, SimulatorProperties.storedQueryEndpoint,
                  SimulatorProperties.repositoryUniqueId, };
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
               log.error("Unknown ActorType");
         }
         return new String[0];
      }

   } // EO Orchestra enum

} // EO IdcOrchestration builder
