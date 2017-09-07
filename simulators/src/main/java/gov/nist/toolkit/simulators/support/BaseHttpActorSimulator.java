/**
 * 
 */
package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.BaseActorSimulator;
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

import java.io.IOException;

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
