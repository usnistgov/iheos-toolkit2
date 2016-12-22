package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.results.client.TestLog;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testenginelogging.client.LogMapDTO;
import gov.nist.toolkit.testenginelogging.client.LogMapItemDTO;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;
import gov.nist.toolkit.utilities.xml.OMFormatter;

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
				testLog.inHeader = new OMFormatter(stepLog.getInHeader()).toHtml();
				testLog.inputMetadata = new OMFormatter(stepLog.getInputMetadata()).toHtml();
				testLog.outHeader = new OMFormatter(stepLog.getOutHeader()).toHtml();
				testLog.result = new OMFormatter(stepLog.getResult()).toHtml();
				testLog.status = stepLog.getStatus();
            testLog.assignedIds  = stepLog.getAssignedIds();
            testLog.assignedUids = stepLog.getAssignedUids();
				testLog.errors = listAsString(stepLog.getErrors());

				testLog.log = new OMFormatter(stepLog.getRoot()).toHtml();
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
