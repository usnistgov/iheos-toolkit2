package gov.nist.toolkit.testengine;

import gov.nist.toolkit.xdsexception.XdsInternalException;

public interface ErrorReportingInterface {
	public void fail(String msg) throws XdsInternalException;
	public void setInContext(String title, Object value);
}
