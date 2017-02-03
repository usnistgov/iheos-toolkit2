/**
 * 
 */
package gov.nist.toolkit.services.client;

import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class IigOrchestrationRequest extends AbstractOrchestrationRequest {
   private static final long serialVersionUID = 1L;
   private boolean useExistingSimulator = true;
   
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