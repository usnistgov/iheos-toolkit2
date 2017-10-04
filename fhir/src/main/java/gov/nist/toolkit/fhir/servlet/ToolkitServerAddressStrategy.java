package gov.nist.toolkit.fhir.servlet;

import ca.uhn.fhir.rest.server.IServerAddressStrategy;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * this addressing strategy takes into account the simulator ID which is part of the address.
 */
public class ToolkitServerAddressStrategy implements IServerAddressStrategy {

    private String base = null;

    @Override
    public String determineServerBase(ServletContext servletContext, HttpServletRequest httpServletRequest) {
        if (base != null)
            return base;


        String uri =  httpServletRequest.getRequestURI();

        int index = uri.indexOf(HttpRequestParser.CONTEXT);   // CONTEXT is fsim - the FHIR sim URI element
        if (index == -1) return uri;
        index = uri.indexOf("/", index); // / following context
        if (index == -1) return uri;
        index++;     // start of simId
        if (index >= uri.length()) return uri;
        index = uri.indexOf("/", index +1);  // / following simid
        index++;
        index = uri.indexOf("/", index);  // / following "fhir"

        base = uri.substring(0, index);
        return base;
    }
}
