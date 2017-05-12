package gov.nist.toolkit.valregmsg.validation.engine;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.gwt.GwtErrorRecorder;
import gov.nist.toolkit.errorrecording.gwt.GwtErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.gwt.client.GwtValidatorErrorItem;
import gov.nist.toolkit.errorrecording.IErrorRecorderBuilder;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.valregmsg.message.HttpMessageValidator;
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

public class ValidateMessageService extends CommonService {
	RegistryValidationInterface rvi;

	public ValidateMessageService(RegistryValidationInterface rvi) {
	this.rvi = rvi;
	}
	
	/**
     * Starts the validation/simulator process by pulling the HTTP wrapper from the db, creating a validation engine if necessary, 
     * and starting an HTTP validator. It returns the validation engine. Remember that the basic abstract
     * Simulator class inherits directly from the abstract MessageValidator class.
	 * @param vc
	 * @param db
	 * @param mvc
	 * @return
	 * @throws IOException
	 */
	public MessageValidatorEngine runValidation(ValidationContext vc, SimDb db, MessageValidatorEngine mvc, IErrorRecorderBuilder gerb) throws IOException {
		return runValidation(vc, db.getRequestMessageHeader(), db.getRequestMessageBody(), mvc, gerb);
	}

	public MessageValidatorEngine runValidation(ValidationContext vc, String httpMsgHdr, byte[] httpMsgBody, MessageValidatorEngine mvc, IErrorRecorderBuilder gerb) throws IOException {

		if (mvc == null)
			mvc = new MessageValidatorEngine();
		HttpMessageValidator val = new HttpMessageValidator(vc, httpMsgHdr, httpMsgBody, gerb, mvc, rvi);
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
				List<GwtValidatorErrorItem> errs = ger.getValidatorErrorItems();
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
	
	List<GwtValidatorErrorItem> buildValidationSummary(ValidationContext vc, MessageValidatorEngine mvc) {
		List<GwtValidatorErrorItem> info = new ArrayList<GwtValidatorErrorItem>();
		
		GwtValidatorErrorItem vei = new GwtValidatorErrorItem();
		vei.msg = "Validation Context: " + vc.toString(); 
		vei.level = GwtValidatorErrorItem.ReportingLevel.DETAIL;
		info.add(vei);
		
		for (int i=0; i<mvc.getValidationStepCount(); i++) {
			ValidationStep vs = mvc.getValidationStep(i);
			vei = new GwtValidatorErrorItem();
			vei.msg = vs.getStepName();
			vei.level = GwtValidatorErrorItem.ReportingLevel.DETAIL;
			info.add(vei);
		}
		return info;
	}
		
	
}
