/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.Logger;

import edu.wustl.mir.erl.ihe.xdsi.util.PfnType;
import edu.wustl.mir.erl.ihe.xdsi.util.Utility;

/**
 * Base class designed to validate transactions which include DICOM image files 
 * and SOAP message components to be matched with test standards.
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class TestDcmSoap extends Test {
   
   private static Logger log = Utility.getLog();
   private static Path xdsiRoot = Utility.getXDSIRootPath();
   
   protected String nameOfTest;
   protected Path testDirPath;
   protected Path testDcmDirPath;
   protected Path testRespDirPath;
   protected Path reportDirPath;
   protected Path stdDirPath;
   protected Path stdDcmDirPath;
   protected Path stdRespDirPath;

   /**
    * Initialize validation of transaction. Parameters:
    * <ol start=0> 
    * <li/>Short, user readable name of test
    * <li/>The transaction id of the test.
    * <li/>String path to directory containing results of test transaction. This 
    * directory will contain:<ol> 
    * <li/> An "attachments" subdirectory containing the DICOM files to be 
    * matched against the standard. Each file will be named using the SOP 
    * Instance UID as the file name, with the extension ".dcm".
    * <li/>A "response" subdirectory containing the SOAP component xml files to
    * be matched. The SOAP body components name's will depended on the 
    * transaction being tested. The header component names will be:
    * <ul>
    * <li/>RequestHeader.xml - The SOAP request header.
    * <li/>ResponseHeader.xml - The SOAP response header.</ul>
    * For brevity, the XOP contents of SOAP response bodies will be removed 
    * and replaced with the string "...".
    * <li/>In addition, a "reports" subdirectory will contain the results of
    * test validation. This directory will be created if it does not exists.
    * </ol>
    * <li/>String path to directory containing standard files for comparison.
    * The structure and contents of this directory will mimic that of the
    * results directory, though files not needed for validation may be omitted,
    * and additional files may be present. There is no "reports" subdirectory in
    * this directory.
    * </ol>
    * <b>NOTE: This method validates the directory structure and initializes the
    * test step for the DICOM files comparison. Initialization of SOAP step(s)
    * should be handled in the subclass for the specific test, which should 
    * invoke this method using: <pre>
    *    {@code super.initializeTest()}</pre>
    * at its beginning.
    * </b><p/>
    * All directory paths may be absolute, or relative to xdsi root.
    * <p/>
    * 
    * @see Test#initializeTest(java.lang.String[])
    */
   @Override
   public void initializeTest(String[] args) throws Exception {
      Utility.invoked(log);
      
      // Validate number of arguments used.
      int numberOfArguments = 4;
      if (args.length < numberOfArguments) 
         throw new Exception(Utility.classMethod() + 
            " error: less than " + numberOfArguments + " arguments passed");
      if (args.length > 4) 
         log.warn(Utility.classMethod() + " called with " + args.length + 
            " parameters. Only " + numberOfArguments + " needed.");
      nameOfTest = args[0];
      
      // Validate required directories and permissions
      testDirPath = xdsiRoot.resolve(args[2]);
      Utility.isValidPfn(nameOfTest + " test directory", testDirPath, PfnType.DIRECTORY, "rwx");
      
      testRespDirPath = testDirPath.resolve("response");
      Utility.isValidPfn(nameOfTest + " test response files directory", testRespDirPath, PfnType.DIRECTORY, "r");
      
      // reports subdirectory. Create if needed.
      reportDirPath = testDirPath.resolve("reports");
      Files.createDirectories(reportDirPath);
      reportFolder = reportDirPath.toString();
      
      // Validate required directories and permissions
      stdDirPath = xdsiRoot.resolve(args[3]);
      Utility.isValidPfn(nameOfTest + " std directory", stdDirPath, PfnType.DIRECTORY, "rwx");
      
      stdRespDirPath = stdDirPath.resolve("response");
      Utility.isValidPfn(nameOfTest + " std response files directory", stdRespDirPath, PfnType.DIRECTORY, "r");
      
      // If there is no std attachments directory, there is no DCM step
      try {         
         stdDcmDirPath = stdDirPath.resolve("attachments");
         Utility.isValidPfn(nameOfTest + " std dcm files directory", stdDcmDirPath, PfnType.DIRECTORY, "r");
      } catch (Exception e) {
         log.debug("No std attachments, so no dcm match step.");
         return;
      }
      testDcmDirPath = testDirPath.resolve("attachments");
      Utility.isValidPfn(nameOfTest + " test dcm files directory", testDcmDirPath, PfnType.DIRECTORY, "r");
      
      // Match returned DICOM files with standard
      StepDcmSetContent dcmStep = new StepDcmSetContent();
      dcmStep.initializeStep(new Object[] { testDcmDirPath.toString(), stdDcmDirPath.toString() });
      dcmStep.setSubtitle(nameOfTest);
      addStep(dcmStep);
      
   } // EO initializeTest method
   
   /**
    * Writes report files to reports directory
    * @throws Exception on error
    */
   public void reportResults() throws Exception {
      reportResults(nameOfTest);
   }

}
