package gov.nist.toolkit.soap.axis2;

import gov.nist.toolkit.docref.WsDocRef;
import gov.nist.toolkit.dsig.XMLDSigProcessor;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.saml.builder.WSSESecurityHeaderUtil;
import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.soap.wsseToolkitAdapter.WsseHeaderGeneratorAdapter;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.wsseTool.util.MyXmlUtils;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.LoadKeystoreException;
import gov.nist.toolkit.xdsexception.XdsFormatException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.protocol.Protocol;

//vbeera: The below imports should be used in case of the potential 2nd fix for MustUnderstand Check Exception.
/*
 import gov.nist.registry.common2.saml.builder.PatchForMustUnderstand;
 import org.apache.axis2.engine.AxisConfiguration;
 import org.apache.axis2.engine.Phase;
 */

public class Soap implements SoapInterface {
	ServiceClient serviceClient = null;
	OperationClient operationClient = null;
	OMElement result = null;
	private OMElement soapHeader = null;
	OMElement securityHeader = null;
	boolean async = false;
	String expectedReturnAction = null;
	boolean mtom = false;
	boolean addressing = true;
	boolean soap12 = true;
	boolean useWSSEC = false;

	List<OMElement> additionalHeaders = null;
	List<OMElement> secHeaders = null;
	String endpoint;
	String action;
	OMElement body = null;

	String repositoryLocation = null; // this is axis2 repository - used only
										// with useSaml / Seems used to store
										// axis modules..
	SecurityParams securityParams; // contextual security info used by SAML/TLS
									// to access the keystore

	OMElement inHeader = null;
	OMElement outHeader = null;

	public void setSecurityParams(SecurityParams securityParams) {
		this.securityParams = securityParams;
	}

	public boolean isTLS() {
		if (endpoint == null)
			return false;
		return endpoint.startsWith("https");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#getInHeader()
	 */
	public OMElement getInHeader() {
		return inHeader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#getOutHeader()
	 */
	public OMElement getOutHeader() {
		return outHeader;
	}

	public Soap() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nist.registry.common2.axis2soap.SoapInterfac#addHeader(org.apache
	 * .axiom.om.OMElement)
	 */
	public void addHeader(OMElement header) {
		if (additionalHeaders == null)
			additionalHeaders = new ArrayList<OMElement>();
		additionalHeaders.add(header);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nist.registry.common2.axis2soap.SoapInterfac#addSecHeader(org.apache
	 * .axiom.om.OMElement)
	 */
	public void addSecHeader(OMElement header) {
		if (secHeaders == null)
			secHeaders = new ArrayList<OMElement>();
		secHeaders.add(header);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#clearHeaders()
	 */
	public void clearHeaders() {
		additionalHeaders = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#setAsync(boolean)
	 */
	public void setAsync(boolean async) {
		this.async = async;
	}

	ConfigurationContext buildConfigurationContext() throws XdsInternalException, AxisFault {
		if (repositoryLocation == null)
			throw new XdsInternalException("Internal Error: Axis2 Repository not configured");

		// TODO remove rl not use anywhere -Antoine
		File rl = new File(repositoryLocation);
		if (!rl.exists() || !rl.isDirectory())
			throw new XdsInternalException("Axis2 repository location, " + repositoryLocation
					+ ", does not exist or is not a directory");
		/*
		 * File ax = new File(repositoryLocation + File.separator + "conf" +
		 * File.separator + "axis2.xml"); if (!ax.exists()) throw new
		 * XdsInternalException("Configuration file, " + ax +
		 * " does not exist");
		 */
		ConfigurationContext cc = null;
		System.out.println(" ******** repositoryLocation = [" + repositoryLocation + "]");
		try {
			cc = ConfigurationContextFactory.createConfigurationContextFromFileSystem(repositoryLocation);
		} catch (Exception e) {
			StringBuffer buf = new StringBuffer();
			buf.append("Error loading Axis2 Repository: " + e.getMessage() + "\n");

			// TODO REMOVE ?? exact same call = exact same result. I am puzzled
			// -Antoine
			cc = ConfigurationContextFactory.createConfigurationContextFromFileSystem(repositoryLocation);
			Hashtable faultyModules = cc.getAxisConfiguration().getFaultyModules();
			for (Object keyObj : faultyModules.keySet()) {
				if (keyObj instanceof String) {
					String key = (String) keyObj;
					String value = (String) faultyModules.get(key);
					buf.append(key).append(": ").append(value).append("\n");
				}
			}
			throw new XdsInternalException(buf.toString());
		}

		return cc;
	}

	SOAPEnvelope createSOAPEnvelope() throws LoadKeystoreException {
		SOAPFactory fac;

		if (this.soap12)
			fac = OMAbstractFactory.getSOAP12Factory();
		else
			fac = OMAbstractFactory.getSOAP11Factory();

		SOAPEnvelope envelope = fac.getDefaultEnvelope();

		envelope.getBody().addChild(body);

		setSoapHeader(envelope.getHeader());
		if (useWSSEC) {
			// OMNamespace ns =
			// OMAbstractFactory.getOMFactory().createOMNamespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
			// "wsse");
			// securityHeader =
			// OMAbstractFactory.getOMFactory().createOMElement("Security", ns);

			/*
			 securityHeader = WSSESecurityHeaderUtil.getWSSecOMElement(securityParams);
		
			 
			 getSoapHeader().addChild(securityHeader);
			 */
			 
			 /*
			  * FIX: When deployed under tomcat, the behavior of the axiom library differs.
			  * When the security header is added to the soap header, the security header is "detached"
			  * from its original parent, with the side-effect of removing the
			  * http://www.w3.org/2001/XMLSchema namespace declaration.
			  * Thus we need to redeclare the prefix we use in the assertion in the soap header itself
			  */
			 
		//	 getSoapHeader().declareNamespace("http://www.w3.org/2001/XMLSchema", "xs");
			 

			try {
				String keystore = securityParams.getKeystore().getAbsolutePath();
				String kpass = securityParams.getKeystorePassword();
				String alias = "1";
				String sPass = "changeit";
				
				System.out.println(keystore);
				
				org.w3c.dom.Element header = WsseHeaderGeneratorAdapter.buildHeader(keystore, kpass, alias, sPass);
				
				System.out.println("********the one in soap*************");
				MyXmlUtils.DomToStream(header, System.out);
				System.out.println("********the one in soap*************");
				
				securityHeader = org.apache.axis2.util.XMLUtils.toOM(header);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(securityHeader.toString());
			}

			getSoapHeader().addChild(securityHeader);
		
		}

		return envelope;
	}

	// if (additionalHeaders != null && additionalHeaders.size() > 0) {
	// RampartMessageData rmd;
	// try {
	// rmd = new RampartMessageData(msgCtx, true);
	// } catch (RampartException e) {
	// throw new XdsException("RampartException: " + e.getMessage(), e);
	// }
	// WSSecHeader secHdr = rmd.getSecHeader();
	// SecurityHeader sh = new SecurityHeader(secHdr.getSecurityHeader());
	// for (OMElement ele : additionalHeaders) {
	// String hdrStr = ele.toString();
	// try {
	// sh.addHeader(hdrStr);
	// }
	// catch (Exception e) {
	// throw new XdsException("Could not parse additionalHeader: error was: " +
	// e.getMessage() + "\n\tHeader was: " + hdrStr );
	// }
	// }
	// }

	// return null;

	boolean done = false;

	void waitTillDone() {
		int i = 5;

		while (!done && i > 0) {
			i--;
			waitTillDone1(500);
		}
	}

	void waitTillDone1(int milliseconds) {
		long t0, t1, diff;
		System.out.print("Waiting " + milliseconds + " milliseconds ...");
		t0 = System.currentTimeMillis();
		do {
			t1 = System.currentTimeMillis();
			diff = t1 - t0;
		} while (diff < milliseconds);
		System.out.println("Done");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#soapCallWithWSSEC()
	 */
	public void soapCallWithWSSEC() throws XdsInternalException, AxisFault, EnvironmentNotSelectedException,
			LoadKeystoreException {
		System.out.println("soapCallWithWSSEC() ----- useWSSEC :" + useWSSEC);
		ConfigurationContext cc = null;
		if (useWSSEC)
			cc = buildConfigurationContext();

		AxisService ANONYMOUS_SERVICE = null;

		serviceClient = new ServiceClient(cc, ANONYMOUS_SERVICE);

		// Start the painful process of loading the addressing module
		// Axis2 has some timing problems so, yes, this is necessary
		AxisFault lastFault = null;
		boolean finished = false;

		// TODO CHECK engaging the addressing module. Should it really be
		// engaged a each soap call? -Antoine
		// TODO CHECK is the module engagement really asynchronous ?? -Antoine
		for (int i = 0; i < 10 && !finished; i++) {
			// System.out.println("engageModule try " + i);
			try {
				lastFault = null;
				serviceClient.engageModule("addressing");
				finished = true;
			} catch (AxisFault e) {
				// System.out.println("Bill says Axis Fault was " +
				// e.getMessage());
				if (e.getMessage().indexOf("Unable to engage module : addressing") == -1) {
					// hmmm - a real error
					throw e;
				}
				// axis2 version 1.4 internal cleanup problem - try again
				// should be able to remove this with upgrade to axis2 1.5
				lastFault = e;
				try {
					synchronized (this) {
						this.wait(10);
					}
				} catch (InterruptedException e1) {
				}
			}
		}
		if (!finished)
			throw lastFault;

		// vbeera: modified code -START-
		MessageContext outMsgCtx = null;
		if (useWSSEC) {
			// operationClient =
			// serviceClient.createClient(ServiceClient.ANON_OUT_IN_OP);

			// TODO CHECK - This should not be necessary. This operation is
			// already created by the serviceClient constructor! -Antoine
			// vbeera: This is first fix found for MustUnderstand exception
			operationClient = serviceClient.createClient(ServiceClient.ANON_ROBUST_OUT_ONLY_OP);

			// vbeera: The below 2 lines is the 2nd fix/solution which is a
			// potential one. Comment the above fix and unComment the below 2
			// lines in order to use 2nd fix.
			// AxisConfiguration ac = cc.getAxisConfiguration();
			// ((Phase)ac.getInFlowPhases().get(0)).addHandler(new
			// PatchForMustUnderstand.SecurityHandler());

			// MessageContext outMsgCtx = new MessageContext();
			outMsgCtx = cc.createMessageContext();
		} else {
			// TODO CHECK - This should not be necessary. This operation is
			// already created by the serviceClient constructor! -Antoine
			operationClient = serviceClient.createClient(ServiceClient.ANON_OUT_IN_OP);
			outMsgCtx = new MessageContext();
		}
		// vbeera: modified code -END-

		Options options = outMsgCtx.getOptions();
		// options.setProperty(AddressingConstants.ADD_MUST_UNDERSTAND_TO_ADDRESSING_HEADERS,
		// Boolean.TRUE);
		// includes setting of endpoint
		setOptions(options);
		options.setProperty(AddressingConstants.ADD_MUST_UNDERSTAND_TO_ADDRESSING_HEADERS, Boolean.TRUE);// vbeera:
																											// modified

		// This creates an HTTPClient using the requested keystore and
		// truststore
		// so that different users can get what they need

		if (isTLS()) {
			try {
				// this is the overly heavy handed approach
				// Protocol.registerProtocol("https", authhttps);

				// TODO REMOVE - I guess this is dead code -Antoine
				Protocol protocol = getAuthHttpsProtocol();
				options.setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER, protocol);

			} catch (IOException e) {
				throw new XdsInternalException("Failed to create custom Protocol for TLS\n"
						+ ExceptionUtil.exception_details(e), e);
			}
		}

		// outMsgCtx.setEnvelope(createSOAPEnvelope()); //vbeera: modified
		SOAPEnvelope envelope = createSOAPEnvelope();
		if (envelope != null)
			outMsgCtx.setEnvelope(envelope);
		else
			throw new XdsInternalException("Failed to create request envelope...\n");

		if (!useWSSEC) // vbeera: added
		{
			if (additionalHeaders != null) {
				for (OMElement hdr : additionalHeaders) {
					getSoapHeader().addChild(Util.deep_copy(hdr));
				}
			}

			if (secHeaders != null && useWSSEC) {
				for (OMElement hdr : secHeaders) {
					securityHeader.addChild(sign(hdr));
				}
			}
		}

		operationClient.addMessageContext(outMsgCtx);

		boolean block = !async;

		AxisCallback callback = new AxisCallback() {

			public void onComplete() {
				done = true;
			}

			public void onError(Exception arg0) {
				done = true;
			}

			public void onFault(MessageContext arg0) {
				done = true;
			}

			public void onMessage(MessageContext arg0) {
				done = true;
			}

		};

		if (async)
			operationClient.setCallback(callback);

		System.out.println("******************************** BEFORE execute ****************************");
		operationClient.execute(block); // execute sync or async
		System.out.println("******************************** AFTER execute ****************************");

		if (async)
			waitTillDone();

		MessageContext inMsgCtx = getInputMessageContext();

		System.out.println("Operation is complete: " + operationClient.getOperationContext().isComplete());

		if (async)
			operationClient.complete(outMsgCtx);

		inMsgCtx.getEnvelope().build();

		OMElement soapBody = inMsgCtx.getEnvelope().getBody();

		soapBody.build();

		result = soapBody.getFirstElement();

		new OMFormatter(result).toString(); // this forces full read before
											// channel is closed
		// removing it breaks the reading of MTOM formatted responses

		loadOutHeader();
		loadInHeader();

		serviceClient.cleanupTransport();
		serviceClient.cleanup();

	}

	// This code is used to bypass the use of javax.net.ssl.keyStore and similar
	// JVM level controls on the certs used and specify certs on a
	// per-connection basis.

	@SuppressWarnings("deprecation")
	Protocol getAuthHttpsProtocol() throws MalformedURLException, IOException, EnvironmentNotSelectedException {
		String keyStoreFile = "file:/Users/bill/tmp/toolkit/environment/EURO2011/keystore/keystore";
		String keyStorePass = "password";
		String trustStoreFile = keyStoreFile;
		String trustStorePass = keyStorePass;
		int tlsPort = 9443;

		keyStoreFile = "file:" + securityParams.getKeystore().toString();
		keyStorePass = securityParams.getKeystorePassword();
		trustStoreFile = keyStoreFile;
		trustStorePass = keyStorePass;
		tlsPort = tlsPortFromEndpoint();

		return new Protocol("https", new AuthSSLProtocolSocketFactory(

		new URL(keyStoreFile), keyStorePass,

		new URL(trustStoreFile), trustStorePass), tlsPort);
	}

	int tlsPortFromEndpoint() throws MalformedURLException {
		if (endpoint == null)
			throw new MalformedURLException("Endpoint not set in Soap.java");
		String[] parts = endpoint.split("/");
		String hostandport = parts[2];
		if (hostandport == null || hostandport.equals(""))
			throw new MalformedURLException("Invalid endpoint set in Soap.java: " + endpoint);
		String[] cparts = hostandport.split(":");
		if (cparts.length != 2)
			return 443;
		String port = cparts[1];
		return Integer.parseInt(port);
	}

	static public void main(String[] args) {
		String keyStoreFile = "file:/Users/bill/tmp/toolkit/environment/EURO2011/keystore/keystore";
		try {
			Object x = new URL(keyStoreFile).getContent();
			System.out.println(x.getClass().getName());
			System.out.println("No Error");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#soapCall()
	 */
	public OMElement soapCall() throws LoadKeystoreException, XdsInternalException, AxisFault, XdsFormatException,
			EnvironmentNotSelectedException {

		soapCallWithWSSEC();

		MessageContext inMsgCxt = getInputMessageContext();

		verifyResponseFormat(inMsgCxt);

		// operationClient.reset();

		return result;

	}

	OMElement sign(OMElement element) throws XdsInternalException {
		byte[] in = element.toString().getBytes();

		XMLDSigProcessor dsig = new XMLDSigProcessor();

		try {
			byte[] out = dsig.signSAMLAssertionsEnveloped(in);

			OMElement outEle = Util.parse_xml(new ByteArrayInputStream(out));

			return outEle;
		} catch (Exception e) {
			throw new XdsInternalException(e.getMessage(), e);
		}
	}

	MessageContext getInputMessageContext() throws XdsInternalException, AxisFault {
		if (operationClient == null) {
			Object in = serviceClient.getServiceContext().getLastOperationContext().getMessageContexts().get("In");
			if (!(in instanceof MessageContext))
				throw new XdsInternalException("Soap: In MessageContext of type " + in.getClass().getName()
						+ " instead of MessageContext");
			MessageContext inMsgCxt = (MessageContext) in;
			return inMsgCxt;
		}
		return operationClient.getMessageContext("In");

	}

	private void verifyResponseFormat(MessageContext inMsgCxt) throws XdsFormatException, XdsInternalException {
		boolean responseMtom = inMsgCxt.isDoingMTOM();

		if (mtom != responseMtom)
			if (mtom) {
				throw new XdsFormatException("Request was MTOM format but response was SIMPLE SOAP",
						WsDocRef.MTOM_in_MTOM_out);
			} else {
				throw new XdsFormatException("Request was SIMPLE SOAP but response was MTOM", WsDocRef.MTOM_in_MTOM_out);
			}

		// toolkit sometimes depends on Synapse ESB to translate async to sync
		// for getting through firewalls
		// sometimes Synapse gets subborn about passing on the correct WS:action
		// on return
		if (async)
			verify_returned_action(expectedReturnAction, "urn:mediateResponse");
		else
			verify_returned_action(expectedReturnAction, null);
	}

	/*
	 * Set options for the current message. TODO CHANGE this should be set once
	 * for the execution! TODO CHANGE hardcoded parameters should be accessible
	 * in some easily identifiable place. What do they mean? -Antoine
	 */
	void setOptions(Options opts) throws AxisFault {
		opts.setTo(new EndpointReference(endpoint));

		if (System.getenv("XDSHTTP10") != null) {
			System.out.println("Generating HTTP 1.0");

			opts.setProperty(org.apache.axis2.transport.http.HTTPConstants.HTTP_PROTOCOL_VERSION,
					org.apache.axis2.transport.http.HTTPConstants.HEADER_PROTOCOL_10);

			opts.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);

		}

		opts.setProperty(Constants.Configuration.ENABLE_MTOM, ((mtom) ? Constants.VALUE_TRUE : Constants.VALUE_FALSE));

		// TODO CHECK WS-Addressing Action / SOAP Action string - what does this
		// really mean? -Antoine
		opts.setAction(action);

		// ***COMMENTED BY vbeera
		/*
		 * if (addressing) { AxisFault lastFault = null; boolean done = false;
		 * for (int i=0; i<10 && !done; i++) { //
		 * System.out.println("engageModule (kinda) try " + i); try { lastFault
		 * = null; serviceClient.engageModule("addressing"); done = true; }
		 * catch (AxisFault e) { //
		 * System.out.println("Bill says Axis Fault was " + e.getMessage()); if
		 * (e.getMessage().indexOf("Unable to engage module : addressing") == -1
		 * ) { // hmmm - a real error throw e; } // axis2 version 1.4 internal
		 * cleanup problem - try again // should be able to remove this with
		 * upgrade to axis2 1.5 lastFault = e; try { synchronized(this) {
		 * this.wait(10); } } catch (InterruptedException e1) { } } } if (!done)
		 * throw lastFault;
		 * 
		 * // serviceClient.engageModule("addressing"); } else {
		 * serviceClient.disengageModule("addressing"); // this does not work in
		 * Axis2 yet }
		 */
		opts.setSoapVersionURI(((soap12) ? SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI
				: SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI));

		opts.setUseSeparateListener(async);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#getResult()
	 */
	public OMElement getResult() {
		return result;
	}

	void verify_returned_action(String expected_return_action, String alternate_return_action)
			throws XdsInternalException {
		if (expected_return_action == null)
			return;

		OMElement hdr = getInHeader();
		if (hdr == null && expected_return_action != null)
			throw new XdsInternalException("No SOAPHeader returned: expected header with action = "
					+ expected_return_action);
		OMElement action = MetadataSupport.firstChildWithLocalName(hdr, "Action");
		if (action == null && expected_return_action != null)
			throw new XdsInternalException("No action returned in SOAPHeader: expected action = "
					+ expected_return_action);
		String action_value = action.getText();
		if (alternate_return_action == null) {
			if (action_value == null || !action_value.equals(expected_return_action))
				throw new XdsInternalException("Wrong action returned in SOAPHeader: expected action = "
						+ expected_return_action + " returned action = " + action_value);
		} else {
			if (action_value == null
					|| ((!action_value.equals(expected_return_action)) && (!action_value
							.equals(alternate_return_action))))
				throw new XdsInternalException("Wrong action returned in SOAPHeader: expected action = "
						+ expected_return_action + " returned action = " + action_value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nist.registry.common2.axis2soap.SoapInterfac#soapCall(org.apache.
	 * axiom.om.OMElement, java.lang.String, boolean, boolean, boolean,
	 * java.lang.String, java.lang.String)
	 */
	public OMElement soapCall(OMElement body, String endpoint, boolean mtom, boolean addressing, boolean soap12,
			String action, String expected_return_action) throws XdsInternalException, AxisFault, XdsFormatException,
			EnvironmentNotSelectedException, LoadKeystoreException {

		this.expectedReturnAction = expected_return_action;
		this.mtom = mtom;
		this.addressing = addressing;
		this.soap12 = soap12;
		this.endpoint = endpoint;
		this.action = action;
		this.body = body;

		return soapCall();
	}

	void loadInHeader() throws XdsInternalException {
		if (serviceClient == null)
			return;
		OperationContext oc = serviceClient.getLastOperationContext();
		if (oc == null)
			return;
		HashMap<String, MessageContext> ocs = oc.getMessageContexts();
		MessageContext in = ocs.get("In");

		if (in == null)
			return;

		if (in.getEnvelope() == null)
			return;

		if (in.getEnvelope().getHeader() == null)
			return;

		inHeader = Util.deep_copy(in.getEnvelope().getHeader());
	}

	void loadOutHeader() throws XdsInternalException {
		if (serviceClient == null)
			return;
		OperationContext oc = serviceClient.getLastOperationContext();
		if (oc == null)
			return;
		HashMap<String, MessageContext> ocs = oc.getMessageContexts();
		MessageContext out = ocs.get("Out");

		if (out == null)
			return;

		outHeader = Util.deep_copy(out.getEnvelope().getHeader());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nist.registry.common2.axis2soap.SoapInterfac#getExpectedReturnAction
	 * ()
	 */
	public String getExpectedReturnAction() {
		return expectedReturnAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nist.registry.common2.axis2soap.SoapInterfac#setExpectedReturnAction
	 * (java.lang.String)
	 */
	public void setExpectedReturnAction(String expectedReturnAction) {
		this.expectedReturnAction = expectedReturnAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#isMtom()
	 */
	public boolean isMtom() {
		return mtom;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#setMtom(boolean)
	 */
	public void setMtom(boolean mtom) {
		this.mtom = mtom;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#isAddressing()
	 */
	public boolean isAddressing() {
		return addressing;
	}

	// public void soapSend(OMElement body, String endpoint, boolean mtom,
	// boolean addressing, boolean soap12, String action)
	// throws XdsException, AxisFault {
	//
	// this.expectedReturnAction = null;
	// this.mtom = mtom;
	// this.addressing = addressing;
	// this.soap12 = soap12;
	//
	// soapSend(body, endpoint, action);
	// }

	// public void soapSend(OMElement body, String endpoint,
	// String action)
	// throws XdsException, AxisFault {
	//
	// // if (1 == 1) {
	// // soapCall(body, endpoint, action);
	// // return;
	// // }
	//
	// // try {
	// if (serviceClient == null)
	// serviceClient = new ServiceClient();
	//
	// serviceClient.getOptions().setTo(new EndpointReference(endpoint));
	//
	// if (System.getenv("XDSHTTP10") != null) {
	// System.out.println("Generating HTTP 1.0");
	//
	// serviceClient.getOptions().setProperty
	// (org.apache.axis2.transport.http.HTTPConstants.HTTP_PROTOCOL_VERSION,
	// org.apache.axis2.transport.http.HTTPConstants.HEADER_PROTOCOL_10);
	//
	// serviceClient.getOptions().setProperty
	// (org.apache.axis2.transport.http.HTTPConstants.CHUNKED,
	// Boolean.FALSE);
	//
	// }
	//
	// serviceClient.getOptions().setProperty(Constants.Configuration.ENABLE_MTOM,
	// ((mtom) ? Constants.VALUE_TRUE : Constants.VALUE_FALSE));
	//
	// serviceClient.getOptions().setAction(action);
	// if (addressing) {
	// serviceClient.engageModule("addressing");
	// } else {
	// serviceClient.disengageModule("addressing"); // this does not work in
	// Axis2 yet
	// }
	//
	// serviceClient.getOptions().setSoapVersionURI(
	// ((soap12) ? SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI :
	// SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI)
	// );
	// // System.out.println("fire and forget " + endpoint);
	// // serviceClient.fireAndForget(body);
	//
	//
	// System.out.println("sendRobust " + endpoint);
	// serviceClient.sendRobust(body);
	//
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nist.registry.common2.axis2soap.SoapInterfac#setAddressing(boolean)
	 */
	public void setAddressing(boolean addressing) {
		this.addressing = addressing;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#isSoap12()
	 */
	public boolean isSoap12() {
		return soap12;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#setSoap12(boolean)
	 */
	public void setSoap12(boolean soap12) {
		this.soap12 = soap12;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#isAsync()
	 */
	public boolean isAsync() {
		return async;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#setUseSaml(boolean)
	 */
	public void setUseSaml(boolean use) {
		useWSSEC = use;
	}

	public void setRepositoryLocation(String location) {
		repositoryLocation = location;
	}

	public OMElement getSoapHeader() {
		return soapHeader;
	}

	public void setSoapHeader(OMElement soapHeader) {
		this.soapHeader = soapHeader;
	}

}
