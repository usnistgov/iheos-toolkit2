package gov.nist.toolkit.results.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public class AssertionResult implements IsSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	public String assertion;
	public boolean status;
	public String info;
	
	public String toString() {
		return ((status) ? "" : "  FAIL  " ) + "[ " + assertion + " ]" + "[ " + info + " ]";
	}
	
	public AssertionResult() {
		assertion = "";
		status = true;
		info = "";
	}
	
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
		this.status = status;
	}
	
	public boolean passed() { return status; }
	
}

