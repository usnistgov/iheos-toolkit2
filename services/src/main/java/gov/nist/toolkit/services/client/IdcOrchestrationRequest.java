/**
 * 
 */
package gov.nist.toolkit.services.client;

import gov.nist.toolkit.sitemanagement.client.*;

/**
 * Orchestration request for Image Document Consumer SUT
 */
public class IdcOrchestrationRequest extends AbstractOrchestrationRequest  {
   private boolean useExistingSimulator = true;

   SiteSpec siteUnderTest;

   public IdcOrchestrationRequest() {
   }

   public boolean isUseExistingSimulator() {
       return useExistingSimulator;
   }

   public void setUseExistingSimulator(boolean useExistingSimulator) {
       this.useExistingSimulator = useExistingSimulator;
   }

   /**
    * @return the {@link #siteUnderTest} value.
    */
   public SiteSpec getSiteUnderTest() {
      return siteUnderTest;
   }

   /**
    * @param siteUnderTest the {@link #siteUnderTest} to set
    */
   public void setSiteUnderTest(SiteSpec siteUnderTest) {
      this.siteUnderTest = siteUnderTest;
   }


}
