/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import org.dcm4che3.data.Keyword;

/**
 * Base class for dicom assertions
 */
public class DCMAssertion {
   
   TYPE type;
   int tag;
   String tagName;
   String value;
   CAT passCat = CAT.SUCCESS;
   CAT failCat = CAT.ERROR;
   String passDetail;
   String failDetail;
   
   /**
    * Create a new DCM Assertion 
    * @param type TYPE of assertion to make
    * @param tag dcm4che Tag of detail element to test. Special value of -1 for
    *            hash code of entire file.
    * @param value value to match (CONSTANT TYPE only).
    * @param cat CATs to use, first is pass (default SUCCESS), second is fail
    * (default ERROR)
    */
   public DCMAssertion(TYPE type, int tag, String value, CAT... cat) {
      this.type = type;
      this.tag = tag;
      if (tag == -1) this.tagName = "HASH";
      else this.tagName = Keyword.valueOf(tag);
      this.value = value;
      if (cat.length > 0 && cat[0] != null) this.passCat = cat[0];
      this.passDetail = gpd(type.passDetail);
      if (cat.length > 1 && cat[1] != null) this.failCat = cat[1];
      this.failDetail = gpd(type.failDetail);
   }
   
   /**
    * Constructor for case with null value
    * @param type TYPE of assertion
    * @param tag dcm4che Tag of detail element
    */
   public DCMAssertion(TYPE type, int tag) {
      this(type, tag, (String) null);
   }
   
   private String gpd(String dtl) {
      if (dtl.contains("%")) dtl = dtl.replace("%", tagName);
      if (dtl.contains("$")) dtl = dtl.replace("$", value);
      return dtl;
   }
   /**
    * @param dtl override assertion pass detail msg to this value.
    */
   public void setPassDetail(String dtl) {
      passDetail = dtl;
   }
   /**
    * @param dtl override assertion fail detail msg to this value.
    */
   public void setFailDetail(String dtl) {
      failDetail = dtl;
   }
   
   public void setTagName(String name) {
      tagName = name;
   }
   
   public enum TYPE { 
      
      PRESENT("% present", "% missing"), 
      NOT_EMPTY("% present and not empty", "% empty or missing"), 
      ABSENT("% not present","% found"), 
      CONSTANT("correct % value","incorrect % value"), 
      SAME("% values match","% values do not match"), 
      SAME_SIZE("% sequences same size","% sequences not same size"),
      DIFFERENT("% values differ as expected", "% values in the test object should not match values in the standard object");
      
      private TYPE(String pass, String fail) {
         passDetail = pass;
         failDetail = fail;
      }
      String passDetail;
      String failDetail;
      
   };
}
