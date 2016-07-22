package gov.nist.toolkit.errorrecording

import gov.nist.toolkit.errorrecording.client.XMLValidatorErrorItem
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder

/**
 * Created by diane on 2/19/2016.
 */
public class XMLErrorRecorder implements ErrorRecorder {
    public ErrorRecorderBuilder errorRecorderBuilder;

    //def summary = // former List<GwtValidatorErrorItem> summary = new ArrayList<>();
    //List<ErrorRecorder> children = new ArrayList<>();  // Probably not useful in new XML validator and should be removed
    def errXml = '''<ErrorLog></ErrorLog>'''
    def errMsgs = new XmlParser().parseText(errXml) // should be called ErrorLog to be accurate
    def errRecords = errMsgs.children()


    /**
     * //TODO
     * Temporary toString function for testing purposes, may need upgrade later
     * @return
     */
    @Override
    def String toString() {
        // Convert back to String / XML
        StringWriter sw = new StringWriter()
        new XmlNodePrinter(new PrintWriter(sw)).print(errMsgs)

        println("\n--- XML output: ---\n\n" + sw.toString())
        return sw.toString()
    }


    @Override
    public void err(Code code, String msg, String location, String resource, Object log_message) {
        // Check if error message is not null
        if (msg == null || msg.trim().equals(""))
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
    }

    def addElement(XMLValidatorErrorItem ei){
        // Convert both main XML and element to add to parsed XML records form
        println("\nei\n" + ei.toString())
        //errMsgs = new XmlSlurper().parseText(errXml)
        def newRecord = new XmlSlurper().parseText(ei)

        // Append the new element
        errMsgs.appendNode(newRecord)
    }

    @Override
    public void err(Code code, String msg, String location, String resource) {
        println("NYI-err2")
    }

    @Override
    public void err(Code code, String msg, Object location, String resource) {
        println("NYI-err3")
    }

    @Override
    public void err(Code code, Exception e) {
        println("NYI-err4")

    }

    @Override
    public void err(Code code, String msg, String location, String severity, String resource) {
        println("NYI-err5")
    }

    @Override
    public void err(String code, String msg, String location, String severity, String resource) {
        println("NYI-err6")
        //err1(code, msg, location, severity, resource);
    }

    private void propagateError() {
        println("NYI-propagateerr")
        // Test if in a section heading or challenge section. If challenge then set the ReportingCompletionType to Error.
    }

    @Override
    public void warning(String code, String msg, String location, String resource) {
        println("NYI-warning")

    }

    @Override
    public void warning(Code code, String msg, String location, String resource) {
        println("NYI-warning")

    }

    //TODO last because a SectionHeading element needs to wrap an entire section
    @Override
    public void sectionHeading(String msg) {
        println("sectionheading")
        //tagLastInfo2(); // TODO let's see if it works without saving location of item
        def newElement = '''<SectionHeading>''' + msg + '''</SectionHeading>'''
        def newRecord = new XmlParser().parseText(newElement)
        errRecords.add(newRecord)
    }

    @Override
    public void challenge(String msg) {
        println("challenge")
        errRecords.add(createXMLElement("Challenge", msg))
    }

    @Override
    public void externalChallenge(String msg) {
        println("extchall")
        errRecords.add(createXMLElement("ExternalChallenge", msg))
    }

    @Override
    public void detail(String msg) {
        println("detail")
        errRecords.add(createXMLElement("Detail", msg))
    }

    @Override
    public void report(String name, String found) {
        println("NYI-report")
    }

    @Override
    public void success(String dts, String name, String found, String expected, String RFC) {
        println("NYI-success")
    }

    @Override
    public void error(String dts, String name, String found, String expected, String RFC) {
        println("NYI-error")
    }

    @Override
    public void test(boolean good, String dts, String name, String found, String expected, String RFC) {
        println("NYI-test")
    }

    @Override
    public void warning(String dts, String name, String found, String expected, String RFC) {
        println("NYI-warning")
    }

    @Override
    public void info(String dts, String name, String found, String expected, String RFC) {
        println("NYI-info")
    }

    @Override
    public void summary(String msg, boolean success, boolean part) {
        println("NYI-summary")
    }

    @Override
    public void finish() {
        println("NYI-finish")
    }

    @Override
    public void showErrorInfo() {
        println("NYI-showerrinfo")
    }

    @Override
    public boolean hasErrors() {
        println("NYI-boolhaserrors")
        return false;
    }

    @Override
    public int getNbErrors() {
        println("NYI-getnberrors")
        return 0;
    }

    @Override
    public void concat(ErrorRecorder er) {
        println("NYI-concat")
    }

    @Override
    public List<XMLValidatorErrorItem> getErrMsgs() {
        println("NYI-geterrmsgs")
        return null;
    }

    @Override
    public List<ErrorRecorder> getChildren() {
        println("NYI-errrecorder")
        return null;
    }

    @Override
    public int depth() {
        println("NYI-depth")
        return 0;
    }

    @Override
    public void registerValidator(Object validator) {
        println("NYI-regvalidator")
    }

    @Override
    public void unRegisterValidator(Object validator) {
        println("NYI-unregvalidator")
    }

    @Override
    public ErrorRecorder buildNewErrorRecorder() {
        XMLErrorRecorder rec =  new XMLErrorRecorder();
        rec.errorRecorderBuilder = this;
        return rec;
    }

    @Override
    public ErrorRecorder buildNewErrorRecorder(Object o) {
        ErrorRecorder er =  errorRecorderBuilder.buildNewErrorRecorder();
        children.add(er);
        return er;
    }

    /**
     * Utility function
     * @param name The name of the new XML element to create.
     * @return The new XML element.
     */
    def private static Node createXMLElement(String name, String msg){
        def newElement = "<" + name + ">" + msg + "</" + name + ">"
        def newRecord = new XmlParser().parseText(newElement)
        return newRecord
    }

}
