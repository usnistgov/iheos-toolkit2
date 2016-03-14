/*
 * HttpClient.java
 *
 * Created on September 29, 2003, 8:06 AM
 */

package gov.nist.toolkit.http.httpclient;

import gov.nist.toolkit.http.axis2soap.MultipartMap;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.HttpCodeException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axiom.om.util.Base64;
import org.apache.soap.util.mime.ByteArrayDataSource;
import org.apache.xml.serialize.DOMSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XML11Serializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import com.sun.tools.javac.resources.javac;




/**
 * HttpClient handles all non-SOAP, non-JAXR communication between the NIST tools
 * and the ebXMLrr Registry.
 * @author bill
 */
public class HttpClient implements HostnameVerifier {

	HttpServletRequest request = null;
	OutputStream os;
	String boundary = "---1-2-3-4-5boundaryafjealdofmnqadd";
	String username, password, authorization;
	String host, service;
	int port;
	HttpURLConnection conn;
	InputStream in;
	String type;
	javax.mail.internet.MimeMultipart mp;
	boolean isMultipart = false;
	String body;  // on receive - if not multipart
	ByteArrayDataSource metadata = null;
	ByteArrayDataSource attachment = null;
	String attachmentType;
	Document doc;
	String documentId;
	static String superUserName;
	static String superUserPassword;

	public HttpURLConnection getConnection() { return conn; }

	/**
	 * Get the currently configured host that HttpClient is set to connect with.
	 * See also getPost() and getService().
	 * @return A String representing the current configured host that HttpClient is set to 
	 * connect with.
	 */
	public String getHost() { return host; }
	/**
	 * Get the currently configured port that HttpClient is set to connect with.
	 * See also getHost() and getService().
	 * @return An int representing the current configured port that HttpClient is set to 
	 * connect with.
	 */
	public int getPort() { return port; }
	/**
	 * Get the currently configured service URL that HttpClient is set to connect with.
	 * See also getHost() and getPost().
	 * @return A String representing the current configured service URI that HttpClient 
	 * is set to connect with.
	 */
	public String getService() { return service; }
	public String getUsername() { return username; }
	public String getUrl() {
		return "http://" + host + ":" + String.valueOf(port) + service;
	}

	public HttpClient() {
		this.username = null;
		this.password = null;
		this.authorization = null;
		this.port = 0;
		this.service = null;
		this.host = null;
		this.metadata = null;
		this.attachment = null;
		this.documentId = null;
	}

	/** Creates a new instance of SOAPLite */
	public HttpClient(String host, int port, String service, String username, String password) {
		this.username = username;
		this.password = password;
		this.authorization = null;
		this.port = port;
		this.service = service;
		this.host = host;
		this.metadata = null;
		this.attachment = null;
		this.documentId = "document";
	}

	public HttpClient(String host, int port, String service, String username, String password, HttpServletRequest request) {
		this.username = username;
		this.password = password;
		this.authorization = null;
		this.port = port;
		this.service = service;
		this.host = host;
		this.metadata = null;
		this.attachment = null;
		this.documentId = "document";
		this.request = request;
	}

	public HttpClient(String host, int port, String service, String authorization) {
		this.username = null;
		this.password = null;
		this.authorization = authorization;
		this.port = port;
		this.service = service;
		this.host = host;
		this.metadata = null;
		this.attachment = null;
		this.documentId = "document";

	}

	public HttpClient(String host, int port, String service, String authorization, HttpServletRequest request) {
		this.username = null;
		this.password = null;
		this.authorization = authorization;
		this.port = port;
		this.service = service;
		this.host = host;
		this.metadata = null;
		this.attachment = null;
		this.documentId = "document";
		this.request = request;
	}



	public static void saveAuth(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("EbxmlAuth") == null) {
			session.setAttribute("EbxmlAuth", request.getHeader("Authorization"));
		}
	}

	public static HttpClient getConnection(HttpClientInfo info) {
		return getConnection(info.getRestHost(), info.getRestPort(), info.getRestService());
	}

	public static HttpClient getConnection(String host, int port, String service) {
		return getConnection(host,port,service,null);
	}
	public static HttpClient getConnection(HttpClientInfo info, HttpServletRequest request) {
		return HttpClient.getConnection(info.getRestHost(),info.getRestPort(), info.getRestService(),request);
	}
	public static HttpClient getConnection(String host, int port, String service, HttpServletRequest request) {
		if (request == null)
			return getConnection(host,port,service,null, true);
		return getConnection(host,port,service,request,  false);
	}
	public static HttpClient getConnection(HttpClientInfo info, HttpServletRequest request, boolean readOnly) {        
		return HttpClient.getConnection(info.getRestHost(), info.getRestPort(), info.getRestService(), request, readOnly);    
	}

	public static HttpClient getConnection(String host, int port, String service, boolean readOnly) {

		if (readOnly)
			return new HttpClient(host, port, service, "", "");
		else {
			return new HttpClient(host,port, service + "/write", "", "");
		}
	}

	public static HttpClient getConnection(String host, int port, String service, boolean readOnly, String user, String pass) {

		if (readOnly)
			return new HttpClient(host, port, service, "", "");
		else {
			return new HttpClient(host,port, service + "/write", user, pass);
		}
	}

	public static HttpClient getConnection(String host, int port, String service, HttpServletRequest request, boolean readOnly) {

		if (readOnly)
			return new HttpClient(host, port, service, "", "", request);
		else {
			saveAuth(request);
			return new HttpClient(host,port, service + "/write", (String) request.getSession().getAttribute("EbxmlAuth"),request);
		}
	}

	public static HttpClient getSuperUserConnection(String host, int port, String service, String registryOperatorUsername, String registryOperatorPassword,HttpServletRequest request) {
		if (request == null)
			return getSuperUserConnection(host, port, service, registryOperatorUsername, registryOperatorPassword, null, true);
		return getSuperUserConnection(host, port, service, registryOperatorUsername, registryOperatorPassword,request,  false);
	}

	public static HttpClient getSuperUserConnection(String host, int port, String service, String registryOperatorUsername, String registryOperatorPassword, HttpServletRequest request, boolean readOnly) {
		superUserName = registryOperatorUsername;
		superUserPassword = registryOperatorPassword;

		if (!readOnly)
			service = service + "/write";

		return  new HttpClient( host,port, service, superUserName, superUserPassword);

	}



	/********************************************************************
	 *
	 * Attachments
	 *
	 ********************************************************************/

	public void setDocumentId(String id) {
		documentId = id;
	}

	public void setAttachment(String at) throws java.io.IOException {
		setAttachment(at, "text/plain");
	}

	public void setAttachment(String at, String type) throws java.io.IOException {
		attachment = new ByteArrayDataSource(at, type);
		attachmentType = type;
	}

	public void setAttachment(File f) throws java.io.IOException {
		setAttachment(f, "text/plain");
	}

	public void setAttachment(File f, String type) throws java.io.IOException {
		attachment = new ByteArrayDataSource(f, type);
		attachmentType = type;
	}

	public void setAttachment(InputStream is, String type) throws java.io.IOException {
		attachment = new ByteArrayDataSource(is, type);
		attachmentType = type;
	}

	public void setAttachment(Object o, String type) throws Exception {
		if (o instanceof InputStream)
			setAttachment((InputStream) o, type);
		else if (o instanceof File)
			setAttachment((File) o, type);
		else if (o instanceof String)
			setAttachment((String) o, type);
		else
			throw new Exception("SOAPLite.setAttachment() cannot handle object type " + o.getClass().getName());
	}

	public String username_password() {
		return username + ":" + password;
	}

	static public String generateAuthorization(String username_password) {
//		return  "Basic " + new sun.misc.BASE64Encoder().encode(username_password.getBytes());
		return "Basic " + Base64.encode(username_password.getBytes());
	}

	/****************************************************************************
	 *
	 * IO
	 *
	 ****************************************************************************/

	void put(String s) throws java.io.IOException {
		os.write(s.getBytes());
	}

	void putLine(String line) throws java.io.IOException  {
		put(line);
		put("\r\n");
	}

	void putFile(String filename, String mimeType) throws java.io.IOException {
		putFile(filename, mimeType, "filename");
		/*        FileInputStream in = new FileInputStream(new File(filename));
        byte[] buf = new byte[256];
        int size;

        putLine(boundary);
        putLine("Content-Disposition: form-data; name=\"filename\"; filename=\"" + filename + "\"");
        putLine("Content-type: " + mimeType);
        putLine("");

        while ( (size=in.read(buf)) > -1) {
            os.write(buf, 0, size);
        }
		 */
	}

	void putFile(String filename, String mimeType, String name) throws java.io.IOException {
		FileInputStream in = new FileInputStream(new File(filename));
		byte[] buf = new byte[256];
		int size;

		putLine(boundary);
		putLine("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"");
		putLine("Content-type: " + mimeType);
		putLine("");

		while ( (size=in.read(buf)) > -1) {
			os.write(buf, 0, size);
		}
	}

	void put(ByteArrayDataSource ds, String mimeType, String name) throws java.io.IOException {

		putLine(boundary);
		putLine("Content-Disposition: form-data; name=\"" + name + "\"");
		putLine("Content-type: " + mimeType);
		putLine("");

		ds.writeTo(os);
	}

	void put(String content, String mimeType, String name) throws java.io.IOException {

		putLine(boundary);
		putLine("Content-Disposition: form-data; name=\"" + name + "\"");
		putLine("Content-type: " + mimeType);
		putLine("");

		putLine(content);
	}

	public String getAuthorization() {
		if (authorization != null) return authorization;
		if (username == null) return null;
		if (username.equals("")) return null;
		if (password == null) return null;
		if (password.equals("")) return null;
		return "Basic " + Base64.encode(username_password().getBytes());
	}

	/*****************************************************************************
	 *
	 * Call
	 *
	 *****************************************************************************/

	public static String httpGet(URI uri) throws java.net.MalformedURLException, java.io.IOException, java.lang.Exception {
		HttpClient hc = new HttpClient();
		return hc.GET(uri);
	}

	public static String GET(String uri) throws MalformedURLException, IOException, Exception {
		return httpGet(uri);
	}

	public static String POST(String uri, String content) throws XdsInternalException, HttpCodeException, IOException, URISyntaxException {
		return new HttpClient().POST(new URI(uri), null, content);
	}

	public static String httpGet(String uri) throws java.net.MalformedURLException, java.io.IOException, java.lang.Exception {
		URI u_uri = new URI(uri);
		return httpGet(u_uri);
	}

	public  byte[] httpGetBytes(URI uri)  throws java.net.MalformedURLException, java.io.IOException, java.lang.Exception {
		basic_raw_get(uri);
		return this.getReplyAsBytes();
	}

	public byte[] httpGetBytes(String uri)  throws java.net.MalformedURLException, java.io.IOException, java.lang.Exception {
		return httpGetBytes(new URI(uri));
	}

	public String GET(URI uri) throws java.net.MalformedURLException, java.io.IOException, java.lang.Exception {
		basic_raw_get(uri);

		return getReply();
	}

	public InputStream GET_inputStream(URI uri) throws java.net.MalformedURLException, java.io.IOException, java.lang.Exception {
		basic_raw_get(uri);

		return in;
	}

	public String POST(URI uri, String service, String content) throws XdsInternalException, IOException, HttpCodeException {
		doc = null;
		URL url;
		try {
			url = uri.toURL();
		} catch (Exception e) {
			throw new XdsInternalException("Error trying to retrieve " + uri.toString() + " : " + e.getMessage());
		}

		try {
			HttpsURLConnection.setDefaultHostnameVerifier(this);  // call verify() above to validate hostnames
		} catch (Exception e) {

		}

		conn = (HttpURLConnection) url.openConnection();

		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Accept", "text/html, text/xml, text/plain, */*");
		conn.connect();
		OutputStream os = conn.getOutputStream();
		os.write(content.getBytes());

		// returns content type, real response is in this.in 
		return getResponse();

	}


	// tells any https connections we make not to verify hostnames  (all are ok)
	public boolean verify(String hostname,
			SSLSession session) {
		return true;
	}

	public String basic_raw_get(URI uri)
	throws java.net.MalformedURLException, java.io.IOException, XdsInternalException, HttpCodeException {
		doc = null;
		URL url;
		try {
			url = uri.toURL();
		} catch (Exception e) {
			throw new XdsInternalException("Error trying to retrieve " + uri.toString() + " : " + e.getMessage());
		}

		try {
			HttpsURLConnection.setDefaultHostnameVerifier(this);  // call verify() above to validate hostnames
		} catch (Exception e) {

		}

		conn = (HttpURLConnection) url.openConnection();

		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "text/html, text/xml, text/plain, */*");
		try {
			conn.connect();
		} catch (java.net.ConnectException e) {
			throw new XdsInternalException("Connecting to " + url, e);
		}
		return getResponse();
	}

	public void basic_get(URI uri) throws java.net.MalformedURLException, java.io.IOException, java.lang.Exception {
		basic_raw_get(uri);
		loadMultipart();
	}

	public MultipartMap new_basic_get(URI uri)
	throws java.net.MalformedURLException, java.io.IOException, java.lang.Exception {
		doc = null;
		URL url = uri.toURL();
		conn = (HttpURLConnection) url.openConnection();

		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "text/html, text/xml, text/plain, */*");
		conn.connect();
		in = (FilterInputStream) conn.getContent();
		String contentType = conn.getHeaderField("Content-Type");

		return new MultipartMap(in,  contentType);
	}


	public void verifyUri(String uriString) throws Exception {
		URI uri;
		try {
			uri = new URI(uriString);
			URL url = uri.toURL();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "text/html, text/xml, text/plain, */*");
			conn.connect();
		} catch (java.net.MalformedURLException e) {
			throw new Exception("Error verifying uri: " + uriString + " : Malformed: " + e.getMessage());
		} catch (java.io.IOException e) {
			throw new Exception("Error verifying uri: " + uriString + " : IOException: " + e.getMessage());
		} catch (java.lang.Exception e) {
			throw new Exception("Error verifying uri: " + uriString + " : Exception: " + e.getMessage());
		}
	}

	public void setRequestProperty(String name, String value) {
		conn.setRequestProperty(name, value);
	}

	void buildConnection()
	throws java.net.MalformedURLException, java.io.IOException {
		doc = null;
		URL url = new URL("http", host, port, service);
		conn = (HttpURLConnection) url.openConnection();

		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Accept", "text/html, text/xml");
		// other web user agents add two hyphens to start of declared boundary
		// so we will too
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary.substring(2));
		String auth = getAuthorization();
		if (auth != null)
			conn.setRequestProperty("Authorization", auth);
		//conn.setRequestProperty("Authorization", "Basic " + new sun.misc.BASE64Encoder().encode(username_password().getBytes()));
//		String user = this.getUsername();
		//       if(user == null || !user.equals(ServletParams.getRegistryOperatorUsername()))
		//           this.setCookies();
		// 04/15/2005 McCaffrey: above not needed any more because we no longer have any use for cookies
		conn.connect();
	}

	void buildLCMCall()
	throws java.io.IOException {
		os = conn.getOutputStream();

		putLine(boundary);
		putLine("Content-Disposition: form-data; name=\"interface\"");
		putLine("");
		putLine("LifeCycleManager");

		// metadata
		put(metadata, "text/xml", "metadata");

		if (attachment != null) {
			put(attachment, attachmentType, documentId);
		}

		putLine("");
		putLine(boundary + "--");

		os.close();
	}

	public void basic_call() throws java.net.MalformedURLException, java.io.IOException, XdsInternalException, HttpCodeException {
		this.buildConnection();
		this.buildLCMCall();
		this.getResponse();
	}

	public void call()
	throws java.net.MalformedURLException, java.io.IOException, XdsInternalException, HttpCodeException, MessagingException {

		basic_call();

		loadMultipart();
	}

	/*******************************************************************************
	 *
	 * PARSE
	 *
	 *******************************************************************************/

	public HttpURLConnection getConnectionObject() {
		return conn;
	}

	public boolean multipart() {
		return isMultipart;
	}

	public String getContentType() {
		return conn.getHeaderField("Content-Type");
	}

	public Map<String, List<String>> getHeaderFields() {
		return conn.getHeaderFields();
	}

	String getResponse()
	throws HttpCodeException, IOException, XdsInternalException {
		try {
			String encoding =conn.getContentEncoding();
			if (encoding == null) {
				in = conn.getInputStream();
			} else {
				Object o = conn.getContent();
				in = (FilterInputStream) o;
			}
		} catch (java.io.IOException e) {
			int code = conn.getResponseCode();
			//System.out.println("ERROR: code: " + String.valueOf(code) + " message: " + conn.getResponseMessage());
			InputStream is = conn.getErrorStream();
			if (is == null) {
				String msg = conn.getResponseMessage();
				URL url = conn.getURL();
				throw new XdsInternalException("Error retieving content of " + url.toString() + "; response was " + msg);
			} else {
				StringBuffer b = new StringBuffer();
				byte[] by = new byte[256];
				while ( is.read(by, 0, 256) > 0 )
					b.append(new String(by));  // get junk at end, should be sensitive to number of bytes read
				//System.out.println(new String(b));
				throw new XdsInternalException("ERROR: HttpClient: code: " + String.valueOf(code) + " message: " + conn.getResponseMessage() +
						"\n" + new String(b) + "\n");
			}
		}
		String contentType = conn.getHeaderField("Content-Type");
		if (contentType == null)
			isMultipart = false;
		else {
			isMultipart = contentType.startsWith("multipart");
		}
		return contentType;
	}


	String origBoundary() {
		String contentType = conn.getHeaderField("Content-Type");
		type = contentType.substring(0, contentType.indexOf(";"));
		return contentType.substring(contentType.indexOf("boundary=")+9);
	}

	public String getReply() throws java.io.IOException {
		return Io.getStringFromInputStream(in);
	}

	public byte[] getReplyAsBytes()  throws java.io.IOException {
		return Io.getBytesFromInputStream(in);
	}

	// assumes all text/*
	public void loadMultipart()
	throws java.net.MalformedURLException, java.io.IOException, MessagingException {

		String reply = getReply();

		// replace original boundary string with our boundary string
		// ebxmlrr seems to generate a boundary string with an embedded equals sign which
		// messes up the parser in MimeMultipart

		if (isMultipart) {
			String origBoundary = origBoundary();
			String newContentType = type + "; boundary=" + boundary;


			reply = reply.replaceAll(origBoundary,  boundary);

			DataSource ds = new org.apache.soap.util.mime.ByteArrayDataSource(reply, newContentType);
			mp = new javax.mail.internet.MimeMultipart(ds);
		} else {
			body = reply;
			mp = null;
		}

	}

	void loadMultipartSimple()
	throws java.net.MalformedURLException, java.io.IOException, java.lang.Exception {

		String reply = getReply();

		if (isMultipart) {
			//String origBoundary = origBoundary();
			//String newContentType = type + "; boundary=" + boundary;


			//reply = reply.replaceAll(origBoundary,  boundary);

			DataSource ds = new org.apache.soap.util.mime.ByteArrayDataSource(reply, type);
			mp = new javax.mail.internet.MimeMultipart(ds);
		} else {
			body = reply;
			mp = null;
		}

	}

	String translate(ByteArrayInputStream is) throws java.io.IOException {
		int count;
		byte[] by = new byte[256];
		StringBuffer buf = new StringBuffer();
		while ( (count=is.read(by)) > 0 )
			buf.append(new String(by,0,count));
		return new String(buf);
	}

	public int partCount() throws javax.mail.MessagingException {
		if (isMultipart)
			return 1;
		return mp.getCount();
	}

	public String getHeader(int bodyPart, String name)  throws javax.mail.MessagingException {
		if (isMultipart) {
			MimeBodyPart bp = (MimeBodyPart) mp.getBodyPart(bodyPart);
			String[] headers = bp.getHeader(name);
			if (headers.length == 0)
				return null;
			return headers[0];
		} else {
			return conn.getHeaderField("Content-Type");
		}
	}

	public String getContent(int bodyPart) throws java.io.IOException,javax.mail.MessagingException{
		if (!isMultipart) {
			return body;
		}
		MimeBodyPart bp = (MimeBodyPart) mp.getBodyPart(bodyPart);
		Object o = bp.getContent();
		if (o.getClass().getName().equals("java.lang.String")) {
			return (String) o;
		} else if (o.getClass().getName().equals("java.io.ByteArrayInputStream")) {
			return translate( (ByteArrayInputStream) o);
		} else {
			throw new java.io.IOException("Cannot handle data type " + o.getClass().getName() + " in SOAPLite");
		}
	}

	// callable after basic_call
	// this will not work with multipart non-text content
	public InputStream getContentInputStream(int bodyPart) throws java.io.IOException,javax.mail.MessagingException{
		if (!isMultipart) {
			return in;
		}
		MimeBodyPart bp = (MimeBodyPart) mp.getBodyPart(bodyPart);
		Object o = bp.getContent();
		if (o instanceof java.io.InputStream) {
			return (InputStream) o;
		} else {
			throw new java.io.IOException("getContentInputStreamCannot handle data type " + o.getClass().getName() + " in SOAPLite");
		}
	}

	public String getBody() {
		return body;
	}

	public String getBodyXML() {
		int x = body.indexOf("?>")+2;
		int y = body.indexOf("<", x);
		return
		//"<wrapper>" +
		body.substring(y);
		// + "</wrapper>";
	}

	// returns body (or bodyPart(0)) as DOM document
	public Document getDocument() 
	throws java.io.UTFDataFormatException, MessagingException, IOException, ParserConfigurationException, SAXException {
		try {
			if (doc == null) {
				InputStream content = getContentInputStream(0);
				try {
					doc = gov.nist.toolkit.utilities.xml.XML.parse(content);
				} catch (Exception e) {
					throw new IOException("Response does not XML parse:\n" + Io.getStringFromInputStream(content));
				}
			}
			return doc;
		} catch (java.io.UTFDataFormatException e) {
			throw e;
		} 
	}

	//	public String documentAsString() throws Exception {
	//	OutputFormat of = new OutputFormat(getDocument());
	//	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	//	XML11Serializer x11ser = new XML11Serializer(bos, of);
	//	DOMSerializer ds = x11ser.asDOMSerializer();

	//	ds.serialize(getDocument());
	//	String xmlString = bos.toString();
	//	return XmlFormatter.format(xmlString, false);
	//	}

	public String documentAsString() throws Exception {
		return XmlUtil.XmlWriter(getDocument());
	}

	public void setMetadata(String md) {
		if (!md.endsWith("\n"))
			md = md + "\n";
		metadata = new ByteArrayDataSource(md, "text/xml");
	}

	public String getMetadataAsString() throws IOException {
		InputStream is = metadata.getInputStream();
		return Io.getStringFromInputStream(is);
	}

	public void setMetadata(File f) throws java.io.IOException {
		metadata = new ByteArrayDataSource(f, "text/xml");
	}

	public String setMetadata(Document doc)
	throws IOException {
		OutputFormat of = new OutputFormat(doc);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XML11Serializer x11ser = new XML11Serializer(bos, of);
		DOMSerializer ds = x11ser.asDOMSerializer();

		ds.serialize(doc);
		String xmlString = bos.toString();
		setMetadata(xmlString);
		return xmlString;
	}

	/*****************************************************************************
	 *
	 *  REGISTRY SPECIFIC STUFF
	 *
	 *****************************************************************************/

	public void submitObjectsRequest(String req) {
		String template1 =
			"<?xml version = \"1.0\" encoding = \"UTF-8\"?>\n" +
			"<SubmitObjectsRequest> " +
			"	<LeafRegistryObjectList>";
		String template2 =
			"	</LeafRegistryObjectList>" +
			"</SubmitObjectsRequest>\n";
		if (req.startsWith("<?xml")) {
			req = req.substring(req.indexOf(">")+2);
		}
		setMetadata(template1 + req + template2);
	}
	public void updateObjectsRequest(String req) {
		String template1 =
			"<?xml version = \"1.0\" encoding = \"UTF-8\"?>\n" +
			"<SubmitObjectsRequest> " +
			"	<LeafRegistryObjectList>";
		String template2 =
			"	</LeafRegistryObjectList>" +
			"</SubmitObjectsRequest>\n";
		if (req.startsWith("<?xml")) {
			req = req.substring(req.indexOf(">")+2);
		}
		Collection uuids = compileObjectRefs(req);
		String objectRefs = getObjectRefs(uuids);
		setMetadata(template1 + objectRefs + req + template2);
		System.out.println("update!" + objectRefs);
	}
	public void removeObjectsRequest(Collection uuids) {
		String template1 =
			"<?xml version = \"1.0\" encoding = \"UTF-8\"?>\n" +
			"<RemoveObjectsRequest deletionScope=\"DeleteAll\"> ";
		String template2 =
			"</RemoveObjectsRequest>\n";
		setMetadata(template1 + getObjectRefList(uuids) + template2);
	}

	String getObjectRefList(Collection uuids) {
		StringBuffer buf = new StringBuffer();
		buf.append("<ObjectRefList>");
		for (Iterator it=uuids.iterator(); it.hasNext(); ) {
			buf.append("<ObjectRef id=\"" + (String) it.next() + "\"/>");
		}
		buf.append("</ObjectRefList>");
		return buf.toString();
	}
	static String getObjectRefs(Collection uuids) {
		StringBuffer sb = new StringBuffer();
		Iterator it = uuids.iterator();
		while(it.hasNext()) {
			sb.append("<ObjectRef id=\"" + (String) it.next() + "\"/>");
		}
		return sb.toString();
	}
	Collection compileObjectRefs(String req) {
		StringTokenizer st = new StringTokenizer(req," \"");
		ArrayList uuids = new ArrayList();
		while(st.hasMoreTokens()) {
			String next = st.nextToken();
			if(next.startsWith("urn:uuid:"))
				uuids.add(next);
		}
		return uuids;

		/*        String[] pieces = req.split(" \"");
        ArrayList uuids = new ArrayList();
        for(int i = 0; i < pieces.length; i++) {
            System.out.println(pieces[i]);
            if(pieces[i].startsWith("urn:uuid:"))
                uuids.add(pieces[i]);
        }
        return uuids;
		 */
	}

	public static String ObjectRefReturnType = "ObjectRef";
	public static String LeafClassReturnType = "LeafClass";

	public void setQuery(String sql, String returnType, boolean returnComposedObjects) throws java.io.IOException {
		setMetadata("<?xml version = \"1.0\" encoding = \"UTF-8\"?>" +
				"<AdhocQueryRequest>" +
				"<ResponseOption returnType = \"" + returnType + "\" returnComposedObjects=\"" +
				((returnComposedObjects) ? "true" : "false") +
				"\"/>" +
				"<SQLQuery>" +
				sql +
				"</SQLQuery>" +
		"</AdhocQueryRequest>");
	}

	public String getErrorMessage() throws Exception {
		String error = "";
		NodeList nl = getDocument().getElementsByTagName("RegistryError");
		for (int i=0; i<nl.getLength(); i++) {
			Node n = nl.item(i);
			String val = n.getFirstChild().getNodeValue();
			error = error + "\n" + val;
		}
		return error;
	}

	public Collection getObjectRefs() throws Exception {
		ArrayList al = new ArrayList();
		NodeList nl = getDocument().getElementsByTagName("ObjectRef");
		for (int i=0; i<nl.getLength(); i++) {
			Node n = nl.item(i);
			NamedNodeMap map = n.getAttributes();
			Node or = map.getNamedItem("id");
			al.add(or.getNodeValue());
		}
		return (Collection) al;
	}

	public String getXMLStatus()  throws ParserConfigurationException, IOException, MessagingException, SAXException {
		return getXMLStatus(getDocument());
	}

	public String getXMLStatus(Document doc) {
		//Document doc = getDocument();
		Element root = doc.getDocumentElement();
		NamedNodeMap map = root.getAttributes();
		Node node = map.getNamedItem("status");
		return node.getNodeValue();
	}

	public String query(String query) throws Exception {
		setQuery(query, HttpClient.LeafClassReturnType, true);
		call();
		return getBody();
	}

	static public String doObjectRefQuery(HttpClientInfo info, String query) throws Exception {
		return HttpClient.doObjectRefQuery(info.getRestHost(), info.getRestPort(), info.getRestService(), query);
	}

	// need to check status for errors
	static public String doObjectRefQuery(String host, int port, String service, String query) throws Exception {
		HttpClient sl = HttpClient.getConnection(host, port, service, null, true);
		sl.setQuery(query, HttpClient.ObjectRefReturnType, true);
		sl.call();
		return sl.getBody();
	}
	static public ArrayList queryForObjectRefs(HttpClientInfo info, String query) throws Exception {
		return HttpClient.queryForObjectRefs(info.getRestHost(), info.getRestPort(), info.getRestService(), query);
	}
	static public ArrayList queryForObjectRefs(String host, int port, String service, String query) throws Exception {
		HttpClient sl = HttpClient.getConnection(host, port, service, null, true);
		sl.setQuery(query, HttpClient.ObjectRefReturnType, true);
		sl.basic_call();
		String status = sl.getXMLStatus();
		if (status.equals("Failure")) {
			throw new Exception(sl.getErrorMessage());
		}
		ArrayList al = new ArrayList();
		Document doc = sl.getDocument();
		NodeList nlist = doc.getElementsByTagName("ObjectRef");
		for (int i=0; i<nlist.getLength(); i++) {
			Node n = nlist.item(i);
			NamedNodeMap atts = n.getAttributes();
			Node idNode = atts.getNamedItem("id");
			String uuid = idNode.getNodeValue();
			al.add(uuid);
		}
		return al;
	}

	//	static public HashMap getSlots(HttpClientInfo info, String id) throws Exception {
	//	return HttpClient.getSlots(info.getRestHost(), info.getRestPort(), info.getRestService(), id);
	//	}

	//	static public HashMap getSlots(String host, int port, String service, String id) 
	//	throws HttpCodeException, XdsInternalException {
	//	try {
	//	HashMap map = new HashMap();
	//	HttpClient sl = getConnection(host, port, service);
	//	String sql = "SELECT * FROM RegistryObject ro WHERE ro.id='" + id + "'";
	//	sl.setQuery(sql, HttpClient.LeafClassReturnType, true);
	//	sl.basic_call();
	//	String status = sl.getXMLStatus();
	//	if (status.equals("Failure")) {
	//	throw new Exception(sl.getErrorMessage());
	//	}
	//	Document doc = sl.getDocument();
	//	NodeList nlist = doc.getElementsByTagName("Slot");
	//	for (int i=0; i<nlist.getLength(); i++) {
	//	Node slot = nlist.item(i);
	//	NamedNodeMap atts = slot.getAttributes();
	//	Node nameNode = atts.getNamedItem("name");
	//	String name = nameNode.getNodeValue();
	//	Node valueList = slot.getFirstChild();
	//	NodeList values = valueList.getChildNodes();
	//	int numberOfValues = values.getLength();
	//	if (values.getLength() == 1) {
	//	Node valueNode = values.item(0);
	//	String value;
	//	if (valueNode.hasChildNodes()) {
	//	Node vn = valueNode.getFirstChild();
	//	value = vn.getNodeValue();
	//	} else {
	//	value = valueNode.getNodeValue();
	//	}
	//	map.put(name, value);
	//	} else {
	//	ArrayList avalues = new ArrayList();
	//	for (int j=0; j<values.getLength(); j++) {
	//	Node valueNode = values.item(j);
	//	if (valueNode.hasChildNodes()) {
	//	valueNode = valueNode.getFirstChild();
	//	}
	//	String value = valueNode.getNodeValue();
	//	avalues.add(value);
	//	}
	//	map.put(name,  avalues);
	//	}
	//	}
	//	return map;
	//	} catch (java.net.MalformedURLException e) {
	//	throw new Exception("SOAPLite.getSlots() threw MalformedURLException: " + e.getMessage());
	//	} catch (java.io.IOException e) {
	//	throw new Exception("SOAPLite.getSlots() threw IOException: " + e.getMessage());
	//	}
	//	}

	public void setCookies() {
		if (request == null) return;
		Cookie[] cookies = request.getCookies();
		if(cookies.length == 0) return;
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			String name = cookie.getName();
			String value = cookie.getValue();
			sb.append(name + "=" + value);
			if(i != (cookies.length - 1))
				sb.append("; ");
		}
		conn.setRequestProperty("Cookie", sb.toString());
	}

}
