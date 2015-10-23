package gov.nist.toolkit.testengine.engine;


import gov.nist.toolkit.results.client.TestInstance;

public class TestSection {
	public TestInstance testInstance;
	public String section;
	
	public TestSection(TestInstance testInstance, String section) {
		this.testInstance = testInstance;
		this.section = section;
	}
	
	void normalize() {
		if (testInstance == null) testInstance = new TestInstance("");
		if (section == null) section = "";
	}
	
	public boolean equals(TestInstance testInstance, String section) {
		if (this.testInstance.equals(testInstance) && this.section.equals(section))
			return true;
		return false;
	}
}
