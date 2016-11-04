package edu.wustl.mir.erl.ihe.xdsi.validation;

import gov.nist.toolkit.testengine.engine.SimulatorTransaction;

/**
 * Metadata content checking for SubmitObjectsRequest
 */
public class StepRAD68KOSMetadata extends Step {
   
   private SimulatorTransaction simTran;
	
   /**
    * @param trn
    */
   public StepRAD68KOSMetadata(SimulatorTransaction trn) {
      simTran = trn;
   }

   /**
    * <b>Parameters:</b><ol>
    * <li>String pfn of test XML file. Absolute path or relative to XDSI Root.</li>
    * <li>String pfn of std XML file used for comparison (The "gold standard" 
    * XML). Absolute path or relative to XDSI Root.</li></ol>
    */
	@Override
   public void initializeStep (Object[] args) throws Exception {
	   DetailXmlKOSMetadataContent d = new DetailXmlKOSMetadataContent(simTran);
	   d.initializeDetail((String) args[0], (String) args[1]);
	   details.add(d);
	}
}
