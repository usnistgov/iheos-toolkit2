/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import org.apache.commons.lang.NotImplementedException;
import gov.nist.toolkit.testengine.engine.SimulatorTransaction;

/**
 *
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class TestRAD68 extends Test {
   
   /**
    * @param cmd command to execute. Same as Assert Element process attribute 
    * value.
    * @param trn test transaction
    * @throws Exception on error
    */
   public void initializeTest(String cmd, SimulatorTransaction trn) throws Exception {
      switch (cmd) {
         case "sameKOSDcm":
            initializeKOSDcm(trn);
            break;
         case "sameKOSMetadata":
            initializeKOSMetadata(trn);
            break;
         default:
            throw new Exception("Don't understand test name " + cmd);
      }
   }
   
   private void initializeKOSDcm(SimulatorTransaction trn) throws Exception {

      StepRAD68KOSDocument stepRAD68KOSDocument = new StepRAD68KOSDocument();
      stepRAD68KOSDocument.initializeStep(new Object[] { trn.getPfns().get(0),
         trn.getStdPfn() });
      addStep(stepRAD68KOSDocument);
   }
   
   private void initializeKOSMetadata(SimulatorTransaction trn) throws Exception {

      StepRAD68KOSMetadata stepRAD68KOSMetadata = new StepRAD68KOSMetadata();
      stepRAD68KOSMetadata.initializeStep(new Object[] { trn.getMetadata(),
            trn.getStdPfn() });
      addStep(stepRAD68KOSMetadata);
   }

   @Override
   public void initializeTest(String[] args) throws Exception {
      throw new NotImplementedException();
      
   }
   
   
   
}
