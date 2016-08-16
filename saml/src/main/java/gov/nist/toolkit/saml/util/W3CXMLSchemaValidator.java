package gov.nist.toolkit.saml.util;



import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ResourceBundle;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Class that abstracts the W3C XML Schema validation process.
 * It is an implementation of the SchemaValidatorInterface.
 * 
 * 
 * @author Srinivasarao.Eadara
 */
public class W3CXMLSchemaValidator implements SchemaValidatorInterface {
	
	private static Logger logger = Logger.getLogger(W3CXMLSchemaValidator.class);

	private static final String SCHEMA_LANGUAGE = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	private Schema schema;

	private static String defaultSchemaLocation;
	static {
		//this can later be replaced with accessing the schema via a url 
		ResourceBundle schemaResourceBundle = 
			ResourceBundle.getBundle("C:\\saml-2.0-xsd\\");
		String schemaLocation = schemaResourceBundle.getString("schemaLocation");
		setDefaultSchemaLocation("C:\\saml-2.0-xsd\\schemas\\");
	}
	
	/**
	 * @return the defaultSchemaLocation
	 */
	public static String getDefaultSchemaLocation() {
		return defaultSchemaLocation;
	}


	/**
	 * @param defaultSchemaLocation the defaultSchemaLocation to set
	 */
	public static void setDefaultSchemaLocation(String defaultSchemaLocation) {
		W3CXMLSchemaValidator.defaultSchemaLocation = defaultSchemaLocation;
	}


	/**
	 * Create a Validator instance with the default Schema as specified in schema_checking.properties file.
	 * This constructor uses CustomisePERMIS.getDefaultSchemaLocation() method to determine the path
	 * for the default schema.
	 * 
	 * @throws org.xml.sax.SAXException Thrown if parsing of the Schema file fails
	 * @throws java.io.IOException Thrown is a file operation fails
	 */
	public W3CXMLSchemaValidator() throws SAXException, IOException {
		
		SchemaFactory schemaFactory = SchemaFactory.newInstance(SCHEMA_LANGUAGE);
		String defaultSchemaLocation = getDefaultSchemaLocation();
		if (logger.isDebugEnabled()) {
			logger.debug("Found schema location " + defaultSchemaLocation);
		}
		if (new File(defaultSchemaLocation).isAbsolute()) {
			File schemaFile = new File(defaultSchemaLocation+"oasis-wss-wssecurity-secext-1.1.xsd");
			schema = schemaFactory.newSchema(new StreamSource(schemaFile));
		} else {
			InputStream schemaInputStream = getClass().getResourceAsStream(defaultSchemaLocation);
			schema = schemaFactory.newSchema(new StreamSource(schemaInputStream));
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("W3CXMLSchemaValidator succesfully constructed using "
					+ "schema " + defaultSchemaLocation);
		}
	}


	/**
	 * Create a Validator instance with the Schema represented by the given File model.
	 * 
	 * @param schemaFile File that contains the Schema to be used
	 * @throws org.xml.sax.SAXException Thrown if parsing of the Schema file fails
	 * @throws java.io.IOException Thrown is a file operation fails
	 */
	public W3CXMLSchemaValidator(File schemaFile) throws SAXException, IOException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(SCHEMA_LANGUAGE);
		schema = schemaFactory.newSchema(new StreamSource(schemaFile));
	}


	/**
	 * Get the Schema used by the Validator to check instance documents.
	 * 
	 * @return The Schema used by the Validator to check instance documents
	 */
	public Schema getSchema() {
		return schema;
	}


	/**
	 * Set the Schema used by the Validator to check instance documents.
	 * 
	 * @param schema Schema to be used for validation
	 */
	public void setSchema(Schema schema) {
		this.schema = schema;
	}


	/**
	 * Checks if a given xml string is valid according to the current schema.
	 * 
	 * @param s String to be validated
	 * @throws org.xml.sax.SAXException If parsing of the schema instance fails
	 * @throws java.io.IOException Thrown on IO error
	 */
	public void validate(String s) throws SAXException, IOException {
		validate(new StringReader(s));
	}


	/**
	 * Checks if xml content represented by a Reader is valid according to the current schema.
	 * 
	 * @param r Reader accessing the xml content
	 * @throws org.xml.sax.SAXException If parsing of the schema instance fails
	 * @throws java.io.IOException Thrown on IO error
	 */
	public void validate(Reader r) throws SAXException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("validate with Reader argument called");
		}
		schema.newValidator().validate(new StreamSource(r));
	}


	/**
	 * Checks if xml represented by a node is valid according to the current schema.
	 * 
	 * @param r XML Node that is to be validated
	 * @throws org.xml.sax.SAXException If parsing of the schema instance fails
	 * @throws java.io.IOException Thrown on IO error
	 */
	public void validate(Node r) throws SAXException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("validate with Node argument called");
		}
		schema.newValidator().validate(new DOMSource(r));
	}


	/** 
	 * Check if xml represented by a Source is valid according to the current schema.
	 * 
	 * @param source Source to be validated
	 * @throws org.xml.sax.SAXException If parsing of the schema instance fails
	 * @throws java.io.IOException Thrown on IO error
	 */
	public void validate(Source source) throws SAXException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("validate with Source argument called");
		}
		schema.newValidator().validate(source);
	}


	/**
	 * Get a meaningful report about a validation exception.
	 * The report includes complete data from the exception model.
	 *  
	 * @param ex SAXParseException to get the report for
	 * @return String report of the meaningful data contained in the exception
	 */
	public static String getExceptionDiagnostic(SAXParseException ex) {
		return ex.getMessage() + "; \n" +
				"At element: \"" + ex.getPublicId() + "\"" +
				"Line: " + ex.getLineNumber() + "\n" +
				"Column: " + ex.getColumnNumber() + "\n";
	}
	
	
	
	
	public static String convertStreamToString(InputStream is)throws IOException {
	/*
	* To convert the InputStream to String we use the
	* Reader.read(char[] buffer) method. We iterate until the
	* Reader return -1 which means there's no more data to
	* read. We use the StringWriter class to produce the string.
	*/
	if (is != null) {
	Writer writer = new StringWriter();
	 
	char[] buffer = new char[1024];
	try {
	Reader reader = new BufferedReader(	new InputStreamReader(is, "UTF-8"));
	int n;
	while ((n = reader.read(buffer)) != -1) {
	writer.write(buffer, 0, n);
	}
	} finally {
	is.close();
	}
	return writer.toString();
	} else {       
	return "";
	}
	}
	
	
}