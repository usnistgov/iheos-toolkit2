package gov.nist.toolkit.fhir.simulators.sim.rg

import gov.nist.toolkit.actortransaction.client.Severity
import gov.nist.toolkit.commondatatypes.MetadataSupport
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.*
import gov.nist.toolkit.errorrecording.client.XdsErrorCode
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code
import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.registrymsg.registry.AdhocQueryRequest
import gov.nist.toolkit.registrymsg.registry.AdhocQueryRequestParser
import gov.nist.toolkit.registrymsg.registry.Response
import gov.nist.toolkit.registrymsg.repository.*
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.simcommon.server.SimCommon
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SimManager
import gov.nist.toolkit.fhir.simulators.sim.reg.AdhocQueryResponseGenerator
import gov.nist.toolkit.fhir.simulators.sim.reg.RegistryActorSimulator
import gov.nist.toolkit.fhir.simulators.sim.reg.SoapWrapperRegistryResponseSim
import gov.nist.toolkit.fhir.simulators.support.*
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.TransactionBean
import gov.nist.toolkit.soap.axis2.Soap
import gov.nist.toolkit.testengine.engine.RetrieveB
import gov.nist.toolkit.utilities.xml.XmlUtil
import gov.nist.toolkit.validatorsSoapMessage.message.SoapMessageValidator
import gov.nist.toolkit.valregmsg.service.SoapActionFactory
import gov.nist.toolkit.valsupport.client.ValidationContext
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator
import gov.nist.toolkit.valsupport.message.ForcedErrorMessageValidator
import groovy.transform.TypeChecked
import org.apache.axiom.om.OMElement
import org.apache.log4j.Logger

@TypeChecked
public class RGActorSimulator extends GatewaySimulatorCommon implements MetadataGeneratingSim {
   SimDb db;
   static Logger logger = Logger.getLogger(RegistryActorSimulator.class);
   Metadata m;
   MessageValidatorEngine mvc;

   public RGActorSimulator(SimCommon common, DsSimCommon dsSimCommon, SimDb db, SimulatorConfig simulatorConfig) {
      super(common, dsSimCommon);
      this.db = db;
      setSimulatorConfig(simulatorConfig);
   }

   public RGActorSimulator(DsSimCommon dsSimCommon, SimulatorConfig simulatorConfig) {
      super(dsSimCommon.simCommon, dsSimCommon);
      this.db = dsSimCommon.simCommon.db;
      setSimulatorConfig(simulatorConfig);
   }

   public RGActorSimulator() {}

   public void init() {}


   public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {

      this.mvc = mvc;


      mvc.addMessageValidator("Forced Error", new ForcedErrorMessageValidator(common.vc, XdsErrorCode.fromString(getSimulatorConfig().get(SimulatorProperties.errors).asString())), er);

      switch (transactionType) {
         case TransactionType.XC_RETRIEVE:

            common.vc.isRequest = true;
            common.vc.isRet = true;
            common.vc.isXC = true;
            common.vc.isSimpleSoap = false;
            common.vc.hasSoap = true;
            common.vc.hasHttp = true;

            // this validates through soap wrapper
            if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
               return false;    // SOAP Fault generated

            if (mvc.hasErrors()) {
               returnRetrieveError();
               return false;
            }

            // extract retrieve request
            AbstractMessageValidator mv = dsSimCommon.getMessageValidatorIfAvailable(SoapMessageValidator.class);
            if (mv == null || !(mv instanceof SoapMessageValidator)) {
               er.err(Code.XDSRegistryError, "RG Internal Error - cannot find SoapMessageValidator instance", "RespondingGatewayActorSimulator", "");
               returnRetrieveError();
               return false;
            }

            SoapMessageValidator smv = (SoapMessageValidator) mv;
            OMElement query = smv.getMessageBody();

            SimulatorConfigElement asce = getSimulatorConfig().getUserByName(SimulatorProperties.homeCommunityId);
            if (asce == null) {
               er.err(Code.XDSUnknownCommunity, "RG Internal Error - homeCommunityId not configured", this, "");
               returnRetrieveError();
               return false;
            }
            String configuredHomeCommunityId = asce.asString();
            List<OMElement> targetHomeCommunityIdEles = XmlUtil.decendentsWithLocalName(query, "HomeCommunityId");
            for (OMElement e : targetHomeCommunityIdEles) {
               String id = e.getText();
               if (id == null)
                  id = "";
               if (id.equals("")) {
                  er.err(Code.XDSMissingHomeCommunityId, "HomeCommunityId is not included in request", this, "");
                  continue;
               }
               if (!configuredHomeCommunityId.equals(id)) {
                  er.err(Code.XDSUnknownCommunity, "HomeCommunityId in request (" +  id + ") does not match configured value (" + configuredHomeCommunityId + ")", this, "");
               }
            }

            // Find the reposistory to send the retrieve to.  It could be the one integrated with the registry in the
            // base configuration of the RG or it could be another Repository sim linked as being part of the AD.
            // This process must be repeated for each retrieve request in the transaction.  All the attempted retrieves are
            // then consolidated into the response.

            SimulatorConfigElement repUidConfEle = getSimulatorConfig().getConfigEle(SimulatorProperties.repositoryUniqueId);
            // local repository
            String configuredRepUid = repUidConfEle.asString();
            // all other reachable repositories (repUid -> site)
            Map<String, Site> repositorySiteMap = getLinkedRepositoryMap();

            Map<String, RetrievedDocumentModel> docMap = new HashMap<>();  // all retrieved documents
            RetrieveRequestModel requestModels = new RetrieveRequestParser(query).getRequest();
         for (RetrieveItemRequestModel requestModel : requestModels.getModels()) {
            boolean foundRepository = false;
            String targetRepoUid = requestModel.getRepositoryId();
            if (configuredRepUid == targetRepoUid) {
               // local repository
               String endpointLabel = (common.isTls()) ? SimulatorProperties.retrieveTlsEndpoint : SimulatorProperties.retrieveEndpoint;
               String endpoint = getSimulatorConfig().get(endpointLabel).asString();
               Map<String, RetrievedDocumentModel> aDocMap = singleRetrieve(endpoint, requestModel)
               if (!aDocMap) {
                  // error already logged
                  continue
               }
               aDocMap.each { String uid, RetrievedDocumentModel model -> docMap.put(uid, model) }  // add to overall result
               foundRepository = true
            } else {
               for (String repoUid : repositorySiteMap.keySet()) {
                  if (repoUid == targetRepoUid) {
                     // linked repository
                     Site site = repositorySiteMap[repoUid]
                     String endpoint = site.getRetrieveEndpoint(repoUid, common.isTls(), false)
                     Map<String, RetrievedDocumentModel> aDocMap = singleRetrieve(endpoint, requestModel)
                     aDocMap.each { String uid, RetrievedDocumentModel model -> docMap.put(uid, model) }  // add to overall result
                     if (!aDocMap) {
                        // error already logged
                        continue
                     }
                     foundRepository = true
                  }
               }
            }
            if (!foundRepository) {
               er.err(Code.XDSRepositoryError, "RepositoryUniqueId " + targetRepoUid + " not configured", this, "");
            }
         }

            if (mvc.hasErrors()) {
               returnRetrieveError();
               return false;
            }

            RetrievedDocumentsModel models = new RetrievedDocumentsModel(docMap)

            String home = getSimulatorConfig().get(SimulatorProperties.homeCommunityId).asString();
            models.values().each { RetrievedDocumentModel model -> model.home = home }

            logger.info("Retrieved content is " + docMap);

            StoredDocumentMap stdocmap = new StoredDocumentMap(dsSimCommon.repIndex, docMap);
            dsSimCommon.intallDocumentsToAttach(stdocmap);

            // wrap in soap wrapper and http wrapper
            //			mvc.addMessageValidator("SendResponseInSoapWrapper", new SoapWrapperResponseSim(common, dsSimCommon, result), er);

            OMElement responseEle = new RetrieveDocumentResponseGenerator(models, dsSimCommon.registryErrorList).get()
            mvc.addMessageValidator("SendResponseInSoapWrapper", new SoapWrapperResponseSim(common, dsSimCommon, responseEle), er);

            mvc.run();

            return true; // no updates anyway

         case TransactionType.XC_QUERY:

            ValidationContext vc = common.vc;
            vc.isRequest = true;
            vc.isSimpleSoap = true;
            vc.isSQ = true;
            vc.isXC = true;
            vc.xds_b = true;
            vc.hasSoap = true;
            vc.hasHttp = true;

            // run validations on message
            er.challenge("Scheduling initial validations");
            if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
               return false;   // if SOAP Fault generated

            if (mvc.hasErrors()) {
               dsSimCommon.sendErrorsInAdhocQueryResponse(er);
               return false;
            }

            // extract query
            AbstractMessageValidator mv = dsSimCommon.getMessageValidatorIfAvailable(SoapMessageValidator.class);
            if (mv == null || !(mv instanceof SoapMessageValidator)) {
               er.err(Code.XDSRegistryError, "RG Internal Error - cannot find SoapMessageValidator instance", "RespondingGatewayActorSimulator", "");
               dsSimCommon.sendErrorsInAdhocQueryResponse(er);
               return false;
            }

            SoapMessageValidator smv = (SoapMessageValidator) mv;
            OMElement query = smv.getMessageBody();

            SimulatorConfigElement asce = getSimulatorConfig().getUserByName(SimulatorProperties.homeCommunityId);
            String configuredHomeCommunityId = null;
            if (asce == null) {
               er.err(Code.XDSRegistryError, "RG Internal Error - homeCommunityId not configured", this, "");
               dsSimCommon.sendErrorsInAdhocQueryResponse(er);
               return false;
            }
            configuredHomeCommunityId = asce.asString();
            if (configuredHomeCommunityId == null || configuredHomeCommunityId.equals("")) {
               er.err(Code.XDSRegistryError, "RG Internal Error - homeCommunityId not configured", this, "");
               dsSimCommon.sendErrorsInAdhocQueryResponse(er);
               return false;
            }

            AdhocQueryRequest queryRequest = new AdhocQueryRequestParser(query).getAdhocQueryRequest();
            String homeInRequest = queryRequest.getHome();

            boolean homeRequired = !MetadataSupport.sqTakesPatientIdParam(queryRequest.queryId);
            if (homeRequired) {
               if (homeInRequest == null || homeInRequest.equals("")) {
                  er.err(Code.XDSMissingHomeCommunityId, String.format("Query %s requires Home Community Id in request", MetadataSupport.getSQName(queryRequest.queryId)), this, "");
                  dsSimCommon.sendErrorsInAdhocQueryResponse(er);
                  return false;
               }
            }

            if (homeRequired && !configuredHomeCommunityId.equals(homeInRequest)) {
               er.err(Code.XDSUnknownCommunity, "HomeCommunityId in request (" +  homeInRequest + ") does not match configured value (" + configuredHomeCommunityId + ")", this, "");
               dsSimCommon.sendErrorsInAdhocQueryResponse(er);
               return false;
            }

            // Handle forced error
            PatientErrorMap patientErrorMap = getSimulatorConfig().getConfigEle(SimulatorProperties.errorForPatient).asPatientErrorMap();
            PatientErrorList patientErrorList = patientErrorMap.get(transactionType.name);
            if (patientErrorList != null && !patientErrorList.empty()) {
               String patientId = queryRequest.patientId;
               if (patientId != null) {
                  Pid pid = PidBuilder.createPid(patientId);
                  String error = patientErrorList.getErrorName(pid);
                  if (error != null) {
                     er.err(error, "Error forced because of Patient ID", "", Severity.Error.toString(), "");
                     dsSimCommon.sendErrorsInAdhocQueryResponse(er);
                     return false;
                  }
               }
            }

            RemoteSqSim rss = new RemoteSqSim(common, dsSimCommon, this, getSimulatorConfig(), query);

            mvc.addMessageValidator("Forward query to local Registry", rss, newER());

            mvc.run();

            m = rss.getMetadata();

            String home = getSimulatorConfig().get(SimulatorProperties.homeCommunityId).asString();

            // add homeCommunityId
            XCQHomeLabelSim xc = new XCQHomeLabelSim(common, this, home);
            mvc.addMessageValidator("Attach homeCommunityId", xc, newER());

            // Add in errors
            AdhocQueryResponseGenerator queryResponseGenerator = new AdhocQueryResponseGenerator(common, dsSimCommon, rss);
            mvc.addMessageValidator("Attach Errors", queryResponseGenerator, newER());

            mvc.run();

            // wrap response in soap wrapper and http wrapper
            mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperRegistryResponseSim(common, dsSimCommon, rss), newER());

            mvc.run();

            return true; // no updates anyway

         case TransactionType.RMU:
            RegistryActorSimulator ras = new RegistryActorSimulator(getDsSimCommon(), simulatorConfig);
            ras.validationContext = validationContext;
            return ras.processRMU(mvc, validation);

         default:

            dsSimCommon.sendFault("RGActorSimulator: Don't understand transaction " + transactionType, null);
            return true;
      }

   }

   // uid -> retrieved model
   private Map<String, RetrievedDocumentModel> singleRetrieve(String endpoint, RetrieveItemRequestModel requestModel) {
      Soap soap = new Soap();

      if (common.vc.requiresStsSaml) {
         soap.addHeader(SimUtil.getSecurityElement(common.vc, dsSimCommon, this.getClass().getName()));
      }

      OMElement request = new RetrieveRequestGenerator(requestModel).get();
      OMElement result = null;

      er.detail("Forwarding Retreive to " + endpoint);

      try {
         result = soap.soapCall(request, endpoint, true, true, true, SoapActionFactory.ret_b_action, SoapActionFactory.getResponseAction(SoapActionFactory.ret_b_action));
      } catch (Exception e) {
         er.err(Code.XDSRegistryError, e);
         returnRetrieveError();
         return null;
      }


      RetrieveB retb = new RetrieveB(null);
      Map<String, RetrievedDocumentModel> docMap = null;

      try {
         docMap = retb.parse_rep_response(result).getMap();
      } catch (Exception e) {
         er.err(Code.XDSRegistryError, e);
         returnRetrieveError();
         return null;  // indicates error
      }

      return docMap;
   }

   // repUId -> Site
   private Map<String, Site> getLinkedRepositoryMap() {
      SimManager simMgr = new SimManager("ignored");
      List<Site> linkedRepositorySites = []
      try {
         linkedRepositorySites = simMgr.getSites(getSimulatorConfig().getConfigEle(SimulatorProperties.repositories).asList(), db.testSession);
      } catch (Exception e) {}
      // map from repUID to the Site that holds it - bascially where to forward the request
      Map<String, Site> repositorySiteMap = new HashMap<>();
      for (Site site : linkedRepositorySites) {
         String repUid = site.getRepositoryUniqueId(TransactionBean.RepositoryType.REPOSITORY);
         repositorySiteMap.put(repUid, site);
      }
      return repositorySiteMap
   }

   private void returnRetrieveError() {
      mvc.run();
      Response response = null;
      try {
         response = dsSimCommon.getRegistryResponse();
         er.detail("Wrapping response in RetrieveDocumentSetResponse and then SOAP Message");
         OMElement rdsr = dsSimCommon.wrapResponseInRetrieveDocumentSetResponse(response.getResponse());
         OMElement env = dsSimCommon.wrapResponseInSoapEnvelope(rdsr);

         dsSimCommon.sendHttpResponse(env, er);

      } catch (Exception e) {

      }
   }

   public Metadata getMetadata() {
      return m;
   }


}
