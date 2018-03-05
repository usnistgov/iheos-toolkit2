package gov.nist.toolkit.validatorsSoapMessage.engine;

import gov.nist.toolkit.errorrecording.GwtErrorRecorder;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.validatorsSoapMessage.message.HttpMessageValidator;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.engine.ValidationStep;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This wraps a bunch of MessageValidator tools for use with Simulators.
 * @author bill
 *
 */

public class ValidateMessageService {
	RegistryValidationInterface rvi;
	private HttpMessageValidator val = null;
	
	public HttpMessageValidator getHttpMessageValidator() {
	   return val;
	}

	public ValidateMessageService(RegistryValidationInterface rvi) {
	this.rvi = rvi;
	}
	

	public MessageValidatorEngine runValidation(ValidationContext vc, String httpMsgHdr, byte[] httpMsgBody, MessageValidatorEngine mvc, ErrorRecorderBuilder gerb, TestSession testSession) throws IOException {

		if (mvc == null)
			mvc = new MessageValidatorEngine();
		val = new HttpMessageValidator(vc, httpMsgHdr, httpMsgBody, gerb, mvc, rvi, testSession);
		mvc.addMessageValidator("Parse HTTP Message", val, gerb.buildNewErrorRecorder());
		mvc.run();

		return mvc;
	}

	/**
	 * Collect error information from all steps in the message validator engine.
	 * @param mvc
	 * @return
	 */
	public MessageValidationResults getMessageValidationResults(MessageValidatorEngine mvc) {
		MessageValidationResults mvr = new MessageValidationResults();
		for (int step=0; step<mvc.getValidationStepCount(); step++) {
			try {
				ValidationStep vs = mvc.getValidationStep(step);
				GwtErrorRecorder ger = (GwtErrorRecorder) vs.getErrorRecorder();
				List<ValidatorErrorItem> errs = ger.getValidatorErrorItems();
				mvr.addResult(vs.getStepName(), errs);
				mvr.addSummary(vs.getStepName(), ger.getSummaryErrorInfo());
			} catch (Exception e) {}
		}

		return mvr;

	}
	
	public MessageValidationResults runValidation(ValidationContext vc,
			byte[] message, byte[] input2, GwtErrorRecorderBuilder gerb) {
		try {
			MessageValidationResults mvr = new MessageValidationResults();
			
			if (message == null) {
				mvr.addError(XdsErrorCode.Code.NoCode, "Upload", "Upload is null");
				return mvr;
			}

			if (vc.isMessageTypeKnown())
				vc.updateable = false;

			MessageValidatorEngine mvc;
			return mvr;
		} catch (RuntimeException e) {
			MessageValidationResults mvr = new MessageValidationResults();
			if (e.getMessage() == null) {
				mvr.addError(XdsErrorCode.Code.NoCode, "Exception", ExceptionUtil.exception_details(e));
			} else {
				mvr.addError(XdsErrorCode.Code.NoCode, "Exception", e.getMessage());
			}
			return mvr;
		}
	}
	
	List<ValidatorErrorItem> buildValidationSummary(ValidationContext vc, MessageValidatorEngine mvc) {
		List<ValidatorErrorItem> info = new ArrayList<ValidatorErrorItem>();
		
		ValidatorErrorItem vei = new ValidatorErrorItem();
		vei.msg = "Validation Context: " + vc.toString(); 
		vei.level = ValidatorErrorItem.ReportingLevel.DETAIL;
		info.add(vei);
		
		for (int i=0; i<mvc.getValidationStepCount(); i++) {
			ValidationStep vs = mvc.getValidationStep(i);
			vei = new ValidatorErrorItem();
			vei.msg = vs.getStepName();
			vei.level = ValidatorErrorItem.ReportingLevel.DETAIL;
			info.add(vei);
		}
		return info;
	}
		
	
}
