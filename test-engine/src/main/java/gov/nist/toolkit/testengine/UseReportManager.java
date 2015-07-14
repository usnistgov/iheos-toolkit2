package gov.nist.toolkit.testengine;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.testenginelogging.LogFileContent;
import gov.nist.toolkit.testenginelogging.Report;
import gov.nist.toolkit.testenginelogging.SectionLogMap;
import gov.nist.toolkit.testenginelogging.TestDetails;
import gov.nist.toolkit.testenginelogging.TestStepLogContent;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;

public class UseReportManager  {
	List<UseReport> useReports;
	RetInfo retInfo;
	ReportManager reportManager; // things reported from query results
	TestConfig testConfig;
	SectionLogMap priorTests = new SectionLogMap();

	/**
	 * Return TestSections necessary to satisfy these UseReport instances.
	 * @return
	 */
	public TestSections getTestSections() {
		TestSections ts = new TestSections();
		
		for (UseReport ur : useReports) {
			ts.add(ur.test, ur.section);
		}
		
		return ts;
	}
	
	public void loadPriorTestSections(TestConfig config) throws Exception {
		TestSections ts = getTestSections();
		for (TestSection tsec : ts.getTestSections()) {
			String test = tsec.test;
			String section = tsec.section;
			if (test == null || test.equals(""))
				test = config.testNum;
			if (section != null && section.equals("THIS"))
				continue;
			if (config.verbose) 
				System.out.println("\tLoading logs for test " + test + " section " + section);
			TestDetails tspec = new TestDetails(config.testkitHome, test);
			tspec.setLogDir(config.logRepository.logDir());
			File testlogFile = tspec.getTestLog(test, section);
			if (testlogFile != null)
				priorTests.put((section.equals("") ? "None" : section), new LogFileContent(testlogFile));
		}
	}
	
	public String toString() {
		return useReports.toString();
	}
	
	String useReportsToString() {
		StringBuffer buf = new StringBuffer();

		for (UseReport ur : useReports) {
			buf.append(ur.toString());
			buf.append("\n");
		}

		return buf.toString();
	}

	public UseReportManager(TestConfig config) {
		testConfig = config;
		useReports = new ArrayList<UseReport>();
	}

	public OMElement toXML() {
		OMElement urm = MetadataSupport.om_factory.createOMElement("UseReports", null);

		for (UseReport ur : useReports) {
			urm.addChild(ur.toXML());
		}

		return  urm;
	}

	static QName test_qname = new QName("test");
	static QName section_qname = new QName("section");
	static QName step_qname = new QName("step");
	static QName reportName_qname = new QName("reportName");
	static QName useas_qname = new QName("useAs");

	public void add(OMElement useRep) throws XdsInternalException {
		UseReport u = new UseReport();
		u.test = useRep.getAttributeValue(test_qname);
		u.section = useRep.getAttributeValue(section_qname);
		u.step = useRep.getAttributeValue(step_qname);
		u.reportName = useRep.getAttributeValue(reportName_qname);
		u.useAs = useRep.getAttributeValue(useas_qname);
		
		if (u.section == null || u.section.equals(""))
			u.section = "None";

		if (!u.isComplete()) {
			throw new XdsInternalException("Invalid UseReport: cannot have null or empty fields: " + u);
		}	

		add(u);
	}
	
	void add(String name, String value) {
		UseReport ur = new UseReport();
		ur.useAs = name;
		ur.value = value;
		
		add(ur);
	}

	public void add(UseReport r) {
		r.normalize();
		useReports.add(r);
	}

	public void setRetInfo(RetInfo ri, int docIndex) {
		retInfo = ri;
		
		add("$repuid_doc" + Integer.toString(docIndex)  + "$", ri.getRep_uid());
		add("$mimetype_doc" + Integer.toString(docIndex)  + "$", ri.getContent_type());
		add("$hash_doc" + Integer.toString(docIndex)  + "$", ri.getHash());
		add("$home_doc" + Integer.toString(docIndex)  + "$", ri.getHome());
		add("$size_doc" + Integer.toString(docIndex)  + "$", Integer.toString(ri.getSize()));
	}

	public void setReportManager(ReportManager rm) {
		reportManager = rm;
	}

	public void resolve(SectionLogMap previousLogs) throws XdsInternalException {
		for (UseReport ur : useReports) {
			LogFileContent log = previousLogs.get(ur.section);
			if (log == null) 
				log = priorTests.get(ur.section);
			if (log == null)
				throw new XdsInternalException("UseReportManager#resolve: cannot find log for section " + ur.section + "\n" + toString() + "\n" + previousLogs.toString() + "\n");
			TestStepLogContent stepLog = log.getStepLog(ur.step);
			if (stepLog == null)
				throw new XdsInternalException("UseReportManager#resolve: cannot find log for step " + ur.step + " in section " + ur.section + "\n" + toString() + "\n" + previousLogs.toString() + "\n");

			OMElement reportEles = stepLog.getRawReports();
			if (reportEles == null)
				throw new XdsInternalException("UseReportManager#resolve: cannot find Reports section for step  " + ur.step + " in section " + ur.section + "\n" + toString() + "\n" + previousLogs.toString() + "\n");	

			String reportName = ur.reportName;
			for (OMElement rep : MetadataSupport.childrenWithLocalName(reportEles, "Report")) {
				Report r = Report.parse(rep);
				if (reportName.equals(r.name)) {
					ur.value = r.getValue();
				}
			}

		}
	}
	
	public void apply(List<OMElement> xmls) throws XdsInternalException {
		for (OMElement xml : xmls) 
			apply(xml);
	}

	public void apply(OMElement xml) throws XdsInternalException {
		if (xml == null)
			return;
		Linkage l = new Linkage(testConfig);
		for (UseReport ur : useReports) {
			String useAs = ur.useAs;
			String value = ur.value;
			try {
				if (useAs == null || useAs.equals("") ||
						value == null || value.equals(""))
					continue;
				l.replace_string_in_text_and_attributes(xml, ur.useAs, ur.value);
			} catch (Exception e) {
				throw new XdsInternalException("UseReportManager#apply: error applying reported value " + ur.useAs + " = " + ur.value + "\n" + useReportsToString(), e);
			}
		}
	}


}
