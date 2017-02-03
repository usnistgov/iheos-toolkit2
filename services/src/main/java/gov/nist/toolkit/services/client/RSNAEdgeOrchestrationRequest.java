/**
 * 
 */
package gov.nist.toolkit.services.client;

import java.io.Serializable;

/**
 * 
 * @author Matt Kelsey / MIR WUSTL IHE Development Project <a
 * href="mailto:kelseym@mir.wustl.edu">kelseym@mir.wustl.edu</a>
 *
 */
public class RSNAEdgeOrchestrationRequest implements Serializable {
   private static final long serialVersionUID = 1L;

   String userName;
   String environmentName;

   public RSNAEdgeOrchestrationRequest() {}

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

}
