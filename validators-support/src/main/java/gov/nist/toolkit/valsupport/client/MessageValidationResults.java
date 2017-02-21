package gov.nist.toolkit.valsupport.client;

import gov.nist.toolkit.errorrecording.gwt.client.GWTValidationStepResult;
import gov.nist.toolkit.errorrecording.gwt.client.GwtValidatorErrorItem;
import gov.nist.toolkit.errorrecording.gwt.client.GwtValidatorErrorItem.ReportingLevel;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode.Code;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A collection of error/statuses/messages for a collection of validation steps.
 * @author bill
 *
 */
public class MessageValidationResults implements IsSerializable {
	
	List<GWTValidationStepResult> results = new ArrayList<GWTValidationStepResult>();
	List<GWTValidationStepResult> summary = new ArrayList<GWTValidationStepResult>();
	String htmlResults = "";
	
	public MessageValidationResults() {} // For GWT
	
	/**
	 * Add results for a validation step
	 * @param stepName the step
	 * @param er the results
	 */
	public void addResult(String stepName, List<GwtValidatorErrorItem> er) {
		GWTValidationStepResult result = new GWTValidationStepResult();
		result.stepName = stepName;
		result.er = er;
		results.add(result);
	}
	
	public void addSummary(String stepName, List<GwtValidatorErrorItem> er) {
		GWTValidationStepResult result = new GWTValidationStepResult();
		result.stepName = stepName;
		result.er = er;
		summary.add(result);
	}
	
	public void addHtmlResults(String htmlResults) {
		this.htmlResults = htmlResults;
	}
	
	public String getHtmlResults() {
		return this.htmlResults;
	}
	
	public List<GWTValidationStepResult> getResults() {
		return results;
	}
	
	public List<GWTValidationStepResult> getSummaryResults() {
		return summary;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		for (GWTValidationStepResult result : results)
			buf.append(result);
		
		return buf.toString();
	}
	
	/**
	 * Does any step declare errors?
	 * @return
	 */
	public boolean hasErrors() {
		for (GWTValidationStepResult result : results) {
			for (GwtValidatorErrorItem info : result.er) {
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
		GWTValidationStepResult result = new GWTValidationStepResult();
		result.stepName = stepName;
		GwtValidatorErrorItem v = new GwtValidatorErrorItem();
		v.level = GwtValidatorErrorItem.ReportingLevel.ERROR;
		v.msg = msg;
		v.setCode(code);
		result.er = new ArrayList<GwtValidatorErrorItem>();
		result.er.add(v);
		results.add(result);
	}
	

}
