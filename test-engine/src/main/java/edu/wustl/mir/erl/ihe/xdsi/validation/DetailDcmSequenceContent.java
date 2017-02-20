/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import edu.wustl.mir.erl.ihe.xdsi.util.Utility;
import edu.wustl.mir.erl.ihe.xdsi.validation.DetailDcmSetContent.AttributesAndHash;
import org.apache.commons.lang3.StringUtils;
import org.dcm4che3.data.Keyword;
import org.dcm4che3.data.Sequence;

import java.util.ArrayList;
import java.util.List;

/**
 *  Base class for Detail comparing two dicom document 
 */
public abstract class DetailDcmSequenceContent extends Detail {
   /**
    * One element for each {@link DCMAssertion} being performed.
    */
   protected List<DCMAssertion> assertions = null;
   /**
    * One element for each sub sequence being processed directly in this sequence
    */
   protected List<Class <? extends DetailDcmSequenceContent>> subSeqs = new ArrayList<>();
   /**
    * Implement in subclass for specific dicom object content tested.
    * Shall set {@link #assertions}
    */
   protected abstract void initializeTest();
   
   protected int uniqueSequenceTag = 0;
   protected String uniqueSequenceName;
   protected List<AttributesAndHash> test = null;
   protected List<AttributesAndHash>  std = null;
   List<Entry> testEntries = null;
   List<Entry> stdEntries = null;
   
   /**
    * Must be implemented in sub class and invoked before runDetail().
    * Initialize:<ol>
    * <li/>desc user readable 'name' of sequence being examined.
    * <li/>uniqueSequenceTag, Tag in Attributes which uniquely identifies a
    * particular instance for matching.
    * <li/>test, List of Attributes sets to examine from test parent.
    * <li/>std, List of Attributes sets to examine from std parent.
    * @param args see subclass for details.
    * @throws Exception on error, usually coding or I/O.
    */
   protected abstract void initializeDetail(Object[] args) throws Exception;
   

   /* (non-Javadoc)
    * @see edu.wustl.mir.erl.ihe.xdsi.validation.Detail#runDetail()
    */
   @Override
   public void runDetail() throws Exception {
	   System.out.println("DetailDcmSequenceContent#runDetail");
      uniqueSequenceName = Keyword.valueOf(uniqueSequenceTag);
      /*
       * Create Entry instances for each item in the test and std Sequence 
       * objects, loading them into Lists.
       */
      testEntries = new ArrayList<>();
      for (AttributesAndHash entry : test) {
         Entry e = new Entry(entry);
         e.uniqueId = entry.attributes.getString(uniqueSequenceTag);
         testEntries.add(e); 
         }
      stdEntries = new ArrayList<>();
      for (AttributesAndHash entry : std) {
         Entry e = new Entry(entry);
         e.uniqueId = entry.attributes.getString(uniqueSequenceTag);
         stdEntries.add(e);
      }
      // Collect info on matched/not matched items here
      int totalMatched = 0;
      int totalMatchedAndPassed = 0;
      int testNoSequenceTotal = 0;
      int testNotInStdTotal = 0;
      String testNotInStdDetail = "";
      int stdNoSequenceTotal = 0;
      int stdNotInTestTotal = 0;
      String stdNotInTestDetail = "";
      
      /*
       * Pass test Entry list, looking for a matching Entry for each one in the
       * std Entry list, based on the uniqueSequenceTag values. Note missing
       * uniqueSequenceTag values in both lists. When a match is found, run
       * the detail tests for test/std Entry pair.
       */
      for (Entry testEntry: testEntries) {
         if (StringUtils.isBlank(testEntry.uniqueId)) {
            testNoSequenceTotal++;
            testEntry.missing = true;
            continue;
         }
         for (Entry stdEntry : stdEntries) {
            /*
             * If stdEntry already matched another testEntry, skip it.  This 
             * will avoid multiple matches in the case where the test Sequence
             * values are not actually unique.
             */
            if (stdEntry.matched == true) continue;
            if (stdEntry.missing == true) continue;
            if (StringUtils.isBlank(stdEntry.uniqueId)) {
               stdNoSequenceTotal++;
               stdEntry.missing = true;
               continue;
            }
            if (testEntry.uniqueId.equals(stdEntry.uniqueId)) {
               testEntry.matched = true;
               stdEntry.matched = true;
               totalMatched++;
               runDetail(testEntry, stdEntry);
               runSubSeqs(testEntry, stdEntry);
               if (testEntry.passed == true) totalMatchedAndPassed++;
               break;
            }
         } // EO iterate stdEntries
         if (testEntry.matched == false) {
            testNotInStdTotal++;
            String m = "        " + uniqueSequenceName + ": " + 
               testEntry.uniqueId + Utility.nl;
            testNotInStdDetail += m;
         }
         
      } // EO iterate testEntries
      
      // Record total test entries matched to std and passing all tests
      if (totalMatchedAndPassed > 0) {
         successCount++;
         successDetails.add(totalMatchedAndPassed + " test entries matched standard and passed all assertions.");
      }
      // Record detail for entries matched to std, didn't pass all tests
      if (totalMatched > totalMatchedAndPassed) {
         for (Entry e : testEntries) {
            if (e.matched == false || e.passed == true) continue;
            String l = uniqueSequenceName + " " + e.getString(uniqueSequenceTag) + " ";
            successCount += e.successCount;
            errorCount += e.errorCount;
            warningCount += e.warningCount;
            uncategorizedCount += e.uncategorizedCount;
            for (String s : e.successDetails)
               successDetails.add(l + s);
            for (String s : e.errorDetails)
               errorDetails.add(l + s);
            for (String s : e.warningDetails)
               warningDetails.add(l + s);
            for (String s : e.uncategorizedDetails)
               uncategorizedDetails.add(l + s);
         }
      }
      
      // Test entries missing unique id value
      if (testNoSequenceTotal > 0) {
         errorCount++ ;
         String m = testNoSequenceTotal + " " + desc + " entries missing " + 
            uniqueSequenceName + " values";
         errorDetails.add(m);
      }
      
      // Record test entries not matched to std
      if (testNotInStdTotal > 0) {
         errorCount++;
         String m = testNotInStdTotal + " " + desc + 
            " entries in the test object did not match entries in the standard:" +
            Utility.nl + testNotInStdDetail;
         errorDetails.add(m);
      } else {
         successCount++;
         successDetails.add("All test entries found in standard.");
      }
      
      // std entries missing unique id value
      if (stdNoSequenceTotal > 0) {
         errorCount++ ;
         String m = stdNoSequenceTotal + " " + desc + " standard entries missing " + 
            uniqueSequenceName + " values";
         errorDetails.add(m);
      }

      // Record std entries not matched to test
      for (Entry stdEntry : stdEntries) {
         if (stdEntry.missing == true) continue;
         if (stdEntry.matched == false) {
            stdNotInTestTotal++;
            String m = "        " + uniqueSequenceName + ": " + 
               stdEntry.uniqueId + Utility.nl;
            stdNotInTestDetail += m;
         }
      }
       if (stdNotInTestTotal > 0) {
          errorCount++;
          String m = stdNotInTestTotal + " " + desc + 
             " entries in the std object did not match entries in the test" +
             Utility.nl + stdNotInTestDetail;
          errorDetails.add(m);
       } else {
          successCount++;
          successDetails.add("All standard entries found in test.");
       }
   } // EO runDetail() method
   
   /*
    * Runs assertions for a matching Entry pair from the Sequences.
    */
   private void runDetail(Entry testEntry, Entry stdEntry) {
      dtl = testEntry;
      String testVal = null;
      String stdVal = null;
      Object testObj = null;
      try {
         for (DCMAssertion assertion : assertions) {
            switch (assertion.type) {
               case PRESENT:
                  testObj = testEntry.getValue(assertion.tag);
                  if (testObj != null) pass(assertion);
                  else fail(assertion);
                  break;
               case NOT_EMPTY:
                  testObj = testEntry.getValue(assertion.tag);
                  if (testObj != null && 
                      StringUtils.isNotBlank(testObj.toString())) pass(assertion);
                  else fail(assertion);
                  break;
               case ABSENT:
                  testObj = testEntry.getValue(assertion.tag);
                  if (testObj == null) pass(assertion);
                  else fail(assertion);
                  break;
               case CONSTANT:
                  testVal = testEntry.getString(assertion.tag, "").trim();
                  if (testVal.equals(assertion.value)) pass(assertion);
                  else fail(assertion);
                  break;
               case SAME:
                  testVal = testEntry.getString(assertion.tag);
                  stdVal = stdEntry.getString(assertion.tag);
                  // Empty strings are returned as null.
                  // Test for null case and replace with zero-length string
                  // to prevent the comparison from throwing a fault.
                  if (testVal == null) testVal = "";
                  if (stdVal  == null) stdVal = "";
                  if (testVal.equals(stdVal)) pass(assertion);
                  else fail(assertion);
                  break;
               case SAME_SIZE:
                  int testSize = testEntry.getSequence(assertion.tag).size();
                  int stdSize = stdEntry.getSequence(assertion.tag).size();
                  if (testSize == stdSize) pass(assertion);
                  else fail(assertion);
                  break;
               default:
                  throw new Exception("invalid test type");
            }
         }
      } catch (Exception e) {
         String em = "Evaluation error " +  Utility.getEM(e);
         Utility.getLog().warn(em);
         errorCount++;
         errorDetails.add(em);
      }
   }


   
   private void pass(DCMAssertion tst) {
      store(tst.passCat, tst.passDetail);
   }
   private void fail(DCMAssertion tst) {
      store(tst.failCat, tst.failDetail);
      dtl.passed = false;
   }
   private Entry dtl;
   private void store(CAT cat, String detail) {
      switch (cat) {
         case SUCCESS:
            dtl.successCount++;
            dtl.successDetails.add(detail);
            break;
         case WARNING:
            dtl.warningCount++;
            dtl.warningDetails.add(detail);
            break;
         case ERROR:
            dtl.errorCount++;
            dtl.errorDetails.add(detail);
            break;
         case UNCAT:
            dtl.uncategorizedCount++;
            dtl.uncategorizedDetails.add(detail);
            break;
         case SILENT:
         default:
      }
   }
   
   /*
    * Runs Sub Sequences for matching Entry Pair from this Sequence
    */
   private void runSubSeqs(Entry testEntry, Entry stdEntry) throws Exception {
      if (subSeqs == null || subSeqs.isEmpty()) return;
      for (Class <? extends DetailDcmSequenceContent> subSeq : subSeqs) {
         DetailDcmSequenceContent seq = subSeq.newInstance();
         seq.initializeTest();
         seq.initializeDetail(new Object[] {testEntry.attr, stdEntry.attr });
         seq.runDetail();
         testEntry.subSeqs.add(seq);
      }
   }
   
   private class Entry extends Detail{
      AttributesAndHash attr;
      /** True if Entry matched an entry in the other list. */
      boolean matched = false;
      /** true if (test) Entry passed all DCMAssertions. */
      boolean passed = true;
      /** unique id */
      String uniqueId = null;
      /** unique id missing */
      boolean missing = false;
      /** Any subsequences which were run for this test data */
      List<DetailDcmSequenceContent> subSeqs = new ArrayList<>();
      
      Entry(AttributesAndHash ent) {
         attr = ent;
      }

      public Object getValue(int a) {
         if (a == -1) return attr.hash;
         return attr.attributes.getValue(a);
      }
      public String getString(int a) {

         try {
            Object o = getValue(a);
            if (o instanceof String) return (String) o;
            if (o instanceof byte[]) {
               byte[] ba = (byte[]) o;
               String s = new String(ba);
               return s;
            }
            return (String) o;
         } catch (Exception e) {
            log.warn("getValue(" + a + ") - " + e.getMessage());
            return "error";
         }
      }
      public String getString(int a, String def) {
         String r = getString(a);
         if (r == null) r = def;
         return r;
      }
      public Sequence getSequence(int a) {
         return attr.attributes.getSequence(a);
      }
     
      @Override
      public void runDetail() throws Exception {
        throw new UnsupportedOperationException();
      }
   } // EO Entry class
    
   /**
    * Add new Sub Sequence class to process for this sequence
    * @param ss SubSeq class to add
    */
   public void addSubSeq(Class <? extends DetailDcmSequenceContent> ss) {
      subSeqs.add(ss);
   }
   
   @Override
   public void getResults(Results results) {
      super.getResults(results);
      for (Entry testEntry : testEntries) {
         for (DetailDcmSequenceContent seq : testEntry.subSeqs) {
            seq.getResults(results);
         }
      }
   }
  

}  // EO DetailDcmSequenceContent class
