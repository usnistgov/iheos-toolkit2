package gov.nist.toolkit.services.client;

import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 *  Orchestration request for Image Document Source SUT
 */
public class IdsOrchestrationRequest extends AbstractOrchestrationRequest {
   private boolean useExistingSimulator = true;
   private static final long serialVersionUID = 1L;

   SiteSpec siteUnderTest;

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
