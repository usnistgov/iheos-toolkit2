package gov.nist.toolkit.errorrecording;

import java.util.List;

import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;

/**
 * Interface for all ErrorRecorders
 * @author bill
 *
 */
public interface ErrorRecorder extends ErrorRecorderBuilder {
	public void err(XdsErrorCode.Code code, String msg, String location, String resource, Object /* LogMessage */ log_message);
	public void err(XdsErrorCode.Code code, String msg, String location, String resource);
	public void err(XdsErrorCode.Code code, String msg, Object location, String resource);
	public void err(XdsErrorCode.Code code, Exception e);  // error in tool
	public void err(XdsErrorCode.Code code, String msg, String location, String severity, String resource);
	public void err(String code, String msg, String location, String severity, String resource);
	public void warning(String code, String msg, String location, String resource);
	public void warning(XdsErrorCode.Code code, String msg, String location, String resource);
	public void sectionHeading(String msg); // section heading
	public void challenge(String msg); // statement of challenge
	public void externalChallenge(String msg); // statement of challenge that requires registry query
	public void detail(String msg); // detail findings
	public void success(String dts, String name, String found, String expected, String RFC);
	public void error(String dts, String name, String found, String expected, String RFC);
	public void warning(String dts, String name, String found, String expected, String RFC);
	public void info(String dts, String name, String found, String expected, String RFC);
	public void summary(String msg, boolean success, boolean part);
	public void finish();
	public void showErrorInfo();
	public boolean hasErrors();
	public int getNbErrors();
	public void concat(ErrorRecorder er);
	public List<ValidatorErrorItem> getErrMsgs();
	
	public ErrorRecorder buildNewErrorRecorder();  // some code only accepts ErrorRecorder.  This gets around this
	public ErrorRecorderBuilder getErrorRecorderBuilder();

}
