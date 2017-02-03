/**
 * 
 */
package gov.nist.toolkit.services.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class RigOrchestrationRequest extends AbstractOrchestrationRequest {
   private static final long serialVersionUID = 1L;
   private boolean useExistingSimulator = true;
   
   private SiteSpec siteUnderTest; 

   public boolean isUseExistingSimulator() {
       return useExistingSimulator;
   }

   public void setUseExistingSimulator(boolean useExistingSimulator) {
       this.useExistingSimulator = useExistingSimulator;
   }


   public SiteSpec getSiteUnderTest() {
       return siteUnderTest;
   }

   public void setSiteUnderTest(SiteSpec siteUnderTest) {
       this.siteUnderTest = siteUnderTest;
   }
}
