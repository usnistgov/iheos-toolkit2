package gov.nist.toolkit.testengine.engine;


import gov.nist.toolkit.results.client.TestInstance;

/**
 * Reference to a section of a test
 */
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TestSection that = (TestSection) o;

		if (testInstance != null ? !testInstance.equals(that.testInstance) : that.testInstance != null) return false;
		return section != null ? section.equals(that.section) : that.section == null;
	}

	@Override
	public int hashCode() {
		int result = testInstance != null ? testInstance.hashCode() : 0;
		result = 31 * result + (section != null ? section.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return testInstance.toString() + " : " + section;
	}
}
