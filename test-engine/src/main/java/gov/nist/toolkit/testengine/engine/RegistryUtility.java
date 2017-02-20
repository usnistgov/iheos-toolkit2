package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.RegistryErrorListGenerator;
import gov.nist.toolkit.valregmsg.message.SchemaValidation;
import gov.nist.toolkit.valregmetadata.top.MetadataValidator;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.SchemaValidationException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.axiom.om.OMElement;


public class RegistryUtility {

	static public void schema_validate_local(OMElement ahqr, int metadata_type)
	throws XdsInternalException, SchemaValidationException {
		String schema_messages = null;
		try {
			schema_messages = SchemaValidation.validate(ahqr, metadata_type);
		} catch (Exception e) {
			throw new XdsInternalException("Schema Validation threw internal error: " + e.getMessage());
		}
		if (schema_messages != null && schema_messages.length() > 0) {
			// System.out.println("SchemaValidationException at RegistryUtility:28");
			throw new SchemaValidationException("Input did not validate against schema:" + schema_messages);
		}
	}

	// the mapping from the call parameters into the ValidationContext is approximate at best. 
	// This method needs new interface
//	static public RegistryErrorList metadata_validator(Metadata m, boolean is_submit, boolean isPnR) throws XdsException {
//		RegistryErrorList rel = new RegistryErrorList((m.isVersion2() ? RegistryErrorList.version_2 : RegistryErrorList.version_3));
//		ValidationContext vc = new ValidationContext();
//		
//		vc.isPnR = isPnR;
//		if (is_submit)
//			vc.isRequest = true;
//		else
//			vc.isResponse = true;
//		if (! isPnR)
//			vc.isR = true;
//		MetadataValidator mv = new MetadataValidator(m, vc);
//		mv.run(rel);
//		return rel;
//	}
	
	static public RegistryErrorListGenerator metadata_validator(Metadata m, ValidationContext vc) throws XdsInternalException {
		RegistryErrorListGenerator rel = new RegistryErrorListGenerator((m.isVersion2() ? RegistryErrorListGenerator.version_2 : RegistryErrorListGenerator.version_3));
		MetadataValidator mv = new MetadataValidator(m, vc, null);
		mv.run(rel);
		return rel;
	}

	public static String exception_details(Exception e) {
		if (e == null) 
			return "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);

		return "Exception thrown: " + e.getClass().getName() + "\n" + e.getMessage() + "\n" + new String(baos.toByteArray());
	}
	
	public static String exception_trace(Exception e) {
		if (e == null) 
			return "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		StackTraceElement ste[] = e.getStackTrace();
		ps.print("\n");
		for (int i=0; i<ste.length && i<15; i++)
			ps.print("\t" + ste[i].toString() + "\n");
		//e.printStackTrace(ps);

		return new String(baos.toByteArray());
	}

}
