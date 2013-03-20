package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.MessageValidatorFactory2.MessageValidatorFactoryFactory;
import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simDb.SimDb;
import gov.nist.toolkit.valdirfactory.DirectMessageValidatorFactory;
import gov.nist.toolkit.valregmsg.message.HttpMessageValidator;
import gov.nist.toolkit.valregmsg.message.MetadataMessageValidator;
import gov.nist.toolkit.valregmsg.validation.factories.MessageValidatorFactory;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine.ValidationStep;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorder;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorderBuilder;
import gov.nist.toolkit.valsupport.message.MessageValidator;
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
public class ValidateMessageService extends CommonServiceManager {
	RegistryValidationInterface rvi;
	Session session;
	
	public ValidateMessageService(Session session, RegistryValidationInterface rvi) {
		this.session = session;
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
	public MessageValidatorEngine runValidation(ValidationContext vc, SimDb db, MessageValidatorEngine mvc) throws IOException {
		String httpMsgHdr = db.getRequestMessageHeader(); 
		byte[] httpMsgBody = db.getRequestMessageBody();
		GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();
		
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
				List<ValidatorErrorItem> errs = ger.getValidatorErrorInfo();
				mvr.addResult(vs.getStepName(), errs);
			} catch (Exception e) {}
		}

		return mvr;

	}
	
	/**
	 * A wrapper for runValidation that starts with the context and the simulator
	 * input filename
	 * @param vc validation context
	 * @param simFileName base filename for the simulator db entry
	 * @return
	 */
	public MessageValidationResults validateMessageFile(ValidationContext vc, String simFileName) {
		try {
			SimDb sdb = SimManager.get(session.id()).getSimDb(session.getDefaultSimId());
			
			sdb.setFileNameBase(simFileName);
			
			if (vc.isMessageTypeKnown())
				vc.updateable = false;

			MessageValidatorEngine mvc = runValidation(vc, sdb, null);
						
			MessageValidator mv = mvc.findMessageValidator("MetadataMessageValidator");
			if (mv != null) {
				MetadataMessageValidator mmv = (MetadataMessageValidator) mv;
				session.setLastMetadata(mmv.getMetadata());
			}

			MessageValidationResults mvr = getMessageValidationResults(mvc);
			
			// Add a summary as if it were the result of its own step
			mvr.addResult("Validation Summary", buildValidationSummary(vc, mvc));

			return mvr;
		} catch (RuntimeException e) {
			MessageValidationResults mvr = new MessageValidationResults();
			if (e.getMessage() == null) {
				mvr.addError(XdsErrorCode.Code.NoCode, "Exception", ExceptionUtil.exception_details(e));
			} else {
				mvr.addError(XdsErrorCode.Code.NoCode, "Exception", e.getMessage());
			}
			return mvr;
		} catch (IOException e) {
			MessageValidationResults mvr = new MessageValidationResults();
			if (e.getMessage() == null) {
				mvr.addError(XdsErrorCode.Code.NoCode, "Exception", ExceptionUtil.exception_details(e));
			} else {
				mvr.addError(XdsErrorCode.Code.NoCode, "Exception", e.getMessage());
			}
			return mvr;
		}
		
	}

	public MessageValidationResults validateLastUpload(ValidationContext vc) {
		byte[] message = session.getlastUpload();
		byte[] input2 = session.getlastUpload2();
		vc.privKeyPassword = session.getPassword2();
		GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();
		if (input2 != null && input2.length <= 2) {
			// input looks like empty file name
			input2 = null;
		}
		return runValidation(vc, session, message, input2, gerb);
	}

	public MessageValidationResults runValidation(ValidationContext vc,
			Session session, byte[] message, byte[] input2, GwtErrorRecorderBuilder gerb) {
		try {
			MessageValidationResults mvr = new MessageValidationResults();
			
			if (message == null) {
				mvr.addError(XdsErrorCode.Code.NoCode, "Upload", "Upload is null");
				return mvr;
			}
			// This validation moved to MessageValidatorFactory#getValidatorForDirect
//			if (input2 == null) {
//				mvr.addError(XdsErrorCode.Code.NoCode, "Upload", "Second upload is null");
//				return mvr;
//			}

			if (vc.isMessageTypeKnown())
				vc.updateable = false;

			MessageValidatorEngine mvc;
			
			// rolls over to MessageValidatorFactory if necessary
			DirectMessageValidatorFactory factory = new DirectMessageValidatorFactory();
			
			mvc = factory.getValidator(gerb, message, input2, vc, rvi);
			mvc.run();
			
			MessageValidator mv = mvc.findMessageValidator("MetadataMessageValidator");
			if (mv != null) {
				MetadataMessageValidator mmv = (MetadataMessageValidator) mv;
				if (session != null)
					session.setLastMetadata(mmv.getMetadata());
			}

			for (int step=0; step<mvc.getValidationStepCount(); step++) {
				ValidationStep vs = mvc.getValidationStep(step);
				GwtErrorRecorder ger = (GwtErrorRecorder) vs.getErrorRecorder();
				List<ValidatorErrorItem> errs = ger.getValidatorErrorInfo();
				mvr.addResult(vs.getStepName(), errs);
			}
			
			mvr.addResult("Validation Summary", buildValidationSummary(vc, mvc));

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
