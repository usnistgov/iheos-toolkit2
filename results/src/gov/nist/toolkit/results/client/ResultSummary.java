package gov.nist.toolkit.results.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ResultSummary implements IsSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	public String testName;   // test can be a single test or a test collection
	public String timestamp;
	public boolean pass = true;

	public ResultSummary() {}
	
	public ResultSummary(Result result) {
		testName = result.testName;
		timestamp = result.timestamp;
		pass = result.pass;
	}
}
