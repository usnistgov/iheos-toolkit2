package gov.nist.toolkit.fhir.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Implement request and response logging for FHIR server
 */
public class FhirServletFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        ServletRequest requestWrapper = new CustomHttpServletRequestWrapper((HttpServletRequest) servletRequest);

        // Log request via requestWrapper.getInputStream()
        // uri has form    /fsim/default__fire/Patient/1 for a READ operation
        String uri = ((HttpServletRequest) servletRequest).getRequestURI();

        // forward on request
        filterChain.doFilter(servletRequest, servletResponse);

        // need to do the same magic here to capture the response for logging


    }


    @Override
    public void destroy() {

    }
}
