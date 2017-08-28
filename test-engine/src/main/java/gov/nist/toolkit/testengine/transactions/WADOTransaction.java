/**
 * 
 */
package gov.nist.toolkit.testengine.transactions;

import edu.wustl.mir.erl.ihe.xdsi.util.Utility;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.testengine.engine.TransactionStatus;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage WADO Retrieve (RAD-55) transactions, for example: 
 * <pre>{@code
 * <TestStep id="submit">
    <ExpectedStatus>Success</ExpectedStatus>
    <WADOTransaction>
      <Headers>
        Accept: application/dicom; 
      </Headers>
      <Parameters>
        requestType = WADO
        contentType = application/dicom
      </Parameters>
      <BodyFile>body.txt</BodyFile>
    </HttpTransaction>
  </TestStep>
 * }
 * </pre>
 * <p/>Notes:<ol>
 * <li/>The type attribute is mandatory, and must match to a value of the {@link
 * gov.nist.toolkit.configDatatypes.client.TransactionType TransactionType} 
 * enum. The endpoint to which the 
 * <li/>Text content of Headers element forms headers for the HTTP Request. Each
 * header must appear on a separate line and be of the form:<br/>
 * Header-name : header-body. The headers shown in the example above are present
 * by default, and do not need to be entered unless changed.
 * <li/>Text content of Parameters element forms parameters for the HTTP Request. 
 * Each parameters must appear on a separate line and be of the form:<br/>
 * parameterName : value. The parameters shown in the example above are present
 * by default, and do not need to be entered unless changed. The special 
 * characters $ and &amp; used to delimit request parameters will be inserted
 * automagically by the software.
 * <li/>UIDFile element is optional. Its text content must be a path relative
 * to the test directory which gives a file which contains composite UIDs for
 * the DICOM files requested, in the form study-uid:series-uid:instance-uid,
 * one per line. If present, one WADO request will be made for each line of the
 * file, using the values to populate studyUID, seriesUID, and objectUID
 * request parameters, keeping the other settings constant. If UIDFile is NOT 
 * present, only one WADO request will be sent, and the studyUID, seriesUID and
 * objectUID parameters must be present in the Parameters element.
 * <li/>Report and UseReport elements may also be present, and if so, are 
 * processed in the usual way.</ol>
 */
public class WADOTransaction extends BasicTransaction {
   
   static Logger log = Logger.getLogger(WADOTransaction.class);
   
   static private QName RetrieveDocumentSetResponse =
      new QName("urn:ihe:iti:xds-b:2007", "RetrieveDocumentSetResponse", "xdsb");
   static private QName DocumentResponse =
      new QName("urn:ihe:iti:xds-b:2007", "DocumentResponse", "xdsb");
   static private QName RepositoryUniqueId =
      new QName("urn:ihe:iti:xds-b:2007", "RepositoryUniqueId", "xdsb");
   static private QName DocumentUniqueId =
      new QName("urn:ihe:iti:xds-b:2007", "DocumentUniqueId", "xdsb");
   static private QName mimeType =
      new QName("urn:ihe:iti:xds-b:2007", "mimeType", "xdsb");
   static private QName Document =
      new QName("urn:ihe:iti:xds-b:2007", "Document", "xdsb");
   static private QName RegistryResponse =
      new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "RegistryResponse", "rs");
   static private final String statusSuccess = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
   
   private Map<String, String> headers;
   private Map<String, String> parameters;
   private File uidFile;
   private CloseableHttpClient httpClient;
   
   @SuppressWarnings("javadoc")
   public WADOTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
      super(s_ctx, instruction, instruction_output);
      // default request headers
      headers = new LinkedHashMap<>();
      headers.put("Accept", "application/dicom");
      // default request parameters
      parameters = new LinkedHashMap<>();
      parameters.put("requestType", "WADO");
      parameters.put("contentType", "application/dicom");
      uidFile = null;
  }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.testengine.transactions.BasicTransaction#run(org.apache.axiom.om.OMElement)
    */
   @Override
   protected void run(OMElement request) throws Exception {
      
      try {
         if (StringUtils.isNotBlank(target)) endpoint = target;
      if (StringUtils.isBlank(endpoint)) 
         endpoint = testConfig.site.getEndpoint(TransactionType.WADO_RETRIEVE, testConfig.secure, async);
      if (StringUtils.isBlank(endpoint)) 
         throw new XdsInternalException("No valid endpoint");
      
      // Log Endpoint
      testLog.add_name_value(instruction_output, "Endpoint", endpoint);
      
      if (!valid(headers, "Accept")) 
         throw new XdsInternalException("No valid 'Accept' header value");
      if (!valid(parameters, "requestType"))
         throw new XdsInternalException("No valid 'requestType' parameter value");
      if (!valid(parameters, "contentType"))
         throw new XdsInternalException("No valid 'contentType' parameter value");
      if (valid(parameters, "studyUID", "seriesUID", "objectUID"))
         prsRequest();
      if (uidFile != null) {
         String s = uidFile.getAbsolutePath();
         if (!uidFile.exists()) 
            throw new XdsInternalException(s + " does not exist");
         if (!uidFile.isFile()) 
            throw new XdsInternalException(s + " is not a file");
         if (!uidFile.canRead()) 
            throw new XdsInternalException(s + " is not readable");
         for (String line : Files.readAllLines(uidFile.toPath())) {
            String[] codes = line.split(":");
            if (codes.length < 3) continue;
            parameters.put("studyUID",  codes[0].trim());
            parameters.put("seriesUID", codes[1].trim());
            parameters.put("objectUID", codes[2].trim());
            prsRequest();
         }
      }
      } catch (Exception e) {
         throw e;
      } finally {
         if (httpClient != null) httpClient.close();
      }
      
   } // EO run method
   
   private Integer transCount = 0;
   private OMElement resultElement;
   private OMElement requestElement;
   private OMElement outHdrElement;
   private OMElement inHdrElement;
   private OMElement rsltElement;
   private OMElement retrieveDocumentSetResponseElement = null;
   
   private void prsRequest() throws Exception {
      if (httpClient == null) {
         httpClient = HttpClients.createDefault();
         transCount = 0;
         rsltElement = resultElement = testLog.add_simple_element(instruction_output, "Result");
         resultElement = testLog.add_simple_element(resultElement, "Transactions");
         requestElement = testLog.add_simple_element(instruction_output, "InputMetadata");
         requestElement = testLog.add_simple_element(requestElement, "Transactions");
         outHdrElement = testLog.add_simple_element(instruction_output, "InHeader");
         outHdrElement = testLog.add_simple_element(outHdrElement, "Transactions");
         inHdrElement = testLog.add_simple_element(instruction_output, "OutHeader");
         inHdrElement = testLog.add_simple_element(inHdrElement, "Transactions");
      }
      transCount++;  
      OMElement requestE = testLog.add_simple_element_with_id(requestElement, 
         "Transaction", transCount.toString());
      OMElement resultE = testLog.add_simple_element_with_id(resultElement, 
         "Transaction", transCount.toString());
      
      
      // Build Http Request
      URIBuilder builder = new URIBuilder(endpoint);
      for (Map.Entry <String, String> parameter : parameters.entrySet()) {
         builder.addParameter(parameter.getKey(), parameter.getValue());
      }
      HttpGet httpGet = new HttpGet(builder.build());
      for (Map.Entry<String, String> header :headers.entrySet()) {
         httpGet.addHeader(header.getKey(), header.getValue());
      }
      log.debug(httpGet.toString());
      
      // Log Request Header
      StringBuilder h = new StringBuilder(httpGet.toString());
      for (Map.Entry<String, String> header :headers.entrySet()) 
         h.append(Utility.nl).append(header.getKey()).append(" ")
          .append(header.getValue());
      testLog.add_simple_element_with_id(inHdrElement, "Transaction", 
         transCount.toString(), h.toString());      
      
      // Log Http Request
      testLog.add_name_value(requestE, "Request", httpGet.toString());
      OMElement headerE = testLog.add_simple_element(requestE, "Headers");
      for (Map.Entry<String, String> header :headers.entrySet()) 
         testLog.add_name_value(headerE, header.getKey(), header.getValue());
      OMElement parsE = testLog.add_simple_element(requestE, "Parameters");
      for (Map.Entry<String, String> par :parameters.entrySet()) 
         testLog.add_name_value(parsE, par.getKey(), par.getValue());

      // send Request     
      HttpResponse response = httpClient.execute(httpGet);
      StatusLine statusLine = response.getStatusLine();
      Header[] responseHeaders = response.getAllHeaders();
      HttpEntity responseEntity = response.getEntity();
      
      // Log Response header
      h = new StringBuilder(statusLine.toString());
      for (Header hdr : responseHeaders)
         h.append(Utility.nl).append(hdr.toString());
      testLog.add_simple_element_with_id(outHdrElement, "Transaction", 
         transCount.toString(), h.toString());

      OMElement statusE = testLog.add_simple_element(resultE, "Status");
      if (statusLine != null) {
         testLog.add_name_value(statusE, "Line", statusLine.toString());
         testLog.add_name_value(statusE, "Code", Integer.toString(statusLine.getStatusCode()));
         testLog.add_name_value(statusE, "Reason", statusLine.getReasonPhrase());
         Integer statusCode = statusLine.getStatusCode();
         String statusCodeString = statusCode.toString();
         List<TransactionStatus> tss = s_ctx.getExpectedStatus();
         boolean fs = false;
         for (TransactionStatus ts : tss) {
            if (ts.isFailure()) fs = true;
         }
         boolean ok = fs == (statusCode != 200);
         String eec = s_ctx.getExpectedErrorCode();
         if (StringUtils.isNotBlank(eec) && !eec.equals(statusCodeString)) ok = false;
         String m = "HTTP Response: " + statusLine.toString();
         if (ok) {
            s_ctx.addDetail("HTTP Response: ", statusLine.toString());
         } else {
            s_ctx.set_error("HTTP Response: " + statusLine.toString());
            failed();
            return;
         }
      }
      // Determine content type of response body
      String content = "application/dicom";
      OMElement hdrE = testLog.add_simple_element(resultE, "Headers");
      if (responseHeaders != null) {
         for (Header hdr : responseHeaders) {
            testLog.add_name_value(hdrE, hdr.getName(), hdr.getValue());
            if (hdr.getName().equals("Content-Type")) 
               content = hdr.getValue();
         }
      }
      if (retrieveDocumentSetResponseElement == null) {
         retrieveDocumentSetResponseElement =
            testLog.add_simple_element(resultElement, RetrieveDocumentSetResponse);
      }
      OMElement documentReponseElement = 
         testLog.add_simple_element(retrieveDocumentSetResponseElement, DocumentResponse);
      testLog.add_simple_element(documentReponseElement, RepositoryUniqueId);
      OMElement documentUniqueId = 
         testLog.add_simple_element(documentReponseElement, DocumentUniqueId);
      documentUniqueId.setText(parameters.get("objectUID"));
      OMElement mimeTypeElement = 
         testLog.add_simple_element(documentReponseElement, mimeType);
      mimeTypeElement.setText(content);
      OMElement documentElement = 
         testLog.add_simple_element(documentReponseElement, Document);
      
      byte[] entityBytes = EntityUtils.toByteArray(responseEntity);
      String suffix = "txt";
      switch (content) {
         case "application/jpeg":
            suffix = "jpeg";
            documentElement.setText(Base64.getEncoder().encodeToString(entityBytes));
            break;
         case "application/dicom":
            suffix = "dcm";
            documentElement.setText(Base64.getEncoder().encodeToString(entityBytes));
            break;
         case "application/html":
            suffix = "html";
            //$FALL-THROUGH$
         default:
               documentElement.setText(new String(entityBytes, Utility.utf8));
      }
      // directory for files and this file name
      Path filesPath = Paths.get(linkage.getLogFileDir()).resolve("files");
      if (transCount == 1) {
         File f = filesPath.toFile();
         f.mkdirs();
         FileUtils.cleanDirectory(f);
      }
      String fileName = String.format("%06d.%s", transCount, suffix);
      // write entity to file.
      FileUtils.writeByteArrayToFile(filesPath.resolve(fileName).toFile(), entityBytes);
   }
   
   private boolean valid(Map<String, String> map, String... keys) {
      for (String key : keys) 
         if (StringUtils.isBlank(map.get(key))) return false;
      return true;
   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.testengine.transactions.BasicTransaction#parseInstruction(org.apache.axiom.om.OMElement)
    */
   @Override
   protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
      switch(part.getLocalName()) {
         case "Headers":
            for (String line : part.getText().split("\\r?\\n")) {
               String[] tokens = line.split(":", 2);
               if (tokens.length == 2)
                  headers.put(tokens[0].trim(), tokens[1].trim());
            }
            break;
         case "Parameters":
            for (String line : part.getText().split("\\r?\\n")) {
               String[] tokens = line.split("=", 2);
               if (tokens.length == 2)
                  parameters.put(tokens[0].trim(), tokens[1].trim());
            }
            break;
         case "UIDFile":
            uidFile = new File(testConfig.testplanDir, part.getText().trim());
            break;
         case "Report":
            parseReportInstruction(part);
            break;
         case "UseReport":
            parseUseReportInstruction(part);
            break;
         case "Target":
            parseTargetInstruction(part);
            break;
         default:
            log.warn("Unrecognized testplan child " + part.getLocalName() + " ignored.");
      }
   }
   
   private String target = null;
   protected void parseTargetInstruction(OMElement part) {
      try {
      String sim = part.getAttributeValue(new QName("sim"));
      String user = testConfig.testInstance.getUser();
      SimulatorConfig simConfig = SimDb.getSimulator(new SimId(user, sim));
      target = simConfig.getConfigEle(SimulatorProperties.wadoEndpoint).asString();
      } catch (Exception e) {
         
      }
   }

   
   @Override
   protected String getRequestAction() {
      return null;
   }
   
   @Override
   protected String getBasicTransactionName() {
      return "WADO";
   }

}
