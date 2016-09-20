/**
 * 
 */
package gov.nist.toolkit.testengine.transactions;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.dcm4che3.data.Tag;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.installation.PropertyManager;
import gov.nist.toolkit.registrymsg.repository.Mtom;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testengine.assertionEngine.Assertion;
import gov.nist.toolkit.testengine.assertionEngine.AssertionEngine;
import gov.nist.toolkit.testengine.engine.*;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import edu.wustl.mir.erl.ihe.xdsi.util.OMEUtil;
import edu.wustl.mir.erl.ihe.xdsi.util.PfnType;
import edu.wustl.mir.erl.ihe.xdsi.util.Utility;
import edu.wustl.mir.erl.ihe.xdsi.validation.*;

/**
 * Handles Image detail validations
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project
 * <a href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 */
public class ImgDetailTransaction extends BasicTransaction {

   private OMElement step;
   private PropertyManager pMgr;

   /*
    * These are the DICOM tags currently supported for evaluation tasks. New
    * ones can be added to this map if needed.
    */
   private static Map <String, Integer> tagMap;
   static {
      tagMap = new HashMap <>();
      tagMap.put("SOPClassUID", Tag.SOPClassUID);
      tagMap.put("SOPInstanceUID", Tag.SOPInstanceUID);
      tagMap.put("PatientID", Tag.PatientID);
      tagMap.put("PatientBirthDate", Tag.PatientBirthDate);
      tagMap.put("PatientSex", Tag.PatientSex);
      tagMap.put("StudyInstanceUID", Tag.StudyInstanceUID);
      tagMap.put("SeriesInstanceUID", Tag.SeriesInstanceUID);
      tagMap.put("AccessionNumber", Tag.AccessionNumber);
      tagMap.put("Modality", Tag.Modality);
      tagMap.put("PatientName", Tag.PatientName);
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
      pMgr = Installation.installation().propertyServiceManager().getPropertyManager();
      this.step = step;
   }

   @Override
   public void runAssertionEngine(OMElement step_output, ErrorReportingInterface eri, OMElement assertion_output)
      throws XdsInternalException {

      AssertionEngine engine = new AssertionEngine();
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

   @Override
   public void processAssertion(AssertionEngine engine, Assertion a, OMElement assertion_output)
      throws XdsInternalException {
      errs = new ArrayList <>();
      switch (a.process) {
         /*
          * Matches documents and their values in SOAP Response body to standard
          */
         case "sameRetImgs":
            try {
               OMElement std = getStdResponseBody();
               OMElement test = getTestResponseBody();
               String t = std.getLocalName();
               if (t.endsWith("RetrieveDocumentSetResponse") == false)
                  throw new XdsInternalException("sameRetImgs assertion only applies to RetrieveDocumentSetResponse");
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
                  if (comp(stdImg.home, testImg.home) && comp(stdImg.repo, testImg.repo)
                     && comp(stdImg.mime, testImg.mime)) {
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
               if (stdImgs.isEmpty()) break;
               Set <String> stdKeys = stdImgs.keySet();
               for (String key : stdKeys)
                  store(engine, CAT.ERROR, "std doc UID: " + key + " not found in test msg.");
            } catch (Exception e) {
               throw new XdsInternalException("sameRetImgs error: " + e.getMessage());
            }
            break;
         /*
          * Matches DICOM tag values in returned images to standard. Uses this
          * format of assert tag in testplan.xml
          * <Assert id="Returned img(s)" process="sameDcmImgs" >
               <TagList>
                  Elements in TagList are the dcm4che Tag names for the DICOM
                  tags which are to be compared. They must appear in tagMap
                  (above). There are two optional attributes for these elements:
                  1. type - which should have the type of assertion, taken from
                     the TYPE enum in DCMAssertion. The default value is "SAME",
                     which is most common.
                  2. value - which should have the string value which the DICOM
                     tag should match. Only used in CONSTANT type assertions.
                     The default value is the empty string.
                  <SOPClassUID />
                  <SOPInstanceUID />
                  <PatientID />
                  <PatientBirthDate />
                  <PatientSex />
                  <StudyInstanceUID />
                  <SeriesInstanceUID />
               </TagList>
               <DirList>
                  StdDir elements contain paths of directories which contain
                  std image files for testing. May have more than one. All files
                  in the directory are added to the list. Subdirectories are
                  ignored. Directories may be absolute, or relative to the
                  External Cache root.
                  <StdDir>path1</StdDir>
                  <StdDir>path2</StdDir>
                  <StdDir>path3</StdDir>
                  TestDir element contains the path where the test images from
                  the test are to be stored. Only one directory. May be 
                  absolute, or relative to the test step log directory. Default
                  is "testImages" in the test step log directory.
                  <TestDir>path</TestDir>
               </DirList>
            </Assert>
          */
         case "sameDcmImgs":
            try {
              OMElement assertElement = a.assertElement;
              // Pull TagList and DirList child elements
              OMElement dirListElement = OMEUtil.firstChildWithLocalName(assertElement, "DirList");
              OMElement tagListElement = OMEUtil.firstChildWithLocalName(assertElement, "TagList");
              
              // Store images to testImgPath directory.
              Path testImgPath = Paths.get(linkage.getLogFileDir());
              String subDir = "testImages";
              if (dirListElement != null) {
                 OMElement testDirElement = OMEUtil.firstChildWithLocalName(dirListElement, "TestDir");
                 if (testDirElement != null) {
                    subDir = testDirElement.getText();
                 }
              }
              testImgPath = testImgPath.resolve(subDir);
              File testImgDir = testImgPath.toFile();
              testImgDir.mkdirs();
              FileUtils.cleanDirectory(testImgDir);
              OMElement testResponseBody = getTestResponseBody();
              storeFiles(testResponseBody, testImgPath);
              // Make list of test image pfns
              List<String> testPfns = new ArrayList<>();
              for (String file : testImgDir.list()) {
                 testPfns.add(testImgPath.resolve(file).toString());
              }
              
              //Make list of std image pfns
              Path extCache = Paths.get(pMgr.getExternalCache());
              List<String> stdPfns = new ArrayList<>();
              for (OMElement stdDirElement : OMEUtil.childrenWithLocalName(dirListElement, "StdDir")) {
                 Path stdDirPath = extCache.resolve(stdDirElement.getText());
                 Utility.isValidPfn("test std img dir", stdDirPath, PfnType.DIRECTORY, "r");
                 Collection<File> files = FileUtils.listFiles(stdDirPath.toFile(), FileFilterUtils.fileFileFilter(), null);
                 for (File file : files) stdPfns.add(file.getPath());
              }
              
              /* 
               * Build list of assertions to be applied to image sets.
               * Assertions are in <TagList> Element. Element name is Tag name
               * (from dcm4che). type attribute is name of DCMAssertion#TYPE,
               * default is SAME. value attribute is constant to compare to,
               * which only applies to type CONSTANT, default is empty string.
               */
              List<DCMAssertion> assertions = new ArrayList<>();
              @SuppressWarnings("unchecked")
            Iterator<OMElement> tags = tagListElement.getChildElements();
              while (tags.hasNext()) {
                 OMElement tag = tags.next();
                 String tagName = tag.getLocalName().trim();
                 if (tagMap.containsKey(tagName) == false)
                    throw new Exception("Unknown DICOM Tag " + tagName);
                 int dcmTag = tagMap.get(tagName);
                 String typeName = tag.getAttributeValue(new QName("type"));
                 if (typeName == null) typeName = "SAME";
                 typeName = typeName.trim().toUpperCase();
                 DCMAssertion.TYPE type = DCMAssertion.TYPE.valueOf(typeName);
                 String value = tag.getAttributeValue(new QName("value"));
                 if (value == null) value = "";
                 value = value.trim();
                 assertions.add(new DCMAssertion(type, dcmTag, value));
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
            break;
         // Matches values in KON to standard. Used on PnR transactions
         case "sameKONDcm":
            try {
               // pfn of std KON.dcm
               String stdDcmPfn = Paths.get(testConfig.testplanDir.getAbsolutePath(), a.xpath).toString();
               TestInstance ti = testConfig.testInstance; 
               SimId simId = new SimId(ti.getUser(), "rep_reg", ActorType.REPOSITORY.getShortName(), "xdsi");
               
               SimulatorTransaction simulatorTransaction =
                  SimulatorTransaction.get(simId, TransactionType.PROVIDE_AND_REGISTER, null, null);
               simulatorTransaction.setStdPfn(stdDcmPfn);
               TestRAD68 testInstance = new TestRAD68();
               testInstance.initializeTest(a.process, simulatorTransaction);
               testInstance.runTest();
               Results results = testInstance.getResults(a.process);
               String rep = results.toString();
               CAT cat = CAT.SUCCESS;
               if (results.getErrorCount() > 0) cat = CAT.ERROR;
               store(engine, cat, rep);
            } catch (Exception e) {
               throw new XdsInternalException("ImgDetailTransaction - sameKONDcm: " + e.getMessage());
            }
            break;
         case "sameKONMetadata":try {
            // pfn of std metadata
            String stdMetadataPfn = Paths.get(testConfig.testplanDir.getAbsolutePath(), a.xpath).toString();
            TestInstance ti = testConfig.testInstance; 
            SimId simId = new SimId(ti.getUser(), "rep_reg", ActorType.REPOSITORY.getShortName(), "xdsi");
            
            SimulatorTransaction simulatorTransaction =
               SimulatorTransaction.get(simId, TransactionType.PROVIDE_AND_REGISTER, null, null);
            simulatorTransaction.setStdPfn(stdMetadataPfn);
            TestRAD68 testInstance = new TestRAD68();
            testInstance.initializeTest(a.process, simulatorTransaction);
            testInstance.runTest();
            Results results = testInstance.getResults(a.process);
            String rep = results.toString();
            CAT cat = CAT.SUCCESS;
            if (results.getErrorCount() > 0) cat = CAT.ERROR;
            store(engine, cat, rep);
         } catch (Exception e) {
            throw new XdsInternalException("ImgDetailTransaction - sameKONDcm: " + e.getMessage());
         }
            break;
         default:
            throw new XdsInternalException("ImgDetailTransaction: Unknown assertion.process " + a.process);
      }
      if (errs.isEmpty() == false) {
         StringBuilder em = new StringBuilder();
         for (String err : errs) {
            em.append(StringEscapeUtils.escapeHtml(err)).append("\n");
         }
         ILogger testLogger = new TestLogFactory().getLogger();
         testLogger.add_name_value_with_id(assertion_output, "AssertionStatus", a.id, "fail");
         s_ctx.fail(em.toString());
      }
   } // EO processAssertion method

   private Map <String, RetImg> loadRetImgs(AssertionEngine engine, Assertion a, OMElement msg) {
      Map <String, RetImg> imgs = new LinkedHashMap <>();
      List <OMElement> docs = XmlUtil.decendentsWithLocalName(msg, "DocumentResponse");
      if (docs.isEmpty()) {
         store(engine, CAT.ERROR, "No DocumentResponse element(s) found.");
         return imgs;
      }
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

   private OMElement getStdResponseBody() throws XdsInternalException {
      try {
         OMElement standard = XmlUtil.onlyChildWithLocalName(step, "Standard");
         OMElement responseBody = XmlUtil.onlyChildWithLocalName(standard, "ResponseBody");
         return XmlUtil.onlyChildWithLocalName(responseBody, null);
      } catch (Exception e) {
         throw new XdsInternalException(e.getMessage());
      }
   }

   private OMElement getTestResponseBody() throws XdsInternalException {
      return linkage.findResultInLog("retrieve", "").getFirstElement();
   }

   /*
    * helper for loadImgs. Gets text content of child tagName of element e.
    * Returns null if no such child or more than one such child.
    */
   private String loadTxt(OMElement e, String tagName) {
      try {
         OMElement child = XmlUtil.onlyChildWithLocalName(e, tagName);
         return child.getText();
      } catch (Exception e1) {}
      return null;
   }

   /**
    * Result categories. Used to group validation results for reporting.
    */
   public enum CAT {
         /**
          * Expected result was found.
          */
      SUCCESS, /**
                * A result was found which is not being tested, but which may be
                * in error or "not what you want". May also relate to something
                * expected, but not found.
                */
      WARNING, /**
                * Expected result was missing or incorrect.
                */
      ERROR, /**
              * Message which was generated but is not (or cannot be) determined
              * to be in SUCCESS, WARNING, or ERROR categories.
              */
      UNCAT, /**
              * A message result or lack of result which was detected but will
              * be ignored. This is for programmers only; the tester will not
              * see these.
              */
      SILENT;

      /**
       * Get CAT which matches name, ignoring case, or null
       * 
       * @param name of CAT
       * @return CAT for name
       */
      public static CAT forThis(String name) {
         CAT[] cats = CAT.values();
         for (CAT cat : cats) {
            if (cat.name().equalsIgnoreCase(name)) return cat;
         }
         return null;
      }
   };

   private void store(AssertionEngine e, CAT cat, String msg) {
      if (cat == CAT.SILENT) return;
      e.addDetail(cat.name() + " " + msg);
      if (cat == CAT.ERROR) errs.add(cat.name() + " " + msg);
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
   
   private void storeFiles(OMElement respBody, Path dirPath) throws Exception {
      Iterator<OMElement> docRespEles = respBody.getChildrenWithLocalName("DocumentResponse");
      while (docRespEles.hasNext()) {
         OMElement docRespEle = docRespEles.next();
         String docUID = XmlUtil.onlyChildWithLocalName(docRespEle, "DocumentUniqueId").getText();
         OMElement docEle = XmlUtil.onlyChildWithLocalName(docRespEle, "Document");
         Mtom mtom = new Mtom();
         mtom.decode(docEle);
         File dcmFile = dirPath.resolve(docUID + ".dcm").toFile();
         FileUtils.writeByteArrayToFile(dcmFile, mtom.getContents());
      }
      
   }
   
} // EO ImgDetailTransaction class
