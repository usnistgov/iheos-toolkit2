package gov.nist.toolkit.fhirServer.config;

import ca.uhn.fhir.rest.server.IServerAddressStrategy;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public class ToolkitIncomingRequestAddressStrategy implements IServerAddressStrategy {
    @Override
    public String determineServerBase(ServletContext theServletContext, HttpServletRequest theRequest) {
        StringBuffer requestUrl = theRequest.getRequestURL();
//        int findex = requestUrl.indexOf("/fsim/");
//        if (findex == -1)  // punt and use default
//            return new ToolkitIncomingRequestAddressStrategy().determineServerBase(theServletContext, theRequest);
//
//
//        int eindex = requestUrl.indexOf("/", findex + "/fsim/".length());
//
//
//        int length = eindex;
//        return requestUrl.substring(0, length);
        return getServerBase(new String(requestUrl));
    }

    public String getServerBase(String theRequestUrl) {
        StringBuffer requestUrl = new StringBuffer(theRequestUrl);
        int findex = requestUrl.indexOf("/fsim/");
        if (findex == -1)  // punt and use default
            return theRequestUrl;


        int eindex = requestUrl.indexOf("/", findex + "/fsim/".length());


        int length = eindex + 1;
        return requestUrl.substring(0, length);
    }
}
