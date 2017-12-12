/**
 * 
 */
package gov.nist.toolkit.simulators.sim.ids;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gov.nist.toolkit.fhir.simulators.sim.ids.WadoRetrieveResponseSim;
import gov.nist.toolkit.fhir.simulators.support.BaseHttpActorSimulator;
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.http.HttpMessageBa;
import gov.nist.toolkit.http.HttpParserBa;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

/**
 * Simulator for Image Document Source (IDS) receiving WADO (RAD-55)
 * transactions.
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project
 * <a href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 */
public class IdsHttpActorSimulator extends BaseHttpActorSimulator {

   static Logger logger = Logger.getLogger(IdsHttpActorSimulator.class);

   static List <TransactionType> transactions = new ArrayList <>();

   static {
      transactions.add(TransactionType.WADO_RETRIEVE);
   }

   public boolean supports(TransactionType transactionType) {
      return transactions.contains(transactionType);
   }
   
   /*
    * Valid MIME types for Accept header in WADO (RAD-55) Http Requests. One of
    * these must be present, others may be present. per RAD TF-3 Table 4.55-1
    */
   private static String[] validTypes = new String[] {
      "application/dicom",
      "image/jpeg",
      "application/text",
      "application/html",
      "*/*" 
   };


   @Override
   public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
      return run(transactionType, mvc);
   }
   /*
    * (non-Javadoc)
    * 
    * @see
    * gov.nist.toolkit.simulators.support.BaseHttpActorSimulator#run(gov.nist.
    * toolkit.configDatatypes.client.TransactionType,
    * gov.nist.toolkit.valsupport.engine.MessageValidatorEngine)
    */
   @Override
   public boolean run(TransactionType transactionType, MessageValidatorEngine mvc) throws IOException {

      logger.info("IdsHttpActorSimulator: run - transactionType = " + transactionType);
      simCommon.setLogger(logger);
      GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();
      DsSimCommon dsSimCommon = new DsSimCommon(simCommon, mvc);

      logger.debug(transactionType);
      switch (transactionType) {
         case WADO_RETRIEVE:
            simCommon.vc.isRad55 = true;
            simCommon.vc.isRequest = true;
            
            logger.debug("dsSimCommon.runInitialValidationsAndFaultIfNecessary()");
            if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary()) {
               return false;
            }

            logger.debug("mvc.hasErrors()");
            if (mvc.hasErrors()) {
               return false;
            }
            
            HttpParserBa hparser = dsSimCommon.getHttpParserBa();
            HttpMessageBa httpMsg = hparser.getHttpMessage();

            String accept = httpMsg.getHeaderValue("Accept");
            if (StringUtils.isBlank(accept)) err("Required header 'Accept' absent or empty");
            boolean foundOne = false;
            for (String type : validTypes) {
               if (accept.contains(type)) {
                  foundOne = true;
                  break;
               }
            }
            if (!foundOne) {
               StringBuilder s = new StringBuilder();
               for (String t : validTypes) s.append(t).append(", ");
               err("Accept header must contain one of " + s);
            }
            
            if (!"WADO".equals(httpMsg.getQueryParameterValue("requestType")))
               err("Required Request parameter 'requestType=WADO' not found.");

            String studyUID = httpMsg.getQueryParameterValue("studyUID");
            if (!isOid(studyUID, false)) 
               err("Required Request parameter 'studyUID' not found.");
            String seriesUID = httpMsg.getQueryParameterValue("seriesUID");
            if (!isOid(seriesUID, false)) 
               err("Required Request parameter 'seriesUID' not found.");
            String objectUID = httpMsg.getQueryParameterValue("objectUID");
            if (!isOid(objectUID, false)) 
               err("Required Request parameter 'objectUID' not found.");
            
            String contentType = httpMsg.getQueryParameterValue("contentType");
            if (StringUtils.isBlank(contentType))
               err("Required Request parameter 'contentType' not found.");
            
            if (mvc.hasErrors()) {
               returnError(mvc, 400, "Invalid WADO (RAD-55) transaction.");
               return false;
            }
            
            WadoRetrieveResponseSim wrr = new WadoRetrieveResponseSim(simCommon, httpMsg, dsSimCommon);
            mvc.addMessageValidator("Generated RAD-55 Response", wrr, gerb.buildNewErrorRecorder());
            mvc.run();
            

            return false;

         default:
            er.err(Code.XDSRegistryError, "Don't understand transaction " + transactionType,
               "ImagingDocSourceActorSimulator", "");
            simCommon.sendHttpFault("Don't understand transaction " + transactionType);
            return true;

      }  // EO switch (transactionType)

   } // EO run()
   
   private void returnError(MessageValidatorEngine mvc, int status, String msg) {
      mvc.run();
      String em = "Returning http response status " + status + " " + msg;
      logger.info(em);
      er.detail(em);
      try {
         simCommon.response.sendError(status, msg);
      } catch (IOException ioe){
         logger.warn("I/O error attempting to send error response " + ioe.getMessage());
         return;
      }      
   }
   
   private void err(String msg) {
      er.err(Code.XDSIRequestError, msg, "", "");
   }
   

   /**
    * Is the passed string properly formatted OID? Example would be a home
    * community id.
    * @param value String to be validated.
    * @param blankOk boolean, return true for a null/empty string?
    * @return boolean true if value is properly formatted, false otherwise.
    */
   private boolean isOid(String value, boolean blankOk) {
      if (value == null || value.length() == 0) return blankOk;
      return value.matches("\\d(?=\\d*\\.)(?:\\.(?=\\d)|\\d){0,255}");
   }

} // EO class
