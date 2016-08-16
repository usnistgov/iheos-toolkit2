package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.xdsexception.client.XdsInternalException;

public interface ErrorReportingInterface {
	public void fail(String msg) throws XdsInternalException;
	public void setInContext(String title, Object value);
}
