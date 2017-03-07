package gov.nist.toolkit.errorrecording;

import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;

import java.util.List;

/**
 * Combines functions from the old GWTErrorRecorder and the new XMLErrorRecorder
 * @author dazais
 *
 */
public interface XMLErrorRecorder extends ErrorRecorderBuilder {

	//----- Prototypes used by the XML ErrorRecorder -----
	void err(XdsErrorCode.Code code, Assertion assertion, String validatorModule, String location, String detail); // updated
	void err(XdsErrorCode.Code code, Assertion assertion, Object validatorModule, String location, String detail); // updated
	void err(XdsErrorCode.Code _code, Assertion _assertion, String _validatorModule, String _location, String _detail, String _logMessage); // updated
	void err(XdsErrorCode.Code _code, Assertion _assertion, Object _validatorModule, String _location, String _detail, Object _logMessage); // updated
	void success(String location, String resource);

}
