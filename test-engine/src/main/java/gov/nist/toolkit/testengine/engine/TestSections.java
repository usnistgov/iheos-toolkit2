package gov.nist.toolkit.testengine.engine;


import gov.nist.toolkit.results.client.TestId;

import java.util.ArrayList;
import java.util.List;

public class TestSections {

	List<TestSection> testSections;
	
	public TestSections() {
		testSections = new ArrayList<TestSection>();
	}
	
	public void add(TestId testId, String section) {
		if (contains(testId, section))
			return;
		testSections.add(new TestSection(testId, section));
	}
	
	public boolean contains(TestId testId, String section) {
		for (TestSection ts : testSections) {
			if (ts.equals(testId, section))
				return true;
		}
		return false;
	}
	
	public List<TestSection> getTestSections() {
		return testSections;
	}
}
