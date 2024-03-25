/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import gov.nist.toolkit.testengine.engine.SimulatorTransaction;

/**
 * Validate Registry Stored Query Request
 */
public class StepITI18SameQuery extends Step {

   /* (non-Javadoc)
    * @see edu.wustl.mir.erl.ihe.xdsi.validation.Step#initializeStep(java.lang.Object[])
    */
   @Override
   public void initializeStep(Object[] args) throws Exception {
      SimulatorTransaction tran = (SimulatorTransaction) args[0];
      DetailXmlITI18RequestContent det = new DetailXmlITI18RequestContent();
      det.initializeDetail(tran.getRequestBody(), tran.getStdPfn());
      details.add(det);
   }

}
