package gov.nist.toolkit.errorrecording;

import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder
import groovy.xml.MarkupBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diane on 2/19/2016.
 */
public class XMLErrorRecorder implements ErrorRecorder {
    ErrorRecorderBuilder errorRecorderBuilder;

    def writer = new StringWriter()
    def xml = new MarkupBuilder(writer)
    def summary = new MarkupBuilder(writer) // former List<ValidatorErrorItem> summary = new ArrayList<>();
    def errors = new MarkupBuilder(writer)  //     List<ValidatorErrorItem> errMsgs = new ArrayList<>();

    // Probably not useful and should be removed
    List<ErrorRecorder> children = new ArrayList<>();



    @Override
    public void err(XdsErrorCode.Code code, String msg, String location, String resource, Object log_message) {
        // Check if error message is not null
        if (msg == null || msg.trim().equals(""))
            return;

        // Set parameters on the ValidatorErrorItem (needs to be converted to GWT ValidatorErrorItem) and run it
        // add result to the list of errors
        // errorcount++
        // propagate error to Challenge level

    }

    @Override
    public void err(XdsErrorCode.Code code, String msg, String location, String resource) {

    }

    @Override
    public void err(XdsErrorCode.Code code, String msg, Object location, String resource) {

    }

    @Override
    public void err(XdsErrorCode.Code code, Exception e) {

    }

    @Override
    public void err(XdsErrorCode.Code code, String msg, String location, String severity, String resource) {

    }

    @Override
    public void err(String code, String msg, String location, String severity, String resource) {

    }

    private void propagateError() {
        // Test if in a section heading or challenge section. If challenge then set the ReportingCompletionType to Error.
    }

        @Override
    public void warning(String code, String msg, String location, String resource) {

    }

    @Override
    public void warning(XdsErrorCode.Code code, String msg, String location, String resource) {

    }

    @Override
    public void sectionHeading(String msg) {

    }

    @Override
    public void challenge(String msg) {

    }

    @Override
    public void externalChallenge(String msg) {

    }

    @Override
    public void detail(String msg) {

    }

    @Override
    public void report(String name, String found) {

    }

    @Override
    public void success(String dts, String name, String found, String expected, String RFC) {

    }

    @Override
    public void error(String dts, String name, String found, String expected, String RFC) {

    }

    @Override
    public void test(boolean good, String dts, String name, String found, String expected, String RFC) {

    }

    @Override
    public void warning(String dts, String name, String found, String expected, String RFC) {

    }

    @Override
    public void info(String dts, String name, String found, String expected, String RFC) {

    }

    @Override
    public void summary(String msg, boolean success, boolean part) {

    }

    @Override
    public void finish() {

    }

    @Override
    public void showErrorInfo() {

    }

    @Override
    public boolean hasErrors() {
        return false;
    }

    @Override
    public int getNbErrors() {
        return 0;
    }

    @Override
    public void concat(ErrorRecorder er) {

    }

    @Override
    public List<ValidatorErrorItem> getErrMsgs() {
        return null;
    }

    @Override
    public List<ErrorRecorder> getChildren() {
        return null;
    }

    @Override
    public int depth() {
        return 0;
    }

    @Override
    public void registerValidator(Object validator) {

    }

    @Override
    public void unRegisterValidator(Object validator) {

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
}
