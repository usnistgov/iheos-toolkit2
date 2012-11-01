package gov.nist.toolkit.testengine;

import java.util.ArrayList;
import java.util.List;

public class TestSections {

	List<TestSection> testSections;
	
	public TestSections() {
		testSections = new ArrayList<TestSection>();
	}
	
	public void add(String test, String section) {
		if (contains(test, section))
			return;
		testSections.add(new TestSection(test, section));
	}
	
	public boolean contains(String test, String section) {
		for (TestSection ts : testSections) {
			if (ts.equals(test, section))
				return true;
		}
		return false;
	}
	
	public List<TestSection> getTestSections() {
		return testSections;
	}
}
