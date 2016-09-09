package edu.wustl.mir.erl.ihe.xdsi.validation;

/**
 * Metadata content checking for SubmitObjectsRequest
 */
public class StepRAD68KOSMetadata extends Step {
	
   /**
    * <b>Parameters:</b><ol>
    * <li>String pfn of test XML file. Absolute path or relative to XDSI Root.</li>
    * <li>String pfn of std XML file used for comparison (The "gold standard" 
    * XML). Absolute path or relative to XDSI Root.</li></ol>
    */
	@Override
   public void initializeStep (Object[] args) throws Exception {
	   DetailXmlKOSMetadataContent d = new DetailXmlKOSMetadataContent();
	   d.initializeDetail((String) args[0], (String) args[1]);
	   details.add(d);
	}
}
