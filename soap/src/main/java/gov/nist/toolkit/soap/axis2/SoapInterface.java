package gov.nist.toolkit.soap.axis2;

import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.LoadKeystoreException;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsFormatException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;

public interface SoapInterface {

	public abstract OMElement getInHeader();

	public abstract OMElement getOutHeader();

	public abstract void addHeader(OMElement header);

	public abstract void addSecHeader(OMElement header);

	public abstract void clearHeaders();

	public abstract void setAsync(boolean async);

	/**
	 * Low level invocation of soap call.  The basic ServiceClient is inadequate to gain this level
	 * of control, OperationClient must be used.  Reference:
	 *    http://today.java.net/pub/a/today/2006/12/13/invoking-web-services-using-apache-axis2.html#working-with
	 * This does not yet handle async.
	 * @param bodyBytes
	 * @return
	 * @throws XdsException
	 * @throws AxisFault
	 * @throws EnvironmentNotSelectedException 
	 */
	public abstract void soapCallWithWSSEC() throws XdsInternalException,
			AxisFault, EnvironmentNotSelectedException, LoadKeystoreException;

	public abstract OMElement soapCall() throws LoadKeystoreException, XdsInternalException,
			AxisFault, XdsFormatException, EnvironmentNotSelectedException;

	public abstract OMElement getResult();

	public abstract OMElement soapCall(OMElement body, String endpoint,
			boolean mtom, boolean addressing, boolean soap12, String action,
			String expected_return_action) throws XdsInternalException,
			AxisFault, XdsFormatException, EnvironmentNotSelectedException, LoadKeystoreException;

	public abstract String getExpectedReturnAction();

	public abstract void setExpectedReturnAction(String expectedReturnAction);

	public abstract boolean isMtom();

	public abstract void setMtom(boolean mtom);

	public abstract boolean isAddressing();

	public abstract void setAddressing(boolean addressing);

	public abstract boolean isSoap12();

	public abstract void setSoap12(boolean soap12);

	public abstract boolean isAsync();

	public abstract void setUseSaml(boolean use);

}