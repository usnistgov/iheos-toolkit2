package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.results.client.TestLog;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testenginelogging.client.LogMapDTO;
import gov.nist.toolkit.testenginelogging.client.LogMapItemDTO;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;

import java.util.List;

public class TestLogsBuilder {

	static public TestLogs build(LogMapDTO logMapDTO) throws Exception {
		TestLogs logs = new TestLogs();
		
		for (LogMapItemDTO item : logMapDTO.getItems()) {
			LogFileContentDTO logFile = item.getLog();
			for (TestStepLogContentDTO stepLog : logFile.getStepLogs()) {
				TestLog testLog = new TestLog();
				String stepName = stepLog.getId();
				logs.logs.add(testLog);

				testLog.stepName = stepName;
				testLog.endpoint = stepLog.getEndpoint();
				testLog.inHeader = stepLog.getInHeader();
				testLog.inputMetadata = stepLog.getInputMetadata();
				testLog.outHeader = stepLog.getOutHeader();
				testLog.result = stepLog.getResult();
				testLog.status = stepLog.getStatus();
                testLog.assignedIds  = stepLog.getAssignedIds();
                testLog.assignedUids = stepLog.getAssignedUids();
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
