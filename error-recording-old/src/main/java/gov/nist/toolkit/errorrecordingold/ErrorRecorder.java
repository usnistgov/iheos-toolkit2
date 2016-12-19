package gov.nist.toolkit.errorrecordingold;

import gov.nist.toolkit.errorrecordingold.client.GwtValidatorErrorItem;
import gov.nist.toolkit.errorrecordingold.client.ValidatorErrorItem;
import gov.nist.toolkit.errorrecordingold.client.XdsErrorCode;
import gov.nist.toolkit.errorrecordingold.factories.ErrorRecorderBuilder;

import java.util.List;

/**
 * Interface for all ErrorRecorders
 * @author bill
 *
 */
public interface ErrorRecorder extends ErrorRecorderBuilder {
	void err(XdsErrorCode.Code code, String msg, String location, String resource, Object /* LogMessage */ log_message);
	void err(XdsErrorCode.Code code, String msg, String location, String resource);
	void err(XdsErrorCode.Code code, String msg, Object location, String resource);
	void err(XdsErrorCode.Code code, Exception e);  // error in tool
	void err(XdsErrorCode.Code code, String msg, String location, String severity, String resource);
	void err(String code, String msg, String location, String severity, String resource);
	void warning(String code, String msg, String location, String resource);
	void warning(XdsErrorCode.Code code, String msg, String location, String resource);
	void sectionHeading(String msg); // section heading
	void challenge(String msg); // statement of challenge
	void externalChallenge(String msg); // statement of challenge that requires registry query
	void detail(String msg); // detail findings
    void report(String name, String found);
	void success(String dts, String name, String found, String expected, String RFC);
	void error(String dts, String name, String found, String expected, String RFC);
    void test(boolean good, String dts, String name, String found, String expected, String RFC);
	void warning(String dts, String name, String found, String expected, String RFC);
	void info(String dts, String name, String found, String expected, String RFC);
	void summary(String msg, boolean success, boolean part);
	void finish();
	void showErrorInfo();
	boolean hasErrors();
	int getNbErrors();

	List<ErrorRecorder> getChildren();
    int depth();
    // register should be called at start of a validator run(ErrorRecorder er, MessageValidatorEngine mvc) method
    //    call should be made as registerValidator(this)
    // unRegister should be called at end - in a finally block if necessary
    void registerValidator(Object validator);    // Used to report location
    // unRegister should be called at end - in a finally block if necessary
    //    call should be made as unRegisterValidator(this)
    void unRegisterValidator(Object validator);
}
