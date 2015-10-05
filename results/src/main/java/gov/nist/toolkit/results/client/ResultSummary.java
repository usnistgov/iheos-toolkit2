package gov.nist.toolkit.results.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class ResultSummary implements IsSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	public TestInstance testInstance;   // test can be a single test or a test collection
	public String timestamp;
	public boolean pass = true;

	public ResultSummary() {}
	
	public ResultSummary(Result result) {
		testInstance = result.testInstance;
		timestamp = result.timestamp;
		pass = result.pass;
	}
}
