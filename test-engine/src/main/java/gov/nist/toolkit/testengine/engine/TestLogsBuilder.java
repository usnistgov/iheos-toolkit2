package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.results.client.TestLog;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.testenginelogging.LogFileContent;
import gov.nist.toolkit.testenginelogging.LogMap;
import gov.nist.toolkit.testenginelogging.LogMapItem;
import gov.nist.toolkit.testenginelogging.TestStepLogContent;

import java.util.List;

public class TestLogsBuilder {

	static public TestLogs build(LogMap logMap) throws Exception {
		TestLogs logs = new TestLogs();
		
		for (LogMapItem item : logMap.getItems()) {
			LogFileContent logFile = item.log;
			for (TestStepLogContent stepLog : logFile.getStepLogs()) {
				TestLog testLog = new TestLog();
				String stepName = stepLog.getName();
				logs.logs.add(testLog);

				testLog.stepName = stepName;
				testLog.endpoint = stepLog.getEndpoint();
				testLog.inHeader = stepLog.getInHeader();
				testLog.inputMetadata = stepLog.getInputMetadata();
				testLog.outHeader = stepLog.getOutHeader();
				testLog.result = stepLog.getResult();
				testLog.status = stepLog.getStatus();
				testLog.errors = listAsString(stepLog.getErrors());

				testLog.log = stepLog.getRoot();
			}
		}

		return logs;

	}

	static String listAsString(List<String> lst) {
		StringBuffer buf = new StringBuffer();

		for (String i : lst) {
			buf.append(i).append("\n");
		}

		return buf.toString();
	}

}
