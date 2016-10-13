package gov.nist.toolkit.testengine.engine;


import gov.nist.toolkit.results.client.TestInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of specific test SECTIONS
 */
public class TestSections {

	private List<TestSection> testSections;
	
	public TestSections() {
		testSections = new ArrayList<TestSection>();
	}
	
	public void add(TestInstance testInstance, String section) {
		if (contains(testInstance, section))
			return;
		testSections.add(new TestSection(testInstance, section));
	}
	
	public boolean contains(TestInstance testInstance, String section) {
		for (TestSection ts : testSections) {
			if (ts.equals(testInstance, section))
				return true;
		}
		return false;
	}
	
	public List<TestSection> getTestSections() {
		return testSections;
	}
}
