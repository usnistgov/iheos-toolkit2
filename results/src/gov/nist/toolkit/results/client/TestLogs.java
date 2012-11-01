package gov.nist.toolkit.results.client;




import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TestLogs implements IsSerializable {

	public XdstestLogId logId;
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
	


}
