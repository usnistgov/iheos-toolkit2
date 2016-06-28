package gov.nist.toolkit.simulators.sim.rg

import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.actorfactory.SimManager
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.client.XdsErrorCode
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code
import gov.nist.toolkit.registrymsg.repository.*
import gov.nist.toolkit.simulators.support.*
import gov.nist.toolkit.sitemanagement.Sites
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType
import gov.nist.toolkit.soap.axis2.Soap
import gov.nist.toolkit.testengine.engine.RetrieveB
import gov.nist.toolkit.valregmsg.message.SoapMessageValidator
import gov.nist.toolkit.valregmsg.registry.RetrieveMultipleResponse
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator
import gov.nist.toolkit.xdsexception.ExceptionUtil
import groovy.transform.TypeChecked

import org.apache.axiom.om.OMElement
import org.apache.log4j.Logger

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.xml.XmlUtil;

/**
 * When and RIG simulator receives a RAD-75, this class handles
 * the generation of one or more RAD-69s to IDS simulator(s),
 * collects the results, and creates the RAD-75 response. *
 */
@TypeChecked
class RGImgDocSetRet extends AbstractMessageValidator {

   //***************************************************************
   // static Properties
   //***************************************************************
   
   static Logger log = Logger.getLogger(RGImgDocSetRet);

   private static final TransactionType type = TransactionType.XC_RET_IMG_DOC_SET;

   //***************************************************************
   // Instance Properties
   //***************************************************************
   
   SimCommon common;
   DsSimCommon dsSimCommon;
   Exception startUpException = null;
   boolean isSecure = false;
   boolean isAsync = false;
   SimulatorConfig asc;
   RetrieveMultipleResponse response;
   RetrievedDocumentsModel retrievedDocs = new RetrievedDocumentsModel();
   OMElement result = null;

   //***************************************************************
   // Constructor
   //***************************************************************

   public RGImgDocSetRet(SimCommon common, DsSimCommon dsSimCommon, SimulatorConfig asc) {
      super(common.vc);
      this.common = common;
      this.dsSimCommon = dsSimCommon;
      this.asc = asc;
      isSecure = common.isTls();
      isAsync = false;

      try {
         response = new RetrieveMultipleResponse();
      } catch (Exception e) {
         System.out.println(ExceptionUtil.exception_details(e));
         startUpException = e;
      }
   }

   // Not an exception, but thrown to run code in finally block.
   class NonException extends Exception { }

   //***************************************************************
   // run method
   //***************************************************************

   @Override
   public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
      this.er = er;
      er.registerValidator(this);

      if (startUpException != null) {
         er.err(XdsErrorCode.Code.XDSRegistryError, startUpException);
         throw new NonException()
      }

      try {

         // Request failed initial validation
         if (common.hasErrors()) {
            response.add(dsSimCommon.getRegistryErrorList(), null);
            throw new NonException();
         }
         
         // Get list of configured IDSs, adding this simulator
         List<String> siteNames = asc.getConfigEle(SimulatorProperties.imagingDocumentSources).asList();         
         siteNames.add(asc.getId().toString());
         SimManager simMgr = new SimManager("ignored");
         List<Site> sites = simMgr.getSites(siteNames);
         if (sites == null || sites.size() == 0) {
            er.err(XdsErrorCode.Code.XDSRepositoryError, "No Imaging Document Sources configured", this, null);
            throw new NonException();
         }
         Sites idsSites = new Sites(sites);

         // Get SOAP body from inbound RAD-75
         SoapMessageValidator smv =
               (SoapMessageValidator) common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
         OMElement ridrBody = smv.getMessageBody();
         
         // Parse SOAP Request
         RetImgDocSetReqParser requestParser = new RetImgDocSetReqParser(ridrBody);
         RetrieveImageRequestModel requestModel = requestParser.getRequest();
         
         // Pass through IDS Repository Unique IDs present in request
         for (String idsRepId : requestModel.getIDSRepositoryUniqueIds()) {
            Site site = idsSites.getSiteForRepUid(idsRepId, RepositoryType.IDS);
            
            // No site with this id
            if (site == null) {
               er.err(XdsErrorCode.Code.XDSIUnknownIdsUid,
                     "Don't have configuration for IDS with repository unique Id " + idsRepId, this, null);
               throw new NonException();
            }
            
            // Generate model for this repository unique id.
            RetrieveImageRequestModel idsModel = requestModel.getModelForRepository(idsRepId);
            
            // Get endpoint for this RAD-69
            TransactionType t = TransactionType.RET_IMG_DOC_SET;
            String endpoint = site.getEndpoint(t, isSecure, isAsync);
            er.detail("Forwarding retrieve request to " + endpoint);
            
            // Send RAD-69 request
            OMElement idsReq = new RetrieveImageRequestGenerator(idsModel).get();
            Soap soap = new Soap();
            soap.setAsync(false);
            soap.setUseSaml(false);
            try {
               soap.soapCall(idsReq, endpoint,
                     true, //mtom
                     true,  // WS-Addressing
                     true,  // SOAP 1.2
                     t.getRequestAction(),
                     t.getResponseAction());
                  
               // Get response and build model
               result = soap.getResult();
               RetrieveB retb = new RetrieveB(null);
               Map <String, RetrievedDocumentModel> map =
                     retb.parse_rep_response(result).getMap();
               RetrievedDocumentsModel rModel = new RetrievedDocumentsModel();
               rModel.setMap(map);
               
               pullErrorList(rModel, result);
               
               for (RetrievedDocumentModel item : rModel.values()) {
                  log.info("IDS retrieve returned " + item)
                  item.setRepUid(idsRepId);
                  item.setHome(asc.getConfigEle(SimulatorProperties.homeCommunityId).asString());
                  retrievedDocs.add(item);
               }
               
            } catch (Exception e) {
               Exception e2 = new Exception("Soap Call to endpoint " + endpoint + " failed - " + e.getMessage(), e);
               logException(er, e2)
               throw e2
            }           
           
            
         } // EO pass through IDS repository unique ids.       

      } catch (NonException e) {
      } catch (Exception e) {
         logException(er, e);
      } finally {
         result = new RetrieveDocumentResponseGenerator(retrievedDocs, 
            dsSimCommon.registryErrorList).get();
         er.unRegisterValidator(this);
      }
   } // EO run method

   public OMElement getResult() {
      return result;
   }
   private void logException(ErrorRecorder er, Exception e) {
      String msg = e.getMessage();
      if (msg == null || msg.equals(""))
         msg = ExceptionUtil.exception_details(e);
      log.error(msg);
      er.err(XdsErrorCode.Code.XDSRepositoryError, msg, this, null);
   }
   
   private void pullErrorList(RetrievedDocumentsModel model, OMElement response) {
      OMElement regResp = XmlUtil.firstChildWithLocalName(response, "RegistryResponse");
      if (regResp == null) return;
      // load status attribute value
      model.setStatus(XmlUtil.getAttributeValue(regResp, "status"));
      for (OMElement regErr : XmlUtil.decendentsWithLocalName(regResp, "RegistryError")) {
         String errorCode = XmlUtil.getAttributeValue(regErr, "errorCode");
         String codeContext = XmlUtil.getAttributeValue(regErr, "codeContext");
         String location = XmlUtil.getAttributeValue(regErr, "location");
         String severity = XmlUtil.getAttributeValue(regErr, "severity");
         er.err(errorCode, codeContext, location, severity, null);
      }
   }

}  // EO RGImgDocSetRet class
