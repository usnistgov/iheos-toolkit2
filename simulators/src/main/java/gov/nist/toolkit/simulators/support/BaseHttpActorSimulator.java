/**
 * 
 */
package gov.nist.toolkit.simulators.support;

import java.io.IOException;

import gov.nist.toolkit.actorfactory.BaseActorSimulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

/**
 * Base class for all Http (only) server simulators
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public abstract class BaseHttpActorSimulator extends BaseActorSimulator {
   
   protected SimCommon simCommon;
   protected ErrorRecorder er = null;
   
   public void init(SimCommon common) {
      simCommon = common;
      er = simCommon.getCommonErrorRecorder();
   }

   /**
    * @param transactionType
    * @param mvc
    * @throws IOException 
    */
   abstract public boolean run(TransactionType transactionType, MessageValidatorEngine mvc) throws IOException;

   /**
    * @param asc
    */
   public void init(SimulatorConfig asc) {
   }
}
