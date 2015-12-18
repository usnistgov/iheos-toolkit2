/*
 * SchemaValidation.java
 */

package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.utilities.xml.MyErrorHandler;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;

public class SchemaValidation extends MetadataTypes {
	static Logger logger = Logger.getLogger(SchemaValidation.class);

	static public String toolkitSchemaLocation = null;

	public static String validate(OMElement ele, int metadataType)  throws XdsInternalException {
		return validate(ele.toString(), metadataType);
	}

	// empty string as result means no errors
	static private String validate(String metadata, int metadataType) throws XdsInternalException {
        String localSchema = Installation.installation().schemaFile().toString();
		localSchema = localSchema.replaceAll(" ", "%20");
		MyErrorHandler errors = null;
		DOMParser p = null;

		logger.debug("Local Schema to be found at " + localSchema);
		String host = "";
        String portString = "";
		boolean noRim = false;
		
		// Decode schema location
		String schemaLocation;
		switch (metadataType) {
		case METADATA_TYPE_Rb:
        case METADATA_TYPE_RODDE:
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