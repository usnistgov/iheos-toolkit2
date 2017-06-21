package gov.nist.toolkit.fhirserver2.servlet;

import ca.uhn.fhir.rest.server.IServerAddressStrategy;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * this addressing strategy takes into account the simulator ID which is part of the address.
 */
public class ToolkitServerAddressStrategy implements IServerAddressStrategy {

//    static final private String context = "fsim";

    private String base = null;

    @Override
    public String determineServerBase(ServletContext servletContext, HttpServletRequest httpServletRequest) {
        if (base != null)
            return base;


        String uri =  httpServletRequest.getRequestURI();

        int index = uri.indexOf(HttpRequestParser.CONTEXT);
        if (index == -1) return uri;
        index = uri.indexOf("/", index); // / following context
        if (index == -1) return uri;
        index++;     // start of simId
//        int simIdStart = index;
        if (index >= uri.length()) return uri;
        index = uri.indexOf("/", index +1);
//        int simIdEnd = index;

//        String simId = uri.substring(simIdStart, simIdEnd);
//
//        SimContext simContext = new SimContext(new SimId(simId));
//        new ToolkitContext(httpServletRequest).setSimContext(simContext);

        base = uri.substring(0, index);
        return base;
    }
}
