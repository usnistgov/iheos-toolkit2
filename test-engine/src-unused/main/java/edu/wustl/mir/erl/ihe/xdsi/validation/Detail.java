package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;

import edu.wustl.mir.erl.ihe.xdsi.util.Utility;


/**
 * Base class for low level validation of a single message, object, file or
 * similar tangible item. This low level validation consists of one or more
 * specific tests/comparisons applied to that single item. The validation will
 * yield results categorized as:
 * <ul>
 * <li>success - A test assertion was met</li>
 * <li>error - A test assertion was not met</li>
 * <li>warning - A situation was noted and may represent a problem, but is not
 * categorized as an error.</li>
 * <li>uncategorized - Notation not categorized as any of the above</li>
 * </ul>
 * The methods return the number of results in each category as well as
 * ArrayLists of strings with human readable details. These should eventually be
 * tied to testable assertions. That is future work.
 */
@SuppressWarnings("javadoc")
public abstract class Detail {
   
   protected static Logger log = Utility.getLog();
   protected String desc;
	protected int successCount = 0;
	protected int errorCount = 0;
	protected int warningCount = 0;
	protected int uncategorizedCount = 0;
	protected List<String> successDetails = new ArrayList<String>();
	protected List<String> errorDetails = new ArrayList<String>();
	protected List<String> warningDetails = new ArrayList<String>();
   protected List<String> uncategorizedDetails = new ArrayList<String>();
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

	public Detail() {
	}
	
	public abstract void runDetail() throws Exception ;
	
	public void getResults(Results results) {
	   results.collect(desc, successCount, warningCount, errorCount, 
	      uncategorizedCount, successDetails, warningDetails, errorDetails, 
	      uncategorizedDetails);
	}

   protected void roll(List<String> up, String prefix, List<String> dns) {
      for (String dn : dns) up.add("Detail (" + prefix + "): " + dn);
   }
}
