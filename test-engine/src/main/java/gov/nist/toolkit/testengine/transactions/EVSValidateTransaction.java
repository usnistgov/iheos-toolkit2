/**
 * 
 */
package gov.nist.toolkit.testengine.transactions;

import edu.wustl.mir.erl.ihe.xdsi.util.PfnType;
import edu.wustl.mir.erl.ihe.xdsi.util.Utility;
import edu.wustl.mir.erl.ihe.xdsi.validation.*;
import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.server.PropertyManager;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.testengine.assertionEngine.Assertion;
import gov.nist.toolkit.testengine.assertionEngine.AssertionEngine;
import gov.nist.toolkit.testengine.engine.*;
import gov.nist.toolkit.testenginelogging.client.ReportDTO;
import gov.nist.toolkit.utilities.xml.Parse;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.util.TagUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Handles validations by invoking Gazell EVS
 */
public class EVSValidateTransaction extends BasicTransaction {

   private OMElement step;
   private PropertyManager pMgr;
   private Logger log = Logger.getLogger(EVSValidateTransaction.class.getName());

   private String evsURL=null;  // "https://validation.sequoiaproject.org/evs/rest/validations/"
   private String gazelleAuthorizationString = null;

   /**
    * @param s_ctx StepContext instance
    * @param step {@code <TestStep>} element from the textplan.xml
    * @param instruction {@code <EVSValidateTransaction>} element from the
    * testplan.xml
    * @param instruction_output {@code <EVSValidateTransaction>} element from the
    * log.xml file.
    */
   public EVSValidateTransaction(StepContext s_ctx, OMElement step, OMElement instruction, OMElement instruction_output) {
      super(s_ctx, instruction, instruction_output);
      pMgr = Installation.instance().propertyServiceManager().getPropertyManager();
      this.step = step;
   }

   @Override
   public void runAssertionEngine(OMElement step_output, ErrorReportingInterface eri, OMElement assertion_output)
      throws XdsInternalException {

      AssertionEngine engine = new AssertionEngine(this);
      engine.setDataRefs(data_refs);
      engine.setCaller(this);

      try {
         if (useReportManager != null) {
            useReportManager.apply(assertionEleList);
         }
      } catch (Exception e) {
         failed();
      }

      engine.setAssertions(assertionEleList);
      engine.setLinkage(linkage);
      engine.setOutput(step_output);
      engine.setTestConfig(testConfig);
      engine.run(eri, assertion_output);
   }

   @Override
   protected void run(OMElement request) throws Exception {
      return;
   }

   @Override
   protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
      parseBasicInstruction(part);
   }

   @Override
   protected String getRequestAction() {
      return null;
   }

   @Override
   protected String getBasicTransactionName() {
      return "EVSValidate";
   }

   private List <String> errs;

   /**
    * Handles validations based on the {@code <Assert>} Element process
    * attribute value
    */
   @Override
   public void processAssertion(AssertionEngine engine, Assertion a, OMElement assertion_output)
      throws XdsInternalException {
      XdsInternalException xdsInternalException = null;
      errs = new ArrayList <>();
      try {
         switch (a.process) {
            case "ITI-55 Response":
               validITI55Response(engine, a, assertion_output);
               break;
            default:
               throw new XdsInternalException("EVSValidateTransaction: Unknown assertion.process " + a.process);
         }
      } catch (XdsInternalException ie) {
         xdsInternalException = ie;
         errs.add(ie.getMessage());
      }
      if (errs.isEmpty() == false) {
         ILogger testLogger = new TestLogFactory().getLogger();
         testLogger.add_name_value_with_id(assertion_output, "AssertionStatus", a.id, "fail");
         for (String err : errs) 
            s_ctx.fail(err);
      }
      if (xdsInternalException != null) throw xdsInternalException;
   } // EO processAssertion method


   private void validITI55Response(AssertionEngine engine, Assertion a, OMElement assertion_output)
           throws XdsInternalException {
      try {
         ensureGazelleEVSData();

         if (evsURL == null) {
            log.severe("Did not find value for Gazelle_EVS_URL in Toolkit properties file; cannot execute EVSValidateTransaction::validITI55Response");
            store(engine, CAT.ERROR, "Did not find value for Gazelle_EVS_URL in Toolkit properties file; cannot execute EVSValidateTransaction::validITI55Response");
            return;
         }
         if (gazelleAuthorizationString == null) {
            log.severe("Did not find value for Gazelle_Authorization_String in Toolkit properties file; cannot execute EVSValidateTransaction::validITI55Response");
            store(engine, CAT.ERROR, "Did not find value for Gazelle_Authorization_String in Toolkit properties file; cannot execute EVSValidateTransaction::validITI55Response");
            return;
         }

         OMElement e = a.assertElement;
         String responseXML = e.getAttributeValue(new QName("responseXML"));
         boolean verbose = ("true".equals(e.getAttributeValue(new QName("verbose"))));

         String validationOID = postValidationRequest(engine, "ITI-55_Response", responseXML);
         if (validationOID == null) {
            store(engine, CAT.ERROR, "POST to EVS returned a NULL validation OID. We cannot complete the sequence to validate this ITI-55 Response");
            log.severe("POST to EVS returned a NULL validation OID. We cannot complete the sequence to validate this ITI-55 Response");
            store(engine, CAT.WARNING, "Possible causes: Incorrect value for Gazelle_EVS_URL in Toolkit Properties, incorrect value for Gazelle_Authorization_String in Toolkit Properties, expired Gazelle_Authorization_String in Toolkit Properties");
         } else {
            String validationSummary = processValidationSummary(engine, validationOID);
            if (verbose) {
               String validationReport = processValidationReport(engine, validationOID);
            }
         }

      } catch (Exception e) {
         throw new XdsInternalException("validITI55Response error: " + e.getMessage());
      }
   }


   private String postValidationRequest(AssertionEngine engine, String validationType, String content) {
      String rtn = null;
      String fullURL = evsURL;
      CloseableHttpClient httpClient;
      httpClient = HttpClients.createDefault();
      Map<String, String> headers = new LinkedHashMap<>();
      headers.put("Content-Type", "application/xml");
      headers.put("Accept", "*/*");
      headers.put("Accept-Encoding", "gzip,deflate,br");
      headers.put("Authorization", gazelleAuthorizationString);
      //headers.put("GazelleAPKey", "6xHmbqnx06JiHPbgXhbKf2T7BRK3np42cdsBgXZdpYyOKDPzSNFuDwio8ApwOH2vp7biV8qLkWsgiTKlV8nswOWTzRk5p7hyPcbhMyXITFtZma2II5HzmSBmCur6jTCJ");

      String body = "";
      if (validationType.equals("ITI-55_Response")) {
         body = buildITI55Request(content);
      }
      store(engine, CAT.SUCCESS, "About to POST Validation Request: " + validationType + " " + fullURL);

      try {
         URIBuilder builder = new URIBuilder(fullURL);
         HttpPost httpPost = new HttpPost(builder.build());
         for (Map.Entry<String, String> header :headers.entrySet()) {
            httpPost.addHeader(header.getKey(), header.getValue());
         }
         httpPost.setEntity(new StringEntity(body));

         HttpResponse response = httpClient.execute(httpPost);
         StatusLine statusLine = response.getStatusLine();
         if (statusLine.getStatusCode() != 201) {
            store(engine, CAT.ERROR, "Expected 201 (created) from EVS, but we received " + statusLine.getStatusCode() + "; URL=" + fullURL);
            return rtn;
         }
         Header[] responseHeaders = response.getAllHeaders();
         rtn = null;
         for (int i = 0; (i < responseHeaders.length && rtn == null); i++) {
            if (responseHeaders[i].getName().equals("Location")) {
               String location = responseHeaders[i].getValue();
               String[] components = location.split("/");
               if (components != null) {
                  rtn = components[components.length-1];
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
         store(engine, CAT.ERROR, "Exception during call to EVSValidateTransaction::postValidationRequest");
         store(engine, CAT.ERROR, "Error occurred when trying to invoke HTTP POST: " + fullURL);
         store(engine, CAT.ERROR, e.getMessage());
         log.severe("Error occurred when trying to invoke HTTP POST: " + fullURL);
         log.severe(e.getMessage());
      }

      if (rtn == null) {
         store(engine, CAT.ERROR, "HTTP POST response did not return a Location value in the header: " + fullURL);
         store(engine, CAT.WARNING, "See EVSValidateTransaction::postValidationRequest");
      }

      return rtn;
   }

   // Retrieve the validation summary from the EVS and look for Pass / Fail
   public String processValidationSummary(AssertionEngine engine, String reportUID) {
      if (evsURL == null) {
         store(engine, CAT.ERROR, "Did not find property for Gazelle_EVS_URL in Toolkit properties file; cannot execute processValidationSummary");
         log.severe("Did not find value for Gazelle_EVS_URL in Toolkit properties file; cannot execute EVSValidateTransaction::processValidationSummary");
         return null;
      }
      if (gazelleAuthorizationString == null) {
         store(engine, CAT.ERROR, "Did not find property for Gazelle_Authorization_String in Toolkit properties file; cannot execute processValidationSummary");
         log.severe("Did not find value for Gazelle_Authorization_String in Toolkit properties file; cannot execute EVSValidateTransaction::processValidationSummary");
         return null;
      }

      String fullURL = evsURL + "/" + reportUID;
      CloseableHttpClient httpClient;
      httpClient = HttpClients.createDefault();
      Map<String, String> headers = new LinkedHashMap<>();
      headers.put("Accept", "application/xml");
      headers.put("Authorization", gazelleAuthorizationString);
      String rtn = null;

      try {
         URIBuilder builder = new URIBuilder(fullURL);
         HttpGet httpGet = new HttpGet(builder.build());
         for (Map.Entry<String, String> header :headers.entrySet()) {
            httpGet.addHeader(header.getKey(), header.getValue());
         }
         HttpResponse response = httpClient.execute(httpGet);

         StatusLine statusLine = response.getStatusLine();
         if (statusLine.getStatusCode() != 200) {
            store(engine, CAT.ERROR, "EVS returned unexpected HTTP status code " + statusLine.getStatusCode() + "; URL=" + fullURL);
            log.severe("EVS returned unexpected HTTP status code " + statusLine.getStatusCode() + "; URL=" + fullURL);
            return rtn;
         }
         store(engine, CAT.SUCCESS, "Retrieved ITI-55 Response Validation Summary: " + fullURL);
         Header[] responseHeaders = response.getAllHeaders();
         HttpEntity responseEntity = response.getEntity();
         byte[] entityBytes = EntityUtils.toByteArray(responseEntity);
         rtn = new String(entityBytes, Charset.forName("UTF-8"));
         OMElement validationElement = Parse.parse_xml_string(rtn);
         String validationStatus = XmlUtil.getStringFromXPath(validationElement, "//*[local-name()='validation']/*[local-name()='status']");
         if (validationStatus == null || !validationStatus.equals("DONE_PASSED")) {
            store(engine, CAT.ERROR, "EVS returned unexpected validation status that is not 'DONE_PASSED': " + validationStatus + "; URL=" + fullURL);
            return rtn;
         }
         store(engine, CAT.SUCCESS, "EVS Validation Status: " + validationStatus);
      } catch (Exception e) {
         e.printStackTrace();
         store(engine, CAT.ERROR, "Exception during call to processValidationSummary");
         store(engine, CAT.ERROR, "Error occurred when trying to invoke HTTP GET: " + fullURL);
         store(engine, CAT.ERROR, e.getMessage());
         log.severe("Error occurred when trying to invoke HTTP GET: " + fullURL);
         log.severe(e.getMessage());
      }
      return rtn;
   }


   // Retrieve the full validation report from the EVS and place report output in our logs.
   public String processValidationReport(AssertionEngine engine, String reportUID) {
      if (evsURL == null) {
         store(engine, CAT.ERROR, "Did not find property for Gazelle_EVS_URL in Toolkit properties file; cannot execute processValidationReport");
         log.severe("Did not find property for Gazelle_EVS_URL in Toolkit properties file; cannot execute processValidationReport");
         return null;
      }
      if (gazelleAuthorizationString == null) {
         store(engine, CAT.ERROR, "Did not find property for Gazelle_Authorization_String in Toolkit properties file; cannot execute processValidationReport");
         log.severe("Did not find property for Gazelle_Authorization_String in Toolkit properties file; cannot execute processValidationReport");
         return null;
      }

      String fullURL = evsURL + "/" +  reportUID + "/report";
      CloseableHttpClient httpClient;
      httpClient = HttpClients.createDefault();
      Map<String, String> headers = new LinkedHashMap<>();
      headers.put("Accept", "application/xml");
      headers.put("Authorization", gazelleAuthorizationString);

      String rtn = null;
      try {
         URIBuilder builder = new URIBuilder(fullURL);
         HttpGet httpGet = new HttpGet(builder.build());
         for (Map.Entry<String, String> header :headers.entrySet()) {
            httpGet.addHeader(header.getKey(), header.getValue());
         }
         HttpResponse response = httpClient.execute(httpGet);
         StatusLine statusLine = response.getStatusLine();
         if (statusLine.getStatusCode() != 200) {
            store(engine, CAT.ERROR, "EVS returned unexpected HTTP status code " + statusLine.getStatusCode() + "; URL=" + fullURL);
            log.severe("EVS returned unexpected HTTP status code " + statusLine.getStatusCode() + "; URL=" + fullURL);
            return rtn;
         }
         store(engine, CAT.SUCCESS, "Retrieved ITI-55 Response Validation Report (Details): " + fullURL);
         Header[] responseHeaders = response.getAllHeaders();
         HttpEntity responseEntity = response.getEntity();
         byte[] entityBytes = EntityUtils.toByteArray(responseEntity);
         rtn = new String(entityBytes, Charset.forName("UTF-8"));
         OMElement reportElement = Parse.parse_xml_string(rtn);
         String result = XmlUtil.getStringFromXPath(reportElement, "//*[local-name()='validationReport']", "result");
         if (result == null || !result.equals("PASSED")) {
            store(engine, CAT.ERROR, "EVS returned unexpected report result that is not 'PASSED': " + result + "; URL=" + fullURL);
            return rtn;
         }
         List<OMElement> subreportList = XmlUtil.childrenWithLocalName(reportElement, "subReport");
         Iterator<OMElement> iterator = subreportList.iterator();
         while (iterator.hasNext()) {
            OMElement subreport = iterator.next();
            String name = XmlUtil.getAttributeValue(subreport, "name");
            String subReportResult = XmlUtil.getAttributeValue(subreport, "subReportResult");
            store(engine, CAT.SUCCESS, "Subreport " + name + " result " + subReportResult);
            List<OMElement> constraintList = XmlUtil.childrenWithLocalName(subreport, "constraint");
            Iterator<OMElement> constraintIterator = constraintList.iterator();
            while (constraintIterator.hasNext()) {
               OMElement constraint = constraintIterator.next();
               String priority = XmlUtil.getAttributeValue(constraint, "priority");
               String severity = XmlUtil.getAttributeValue(constraint, "severity");
               String testResult = XmlUtil.getAttributeValue(constraint, "testResult");
               String constraintText = priority + " / " + severity + " / " + testResult + ":";

               OMElement constraintDescription = XmlUtil.firstChildWithLocalName(constraint, "constraintDescription");
               OMElement locationInValidatedObject = XmlUtil.firstChildWithLocalName(constraint, "locationInValidatedObject");
               if (locationInValidatedObject != null) {
                  constraintText += locationInValidatedObject.getText() + ": ";
               } else {
                  constraintText += " : ";
               }
               if (constraintDescription != null) {
                  constraintText += constraintDescription.getText();
               }

               store(engine, CAT.SUCCESS, " Subreport Constraint " + constraintText);
            }
         }

         store(engine, CAT.SUCCESS, "EVS Report Result: " + result);
      } catch (Exception e) {
         e.printStackTrace();
         store(engine, CAT.ERROR, "Exception during call to processValidationReport");
         store(engine, CAT.ERROR, "Error occurred when trying to invoke HTTP GET: " + fullURL);
         store(engine, CAT.ERROR, e.getMessage());
         log.severe("Error occurred when trying to invoke HTTP GET: " + fullURL);
         log.severe(e.getMessage());
      }
      return rtn;
   }

   private void ensureGazelleEVSData() {
      if (evsURL == null) {
         PropertyManager propertyManager = Installation.instance().propertyServiceManager().getPropertyManager();

         // Something like: https://hub-itp-val.ehealthexchange.org/evs/rest/validations
         evsURL = propertyManager.getGazelleEVSURL();

         // As of 2024.11.11, the string in gazelleAuthorizationString is of the format:
         // "GazelleAPIKey 4rzEub2BH..." where the key itself (4rzEub2BH... is generated by a Gazelle application
         // API keys are specific to the server that generated them. They are maintained in the local database.
         gazelleAuthorizationString = propertyManager.getGazelleAuthorizationString();
      }
   }

   // Build up the XML for the validation request for an ITI-55 response.
   // If we had more calls to EVS, we would write a more general method somewhere.
   private String buildITI55Request(String content) {
      StringBuffer buffer = new StringBuffer(content.length() + 1000);
      buffer.append("<validation xmlns=\"http://evsobjects.gazelle.ihe.net/\">\n");
      buffer.append("  <validationService name=\"Gazelle HL7V3 Validator\" validator=\"[ITI-55] Cross Gateway Patient Discovery Response\"/>\n");
      buffer.append("  <object originalFileName=\"iti-55.xml\">\n");
      buffer.append("    <content>\n");

      String encoded = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
      buffer.append(encoded);
      buffer.append("\n");

      buffer.append("    </content>\n");
      buffer.append("  </object>\n");
      buffer.append("</validation>\n");

      return buffer.toString();
   }

   class RegErr implements Comparable <RegErr> {
      String errorCode;
      String codeContext;
      String location;
      String severity;
      int expected = 0;
      int found = 0;

      RegErr(String ec, String cc, String l, String s) {
         errorCode = ec;
         codeContext = cc;
         location = l;
         severity = s;
      }

      /*
       * This sorts errors which were not expected to the bottom, and within
       * that, by error code and severity
       */
      @Override
      public int compareTo(RegErr o) {
         if (expected == 0 && o.expected != 0) return 1;
         if (expected != 0 && o.expected == 0) return -1;
         int ecc = errorCode.compareTo(o.errorCode);
         if (ecc != 0) return ecc;
         return (severity.compareTo(o.severity));
      }
   }

   private void post(OMElement registryErrorElement, boolean std, Map <String, RegErr> map) throws Exception {
      String n = registryErrorElement.getLocalName();
      if ("RegistryError".equals(n) == false) throw new Exception("RegErr called with invalid [" + n + "] element");
      String ec = registryErrorElement.getAttributeValue(new QName("errorCode"));
      if (StringUtils.isBlank(ec)) throw new Exception("Missing/Empty Error Code");
      String cc = registryErrorElement.getAttributeValue(new QName("codeContext"));
      String l = registryErrorElement.getAttributeValue(new QName("location"));
      String s = registryErrorElement.getAttributeValue(new QName("severity"));
      if (StringUtils.isBlank(s)) throw new Exception("Missing/Empty severity");
      String key = key(ec, s);
      RegErr regErr = map.get(key);
      if (regErr == null) {
         regErr = new RegErr(ec, cc, l, s);
         map.put(key, regErr);
      }
      if (std) regErr.expected++ ;
      else regErr.found++ ;
   }

   private String key(String errorCd, String sev) throws Exception {
      String s = StringUtils.substringAfterLast(sev, ":");
      if (s.matches("Error|Warning") == false) throw new Exception("Invalid Severity: " + sev);
      return errorCd + ":" + s;
   }

   private void store(AssertionEngine e, CAT cat, String msg) {
      if (cat == CAT.SILENT) return;
      for (String line : StringUtils.split(msg, Utility.nl)) {
         if (cat == CAT.ERROR) errs.add(line);
         else e.addDetail(line);
      }
   }


} // EO EVSValidateTransaction class
