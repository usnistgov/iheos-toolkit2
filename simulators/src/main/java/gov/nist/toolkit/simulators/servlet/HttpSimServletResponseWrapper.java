/**
 * 
 */
package gov.nist.toolkit.simulators.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;

/**
 * Wraps http response for logging
 */
public class HttpSimServletResponseWrapper extends HttpServletResponseWrapper {
   
   static Logger logger = Logger.getLogger(SimServletResponseWrapper.class);
   
   HttpServletResponse response;
   Map<String, String> headers = new HashMap<String, String>();
   Integer status = null;
   String statusMessage = null;
   String contentType = null;

   /**
    * @param response
    */
   public HttpSimServletResponseWrapper(HttpServletResponse response) {
      super(response);
      this.response = response;
   }
   
   public void setHeader(String name, String value) {
      headers.put(name, value);
      response.setHeader(name, value);
   }
   
   public void addHeader(String name, String value) {
      headers.put(name, value);
      response.addHeader(name, value);
   }

   public void setContentType(String type) {
      logger.fatal("ContentType => " + type);
      response.setContentType(type);
      contentType = type;
   }
   
   public void setStatus(int sc) {
      status = sc;
      response.setStatus(sc);
   }
   
   public void sendError(int sc, String msg) throws IOException {
      status = sc;
      statusMessage = msg;
      response.sendError(sc, msg);
   }

}
