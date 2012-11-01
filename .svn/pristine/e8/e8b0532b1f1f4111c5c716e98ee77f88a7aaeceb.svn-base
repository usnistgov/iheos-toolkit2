/*
 * SchemaValidation.java
 */

package gov.nist.toolkit.utilities.xml;

import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;

import org.apache.axiom.om.OMElement;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SchemaValidation extends MetadataTypes {
	
	static public String toolkitSchemaLocation = null;

	public static String validate(OMElement ele, int metadataType)  throws XdsInternalException {
		return validate_local(ele, metadataType);
	}

	// The only known use case for localhost validation failing is when this is called from
	// xdstest2 in which case it is trying to call home to reference the schema files.
	// What is really needed is a configuration parm that points the reference to the local filesystem
	// and include the schema files in the xdstest2tool environment.

	// port 80 does not exist for requests on-machine (on the server). only requests coming in from
	// off-machine go through the firewall where the port translation happens.

	// even though this says validate_local, it is used by all requests
	public static String validate_local(OMElement ele, int metadataType)  throws XdsInternalException {
		String msg;
		
		// This should cover all use cases except xdstest2 running on a users desktop
		msg = SchemaValidation.run(ele.toString(), metadataType, "localhost", "9080");
		if (msg.indexOf("Failed to read schema document") == -1) 
			return msg;
		
		// Xdstest2 needs to reference the public register server
		// port 80 makes it easier when strict firewalls are in place
		msg = SchemaValidation.run(ele.toString(), metadataType, "129.6.24.109", "80");
		return msg;
	}


	// empty string as result means no errors
	static private String run(String metadata, int metadataType, String host, String portString) throws XdsInternalException {

		MyErrorHandler errors = null;
		DOMParser p = null;
		//String portString = "9080";
		String localSchema = System.getenv("XDSSchemaDir");
		if (localSchema == null)
			localSchema = System.getProperty("XDSSchemaDir");
		if (localSchema == null)
			localSchema = SchemaValidation.toolkitSchemaLocation;
		
		System.out.println("Local Schema to be found at " + localSchema);
		
		boolean noRim = false;
		
		// Decode schema location
		String schemaLocation;
		switch (metadataType) {
		case METADATA_TYPE_Rb:
			schemaLocation = "urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0 " + 
			((localSchema == null) ? 
					"http://" + host + ":" + portString + "/xdsref/schema/v3/lcm.xsd" :
					localSchema + "/v3/lcm.xsd");
			break;
		case METADATA_TYPE_PR:
		case METADATA_TYPE_R:
			schemaLocation = "urn:oasis:names:tc:ebxml-regrep:registry:xsd:2.1 " +
			((localSchema == null) ?
			"http://" + host + ":" + portString + "/xdsref/schema/v2/rs.xsd" :
			localSchema + "/v2/rs.xsd");
			break;
		case METADATA_TYPE_REGISTRY_RESPONSE3:
			schemaLocation = "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0 " +
			((localSchema == null) ?
			"http://" + host + ":" + portString + "/xdsref/schema/v3/rs.xsd" :
			localSchema + "/v3/rs.xsd");
			break;
		case METADATA_TYPE_Q:
			schemaLocation =
			"urn:oasis:names:tc:ebxml-regrep:query:xsd:2.1 " +
			((localSchema == null) ?
			"http://" + host + ":" + portString + "/xdsref/schema/v2/query.xsd " :
			localSchema + "/v2/query.xsd "	) + 
			
			"urn:oasis:names:tc:ebxml-regrep:registry:xsd:2.1 " +
			((localSchema == null) ?
			"http://" + host + ":" + portString + "/xdsref/schema/v2/rs.xsd" :
			localSchema + "/v2/rs.xsd" ) ;
			
			break;
		case METADATA_TYPE_SQ:
			schemaLocation = "urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0 " + 
			((localSchema == null) ?
			"http://" + host + ":" + portString + "/xdsref/schema/v3/query.xsd " : 
			localSchema + "/v3/query.xsd "  ) +
			
			"urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0 " + 
			((localSchema == null) ?
			"http://" + host + ":" + portString + "/xdsref/schema/v3/rs.xsd" :
			localSchema + "/v3/rs.xsd" );
			break;
		case METADATA_TYPE_EPSOS:
			schemaLocation = "urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0 " + 
			((localSchema == null) ?
			"http://" + host + ":" + portString + "/xdsref/schema/epsos/query.xsd " : 
			localSchema + "/epsos/query.xsd "  ) 
			
//			"urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0 " + 
//			((localSchema == null) ?
//			"http://" + host + ":" + portString + "/xdsref/schema/epsos/rs.xsd " :
//			localSchema + "/epsos/rs.xsd " ) 

			
//			+ 
//			((localSchema == null) ?
//					"http://" + host + ":" + portString + "/xdsref/schema/epsos/rimext.xsd " :
//					localSchema + "/epsos/rimext.xsd " ) 
			;
			
			noRim = true;
			break;
		case METADATA_TYPE_PRb:
		case METADATA_TYPE_RET:
			schemaLocation = "urn:ihe:iti:xds-b:2007 " + 
			((localSchema == null) ?
			"http://" + host + ":" + portString + "/xdsref/schema/v3/XDS.b_DocumentRepository.xsd " :
				localSchema + "/v3/XDS.b_DocumentRepository.xsd ") +
			
			"urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0 " + 
			((localSchema == null) ?
			"http://" + host + ":" + portString + "/xdsref/schema/v3/rs.xsd" :
			localSchema + "/v3/rs.xsd"	);
			break;
		case AUDIT_LOG:
			schemaLocation = "noNamespaceSchemaLocation " + 
			((localSchema == null) ?
			"http://" + host + ":" + portString + "/xdsref/schema/audit/healthcare-security-audit.xsd " :
				localSchema + "/audit/healthcare-security-audit.xsd ");
			break;
		default:
			throw new XdsInternalException("SchemaValidation: invalid metadata type = " + metadataType);
		}

		if (noRim == false) {
		schemaLocation += " urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0 " + 
		((localSchema == null) ?
		"http://" + host + ":" + portString + "/xdsref/schema/v3/rim.xsd" :
			localSchema + 	"/v3/rim.xsd");

		schemaLocation += " http://schemas.xmlsoap.org/soap/envelope/ " + 
		((localSchema == null) ?
		"http://" + host + ":" + portString + "/xdsref/schema/v3/soap.xsd" :
			localSchema + 	"/v3/soap.xsd");

		schemaLocation += " http://docs.oasis-open.org/wsn/b-2 " + 
		((localSchema == null) ?
		"http://" + host + ":" + portString + "/xdsref/schema/wsn/b-2.xsd" :
			localSchema + 	"/wsn/b-2.xsd");

		schemaLocation += " http://docs.oasis-open.org/wsn/br-2 " + 
		((localSchema == null) ?
		"http://" + host + ":" + portString + "/xdsref/schema/wsn/br-2.xsd" :
			localSchema + 	"/wsn/br-2.xsd");

		schemaLocation += " http://docs.oasis-open.org/wsn/t-1 " + 
		((localSchema == null) ?
		"http://" + host + ":" + portString + "/xdsref/schema/wsn/t-1.xsd" :
			localSchema + 	"/wsn/t-1.xsd");
		}

		// build parse to do schema validation
		try {
			p=new DOMParser();
		} catch (Exception e) {
			throw new XdsInternalException("DOMParser failed: " + e.getMessage());
		}
		try {        
			p.setFeature( "http://xml.org/sax/features/validation", true );
			p.setFeature("http://apache.org/xml/features/validation/schema", true);
			p.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation",
					schemaLocation);
			errors = new MyErrorHandler();
			errors.setSchemaFile(schemaLocation);
			p.setErrorHandler( errors );
		} catch (SAXException e) {
			throw new XdsInternalException("SchemaValidation: error in setting up parser property: SAXException thrown with message: " 
					+ e.getMessage());             
		}

		// run parser and collect parser and schema errors
		try {
			// translate urn:uuid: to urn_uuid_ since the colons really screw up schema stuff
			String metadata2 = metadata.replaceAll("urn:uuid:", "urn_uuid_");
			InputSource is = new InputSource(new StringReader(metadata2));
			p.parse(is);
		} catch (Exception e) {
			throw new XdsInternalException("SchemaValidation: XML parser/Schema validation error: " + 
					exception_details(e));
		}
		String errs = errors.getErrors();
//		if (errs.length() != 0) {
//		errs = errs + "\n" + metadata.substring(1,500);
//		}
		return errs;

	}
	protected static String exception_details(Exception e) {
		if (e == null)
			return "No stack trace available";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);

		return "Exception thrown: " + e.getClass().getName() + "\n" + e.getMessage() + "\n" + new String(baos.toByteArray());
	}

	static public void main(String[] args) {
		String x = "Documents and Settings\\foo\\bar";
		System.out.println(x);
			x = x.replace(" ", "\\ ");
		System.out.println(x);
	}

}