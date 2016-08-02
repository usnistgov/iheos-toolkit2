package gov.nist.toolkit.soap.http;

/*
 * This class does not seems to be used anywhere. REMOVE? - Antoine 07/24/2013 
 */


import gov.nist.toolkit.soap.axis2.SoapInterface;
import gov.nist.toolkit.xdsexception.XdsFormatException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;

import java.util.ArrayList;
import java.util.List;

public class HomeSoap implements SoapInterface {
	OMElement inHeader = null;
	OMElement outHeader = null;
	List<OMElement> headersToBeSent = new ArrayList<OMElement>();
	List<OMElement> secHeadersToBeSent = new ArrayList<OMElement>();
	boolean async = false;
	boolean mtom = false;
	boolean addressing = true;
	boolean soap12 = false;
	boolean saml = false;
	String expectedReturnAction = null;
	
	public OMElement getInHeader() {
		return inHeader;
	}

	public OMElement getOutHeader() {
		return outHeader;
	}

	public void addHeader(OMElement header) {
		headersToBeSent.add(header);
	}

	public void addSecHeader(OMElement header) {
		secHeadersToBeSent.add(header);
	}

	public void clearHeaders() {
		headersToBeSent.clear();
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public void soapCallWithWSSEC() throws XdsInternalException, AxisFault {

	}

	public OMElement soapCall() throws XdsInternalException, AxisFault,
			XdsFormatException {
		return null;
	}

	public OMElement getResult() {
		return null;
	}

	public OMElement soapCall(OMElement body, String endpoint, boolean mtom,
			boolean addressing, boolean soap12, String action,
			String expected_return_action) throws XdsInternalException,
			AxisFault, XdsFormatException {
		return null;
	}

	public String getExpectedReturnAction() {
		return expectedReturnAction;
	}

	public void setExpectedReturnAction(String expectedReturnAction) {
		this.expectedReturnAction = expectedReturnAction;
	}

	public boolean isMtom() {
		return mtom;
	}

	public void setMtom(boolean mtom) {
		this.mtom = mtom;
	}

	public boolean isAddressing() {
		return addressing;
	}

	public void setAddressing(boolean addressing) {
		this.addressing = addressing;
	}

	public boolean isSoap12() {
		return soap12;
	}

	public void setSoap12(boolean soap12) {
		this.soap12 = soap12;
	}

	public boolean isAsync() {
		return async;
	}

	public void setUseSaml(boolean use) {
		saml = use;
	}

}
