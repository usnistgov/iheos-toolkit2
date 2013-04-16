package gov.nist.toolkit.soap.wsseToolkitAdapter;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;
import gov.nist.toolkit.soap.wsseToolkitAdapter.log4jToErrorRecorder.AppenderForErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;
import gov.nist.toolkit.wsseToolkit.api.WsseHeaderValidator;

/**
 * Temporary adapter between toolkit legacy validation code and the wsse module
 * validation code.
 * 
 * TODO: check with Bill. In my own opinion, the design of the message validator
 * interface is flawed. As a first shot and since the goal is to enforce an
 * contract, an interface Validator with a run() method seems more appropriate.
 * ValidationContext could be push as a parameter of this method.
 * 
 * NOTE : CustomLogger is a quick way to log stuff from the wsse module without
 * having to define an object model of what is "logging"!
 * 
 * TODO clarify what vc , err, mvc are doing!
 * 
 * TODO field er in MessageValidator is not initialized!
 * 
 * @author gerardin
 * 
 */

public class WsseHeaderValidatorAdapter extends MessageValidator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WsseHeaderValidatorAdapter validator = new WsseHeaderValidatorAdapter(
				new ValidationContext());
		ErrorRecorder er = new TextErrorRecorder();
		MessageValidatorEngine mvc = new MessageValidatorEngine();
		validator.run(er, mvc);
	}

	private WsseHeaderValidator val;

	public WsseHeaderValidatorAdapter(ValidationContext vc) {
		super(vc);
		val = new WsseHeaderValidator();
	}

	@Override
	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		AppenderForErrorRecorder wsseLogApp = new AppenderForErrorRecorder(vc, er, mvc);		
		org.apache.log4j.Logger log2 = org.apache.log4j.Logger.getLogger(WsseHeaderValidator.class);
		log2.addAppender(wsseLogApp);
		val.validate(null);
		er.showErrorInfo();
	}
}
