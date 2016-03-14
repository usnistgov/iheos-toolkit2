package gov.nist.toolkit.testengine.test;

import gov.nist.toolkit.testengine.engine.TestCollection;
import gov.nist.toolkit.testenginelogging.TestDetails;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.File;
import java.util.List;

import org.junit.Test;


public class TestCollectionTest {
	File testkit = new File("/Users/bill/dev/testkit");
	
	@Test
	public void noCollection() {
		try {
			@SuppressWarnings("unused")
			TestCollection tc = new TestCollection(testkit, "xxxxx");
		} catch (Exception e) {
			return;
		}
		assert false;
	}

	@Test
	public void yesCollection() {
		try {
			@SuppressWarnings("unused")
			TestCollection tc = new TestCollection(testkit, "sql");
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
	}
	
	@Test
	public void getTestSpecs() {
		try {
			TestCollection tc = new TestCollection(testkit, "sql");
			List<TestDetails> col = tc.getTestSpecs();
			assert col.size() == 2;
			
			// 11801
			TestDetails ts0 = col.get(0);
			assert ts0.getTestPlans().size() == 1;
			
			String testPlanPath = ts0.getTestPlans().get(0).getAbsolutePath();
			System.out.println("testPlanPath is " + testPlanPath);
			assert testPlanPath.indexOf("11801") != -1;
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			assert false;
		}
	}
}
