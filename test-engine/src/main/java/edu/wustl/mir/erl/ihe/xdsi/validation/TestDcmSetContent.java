/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.Logger;

/**
 * Base class for test comparing two sets of DICOM documents.
 */
public class TestDcmSetContent extends Test {
   
   private static Logger log = null;
   
   /**
    * @param testPfns {@code List<String>} of absolute pfns for test image files.
    * @param stdPfns {@code List<String>} of absolute pfns for std image files.
    * @param assertions  {@code List<DCMAssertion>} of assertions to be
    * applied to the images
    * @throws Exception on error, for example, IO error.
    */
   public void initializeTest(List<String> testPfns, List<String> stdPfns, 
      List<DCMAssertion> assertions) throws Exception {
      
      StepDcmSetContent step = new StepDcmSetContent();
      step.initializeStep(new Object[] {testPfns, stdPfns, assertions});
      steps.add(step);
   }

   /* (non-Javadoc)
    * @see edu.wustl.mir.erl.ihe.xdsi.validation.Test#initializeTest(java.lang.String[])
    */
   @Override
   public void initializeTest(String[] args) throws Exception {
     throw new NotImplementedException("Not used.");
   }
   
//	@Override
//	   public void initializeTest(String[] args) throws Exception {
//			log = Utility.getLog();
//			String testCmd = Utility.getArg(args, 0);
//			if (testCmd.equalsIgnoreCase("RUNTEST")) {
//				initializeRunTest(args);
//			} else if (testCmd.equalsIgnoreCase("RUNMULTILEVEL")) {
//				initializeRunMultiLevel(args);
//			} else if (testCmd.equalsIgnoreCase("RUNSINGLELEVEL")) {
//				initializeRunSingleLevel(args);
//			} else {
//				throw new Exception("Unrecognized command: " + testCmd);
//			}
//	}
//	
//	private void initializeRunTest(String[] args) throws Exception {
//		Path root = Paths.get(Utility.getXDSIRoot());
//		
//		// args[0] is the command that brought us to this method
//		String testDcmPfn = args[1];
//		String stdDcmPfn  = args[2];
//		String label     = args[3];
////		String extension = (String)args[4];
//		String testName  = args[5];
//		String subLabel  = args[6];
//		
//		StepDcmSetContent step = new StepDcmSetContent();
//		step.initializeStep(new Object[] { testDcmPfn, stdDcmPfn });
//		addStep(step);
//		
//
//		Path report = root.resolve("results" + Utility.fs 
//				+ label + Utility.fs 
//				+ testName + Utility.fs 
//				+ "validation" + Utility.fs
//				+ subLabel);
//		Files.createDirectories(report);
//		reportFolder = report.toString();
//	}
//	
//	
//	private void initializeRunSingleLevel(String[] args) throws Exception {
//		Path root = Paths.get(Utility.getXDSIRoot());
//		
//		// args[0] is the command that brought us to this method
//		String testDcmPfn = args[1];
//		String stdDcmPfn  = args[2];
//		String label     = args[3];
////		String extension = (String)args[4];
//		String testName  = args[5];
////		String subLabel  = (String)args[6];
//		
//		StepDcmSetContent step = new StepDcmSetContent();
//		step.initializeStep(new Object[] { testDcmPfn, stdDcmPfn });
//		addStep(step);
//		
//
//		Path report = root.resolve("results" + Utility.fs 
//				+ label + Utility.fs 
//				+ testName + Utility.fs 
//				+ "validation");
//		Files.createDirectories(report);
//		reportFolder = report.toString();
//	}
//	
//	
//	private void initializeRunMultiLevel(String[] args) throws Exception {
//		Path root = Paths.get(Utility.getXDSIRoot());
//		
//		// args[0] is the command that brought us to this method
//		String testDcmPfn = args[1];
//		String stdDcmPfn  = args[2];
//		String label     = args[3];
////		String extension = (String)args[4];
//		String testName  = args[5];
//		String compositeLevels  = args[6];
//		
//		String[] levels = compositeLevels.split(":");
//		
//		for (int i = 0; i < levels.length; i++) {
//
//			StepDcmSetContent step = new StepDcmSetContent();
//			step.initializeStep(new Object[] { testDcmPfn + Utility.fs + levels[i], stdDcmPfn });
//			step.setSubtitle(levels[i]);
//			addStep(step);
//		}
//		
//
//		Path report = root.resolve("results" + Utility.fs 
//				+ label + Utility.fs 
//				+ testName + Utility.fs 
//				+ "validation");
//		Files.createDirectories(report);
//		reportFolder = report.toString();
//	}
//
//
//   /**
//    * Test harness.
//    * <ol>
//    * <li>First argument indicates method to test</li>
//    * <ol>
//    * <li>RUNTEST = initialize, run, get results</li>
//    * </ol>
//    * <li/>Remainder of arguments are passed to method in order.
//    * <li/>args[1] String directory containing test dicom objects.
//    * <li/>args[2] String directory containing std dicom objects.
//    * </ol>
//    * Note: Directories may be entered as absolute, or relative to XDSI root.
//    * 
//    * @param args arguments
//    */
////   public static void main(String[] args) {
////      log = Utility.getLog();
////      TestDcmSetContent testDcmSetContent = new TestDcmSetContent();
////      try {
////    	  testDcmSetContent.initializeTest(args);
////    	  testDcmSetContent.runTest();
////    	  testDcmSetContent.reportResults("DICOM SET");
////    	  
////      } catch (Exception e) {
////    	  e.printStackTrace();
////      }
////      
////   }
//   

}
