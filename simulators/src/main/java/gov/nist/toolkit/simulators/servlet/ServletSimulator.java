package gov.nist.toolkit.simulators.servlet;

import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;
import gov.nist.toolkit.http.HttpMessage;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;

/**
 * This servlet implementation is a desperate attempt to capture the entire
 * output stream from the REAL servlet defined in SimServlet. All endpoints
 * generated for simulators point to this servlet (through web.xml mapping of
 * course) instead of SimServlet.  The
 * output message, including HTTP header, is sent directly to the proper log
 * file.
 * @author bill
 *
 */
public class ServletSimulator  {
	
	ServletConfig config = new ServletConfigImpl();
	HttpServletRequest request = new HttpServletRequestImpl();
	HttpServletResponse response = new HttpServletResponseImpl();
	HttpSession session = new HttpSessionImpl();
	Map<String, Object> sessionAtts = new HashMap<String, Object>();
	ServletOutputStream os = new ServletOutputStreamImpl();
	ServletInputStream is;
	Map<String, Object> requestAtts = new HashMap<String, Object>();
	Map<String, String> requestHdrs = new HashMap<String, String>();
	HttpMessage outMsg = new HttpMessage();
	String requestURI;
	SimDb db;
	
	HttpMessage inputMessage;
	SimServlet ss;
	
	public ServletSimulator(SimDb db) {
		this.db = db;
	}
	
	public void post() throws HttpParseException, HttpHeaderParseException, IOException, ParseException {
		inputMessage = db.getParsedRequest();

		is = new ServletInputStreamImpl(inputMessage.getBody());

		ss = new SimServlet();
		
		for (Enumeration<String> en=inputMessage.getHeaderNames(); en.hasMoreElements(); ) {
			String hdrName = en.nextElement();
			String val = inputMessage.getHeaderValue(hdrName);
			requestHdrs.put(hdrName, val);
		}
		
		
		String simulatorType = db.getSimulatorType();
		requestURI = "/simulator/" + simulatorType;
		
		ss.doPost(request, response);
		
		String body = os.toString();
		
		outMsg.setBody(body);
		
		db.putResponse(outMsg);
	}
	
	public MessageValidationResults getMessageValidationResults() {
		return ss.getMessageValidationResults();
	}
	

	class HttpSessionImpl implements HttpSession {

		public Object getAttribute(String arg0) {
			return sessionAtts.get(arg0);
		}

		public Enumeration getAttributeNames() {
			return null;
		}

		public long getCreationTime() {
			return 0;
		}

		public String getId() {
			return null;
		}

		public long getLastAccessedTime() {
			return 0;
		}

		public int getMaxInactiveInterval() {
			return 0;
		}

		public ServletContext getServletContext() {

			return null;
		}

		public HttpSessionContext getSessionContext() {
			return null;
		}

		public Object getValue(String arg0) {
			return null;
		}

		public String[] getValueNames() {
			return null;
		}

		public void invalidate() {

		}

		public boolean isNew() {
			return false;
		}

		public void putValue(String arg0, Object arg1) {

		}

		public void removeAttribute(String arg0) {

		}

		public void removeValue(String arg0) {

		}

		public void setAttribute(String arg0, Object arg1) {
			sessionAtts.put(arg0, arg1);
		}

		public void setMaxInactiveInterval(int arg0) {

		}
		
	}

	class ServletConfigImpl implements ServletConfig {

		public String getInitParameter(String arg0) {
			return null;
		}

		public Enumeration getInitParameterNames() {
			return null;
		}

		public ServletContext getServletContext() {
			return null;
		}

		public String getServletName() {
			return null;
		}
		
	}
	
	class HttpServletRequestImpl implements HttpServletRequest {

		public void logout() {}

		public Object getAttribute(String arg0) {
			return requestAtts.get(arg0);
		}

		public Enumeration getAttributeNames() {
			return null;
		}

		public String getCharacterEncoding() {
			return null;
		}

		public int getContentLength() {
			return 0;
		}

		public String getContentType() {
			return null;
		}

		public ServletInputStream getInputStream() throws IOException {
			return is;
		}

		public String getLocalAddr() {
			return null;
		}

		public String getLocalName() {
			return null;
		}

		public int getLocalPort() {
			return 0;
		}


		public Locale getLocale() {
			return null;
		}

		public Enumeration getLocales() {
			return null;
		}

		public String getParameter(String arg0) {
			return null;
		}

		public Map getParameterMap() {
			return null;
		}

		public Enumeration getParameterNames() {
			return null;
		}

		public String[] getParameterValues(String arg0) {
			return null;
		}

		public String getProtocol() {
			return "HTTP/1.1";
		}

		public BufferedReader getReader() throws IOException {
			return null;
		}

		public String getRealPath(String arg0) {
			return null;
		}

		public String getRemoteAddr() {
			return null;
		}

		public String getRemoteHost() {
			return "127.0.0.1";
		}

		public int getRemotePort() {
			return 0;
		}

		public RequestDispatcher getRequestDispatcher(String arg0) {
			return null;
		}

		public String getScheme() {
			return null;
		}

		public String getServerName() {
			return null;
		}

		public int getServerPort() {
			return 0;
		}

		public boolean isSecure() {
			return false;
		}

		public void removeAttribute(String arg0) {

		}

		public void setAttribute(String arg0, Object arg1) {
			requestAtts.put(arg0, arg1);
		}

		public void setCharacterEncoding(String arg0)
				throws UnsupportedEncodingException {

		}

		public String getAuthType() {
			return null;
		}

		public String getContextPath() {
			return null;
		}

		public Cookie[] getCookies() {
			return null;
		}

		public long getDateHeader(String arg0) {
			return 0;
		}

		public String getHeader(String arg0) {
			return requestHdrs.get(arg0);
		}

		public Enumeration getHeaderNames() {
			return inputMessage.getHeaderNames();
		}

		public Enumeration getHeaders(String arg0) {
			return null;
		}

		public int getIntHeader(String arg0) {
			return 0;
		}

		public String getMethod() {
			return "POST";
		}

		public String getPathInfo() {
			return null;
		}

		public String getPathTranslated() {
			return null;
		}

		public String getQueryString() {
			return null;
		}

		public String getRemoteUser() {
			return null;
		}

		public String getRequestURI() {
			return requestURI;
		}

		public StringBuffer getRequestURL() {
			return null;
		}

		public String getRequestedSessionId() {
			return null;
		}

		public String getServletPath() {
			return null;
		}

		public HttpSession getSession() {
			return session;
		}

		public HttpSession getSession(boolean arg0) {
			return null;
		}

		public Principal getUserPrincipal() {
			return null;
		}

		public boolean isRequestedSessionIdFromCookie() {
			return false;
		}

		public boolean isRequestedSessionIdFromURL() {
			return false;
		}

		public boolean isRequestedSessionIdFromUrl() {
			return false;
		}

		public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
			return false;
		}

		public void login(String s, String s1) throws ServletException {

		}

		public boolean isRequestedSessionIdValid() {
			return false;
		}

		public boolean isUserInRole(String arg0) {
			return false;
		}

		public Part getPart(String x) { return null; }
		public List<Part> getParts() { return null; }
	}
	
	class HttpServletResponseImpl implements HttpServletResponse {

				public List<String> getHeaderNames() { return null; }
		public List<String> getHeaders(String name) { return null; }

		public void flushBuffer() throws IOException {

		}

		public int getBufferSize() {
			return 0;
		}

		public String getCharacterEncoding() {
			return null;
		}

		public String getContentType() {
			return null;
		}

		public Locale getLocale() {
			return null;
		}

		public ServletOutputStream getOutputStream() throws IOException {
			return os;
		}

		public PrintWriter getWriter() throws IOException {
			return null;
		}

		public boolean isCommitted() {
			return false;
		}

		public void reset() {

		}

		public void resetBuffer() {

		}

		public void setBufferSize(int arg0) {

		}

		public void setCharacterEncoding(String arg0) {

		}

		public void setContentLength(int arg0) {

		}

		public void setContentType(String arg0) {

		}

		public void setLocale(Locale arg0) {

		}

		public void addCookie(Cookie arg0) {

		}

		public void addDateHeader(String arg0, long arg1) {

		}

		public void addHeader(String arg0, String arg1) {

		}

		public void addIntHeader(String arg0, int arg1) {

		}

		public boolean containsHeader(String arg0) {
			return false;
		}

		public String encodeRedirectURL(String arg0) {
			return null;
		}

		public String encodeRedirectUrl(String arg0) {
			return null;
		}

		public String encodeURL(String arg0) {
			return null;
		}

		public String encodeUrl(String arg0) {
			return null;
		}

		public void sendError(int arg0) throws IOException {

		}

		public void sendError(int arg0, String arg1) throws IOException {

		}

		public void sendRedirect(String arg0) throws IOException {

		}

		public void setDateHeader(String arg0, long arg1) {

		}

		public void setHeader(String arg0, String arg1) {
			outMsg.addHeader(arg0, arg0);
		}

		public void setIntHeader(String arg0, int arg1) {

		}

		public void setStatus(int arg0) {

		}

		public void setStatus(int arg0, String arg1) {

		}

		public int getStatus() {
			return 0;
		}

		public String getHeader(String s) {
			return null;
		}
	}
	
	class ServletOutputStreamImpl extends ServletOutputStream {
		StringBuffer buf = new StringBuffer();
		
		public void write(int b) throws IOException {
			buf.append(b);
		}
		
		public String toString() {
			return buf.toString();
		}
		
	}
	
	class ServletInputStreamImpl extends ServletInputStream {
		String input;
		int i;
		
		ServletInputStreamImpl(String input) {
			this.input = input;
			i=0;
		}
		
		public int read() throws IOException {
			if (i >= input.length())
				return -1;
			int val = input.charAt(i);
			i++;
			return val;
		}
		
	}
	
	
}
