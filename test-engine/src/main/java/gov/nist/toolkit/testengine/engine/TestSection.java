package gov.nist.toolkit.testengine.engine;


import gov.nist.toolkit.results.client.TestId;

public class TestSection {
	public TestId testId;
	public String section;
	
	public TestSection(TestId testId, String section) {
		this.testId = testId;
		this.section = section;
	}
	
	void normalize() {
		if (testId == null) testId = new TestId("");
		if (section == null) section = "";
	}
	
	public boolean equals(TestId testId, String section) {
		if (this.testId.equals(testId) && this.section.equals(section))
			return true;
		return false;
	}
}
