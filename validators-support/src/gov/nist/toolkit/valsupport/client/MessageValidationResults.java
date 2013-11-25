package gov.nist.toolkit.valsupport.client;

import gov.nist.toolkit.errorrecording.client.ValidationStepResult;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem.ReportingLevel;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A collection of error/statuses/messages for a collection of validation steps.
 * @author bill
 *
 */
public class MessageValidationResults implements IsSerializable {
	
	List<ValidationStepResult> results = new ArrayList<ValidationStepResult>();
	List<ValidationStepResult> summary = new ArrayList<ValidationStepResult>();
	String htmlResults = "";
	
	public MessageValidationResults() {} // For GWT
	
	/**
	 * Add results for a validation step
	 * @param stepName the step
	 * @param er the results
	 */
	public void addResult(String stepName, List<ValidatorErrorItem> er) {
		ValidationStepResult result = new ValidationStepResult();
		result.stepName = stepName;
		result.er = er;
		results.add(result);
	}
	
	public void addSummary(String stepName, List<ValidatorErrorItem> er) {
		ValidationStepResult result = new ValidationStepResult();
		result.stepName = stepName;
		result.er = er;
		summary.add(result);
	}
	
	public void addHtmlResults(String htmlResults) {
		if(this.htmlResults.equals("")) {
			this.htmlResults = htmlResults;
		} else {
			this.htmlResults += htmlResults;
		}
	}
	
	public String getHtmlResults() {
		return this.htmlResults;
	}
	
	public List<ValidationStepResult> getResults() {
		return results;
	}
	
	public List<ValidationStepResult> getSummaryResults() {
		return summary;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		for (ValidationStepResult result : results)
			buf.append(result);
		
		return buf.toString();
	}
	
	/**
	 * Does any step declare errors?
	 * @return
	 */
	public boolean hasErrors() {
		for (ValidationStepResult result : results) {
			for (ValidatorErrorItem info : result.er) {
				if (info.level == ReportingLevel.ERROR)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Add an error to an existing step
	 * @param code
	 * @param stepName
	 * @param msg
	 */
	public void addError(Code code, String stepName, String msg) {
		ValidationStepResult result = new ValidationStepResult();
		result.stepName = stepName;
		ValidatorErrorItem v = new ValidatorErrorItem();
		v.level = ValidatorErrorItem.ReportingLevel.ERROR;
		v.msg = msg;
		v.setCode(code);
		result.er = new ArrayList<ValidatorErrorItem>();
		result.er.add(v);
		results.add(result);
	}
	

}
