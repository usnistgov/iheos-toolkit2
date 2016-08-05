/**
 * 
 */
package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

/**
 * Handles XmlDetail validations
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class XmlDetailTransaction extends BasicTransaction {

   /**
    * @param s_ctx
    * @param instruction
    * @param instruction_output
    */
   public XmlDetailTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
      super(s_ctx, instruction, instruction_output);
      // TODO Auto-generated constructor stub
   }

   
   @Override
   protected void run(OMElement stdResponse) throws Exception {
      OMElement testResponse = linkage.findResultInLog("retrieve", "").getFirstElement();
      
      assign_patient_id = false;
      /* TODO Working here
       * At this point stdResponse and testResponse have the data we want to
       * validate. Need to incorporate the xml validation stuff from xdsi and
       * figure out how to put the results into the output log.
       */
   }

   
   @Override
   protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
               parseBasicInstruction(part);
   }

   
   @Override
   protected String getRequestAction() {
      // TODO Auto-generated method stub
      return null;
   }

   
   @Override
   protected String getBasicTransactionName() {
      return "XmlDetail";
   }

}
