package gov.nist.toolkit.soap.axis2;

import gov.nist.toolkit.docref.WsDocRef;
import gov.nist.toolkit.dsig.XMLDSigProcessor;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.securityCommon.SecurityParamsImpl;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.LoadKeystoreException;
import gov.nist.toolkit.xdsexception.XdsFormatException;
import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
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
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;




//import gov.nist.toolkit.soap.wsseToolkitAdapter.WsseHeaderGeneratorAdapter;

//vbeera: The below imports should be used in case of the potential 2nd fix for MustUnderstand Check Exception.
/*
 import gov.nist.registry.common2.saml.builder.PatchForMustUnderstand;
 import org.apache.axis2.engine.AxisConfiguration;
 import org.apache.axis2.engine.Phase;
 */

public class Soap implements SoapInterface {
    static Logger logger = Logger.getLogger(Soap.class);

	private static Logger log = Logger.getLogger(Soap.class);

	int timeout = 1000 * 60 * 60;
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
	List<OMElement> additionalHeaders = null;

	// This doesn't seem to be no longer used for the original purpose of conveying a home brewed saml
	boolean useWSSEC = false;
	boolean useSaml;
	String gazelleXuaUsername;
	List<OMElement> secHeaders = null;

	String endpoint;
	String action;
	OMElement body = null;

	private Map<String, String> params;

	String repositoryLocation = null; // this is axis2 repository - used only
										// with useSaml / Seems used to store
										// axis modules..
	SecurityParams securityParams; // contextual security info used by SAML/TLS
									// to access the keystore

	// so they can be logged by the caller
	OMElement inHeader = null;
	OMElement outHeader = null;

	public void setSecurityParams(SecurityParams securityParams) {
		this.securityParams = securityParams;
	}

	private void installDefaultSecurityParamsIfNeeded() {
		if (securityParams != null)
			return;
		this.securityParams = new SecurityParamsImpl(Installation.instance().defaultEnvironmentName());
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
	@Override
   public OMElement getInHeader() {
		return inHeader;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#getOutHeader()
	 */
	@Override
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
	@Override
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
	@Override
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
	@Override
   public void clearHeaders() {
		additionalHeaders = null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#setAsync(boolean)
	 */
	@Override
   public void setAsync(boolean async) {
		this.async = async;
	}

	ConfigurationContext buildConfigurationContext()
			throws XdsInternalException, AxisFault {
		if (repositoryLocation == null)
			throw new XdsInternalException(
					"Internal Error: Axis2 Repository not configured");

		// - remove rl not use anywhere -Antoine
		File rl = new File(repositoryLocation);
		if (!rl.exists() || !rl.isDirectory())
			throw new XdsInternalException("Axis2 repository location, "
					+ repositoryLocation
					+ ", does not exist or is not a directory");
		/*
		 * File ax = new File(repositoryLocation + File.separator + "conf" +
		 * File.separator + "axis2.xml"); if (!ax.exists()) throw new
		 * XdsInternalException("Configuration file, " + ax +
		 * " does not exist");
		 */
		ConfigurationContext cc = null;
		System.out.println(" ******** repositoryLocation = ["
				+ repositoryLocation + "]");
		try {
			cc = ConfigurationContextFactory
					.createConfigurationContextFromFileSystem(repositoryLocation);
		} catch (Exception e) {
			StringBuffer buf = new StringBuffer();
			buf.append("Error loading Axis2 Repository: " + e.getMessage()
					+ "\n");

			// - REMOVE ?? exact same call = exact same result. I am puzzled
			// -Antoine
			cc = ConfigurationContextFactory
					.createConfigurationContextFromFileSystem(repositoryLocation);
			Hashtable faultyModules = cc.getAxisConfiguration()
					.getFaultyModules();
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
//		if (useWSSEC) {
//			try {
//				String store = securityParams.getKeystore().getAbsolutePath();
//				String kPass = securityParams.getKeystorePassword();
//				String alias = "1";
//				String sPass = "changeit";
//
//				KeystoreAccess keystore = new KeystoreAccess(store, sPass,
//						alias, kPass);
//				SecurityContext context = SecurityContextFactory.getInstance();
//				context.setKeystore(keystore);
//
//				String pid = this.params.get("$patientid$");
//				parsePid(pid, context);
//				context.getParams().put("endpoint", this.endpoint);
//
//				org.w3c.dom.Element header = WsseHeaderGeneratorAdapter
//						.buildHeader(context);
//
//				securityHeader = org.apache.axis2.util.XMLUtils.toOM(header);
//				getSoapHeader().addChild(securityHeader);
//
//			} catch (Exception e) {
//				log.error(
//						"!! error while trying to generate security header !!",
//						e);
//			}
//
//		}

		return envelope;
	}

//	private void parsePid(String pid, SecurityContext context) {
//
//			try {
//				if(pid == null || pid.equals("")){
//					throw new Exception("cannot retrieve params from the planContext in the soap layer");
//				}
//
//				String hid = pid.split("&")[1];
//
//				if(hid == null || pid.equals("")){
//					throw new Exception("cannot parse patient_id to retrieve home_community_id");
//				}
//
//				log.info("param patientId" + pid + " passed to the saml header generator");
//				log.info("homeCommunityId" + hid + " passed to the saml header generator");
//				context.getParams().put("patientId", pid);
//				context.getParams().put("homeCommunityId", "urn:oid:"+ hid);
//
//			} catch (Exception e) {
//				log.error(e.getMessage());
//			}
//
//	}

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
	@Override
   public void soapCallWithWSSEC() throws XdsInternalException, AxisFault,
            EnvironmentNotSelectedException, LoadKeystoreException {
		System.out.println("soapCallWithWSSEC() ----- useWSSEC :" + useWSSEC);
		installDefaultSecurityParamsIfNeeded();
		ConfigurationContext cc = null;
		if (useWSSEC)
			cc = buildConfigurationContext();

		AxisService ANONYMOUS_SERVICE = null;

  		serviceClient = new ServiceClient(cc, ANONYMOUS_SERVICE);

		// Start the painful process of loading the addressing module
		// Axis2 has some timing problems so, yes, this is necessary
		AxisFault lastFault = null;
		boolean finished = false;

		// - CHECK engaging the addressing module. Should it really be
		// engaged a each soap call? -Antoine
		// - CHECK is the module engagement really asynchronous ?? -Antoine
		for (int i = 0; i < 10 && !finished; i++) {
			// System.out.println("engageModule try " + i);
			try {
				lastFault = null;
				serviceClient.engageModule("addressing");
				finished = true;
			} catch (AxisFault e) {
				// System.out.println("Bill says Axis Fault was " +
				// e.getMessage());
				if (e.getMessage().indexOf(
						"Unable to engage module : addressing") == -1) {
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

			//  CHECK - This should not be necessary. This operation is
			// already created by the serviceClient constructor! -Antoine
			// vbeera: This is first fix found for MustUnderstand exception
			operationClient = serviceClient
					.createClient(ServiceClient.ANON_ROBUST_OUT_ONLY_OP);

			// vbeera: The below 2 lines is the 2nd fix/solution which is a
			// potential one. Comment the above fix and unComment the below 2
			// lines in order to use 2nd fix.
			// AxisConfiguration ac = cc.getAxisConfiguration();
			// ((Phase)ac.getInFlowPhases().get(0)).addHandler(new
			// PatchForMustUnderstand.SecurityHandler());

			// MessageContext outMsgCtx = new MessageContext();
			outMsgCtx = cc.createMessageContext();
		} else {
			//  CHECK - This should not be necessary. This operation is
			// already created by the serviceClient constructor! -Antoine
			operationClient = serviceClient
					.createClient(ServiceClient.ANON_OUT_IN_OP);
			outMsgCtx = new MessageContext();
		}
		// vbeera: modified code -END-

		Options options = outMsgCtx.getOptions();
		// options.setProperty(AddressingConstants.ADD_MUST_UNDERSTAND_TO_ADDRESSING_HEADERS,
		// Boolean.TRUE);
		// includes setting of endpoint
		setOptions(options);
        setMaxConnections();
        options.setProperty(
				AddressingConstants.ADD_MUST_UNDERSTAND_TO_ADDRESSING_HEADERS,
				Boolean.TRUE);// vbeera:
								// modified

		// This creates an HTTPClient using the requested keystore and
		// truststore
		// so that different users can get what they need

		if (isTLS()) {
			try {
				// this is the overly heavy handed approach
				// Protocol.registerProtocol("https", authhttps);

				//  REMOVE - I guess this is dead code -Antoine
				Protocol protocol = getAuthHttpsProtocol();
				options.setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER,
						protocol);

			} catch (IOException e) {
				throw new XdsInternalException(
						"Failed to create custom Protocol for TLS\n"
								+ ExceptionUtil.exception_details(e), e);
			}
		}

		// outMsgCtx.setEnvelope(createSOAPEnvelope()); //vbeera: modified
		SOAPEnvelope envelope = createSOAPEnvelope();
		if (envelope != null)
			outMsgCtx.setEnvelope(envelope);
		else
			throw new XdsInternalException(
					"Failed to create request envelope...\n");

//		if (!useWSSEC) // vbeera: added
//		{
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
//			}


		operationClient.addMessageContext(outMsgCtx);

		boolean block = !async;

		AxisCallback callback = new AxisCallback() {

			@Override
         public void onComplete() {
				done = true;
			}

			@Override
         public void onError(Exception arg0) {
				done = true;
			}

			@Override
         public void onFault(MessageContext arg0) {
				done = true;
			}

			@Override
         public void onMessage(MessageContext arg0) {
				done = true;
			}

		};

		if (async)
			operationClient.setCallback(callback);

		log.info(String.format("******************************** BEFORE SOAP SEND to %s ****************************", endpoint));
        AxisFault soapFault = null;
        long start = 0;
		try {
		   start = System.nanoTime();
			operationClient.execute(block); // execute sync or async
//		} catch (AxisFault e) {
//            soapFault = e;
//            operationClient.execute(block); // execute sync or async
        } catch (AxisFault e) {
           logger.debug("$$$$$ Timeout: " + timeout + ", Elapsed time: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) / 1000.0 + " milliseconds");
           soapFault = e;
            MessageContext inMsgCtx = getInputMessageContext();
            OMElement soapBody = inMsgCtx.getEnvelope().getBody();
            result = soapBody.getFirstElement();
            logger.info(new OMFormatter(result).toString());
        }
//        catch (Exception e) {
//            soapFault = e;
//            logger.info("SOAP Fault received - " + e.getClass().getName());
//        }
        finally {
			log.info(String.format("******************************** AFTER SOAP SEND to %s ****************************", endpoint));

			if (async)
				waitTillDone();

			MessageContext inMsgCtx = getInputMessageContext();

			System.out.println("Operation is complete: "
					+ operationClient.getOperationContext().isComplete());

			if (async)
				operationClient.complete(outMsgCtx);

            loadOutHeader();

            if (soapFault != null) {
                throw new XdsInternalException("SOAP Fault: " + soapFault.getReason(), soapFault);
            }
			//  - null pointer exception here if port number in configuration is wrong
			try {
				inMsgCtx.getEnvelope().build();
			} catch (NullPointerException e) {
				throw new XdsInternalException("Service not available on this host:port (" + endpoint + ")");
			}

			OMElement soapBody = inMsgCtx.getEnvelope().getBody();

			soapBody.build();

			result = soapBody.getFirstElement();

			new OMFormatter(result).toString(); // this forces full read before
			// channel is closed
			// removing it breaks the reading of MTOM formatted responses

			loadInHeader();

			serviceClient.cleanupTransport();
			serviceClient.cleanup();
            logger.info("soapCallWithWSSEC done");
		}
	}

	// This code is used to bypass the use of javax.net.ssl.keyStore and similar
	// JVM level controls on the certs used and specify certs on a
	// per-connection basis.

	@SuppressWarnings("deprecation")
	Protocol getAuthHttpsProtocol() throws MalformedURLException, IOException,
			EnvironmentNotSelectedException {
		String keyStoreFile = "file:/Users/bill/tmp/toolkit/environment/EURO2011/keystore/keystore";
		String keyStorePass = "password";
		String trustStoreFile = keyStoreFile;
		String trustStorePass = keyStorePass;
		int tlsPort = 9443;

		if (securityParams == null)
			throw new EnvironmentNotSelectedException("Trying to initiate a TLS connection - securityParams are null");
		if (securityParams.getKeystore() == null || securityParams.getKeystore().equals(""))
			throw new EnvironmentNotSelectedException("Trying to initialize a TLS connection - keystore location not recorded in securityParams");
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
			throw new MalformedURLException(
					"Invalid endpoint set in Soap.java: " + endpoint);
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#soapCall()
	 */
	@Override
   public OMElement soapCall() throws LoadKeystoreException,
			XdsInternalException, AxisFault, XdsFormatException,
			EnvironmentNotSelectedException {

		soapCallWithWSSEC();

		MessageContext inMsgCxt = getInputMessageContext();

		verifyResponseFormat(inMsgCxt);

		// operationClient.reset();

        logger.info("soepCall done");
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

	MessageContext getInputMessageContext() throws XdsInternalException,
			AxisFault {
		if (operationClient == null) {
			Object in = serviceClient.getServiceContext()
					.getLastOperationContext().getMessageContexts().get("In");
			if (!(in instanceof MessageContext))
				throw new XdsInternalException(
						"Soap: In MessageContext of type "
								+ in.getClass().getName()
								+ " instead of MessageContext");
			MessageContext inMsgCxt = (MessageContext) in;
			return inMsgCxt;
		}
		return operationClient.getMessageContext("In");

	}

	private void verifyResponseFormat(MessageContext inMsgCxt)
			throws XdsFormatException, XdsInternalException {
		boolean responseMtom = inMsgCxt.isDoingMTOM();

		if (mtom != responseMtom)
			if (mtom) {
				throw new XdsFormatException(
						"Request was MTOM format but response was SIMPLE SOAP",
						WsDocRef.MTOM_in_MTOM_out);
			} else {
				throw new XdsFormatException(
						"Request was SIMPLE SOAP but response was MTOM",
						WsDocRef.MTOM_in_MTOM_out);
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
	 * Set options for the current message.
	 * CHANGE this should be set once
	 * for the execution!
	 * CHANGE hardcoded parameters should be accessible
	 * in some easily identifiable place. What do they mean? -Antoine
	 */
	@SuppressWarnings("restriction")
   void setOptions(Options opts) throws AxisFault {
		opts.setTo(new EndpointReference(endpoint));

		if (System.getenv("XDSHTTP10") != null) {
			System.out.println("Generating HTTP 1.0");

			opts.setProperty(
					org.apache.axis2.transport.http.HTTPConstants.HTTP_PROTOCOL_VERSION,
					org.apache.axis2.transport.http.HTTPConstants.HEADER_PROTOCOL_10);

			opts.setProperty(
					org.apache.axis2.transport.http.HTTPConstants.CHUNKED,
					Boolean.FALSE);

		}

		opts.setProperty(Constants.Configuration.ENABLE_MTOM,
				((mtom) ? Constants.VALUE_TRUE : Constants.VALUE_FALSE));

		// CHECK WS-Addressing Action / SOAP Action string - what does this
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

    // Set the max connections and timeout - needed because by default you can only have
    // two connections to a single host.  This doesn't work with simulators in toolkit.
    void setMaxConnections() {
        MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setDefaultMaxConnectionsPerHost(50);
        params.setMaxTotalConnections(50);
        params.setSoTimeout(20000);
        params.setConnectionTimeout(20000);
        multiThreadedHttpConnectionManager.setParams(params);
        HttpClient httpClient = new HttpClient(multiThreadedHttpConnectionManager);
        serviceClient.getServiceContext().getConfigurationContext().setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#getResult()
	 */
	@Override
   public OMElement getResult() {
		return result;
	}

	void verify_returned_action(String expected_return_action,
			String alternate_return_action) throws XdsInternalException {
		if (expected_return_action == null)
			return;

		OMElement hdr = getInHeader();
		if (hdr == null && expected_return_action != null)
			throw new XdsInternalException(
					"No SOAPHeader returned: expected header with action = "
							+ expected_return_action);
		OMElement action = XmlUtil.firstChildWithLocalName(hdr, "Action");
		if (action == null && expected_return_action != null)
			throw new XdsInternalException(
					"No action returned in SOAPHeader: expected action = "
							+ expected_return_action);
		String action_value = action.getText();
		if (alternate_return_action == null) {
			if (action_value == null
					|| !action_value.equals(expected_return_action))
				throw new XdsInternalException(
						"Wrong action returned in SOAPHeader: expected action = "
								+ expected_return_action
								+ " returned action = " + action_value);
		} else {
			if (action_value == null
					|| ((!action_value.equals(expected_return_action)) && (!action_value
							.equals(alternate_return_action))))
				throw new XdsInternalException(
						"Wrong action returned in SOAPHeader: expected action = "
								+ expected_return_action
								+ " returned action = " + action_value);
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
	@Override
	public OMElement soapCall(OMElement body, String endpoint, boolean mtom,
			boolean addressing, boolean soap12, String action,
			String expected_return_action) throws XdsInternalException,
			AxisFault, XdsFormatException, EnvironmentNotSelectedException,
			LoadKeystoreException {
		installDefaultSecurityParamsIfNeeded();
		return soapCall(body, endpoint, mtom, addressing, soap12,
				action, expected_return_action, null);
	}

	public OMElement soapCall(OMElement body, String endpoint, boolean mtom,
			boolean addressing, boolean soap12, String action,
			String expected_return_action, Map<String, String> linkage)
			throws XdsInternalException, AxisFault, XdsFormatException,
			EnvironmentNotSelectedException, LoadKeystoreException {

		installDefaultSecurityParamsIfNeeded();
		this.expectedReturnAction = expected_return_action;
		this.mtom = mtom;
		this.addressing = addressing;
		this.soap12 = soap12;
		this.endpoint = endpoint;
		this.action = action;
		this.body = body;
		this.params = (linkage == null) ? new HashMap<String, String>() : linkage;
//		log.info("params in soap : " + params.toString());
//		log.info("pid in soap :" + params.get("$patientid$"));

		if (endpoint == null || endpoint.equals(""))
			throw new XdsInternalException("No endpoint configured for SOAP action " + action);

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
        logger.info("incoming header loaded");
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
	@Override
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
	@Override
   public void setExpectedReturnAction(String expectedReturnAction) {
		this.expectedReturnAction = expectedReturnAction;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#isMtom()
	 */
	@Override
   public boolean isMtom() {
		return mtom;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#setMtom(boolean)
	 */
	@Override
   public void setMtom(boolean mtom) {
		this.mtom = mtom;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#isAddressing()
	 */
	@Override
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
	@Override
   public void setAddressing(boolean addressing) {
		this.addressing = addressing;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#isSoap12()
	 */
	@Override
   public boolean isSoap12() {
		return soap12;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#setSoap12(boolean)
	 */
	@Override
   public void setSoap12(boolean soap12) {
		this.soap12 = soap12;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#isAsync()
	 */
	@Override
   public boolean isAsync() {
		return async;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.nist.registry.common2.axis2soap.SoapInterfac#setUseSaml(boolean)
	 */
	@Override
   public void setUseSaml(boolean use) {
//		useWSSEC = use;
		useSaml = use;
	}


	public String getGazelleXuaUsername() {
		return gazelleXuaUsername;
	}

	public void setGazelleXuaUsername(String gazelleXuaUsername) {
		this.gazelleXuaUsername = gazelleXuaUsername;
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
