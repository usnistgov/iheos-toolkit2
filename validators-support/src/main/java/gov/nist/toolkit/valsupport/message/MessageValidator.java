package gov.nist.toolkit.valsupport.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

import org.apache.axiom.om.OMElement;

/**
 * Abstract class that all valiators are based on. It's primary
 * significance is forcing validators to support the run method so they
 * can be called when their turn comes on the validation queue.
 * @author bill
 *
 */
abstract public class MessageValidator {
	protected ValidationContext vc; 
	public ErrorRecorder er;
	
	
	abstract public void run(ErrorRecorder er, MessageValidatorEngine mvc);

	public MessageValidator(ValidationContext vc) {
		this.vc = vc;
	}
	
	public ErrorRecorder getErrorRecorder() {
		return er;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("\n\tValidationContext: ").append(vc.toString());
		
		return buf.toString();
	}
	
	public String formatObjectIdentity(OMElement ele) {
		Metadata m = new Metadata(); 
		String id = m.getId(ele);
		
		if (id != null && !id.equals(""))
			return ele.getLocalName() + "(id=" + id + ")";

		String title = m.getTitleValue(ele);
		if (title != null && !title.equals(""))
			return ele.getLocalName() + "(title=" + title + ")";
		
		return ele.getLocalName() + "(??)";
	}

}
