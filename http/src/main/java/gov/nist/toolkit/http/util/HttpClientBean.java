/*
 * HttpClientBean.java
 *
 * Created on September 23, 2004, 1:17 PM
 */

package gov.nist.toolkit.http.util;


import gov.nist.toolkit.http.httpclient.HttpClient;
import gov.nist.toolkit.http.httpclient.HttpClientInfo;
import gov.nist.toolkit.xdsexception.HttpClientException;
import gov.nist.toolkit.xdsexception.HttpCodeException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


/**
 * JavaBean wrapper of HttpClient class.  Allows JavaBean access to HttpClient
 * functionality.
 * @author bill
 */
public class HttpClientBean extends Object implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Holds value of property metadata.
	 */
	private String metadata;

	/**
	 * Holds value of property document1.
	 */
	private Object document1;

	/**
	 * Holds value of property requestIpAddress.
	 */
	private String requestIpAddress;


	/**
	 * Holds value of property registryErrorMessage.
	 */
	private String registryErrorMessage;

	/**
	 * Holds value of property document1Type.
	 */
	private String document1Type;

	private String host = null;
	private int port = 0;
	private String service = null;
	private String username = "";
	private String password = "";
	private boolean read_only = true;
	Map<String, List<String>> header_fields = null;
	
	public Map<String, List<String>> getHeaderFields() {
		return header_fields;
	}

	/**
	 * Constructor.
	 */
	public HttpClientBean() {

		metadata = null;
		document1 = null;
	}

	public String getErrorMessage() {
		return registryErrorMessage;
	}	


	/**
	 * Getter for property metadata.
	 * @return Value of property metadata.
	 */
	public String getMetadata() {
		return this.metadata;
	}

	/**
	 * Setter for property metadata.
	 * @param metadata New value of property metadata.
	 */
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	/**
	 * Getter for property document1.
	 * @return Value of property document1.
	 */
	public Object getDocument1() {
		return this.document1;
	}

	/**
	 * Setter for property document1.
	 * @param document1 New value of property document1.
	 */
	public void setDocument1(Object document1) {
		this.document1 = document1;
	}

	/**
	 * Getter for property requestIpAddress.
	 * @return Value of property requestIpAddress.
	 */
	public String getRequestIpAddress() {
		return this.requestIpAddress;
	}

	/**
	 * Getter for property registryResponse.
	 * @return Value of property registryResponse.
	 * @throws java.lang.Exception Thrown if servletRequest not set or if there is a problem communicating
	 * between HttpClient and registry.
	 */
	public boolean isRegistryResponse() 
	throws HttpClientException, MalformedURLException, HttpCodeException, IOException, XdsInternalException, 
	ParserConfigurationException, SAXException, MessagingException {
		HttpClient sl = HttpClient.getConnection(this.getHost(),this.getPort(), this.getService(), false, username, password);
		String registryStatus;

		registryErrorMessage = "";
		if (getMetadata() == null || getMetadata() == "") {
			registryErrorMessage = "No metadata";
			return false;
		}
		sl.setMetadata(getMetadata());
		if (document1 != null) {
			try {
				sl.setAttachment(document1, document1Type);
			} catch (Exception e) {
				throw new HttpClientException("HttpClientBean: Unable to attach document: " + e.getMessage(), null);
			}
		}
		sl.basic_call();
		registryStatus = sl.getXMLStatus();
		if (registryStatus.equals("Failure")) {
			String err = "";
			try {
				err = sl.getErrorMessage();
			}catch (Exception e1) {
			}
			registryErrorMessage = "Registry rejected metadata submission: " + err;
			return false;
		}
		return true;
	}


	/**
	 * Getter for property registryErrorMessage.
	 * @return Value of property registryErrorMessage.
	 */
	public String getRegistryErrorMessage() {
		return this.registryErrorMessage;
	}

	/**
	 * Getter for property document1Type.
	 * @return Value of property document1Type.
	 */
	public String getDocument1Type() {
		return this.document1Type;
	}

	/**
	 * Setter for property document1Type.
	 * @param document1Type New value of property document1Type.
	 */
	public void setDocument1Type(String document1Type) {
		this.document1Type = document1Type;
	}

	public void setUsernamePassword(String usernamePassword)  {
		String[] up = usernamePassword.split(":");
		if (up.length > 0)
			username = up[0];
		if (up.length > 1)
			password = up[1];
	}

	public void setReadOnly(boolean ro) {
		read_only = ro;
	}
	/**
	 * Getter for property queryResponse.
	 * @return Value of property queryResponse.
	 */
	public String getQueryResponse() throws XdsInternalException {
//		if (servletRequest == null) {
//		registryErrorMessage = "HttpClientBean - servletRequest not set";
//		return null;
//		}
		HttpClient sl = HttpClient.getConnection(this.getHost(),this.getPort(), this.getService(), read_only, username, password);
		String registryStatus;

		registryErrorMessage = "";
		if (getMetadata() == null || getMetadata() == "") {
			registryErrorMessage = "No metadata";
			return null;
		}
		sl.setMetadata(getMetadata());
		try {
			sl.call();
			header_fields = sl.getHeaderFields();
		} catch (Exception e) {
			try {
				registryErrorMessage = sl.getErrorMessage();
			}catch (Exception e1) {
				// let the higher-up logic sort this out and report it
				throw new XdsInternalException(e.getMessage());
//				e.printStackTrace();
//				StringWriter sw = new StringWriter();
//				PrintWriter pw = new PrintWriter(sw);
//				e.printStackTrace(pw);
//				pw.close();
//				registryErrorMessage = "HttpClient threw exception - could not retrieve error message\n" +
//				sw.toString();
			}
			return "";
		}
		try {
			String body = sl.getBody();
			if (body.startsWith("<?xml version=\"1.0\"?>")) {
				int from = body.indexOf('<', 5);
				body = body.substring(from);
			}
			return body;
		} catch (Exception e) {
			registryErrorMessage = "Error retrieving query result";
			return "";
		}

	}

	/**
	 * Getter for property host.
	 * @return Value of property host.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Setter for property host.
	 * @param host New value of property host.
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Getter for property port.
	 * @return Value of property port.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Setter for property port.
	 * @param port New value of property post.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Getter for property service.
	 * @return Value of property service.
	 */
	public String getService() {
		return service;
	}

	/**
	 * Setter for property service.
	 * @param service New value of property service.
	 */
	public void setService(String service) {
		this.service = service;
	}
	/**
	 * Setter for property httpClientInfo.
	 * @param info New value of property httpClientInfo.
	 */
	public void setHttpClientInfo(HttpClientInfo info) {
		this.setHost(info.getRestHost());
		this.setPort(info.getRestPort());
		this.setService(info.getRestService());
	}
}
