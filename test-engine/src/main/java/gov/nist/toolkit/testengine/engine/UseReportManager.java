package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentModel;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testenginelogging.*;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UseReportManager  {
    private final static Logger logger = Logger.getLogger(UseReportManager.class);
	List<UseReport> useReports;
	RetrievedDocumentModel retrievedDocumentModel;
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
			ts.add(ur.testInstance, ur.section);
		}

		return ts;
	}

	public void loadPriorTestSections(TestConfig config) throws Exception {
		TestSections ts = getTestSections();
		for (TestSection tsec : ts.getTestSections()) {
			TestInstance testInstance = tsec.testInstance;
			String section = tsec.section;
			if (testInstance == null || testInstance.isEmpty())
				testInstance = config.testInstance;
			if (section != null && section.equals("THIS"))
				continue;
			if (config.verbose)
				System.out.println("\tLoading logs for test " + testInstance + " section " + section + "...");
			TestDetails tspec = null;
			try {
				tspec = new TestDetails(config.altTestkitHome, testInstance);
			} catch (Exception e) {
				tspec = new TestDetails(config.testkitHome, testInstance);
			}
            System.out.println("TestDetails are: " + tspec.toString());
			tspec.setLogRepository(config.logRepository);
			File testlogFile = tspec.getTestLog(testInstance, section);
            System.out.println("Loading log " + testlogFile);
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
        logger.info("Parsing " + new OMFormatter(useRep).toString());
		UseReport u = new UseReport();
		u.testInstance = new TestInstance(useRep.getAttributeValue(test_qname));
		u.section = useRep.getAttributeValue(section_qname);
		u.step = useRep.getAttributeValue(step_qname);
		u.reportName = useRep.getAttributeValue(reportName_qname);
		u.useAs = useRep.getAttributeValue(useas_qname);

        logger.info("Parsing UseReport " + u.reportName);

		if (u.section == null || u.section.equals(""))
			u.section = "None";

		if (!u.isComplete()) {
			throw new XdsInternalException("Invalid UseReport: cannot have null or empty fields: " + u);
		}

		add(u);
	}

	public void add(String name, String value) {
		UseReport ur = new UseReport();
		ur.useAs = name;
		ur.value = value;

		add(ur);
	}

	public void add(UseReport r) {
		r.normalize();
		useReports.add(r);
	}

	public void setRetInfo(RetrievedDocumentModel ri, int docIndex) {
		retrievedDocumentModel = ri;

		add("$repuid_doc" + Integer.toString(docIndex)  + "$", ri.getRepUid());
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
            if (ur.useAs != null && !ur.useAs.equals("") &&
                    ur.value != null && !ur.value.equals(""))
                continue;  // already resolved
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
			for (OMElement rep : XmlUtil.childrenWithLocalName(reportEles, "Report")) {
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
        logger.info("apply UseReports");
		if (xml == null)
			return;
		Linkage l = new Linkage(testConfig);
		for (UseReport ur : useReports) {
			String useAs = ur.useAs;
			String value = ur.value;
			try {
				if (useAs == null || useAs.equals("") ||
						value == null || value.equals("")) {
                    logger.info("Skipping UseReport " + ur);
                    continue;
                }
                logger.info(String.format("Apply %s to %s", ur, new OMFormatter(xml).toString()));
				l.replace_string_in_text_and_attributes(xml, ur.useAs, ur.value);
			} catch (Exception e) {
				throw new XdsInternalException("UseReportManager#apply: error applying reported value " + ur.useAs + " = " + ur.value + "\n" + useReportsToString(), e);
			}
		}
	}


}
