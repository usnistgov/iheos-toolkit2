package gov.nist.toolkit.simulators.sim.rg

import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.client.Severity
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.PatientErrorList
import gov.nist.toolkit.configDatatypes.client.PatientErrorMap
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code
import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.registrymsg.registry.AdhocQueryRequest
import gov.nist.toolkit.registrymsg.registry.AdhocQueryRequestParser
import gov.nist.toolkit.registrymsg.registry.Response
import gov.nist.toolkit.registrymsg.repository.RetrieveDocumentResponseGenerator
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentModel
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentsModel
import gov.nist.toolkit.registrysupport.MetadataSupport
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.simulators.sim.reg.AdhocQueryResponseGenerator
import gov.nist.toolkit.simulators.sim.reg.RegistryActorSimulator
import gov.nist.toolkit.simulators.sim.reg.SoapWrapperRegistryResponseSim
import gov.nist.toolkit.simulators.support.*
import gov.nist.toolkit.soap.axis2.Soap
import gov.nist.toolkit.testengine.engine.RetrieveB
import gov.nist.toolkit.utilities.xml.XmlUtil
import gov.nist.toolkit.valregmsg.message.SoapMessageValidator
import gov.nist.toolkit.valregmsg.service.SoapActionFactory
import gov.nist.toolkit.valsupport.client.ValidationContext
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator
import groovy.transform.TypeChecked

import org.apache.axiom.om.OMElement
import org.apache.commons.lang.StringUtils
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

      switch (transactionType) {

         //***************************************************************
         // Cross Community Retrieve Document
         //***************************************************************

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
            AbstractMessageValidator mv = common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
            if (mv == null || !(mv instanceof SoapMessageValidator)) {
               er.err(Code.XDSRegistryError, "RG Internal Error - cannot find SoapMessageValidator instance", "RespondingGatewayActorSimulator", "");
               returnRetrieveError();
               return false;
            }

            SoapMessageValidator smv = (SoapMessageValidator) mv;
            OMElement query = smv.getMessageBody();

            SimulatorConfigElement asce = getSimulatorConfig().getUserByName(SimulatorProperties.homeCommunityId);
            if (asce == null) {
               er.err(Code.XDSRepositoryError, "RG Internal Error - homeCommunityId not configured", this, "");
               returnRetrieveError();
               return false;
            }
            String configuredHomeCommunityId = asce.asString();
            List<OMElement> targetHomeCommunityIdEles = XmlUtil.decendentsWithLocalName(query, "HomeCommunityId");
            for (OMElement e : targetHomeCommunityIdEles) {
               String id = e.getText();
               if (id == null)
                  id = "";
               if (!configuredHomeCommunityId.equals(id)) {
                  er.err(Code.XDSRepositoryError, "HomeCommunityId in request (" +  id + ") does not match configured value (" + configuredHomeCommunityId + ")", this, "");
               }
            }

            if (mvc.hasErrors()) {
               returnRetrieveError();
               return false;
            }

         // getRetrievedDocumentsModel repository endpoint for retrieve
            String endpointLabel = (common.isTls()) ? SimulatorProperties.retrieveTlsEndpoint : SimulatorProperties.retrieveEndpoint;
            String endpoint = getSimulatorConfig().get(endpointLabel).asString();

         // issue soap call to repository
            Soap soap = new Soap();
            OMElement result = null;

            try {
               result = soap.soapCall(query, endpoint, true, true, true, SoapActionFactory.ret_b_action, SoapActionFactory.getResponseAction(SoapActionFactory.ret_b_action));

               // add these back in after testing sq
               //				boolean hasErrors = passOnErrors(result);
               //
               //				if (hasErrors)
               //					return false;

            } catch (Exception e) {
               er.err(Code.XDSRegistryError, e);
               returnRetrieveError();
               return false;
            }


            RetrieveB retb = new RetrieveB(null);
            Map<String, RetrievedDocumentModel> docMap = null;

            try {
               docMap = retb.parse_rep_response(result).getMap();
            } catch (Exception e) {
               er.err(Code.XDSRegistryError, e);
               returnRetrieveError();
               return false;
            }

            RetrievedDocumentsModel models = new RetrievedDocumentsModel(docMap)

            String home = getSimulatorConfig().get(SimulatorProperties.homeCommunityId).asString();
            models.values().each { RetrievedDocumentModel model -> model.home = home }

            logger.info("Retrieved content is " + docMap);

            StoredDocumentMap stdocmap = new StoredDocumentMap(docMap);
            dsSimCommon.intallDocumentsToAttach(stdocmap);

         // wrap in soap wrapper and http wrapper
         //			mvc.addMessageValidator("SendResponseInSoapWrapper", new SoapWrapperResponseSim(common, dsSimCommon, result), er);

            OMElement responseEle = new RetrieveDocumentResponseGenerator(models, dsSimCommon.registryErrorList).get()
            mvc.addMessageValidator("SendResponseInSoapWrapper", new SoapWrapperResponseSim(common, dsSimCommon, responseEle), er);

            mvc.run();

            return true; // no updates anyway

         //***************************************************************
         // Cross Community Query
         //***************************************************************

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
               dsSimCommon.sendErrorsInRegistryResponse(er);
               return false;
            }

         // extract query
            AbstractMessageValidator mv = common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
            if (mv == null || !(mv instanceof SoapMessageValidator)) {
               er.err(Code.XDSRegistryError, "RG Internal Error - cannot find SoapMessageValidator instance", "RespondingGatewayActorSimulator", "");
               dsSimCommon.sendErrorsInRegistryResponse(er);
               return false;
            }

            SoapMessageValidator smv = (SoapMessageValidator) mv;
            OMElement query = smv.getMessageBody();

            SimulatorConfigElement asce = getSimulatorConfig().getUserByName(SimulatorProperties.homeCommunityId);
            String configuredHomeCommunityId = null;
            if (asce == null) {
               er.err(Code.XDSRegistryError, "RG Internal Error - homeCommunityId not configured", this, "");
               dsSimCommon.sendErrorsInRegistryResponse(er);
               return false;
            }
            configuredHomeCommunityId = asce.asString();
            if (configuredHomeCommunityId == null || configuredHomeCommunityId.equals("")) {
               er.err(Code.XDSRegistryError, "RG Internal Error - homeCommunityId not configured", this, "");
               dsSimCommon.sendErrorsInRegistryResponse(er);
               return false;
            }

            AdhocQueryRequest queryRequest = new AdhocQueryRequestParser(query).getAdhocQueryRequest();
            String homeInRequest = queryRequest.getHome();

            boolean homeRequired = !MetadataSupport.sqTakesPatientIdParam(queryRequest.queryId);
            if (homeRequired) {
               if (homeInRequest == null || homeInRequest.equals("")) {
                  er.err(Code.XDSRegistryError, String.format("Query %s requires Home Community Id in request", MetadataSupport.getSQName(queryRequest.queryId)), this, "");
                  dsSimCommon.sendErrorsInRegistryResponse(er);
                  return false;
               }
            }

            if (homeRequired && !configuredHomeCommunityId.equals(homeInRequest)) {
               er.err(Code.XDSRegistryError, "HomeCommunityId in request (" +  homeInRequest + ") does not match configured value (" + configuredHomeCommunityId + ")", this, "");
               dsSimCommon.sendErrorsInRegistryResponse(er);
               return false;
            }

         // Handle forced error
            PatientErrorMap patientErrorMap = getSimulatorConfig().getConfigEle(SimulatorProperties.errorForPatient).asPatientErrorMap();
            PatientErrorList patientErrorList = patientErrorMap.get(transactionType.name);
            if (patientErrorList != null && !patientErrorList.isEmpty()) {
               String patientId = queryRequest.patientId;
               if (patientId != null) {
                  Pid pid = PidBuilder.createPid(patientId);
                  String error = patientErrorList.getErrorName(pid);
                  if (error != null) {
                     er.err(error, "Error forced because of Patient ID", "", Severity.Error.toString(), "");
                     dsSimCommon.sendErrorsInRegistryResponse(er);
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

         //***************************************************************
         // Cross Community Patient Discovery
         //***************************************************************

         case TransactionType.XC_PATIENT_DISCOVERY:
            common.vc.isRequest = true;
            common.vc.updateable = false;
            common.vc.isXcpd = true;
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
         // We do not do anything with retrieve request for now ...  later we will need the parameters to create response.
         /*
          MessageValidator mv = common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
          if (mv == null || !(mv instanceof SoapMessageValidator)) {
          er.err(Code.XDSRegistryError, "RG Internal Error - cannot find SoapMessageValidator instance", "RespondingGatewayActorSimulator", "");
          return false;
          }
          SoapMessageValidator smv = (SoapMessageValidator) mv;
          OMElement query = smv.getMessageBody();
          */
         // End obtaining retrieve
            mvc.run();
         // Create a response and Add in errors
            XCPDResponseGenerator xcpdResponse = new XCPDResponseGenerator(common);
            OMElement result = null;
            result = xcpdResponse.getXCPDResponse();
            mvc.addMessageValidator("Attach Errors", xcpdResponse, er);


            mvc.run();
         // wrap response in soap wrapper and http wrapper
         //mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperXCPDResponseSim(common, result), newER());
         //mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperXCPDResponseSim(common, result), er);
            mvc.addMessageValidator("SendResponseInSoapWrapper", new SoapWrapperResponseSim(common, dsSimCommon, result), er);

            mvc.run();

            return true;


         //***************************************************************
         // Cross Community Retrieve Image Document Set
         //***************************************************************

         case TransactionType.XC_RET_IMG_DOC_SET:

            logger.debug("Transaction type: XC_RET_IMG_DOC_SET");
            common.vc.isRet = true;
            common.vc.isRad69 = true;
            common.vc.isSQ = true;
            common.vc.isXC = true;
            common.vc.isRequest = true;
            common.vc.isSimpleSoap = true;
            common.vc.hasSoap = true;
            common.vc.hasHttp = true;

            logger.debug("dsSimCommon.runInitialValidationsAndFaultIfNecessary()");
            if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
               return false;

            logger.debug("mvc.hasErrors()");
            if (mvc.hasErrors()) {
               dsSimCommon.sendErrorsInRegistryResponse(er);
               return false;
            }

            logger.debug("Extract retrieve from validator chain");
            AbstractMessageValidator mv = common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
            if (mv == null || !(mv instanceof SoapMessageValidator)) {
               er.err(Code.XDSRegistryError, "RG Internal Error - cannot find SoapMessageValidator instance", "RespondingGatewayActorSimulator", "");
               dsSimCommon.sendErrorsInRegistryResponse(er);
               return false;
            }

            logger.debug("Got AbstractMessageValidator");
            SoapMessageValidator smv = (SoapMessageValidator) mv;
            OMElement retReq = smv.getMessageBody();

            logger.debug("Pull home community id");
            SimulatorConfigElement asce = getSimulatorConfig().getUserByName(SimulatorProperties.homeCommunityId);
            if (asce == null) {
               er.err(Code.XDSRepositoryError, "RG Internal Error - homeCommunityId not configured", this, "");
               returnRetrieveError();
               return false;
            }
            String configuredHomeCommunityId = StringUtils.trimToEmpty(asce.asString());

            for (OMElement e : XmlUtil.decendentsWithLocalName(retReq, "homeCommunityId")) {
               String id = StringUtils.trimToEmpty(e.getText());
               if (!configuredHomeCommunityId.equals(id)) {
                  er.err(Code.XDSRepositoryError, "HomeCommunityId in request (" +  id + ") does not match configured value (" + configuredHomeCommunityId + ")", this, "");
               }
            }

            if (mvc.hasErrors()) {
               returnRetrieveError();
               return false;
            }
            
            logger.debug("get endpoint for grouped IDS");
            String endpointLabel = (common.isTls()) ? 
               SimulatorProperties.xcirTlsEndpoint :
               SimulatorProperties.xcirEndpoint;
            String endpoint = getSimulatorConfig().get(endpointLabel).asString();
            
            logger.debug("send SOAP request to IDS");
            Soap soap = new Soap();
            OMElement result = null;
            TransactionType type = TransactionType.RET_IMG_DOC_SET;
            
            try {
               result = soap.soapCall(
                     retReq, // Retrieve Image Document Set SOAP Request body
                     endpoint, // IDS SOAP endpoint
                     true, // use MTOM
                     true, // use WSA addressing
                     true, // use SOAP 1.2
                     type.getRequestAction(),
                     type.getResponseAction());
            } catch (Exception e) {
               er.err(Code.XDSRegistryError, e);
               returnRetrieveError();
               return false;
            }
                    
            logger.debug("Set all document home community ids");
            for (OMElement docElement : XmlUtil.decendentsWithLocalName(result, "DocumentResponse")) {
               OMElement hciElement = XmlUtil.firstChildWithLocalName(docElement, "homeCommunityId");
               if (hciElement == null) {
                  hciElement = MetadataSupport.om_factory.createOMElement(MetadataSupport.home_community_id_qname);
                  docElement.addChild(hciElement);
               }
               hciElement.setText(configuredHomeCommunityId);
            }
            
            logger.debug("Send response")
            mvc.addMessageValidator("SendResponseInSoapWrapper", new SoapWrapperResponseSim(common, dsSimCommon, result), er);

            mvc.run();
           
            
         // XCAI_TODO coded, need to test

            return true;

         default:
            dsSimCommon.sendFault("RGActorSimulator: Don't understand transaction " + transactionType, null);
            return true;

      } // EO switch on transactionType

   } // EO run method

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
