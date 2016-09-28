package gov.nist.toolkit.results.client;




import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

public class TestLogs implements IsSerializable {

	public TestInstance testInstance;
	public AssertionResult assertionResult; // for reporting errors in getLogs() call
	public List<TestLog> logs = new ArrayList<TestLog>();
	
	public TestLogs() {}
		
	public int size() {
		if (logs == null)
			return 0;
		return logs.size();
	}
	
	public TestLog getTestLog(int i) {
		if (logs == null || logs.size() <= i)
			return null;
		return logs.get(i);
	}
	
	public boolean isSuccess() {
		boolean success = true;

		if (assertionResult != null && !assertionResult.passed()) success = false;
		for (TestLog log : logs) {
			if (!log.status) success = false;
		}

		return success;
	}

}
