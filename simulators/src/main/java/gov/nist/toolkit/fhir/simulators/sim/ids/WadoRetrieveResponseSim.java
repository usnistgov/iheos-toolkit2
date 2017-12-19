/**
 * 
 */
package gov.nist.toolkit.fhir.simulators.sim.ids;

import edu.emory.mathcs.backport.java.util.Arrays;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.http.HttpMessageBa;
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon;
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.fhir.simulators.support.StoredDocument;
import gov.nist.toolkit.fhir.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates HTTP Response for WADO Retrieve Request (RAD-55)
 */
public class WadoRetrieveResponseSim extends TransactionSimulator {
   
   private static Logger logger = Logger.getLogger(WadoRetrieveResponseSim.class);
   // maps contentType to acceptable transfer syntax UIDs.
   private static Map<String, String> typesMap;
   // list of WADO HTTP parameters currently supported by sim
   private static String[] supportedParameters = new String[] {
      "requestType", "studyUID", "seriesUID", "objectUID", "contentType", "transferSyntax"
   };
   
   static {
      typesMap = new HashMap<>();
      typesMap.put("application/dicom", "1.2.840.10008.1.2.1");
   }
   
   HttpMessageBa httpMsg;
   DsSimCommon dsSimCommon;

   public WadoRetrieveResponseSim(SimCommon common, HttpMessageBa httpMsg,
      DsSimCommon dsSimCommon) {
      super(common, common.simConfig);
      this.httpMsg = httpMsg;
      this.dsSimCommon = dsSimCommon;
      this.dsSimCommon.setSimulatorConfig(simulatorConfig);
   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.valsupport.message.AbstractMessageValidator#run(gov.nist.toolkit.errorrecording.ErrorRecorder, gov.nist.toolkit.valsupport.engine.MessageValidatorEngine)
    */
   @Override
   public void run(ErrorRecorder err, MessageValidatorEngine mvc) {
      ServletOutputStream os;
      this.er = err;
      
      String studyUID = httpMsg.getQueryParameterValue("studyUID");
      String seriesUID = httpMsg.getQueryParameterValue("seriesUID");
      String objectUID = httpMsg.getQueryParameterValue("objectUID");
      String compositeUID = studyUID + ":" + seriesUID + ":" + objectUID;
      /* 
       * check if contenType is represented in typesMap; if not, it is not 
       * supported
       */
      String contentType = httpMsg.getQueryParameterValue("contentType");
      if (typesMap.containsKey(contentType) == false) 
         err("contentType [" + contentType + "] not supported.");
      String transferSyntaxUid = typesMap.get(contentType);
      String x = httpMsg.getQueryParameterValue("transferSyntax");
      if (StringUtils.isNotBlank(x)) transferSyntaxUid = x;
      /*
       * Look for unsupported http request parameters
       */
      List<NameValuePair> queryParameters = httpMsg.getQueryParameters();
      for (NameValuePair queryParameter : queryParameters) {
         String n = queryParameter.getName();
         if (!Arrays.asList(supportedParameters).contains(n))
            er.detail("request parameter [" + n + "] not supported.");
      }
      if (mvc.hasErrors()) {
         returnError(mvc, 406, "Not Acceptable");
         return;
      }
      List<String> transferSyntaxUids = new ArrayList<>();
      transferSyntaxUids.add(transferSyntaxUid);
      StoredDocument doc = dsSimCommon.getStoredImagingDocument(compositeUID, transferSyntaxUids);
      if (doc == null) {
         returnError(mvc, 404, "Not Found");
         return;
      }
      byte[] content = doc.getContent();
      common.response.setContentType(contentType);
      common.response.setContentLength(content.length);
      common.response.setStatus(200);
      try {
      os = common.response.getOutputStream();
      os.write(content);
      common.response.flushBuffer();
      os.close();
      } catch (IOException ioe) {
         String em = "IOException during write: " + ioe.getMessage();
         logger.warn(em);
         err("IOException during write: " + ioe.getMessage());
         returnError(mvc, 500, "Error on server");
      } 
      return;
   }

   private void returnError(MessageValidatorEngine mvc, int status, String msg) {
      mvc.run();
      String em = "Returning http response status " + status + " " + msg;
      logger.info(em);
      er.detail(em);
      try {
         common.response.sendError(status, msg);
      } catch (IOException ioe){
         logger.warn("I/O error attempting to send error response " + ioe.getMessage());
         return;
      }      
   }
   
   private void err(String msg) {
      er.err(Code.XDSIRequestError, msg, "", "");
   }
}
