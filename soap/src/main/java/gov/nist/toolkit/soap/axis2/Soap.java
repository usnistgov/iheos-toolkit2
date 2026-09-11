package gov.nist.toolkit.soap.axis2;

import gov.nist.toolkit.docref.WsDocRef;
import gov.nist.toolkit.dsig.XMLDSigProcessor;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.securityCommon.SecurityParamsImpl;
import gov.nist.toolkit.soap.axis2.handlers.header.security.BypassMustUnderstand;
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
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.*;
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
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.Phase;
import org.apache.axis2.kernel.http.HTTPConstants;
import org.apache.commons.httpclient.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;




//import gov.nist.toolkit.soap.wsseToolkitAdapter.WsseHeaderGeneratorAdapter;

//vbeera: The below imports should be used in case of the potential 2nd fix for MustUnderstand Check Exception.
/*
 import gov.nist.registry.common2.saml.builder.PatchForMustUnderstand;
 import org.apache.axis2.engine.AxisConfiguration;
 import org.apache.axis2.engine.Phase;
 */

public class Soap implements SoapInterface {

    private static Logger logger = Logger.getLogger(Soap.class.getName());

	int timeout = 1000 * 60 * 60;
        int defaultSocketTimeout = 20;	// 20 seconds
        int defaultConnectTimeout = 20;	// 20 seconds
        int deployedSocketTimeout = -1;
        int deployedConnectTimeout = -1;
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
	boolean useTimestampProxy = false;

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
		return buildConfigurationContext(repositoryLocation);
	}

	ConfigurationContext buildConfigurationContext(String location)
			throws XdsInternalException, AxisFault {
		if (location == null)
			throw new XdsInternalException(
					"Internal Error: Axis2 Repository not configured");

		// - remove rl not use anywhere -Antoine
		File rl = new File(location);
		if (!rl.exists() || !rl.isDirectory())
			throw new XdsInternalException("Axis2 repository location, "
					+ location
					+ ", does not exist or is not a directory");
		/*
		 * File ax = new File(repositoryLocation + File.separator + "conf" +
		 * File.separator + "axis2.xml"); if (!ax.exists()) throw new
		 * XdsInternalException("Configuration file, " + ax +
		 * " does not exist");
		 */
		ConfigurationContext cc = null;
		System.out.println(" ******** repositoryLocation = ["
				+ location + "]");
		try {
			cc = ConfigurationContextFactory
					.createConfigurationContextFromFileSystem(location);
		} catch (Exception e) {
			StringBuffer buf = new StringBuffer();
			buf.append("Error loading Axis2 Repository: " + e.getMessage()
					+ "\n");

			// - REMOVE ?? exact same call = exact same result. I am puzzled
			// -Antoine
			cc = ConfigurationContextFactory
					.createConfigurationContextFromFileSystem(location);
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

	ConfigurationContext buildConfigurationContextIfAvailable()
			throws XdsInternalException, AxisFault {
		String location = getRepositoryLocationIfAvailable();
		if (location == null)
			return null;
		return buildConfigurationContext(location);
	}

	private String getRepositoryLocationIfAvailable()
			throws XdsInternalException {
		if (repositoryLocation != null && repositoryLocation.trim().length() > 0)
			return repositoryLocation;

		URL axis2Xml = Soap.class.getClassLoader().getResource("axis2.xml");
		if (axis2Xml == null || !"file".equals(axis2Xml.getProtocol()))
			return null;

		try {
			File axis2XmlFile = new File(axis2Xml.toURI());
			File repository = axis2XmlFile.getParentFile();
			if (repository == null)
				return null;
			File modules = new File(repository, "modules");
			if (!modules.isDirectory())
				return null;
			return repository.getAbsolutePath();
		} catch (URISyntaxException e) {
			throw new XdsInternalException(
					"Error resolving Axis2 Repository from classpath: "
							+ axis2Xml, e);
		}
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
		ConfigurationContext cc = buildConfigurationContextIfAvailable();

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
			outMsgCtx = serviceClient.getServiceContext()
					.getConfigurationContext().createMessageContext();
		}
		// vbeera: modified code -END-

        if (Installation.instance().propertyServiceManager().getPropertyManager().isBypassSecurityHeaderMuOnResponse()) {
			// Sequoia: begin attach handler
			AxisConfiguration axisConfiguration = serviceClient.getAxisConfiguration();
			if (axisConfiguration != null) {
				List<Phase> inPhases = axisConfiguration.getInFlowPhases();
				if (inPhases != null) {
					for (Phase p : inPhases) {
						if ("Security".equals(p.getPhaseName())) {
							p.addHandler(new BypassMustUnderstand(), p.getHandlerCount());
						}
					}
				}
			}
		}
		// end

		Options options = operationClient.getOptions();
		// options.setProperty(AddressingConstants.ADD_MUST_UNDERSTAND_TO_ADDRESSING_HEADERS,
		// Boolean.TRUE);
		// includes setting of endpoint
		setOptions(options);
		outMsgCtx.setOptions(options);
        loadTimeoutValues();
        setMaxConnections();
        options.setProperty(
				AddressingConstants.ADD_MUST_UNDERSTAND_TO_ADDRESSING_HEADERS,
				Boolean.TRUE);// vbeera:
								// modified

		if (useTimestampProxy) {
			List<Header> headers = new ArrayList<>();
			Header header = new Header();
			header.setName("Host");
			header.setValue(timestampProxyString());
		}

		// TLS setup is applied to the cached HttpClient 4 client in setMaxConnections().

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
//					getSoapHeader().addChild(Util.deep_copy(hdr));
					getSoapHeader().addChild(hdr);
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



		logger.info(String.format("******************************** BEFORE SOAP SEND to %s ****************************", endpoint));
		// This line added for Java 17 and new libraries.
		// Without this, we have a problem with the logging software trying to read nodes a second time.
		String k = envelope.toString();
		// End workaround for node caching issue.

        AxisFault soapFault = null;
		OMException networkFault = null;
        RuntimeException runtimeFault = null;
        Throwable sendFailure = null;
        long start = 0;
		try {
		   start = System.nanoTime();
			operationClient.execute(block); // execute sync or async
        } catch (AxisFault e) {
           logger.warning("$$$$$ AxisFault: with timeout of " + deployedSocketTimeout + ", Elapsed time: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) / 1000.0 + " seconds");
           soapFault = e;
           logger.warning(ExceptionUtil.exception_details(soapFault));
            MessageContext inMsgCtx = getInputMessageContext();
            OMElement soapBody = inMsgCtx.getEnvelope().getBody();
            result = soapBody.getFirstElement();
            logger.info(new OMFormatter(result).toString());
        } catch (OMException e) {
			logger.warning("org.apache.axiom.om.OMException fault: " + e.toString());
			networkFault = e;
			SOAPEnvelope myEnvelope = getInputMessageContext().getEnvelope();
			OMElement soapBody = myEnvelope.getBody();
			// The code in the catch block above wants to pull soapBoady.getFirstElement();
			// Unfortunately, when we are here, the inMsgCtx.getEnvelope() method returns null;
			//			result = soapBody.getFirstElement();
			//			logger.info(new OMFormatter(result).toString());
        } catch (RuntimeException e) {
            runtimeFault = e;
        } catch (Throwable e) {
            sendFailure = e;
        }
        finally {
			logger.info(String.format("******************************** AFTER SOAP SEND to %s ****************************", endpoint));

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
            if (networkFault != null) {
            	throw new XdsInternalException("Network connection fault: " + networkFault.toString(), networkFault);
			}
            if (runtimeFault != null) {
                throw new XdsInternalException("SOAP send failed before a response was received: " + runtimeFault, runtimeFault);
            }
            if (sendFailure != null) {
                throw new XdsInternalException("SOAP send failed before a response was received: " + sendFailure, sendFailure);
            }
			if (inMsgCtx == null || inMsgCtx.getEnvelope() == null) {
				throw new XdsInternalException("Toolkit Exception: No SOAP response message received from " + endpoint);
			}
			inMsgCtx.getEnvelope().build();

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

	private String timestampProxyString() {
		boolean isTls = endpoint.startsWith("https");
		String parts[] = endpoint.split("/");
		String hostAndPort = parts[2];
		String partx[] = hostAndPort.split(":");
		String host = partx[0];
		String port = partx[1];
		return host + " " + port + " " + ((isTls) ? "tls" : "");
	}

	// Build a per-client SSL context instead of relying on JVM-level
	// javax.net.ssl.keyStore / trustStore properties.
	SSLContext getAuthSslContext() throws IOException,
			EnvironmentNotSelectedException {
		String keyStoreFile = "file:/Users/bill/tmp/toolkit/environment/EURO2011/keystore/keystore";
		String keyStorePass = "password";
		String trustStoreFile = keyStoreFile;
		String trustStorePass = keyStorePass;

		if (securityParams == null)
			throw new EnvironmentNotSelectedException("Trying to initiate a TLS connection - securityParams are null");
		if (securityParams.getKeystore() == null || securityParams.getKeystore().equals(""))
			throw new EnvironmentNotSelectedException("Trying to initialize a TLS connection - keystore location not recorded in securityParams");
		keyStoreFile = "file:" + securityParams.getKeystore().toString();
		keyStorePass = securityParams.getKeystorePassword();
		trustStoreFile = "file:" + securityParams.getTruststore().toString();
		trustStorePass = securityParams.getTruststorePassword();

		return new AuthSSLProtocolSocketFactory(
				new URL(keyStoreFile), keyStorePass,
				new URL(trustStoreFile), trustStorePass).getSSLContext();
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

        logger.info("soapCall done");
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
					org.apache.axis2.kernel.http.HTTPConstants.HTTP_PROTOCOL_VERSION,
					org.apache.axis2.kernel.http.HTTPConstants.HEADER_PROTOCOL_10);

			opts.setProperty(
					org.apache.axis2.kernel.http.HTTPConstants.CHUNKED,
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

    // Load timeout values one time.
    // They will be set to -1 the first time through.
    // First choice is from the properties file.
    // Second choice is from the default values

    void loadTimeoutValues() {
        if (deployedSocketTimeout < 0) {
            if (Installation.instance().propertyServiceManager().getPropertyManager().getSocketTimeout() >= 0) {
                deployedSocketTimeout = Installation.instance().propertyServiceManager().getPropertyManager().getSocketTimeout() * 1000;
            } else {
                deployedSocketTimeout = defaultSocketTimeout * 1000;
            }
            if (Installation.instance().propertyServiceManager().getPropertyManager().getConnectTimeout() >= 0) {
                deployedConnectTimeout = Installation.instance().propertyServiceManager().getPropertyManager().getConnectTimeout() * 1000;
            } else {
                deployedConnectTimeout = defaultConnectTimeout * 1000;
            }
        }
    }

    // Set the max connections and timeout - needed because by default you can only have
    // two connections to a single host.  This doesn't work with simulators in toolkit.
    void setMaxConnections() throws XdsInternalException, EnvironmentNotSelectedException {
        // axis2 1.8.2 uses HttpComponents HttpClient 4.x: CACHED_HTTP_CLIENT must be an
        // org.apache.http.client.HttpClient. The old commons-httpclient 3.x client caused a
        // ClassCastException in axis2's HTTPSenderImpl. Pool sized >2 per host for simulators.
        PoolingHttpClientConnectionManager connectionManager;
        try {
            connectionManager = createConnectionManager();
        } catch (IOException e) {
            throw new XdsInternalException(
                    "Failed to create HttpClient 4 TLS configuration\n"
                            + ExceptionUtil.exception_details(e), e);
        }
        connectionManager.setDefaultMaxPerRoute(50);
        connectionManager.setMaxTotal(50);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(deployedSocketTimeout)
                .setConnectTimeout(deployedConnectTimeout)
                .build();
        HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
        serviceClient.getServiceContext().getConfigurationContext().setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
    }

    private PoolingHttpClientConnectionManager createConnectionManager()
            throws IOException, EnvironmentNotSelectedException {
        if (!isTLS()) {
            return new PoolingHttpClientConnectionManager();
        }

        SSLContext sslContext = getAuthSslContext();
        serviceClient.getServiceContext().getConfigurationContext().setProperty(SSLContext.class.getName(), sslContext);

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                getSupportedClientSslProtocols(sslContext),
                getSupportedClientCipherSuites(sslContext),
                NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();
        return new PoolingHttpClientConnectionManager(socketFactoryRegistry);
    }

    private String[] getSupportedClientSslProtocols(SSLContext sslContext) throws IOException {
        String[] configuredProtocols = Installation.instance().propertyServiceManager().getPropertyManager().getClientSSLProtocols();
        if (configuredProtocols == null) {
            return null;
        }

        SSLSocket socket = (SSLSocket) sslContext.getSocketFactory().createSocket();
        try {
            return filterSupportedValues(configuredProtocols, socket.getSupportedProtocols(), "SSL protocol");
        } finally {
            socket.close();
        }
    }

    private String[] getSupportedClientCipherSuites(SSLContext sslContext) {
        String[] configuredCipherSuites = Installation.instance().propertyServiceManager().getPropertyManager().getClientCipherSuites();
        if (configuredCipherSuites == null) {
            return null;
        }

        return filterSupportedValues(configuredCipherSuites, sslContext.getSocketFactory().getSupportedCipherSuites(), "cipher suite");
    }

    private String[] filterSupportedValues(String[] configuredValues, String[] supportedValues, String valueType) {
        List<String> supported = Arrays.asList(supportedValues);
        List<String> enabled = new ArrayList<>();
        for (String configuredValue : configuredValues) {
            if (supported.contains(configuredValue)) {
                enabled.add(configuredValue);
            } else {
                logger.fine("Configured " + valueType + " is not supported by JVM: " + configuredValue);
            }
        }
        return enabled.toArray(new String[0]);
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
		String action_value = action.getText().trim();
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
				action, expected_return_action, null, false);
	}

	public OMElement soapCall(OMElement body, String endpoint, boolean mtom,
			boolean addressing, boolean soap12, String action,
			String expected_return_action, Map<String, String> linkage, boolean useTimestampProxy)
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

		this.useTimestampProxy = useTimestampProxy;
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

		try {
			inHeader = Util.deep_copy(in.getEnvelope().getHeader());
		} catch (Exception ex) {
			SOAPFactory fac;
			if (this.soap12)
				fac = OMAbstractFactory.getSOAP12Factory();
			else
				fac = OMAbstractFactory.getSOAP11Factory();

//			"http://www.w3.org/2003/05/soap-envelope"
			// Added this code with Java 17/library upgrades in 2026.
			// Without this checking, we would sometimes get null pointer errors
			String nameSpace = "";
			String prefix = "";
			if (in.getEnvelope().getDefaultNamespace() != null) {
				nameSpace = in.getEnvelope().getDefaultNamespace().toString();
				prefix = String.valueOf(in.getEnvelope().getDefaultNamespace().getPrefix());
			}

			OMNamespace ns = fac.createOMNamespace(nameSpace, prefix);

//			OMNamespace ns = fac.createOMNamespace(in.getEnvelope().getDefaultNamespace().toString(), in.getEnvelope().getDefaultNamespace().getPrefix());
			// End fix for null pointer issue, 2026

			outHeader = fac.createOMElement("Header", ns);
			logger.warning("inHeader value could not be set: " + ex.toString());
			logger.info("Empty SOAP IN Header was created.");

		}
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

		try {
			outHeader = Util.deep_copy(out.getEnvelope().getHeader());
		} catch (Exception ex) {
			SOAPFactory fac;
			if (this.soap12)
				fac = OMAbstractFactory.getSOAP12Factory();
			else
				fac = OMAbstractFactory.getSOAP11Factory();

//			"http://www.w3.org/2003/05/soap-envelope"
			// Added this code with Java 17/library upgrades in 2026.
			// Without this checking, we would sometimes get null pointer errors
			String nameSpace = "";
			String prefix = "";
			if (out.getEnvelope().getDefaultNamespace() != null) {
				nameSpace =out.getEnvelope().getDefaultNamespace().toString();
				prefix = String.valueOf(out.getEnvelope().getDefaultNamespace().getPrefix());
			}

			OMNamespace ns = fac.createOMNamespace(nameSpace, prefix);
//			OMNamespace ns = fac.createOMNamespace(out.getEnvelope().getDefaultNamespace().toString(), out.getEnvelope().getDefaultNamespace().getPrefix());
			// End fix for null pointer issue, 2026

			outHeader = fac.createOMElement("Header", ns);
			logger.warning("outHeader value could not be set: " + ex.toString());
			logger.info("Empty SOAP OUT Header was created.");
		}
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

	// TODO: This seems deprecated.
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
