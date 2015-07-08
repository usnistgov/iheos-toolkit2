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
    boolean firstError = true;
    
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
    
    public void error(SAXParseException exception) { addError(exception.getMessage()); }

    public void fatalError(SAXParseException exception) {
        addError("Fatal Error: " + exception.getMessage());
    }
    
    public void warning(SAXParseException exception) {
        addError("Warning: " + exception.getMessage());
    }

    void addError(String msg) {
        if (firstError) {
            errors.append("Schema location is " + schemaFile + "\n");
            firstError = false;
        }
        errors.append(msg + "\n");
    }
}
