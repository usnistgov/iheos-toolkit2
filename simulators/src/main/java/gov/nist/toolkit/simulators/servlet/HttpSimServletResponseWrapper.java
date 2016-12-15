/**
 * 
 */
package gov.nist.toolkit.simulators.servlet;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import edu.wustl.mir.erl.ihe.xdsi.util.Utility;

/**
 * Wraps http response for logging
 */
public class HttpSimServletResponseWrapper extends HttpServletResponseWrapper {
   
   static Logger logger = Logger.getLogger(HttpSimServletResponseWrapper.class);

   static final String nl = Utility.nl;
   
   HttpServletResponse response;
   Map<String, String> headers = new LinkedHashMap<String, String>();
   Integer status = null;
   String statusMessage = null;
   String contentType = null;
   Integer contentLength = null;
   byte[] body = new byte[0];

   /**
    * @param response
    */
   public HttpSimServletResponseWrapper(HttpServletResponse response) {
      super(response);
      this.response = response;
   }
   
   @Override
   public void setHeader(String name, String value) {
      headers.put(name, value);
      response.setHeader(name, value);
   }
   
   @Override
   public void addHeader(String name, String value) {
      headers.put(name, value);
      response.addHeader(name, value);
   }

   @Override
   public void setContentType(String type) {
      setHeader("Content-Type", type);
      response.setContentType(type);
      contentType = type;
   }
   
   @Override
   public void setDateHeader(String name, long date) {
      setHeader(name, new Date(date).toString());
      response.setDateHeader(name, date);
   }
   
   @Override
   public void addDateHeader( String name, long date) {
      setHeader(name, new Date(date).toString());
      response.addDateHeader(name, date);
   }
   
   @Override
   public void setContentLength(int len) {
      contentLength = len;
      setHeader("Content-Length", contentLength.toString());
      response.setContentLength(len);
   }
   
   @Override
   public void setStatus(int sc) {
      status = sc;
      response.setStatus(sc);
   }
   
   @Override
   public void sendError(int sc, String msg) throws IOException {
      status = sc;
      statusMessage = msg;
      response.sendError(sc, msg);
   }
   
   @Override
   public ServletOutputStreamWrapper getOutputStream() throws IOException {
      return new ServletOutputStreamWrapper(response.getOutputStream(), this);
   }
   
   /**
    * Add passed bytes to contents. This should be called only by 
    * ServletOutputStreamWrapper
    * @param bytes byte[] to add.
    */
   public void addBytes(byte[] bytes) {
      byte[] dest = new byte[body.length + bytes.length];
      if (body.length > 0)
         System.arraycopy(body, 0, dest, 0, body.length);
      if (bytes.length > 0)
         System.arraycopy(bytes, 0, dest, body.length, bytes.length);
      body = dest;
   }
   
   @Override
   public String toString() {
      if (StringUtils.isBlank(statusMessage) && status == 200)
         statusMessage = "OK";
      StringBuilder str = new StringBuilder("HTTP/1.1 " + status + " " + statusMessage + nl);
      for (Map.Entry <String, String> entry : headers.entrySet()) 
         str.append(entry.getKey() + ":" + entry.getValue() + nl);
      if (StringUtils.isNotBlank(contentType) && contentType.startsWith("text/"))
         str.append(new String(body,Utility.utf8));
      else if (contentLength != null || contentLength > 0)
         str.append("contents " + contentLength + " bytes");
      return str.toString();
   }
   
   /**
    * @return response header in string form
    */
   public String getResponseHeader() {
      if (StringUtils.isBlank(statusMessage) && status == 200)
         statusMessage = "OK";
      StringBuilder str = new StringBuilder("HTTP/1.1 " + status + " " + statusMessage + nl);
      for (Map.Entry <String, String> entry : headers.entrySet()) 
         str.append(entry.getKey() + ":" + entry.getValue() + nl);
      return str.toString();
   }
   
   /**
    * @return response body in string form if text, otherwise message giving
    * body length in bytes.
    */
   public String getResponseBody() {
      StringBuilder str = new StringBuilder();
      if (StringUtils.isNotBlank(contentType) && contentType.startsWith("text/"))
         str.append(new String(body,Utility.utf8));
      else if (contentLength != null || contentLength > 0)
         str.append("contents " + contentLength + " bytes");
      return str.toString();
   }

}
