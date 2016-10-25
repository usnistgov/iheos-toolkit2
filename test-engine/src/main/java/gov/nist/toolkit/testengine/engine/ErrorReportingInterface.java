package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

public interface ErrorReportingInterface {
	public void fail(String msg) throws XdsInternalException;
	void fail(OMElement ele) throws XdsInternalException;
	public void setInContext(String title, Object value);
}
