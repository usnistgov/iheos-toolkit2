/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import java.util.logging.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;

import edu.wustl.mir.erl.ihe.xdsi.util.PfnType;
import edu.wustl.mir.erl.ihe.xdsi.util.Utility;
import edu.wustl.mir.erl.ihe.xdsi.validation.DCMAssertion.TYPE;

/**
 * Base class for test comparing two sets of DICOM documents.
 */
public class TestRAD55Exception extends Test {
   
   private static Logger log = null;
   
	@Override
	public void initializeTest(String[] args) throws Exception {
		log = Utility.getLog();
		String testCmd = Utility.getArg(args, 0);
		if (testCmd.equalsIgnoreCase("RUNTEST")) {
			initializeRunTest(args);
		} else {
			throw new Exception("Unrecognized command: " + testCmd);
		}
	}
	
	private void initializeRunTest(String[] args) throws Exception {
		Path root = Paths.get(Utility.getXDSIRoot());

		// args[0] is the command that brought us to this method
		String responsePfn = args[1];
		String label     = (String)args[2];
//		String extension = (String)args[3];
		String testName  = (String)args[4];
//		String subLabel  = (String)args[5];
		
		for (int testIndex = 101; testIndex <= 106; testIndex++) {
			StepRad69Exception step;
			step = new StepRad69Exception();
			step.initializeStep(new Object[] {responsePfn + "/" + testIndex, "" + testIndex});
			addStep(step);
		}

//		StepDcmSetContent step = new StepDcmSetContent();
//		step.initializeStep(new Object[] { testDcmPfn, stdDcmPfn });
//		addStep(step);


		Path report = root.resolve("results" + Utility.fs 
				+ label + Utility.fs 
				+ testName + Utility.fs 
				+ "validation");
		Files.createDirectories(report);
		reportFolder = report.toString();
	}
	
	
	

   /**
    * Test harness.
    * <ol>
    * <li>First argument indicates method to test</li>
    * <ol>
    * <li>RUNTEST = initialize, run, get results</li>
    * </ol>
    * <li/>Remainder of arguments are passed to method in order.
    * <li/>args[1] String directory of folder with test output.
    * <li/>args[2] Label
    * <li/>args[3] Extension
    * <li/>args[4] Test Name
    * <li/>args[5] Sub Label
    * </ol>
    * Note: Directories may be entered as absolute, or relative to XDSI root.
    * 
    * @param args arguments
    */
   public static void main(String[] args) {
      log = Utility.getLog();
      TestRAD55Exception testRAD55Exception = new TestRAD55Exception();
      try {
    	  testRAD55Exception.initializeTest(args);
    	  testRAD55Exception.runTest();
    	  testRAD55Exception.reportResults("RAD 55 Exceptions");
    	  
      } catch (Exception e) {
    	  e.printStackTrace();
      }
      
   }
   

}
