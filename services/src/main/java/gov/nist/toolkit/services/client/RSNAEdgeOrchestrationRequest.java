/**
 * 
 */
package gov.nist.toolkit.services.client;

import gov.nist.toolkit.installation.shared.TestSession;

import java.io.Serializable;

/**
 * 
 * @author Matt Kelsey / MIR WUSTL IHE Development Project <a
 * href="mailto:kelseym@mir.wustl.edu">kelseym@mir.wustl.edu</a>
 *
 */
public class RSNAEdgeOrchestrationRequest extends AbstractOrchestrationRequest implements Serializable {
   private static final long serialVersionUID = 1L;

   TestSession testSession;
   String environmentName;

   public RSNAEdgeOrchestrationRequest() {}

   public TestSession getTestSession() {
       return testSession;
   }

   public void setTestSession(TestSession testSession) {
       this.testSession = testSession;
   }

   public String getEnvironmentName() {
       return environmentName;
   }

   public void setEnvironmentName(String environmentName) {
       this.environmentName = environmentName;
   }

}
