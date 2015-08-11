package gov.nist.toolkit.testenginelogging;



import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionLogMap {
	// TODO - section name must expand to test/section so that test context is maintained
	// section name => log
	Map<String, LogFileContent> sectionLogs;
	List<String> sectionNames;   // this dictates the order of the sections

	public SectionLogMap() {
		sectionLogs = new HashMap<String, LogFileContent>();
		sectionNames = new ArrayList<String>();
	}
	
	public List<SectionGoals> getGoals() {
		List<SectionGoals> goals = new ArrayList<SectionGoals>();
		for (String sectionName : sectionNames) {
			goals.add(sectionLogs.get(sectionName).getGoals());
		}
		return goals;
	}

	public LogFileContent getLogForSection(String sectionName) {
		return sectionLogs.get(sectionName);
	}

	public void put(String sectionName, LogFileContent log) throws XdsInternalException {
		if (log == null)
			throw new XdsInternalException("Null log for section " + sectionName);
		sectionNames.add(sectionName);
		sectionLogs.put(sectionName, log);
		Report.setSection(log.getReports(), sectionName);
	}

	public LogFileContent get(String sectionName) throws XdsInternalException {
		LogFileContent lf = sectionLogs.get(sectionName);
//		if (lf == null && !sectionName.equals("THIS"))
//			throw new XdsInternalException("Log for section " + sectionName + " is null");
		return lf;
	}

	public Collection<String> keySet() {
		return sectionNames;
	}

	public String toString() {
		return reportsToString();
	}
	
	public void remove(String sectionName) {
		sectionLogs.remove(sectionName);
	}

	public String reportsToString()  {
		StringBuffer buf = new StringBuffer();
		buf.append('[');

		for (String section : sectionLogs.keySet()) {
			buf.append("Section: ").append(section).append(": ");
			LogFileContent log = sectionLogs.get(section);
			try {
				List<Report> reports = log.getReports();
				for (Report r : reports) {
					r.section = section;
				}
				buf.append(reports.toString());
			} catch (Exception e) {
				System.out.println("Cannot find Reports for section " + section);
			}
		}

		buf.append(']');
		return buf.toString();

	}


}
