package gov.nist.toolkit.errorrecording;

import gov.nist.toolkit.errorrecording.common.XdsErrorCode;

import java.util.List;

/**
 * Combines functions from the old GWTErrorRecorder and the new XMLErrorRecorder
 * @author dazais
 *
 */
public interface IGWTErrorRecorder extends IErrorRecorderBuilder {

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

	List<IErrorRecorder> getChildren();

	int depth();
}
