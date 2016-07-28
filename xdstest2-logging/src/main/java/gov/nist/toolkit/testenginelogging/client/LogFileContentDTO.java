package gov.nist.toolkit.testenginelogging.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Object version of log.xml content.
 * @author bill
 *
 */
public class LogFileContentDTO implements Serializable, IsSerializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -2046605414265224604L;
	private boolean success;
	private List<TestStepLogContentDTO> steps = new ArrayList<>();
	private Map<String, TestStepLogContentDTO> stepMap = new HashMap<>();
	private String testAttribute;
	private String test = null;
	private String section = null;
	private SectionGoalsDTO sectionGoalsDTO;
	private String fatalError = null;
	private List<ReportDTO> reportDTOs = new ArrayList<>();

	public LogFileContentDTO() {}

	public Map<String, TestStepLogContentDTO> getStepMap() {
		return stepMap;
	}

	public String toString() {
		return test + "/" + section;
	}

	public String getSection() {
		return section;
	}

	// Everything after this can be called after this instance is retrieved from storage


	public List<TestStepLogContentDTO> getStepLogs() throws Exception {
		//		parseTestSteps();
		return steps;
	}


	public SectionGoalsDTO getGoals() {
		return sectionGoalsDTO;
	}

	public TestStepLogContentDTO getStepLog(String stepName) {
		return stepMap.get(stepName);
	}

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

	public List<ReportDTO> getReportDTOs()  {
		return reportDTOs;
	}

	public TestStepLogContentDTO getTestStepLog(int index) throws XdsInternalException {
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

	public String stepName(int step) throws Exception {
		if (step < 0 || step >= steps.size())
			throw new Exception("LogFile#stepName: step index " + step + " does not exist");
		return steps.get(step).getId();
	}

	public int size() {
		return steps.size();
	}

	public boolean hasStep(String stepname) {
		for (int i=0; i<size(); i++) {
			if (stepname.equals(steps.get(i).getId()))
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

//	public File getInputFile() {
//		return inputFile;
//	}
//
//	public void setInputFile(File inputFile) {
//		this.inputFile = inputFile;
//	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<TestStepLogContentDTO> getSteps() {
		return steps;
	}

	public void setSteps(List<TestStepLogContentDTO> steps) {
		this.steps = steps;
	}

	public void setStepMap(Map<String, TestStepLogContentDTO> stepMap) {
		this.stepMap = stepMap;
	}

	public void setTestAttribute(String testAttribute) {
		this.testAttribute = testAttribute;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public SectionGoalsDTO getSectionGoalsDTO() {
		return sectionGoalsDTO;
	}

	public void setSectionGoalsDTO(SectionGoalsDTO sectionGoalsDTO) {
		this.sectionGoalsDTO = sectionGoalsDTO;
	}

	public void setFatalError(String fatalError) {
		this.fatalError = fatalError;
	}

	public void setReportDTOs(List<ReportDTO> reportDTOs) {
		this.reportDTOs = reportDTOs;
	}
}
