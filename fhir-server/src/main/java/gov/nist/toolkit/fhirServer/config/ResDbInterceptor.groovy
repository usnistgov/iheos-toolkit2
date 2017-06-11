package gov.nist.toolkit.fhirServer.config

import ca.uhn.fhir.rest.method.RequestDetails
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter
import gov.nist.toolkit.fhir.support.SimContext
import org.hl7.fhir.instance.model.api.IBaseResource

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This interceptor handles locking of the ResDb
 */
class ResDbInterceptor extends InterceptorAdapter {

    @Override
    public boolean incomingRequestPreProcessed(HttpServletRequest theRequest, HttpServletResponse theResponse) {
        SimContext simContext = SimTracker.getContext()

        return super.incomingRequestPreProcessed(theRequest, theResponse)
    }

    @Override
    public boolean outgoingResponse(RequestDetails theRequestDetails, IBaseResource theResponseObject, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse)
            throws AuthenticationException {
        SimContext simContext = SimTracker.getContext()



        return super.outgoingResponse(theRequestDetails,  theResponseObject,  theServletRequest,  theServletResponse)
    }
}
