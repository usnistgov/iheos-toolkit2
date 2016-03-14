package gov.nist.toolkit.errorrecording;

import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;

import java.util.List;

/**
 * Created by diane on 2/19/2016.
 */
public class XMLErrorRecorder implements ErrorRecorder {
    @Override
    public void err(XdsErrorCode.Code code, String msg, String location, String resource, Object log_message) {
        
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
        return null;
    }

    @Override
    public ErrorRecorder buildNewErrorRecorder(Object o) {
        return null;
    }
}
