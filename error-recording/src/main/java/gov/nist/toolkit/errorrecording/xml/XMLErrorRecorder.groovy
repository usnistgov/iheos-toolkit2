package gov.nist.toolkit.errorrecording.xml

import gov.nist.toolkit.errorrecording.IErrorRecorder
import gov.nist.toolkit.errorrecording.IErrorRecorderBuilder
import gov.nist.toolkit.errorrecording.common.XdsErrorCode.Code
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion
import gov.nist.toolkit.xdsexception.ExceptionUtil
import groovy.xml.MarkupBuilder
import org.apache.log4j.Logger

/**
 * Created by diane on 2/19/2016.
 */
public class XMLErrorRecorder implements IErrorRecorder {
    public IErrorRecorderBuilder errorRecorderBuilder;

    static Logger logger = Logger.getLogger(XMLErrorRecorder.class);
    boolean firstSectionHeading = true;
    boolean hasErrors = false;
    def errXml = "<ErrorLog>\n"

    /**
     * Adds the end elements to the XML and returns the XML output as a String
     * @return the XML output
     */
    @Override
    def String toString() {
        String output = errXml + "</SectionHeading>\n</ErrorLog>\n"
        return prettyPrint(output)
    }

// Not used
    public void err(Code code, String msg, String location, String resource, Object log_message) {
        println("err")
        // Check if error message is not null
        /*if (msg == null || msg.trim().equals(""))
            return;

        // Set parameters on the XMLValidatorErrorItem and run it
        XMLValidatorErrorItem ei = new XMLValidatorErrorItem();
        ei.level = XMLValidatorErrorItem.ReportingLevel.ERROR;
        ei.msg = msg;
        ei.setCode(code);
        ei.location = location;
        ei.resource = resource;
        ei.completion = XMLValidatorErrorItem.ReportingCompletionType.ERROR;

        // add result to the list of errors
        addElement(ei);

        // errorcount++
        lastErrCount++;

        // propagate error to Challenge level
        propagateError();
        */
    }

    // Updated
    // TODO this is the only syntax that works. Propagate it to rest of document.
    public void err(Code _code, Assertion _assertion, String _validatorModule, String _location, String _detail) {
        println("err2")
        // TODO there used to be a null check on the location that I deactivated. Monitor for possible issues.
        //if (_location == null || _location.trim().equals("")) { return; }

        // Generate the new element
        def sw = new StringWriter()
        def builder = new MarkupBuilder(sw)
        builder.Error(code:_code, validatorModule:_validatorModule){
            Assertion(text:_assertion.getErrorMessage(), resource:_assertion.getLocation(),
                    gazelleScheme:_assertion.getGazelleScheme(), gazelleAssertionID:_assertion.getGazelleAssertionID()){
                Detail(_detail);
                Location(_location);
            }
        }
        // Parse and add
        errXml = errXml.concat(sw.toString() + "\n")
        hasErrors = true;
    }

    public void err(Code _code, Assertion _assertion, String _validatorModule, String _location, String _detail, String _logMessage) {
        println("err2-2")

        // Generate the new element
        def sw = new StringWriter()
        def builder = new MarkupBuilder(sw)
        builder.Error(code:_code, validatorModule:_validatorModule){
            Assertion(text:_assertion.getErrorMessage(), resource:_assertion.getLocation(),
                    gazelleScheme:_assertion.getGazelleScheme(), gazelleAssertionID:_assertion.getGazelleAssertionID()){
                Detail(_detail);
                Location(_location);
                LogMessage(_logMessage);
            }
        }
        // Parse and add
        errXml = errXml.concat(sw.toString() + "\n")
        hasErrors = true;
    }

    // Updated
    public void err(Code _code, Assertion _assertion, Object _validatorModule, String _location, String _detail) {
        println("err3")
        String valModuleName = getSimpleName(_validatorModule)
        err(_code, _assertion, valModuleName, _location, _detail);
    }

    public void err(Code _code, Assertion _assertion, Object _validatorModule, String _location, String _detail, Object _logMessage) {
        println("err3-2")
        String valModuleName = getSimpleName(_validatorModule)
        String logMessage = _logMessage.toString()
        err(_code, _assertion, valModuleName, _location, _detail, logMessage);
    }

    // Not used
    @Override
    public void err(Code code, Exception e) {
        println("err4")
        err(code, ExceptionUtil.exception_details(e), null, "");
    }

    // Not used
    @Override
    public void err(Code _code, String _msg, String _location, String _severity, String _resource) {
        println("err5")
        err(_code.toString(), _msg, _location, _severity, _resource)
    }

    // Not used
    @Override
    public void err(String _code, String _msg, String _location, String _severity, String _resource) {
        println("err6")
        if (_msg == null || _msg.trim().equals(""))
            return;

        // Log errors if any
        logger.debug(ExceptionUtil.here("err - " + _msg));
        if (_severity.indexOf("Error") != -1)
            System.out.println("Got Error");

        // Prepare parameters for logging
        boolean isWarning = (_severity == null) ? false : ((_severity.indexOf("Warning") != -1));

        // Generate the new element
        if (isWarning) {
            println("this should print a warning")
            // warning(_code, _msg, _location, _resource)
        }
        else {
            println("this should print an error")
            //err(_code, _msg, _location, _resource)
        }
    }

    // Old prototype
    @Override
    void err(Code code, String msg, String location, String resource) {
        println("err7 - old prototype, needs to be upgraded to use Assertions")
        println("code: " + code + "; msg: " + msg + "; location: " + location + "; resource: " + resource);
    }

    // Old prototype
    @Override
    void err(Code code, String msg, Object location, String resource) {
        println("err8 - old prototype, needs to be upgraded to use Assertions")
        println("code: " + code + "; msg: " + msg + "; location: " + location + "; resource: " + resource);
    }

    // Untested / not used
    @Override
    public void warning(String _code, String _msg, String _location, String _resource) {
        println("warning1")
        err(_code, _msg, _location, "Warning", _resource);
    }

// Untested / not used
    @Override
    public void warning(Code _code, String _msg, String _location, String _resource) {
        println("warning2")
        err(_code.toString(), _msg, _location, "Warning", _resource);
    }

    // Not used
    private void propagateError() {
        println("NYI-propagateerr")
        // Test if in a section heading or challenge section. If challenge then set the ReportingCompletionType to Error.
    }

    /**
     * Opens a new Section and optionally closes the previous one
     * @param msg
     */
    @Override
    public void sectionHeading(String msg) {
        println("sectionheading " + msg)
        def el = "";

        // Process the text of the message
        XMLErrorRecorderMessage processedMsg = processMessage(msg)

        // Create the SectionHeading element
        if (firstSectionHeading) { firstSectionHeading = false; }
        else {
            el = "</SectionHeading>\n"
        }
        if (processedMsg.getXDS_DOCUMENT_TYPE() == null) {
            el = el + "<SectionHeading message=\"" + msg + "\">\n";
        } else {
            el = el + "<SectionHeading " +
                    "type=\"" + processedMsg.getXDS_DOCUMENT_TYPE() + "\" " +
                    "id=\"" + processedMsg.getId() + "\"" +
                    ">\n";
        }
        errXml = errXml.concat(el)
    }

    @Override
    public void challenge(String msg) {
        println("challenge")
        errXml = errXml.concat(createXMLString("Challenge", msg))
    }

// Not used / not tested
    @Override
    public void externalChallenge(String msg) {
        println("extchallenge")
//errRecords.add(createXMLElement("ExternalChallenge", msg))
    }

// Not used / Not tested
    @Override
    public void detail(String msg) {
        println("detail")
        errXml = errXml.concat(createXMLString("Detail", msg))
    }

// Not used / Not tested
    @Override
    public void report(String name, String found) {
        println("report")
        detail(name + " " + found);
    }

    @Override
    void success(String dts, String name, String found, String expected, String RFC) {

    }
/**
 * Not used / Not tested
 * @param dts
 * @param name
 * @param found
 * @param expected
 * @param RFC
 */
    @Override
    public void success(String _location, String _resource) {
        println("success")

        // Generate the new element
        def sw = new StringWriter()
        def builder = new MarkupBuilder(sw)
        builder.Success(location:_location, resource:_resource){}

        // Parse and add
        errXml = errXml.concat(sw.toString() + "\n")
    }

// Not used / Not tested
    @Override
    public void error(String _dts, String _name, String _found, String _expected, String _rfc) {
        println("error")
        def sw = new StringWriter()
        def builder = new MarkupBuilder(sw)
        builder.records() {
            Error (name:_name, dts:_dts, found:_found, expected:_expected, rfc:_rfc)
        }
        def el = sw.write()
        //errRecords.add(el)
    }

    // Not used / Not tested
    @Override
    public void test(boolean good, String dts, String name, String found, String expected, String RFC) {
        println("NYI-test")
    }

    // Not used / Not tested
    @Override
    public void warning(String _dts, String _name, String _found, String _expected, String _rfc) {
        println("warning")
        def sw = new StringWriter()
        def builder = new MarkupBuilder(sw)
        builder.records() {
            Warning (name:_name, dts:_dts, found:_found, expected:_expected, rfc:_rfc)
        }
        def el = sw.write()
        //errRecords.add(el)
    }

    // Not used / Not tested
    @Override
    public void info(String _dts, String _name, String _found, String _expected, String _rfc) {
        println("info")
        def sw = new StringWriter()
        def builder = new MarkupBuilder(sw)
        builder.records() {
            Info (name:_name, dts:_dts, found:_found, expected:_expected, rfc:_rfc)
        }
        def el = sw.write()
        //errRecords.add(el)
    }

    // Not used / Not tested
    @Override
    public void summary(String _msg, boolean _success, boolean _part) {
        println("summary")
        def sw = new StringWriter()
        def builder = new MarkupBuilder(sw)
        builder.records() {
            Summary (success:_success, part:_part){
                Message(_msg)
            }
        }
        def el = sw.write()
        //errRecords.add(el)
    }

    // Not used / Not tested
    @Override
    public void finish() {
        println("NYI-finish")
    }

    // Not used / Not tested
    @Override
    public boolean hasErrors() {
        println("boolhaserrors")
        return hasErrors;
    }

    // Not used / Not tested
    @Override
    public int getNbErrors() {
        println("NYI-getnberrors")
        return 0;
    }

    // Not used / Not tested
    @Override
    public List<IErrorRecorder> getChildren() {
        println("NYI-errrecorder")
        return null;
    }

    @Override
    public int depth() {
        println("NYI-depth")
        return 0;
    }

    // Not used
    @Override
    public void registerValidator(Object validator) {
        println("NYI-regvalidator")
    }

    // Not used
    @Override
    public void unRegisterValidator(Object validator) {
        println("NYI-unregvalidator")
    }

    @Override
    public void showErrorInfo() {
        // was empty in original code
    }

    @Override
    public IErrorRecorder buildNewErrorRecorder() {
        XMLErrorRecorder rec =  new XMLErrorRecorder();
        rec.errorRecorderBuilder = this;
        return rec;
    }

    @Override
    public IErrorRecorder buildNewErrorRecorder(Object o) {
        IErrorRecorder er =  errorRecorderBuilder.buildNewErrorRecorder();
        children.add(er);
        return er;
    }

    // ---------- Utility functions -----------

    /**
     * Creates XML elements
     * @param name The name of the new XML element to create.
     * @return The new XML element.
     */
    def private static Node createXMLElement(String name, String msg){
        def newElement = "<" + name + ">" + msg + "</" + name + ">"
        def newRecord = new XmlParser().parseText(newElement)
        return newRecord
    }

    /**
     * Creates XML elements as text
     * @param name The name of the new XML element to create.
     * @return The new XML element as text.
     */
    def private static String createXMLString(String name, String msg){
        return "<" + name + ">" + msg + "</" + name + ">\n"
    }

    private String getSimpleName(Object location){
        if (location != null)
            return location.getClass().getSimpleName();
        return ""
    }

    /**
     * Validates an XML string by running it through SAX / Groovy parser. Pretty prints the XML as a string.
     * @param input
     * @return
     */
    private String prettyPrint(String input) {
        try {
            // parse existing XML string into a Node. This has the advantage of also validating the XML.
            def xml = new XmlParser().parseText(input)

            // Remove empty elements and attributes
            //cleanNode(xml)

            // Pretty print the XML
            StringWriter sw = new StringWriter();
            def printer = new XmlNodePrinter(new PrintWriter(sw))
            printer.preserveWhitespace = true
            printer.print(xml)
            String output = sw.toString()
            return output.replaceAll("&amp;", "&");
        } catch (e) {
            println("Error in output pretty print in XMLErrorRecorder. The XML could not be validated or formatted :" + e)
        }
        return input
    }

    /**
     * Removes empty elements and attributes from an XML Node
     * @param node
     * @return XML output cleaned of empty values
     */
    /*boolean cleanNode(Node node) {
        node.attributes().with { a ->
            a.findAll {
                !it.value.each { a.remove(it.key) }
            }
        }
        node.children().with { kids ->
            kids.findAll { it instanceof Node ? !cleanNode(it) : false }
                    .each { kids.remove(it) }
        }
        node.attributes() || node.children() || node.text()
    }*/


    /**
     * TODO not sure what this is used for
     * Takes a message item from the XMLErrorRecorder output and processes it to extract separate pieces of information.
     * @see XMLErrorRecorderMessage for more information.
     * @param msg
     * @return
     */
    private XMLErrorRecorderMessage processMessage(String msg){
        return new XMLErrorRecorderMessage(msg);
    }

}
