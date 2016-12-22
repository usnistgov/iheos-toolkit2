/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Collect test results
 */
@SuppressWarnings("javadoc")
public class Results {
   
   /**
    * The system newline character. Usually '\n'
    */
   public static final String nl = System.getProperty("line.separator");

   private String name = "";
   private Integer successCount = 0;
   private Integer warningCount = 0;
   private Integer errorCount = 0;
   private Integer uncategorizedCount = 0;
   
   private List<String> successDetails = new ArrayList<>();
   private List<String> warningDetails = new ArrayList<>();
   private List<String> errorDetails = new ArrayList<>();
   private List<String> uncategorizedDetails = new ArrayList<>();
   private List<String> testStepsAndDetails = new ArrayList<>();
   
   public List<String> getTestStepsAndDetails() {
	return testStepsAndDetails;
}
public void setTestStepsAndDetails(List<String> testStepsAndDetails) {
	this.testStepsAndDetails = testStepsAndDetails;
}
public Results() { }
   public Results(String name) {
      this.name = name;
   }
   /**
    * @return the {@link #successCount} value.
    */
   public Integer getSuccessCount() {
      return successCount;
   }
   /**
    * @return the {@link #warningCount} value.
    */
   public Integer getWarningCount() {
      return warningCount;
   }
   /**
    * @return the {@link #errorCount} value.
    */
   public Integer getErrorCount() {
      return errorCount;
   }
   /**
    * @return the {@link #uncategorizedCount} value.
    */
   public Integer getUncategorizedCount() {
      return uncategorizedCount;
   }
   /**
    * @return the {@link #successDetails} value.
    */
   public List <String> getSuccessDetails() {
      return successDetails;
   }
   /**
    * @return the {@link #warningDetails} value.
    */
   public List <String> getWarningDetails() {
      return warningDetails;
   }
   /**
    * @return the {@link #errorDetails} value.
    */
   public List <String> getErrorDetails() {
      return errorDetails;
   }
   /**
    * @return the {@link #uncategorizedDetails} value.
    */
   public List <String> getUncategorizedDetails() {
      return uncategorizedDetails;
   }
   
   public void collect(String desc, int success, int warning, int error, int uncat,
      List<String> successDtls, List<String> warningDtls, 
      List<String> errorDtls, List<String> uncatDtls) {
      successCount += success;
      warningCount += warning;
      errorCount += error;
      uncategorizedCount += uncat;
      roll(successDetails, desc, successDtls);
      roll(warningDetails, desc, warningDtls);
      roll(errorDetails, desc, errorDtls);
      roll(uncategorizedDetails, desc, uncatDtls);
   }
   
   public void addStepTitle(String title) {
	   successDetails.add("Step: " + title);
	   warningDetails.add("Step: " + title);
	   errorDetails.add("Step: " + title);
	   uncategorizedDetails.add("Step: " + title);
   }
   
   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Results for ").append(name).append(" test:").append(nl);
      sb.append("success count = ").append(successCount).append(nl);
      sb.append("warning count = ").append(warningCount).append(nl);
      sb.append("error   count = ").append(errorCount).append(nl);
      sb.append("uncateg count = ").append(uncategorizedCount).append(nl);
      if (successDetails.size() > 0)
         sb.append("------- -----------------------------------------------").append(nl);
      for(String l : successDetails)       sb.append("success ").append(l).append(nl);
      if (warningDetails.size() > 0)
         sb.append("------- -----------------------------------------------").append(nl);
      for(String l : warningDetails)       sb.append("warning ").append(l).append(nl);
      if (errorDetails.size() > 0)
         sb.append("------- -----------------------------------------------").append(nl);
      for(String l : errorDetails)         sb.append("error   ").append(l).append(nl);
      if (uncategorizedDetails.size() > 0)
         sb.append("------- -----------------------------------------------").append(nl);
      for(String l : uncategorizedDetails) sb.append("uncateg ").append(l).append(nl);
      return sb.toString();
   }
   
   protected void roll(List<String> up, String prefix, List<String> dns) {
      for (String dn : dns) up.add(prefix + ": " + dn);
   }
}
