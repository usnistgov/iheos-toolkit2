package edu.wustl.mir.erl.ihe.xdsi.validation;

@SuppressWarnings("javadoc")
public class StepDcmSetContent extends Step {
	
   /**
    * <b>Parameters:</b>
    * <ol start=0>
    * <li>String pfn of test dicom object file. Absolute path or relative to
    * XDSI Root.</li>
    * <li>String pfn of std dicom object file used for comparison (The "gold
    * standard" object). Absolute path or relative to XDSI Root.</li>
    * </ol>
    */
	@Override
   public void initializeStep (Object[] args) throws Exception {
		String arg1 = (String)args[0];
		String arg2 = (String)args[1];
		setTitle("Compare a Set of DICOM Files to a Master Set");
		
		DetailReporter reporter = new DetailReporter();
		reporter.initializeDetail(
				"Folder with test objects:     " + arg1,
				"Folder with standard objects: " + arg2);
		details.add(reporter);
		
		DetailDcmSetContent detail = new DetailDcmSetContent();
		detail.initializeDetail(args);
		details.add(detail);
	   
/*		DetailDciodvfy kosval = new DetailDciodvfy();
		kosval.initializeDetail((String) args[0], "KOS Document");
		details.add(kosval);

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
		details.add(crossWalk);*/
	}
}
