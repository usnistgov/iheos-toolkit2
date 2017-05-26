package gov.nist.toolkit.errorrecording;

import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;

/**
 * New XMLErrorRecorder interface. After the transition to the new XML architecture is complete, this can be used as the only
 * interface calls in the IErrorRecorder.
 * @author dazais
 *
 */
public interface IXMLErrorRecorder extends IErrorRecorderBuilder {

	//----- Prototypes used by the XML ErrorRecorder -----
	void err(XdsErrorCode.Code code, Assertion assertion, String validatorModule, String location, String detail);
	void err(XdsErrorCode.Code code, Assertion assertion, Object validatorModule, String location, String detail);
	void err(XdsErrorCode.Code _code, Assertion _assertion, String _validatorModule, String _location, String _detail, String _logMessage);
	void err(XdsErrorCode.Code _code, Assertion _assertion, Object _validatorModule, String _location, String _detail, Object _logMessage);
	void success(Assertion _assertion, Object _validatorModule, String _detail);

}
