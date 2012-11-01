package gov.nist.toolkit.testengine;

public class TestSection {
	public String test;
	public String section;
	
	public TestSection(String test, String section) {
		this.test = test;
		this.section = section;
	}
	
	void normalize() {
		if (test == null) test = "";
		if (section == null) section = "";
	}
	
	public boolean equals(String test, String section) {
		if (this.test.equals(test) && this.section.equals(section))
			return true;
		return false;
	}
}
