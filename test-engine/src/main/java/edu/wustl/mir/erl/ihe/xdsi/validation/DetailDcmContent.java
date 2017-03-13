/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import edu.wustl.mir.erl.ihe.xdsi.util.PfnType;
import edu.wustl.mir.erl.ihe.xdsi.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.javatuples.Pair;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Base class for Detail comparing two dicom document's header tags
 */
public abstract class DetailDcmContent extends Detail {
   
   private static Path xdsi;
   
   static {
      xdsi = Paths.get(Utility.getXDSIRoot());
   }
   
   /**
    * One element for each {@link DCMAssertion} being performed.
    */
   protected List<DCMAssertion> assertions = null;
   
   /**
    * Implement in subclass for specific dicom object content tested.
    * Shall set {@link #assertions} and {{@link #desc}
    */
   protected abstract void initializeTests();
   
   protected String testPfn;
   protected String stdPfn;
   protected Attributes testAttr;
   protected Attributes stdAttr;

   private DicomInputStream din = null;
   
   /**
    * Get Attributes from test and std dicom documents
    * @return Pair with the Attributes, Value0 is the test, Value1 is the std
    * @throws Exception on error.
    */
   public Pair<Attributes, Attributes> getParents() throws Exception {
      if (testAttr == null || stdAttr == null) loadDcmDocuments();
      return new Pair<Attributes, Attributes>(testAttr, stdAttr);
   }
   
   /**
    * @param testPfn String pfn of test dicom object file. Absolute path or
    * relative to XDSI Root.
    * @param stdPfn String pfn of std dicom object file used for comparison (The
    * "gold standard" object). Absolute path or relative to XDSI Root.
    */
   @SuppressWarnings("hiding")
   void initializeDetail(String testPfn, String stdPfn) {
      this.testPfn = testPfn;
      this.stdPfn = stdPfn;
      initializeTests();
   }

   
   @Override
   public void runDetail() throws Exception {
      String testVal = null;
      Object testObj = null;
      String t = null;
      String s = null;
      try {
         loadDcmDocuments();
         for (DCMAssertion assertion : assertions) {
            switch (assertion.type) {
               case PRESENT:
                  testObj = testAttr.getValue(assertion.tag);
                  if (testObj != null) pass(assertion);
                  else fail(assertion);
                  break;
               case NOT_EMPTY:
                  testObj = testAttr.getValue(assertion.tag);
                  if (testObj != null && 
                      StringUtils.isNotBlank(testObj.toString())) pass(assertion);
                  else fail(assertion);
                  break;
               case ABSENT:
                  testObj = testAttr.getValue(assertion.tag);
                  if (testObj == null) pass(assertion);
                  else fail(assertion);
                  break;
               case CONSTANT:
                  testVal = testAttr.getString(assertion.tag, "").trim();
                  if (testVal.equals(assertion.value)) pass(assertion, testVal);
                  else fail(assertion, testVal, assertion.value);
                  break;
               case SAME:
                  t = testAttr.getString(assertion.tag);
                  s = stdAttr.getString(assertion.tag);
                  if (t == null && testAttr.contains(assertion.tag)) {
                     fail(assertion, "Zero length element detected when expecting a value", s);
                  } else if (t == null) {
                     fail(assertion, "Element not present in object when expecting a value", s);
                  } else if (StringUtils.isNotBlank(t) && StringUtils.isNotBlank(s) &&
                     t.equals(s)) {
                     pass(assertion, t);
                  } else {
                     fail(assertion, t, s);
                  }
                  break;
               case DIFFERENT:
                   t = testAttr.getString(assertion.tag);
                   s = stdAttr.getString(assertion.tag);
                   if (StringUtils.isNotBlank(t) && StringUtils.isNotBlank(s) &&
                      !t.equals(s)) pass(assertion, t);
                   else fail(assertion, t, s);
                   break;
               case SAME_SIZE:
                  int testSize = testAttr.getSequence(assertion.tag).size();
                  int stdSize = stdAttr.getSequence(assertion.tag).size();
                  if (testSize == stdSize) 
                     pass(assertion, Integer.toString(testSize));
                  else 
                     fail(assertion, Integer.toString(testSize), Integer.toString(stdSize));
                  break;
               default:
                  throw new Exception("invalid test type");
            }
         }
      } catch (Exception e) {
         String em = "Evaluation error " + desc + " " + Utility.getEM(e);
         Utility.getLog().warn(em);
         errorCount++;
         errorDetails.add(em);
      } finally {
         if (din != null) din.close();
      }

   } // EO runDetail method
   
   private void loadDcmDocuments() throws Exception {

      Path testPath = xdsi.resolve(testPfn);
      Utility.isValidPfn("test doc", testPath, PfnType.FILE, "r");
      din = new DicomInputStream(testPath.toFile());
      testAttr = din.readDataset(-1, Tag.PixelData);
      din.close();
      din = null;

      Path stdPath = xdsi.resolve(stdPfn);
      Utility.isValidPfn("std doc", stdPath, PfnType.FILE, "r");
      din = new DicomInputStream(stdPath.toFile());
      stdAttr = din.readDataset(-1, Tag.PixelData);
      din.close();
      din = null;
   }
   
   private void pass(DCMAssertion test, String... values) {
      store(test, test.passCat, append(test.passDetail, values));
   }
   private void fail(DCMAssertion test, String... values) {
      store(test, test.failCat, append(test.failDetail, values));
   }
   private String append(String prefix, String[] values) {
      if (values.length > 0)
         prefix += " found-[" + values[0].trim() + "]";
      if (values.length > 1)
         prefix += " expected-[" + values[1].trim() + "]";
      return prefix;
   }
   
   private void store(DCMAssertion test, CAT cat, String detail) {
      switch (cat) {
         case SUCCESS:
            successCount++;
            successDetails.add(detail);
            break;
         case WARNING:
            warningCount++;
            warningDetails.add(detail);
            break;
         case ERROR:
            errorCount++;
            errorDetails.add(detail);
            break;
         case UNCAT:
            uncategorizedCount++;
            uncategorizedDetails.add(detail);
            break;
         case SILENT:
         default:
      }
   }
}
