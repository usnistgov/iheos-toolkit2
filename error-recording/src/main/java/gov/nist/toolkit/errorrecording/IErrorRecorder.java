package gov.nist.toolkit.errorrecording;

import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;

import java.util.List;

/**
 * Combines functions from the old GWTErrorRecorder and the new XMLErrorRecorder
 * @author dazais
 *
 */
public interface IErrorRecorder extends IXMLErrorRecorder, IGWTErrorRecorder, IErrorRecorderBuilder {

	//----- Prototypes used by the XML ErrorRecorder -----
	void err(XdsErrorCode.Code code, Assertion assertion, String validatorModule, String location, String detail); // updated
	void err(XdsErrorCode.Code code, Assertion assertion, Object validatorModule, String location, String detail); // updated
	void err(XdsErrorCode.Code _code, Assertion _assertion, String _validatorModule, String _location, String _detail, String _logMessage); // updated
	void err(XdsErrorCode.Code _code, Assertion _assertion, Object _validatorModule, String _location, String _detail, Object _logMessage); // updated
	void success(String location, String resource);
	void success(Assertion _assertion, Object _validatorModule, String _detail);
	//List<gov.nist.toolkit.errorrecording.ErrorRecorder> getChildren();


	//----- Prototypes used by the original GWTErrorRecorder -----
	void err(XdsErrorCode.Code code, String msg, String location, String resource, Object log_message);
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
	//void success(String dts, String name, String found, String expected, String RFC);
	void error(String dts, String name, String found, String expected, String RFC);
	void test(boolean good, String dts, String name, String found, String expected, String RFC);
	void warning(String dts, String name, String found, String expected, String RFC);
	void info(String dts, String name, String found, String expected, String RFC);
	void summary(String msg, boolean success, boolean part);
	void finish();
	void showErrorInfo();
	boolean hasErrors();
	int getNbErrors();

	List<IErrorRecorder> getChildren();
	int depth();

	/**
	 * Used to report location
	 * Use: Register should be called at start of a validator run(ErrorRecorder er, MessageValidatorEngine mvc) method.
	 * Call should be made as registerValidator(this)
	 * Unregister should be called at end - in a finally block if necessary
	 * @param validator
	 * @see IErrorRecorder#unRegisterValidator(Object)
	 */
	void registerValidator(Object validator);
	//

	/**
	 * Use: Unregister should be called at end - in a finally block if necessary
	 * Call should be made as unRegisterValidator(this)
	 * @param validator
	 * @see IErrorRecorder#registerValidator(Object)
	 */
	void unRegisterValidator(Object validator);

}
