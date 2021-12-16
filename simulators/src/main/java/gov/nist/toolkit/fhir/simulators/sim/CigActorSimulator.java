/**
 * 
 */
package gov.nist.toolkit.fhir.simulators.sim;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.fhir.simulators.sim.ig.IgActorSimulator;
import gov.nist.toolkit.fhir.simulators.sim.iig.IigActorSimulator;
import gov.nist.toolkit.fhir.simulators.support.BaseDsActorSimulator;
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import java.util.logging.Logger;

import java.io.IOException;

/**
 * Combination Initiating Gateway Simulator
 * (Initiating Gateway and Initiating Imaging Gateway
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class CigActorSimulator extends BaseDsActorSimulator {
   
   static final Logger logger = Logger.getLogger(CigActorSimulator.class.getName());
   
   IgActorSimulator ig;
   IigActorSimulator iig;
   
   public CigActorSimulator() {
      ig = new IgActorSimulator();
      iig = new IigActorSimulator();      
   }

   @Override
   public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation)
      throws IOException {
      if (iig.supports(transactionType)) return iig.run(transactionType, mvc, validation);
      return ig.run(transactionType, mvc, validation);
   }

   @Override
   public void init() {
      ig.init();
      iig.init();
   }
   
   @Override
   public void init(DsSimCommon c, SimulatorConfig config) {
      ig.init(c, config);
      iig.init(c, config);
  }

   @Override
   public void onCreate(SimulatorConfig config) {
       ig.onCreate(config);
       iig.onCreate(config);
   }

   @Override
   public void onDelete(SimulatorConfig config) {
       ig.onDelete(config);
       iig.onDelete(config);
   }

   @Override
   public void onServiceStart(SimulatorConfig config) {
       ig.onServiceStart(config);
       iig.onServiceStart(config);
   }

   @Override
   public void onServiceStop(SimulatorConfig config) {
       ig.onServiceStop(config);
       iig.onServiceStop(config);
   }

}
