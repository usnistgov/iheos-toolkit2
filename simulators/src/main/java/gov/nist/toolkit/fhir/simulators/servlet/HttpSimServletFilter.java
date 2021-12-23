/**
 *
 */
package gov.nist.toolkit.fhir.simulators.servlet;

import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.GwtErrorRecorder;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.engine.ValidationStep;
import java.util.logging.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Filter for HttpSimServlet logging
 */
public class HttpSimServletFilter implements Filter {

   static Logger logger = Logger.getLogger(HttpSimServletFilter.class.getName());

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
      HttpSimServletResponseWrapper wrapper = new HttpSimServletResponseWrapper((HttpServletResponse) response);

      logger.fine("in HttpSimServletFilter");
      if (request instanceof HttpServletRequest) {
         HttpServletRequest r = (HttpServletRequest) request;
         logger.info(r.getRequestURI());
      }

      chain.doFilter(request, wrapper);


      SimDb db = (SimDb) request.getAttribute("SimDb");
      if (db == null) {
         logger.severe("HttpSimServletFilter - request.getAttribute(\"SimDb\") failed");
         return;
      }
      MessageValidatorEngine mvc = (MessageValidatorEngine) request.getAttribute("mvc");
      if (mvc == null) {
         logger.severe("HttpSimServletFilter - request.getAttribute(\"mvc\") failed");
         return;
      }

      Io.stringToFile(db.getResponseHdrFile(), wrapper.getResponseHeader());
      db.putResponseBody(wrapper.getResponseBody());
//      Io.stringToFile(db.getResponseBodyFile(), wrapper.getResponseBody());

      StringBuilder buf = new StringBuilder();

      // buf.append(mvc.toString());

      Enumeration <ValidationStep> steps = mvc.getValidationStepEnumeration();
      while (steps.hasMoreElements()) {
         ValidationStep step = steps.nextElement();
         buf.append(step).append("\n");
         ErrorRecorder er = step.getErrorRecorder();
         if (er instanceof GwtErrorRecorder) {
            GwtErrorRecorder ger = (GwtErrorRecorder) er;
            buf.append(ger);
         }
      }
      Io.stringToFile(db.getLogFile(), buf.toString());

   }

   @Override
   public void destroy() {}

   @Override
   public void init(FilterConfig filterConfig) throws ServletException {}

}
