package gov.nist.toolkit.testengine.errormgr;

public class AssertionResult {
	public String assertion;
	public boolean status;
	public String info;
	
	/**
	 * Use for assertions that fail
	 * @param assertion
	 * @param info
	 * @param status
	 */
	public AssertionResult(String assertion, String info) {
		this.assertion = assertion;
		this.info = info;
		this.status = false;
	}

	/**
	 * Use for assertions that pass
	 * @param assertion
	 * @param status
	 */
	public AssertionResult(String assertion) {
		this.assertion = assertion;
		this.info = "";
		this.status = true;
	}
	
	/**
	 * Use for any assertion
	 * @param assertion
	 * @param info
	 * @param status
	 */
	public AssertionResult(String assertion, String info, boolean status) {
		this.assertion = assertion;
		this.info = info;
		this.status = status;
	}

	public AssertionResult(String assertion, boolean status) {
		this.assertion = assertion;
		this.info = "";
		this.status = status;
	}
}

