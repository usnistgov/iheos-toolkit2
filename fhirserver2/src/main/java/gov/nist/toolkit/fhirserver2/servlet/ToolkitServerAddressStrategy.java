package gov.nist.toolkit.fhirserver2.servlet;

import ca.uhn.fhir.rest.server.IServerAddressStrategy;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.fhir.support.SimContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * this addressing strategy takes into account the simulator ID which is part of the address.
 */
public class ToolkitServerAddressStrategy implements IServerAddressStrategy {

    static final private String context = "fsim";

    @Override
    public String determineServerBase(ServletContext servletContext, HttpServletRequest httpServletRequest) {
        String uri =  httpServletRequest.getRequestURI();

        int index = uri.indexOf(context);
        if (index == -1) return uri;
        index = uri.indexOf("/", index); // / following context
        if (index == -1) return uri;
        index++;     // start of simId
        int simIdStart = index;
        if (index >= uri.length()) return uri;
        index = uri.indexOf("/", index +1);
        int simIdEnd = index;

        String simId = uri.substring(simIdStart, simIdEnd);

        SimContext simContext = new SimContext(new SimId(simId));
        new ToolkitContext(httpServletRequest).setSimContext(simContext);

        String base = uri.substring(0, index);
        return base;
    }
}
