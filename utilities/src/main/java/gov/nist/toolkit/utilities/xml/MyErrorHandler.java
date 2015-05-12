/*
 * ErrorHandler.java
 *
 * Created on September 27, 2005, 9:10 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gov.nist.toolkit.utilities.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 *
 * @author bill
 */
public class MyErrorHandler implements ErrorHandler {
    StringBuffer errors;
    String schemaFile = "";
    
    /** Creates a new instance of ErrorHandler */
    public MyErrorHandler() {
        errors = new StringBuffer();
    }

    public void setSchemaFile(String file) {
	schemaFile = file;
    }
    
    public String getErrors() {
        return errors.toString();
    }
    
    public void error(SAXParseException exception) {
        errors.append("\nError: " + exception.getMessage() + "\n" +
		      "Schema location is " + schemaFile);
    }
    
    public void fatalError(SAXParseException exception) {
        errors.append("\nFatal Error: " + exception.getMessage());
    }
    
    public void warning(SAXParseException exception) {
        errors.append("\nWarning: " + exception.getMessage());
    }
}
