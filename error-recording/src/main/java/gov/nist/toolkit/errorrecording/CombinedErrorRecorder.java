package gov.nist.toolkit.errorrecording;

import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.XMLErrorRecorder;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.gwt.GwtErrorRecorder;

import java.util.List;

/**
 * Created by diane on 2/17/2017.
 */
public class CombinedErrorRecorder implements ErrorRecorder {
    GwtErrorRecorder oldEr = new GwtErrorRecorder();
    XMLErrorRecorder newEr = new XMLErrorRecorder();

    /**
     * Figure out if the current ErrorRecorder is an instance of the old or new one
     **/
    private boolean isGwtErrorRecorder(ErrorRecorder er){
        return (er instanceof GwtErrorRecorder);
    }
    private boolean isXMLErrorRecorder(ErrorRecorder er){
        return (er instanceof XMLErrorRecorder);
    }


    //TODO uncomment here to switch between ErrorRecorders
    @Override
    public ErrorRecorder buildNewErrorRecorder() {
        return oldEr.buildNewErrorRecorder(); //GWT
        // return newEr.buildNewErrorRecorder(); //XML
    }

    @Override
    public ErrorRecorder buildNewErrorRecorder(Object o) {
        return null;
    }


    // ------ New ErrorRecorder functions -------
    @Override
    public void err(XdsErrorCode.Code code, Assertion assertion, String validatorModule, String location, String detail) {
        newEr.err(code, assertion, validatorModule, location, detail);
    }

    @Override
    public void err(XdsErrorCode.Code code, Assertion assertion, Object validatorModule, String location, String detail) {
        newEr.err(code, assertion, validatorModule, location, detail);
    }

    @Override
    public void err(XdsErrorCode.Code _code, Assertion _assertion, String _validatorModule, String _location, String _detail, String _logMessage) {
        newEr.err(_code, _assertion, _validatorModule, _location, _detail, _logMessage);
    }

    @Override
    public void err(XdsErrorCode.Code _code, Assertion _assertion, Object _validatorModule, String _location, String _detail, Object _logMessage) {
        newEr.err(_code, _assertion, _validatorModule, _location, _detail, _logMessage);
    }


    // ------ Old ErrorRecorder functions -------

    @Override
    public void warning(XdsErrorCode.Code code, String msg, String location, String resource) {
        oldEr.warning(code, msg, location, resource);
    }

    @Override
    public void success(String location, String resource) {
        oldEr.success(location, resource);
    }

    @Override
    public void err(XdsErrorCode.Code code, String msg, String location, String resource, Object log_message) {
        oldEr.err(code, msg, location, resource, log_message);
    }

    @Override
    public void err(XdsErrorCode.Code code, String msg, String location, String resource) {
        oldEr.err(code, msg, location, resource);
    }

    @Override
    public void err(XdsErrorCode.Code code, String msg, Object location, String resource) {
        oldEr.err(code, msg, location, resource);
    }

    @Override
    public void err(XdsErrorCode.Code code, Exception e) {
        oldEr.err(code, e);
    }

    @Override
    public void err(XdsErrorCode.Code code, String msg, String location, String severity, String resource) {
        oldEr.err(code, msg, location, severity, resource);
    }

    @Override
    public void err(String code, String msg, String location, String severity, String resource) {
        oldEr.err(code, msg, location, severity, resource);
    }

    @Override
    public void warning(String code, String msg, String location, String resource) {
        oldEr.warning(code, msg, location, resource);
    }

    @Override
    public void sectionHeading(String msg) {
        oldEr.sectionHeading(msg);
    }

    @Override
    public void challenge(String msg) {
        oldEr.challenge(msg);
    }

    @Override
    public void externalChallenge(String msg) {
        oldEr.externalChallenge(msg);
    }

    @Override
    public void detail(String msg) {
        oldEr.detail(msg);
    }

    @Override
    public void report(String name, String found) {
        oldEr.report(name, found);
    }

    @Override
    public void success(String dts, String name, String found, String expected, String RFC) {
        oldEr.success(dts, name, found, expected, RFC);
    }

    @Override
    public void error(String dts, String name, String found, String expected, String RFC) {
        oldEr.error(dts, name, found, expected, RFC);
    }

    @Override
    public void test(boolean good, String dts, String name, String found, String expected, String RFC) {
        oldEr.test(good, dts, name, found, expected, RFC);
    }

    @Override
    public void warning(String dts, String name, String found, String expected, String RFC) {
        oldEr.warning(dts, name, found, expected, RFC);
    }

    @Override
    public void info(String dts, String name, String found, String expected, String RFC) {
        oldEr.info(dts, name, found, expected, RFC);
    }

    @Override
    public void summary(String msg, boolean success, boolean part) {
        oldEr.summary(msg, success, part);
    }

    @Override
    public void finish() {
        oldEr.finish();
    }

    @Override
    public void showErrorInfo() {
        oldEr.showErrorInfo();
    }

    @Override
    public boolean hasErrors() {
        return oldEr.hasErrors();
    }

    @Override
    public int getNbErrors() {
        return oldEr.getNbErrors();
    }

    @Override
    public List<ErrorRecorder> getChildren() {
        return oldEr.getChildren();
    }

    @Override
    public int depth() {
        return oldEr.depth();
    }

    @Override
    public void registerValidator(Object validator) {
        oldEr.registerValidator(validator);
    }

    @Override
    public void unRegisterValidator(Object validator) {
        oldEr.unRegisterValidator(validator);
    }
}
