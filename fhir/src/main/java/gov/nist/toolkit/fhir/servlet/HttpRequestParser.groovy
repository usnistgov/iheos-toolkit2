package gov.nist.toolkit.fhir.servlet

import gov.nist.toolkit.simcommon.client.SimId

import javax.servlet.http.HttpServletRequest
/**
 * This extracts the SimId from the HttpServletRequest
 */
class HttpRequestParser {

    static final protected String CONTEXT = "fsim"

    static SimId simIdFromRequest(HttpServletRequest httpServletRequest) {
        String uri =  httpServletRequest.getRequestURI();

        int index = uri.indexOf(CONTEXT);
        if (index == -1) return null;
        index = uri.indexOf("/", index); // / following context
        if (index == -1) return null;
        index++;     // start of simId
        int simIdStart = index;
        if (index >= uri.length()) return null;
        index = uri.indexOf("/", index +1);
        int simIdEnd = index;

        String simId = uri.substring(simIdStart, simIdEnd);

        return new SimId(simId).forFhir()
    }
}
