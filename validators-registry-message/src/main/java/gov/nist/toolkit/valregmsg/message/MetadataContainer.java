package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;

/**
 * Empty shell of a validator that holds a copy of the pre-parsed 
 * metadata so other validators can find it later.
 * @author bill
 *   
 */
public class MetadataContainer  extends AbstractMessageValidator {
	Metadata m;
	
	public MetadataContainer(ValidationContext vc, Metadata m) {
		super(vc);
		this.m = m;
	}

	public void run(IErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		//er.registerValidator(this);
		//er.unRegisterValidator(this);
		
	}
	
	public Metadata getMetadata() {
		return m;
	}

}
