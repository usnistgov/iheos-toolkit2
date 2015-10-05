package gov.nist.toolkit.testengine.test;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testenginelogging.TestDetails;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.junit.Test;

import java.io.File;


public class TestSpecTest {
	File testKitDir = new File("/Users/bill/dev/testkit");
	
	@Test
	public void badTest() {
		try {
			@SuppressWarnings("unused")
			TestDetails ts = new TestDetails(testKitDir, new TestInstance("999999"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			assert e.getMessage().indexOf("no testplan.xml files found") != -1;
		}
	}
	
	@Test
	public void simpleTestDirTest() {
		try {
			// 
			TestDetails ts = new TestDetails(testKitDir, new TestInstance("11801"));
			String testdir = ts.getTestDir().toString();
			assert "/Users/bill/dev/testkit/tests/11801".equals(testdir);
			assert ts.isTestDir();
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
	}

	@Test
	public void multiTestDirTest() {
		try {
			TestDetails ts = new TestDetails(testKitDir, new TestInstance("11733"));
			String testdir = ts.getTestDir().toString();
			assert "/Users/bill/dev/testkit/tests/11733".equals(testdir);
			assert ts.isTestDir();
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
	}
	
//	@Test
//	public void multiSubtestTest() {
//		try {
//			TestSpec ts = new TestSpec(testKitDir, "11733");
//			List<File> testplans = ts.getTestPlans();
//			assert testplans.size() == 2;
//			assert "/Users/bill/dev/testkit/tests/11733/submit/testplan.xml".equals(testplans.get(0).toString());
//			assert "/Users/bill/dev/testkit/tests/11733/eval/testplan.xml".equals(testplans.get(1).toString());
//		} catch (Exception e) {
//			System.out.println(ExceptionUtil.exception_details(e));
//			assert false;
//		}
//	}

//	@Test
//	public void multiSubtestNextTest() {
//		try {
//			TestSpec ts = new TestSpec(testKitDir, "11733");
//			assert ts.hasNext();
//			String path;
//			path = ts.next().toString();
//			assert "/Users/bill/dev/testkit/tests/11733/submit/testplan.xml".equals(path);
//			assert ts.hasNext();
//			path = ts.next().toString();
//			assert "/Users/bill/dev/testkit/tests/11733/eval/testplan.xml".equals(path);
//			assert ts.hasNext() == false;
//		} catch (Exception e) {
//			System.out.println(ExceptionUtil.exception_details(e));
//			assert false;
//		}
//	}

//	@Test
//	public void singleSubtestTest() {
//		try {
//			TestSpec ts = new TestSpec(testKitDir, "11801");
//			List<File> testplans = ts.getTestPlans();
//			assert testplans.size() == 1;
//			assert "/Users/bill/dev/testkit/tests/11801/testplan.xml".equals(testplans.get(0).toString());
//		} catch (Exception e) {
//			System.out.println(ExceptionUtil.exception_details(e));
//			assert false;
//		}
//	}
	
	@Test
	public void logicalPath() {
		File testkitPath = new File("/Users/bill/dev/testkit");
		File testPlan = new File("/Users/bill/dev/testkit/tests/11701/testplan.xml");
		try {
			String logical = TestDetails.getLogicalPath(testPlan, testkitPath);
			assert "tests/11701/testplan.xml".equals(logical);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
	}

}
