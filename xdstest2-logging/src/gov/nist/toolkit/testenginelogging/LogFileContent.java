package gov.nist.toolkit.testenginelogging;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.registrysupport.logging.RegistryResponseLog;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;

/**
 * Object version of log.xml content.
 * @author bill
 *
 */
public class LogFileContent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2046605414265224604L;
	transient OMElement log;
	boolean success;
	List<TestStepLogContent> steps;
	Map<String, TestStepLogContent> stepMap;
	String testAttribute;
	String test = null;
	String section = null;
	SectionGoals sectionGoals;
	String fatalError;
	List<Report> reports;
	File inputFile = null;
	
	public Map<String, TestStepLogContent> getStepMap() {
		return stepMap;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("[LogFile: ");
		
		if (inputFile != null)
			buf.append(inputFile).append("  ");
		
		for (TestStepLogContent s : steps) {
			buf.append(s.toString());
		}
		
		buf.append("]");
		
		return buf.toString();
	}
	
	public String getSection() {
		return section;
	}

	public LogFileContent(File logfile) throws FactoryConfigurationError, Exception {
		inputFile = logfile;
		log = Util.parse_xml(logfile);
		init(false);
	}

	public LogFileContent(File logfile, boolean incompleteOk) throws FactoryConfigurationError, Exception {
		inputFile = logfile;
		log = Util.parse_xml(logfile);
		init(incompleteOk);
	}

	public LogFileContent(OMElement testresults) throws NotALogFileException, Exception {
		log = testresults;
		init(false);
	}

	public LogFileContent(OMElement testresults, boolean incompleteOk) throws NotALogFileException, Exception {
		log = testresults;
		init(incompleteOk);
	}

	private void init(boolean incompleteOk) throws NotALogFileException, Exception {
		steps = new ArrayList<TestStepLogContent>();
		try {
			parseStatus();
		} catch (Exception e) {
			if ( ! incompleteOk )
				throw new NotALogFileException(e.getMessage() + " log is [" + firstNChars(log.toString(), 20) + "]");
		}
		parseTest();
		parseTestSteps();
		calcGoals();
		parseFatalError();
		parseReports();
	}

	void parseTestSteps() throws Exception {
		steps = new ArrayList<TestStepLogContent>();
		stepMap = new HashMap<String, TestStepLogContent>();
		List<OMElement> stepEles = MetadataSupport.childrenWithLocalName(log, "TestStep");
		for (OMElement step : stepEles) {
			TestStepLogContent stepLog = new TestStepLogContent(step);
			steps.add(stepLog);
			stepMap.put(stepLog.getName(), stepLog);
		}
	}

	void calcGoals() {
		SectionGoals goals = new SectionGoals(testAttribute);

		for (String stepName : stepMap.keySet()) {
			goals.stepGoals.add(stepMap.get(stepName).getGoals());
		}

		sectionGoals = goals;
	}

	public OMElement getLog() { 
		return log;
	}

	void parseFatalError() {
		OMElement ele = MetadataSupport.firstChildWithLocalName(log, "FatalError");
		if (ele == null)
			return;
		fatalError = ele.getText();
	}

	void parseReports() {
		reports = new ArrayList<Report>();

		for (int i=0; i<steps.size(); i++) {
			TestStepLogContent log = null;
			try {
				log = getTestStepLog(i);
			} catch (Exception e) {
				Report err = new Report();
				err.name = "Error getting report from step " + i;
				reports.add(err);
			}
			OMElement sectionReportsEle = log.getRawReports();
			if (sectionReportsEle != null) {
				try {
					List<Report> sectionReports = Report.parseReports(sectionReportsEle);
					reports.addAll(sectionReports);
				} catch (Exception e) {
					Report err = new Report();
					err.name = "Cannot parse Reports from step " + i;
					reports.add(err);
				}
			}
		}
	}

	void parseStatus() throws Exception {
		String stat = log.getAttributeValue(new QName("status"));
		if (stat == null)
			throw new Exception("Log file status not available. Log element is " + log.getLocalName());
		if (stat.equals("Pass"))
			success = true;
		else
			success = false;
	}

	void parseTest() {
		try {
			testAttribute = MetadataSupport.firstChildWithLocalName(log, "Test").getText();
			if (testAttribute == null) {
				test = null;
				section = null;
			} else {
				String[] parts = testAttribute.split("/");
				if (parts.length == 2) {
					test = parts[0];
					section = parts[1];
				} else {
					test = testAttribute;
					section = null;
				}
			}
		} catch (Exception e) {
			testAttribute = "Unknown";
		}
	}

	// Everything after this can be called after this instance is retrieved from storage


	public List<TestStepLogContent> getStepLogs() throws Exception {
		//		parseTestSteps();
		return steps;
	}


	public SectionGoals getGoals() {
		return sectionGoals;
	}

	public TestStepLogContent getStepLog(String stepName) {
		return stepMap.get(stepName);
	}

	//	public Metadata getAllMetadata()  {
	//		Metadata m = new Metadata();
	//
	//		for (TestStepLog step : steps) {
	//			try {
	//				Metadata m1 = step.getMetadata();
	//				m.addMetadata(m1);
	//			} catch (Exception e) {}
	//		}
	//
	//		return m;
	//	}

	String firstNChars(String s, int n) {
		if (s.length() > n) 
			return s.substring(0, n);
		return s;
	}

	public String getFatalError() {
		return fatalError;
	}

	public boolean hasFatalError() {
		return getFatalError() != null;
	}

	public boolean isSuccess() {
		return success;
	}

	public List<Report> getReports()  {
		return reports;
	}
	
	public TestStepLogContent getTestStepLog(int index) throws XdsInternalException {
		if (index >= steps.size())
			throw new XdsInternalException("Step index " + index + " is illegal, there are " + steps.size() + " steps");
		return steps.get(index);
	}

	public List<String> getAssertionErrors(int index) throws XdsInternalException {
		return getTestStepLog(index).getAssertionErrors();
	}

	public List<String> getSoapFaults(int index) throws XdsInternalException {
		return getTestStepLog(index).getSoapFaults();
	}

	public RegistryResponseLog getUnexpectedErrors(int step) throws Exception {
		if (steps == null || step < 0 || step >= steps.size())
			throw new Exception("LogFile#getUnexpectedErrors: step index " + step + " does not exist");
		return steps.get(step).getUnexpectedErrors();
	}

	public String stepName(int step) throws Exception {
		if (step < 0 || step >= steps.size())
			throw new Exception("LogFile#stepName: step index " + step + " does not exist");
		return steps.get(step).id;
	}

	public int size() {
		return steps.size();
	}

	public boolean hasStep(String stepname) {
		for (int i=0; i<size(); i++) {
			if (stepname.equals(steps.get(i).id))
				return true;
		}
		return false;
	}

	public String getTestAttribute() {
		return testAttribute;
	}

	public String getTest() {
		return test;
	}


}
