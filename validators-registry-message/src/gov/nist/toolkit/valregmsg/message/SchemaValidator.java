package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.utilities.xml.SchemaValidation;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;
import gov.nist.toolkit.xdsexception.SchemaValidationException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import org.apache.axiom.om.OMElement;

/**
 * Run XML Schema validation on the provided XML. 
 * @author bill
 *
 */
public class SchemaValidator extends MessageValidator {
	OMElement xml;

	public SchemaValidator(ValidationContext vc, OMElement xml) {
		super(vc);
		this.xml = xml;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		
		int schemaValidationType = vc.getSchemaValidationType();
		
		er.detail("Metadata Type (for selecting Schema) is " + vc.getSchemaValidationTypeName(schemaValidationType));
		
		try {
			schema_validate_local(xml, schemaValidationType);
		} catch (Exception e) {
			er.err(XdsErrorCode.Code.XDSRegistryError, e.getMessage(), this, "Schema");
		}

	}
	
	void schema_validate_local(OMElement ahqr, int metadata_type)
	throws XdsInternalException, SchemaValidationException {
		String schema_messages = null;
		try {
			schema_messages = SchemaValidation.validate_local(ahqr, metadata_type);
		} catch (Exception e) {
			throw new XdsInternalException("Schema Validation threw internal error: " + e.getMessage());
		}
		if (schema_messages != null && schema_messages.length() > 0)
			throw new SchemaValidationException("Input did not validate against schema:" + schema_messages);
	}


}
