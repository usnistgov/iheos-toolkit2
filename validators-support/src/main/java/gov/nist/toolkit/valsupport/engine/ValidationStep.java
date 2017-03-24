package gov.nist.toolkit.valsupport.engine;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;

/**
 * Created by bill on 6/23/15.
 */
public class ValidationStep {
    String stepName;
    AbstractMessageValidator validator;
    ErrorRecorder er;
    boolean ran = false;

    public ValidationStep(String stepName, AbstractMessageValidator validator, ErrorRecorder er) {
        this.stepName = stepName;
        this.validator = validator;
        this.er = er;
    }

    public String getStepName() { return stepName; }
    public ErrorRecorder getErrorRecorder() { return er; }
    public boolean hasErrors() { return er.hasErrors(); }

    public String toString() {
        StringBuffer buf = new StringBuffer();

        String className = validator.getClass().getName();
        try {
            className = className.substring(className.lastIndexOf(".") + 1);
        } catch (Exception e) {
            className = "Unknown";
        }


        buf
                .append(stepName)
                .append(" [")
                .append(className)
                .append("] ")
                .append(validator.toString());

        return buf.toString();
    }

    public boolean isRan() {
        return ran;
    }
    public void setRan(boolean r) {
        ran = r;
    }
    public AbstractMessageValidator getValidator() {
        return validator;
    }
    public ErrorRecorder getEr() {
        return er;
    }
}
