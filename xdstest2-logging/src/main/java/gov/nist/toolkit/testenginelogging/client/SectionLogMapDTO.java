package gov.nist.toolkit.testenginelogging.client;



import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testenginelogging.ReportBuilder;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionLogMapDTO {
	// - section name must expand to test/section so that test context is maintained
	// section name => log
	Map<String, LogFileContentDTO> sectionLogs = new HashMap<String, LogFileContentDTO>();
	List<String> sectionNames = new ArrayList<String>();   // this dictates the order of the sections
	TestInstance testInstance;

	public SectionLogMapDTO(TestInstance testInstance) {
		this.testInstance = testInstance;
	}
	
	public List<SectionGoalsDTO> getGoals() {
		List<SectionGoalsDTO> goals = new ArrayList<SectionGoalsDTO>();
		for (String sectionName : sectionNames) {
			goals.add(sectionLogs.get(sectionName).getGoals());
		}
		return goals;
	}

	public LogFileContentDTO getLogForSection(String sectionName) {
		return sectionLogs.get(sectionName);
	}

	public void put(String sectionName, LogFileContentDTO log) throws XdsInternalException {
		if (log == null)
			throw new XdsInternalException("Null log for section " + sectionName);
		sectionNames.add(sectionName);
		sectionLogs.put(sectionName, log);
		ReportBuilder.setSection(log.getReportDTOs(), sectionName);
	}

	public LogFileContentDTO get(String sectionName) throws XdsInternalException {
		LogFileContentDTO lf = sectionLogs.get(sectionName);
//		if (lf == null && !sectionName.equals("THIS"))
//			throw new XdsInternalException("Log for section " + sectionName + " is null");
		return lf;
	}

	public Collection<String> keySet() {
		return sectionNames;
	}

	public String toString() {
		return ((testInstance != null) ? testInstance.toString() : "null") + reportsToString();
	}
	
	public void remove(String sectionName) {
		sectionLogs.remove(sectionName);
	}

    public String describe() { return "SectionLogMapDTO...\n" + reportsToString(); }

	public String reportsToString()  {
		StringBuffer buf = new StringBuffer();
		buf.append('[');

		for (String section : sectionLogs.keySet()) {
			buf.append("Section: ").append(section).append(": ");
			LogFileContentDTO log = sectionLogs.get(section);
			try {
				List<ReportDTO> reportDTOs = log.getReportDTOs();
				for (ReportDTO r : reportDTOs) {
					r.setSection(section);
				}
				buf.append(reportDTOs.toString());
			} catch (Exception e) {
				System.out.println("Cannot find Reports for section " + section);
			}
		}

		buf.append(']');
		return buf.toString();

	}

	public TestInstance getTestInstance() {
		return testInstance;
	}

	public void setTestInstance(TestInstance testInstance) {
		this.testInstance = testInstance;
	}
}
