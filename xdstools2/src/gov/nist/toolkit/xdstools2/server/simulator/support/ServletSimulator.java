package gov.nist.toolkit.xdstools2.server.simulator.support;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;
import gov.nist.toolkit.http.HttpMessage;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;


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
			// TODO Auto-generated method stub
			return null;
		}

		public long getCreationTime() {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getId() {
			// TODO Auto-generated method stub
			return null;
		}

		public long getLastAccessedTime() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getMaxInactiveInterval() {
			// TODO Auto-generated method stub
			return 0;
		}

		public ServletContext getServletContext() {
			// TODO Auto-generated method stub
			return null;
		}

		public HttpSessionContext getSessionContext() {
			// TODO Auto-generated method stub
			return null;
		}

		public Object getValue(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String[] getValueNames() {
			// TODO Auto-generated method stub
			return null;
		}

		public void invalidate() {
			// TODO Auto-generated method stub
			
		}

		public boolean isNew() {
			// TODO Auto-generated method stub
			return false;
		}

		public void putValue(String arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}

		public void removeAttribute(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void removeValue(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setAttribute(String arg0, Object arg1) {
			sessionAtts.put(arg0, arg1);
		}

		public void setMaxInactiveInterval(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}

	class ServletConfigImpl implements ServletConfig {

		public String getInitParameter(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public Enumeration getInitParameterNames() {
			// TODO Auto-generated method stub
			return null;
		}

		public ServletContext getServletContext() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getServletName() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	class HttpServletRequestImpl implements HttpServletRequest {

		public Object getAttribute(String arg0) {
			return requestAtts.get(arg0);
		}

		public Enumeration getAttributeNames() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getCharacterEncoding() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getContentLength() {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getContentType() {
			// TODO Auto-generated method stub
			return null;
		}

		public ServletInputStream getInputStream() throws IOException {
			return is;
		}

		public String getLocalAddr() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getLocalName() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getLocalPort() {
			// TODO Auto-generated method stub
			return 0;
		}

		public Locale getLocale() {
			// TODO Auto-generated method stub
			return null;
		}

		public Enumeration getLocales() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getParameter(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public Map getParameterMap() {
			// TODO Auto-generated method stub
			return null;
		}

		public Enumeration getParameterNames() {
			// TODO Auto-generated method stub
			return null;
		}

		public String[] getParameterValues(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getProtocol() {
			return "HTTP/1.1";
		}

		public BufferedReader getReader() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		public String getRealPath(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getRemoteAddr() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getRemoteHost() {
			return "127.0.0.1";
		}

		public int getRemotePort() {
			// TODO Auto-generated method stub
			return 0;
		}

		public RequestDispatcher getRequestDispatcher(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getScheme() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getServerName() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getServerPort() {
			// TODO Auto-generated method stub
			return 0;
		}

		public boolean isSecure() {
			// TODO Auto-generated method stub
			return false;
		}

		public void removeAttribute(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setAttribute(String arg0, Object arg1) {
			requestAtts.put(arg0, arg1);
		}

		public void setCharacterEncoding(String arg0)
				throws UnsupportedEncodingException {
			// TODO Auto-generated method stub
			
		}

		public String getAuthType() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getContextPath() {
			// TODO Auto-generated method stub
			return null;
		}

		public Cookie[] getCookies() {
			// TODO Auto-generated method stub
			return null;
		}

		public long getDateHeader(String arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getHeader(String arg0) {
			return requestHdrs.get(arg0);
		}

		public Enumeration getHeaderNames() {
			return inputMessage.getHeaderNames();
		}

		public Enumeration getHeaders(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public int getIntHeader(String arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getMethod() {
			return "POST";
		}

		public String getPathInfo() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getPathTranslated() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getQueryString() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getRemoteUser() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getRequestURI() {
			return requestURI;
		}

		public StringBuffer getRequestURL() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getRequestedSessionId() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getServletPath() {
			// TODO Auto-generated method stub
			return null;
		}

		public HttpSession getSession() {
			return session;
		}

		public HttpSession getSession(boolean arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public Principal getUserPrincipal() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isRequestedSessionIdFromCookie() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isRequestedSessionIdFromURL() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isRequestedSessionIdFromUrl() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isRequestedSessionIdValid() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isUserInRole(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	class HttpServletResponseImpl implements HttpServletResponse {

		public void flushBuffer() throws IOException {
			// TODO Auto-generated method stub
			
		}

		public int getBufferSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getCharacterEncoding() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getContentType() {
			// TODO Auto-generated method stub
			return null;
		}

		public Locale getLocale() {
			// TODO Auto-generated method stub
			return null;
		}

		public ServletOutputStream getOutputStream() throws IOException {
			return os;
		}

		public PrintWriter getWriter() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isCommitted() {
			// TODO Auto-generated method stub
			return false;
		}

		public void reset() {
			// TODO Auto-generated method stub
			
		}

		public void resetBuffer() {
			// TODO Auto-generated method stub
			
		}

		public void setBufferSize(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setCharacterEncoding(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setContentLength(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setContentType(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setLocale(Locale arg0) {
			// TODO Auto-generated method stub
			
		}

		public void addCookie(Cookie arg0) {
			// TODO Auto-generated method stub
			
		}

		public void addDateHeader(String arg0, long arg1) {
			// TODO Auto-generated method stub
			
		}

		public void addHeader(String arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}

		public void addIntHeader(String arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		public boolean containsHeader(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		public String encodeRedirectURL(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String encodeRedirectUrl(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String encodeURL(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String encodeUrl(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public void sendError(int arg0) throws IOException {
			// TODO Auto-generated method stub
			
		}

		public void sendError(int arg0, String arg1) throws IOException {
			// TODO Auto-generated method stub
			
		}

		public void sendRedirect(String arg0) throws IOException {
			// TODO Auto-generated method stub
			
		}

		public void setDateHeader(String arg0, long arg1) {
			// TODO Auto-generated method stub
			
		}

		public void setHeader(String arg0, String arg1) {
			outMsg.addHeader(arg0, arg0);
		}

		public void setIntHeader(String arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		public void setStatus(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setStatus(int arg0, String arg1) {
			// TODO Auto-generated method stub
			
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
