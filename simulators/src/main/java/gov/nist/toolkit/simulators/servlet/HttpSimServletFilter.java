/**
 * 
 */
package gov.nist.toolkit.simulators.servlet;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;

/**
 * Filter for HttpSimServlet logging
 */
public class HttpSimServletFilter implements Filter {
   
   static Logger logger = Logger.getLogger(SimServletFilter.class);
   
   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
      HttpSimServletResponseWrapper wrapper = 
         new HttpSimServletResponseWrapper((HttpServletResponse) response); 
      
      logger.debug("in doFilter");
      
      SimDb db = (SimDb) request.getAttribute("SimDb");
      if (db == null) {
         logger.error("HttpSimServletFilter - request.getAttribute(\"SimDb\") failed");
         return;
      }
        SimulatorConfig config = (SimulatorConfig) request.getAttribute("SimulatorConfig");
        if (config == null) {
            logger.error("HttpSimServletFilter - request.getAttribute(\"SimulatorConfig\") failed");
            return;
        }
      
   }

   
   @Override
   public void destroy() {
   }

   @Override
   public void init(FilterConfig filterConfig) throws ServletException {
   }

}
