package gov.nist.toolkit.testengine.engine;



import gov.nist.toolkit.testenginelogging.TestDetails;

import java.io.File;
import java.util.regex.Matcher;

public class TestKitLog {
	File testLog;
	File testKit;
	File altTestKit;

	public TestKitLog(File testLogBaseDir, File testkitBaseDir, File altTestkitBaseDir) throws Exception {
		testLog = testLogBaseDir;
		testKit = testkitBaseDir;
		altTestKit = altTestkitBaseDir;

		if ( !testLog.isDirectory() )
			throw new Exception("TestLog: log directory " + testLog + " does not exist");
	}

	/**
	 * Return log file and as a side effect create directory structure necessary to store it.
	 * @param testPlan
	 * @return
	 * @throws Exception 
	 */
	public File getLogFile(File testPlan) throws Exception {
		String relativePath = null;

		try {
			relativePath = TestDetails.getLogicalPath(testPlan.getParentFile(), altTestKit);
		} catch (Exception e) {
			relativePath = TestDetails.getLogicalPath(testPlan.getParentFile(), testKit);
		}
		// formats:
		//	tests/testname/section
		// or
		//  tests/testname

		String[] parts = relativePath.split(Matcher.quoteReplacement(File.separator));

		File path;

		/* if (parts.length == 3)
			path = new File(testLog + File.separator + parts[2] +  File.separator + "log.xml");
		else
			path = new File(testLog + File.separator + "log.xml");
		*/

		if (parts.length>2) {
			path = testLog;
			int offset = 2; // + (parts.length - 3);
			for (int cx=offset; cx < parts.length; cx++) {
				path = new File(path, parts[cx]);
			}
			path = new File(path, "log.xml");

		} else
			path = new File(testLog + File.separator + "log.xml");

		System.out.println("getLogFile");
		System.out.println("   testlog is " + testLog);
		System.out.println("   testspec is " + testPlan);
		System.out.println("   log file is " + path);
		System.out.println("   relative path is " + relativePath);
		path.getParentFile().mkdirs();

		return path;
	}
}
