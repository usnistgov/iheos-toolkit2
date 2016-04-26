package gov.nist.toolkit.simulators.sim.ig

import gov.nist.toolkit.actorfactory.SimManager
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.client.XdsErrorCode
import gov.nist.toolkit.registrymsg.repository.RetImgDocSetReqParser
import gov.nist.toolkit.registrymsg.repository.RetrieveDocumentResponseGenerator
import gov.nist.toolkit.registrymsg.repository.RetrieveImageRequestGenerator
import gov.nist.toolkit.registrymsg.repository.RetrieveImageRequestModel
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentModel
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentsModel
import gov.nist.toolkit.simulators.support.DsSimCommon
import gov.nist.toolkit.simulators.support.SimCommon
import gov.nist.toolkit.sitemanagement.Sites
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.soap.axis2.Soap
import gov.nist.toolkit.testengine.engine.RetrieveB
import gov.nist.toolkit.valregmsg.message.SoapMessageValidator
import gov.nist.toolkit.valregmsg.registry.RetrieveMultipleResponse
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator
import gov.nist.toolkit.xdsexception.ExceptionUtil
import groovy.transform.TypeChecked

import javax.xml.stream.XMLStreamException

import org.apache.axiom.om.OMElement
import org.apache.log4j.Logger

/**
 * Handles receipt of RAD-69 by IG, generation of RAD-75(s), collection of
 * responses and generation of RAD-69 Response.
 * 
 * XCAI_TODO written, need to test
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
@TypeChecked
class XcRetrieveImgSim extends AbstractMessageValidator {
   Logger logger = Logger.getLogger(XcRetrieveSim);

   SimCommon common;
   DsSimCommon dsSimCommon;
   Exception startUpException = null;
   boolean isSecure = false;
   boolean isAsync = false;
   SimulatorConfig asc;
   RetrieveMultipleResponse response;
   RetrievedDocumentsModel retrievedDocs = new RetrievedDocumentsModel();
   OMElement result = null;

   public XcRetrieveImgSim(SimCommon common, DsSimCommon dsSimCommon, SimulatorConfig asc) {
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

         // Get list of configured Responding Gateways
         SimManager simMgr = new SimManager("ignored");
         List<Site> sites = simMgr.getSites(asc.getConfigEle(SimulatorProperties.respondingGateways).asList());
         if (sites == null || sites.size() == 0) {
            er.err(XdsErrorCode.Code.XDSRepositoryError, "No RespondingGateways configured", this, null);
            throw new NonException();
         }
         Sites remoteSites = new Sites(sites);

         // Get SOAP Request body
         SoapMessageValidator smv =
               (SoapMessageValidator) common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
         OMElement ridrBody = smv.getMessageBody();

         // Parse SOAP Request
         RetImgDocSetReqParser requestParser = new RetImgDocSetReqParser(ridrBody);
         RetrieveImageRequestModel requestModel = requestParser.getRequest();

         // Pass through home community ids present in the request
         for (String homeId : requestModel.getHomeCommunityIds()) {
            Site site = remoteSites.getSiteForHome(homeId);

            if (site == null) {
               er.err(XdsErrorCode.Code.XDSRepositoryError,
                     "Don't have configuration for RG with homeCommunityId " + homeId,
                     this, null);
               throw new NonException();
            }

            // Generate RAD-75(69) request model for this home community id.
            RetrieveImageRequestModel homeModel = requestModel.getModelForCommunity(homeId);

            RetrievedDocumentsModel retDocsModel = forwardRetrieve(site, homeModel);
            for (RetrievedDocumentModel item : retDocsModel.values()) {
               logger.info("XCAI retrieve returned " + item)
               retrievedDocs.add(item);
            }
         } // EO homeId loop

      } catch (NonException e) {
      } catch (Exception e) {
         logException(er, e);
      } finally {
         result = new RetrieveDocumentResponseGenerator(retrievedDocs, dsSimCommon.registryErrorList).get();
         er.unRegisterValidator(this);
      }
   } // EO run method

   /**
    * Send XCAI RetImgDocSetRequest (RAD-75) to Receiving Gateway
    * @param site target RG
    * @param reqModel RetImgDocSetRequest (RAD-75) model for request to send
    * @return Results of request
    */
   private RetrievedDocumentsModel forwardRetrieve(Site site, RetrieveImageRequestModel reqModel) {
      String endpoint = site.getEndpoint(TransactionType.XC_RET_IMG_DOC_SET, isSecure, isAsync);
      er.detail("Forwarding retrieve request to " + endpoint);
      RetrievedDocumentsModel resultsModel = retrieveCall(reqModel, endpoint);
      validateXcRetrieveResponse(resultsModel);
      return resultsModel;
   }

   private RetrievedDocumentsModel retrieveCall(RetrieveImageRequestModel reqModel, String endpoint) {
      OMElement request = new RetrieveImageRequestGenerator(reqModel).get();
      Soap soap = new Soap();
      soap.setAsync(false);
      soap.setUseSaml(false);

      try {
         soap.soapCall(request,
               endpoint,
               true, //mtom
               true,  // WS-Addressing
               true,  // SOAP 1.2
               "urn:ihe:iti:2007:CrossGatewayRetrieve",
               "urn:ihe:iti:2007:CrossGatewayRetrieveResponse"
               );
      } catch (Exception e) {
         Exception e2 = new Exception("Soap Call to endpoint " + endpoint + " failed - " + e.getMessage(), e);
         logException(er, e2)
         throw e2
      }
      OMElement result = soap.getResult();

      RetrievedDocumentsModel retDocsModel = parseResponse(result);
      return retDocsModel
   }

   private RetrievedDocumentsModel parseResponse(OMElement result)
   throws Exception {
      RetrieveB retb = new RetrieveB(null);
      Map <String, RetrievedDocumentModel> map =
            retb.parse_rep_response(result).getMap();
      RetrievedDocumentsModel rModel = new RetrievedDocumentsModel();
      rModel.setMap(map);
      rModel.setAbbreviatedMessage(abbreviateResponse(result));

      return rModel;
   } // EO retrieveCall method

   private void validateXcRetrieveResponse(RetrievedDocumentsModel models) {
      models.values().each { RetrievedDocumentModel model ->
         model.with {
            if (!docUid) er.err(XdsErrorCode.Code.XDSRegistryMetadataError, 'Responding Gateway returned no Document.uniqueId', this, null)
            if (!repUid) er.err(XdsErrorCode.Code.XDSRegistryMetadataError, 'Responding Gateway returned no repositoryUniqueId', this, null)
            if (!content_type) er.err(XdsErrorCode.Code.XDSRegistryMetadataError, 'Responding Gateway returned no mimeType', this, null)
            if (!home) er.err(XdsErrorCode.Code.XDSRegistryMetadataError, 'Responding Gateway returned no homeCommunityId', this, null)
         }
      }
   }

   /**
    * Returns the passed response message in string form, replacing text in
    * {@code <Document>} elements with "...". <b>Destructive</b>
    * @param resp passed message
    * @return String version of abbreviated response
    */
   private String abbreviateResponse(OMElement resp) throws XMLStreamException {
      Iterator<OMElement> dri = resp.getChildrenWithLocalName("DocumentResponse");
      while (dri.hasNext()) {
         OMElement dr = (OMElement) dri.next();
         Iterator<OMElement> di = dr.getChildrenWithLocalName("Document");
         while (di.hasNext()) {
            OMElement d = (OMElement) di.next();
            d.setText("...");
         } // <Document> loop
      } // <DocumentResponse> loop
      return resp.toStringWithConsume();
   }

   private void logException(ErrorRecorder er, Exception e) {
      String msg = e.getMessage();
      if (msg == null || msg.equals(""))
         msg = ExceptionUtil.exception_details(e);
      logger.error(msg);
      er.err(XdsErrorCode.Code.XDSRepositoryError, msg, this, null);
   }

} // EO XcRetrieveImgSim class







