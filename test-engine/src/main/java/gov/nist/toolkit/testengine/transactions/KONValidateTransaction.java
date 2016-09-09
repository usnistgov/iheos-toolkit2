/**
 * 
 */
package gov.nist.toolkit.testengine.transactions;

import org.apache.axiom.om.OMElement;

import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

/**
 *
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class KONValidateTransaction extends BasicTransaction {

   private OMElement step;
   
   /**
    * @param s_ctx
    * @param step
    * @param instruction
    * @param instruction_output
    */
   public KONValidateTransaction(StepContext s_ctx, OMElement step, OMElement instruction, OMElement instruction_output) {
      super(s_ctx, instruction, instruction_output);
      this.step = step;
   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.testengine.transactions.BasicTransaction#run(org.apache.axiom.om.OMElement)
    */
   @Override
   protected void run(OMElement request) throws Exception {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.testengine.transactions.BasicTransaction#parseInstruction(org.apache.axiom.om.OMElement)
    */
   @Override
   protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.testengine.transactions.BasicTransaction#getRequestAction()
    */
   @Override
   protected String getRequestAction() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.testengine.transactions.BasicTransaction#getBasicTransactionName()
    */
   @Override
   protected String getBasicTransactionName() {
      // TODO Auto-generated method stub
      return null;
   }

}
