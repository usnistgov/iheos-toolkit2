/**
 * 
 */
package gov.nist.toolkit.simulators.sim.ids;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

/**
 *
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class WadoRetrieveResponseSim extends TransactionSimulator {

   /**
    * @param common
    * @param simulatorConfig
    */
   public WadoRetrieveResponseSim(SimCommon common, SimulatorConfig simulatorConfig) {
      super(common, simulatorConfig);
      // TODO Auto-generated constructor stub
   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.valsupport.message.AbstractMessageValidator#run(gov.nist.toolkit.errorrecording.ErrorRecorder, gov.nist.toolkit.valsupport.engine.MessageValidatorEngine)
    */
   @Override
   public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
      // TODO Auto-generated method stub

   }

}
