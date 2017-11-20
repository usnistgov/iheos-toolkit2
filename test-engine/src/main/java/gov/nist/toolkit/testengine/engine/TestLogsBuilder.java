package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.fhir.context.ToolkitFhirContext;
import gov.nist.toolkit.results.client.TestLog;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testenginelogging.client.LogMapDTO;
import gov.nist.toolkit.testenginelogging.client.LogMapItemDTO;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.hl7.fhir.instance.model.api.IBaseResource;

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
				testLog.inHeader = formatXmlToHtml(stepLog.getInHeader());
				testLog.inputMetadata = formatXmlToHtml(stepLog.getInputMetadata());
				testLog.outHeader = formatXmlToHtml(stepLog.getOutHeader());
				testLog.result = formatXmlToHtml(stepLog.getResult());
				testLog.status = stepLog.getStatus();
            testLog.assignedIds  = stepLog.getAssignedIds();
            testLog.assignedUids = stepLog.getAssignedUids();
				testLog.errors = listAsString(stepLog.getErrors());

				testLog.log = formatXmlToHtml(stepLog.getRoot());
			}
		}

		return logs;

	}

	private static String formatXmlToHtml(String xml) throws XdsInternalException {
		if(xml == null) return xml;
		xml = xml.trim();
		if (xml.startsWith("<"))
			return new OMFormatter(xml).toHtml();
		if (xml.startsWith("{")) {
			try {
				IBaseResource res = ToolkitFhirContext.get().newJsonParser().parseResource(xml);
				xml = ToolkitFhirContext.get().newJsonParser().setPrettyPrint(true).encodeResourceToString(res);
			} catch (Exception e) {
				return xml;
			}
		}
		return xml;
	}

	static String listAsString(List<String> lst) {
		StringBuffer buf = new StringBuffer();

		for (String i : lst) {
			buf.append(i).append("\n");
		}

		return buf.toString();
	}

}
