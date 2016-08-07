package gov.nist.toolkit.testenginelogging.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestStepLogContentDTO implements Serializable, IsSerializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 2676088682465214583L;
	private String id;
	private boolean success;
	private boolean expectedSuccess;
	private boolean expectedWarning;
	private StepGoalsDTO stepGoalsDTO;
	private String endpoint;
	private List<String> errors = new ArrayList<>();
	private List<String> details = new ArrayList<>();
	private List<ReportDTO> reportDTOs = new ArrayList<>();
	private List<String> reportsSummary = new ArrayList<>();
	private List<UseReportDTO> useReports = new ArrayList<>();
	private Map<String, String> assignedIds = new HashMap<>();
	private Map<String, String> assignedUids = new HashMap<>();
	private List<String> soapFaults = new ArrayList<>();
	private List<String> assertionErrors = new ArrayList<>();
	private String inputMetadata;
	private String result;
	private String inHeader = null;
	private String outHeader = null;
	private String rootString;


	public StepGoalsDTO getStepGoalsDTO() {
		return stepGoalsDTO;
	}

	public boolean getStatus() {
		return success;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public List<String> getErrors()  {
		return errors;
	}

    public List<String> getReportsSummary() {
		return reportsSummary;
	}

    public List<String> getDetails() {
		return details;
	}

	public String getInputMetadata() {
		return inputMetadata;
	}

	public String getResult() {
		return result;
	}

	public String getInHeader() {
		return inHeader;
	}

	public String getOutHeader() {
		return outHeader;
	}

	public String getRoot() {
		return rootString;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

    public Map<String, String> getAssignedIds() {
        return assignedIds;
    }

    public Map<String, String> getAssignedUids() {
        return assignedUids;
    }

	public boolean isExpectedSuccess() {
		return expectedSuccess;
	}

	public void setExpectedSuccess(boolean expectedSuccess) {
		this.expectedSuccess = expectedSuccess;
	}

	public boolean isExpectedWarning() {
		return expectedWarning;
	}

	public void setExpectedWarning(boolean expectedWarning) {
		this.expectedWarning = expectedWarning;
	}

	public void setStepGoalsDTO(StepGoalsDTO stepGoalsDTO) {
		this.stepGoalsDTO = stepGoalsDTO;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setDetails(List<String> details) {
		this.details = details;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public void setInHeader(String inHeader) {
		this.inHeader = inHeader;
	}

	public void setOutHeader(String outHeader) {
		this.outHeader = outHeader;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setInputMetadata(String inputMetadata) {
		this.inputMetadata = inputMetadata;
	}

	public String getRootString() {
		return rootString;
	}

	public void setRootString(String rootString) {
		this.rootString = rootString;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getSoapFaults() {
		return soapFaults;
	}

	public void setSoapFaults(List<String> soapFaults) {
		this.soapFaults = soapFaults;
	}

	public List<String> getAssertionErrors() {
		return assertionErrors;
	}

	public void setAssertionErrors(List<String> assertionErrors) {
		this.assertionErrors = assertionErrors;
	}

	public List<ReportDTO> getReportDTOs() {
		return reportDTOs;
	}

	public void addReportDTO(ReportDTO report) {
		reportDTOs.add(report);
	}

	public void setReportDTOs(List<ReportDTO> reportDTOs) {
		this.reportDTOs = reportDTOs;
	}

	public List<UseReportDTO> getUseReports() {
		return useReports;
	}

	public void addUseReport(UseReportDTO useReport) {
		this.useReports.add(useReport);
	}


}
