/**
 * 
 */
package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.testengine.assertionEngine.Assertion;
import gov.nist.toolkit.testengine.assertionEngine.AssertionEngine;
import gov.nist.toolkit.testengine.engine.ErrorReportingInterface;
import gov.nist.toolkit.testengine.engine.ILogger;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.testengine.engine.TestLogFactory;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.*;

/**
 * Handles XmlDetail validations
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project
 * <a href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 */
public class XmlDetailTransaction extends BasicTransaction {

   private OMElement step;

   /**
    * @param s_ctx
    * @param step
    * @param instruction
    * @param instruction_output
    */
   public XmlDetailTransaction(StepContext s_ctx, OMElement step, OMElement instruction, OMElement instruction_output) {
      super(s_ctx, instruction, instruction_output);
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
      /*
       * TODO Working here At this point stdResponse and testResponse have the
       * data we want to validate. Need to incorporate the xml validation stuff
       * from xdsi and figure out how to put the results into the output log.
       */
   }

   @Override
   protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
      parseBasicInstruction(part);
   }

   @Override
   protected String getRequestAction() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   protected String getBasicTransactionName() {
      return "XmlDetail";
   }

   private List<String> errs;
   @Override
   public void processAssertion(AssertionEngine engine, Assertion a, OMElement assertion_output) throws XdsInternalException {
      errs = new ArrayList<>();
      switch (a.process) {
         case "sameRetImgs":
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
            break;
         default:
            throw new XdsInternalException("XmlDetailTransaction: Unknown assertion.process " + a.process);
      }
      if (errs.isEmpty() == false) {
         StringBuilder em = new StringBuilder();
         for(String err : errs) {
            em.append(StringEscapeUtils.escapeHtml(err)).append("\n");
         }
         ILogger testLogger = new TestLogFactory().getLogger();
         testLogger.add_name_value_with_id(assertion_output, "AssertionStatus", a.id, "fail");
         s_ctx.fail(em.toString());
      }
   }

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
      if (cat != CAT.ERROR) {
         e.addDetail(cat.name() + " " + msg);
         return;
      }
      errs.add(cat.name() + " " + msg);
      // TODO method stub
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
}
