/**
 * 
 */
package gov.nist.toolkit.services.client;

import java.io.Serializable;

/**
 *
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class RigOrchestrationRequest implements Serializable {
   private static final long serialVersionUID = 1L;
   
   String userName;
   String environmentName;
   boolean includeLinkedRIG;

   public RigOrchestrationRequest() {}

   public String getUserName() {
       return userName;
   }

   public void setUserName(String userName) {
       this.userName = userName;
   }

   public String getEnvironmentName() {
       return environmentName;
   }

   public void setEnvironmentName(String environmentName) {
       this.environmentName = environmentName;
   }

   public boolean isIncludeLinkedRIG() {
       return includeLinkedRIG;
   }

   public void setIncludeLinkedRIG(boolean includeLinkedRIG) {
       this.includeLinkedRIG = includeLinkedRIG;
   }

}
