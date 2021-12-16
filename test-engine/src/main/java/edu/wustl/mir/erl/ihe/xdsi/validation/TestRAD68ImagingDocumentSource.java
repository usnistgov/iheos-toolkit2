package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import java.util.logging.Logger;

import gov.nist.toolkit.testengine.engine.SimulatorTransaction;

import edu.wustl.mir.erl.ihe.xdsi.util.PfnType;
import edu.wustl.mir.erl.ihe.xdsi.util.PrsSimLogs;
import edu.wustl.mir.erl.ihe.xdsi.util.Utility;
import edu.wustl.mir.erl.ihe.xdsi.util.DICOMUtility;
import edu.wustl.mir.erl.ihe.xdsi.util.KOSBean;

@SuppressWarnings("javadoc")
public class TestRAD68ImagingDocumentSource extends Test {
   
   private static Logger log = null;
   private static Path root = Paths.get(Utility.getXDSIRoot());
   private String testCmd = null;
   SimulatorTransaction trn = null;

   /**
    * <b>Parameters:</b>
    * <ol>
    * <li>String pfn of test KOS Document file. Absolute path or relative to
    * XDSI Root.</li>
    * <li>String pfn of std KOS Document file used for comparison (The
    * "gold standard" object). Absolute path or relative to XDSI Root.</li>
    * <li>String pfn of test KOS Metadata XML file. Absolute path or relative to
    * XDSI Root.</li>
    * <li>String pfn of std KOS MetadataXML file used for comparison (The
    * "gold standard" XML). Absolute path or relative to XDSI Root.</li>
    * </ol>
    */
	@Override
   public void initializeTest(String[] args) throws Exception {
		log = Utility.getLog();
		testCmd = getArg(args, 0);

        log.info("TestRAD68ImagingDocumentSource Running " + testCmd);
		if (testCmd.equalsIgnoreCase("RUNTEST")) {
			this.initializeMethodTest(args);
		} else if (testCmd.equalsIgnoreCase("TESTLOGS")) {
			this.initializeMethodLogs(args);
		} else if (testCmd.equalsIgnoreCase("TESTLOGS2")) {
			this.initializeMethodLogs2(args);
		} else if (testCmd.equalsIgnoreCase("DRYRUN")) {
			this.initializeMethodDryRun(args);
		}   else if (testCmd.equalsIgnoreCase("DRYRUN2")) {
			this.initializeMethodDryRun2(args);
		}
	}

	public void runTestForPID(String pid, String simulatorName) throws Exception {
	   log.info("BO TestRAD68ImagingDocumentSource runTestForPID");
	   Path stdPath = Paths.get(Utility.getXDSIRoot(), "storage", "runtime", pid, "kos");
      log.info("test data dir " + stdPath);
	   Utility.isValidPfn("RAD-68 test data dir", stdPath, PfnType.DIRECTORY, "r");
	}
	
	private void initializeMethodTest(String[] args) throws Exception {
		// args[0] is the command that brought us to this method
		String testDcmPfn = args[1];
		String stdDcmPfn  = args[2];
		String testXmlPfn = args[3];
		String stdXmlPfn  = args[4];

		StepRAD68KOSDocument stepRAD68KOSDocument = new StepRAD68KOSDocument();
		stepRAD68KOSDocument.initializeStep(new Object[] { testDcmPfn,
				stdDcmPfn });
		addStep(stepRAD68KOSDocument);

		StepRAD68KOSMetadata stepRAD68KOSMetadata = new StepRAD68KOSMetadata(trn);
		stepRAD68KOSMetadata.initializeStep(new Object[] { testXmlPfn,
				stdXmlPfn });
		addStep(stepRAD68KOSMetadata);
	}
	
	private void initializeMethodLogs(String[] args) throws Exception {
        String simulator = getArg(args, 1);
        String pidDepartment = getArg(args, 2);
        String pidAffinityDomain = getArg(args, 3);
        Path dir = root.resolve("storage" + Utility.fs + "runtime").resolve(pidDepartment);
        Path std = dir.resolve("kos");
        Path test = dir.resolve("test");
        Files.createDirectories(test);
        this.deleteIfExists(test, "kos.dcm");
        this.deleteIfExists(test, "metadata.xml");
        this.fileMustExist(std, "kos.dcm");
        this.fileMustExist(std, "metadata.xml");
        PrsSimLogs.main(new String[] {"GETKOS", simulator, pidAffinityDomain, "-", test.toString(), "kos.dcm"});
        PrsSimLogs.main(new String[] {"GETMETADATA", simulator, pidAffinityDomain, "-", test.toString(), "metadata.xml"});
        this.fileMustExist(test, "kos.dcm");
        this.fileMustExist(test, "metadata.xml");
       this.initializeMethodTest(new String[] {
        		"RUNTEST",
                test.resolve("kos.dcm").toString(),  
                std.resolve("kos.dcm").toString(),
                test.resolve("metadata.xml").toString(),  
                std.resolve("metadata.xml").toString()});
	}
	
	
	private void initializeMethodLogs2(String[] args) throws Exception {
        String simulator = getArg(args, 1);
        String pidDepartment = getArg(args, 2);
        String pidAffinityDomain = getArg(args, 3);
        String label = getArg(args, 4);
        String extension = getArg(args, 5);
        String testName = getArg(args, 6);
        
        Path dir = root.resolve("storage" + Utility.fs + "ids" + Utility.fs + extension).resolve(pidDepartment);
        Path std = dir.resolve("kos");
        Path test = root.resolve("results" + Utility.fs + label + Utility.fs + testName + Utility.fs + "data");
        Files.createDirectories(test);
        
        Path report = root.resolve("results" + Utility.fs + label + Utility.fs + testName + Utility.fs + "validation");
        Files.createDirectories(report);
        reportFolder = report.toString();
        
        this.deleteIfExists(test, "kos.dcm");
        this.deleteIfExists(test, "metadata.xml");
        this.fileMustExist(std, "kos.dcm");
        this.fileMustExist(std, "metadata.xml");

        PrsSimLogs.main(new String[] {"GETKOS",      simulator, "rep", "prb", pidAffinityDomain, "-", test.toString(), "kos.dcm"});
        PrsSimLogs.main(new String[] {"GETMETADATA", simulator, "rep", "prb", pidAffinityDomain, "-", test.toString(), "metadata.xml"});
        this.fileMustExist(test, "kos.dcm");
        this.fileMustExist(test, "metadata.xml");
        
        dumpKOSToText(test.toString() + Utility.fs + "kos.txt", test.toString() + Utility.fs + "kos.dcm");
        
        this.initializeMethodTest(new String[] {
        		"RUNTEST",
                test.resolve("kos.dcm").toString(),  
                std.resolve("kos.dcm").toString(),
                test.resolve("metadata.xml").toString(),  
                std.resolve("metadata.xml").toString()});
	}
	
	private void dumpKOSToText(String outputPath, String kosPath) throws Exception {
        DICOMUtility u = new DICOMUtility();
        KOSBean bean = u.readKOS(kosPath);
        String s = bean.toString();
        PrintWriter writer = new PrintWriter(outputPath, "UTF-8");
        writer.println(s);
        writer.close();        
	}
	
	
	private void initializeMethodDryRun(String[] args) throws Exception {
        String simulator = getArg(args, 1);
        String pidDepartment = getArg(args, 2);
        String pidAffinityDomain = getArg(args, 3);
        Path dir = root.resolve("storage" + Utility.fs + "runtime").resolve(pidDepartment);
        Path std = dir.resolve("kos");
        Path test = dir.resolve("test");
        Files.createDirectories(test);
        this.deleteIfExists(test, "kos.dcm");
        this.deleteIfExists(test, "metadata.xml");
        this.fileMustExist(std, "kos.dcm");
        this.fileMustExist(std, "metadata.xml");
        PrsSimLogs.main(new String[] {"GETKOS", simulator, pidAffinityDomain, "-", test.toString(), "kos.dcm"});
        PrsSimLogs.main(new String[] {"GETMETADATA", simulator, pidAffinityDomain, "-", test.toString(), "metadata.xml"});
        this.fileMustExist(test, "kos.dcm");
        this.fileMustExist(test, "metadata.xml");
//        Path testPathKOS = test.resolve("kos.dcm");
//        Path testPathMetadata = test.resolve("metadata.xml");
//        Path stdPathKOS = std.resolve("kos.dcm");
//        Path stdPathMetadata = std.resolve("metadata.xml");
	}
	
	
	private void initializeMethodDryRun2(String[] args) throws Exception {
        String simulator = getArg(args, 1);
        String pidDepartment = getArg(args, 2);
        String pidAffinityDomain = getArg(args, 3);
        String label = getArg(args, 4);
        String extension = getArg(args, 5);
        String testName = getArg(args, 6);
        
        Path dir = root.resolve("storage" + Utility.fs + "ids" + Utility.fs + extension).resolve(pidDepartment);
        Path std = dir.resolve("kos");
        Path test = root.resolve("results" + Utility.fs + label + Utility.fs + testName + Utility.fs + "data");
        Files.createDirectories(test);
        this.deleteIfExists(test, "kos.dcm");
        this.deleteIfExists(test, "metadata.xml");
        this.fileMustExist(std, "kos.dcm");
        this.fileMustExist(std, "metadata.xml");

        PrsSimLogs.main(new String[] {"GETKOS", simulator, pidAffinityDomain, "-", test.toString(), "kos.dcm"});
        PrsSimLogs.main(new String[] {"GETMETADATA", simulator, pidAffinityDomain, "-", test.toString(), "metadata.xml"});
        this.fileMustExist(test, "kos.dcm");
        this.fileMustExist(test, "metadata.xml");
        Path testPathKOS = test.resolve("kos.dcm");
        Path testPathMetadata = test.resolve("metadata.xml");
        Path stdPathKOS = std.resolve("kos.dcm");
        Path stdPathMetadata = std.resolve("metadata.xml");
	}
	
	private void deleteIfExists(Path p, String filename) throws Exception {
		log.fine("TestRAD68ImagingDocumentSource::deleteIfExists start method " + p.toString() + " " + filename);
		Path filePath = p.resolve(filename);
		File f = filePath.toFile();
		if (f.exists()) {
			log.fine("File does exist and will be deleted");
			f.delete();
		}
	}
	
	private void fileMustExist(Path p, String filename) throws Exception {
		log.fine("TestRAD68ImagingDocumentSource::fileMustExist start method " + p.toString() + " " + filename);
		File f = p.resolve(filename).toFile();
		if (! f.exists()) {
			String s = p.resolve(filename).toString();
			log.severe("File does not exist; fileMustExist method throwing an exception: " + s);
			throw new Exception("File is missing and is required for operation: " + s);
		}
	}
	/**
    * Test harness.
    * <ol>
    * <li>First argument indicates method to test</li>
    * <ol>
    * <li>RUNTEST = initialize, run, get results</li>
    * </ol>
    * <li>Remainder of arguments are passed to method in order.</li>
    * </ol>
    * 
    * @param args arguments
    */
   public static void main(String[] args) {
		TestRAD68ImagingDocumentSource testRAD68ImagingDocumentSource = new TestRAD68ImagingDocumentSource();
		try {
			testRAD68ImagingDocumentSource.initializeTest(args);
			testRAD68ImagingDocumentSource.runTest();
			Results results = testRAD68ImagingDocumentSource
			   .getResults("RAD-68 Imaging Document Source");
			log.info("Test Results:" + Utility.nl + results);
			testRAD68ImagingDocumentSource.reportResults("RAD-68 Imaging Document Source");
		} catch (Exception e) {
			e.printStackTrace();
		}
/*	   
	   String cmd;
      String[] pars = { "-r", Utility.getXDSIRoot() + fs + "runDirectory" };
      Util.initializeCommandLine("METHOD_TEST", pars, null);
      log = Util.getLog();
      cmd = getArg(args, 0);
      log.info("Running " + cmd + " test");
      try {
         if (cmd.equalsIgnoreCase("RUNTEST")) {
            TestRAD68ImagingDocumentSource test = new TestRAD68ImagingDocumentSource();
            test.initializeTest(new String[] { args[1], args[2], args[3], args[4] });
            test.runTest();
            Results results = test.getResults();
            log.info("Test Results:" + nl + results);
         }
         if (cmd.equalsIgnoreCase("TESTLOGS")) {
        	
        	 // Args:
        	 //   simulator name (e.g., xdsi01__rep-reg)
        	 //   Patient ID: Department
        	 //   Patient ID: Affinity Domain
        	 
            String simulator = getArg(args, 1);
            String pidDepartment = getArg(args, 2);
            String pidAffinityDomain = getArg(args, 3);
            Path dir = root.resolve("storage" + fs + "runtime").resolve(pidDepartment);
            Path std = dir.resolve("kos");
            Path test = dir.resolve("test");
            Files.createDirectories(test);
            PrsSimLogs.main(new String[] {"GETKOS", simulator, pidAffinityDomain, "-", test.toString(), "kos.dcm"});
            PrsSimLogs.main(new String[] {"GETMETADATA", simulator, pidAffinityDomain, "-", test.toString(), "metadata.xml"});
            
            TestRAD68ImagingDocumentSource testLogs = new TestRAD68ImagingDocumentSource();
            testLogs.initializeTest(new String[] { 
               test.resolve("kos.dcm").toString(),  
               std.resolve("kos.dcm").toString(),
               test.resolve("metadata.xml").toString(),  
               std.resolve("metadata.xml").toString()});
            testLogs.runTest();
            Results results = testLogs.getResults();
            log.info("Test Results:" + nl + results);
            
         }
            

         log.info(cmd + " test completed");
      } catch (Exception e) {
         log.severe(cmd + " test failed");
         e.printStackTrace();
      }
      */
   }
   private String getArg(String[] args, int arg) {
      if (args.length > arg) {
         String a = args[arg];
         if (StringUtils.isBlank(a) || a.equals("-") || a.equals("_") || a.equalsIgnoreCase("null")) return null;
         return a.trim();
      }
      return null;
   }
/*
	private String getArg(Object[] args, int arg) {
		if (args.length > arg) {
			String a = (String) args[arg];
			if (StringUtils.isBlank(a) || a.equals("-") || a.equals("_")
					|| a.equalsIgnoreCase("null"))
				return null;
			return a.trim();
		}
		return null;
	}
*/
}
