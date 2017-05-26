package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;

import org.apache.axiom.om.OMElement;

/**
 * Validate a Query Response message.
 * @author bill
 *
 */
public class QueryResponseValidator extends AbstractMessageValidator {
	OMElement xml;

	public QueryResponseValidator(ValidationContext vc, OMElement xml) {
		super(vc);
		this.xml = xml;
	}

	public void run(IErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		//er.registerValidator(this);
		//er.unRegisterValidator(this);

	}

}
