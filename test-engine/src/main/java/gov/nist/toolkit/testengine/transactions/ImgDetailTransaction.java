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
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
 * Handles validations for Retrieve Image Document Set Transactions:
 * <ul>
 * <li/>RAD-68
 * <li/>RAD-69
 * <li/>RAD-75
 * </ul>
 */
public class ImgDetailTransaction extends BasicTransaction {

   private OMElement step;
   private PropertyManager pMgr;
   private Logger log = Logger.getLogger(ImgDetailTransaction.class);

   /*
    * These are the DICOM tags currently supported for evaluation tasks. New
    * ones can be added to this map if needed. Any tag can be referenced using
    * Group-Element Syntax
    */
   private static Map <String, Integer> tagMap;

   static {
      tagMap = new HashMap <>();
      tagMap.put("SOPClassUID", Tag.SOPClassUID);
      tagMap.put("SOPInstanceUID", Tag.SOPInstanceUID);
      tagMap.put("PatientName", Tag.PatientName);
      tagMap.put("PatientID", Tag.PatientID);
      tagMap.put("PatientBirthDate", Tag.PatientBirthDate);
      tagMap.put("PatientSex", Tag.PatientSex);
      tagMap.put("StudyInstanceUID", Tag.StudyInstanceUID);
      tagMap.put("SeriesInstanceUID", Tag.SeriesInstanceUID);
      tagMap.put("AccessionNumber", Tag.AccessionNumber);
      tagMap.put("Modality", Tag.Modality);
      tagMap.put("ContentSequence", Tag.ContentSequence);
   }

   /**
    * @param s_ctx StepContext instance
    * @param step {@code <TestStep>} element from the textplan.xml
    * @param instruction {@code <ImgDetailTransaction>} element from the
    * testplan.xml
    * @param instruction_output {@code <ImgDetailTransaction>} element from the
    * log.xml file.
    */
   public ImgDetailTransaction(StepContext s_ctx, OMElement step, OMElement instruction, OMElement instruction_output) {
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
      return "XmlDetail";
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
         case "sameReqImgs":
            prsSameReqImgs(engine, a, assertion_output);
            break;
         case "sameRetImgs":
            prsSameRetImgs(engine, a, assertion_output);
            break;
         case "sameRegErrors":
            prsSameRegErrors(engine, a, assertion_output);
            break;
         case "registryErrorListNotEmpty":
            prsRegistryErrorListNotEmpty(engine, a, assertion_output);
            break;
         case "sameDcmImgs":
            prsSameDcmImgs(engine, a, assertion_output);
            break;
         case "sameKOSDcm":
            prsSameKOSDcm(engine, a, assertion_output);
            break;
         case "sameKOSMetadata":
            prsSameKOSMetadata(engine, a, assertion_output);
            break;
         case "sameWADOResp":
            prsSameWADOResp(engine, a, assertion_output);
            break;
         case "sameQuery":
            this.prsSameQuery(engine, a, assertion_output);
            break;
         case "sameRetrieve":
            this.prsSameRetrieve(engine, a, assertion_output);
            break;
         case "loadSOAPSimTransaction":
            this.prsLoadSOAPSimTransaction(engine, a, assertion_output);
            break;
         case "matchHomeCommunityIds":
            this.prsMatchHomeCommunityIds(engine, a, assertion_output);
            break;
         default:
            throw new XdsInternalException("ImgDetailTransaction: Unknown assertion.process " + a.process);
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

   /*
    * Validates WADO Http Response Codes. A transaction consists of 1-n Http
    * Requests, numbered 1-n in the order they are executed. each has a response
    * code, matched to the values in the standard Transactions element(s) in
    * this manner: 1. The default code, for transactions not explicitly
    * referenced, is 200. 2. The default code can be changed with a Transactions
    * element having an id attribute value of "*" and a code attribute value
    * with the new default. 3. Default overrides are referenced with a
    * Transaction element having the override code attribute and a id value
    * giving the transaction numbers that should have that code value. The id
    * attribute can contain any number of transaction ids separated by commas;
    * ranges can be represented using "-"s. For example: "1,3,5-10". 4.
    * Subsequent entries replace earlier entries with which they might otherwise
    * conflict. If all transactions are to return 200 (OK), no Transactions
    * entries need be made at all, but the HttpResponse element should still be
    * present.
    */
   private void prsSameWADOResp(AssertionEngine engine, Assertion a, OMElement assertion_output)
      throws XdsInternalException {
      try {
         OMElement std = getStdElement("HttpResponseCodes");
         // process Transactions elements
         Integer defaultCode = 200;
         Map <Integer, Integer> codes = new HashMap <>();
         for (OMElement transactionE : XmlUtil.childrenWithLocalName(std, "Transactions")) {
            String id = XmlUtil.getAttributeValue(transactionE, "id");
            String codeS = XmlUtil.getAttributeValue(transactionE, "code");
            log.debug("Transactions id=[" + id + "] code=[" + codeS + "].");
            if (StringUtils.isBlank(id)) throw new Exception("empty id");
            Integer code = Integer.parseInt(codeS);
            for (String tok : id.split(",")) {
               String[] s = tok.split("-", 2);
               if (s.length == 1) {
                  if (s[0].equals("*")) {
                     defaultCode = code;
                     continue;
                  }
                  codes.put(Integer.parseInt(s[0]), code);
                  continue;
               }
               int first = Integer.parseInt(s[0]);
               int last = Integer.parseInt(s[1]);
               for (Integer i = first; i <= last; i++ )
                  codes.put(i, code);
            }
         }
         // process individual WADO request transactions
         OMElement transactionsElement = getTestTrans(a, "");
         for (OMElement transactionElement : XmlUtil.childrenWithLocalName(transactionsElement, "Transaction")) {
            String idS = XmlUtil.getAttributeValue(transactionElement, "id");
            Integer id = Integer.parseInt(idS);
            Integer expectedCode = codes.get(id);
            if (expectedCode == null) expectedCode = defaultCode;
            String msg = "WADO trans " + idS + " status: expected [" + expectedCode.toString() + "] ";
            OMElement codeElement = XmlUtil.firstChildChain(transactionElement, "Status", "Code");
            if (codeElement == null) {
               store(engine, CAT.ERROR, msg + "found null");
               continue;
            }
            String foundCodeS = codeElement.getText();
            Integer foundCode = null;
            try {
               foundCode = Integer.parseInt(foundCodeS);
            } catch (NumberFormatException e) {
               store(engine, CAT.ERROR, msg + "found [" + foundCodeS + "]");
               continue;
            }
            if (expectedCode.equals(foundCode)) {
               store(engine, CAT.SUCCESS, msg + "matched.");
            } else {
               store(engine, CAT.ERROR, msg + "found [" + foundCodeS + "]");
            }
         }
      } catch (Exception e) {
         throw new XdsInternalException("sameWADOResp error: " + e.getMessage());
      }
   }

   private void prsSameReqImgs(AssertionEngine engine, Assertion a, OMElement assertion_output)
      throws XdsInternalException {
      try {
         // Get gold standard request, validate
         String stdName = a.assertElement.getAttributeValue(new QName("std"));
         if (StringUtils.isBlank(stdName)) stdName = "RequestBody";
         OMElement std = getStdElement(stdName);
         String t = std.getLocalName();
         if (t.endsWith("RetrieveImagingDocumentSetRequest") == false) { throw new XdsInternalException(
            "sameReQImgs assertion only applies to RetrieveImagingDocumentSetRequest"); }
         // get test request
         OMElement test = getTestTrans(a, "request");
         // load requested docs data for test/std
         Map <String, ReqImg> testImgs = loadReqImgs(engine, a, test);
         Map <String, ReqImg> stdImgs = loadReqImgs(engine, a, std);
         // load xfer syntax data for test/std
         Set <String> testSyntaxes = loadXferSyntaxes(engine, test);
         Set <String> stdSyntaxes = loadXferSyntaxes(engine, std);
         // pass test request docs against std
         Set <String> testKeys = testImgs.keySet();
         for (String testKey : testKeys) {
            if (stdImgs.containsKey(testKey) == false) {
               store(engine, CAT.ERROR, "test doc UID " + testKey + ", not found in standard.");
               continue;
            }
            ReqImg testImg = testImgs.get(testKey);
            ReqImg stdImg = stdImgs.get(testKey);
            stdImgs.remove(testKey);
            // found and everything matches
            if (comp(stdImg, testImg)) {
               store(engine, CAT.SUCCESS, "test doc UID " + testKey + ", all values match.");
               continue;
            }
            // found, but not everything matches
            store(engine, CAT.SUCCESS, "test doc UID " + testKey + ", found in std.");
            if (!comp(stdImg.studyUID, testImg.studyUID)) store(engine, CAT.ERROR, "for doc with UID: " + testKey
               + " studyUID mismatch (std/test): (" + stdImg.studyUID + "/" + testImg.studyUID + ")");
            if (!comp(stdImg.seriesUID, testImg.seriesUID)) store(engine, CAT.ERROR, "for doc with UID: " + testKey
               + " seriesUID mismatch (std/test): (" + stdImg.seriesUID + "/" + testImg.seriesUID + ")");
            if (!comp(stdImg.homeCommunityUID, testImg.homeCommunityUID))
               store(engine, CAT.ERROR, "for doc with UID: " + testKey + " homeCommunityID mismatch (std/test): ("
                  + stdImg.homeCommunityUID + "/" + testImg.homeCommunityUID + ")");
            if (!comp(stdImg.repositoryUID, testImg.repositoryUID))
               store(engine, CAT.ERROR, "for doc with UID: " + testKey + " RepositoryUniqueID mismatch (std/test): ("
                  + stdImg.repositoryUID + "/" + testImg.repositoryUID + ")");
         }
         // list any std docs which weren't in test
         for (String key : stdImgs.keySet())
            store(engine, CAT.ERROR, "std doc UID: " + key + " not found in test msg.");
         // match xfer syntaxes
         boolean errorFound = false;
         for (String syntax : testSyntaxes) {
            if (stdSyntaxes.contains(syntax)) {
               stdSyntaxes.remove(syntax);
               continue;
            }
            store(engine, CAT.ERROR, "transfer syntax " + syntax + "in test, not found in standard.");
            errorFound = true;
         }
         for (String syntax : stdSyntaxes) {
            store(engine, CAT.ERROR, "transfer syntax " + syntax + "in standard, not found in test.");
            errorFound = true;
         }
         if (!errorFound) store(engine, CAT.SUCCESS, "transfer syntax lists match.");
      } catch (Exception e) {
         throw new XdsInternalException("sameRetImgs error: " + e.getMessage());
      }
   }

   private Map <String, ReqImg> loadReqImgs(AssertionEngine engine, Assertion a, OMElement msg) {
      Map <String, ReqImg> imgs = new LinkedHashMap <>();
      for (OMElement study : XmlUtil.decendentsWithLocalName(msg, "StudyRequest")) {
         String studyUID = study.getAttributeValue(new QName("studyInstanceUID"));
         for (OMElement series : XmlUtil.decendentsWithLocalName(study, "SeriesRequest")) {
            String seriesUID = series.getAttributeValue(new QName("seriesInstanceUID"));
            for (OMElement doc : XmlUtil.decendentsWithLocalName(series, "DocumentRequest")) {
               String docUID = loadTxt(doc, "DocumentUniqueId");
               if (StringUtils.isBlank(docUID)) {
                  String em = "Doc request with no UID. study=" + studyUID + ", series=" + seriesUID;
                  store(engine, CAT.ERROR, em);
                  continue;
               }
               ReqImg reqImg = new ReqImg();
               reqImg.studyUID = studyUID;
               reqImg.seriesUID = seriesUID;
               reqImg.homeCommunityUID = loadTxt(doc, "HomeCommunityId");
               reqImg.repositoryUID = loadTxt(doc, "RepositoryUniqueId");
               imgs.put(docUID, reqImg);
            }
         }
      }
      return imgs;
   }

   private Set <String> loadXferSyntaxes(AssertionEngine engine, OMElement msg) {
      Set <String> syntaxes = new HashSet <>();
      try {
         OMElement sList = XmlUtil.onlyChildWithLocalName(msg, "TransferSyntaxUIDList");
         for (OMElement s : XmlUtil.childrenWithLocalName(sList, "TransferSyntaxUID")) {
            String x = s.getText().trim();
            if (StringUtils.isNotBlank(x)) syntaxes.add(x);
         }
      } catch (Exception e) {
         store(engine, CAT.ERROR, "no TransferSyntaxUIDList element found.");
      }
      return syntaxes;
   }

   class ReqImg {
      String studyUID;
      String seriesUID;
      String homeCommunityUID;
      String repositoryUID;
   }

   private boolean comp(ReqImg std, ReqImg test) {
      return comp(std.studyUID, test.studyUID) && comp(std.seriesUID, test.seriesUID)
         && comp(std.homeCommunityUID, test.homeCommunityUID) && comp(std.repositoryUID, test.repositoryUID);
   }

   /**
    * Matches documents and status in a {@code <RetrieveDocumentSetResponse>}
    * against a "gold standard" response which is in the {@code <ResponseBody>}
    * child element of the testplan.xml {@code <Standard>} element.
    * 
    * @param engine AssertionEngine instance
    * @param a Assert being processed
    * @param assertion_output log.xml output element for that assert
    * @throws XdsInternalException on error
    */
   private void prsSameRetImgs(AssertionEngine engine, Assertion a, OMElement assertion_output)
      throws XdsInternalException {
      try {
         OMElement std = getStdElement("ResponseBody");
         OMElement test = getTestTrans(a, "response");
         String t = std.getLocalName();
         if (t.endsWith("RetrieveDocumentSetResponse") == false)
            throw new XdsInternalException("sameRetImgs assertion only applies to RetrieveDocumentSetResponse");
         // Check RegistryResponse status attribute
         String stdStatus = getResponseStatus(std);
         String testStatus = getResponseStatus(test);
         if (comp(stdStatus, testStatus)) {
            store(engine, CAT.SUCCESS, "RegistryResponse status match: " + testStatus);
         } else {
            store(engine, CAT.ERROR,
               " RegistryResponse status mismatch (std/test): (" + stdStatus + "/" + testStatus + ")");
         }
         Map <String, RetImg> testImgs = loadRetImgs(engine, a, test);
         Map <String, RetImg> stdImgs = loadRetImgs(engine, a, std);
         Set <String> testKeys = testImgs.keySet();
         for (String testKey : testKeys) {
            if (stdImgs.containsKey(testKey) == false) {
               store(engine, CAT.ERROR, "test doc UID " + testKey + ", not found in standard.");
               continue;
            }
            RetImg testImg = testImgs.get(testKey);
            RetImg stdImg = stdImgs.get(testKey);
            stdImgs.remove(testKey);
            if (comp(stdImg.home, testImg.home) && comp(stdImg.repo, testImg.repo) && comp(stdImg.mime, testImg.mime)) {
               store(engine, CAT.SUCCESS, "test doc UID " + testKey + ", all values match.");
               continue;
            }
            store(engine, CAT.SUCCESS, "test doc UID " + testKey + ", found in std.");
            if (comp(stdImg.home, testImg.home) == false) store(engine, CAT.ERROR, "for doc with UID: " + testKey
               + " homeCommunityID mismatch (std/test): (" + stdImg.home + "/" + testImg.home + ")");
            if (comp(stdImg.repo, testImg.repo) == false) store(engine, CAT.ERROR, "for doc with UID: " + testKey
               + " RepositoryUniqueID mismatch (std/test): (" + stdImg.repo + "/" + testImg.repo + ")");
            if (comp(stdImg.mime, testImg.mime) == false) store(engine, CAT.ERROR, "for doc with UID: " + testKey
               + " mimeType mismatch (std/test): (" + stdImg.mime + "/" + testImg.mime + ")");
         }
         if (stdImgs.isEmpty()) return;
         Set <String> stdKeys = stdImgs.keySet();
         for (String key : stdKeys)
            store(engine, CAT.ERROR, "std doc UID: " + key + " not found in test msg.");
      } catch (Exception e) {
         throw new XdsInternalException("sameRetImgs error: " + e.getMessage());
      }
   }

   private void prsSameRegErrors(AssertionEngine engine, Assertion a, OMElement assertion_output)
      throws XdsInternalException {
      // key is errorCode:severity
      Map <String, RegErr> map = new HashMap <>();

      try {
         // process and collect data on errors in std message
         OMElement stnd = getStdElement("ResponseBody");
         String t = stnd.getLocalName();
         if (t.endsWith("RetrieveDocumentSetResponse") == false)
            throw new XdsInternalException("sameRegErrors assertion only applies to RetrieveDocumentSetResponse");
         for (OMElement err : XmlUtil.decendentsWithLocalName(stnd, "RegistryError")) {
            try {
               post(err, true, map);
            } catch (Exception se) {
               // Errors in the std message are abort bait
               throw new XdsInternalException("sameRegErrors std msg error: " + se.getMessage());
            }
         }
         // process errors in test message
         OMElement test = getTestTrans(a, "response");
         String q = test.getLocalName();
         if (q.endsWith("RetrieveDocumentSetResponse") == false)
            throw new XdsInternalException("sameRegErrors assertion only applies to RetrieveDocumentSetResponse");
         for (OMElement err : XmlUtil.decendentsWithLocalName(test, "RegistryError")) {
            try {
               post(err, false, map);
            } catch (Exception te) {
               // errors in the test message are logged
               store(engine, CAT.ERROR, te.getMessage() + " - msg ignored");
            }
         }
         RegErr[] errors = map.values().toArray(new RegErr[0]);
         Arrays.sort(errors);
         for (RegErr error : errors) {
            String severity = StringUtils.substringAfterLast(error.severity, ":");
            String msg = error.errorCode + ":" + severity + " - " + "Expected " + error.expected + ", found "
               + error.found + "\n" + error.codeContext + " " + error.location;
            CAT cat = CAT.SUCCESS;
            if (error.expected == 0) cat = CAT.WARNING;
            if (error.expected > error.found) cat = CAT.ERROR;
            store(engine, cat, msg);
         }

      } catch (Exception e) {
         throw new XdsInternalException("sameRetImgs error: " + e.getMessage());
      }
   } // EO prsSameRegErrors method

   private void prsRegistryErrorListNotEmpty(AssertionEngine engine, Assertion a, OMElement assertion_output)
           throws XdsInternalException {
      store(engine, CAT.INFO, "Assertion: RegistryErrorList element SHALL contain one or more RegistryError elements");

      // key is errorCode:severity
      Map <String, RegErr> map = new HashMap <>();

      try {
         // process and collect data on errors in std message
         OMElement stnd = getStdElement("ResponseBody");
         String t = stnd.getLocalName();
         if (t.endsWith("RetrieveDocumentSetResponse") == false)
            throw new XdsInternalException("sameRegErrors assertion only applies to RetrieveDocumentSetResponse");
         for (OMElement err : XmlUtil.decendentsWithLocalName(stnd, "RegistryError")) {
            try {
               post(err, true, map);
            } catch (Exception se) {
               // Errors in the std message are abort bait
               throw new XdsInternalException("sameRegErrors std msg error: " + se.getMessage());
            }
         }
         // process errors in test message
         OMElement test = getTestTrans(a, "response");
         String q = test.getLocalName();
         if (q.endsWith("RetrieveDocumentSetResponse") == false)
            throw new XdsInternalException("sameRegErrors assertion only applies to RetrieveDocumentSetResponse");
         for (OMElement err : XmlUtil.decendentsWithLocalName(test, "RegistryError")) {
            try {
               post(err, false, map);
            } catch (Exception te) {
               // errors in the test message are logged
               store(engine, CAT.ERROR, te.getMessage() + " - msg ignored");
            }
         }
         RegErr[] errors = map.values().toArray(new RegErr[0]);
         Arrays.sort(errors);
         for (RegErr error : errors) {
            String severity = StringUtils.substringAfterLast(error.severity, ":");
            String msg = "RegistryError element from system under test: " + this.formatRegistryErrorForLogging(error);
            CAT cat = CAT.INFO;
            store(engine, cat, msg);
         }
         if (errors.length > 0) {
            store(engine, CAT.SUCCESS, String.format("Found at least one RegistryError element in RegistryErrorList: count=%d", errors.length));
         } else {
            store(engine, CAT.ERROR, "Found no RegistryError elements in RegistryErrorList. At least one error element required");
         }

      } catch (Exception e) {
         throw new XdsInternalException("sameRetImgs error: " + e.getMessage());
      }
   } // EO prsRegistryErrorListNotEmpty method

   private String formatRegistryErrorForLogging(RegErr error) {
      String rtn = "";
      rtn = "errorCode=" + error.errorCode +
              " severity=" + error.severity +
              " codeContext=" + error.codeContext +
              " location=";
      if (error.location != null)
         rtn += error.location;
      return rtn;
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

   /**
    * Matches DICOM tag values in returned images to standard. Uses this format
    * of assert tag in testplan.xml:
    * 
    * <pre>
    * {@code
    * <Assert id="Returned img(s)" process="sameDcmImgs" >
    *    <TagList>
    *       Elements in TagList are the dcm4che Tag names for the DICOM
    *       tags which are to be compared. They must:
    *       1. Appear in tagMap (above)
    *       2. Have the Element name "HASH", in which case a hash of the entire
    *          file contents is used as the comparison value rather than the
    *          contents of a DICOM tag
    *       3. Have the Element name "TAG" and an attribute "hex" with
    *       a value which is a valid dicom tag group and element number 
    *       separated by a comma (for example hex="0020,0020") There are 
    *       four optional attributes for these elements:
    *       1. type - which should have the type of assertion, taken from
    *          the TYPE enum in DCMAssertion. The default value is "SAME",
    *          which is most common.
    *       2. value - which should have the string value which the DICOM
    *          tag should match. Only used in CONSTANT type assertions.
    *          The default value is the empty string.
    *       3. passCat - which should have the string CAT value to be used if 
    *          the assertion succeeds. The default value is SUCCESS.
    *       4. failCat - which should have the string CAT value to be used if 
    *          the assertion fails. The default value is ERROR.
    *       <SOPClassUID />
    *       <SOPInstanceUID />
    *       <PatientID />
    *       <PatientBirthDate />
    *       <PatientSex />
    *       <StudyInstanceUID />
    *       <SeriesInstanceUID />
    *    </TagList>
    *    <DirList>
    *       StdDir elements contain paths of directories which contain
    *       std image files for testing. May have more than one. All files
    *       in the directory are added to the list. Subdirectories are
    *       ignored. Directories are relative to the Image Cache
    *       <StdDir>path1</StdDir>
    *       <StdDir>path2</StdDir>
    *       <StdDir>path3</StdDir>
    *       TestDir element contains the path where the test images from
    *       the test are to be stored. Only one directory. May be 
    *       absolute, or relative to the test step log directory. Default
    *       is "testImages" in the test step log directory. TestDir element may
    *       also have testDir attribute, in which case directory is
    *       relative to that test step log directory.
    *       <TestDir>path</TestDir>
    *    </DirList>
    * </Assert> 
    * }
    * </pre>
    */
   private void prsSameDcmImgs(AssertionEngine engine, Assertion a, OMElement assertion_output)
      throws XdsInternalException {
      try {
         OMElement assertElement = a.assertElement;
         // Pull TagList and DirList child elements
         OMElement dirListElement = XmlUtil.firstChildWithLocalName(assertElement, "DirList");
         OMElement tagListElement = XmlUtil.firstChildWithLocalName(assertElement, "TagList");

         // Store images to testImgPath directory.
         Path testImgPath = Paths.get(linkage.getLogFileDir());
         String subDir = "testImages";
         if (dirListElement != null) {
            OMElement testDirElement = XmlUtil.firstChildWithLocalName(dirListElement, "TestDir");
            if (testDirElement != null) {
               String td = XmlUtil.getAttributeValue(testDirElement, "testDir");
               subDir = testDirElement.getText();
               if (StringUtils.isNotBlank(td)) {
                  testImgPath = Paths.get(linkage.getLogFileDir(td));
               }
            }
         }
         testImgPath = Paths.get(testImgPath.resolve(subDir).toFile().getCanonicalPath());
         File testImgDir = testImgPath.toFile();
         OMElement testResponseBody = getTestTrans(a, "response");
         if (testResponseBody != null ) {
            testImgDir.mkdirs();
            FileUtils.cleanDirectory(testImgDir);            
            storeFiles(testResponseBody, testImgPath);
         }
         // Make list of test image pfns
         List <String> testPfns = new ArrayList <>();
         for (String file : testImgDir.list()) {
            testPfns.add(testImgPath.resolve(file).toString());
         }

         // Make list of std image pfns
         List <String> stdPfns = new ArrayList <>();
         for (OMElement stdDirElement : XmlUtil.childrenWithLocalName(dirListElement, "StdDir")) {
            File stdDirFile = Installation.instance().imageCache("std" + File.separator + stdDirElement.getText());
            Utility.isValidPfn("test std img dir", stdDirFile, PfnType.DIRECTORY, "r");
            Collection <File> files = FileUtils.listFiles(stdDirFile, FileFilterUtils.fileFileFilter(), null);
            for (File file : files)
               stdPfns.add(file.getPath());
         }

         /*
          * Build list of assertions to be applied to image sets. Assertions are
          * in <TagList> Element. Element name is Tag name (from dcm4che). type
          * attribute is name of DCMAssertion#TYPE, default is SAME. value
          * attribute is constant to compare to, which only applies to type
          * CONSTANT, default is empty string.
          */
         List <DCMAssertion> assertions = new ArrayList <>();
         @SuppressWarnings("unchecked")
         Iterator <OMElement> tags = tagListElement.getChildElements();
         while (tags.hasNext()) {
            int dcmTag;
            OMElement tag = tags.next();
            String tagName = tag.getLocalName().trim();
            // HASH tag
            if (tagName.equalsIgnoreCase("HASH")) {
               dcmTag = -1;
            } else
            // Tag defined with hex value
            if (tagName.equalsIgnoreCase("Tag")) {
               String hex = tag.getAttributeValue(new QName("hex"));
               if (hex == null) throw new Exception("TagList Tag element has no hex attribute");
               String[] tkns = hex.split(",");
               if (tkns.length != 2) throw new Exception("TagList Tag element inv hex attr value [" + hex + "]");
               int group = 0, element = 0;
               try {
                  group = Integer.parseInt(tkns[0], 16);
                  element = Integer.parseInt(tkns[1], 16);
               } catch (Exception e) {
                  throw new Exception("TagList Tag element inv hex attr value [" + hex + "] " + e.getMessage());
               }
               dcmTag = TagUtils.toTag(group, element);
               tagName = TagUtils.toString(dcmTag);
            } else {
               // tags defined in tag map.
               if (tagMap.containsKey(tagName) == false) throw new Exception("Unknown DICOM Tag " + tagName);
               dcmTag = tagMap.get(tagName);
            }
            String typeName = tag.getAttributeValue(new QName("type"));
            if (typeName == null) typeName = "SAME";
            typeName = typeName.trim().toUpperCase();
            DCMAssertion.TYPE type = DCMAssertion.TYPE.valueOf(typeName);
            String value = tag.getAttributeValue(new QName("value"));
            if (value == null) value = "";
            value = value.trim();
            CAT passCat = CAT.forThis(tag.getAttributeValue(new QName("passCat")));
            CAT failCat = CAT.forThis(tag.getAttributeValue(new QName("failCat")));
            DCMAssertion dcmAssertion = new DCMAssertion(type, dcmTag, value, passCat, failCat);
            dcmAssertion.setTagName(tagName);
            assertions.add(dcmAssertion);
         }

         // Now we run the tests
         TestDcmSetContent test = new TestDcmSetContent();
         test.initializeTest(testPfns, stdPfns, assertions);
         test.runTest();
         Results results = test.getResults(a.process);
         String rep = results.toString();
         CAT cat = CAT.SUCCESS;
         if (results.getErrorCount() > 0) cat = CAT.ERROR;
         store(engine, cat, rep);

      } catch (Exception e) {
         throw new XdsInternalException("sameRetImgs error: " + e.getMessage());
      }
   }

   // Matches values in KOS to standard. Used on PnR transactions
   private void prsSameKOSDcm(AssertionEngine engine, Assertion a, OMElement assertion_output)
      throws XdsInternalException {
      try {
         // pfn of std KON.dcm
         String pfn = a.xpath.trim();
         String stdDcmPfn = Paths.get(testConfig.testplanDir.getAbsolutePath(), pfn).toString();
         SimulatorTransaction simulatorTransaction = getSimulatorTransaction(a);
         simulatorTransaction.setStdPfn(stdDcmPfn);
         // load sop instance uid from test kos for later comparison
         String tstKOSPfn = simulatorTransaction.getPfns().get(0);
         DicomInputStream din = new DicomInputStream(new File(tstKOSPfn));
         Attributes at = din.readDataset(-1, Tag.PixelData);
         String siu = at.getString(Tag.SOPInstanceUID);
         reportManager.addReport(new ReportDTO("SOPInstanceUID", siu));
         din.close();

         TestRAD68 testInstance = new TestRAD68();
         testInstance.initializeTest(a.process, simulatorTransaction);
         testInstance.runTest();
         Results results = testInstance.getResults(a.process);
         String rep = results.toString();
         CAT cat = CAT.SUCCESS;
         if (results.getErrorCount() > 0) cat = CAT.ERROR;
         store(engine, cat, rep);
         reportManagerPostRun();
      } catch (Exception e) {
         e.printStackTrace();
         throw new XdsInternalException("ImgDetailTransaction - sameKOSDcm: " + e.getMessage());
      }

   }

   private void prsSameKOSMetadata(AssertionEngine engine, Assertion a, OMElement assertion_output)
      throws XdsInternalException {
      try {
         // pfn of std metadata
         String pfn = a.xpath.trim();
         String stdMetadataPfn = Paths.get(testConfig.testplanDir.getAbsolutePath(), pfn).toString();

         SimulatorTransaction simulatorTransaction = getSimulatorTransaction(a);
         simulatorTransaction.setStdPfn(stdMetadataPfn);
         simulatorTransaction.setUseReportManager(useReportManager);
         TestRAD68 testInstance = new TestRAD68();
         testInstance.initializeTest(a.process, simulatorTransaction);
         testInstance.runTest();
         Results results = testInstance.getResults(a.process);
         String rep = results.toString();
         CAT cat = CAT.SUCCESS;
         if (results.getErrorCount() > 0) cat = CAT.ERROR;
         store(engine, cat, rep);
      } catch (Exception e) {
         throw new XdsInternalException("ImgDetailTransaction - sameKOSMetadata: " + e.getMessage());
      }
   }
   
   private void prsSameQuery(AssertionEngine engine, Assertion a, OMElement assertion_output)
      throws XdsInternalException {
      try {
      String pfn = a.xpath.trim();
      String stdPfn = Paths.get(testConfig.testplanDir.getAbsolutePath(), pfn).toString();
      SimulatorTransaction simTran = getSimulatorTransaction(a);
      simTran.setStdPfn(stdPfn);
      TestITI18 testInstance = new TestITI18();
      testInstance.initializeTest(a.process, simTran);
      testInstance.runTest();
      Results results = testInstance.getResults(a.process);
      String rep = results.toString();
      CAT cat = CAT.SUCCESS;
      if (results.getErrorCount() > 0) cat = CAT.ERROR;
      store(engine, cat, rep);
      } catch (Exception e) {
         throw new XdsInternalException("ImgDetailTransaction - sameQuery: " + e.getMessage());
      }
   }
   
   private void prsLoadSOAPSimTransaction(AssertionEngine engine, Assertion a, OMElement assertion_output) {
      store(engine, CAT.SUCCESS, "currently a no op");
//      try {
//         SimulatorTransaction simTran = getSimulatorTransaction(a);
//         store(engine, CAT.SUCCESS, "load successful");
//      } catch (XdsInternalException e) {
//         store(engine, CAT.ERROR, "");
//      }
   }
   
   private void prsSameRetrieve(AssertionEngine engine, Assertion a, OMElement assertion_output)
      throws XdsInternalException {
      String pfn = a.xpath.trim();
      String stdPfn = Paths.get(testConfig.testplanDir.getAbsolutePath(), pfn).toString();
      SimulatorTransaction simTran = getSimulatorTransaction(a);
      simTran.setStdPfn(stdPfn);
   }

   private Map <String, RetImg> loadRetImgs(AssertionEngine engine, Assertion a, OMElement msg) {
      Map <String, RetImg> imgs = new LinkedHashMap <>();
      List <OMElement> docs = XmlUtil.decendentsWithLocalName(msg, "DocumentResponse");
      for (OMElement docReq : docs) {
         RetImg img = new RetImg();
         img.instance = loadTxt(docReq, "DocumentUniqueId");
         img.home = loadTxt(docReq, "HomeCommunityId");
         img.repo = loadTxt(docReq, "RepositoryUniqueId");
         img.mime = loadTxt(docReq, "mimeType");
         imgs.put(img.instance, img);
      }
      return imgs;
   }

   class RetImg {
      String instance;
      String home;
      String repo;
      String mime;
   }

   private String getResponseStatus(OMElement retDocSetRespElement) {
      String status = "No RegistryReponse Element";
      try {
         OMElement regRespElement = XmlUtil.onlyChildWithLocalName(retDocSetRespElement, "RegistryResponse");
         status = regRespElement.getAttributeValue(new QName("status"));
         if (status == null) status = "No status attribute";
      } catch (Exception e) {}
      return status;
   }
   
   /**
    * Matches documents and homecommunity id values in a 
    * {@code <RetrieveDocumentSetResponse>}
    * against a "gold standard" response which is in the {@code <ResponseBody>}
    * child element of the testplan.xml {@code <Standard>} element.
    * 
    * @param engine AssertionEngine instance
    * @param a Assert being processed
    * @param assertion_output log.xml output element for that assert
    * @throws XdsInternalException on error
    */
   private void prsMatchHomeCommunityIds(AssertionEngine engine, Assertion a, OMElement assertion_output)
            throws XdsInternalException {
      try {
         OMElement std = getStdElement("ResponseBody");
         OMElement test = getTestTrans(a, "response");
         String t = std.getLocalName();
         if (t.endsWith("RetrieveDocumentSetResponse") == false)
            throw new XdsInternalException("matchHomeCommunityIds assertion only applies to RetrieveDocumentSetResponse");
        
         Map <String, RetImg> testImgs = loadRetImgs(engine, a, test);
         Map <String, RetImg> stdImgs = loadRetImgs(engine, a, std);
         Set <String> testKeys = testImgs.keySet();
         for (String testKey : testKeys) {
            if (stdImgs.containsKey(testKey) == false) {
               store(engine, CAT.ERROR, "test doc UID " + testKey + ", not found in standard.");
               continue;
            }
            RetImg testImg = testImgs.get(testKey);
            RetImg stdImg = stdImgs.get(testKey);
            stdImgs.remove(testKey);
            store(engine, CAT.SUCCESS, "test doc UID " + testKey + ", found in std.");
            if (compMandatory(stdImg.home, testImg.home)) {
               store(engine, CAT.SUCCESS, "for doc with UID: " + testKey +
               " homeCommunityID match (std/test): (" + stdImg.home + "/" + testImg.home + ")");
            } else {
               store(engine, CAT.ERROR, "for doc with UID: " + testKey +
               " homeCommunityID mismatch (std/test): (" + stdImg.home + "/" + testImg.home + ")");
            }
         }
         if (stdImgs.isEmpty()) return;
         Set <String> stdKeys = stdImgs.keySet();
         for (String key : stdKeys)
            store(engine, CAT.ERROR, "std doc UID: " + key + " not found in test msg.");
      } catch (Exception e) {
         throw new XdsInternalException("sameRetImgs error: " + e.getMessage());
      }
   }

   /**
    * Expects the current testplan.xml {@code <TestStep>} element to contain:
    * 
    * <pre>
    *  {@code
    * <Standard>
    *    ...
    *    <name>
    *       <SomeSingleElement>...</SomeSingleElement>
    *    </name>
    *    ...
    * </Standard>}
    * </pre>
    * 
    * @param name String element name for {@code <Standard>} child element to
    * return.
    * @return OMElement for the internal element, in this example
    * {@code <SomeSingleElement>}.
    * @throws XdsInternalException on error, typically xml that is missing (or
    * has more than one of) a required Element.
    */
   private OMElement getStdElement(String name) throws XdsInternalException {
      try {
         OMElement standard = XmlUtil.onlyChildWithLocalName(step, "Standard");
         OMElement element = XmlUtil.onlyChildWithLocalName(standard, name);
         OMElement retVal = XmlUtil.onlyChildWithLocalName(element, null);
         if (retVal == null) throw new Exception("std name has no child element");
         return retVal;
      } catch (Exception e) {
         throw new XdsInternalException(e.getMessage());
      }
   }

   private OMElement getTestTrans(Assertion a, String piece) throws XdsInternalException {
      // Get response from log.xml... somewhere.
      OMElement testResponseElement = XmlUtil.firstChildWithLocalName(a.assertElement, "TestResponse");
      if (testResponseElement != null) {
         String testDir = testResponseElement.getAttributeValue(new QName("testDir"));
         if (testDir.equalsIgnoreCase("THIS")) testDir = null;
         String stepp = testResponseElement.getAttributeValue(new QName("step"));
         return linkage.findResultInLog(stepp, testDir).getFirstElement();
      }
      // Get response from simulator transaction
      OMElement simTransactionElement = XmlUtil.firstChildWithLocalName(a.assertElement, "SimTransaction");
      if (simTransactionElement != null) {
         String id = simTransactionElement.getAttributeValue(new QName("id"));
         String trans = simTransactionElement.getAttributeValue(new QName("transaction"));
         String pid = simTransactionElement.getAttributeValue(new QName("pid"));
         TransactionType tType = TransactionType.find(trans);
         if (tType == null) throw new XdsInternalException(a.toString() + " invalid transaction");
         TestInstance ti = testConfig.testInstance;
         SimId simId = SimDb.getFullSimId(new SimId(ti.getTestSession(), id));
         SimulatorTransaction simulatorTransaction = SimulatorTransaction.get(simId, tType, pid, null);
         try {
            switch (piece.toUpperCase()) {
               case "RESPONSE":
                  return AXIOMUtil.stringToOM(simulatorTransaction.getResponseBody());
               case "REQUEST":
                  return AXIOMUtil.stringToOM(simulatorTransaction.getRequestBody());
               default:
                  throw new XdsInternalException("invalid test transaction piece " + piece);
            }
         } catch (XMLStreamException e) {
            throw new XdsInternalException(e.getMessage());
         }
      }
      return null;
   }

   private SimulatorTransaction getSimulatorTransaction(Assertion a) throws XdsInternalException {
      try {
      OMElement simTransactionElement = XmlUtil.firstChildWithLocalName(a.assertElement, "SimReference");
      if (simTransactionElement == null)
         throw new XdsInternalException(a.toString() + " has no SimReference element");

      String actor = simTransactionElement.getAttributeValue(new QName("actor"));
      String id    = simTransactionElement.getAttributeValue(new QName("id"));
      String trans = simTransactionElement.getAttributeValue(new QName("transaction"));
      String pid   = simTransactionElement.getAttributeValue(new QName("pid"));

      TransactionType tType = TransactionType.find(trans);
      if (tType == null) throw new XdsInternalException(a.toString() + " invalid transaction");
      ActorType aType = ActorType.findActor(actor);
      TestInstance ti = testConfig.testInstance;
      SimId simId = SimDb.getFullSimId(new SimId(ti.getTestSession(), id));
      return SimulatorTransaction.get(simId, aType, tType, pid, null);
      } catch (XdsInternalException ie) {
         errs.add("Error loading simulator transaction" + ie.getMessage());
         throw ie;
      }
   }

   /*
    * helper for loadImgs. Gets text content of child tagName of element e.
    * Returns null if no such child or more than one such child.
    */
   private String loadTxt(OMElement e, String tagName) {
      try {
         OMElement child = XmlUtil.onlyChildWithLocalName(e, tagName);
         return child.getText().trim();
      } catch (Exception e1) {}
      return null;
   }

   private void store(AssertionEngine e, CAT cat, String msg) {
      if (cat == CAT.SILENT) return;
      for (String line : StringUtils.split(msg, Utility.nl)) {
         if (cat == CAT.ERROR) errs.add(line);
         else e.addDetail(line);
      }
   }

   /*
    * helper method for string compares between std and test where an empty or
    * null std value means the value is not required. Used for home and
    * repository UIDs.
    */
   private boolean comp(String std, String test) {
      if (std == null || std.length() == 0) return true;
      if (test == null || test.length() == 0) return false;
      return std.equals(test);
   }
   
   /*
    * helper method for string compares between std and test where an empty or
    * null std value is considered to match an empty or null test value. Used 
    * for home and repository UIDs.
    */
   private boolean compMandatory(String std, String test) {
      if (StringUtils.isBlank(std) && StringUtils.isBlank(test)) return true;
      return std.equals(test);
   }

   private void storeFiles(OMElement respBody, Path dirPath) throws Exception {
      Iterator <OMElement> docRespEles = respBody.getChildrenWithLocalName("DocumentResponse");
      while (docRespEles.hasNext()) {
         OMElement docRespEle = docRespEles.next();
         String docUID = XmlUtil.onlyChildWithLocalName(docRespEle, "DocumentUniqueId").getText();
         OMElement docEle = XmlUtil.onlyChildWithLocalName(docRespEle, "Document");
         String txt = docEle.getText();
         byte[] contents;
         Base64.Decoder d = Base64.getDecoder();
         try {
            contents = d.decode(txt);
         } catch (IllegalArgumentException iae1) {
            contents = String.format("ImgDetailTransaction Base64 Decode Exception %s. String length was %d.", iae1.toString(), (txt!=null)?txt.length():0).getBytes();
         }
         File dcmFile = dirPath.resolve(docUID + ".dcm").toFile();
         FileUtils.writeByteArrayToFile(dcmFile, contents);
      }

   }

} // EO ImgDetailTransaction class
