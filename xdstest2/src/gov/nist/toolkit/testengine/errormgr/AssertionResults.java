package gov.nist.toolkit.testengine.errormgr;

import java.util.ArrayList;
import java.util.List;

public class AssertionResults {

	public List<AssertionResult> assertions;
	
	public AssertionResults() {
		assertions = new ArrayList<AssertionResult>();
	}
	
	/**
	 * Use for assertions that fail
	 * @param assertion
	 * @param info
	 */
	public void add(String assertion, String info) {
		assertions.add(new AssertionResult(assertion, info));
	}
	
	/**
	 * Use for assertions that pass
	 * @param assertion
	 */
	public void add(String assertion) {
		assertions.add(new AssertionResult(assertion));
	}
	
	/**
	 * Use for any assertion
	 * @param assertion
	 * @param info
	 * @param status
	 */
	public void add(String assertion, String info, boolean status) {
		assertions.add(new AssertionResult(assertion, info, status));
	}

	public void add(String assertion, boolean status) {
		assertions.add(new AssertionResult(assertion, status));
		
	}
}
