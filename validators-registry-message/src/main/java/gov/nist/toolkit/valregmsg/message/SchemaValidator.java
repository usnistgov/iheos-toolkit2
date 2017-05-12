package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import gov.nist.toolkit.xdsexception.SchemaValidationException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import org.apache.axiom.om.OMElement;

/**
 * Run XML Schema validation on the provided XML. 
 * @author bill
 *
 */
public class SchemaValidator extends AbstractMessageValidator {
	OMElement xml;

	public SchemaValidator(ValidationContext vc, OMElement xml) {
		super(vc);
		this.xml = xml;
	}

	public void run(IErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		er.registerValidator(this);
		
		int schemaValidationType = vc.getSchemaValidationType();
		
		er.detail("Metadata Type (for selecting Schema) is " + vc.getSchemaValidationTypeName(schemaValidationType));
		
		try {
			schema_validate_local(xml, schemaValidationType);
		} catch (Exception e) {
			er.err(XdsErrorCode.Code.XDSRegistryError, e.getMessage(), this, "Schema");
		}
        finally {
            er.unRegisterValidator(this);
        }

	}
	
	void schema_validate_local(OMElement ahqr, int metadata_type)
	throws XdsInternalException {
		String schema_messages = null;
		try {
			schema_messages = SchemaValidation.validate(ahqr, metadata_type);
		} catch (Exception e) {
			throw new XdsInternalException("Schema Validation threw internal error: " + e.getMessage());
		}
		if (schema_messages != null && schema_messages.length() > 0) {
			throw new SchemaValidationException("Input did not validate against schema:" + schema_messages);
		}
	}


}
