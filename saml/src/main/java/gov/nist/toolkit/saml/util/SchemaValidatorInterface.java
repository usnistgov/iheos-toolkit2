package gov.nist.toolkit.saml.util;

import java.io.IOException;
import java.io.Reader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Interface defining the API used by SAML for schema checking.
 *  
 * @author Srinviasarao Eadara
 */
public interface SchemaValidatorInterface {


	/**
     * Get the Schema used by the Validator to check instance documents.
     * 
	 * @return The Schema used by the Validator to check instance documents
	 */
		Schema getSchema();


	/**
     * Set the Schema used by the Validator to check instance documents.
     * 
	 * @param schema Schema to be used for validation
	 */
		void setSchema(Schema schema);


	/**
     * Check if a given xml string is valid according to the current schema.
     * 
	 * @param s String to be validated
	 * @throws org.xml.sax.SAXException If parsing of the schema instance fails
	 * @throws java.io.IOException Thrown on IO error
	 */
		void validate(String s) throws SAXException, IOException;


	/**
     * Check if xml content represented by a Reader is valid according to the current schema.
     * 
	 * @param r Reader acessing the xml content
	 * @throws org.xml.sax.SAXException If parsing of the schema instance fails
	 * @throws java.io.IOException Thrown on IO error
	 */
		void validate(Reader r) throws SAXException, IOException;


	/**
     * Check if xml represented by a node is valid according to the current schema.
     * 
	 * @param r XML Node that is to be validated
	 * @throws org.xml.sax.SAXException If parsing of the schema instance fails
	 * @throws java.io.IOException Thrown on IO error
	 */
		void validate(Node r) throws SAXException, IOException;


	/**
     *
	 * Check if XML represented by a Source is valid according to the current schema.
	 * 
	 * @param source Source to be validated
	 * @throws org.xml.sax.SAXException If parsing of the schema instance fails
	 * @throws java.io.IOException Thrown on IO error
	 */
		void validate(Source source) throws SAXException, IOException;

}