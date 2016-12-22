package edu.wustl.mir.erl.ihe.xdsi.validation;

import gov.nist.toolkit.testengine.engine.SimulatorTransaction;

@SuppressWarnings("javadoc")
public class StepRAD68KOSDocument extends Step {
	
  
   /**
    * <b>Parameters:</b>
    * <ol>
    * <li>String pfn of test dicom object file. Absolute path or relative to
    * XDSI Root.</li>
    * <li>String pfn of std dicom object file used for comparison (The "gold
    * standard" object). Absolute path or relative to XDSI Root.</li>
    * </ol>
    */
	@Override
   public void initializeStep (Object[] args) throws Exception {
	   
//	   DetailDciodvfy kosval = new DetailDciodvfy();
//      kosval.initializeDetail((String) args[0], "KOS Document");
//      details.add(kosval);
            
		DetailDcmKOSContent detailKOSContent = new DetailDcmKOSContent();
		detailKOSContent.initializeDetail((String) args[0], (String) args[1]);
		details.add(detailKOSContent);
		
		DetailDcmKOSCurrentRequestedProcedureEvidenceSequence seq =
		   new DetailDcmKOSCurrentRequestedProcedureEvidenceSequence();
		seq.initializeTest();
		seq.initializeDetail(new Object[] {detailKOSContent});
		details.add(seq);
		
		DetailDcmKOSCrossWalk crossWalk = new DetailDcmKOSCrossWalk();
		crossWalk.initializeDetail((String)args[0], "KOS Document Cross Walk");
		details.add(crossWalk);
	}
}
