/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.NotImplementedException;

import gov.nist.toolkit.testengine.engine.SimulatorTransaction;

/**
 * Registry Stored Query
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class TestITI18 extends Test {

   
   /**
    * @param cmd command to execute. Same as Assert Element process attribute 
    * value.
    * @param trn test transaction
    * @throws Exception on error
    */
   public void initializeTest(String cmd, SimulatorTransaction test) throws Exception {
      switch (cmd) {
         case "sameQuery":
            initializeSameQuery(test);
            break;
         default:
            throw new Exception("Don't understand test name " + cmd);
      }
   }
   
   private void initializeSameQuery(SimulatorTransaction test) throws Exception {
      StepITI18SameQuery s = new StepITI18SameQuery();
      s.initializeStep(new Object[] {test});
      addStep(s);
   }
   
   
   @Override
   public void initializeTest(String[] args) throws Exception {
      throw new NotImplementedException();

   }

}
