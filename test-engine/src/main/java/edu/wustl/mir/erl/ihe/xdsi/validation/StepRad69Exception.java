package edu.wustl.mir.erl.ihe.xdsi.validation;

@SuppressWarnings("javadoc")
public class StepRad69Exception extends Step {
	
   /**
    * <b>Parameters:</b>
    * <ol>
    * <li>String pfn of folder with retrieve response. That folder will contain subfolders for response and attachments.</li>
    * </ol>
    */
	@Override
   public void initializeStep (Object[] args) throws Exception {
		String arg1 = (String)args[0];
		String arg2 = (String)args[1];
		setTitle("Rad-69 Exception Case");
		setSubtitle(arg2);
		
		DetailReporter reporter;
		DetailRad69ExceptionNoAttachments noAttachments;
		
		reporter = new DetailReporter();
		reporter.initializeDetail("Folder with responses: " + arg1);
		details.add(reporter);
		noAttachments = new DetailRad69ExceptionNoAttachments();
		noAttachments.initializeDetail(arg1, arg2);
		details.add(noAttachments);
	}
}
