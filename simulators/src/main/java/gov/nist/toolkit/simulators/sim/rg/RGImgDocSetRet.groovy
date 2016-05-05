package gov.nist.toolkit.simulators.sim.rg

import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.client.XdsErrorCode
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentsModel
import gov.nist.toolkit.simulators.support.*
import gov.nist.toolkit.soap.axis2.Soap
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

@TypeChecked
class RGImgDocSetRet extends AbstractMessageValidator {
   Logger logger = Logger.getLogger(RGImgDocSetRet);

   private static final TransactionType type = TransactionType.RET_IMG_DOC_SET;

   SimCommon common;
   DsSimCommon dsSimCommon;
   Exception startUpException = null;
   boolean isSecure = false;
   boolean isAsync = false;
   SimulatorConfig asc;
   RetrieveMultipleResponse response;
   RetrievedDocumentsModel retrievedDocs = new RetrievedDocumentsModel();
   OMElement result = null;

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

         logger.debug("Get endpoint for IDS associated with this RG");
         String endpointLabel = (common.isTls()) ? SimulatorProperties.idsrTlsEndpoint : SimulatorProperties.idsrEndpoint;
         String endpoint = dsSimCommon.getSimulatorConfig().get(endpointLabel).asString();

         logger.debug("Get SOAP body from inbound RAD-75");
         SoapMessageValidator smv =
               (SoapMessageValidator) common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
         OMElement ridrBody = smv.getMessageBody();

         logger.debug("Forwarding Request to IDS");
         Soap soap = new Soap();
         soap.setAsync(false);
         soap.setUseSaml(false);

         try {
            soap.soapCall(ridrBody, endpoint,
                  true, //mtom
                  true,  // WS-Addressing
                  true,  // SOAP 1.2
                  type.getRequestAction(),
                  type.getResponseAction());
         } catch (Exception e) {
            Exception e2 = new Exception("Soap Call to endpoint " + endpoint + " failed - " + e.getMessage(), e);
            logException(er, e2)
            throw e2
         }

         result = soap.getResult();

         /*
          * Add the Home Community ID and IDS Repository Unique Id to documents
          */

         String idsRepositoryUniqueIdValue = dsSimCommon.getSimulatorConfig().get(SimulatorProperties.idsRepositoryUniqueId).asString();
         String homeCommunityIdValue = dsSimCommon.getSimulatorConfig().get(SimulatorProperties.homeCommunityId).asString();

         for (OMElement documentResponseElement : XmlUtil.decendentsWithLocalName(result, "DocumentResponse")) {
            OMElement repositoryUniqueIdElement = documentResponseElement.getFirstChildWithName(MetadataSupport.repository_unique_id_qnamens);
            if (repositoryUniqueIdElement == null) {
               repositoryUniqueIdElement = MetadataSupport.om_factory.createOMElement(MetadataSupport.repository_unique_id_qnamens);
               documentResponseElement.addChild(repositoryUniqueIdElement);
            }
            repositoryUniqueIdElement.setText(idsRepositoryUniqueIdValue);

            OMElement homeCommunityIdElement = documentResponseElement.getFirstChildWithName(MetadataSupport.home_community_id_qname);
            if (homeCommunityIdElement == null) {
               homeCommunityIdElement = MetadataSupport.om_factory.createOMElement(MetadataSupport.home_community_id_qname);
               documentResponseElement.addChild(homeCommunityIdElement);
            }
            homeCommunityIdElement.setText(homeCommunityIdValue);
         }

      } catch (NonException e) {
      } catch (Exception e) {
         logException(er, e);
      } finally {
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
      logger.error(msg);
      er.err(XdsErrorCode.Code.XDSRepositoryError, msg, this, null);
   }

}  // EO RGImgDocSetRet class
