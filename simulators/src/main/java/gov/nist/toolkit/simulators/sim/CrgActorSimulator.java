/**
 * 
 */
package gov.nist.toolkit.simulators.sim;

import java.io.IOException;

import org.apache.log4j.Logger;

import gov.nist.toolkit.simcommon.shared.config.SimulatorConfig;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simulators.sim.rg.RGActorSimulator;
import gov.nist.toolkit.simulators.sim.rig.RigActorSimulator;
import gov.nist.toolkit.simulators.support.BaseDsActorSimulator;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

/**
 * Combination Responding Gateway Simulator
 * (Responding Gateway and Responding Imaging Gateway
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class CrgActorSimulator extends BaseDsActorSimulator {
   
   static final Logger logger = Logger.getLogger(CrgActorSimulator.class);
   
   RGActorSimulator rg;
   RigActorSimulator rig;
   
   public CrgActorSimulator() {
      rg = new RGActorSimulator();
      rig = new RigActorSimulator();      
   }

   @Override
   public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation)
      throws IOException {
      if (rig.supports(transactionType)) return rig.run(transactionType, mvc, validation);
      return rg.run(transactionType, mvc, validation);
   }

   @Override
   public void init() {
      rg.init();
      rig.init();
   }
   
   @Override
   public void init(DsSimCommon c, SimulatorConfig config) {
      rg.init(c, config);
      rig.init(c, config);
  }

   @Override
   public void onCreate(SimulatorConfig config) {
       rg.onCreate(config);
       rig.onCreate(config);
   }

   @Override
   public void onDelete(SimulatorConfig config) {
       rg.onDelete(config);
       rig.onDelete(config);
   }

   @Override
   public void onServiceStart(SimulatorConfig config) {
       rg.onServiceStart(config);
       rig.onServiceStart(config);
   }

   @Override
   public void onServiceStop(SimulatorConfig config) {
       rg.onServiceStop(config);
       rig.onServiceStop(config);
   }

}
