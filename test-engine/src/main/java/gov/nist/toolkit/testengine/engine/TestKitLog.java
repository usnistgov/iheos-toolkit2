package gov.nist.toolkit.testengine.engine;



import gov.nist.toolkit.testenginelogging.TestLogDetails;

import java.io.File;
import java.util.regex.Matcher;

class TestKitLog {
	private File testLog;
	private File testKit;

	TestKitLog(File testLogBaseDir, File testkitBaseDir) throws Exception {
		testLog = testLogBaseDir;
		testKit = testkitBaseDir;

		if ( !testLog.isDirectory() )
			throw new Exception("TestLog: log directory " + testLog + " does not exist");
	}

	/**
	 * Return log file and as a side effect create directory structure necessary to store it.
	 * @param testPlan
	 * @return
	 * @throws Exception 
	 */
	File getLogFile(File testPlan) throws Exception {
		return new File(getLogDir(testPlan), "log.xml");
//		String relativePath = null;
//
//		relativePath = TestLogDetails.getLogicalPath(testPlan.getParentFile(), testKit);
//		// formats:
//		//	tests/testname/section
//		// or
//		//  tests/testname
//
//		String[] parts = relativePath.split(Matcher.quoteReplacement(File.separator));
//
//		File path;
//
//		if (parts.length>2) {
//			path = testLog;
//			int offset = 2; // + (parts.length - 3);
//			for (int cx=offset; cx < parts.length; cx++) {
//				path = new File(path, parts[cx]);
//			}
//			path = new File(path, "log.xml");
//
//		} else
//			path = new File(testLog + File.separator + "log.xml");
//
//		System.out.println("getLogFile");
//		System.out.println("   testlog is " + testLog);
//		System.out.println("   testspec is " + testPlan);
//		System.out.println("   log file is " + path);
//		System.out.println("   relative path is " + relativePath);
//		path.getParentFile().mkdirs();
//
//		return path;
	}

	/**
	 * A copy of the test plan can be stored next to the log file.
	 * @param testPlan
	 * @return log.xml file
	 * @throws Exception
	 */
	File getTestPlanArchive(File testPlan) throws Exception {
		return new File(getLogDir(testPlan), "testplan.xml");
	}

	File getLogDir(File testPlan) throws Exception {
		String relativePath = null;

		relativePath = TestLogDetails.getLogicalPath(testPlan.getParentFile(), testKit);
		// formats:
		//	tests/testname/section
		// or
		//  tests/testname

		String[] parts = relativePath.split(Matcher.quoteReplacement(File.separator));

		File path;

		if (parts.length>2) {
			path = testLog;
			int offset = 2; // + (parts.length - 3);
			for (int cx=offset; cx < parts.length; cx++) {
				path = new File(path, parts[cx]);
			}

		} else
			path = testLog;
		return path;
	}
}
