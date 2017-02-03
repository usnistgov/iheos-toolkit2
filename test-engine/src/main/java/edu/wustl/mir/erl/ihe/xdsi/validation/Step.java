package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.util.ArrayList;

/** 
 * Base class for Validation Steps. 
 * A collection of Validation Detail objects.
 */
@SuppressWarnings("javadoc")
public abstract class Step {
	protected String title = "";
	protected String subtitle = "";
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	protected ArrayList<Detail> details = new ArrayList<Detail>();
	
	public abstract void initializeStep(Object[] args) throws Exception;
   
   public void runStep() throws Exception {
      runDetails();
   }
   
   public void runDetails() throws Exception {
      for (Detail detail : details) {
    	  detail.runDetail();
      }
   }
	
	public void getResults(Results results) {
	   for(Detail detail : details) {
		   results.addStepTitle(title + "(" + subtitle + ")");
		   detail.getResults(results);
	   }
	}
}
