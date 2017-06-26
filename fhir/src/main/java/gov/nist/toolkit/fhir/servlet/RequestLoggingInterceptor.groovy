package gov.nist.toolkit.fhir.servlet

import ca.uhn.fhir.model.api.Bundle
import ca.uhn.fhir.model.api.TagList
import ca.uhn.fhir.rest.api.RestOperationTypeEnum
import ca.uhn.fhir.rest.method.RequestDetails
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.utilities.io.Io
import org.hl7.fhir.instance.model.api.IBaseResource

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
/**
 *
 */
class RequestLoggingInterceptor implements IServerInterceptor {

    static final String SIMID = 'SIMID'
    static final String SIMDB = 'SIMDB'

    @Override
    boolean handleException(RequestDetails theRequestDetails, BaseServerResponseException theException, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse) throws ServletException, IOException {
        return true
    }

    @Override
    boolean incomingRequestPostProcessed(RequestDetails theRequestDetails, HttpServletRequest theRequest, HttpServletResponse theResponse) throws AuthenticationException {
        return true
    }

    @Override
    void incomingRequestPreHandled(RestOperationTypeEnum theOperation, IServerInterceptor.ActionRequestDetails theProcessedRequest) {

    }

    @Override
    boolean incomingRequestPreProcessed(HttpServletRequest theRequest, HttpServletResponse theResponse) {
        // do logging here
        SimId simId = HttpRequestParser.simIdFromRequest(theRequest)
        if (!simId) return true // note: id not added to request

        SimDb simDb
        try {
            simDb = new SimDb(simId, SimDb.BASE_TYPE, SimDb.ANY_TRANSACTION)
        } catch (Exception e) {
            return true;// this will be noticed in the main flow and handled separately
        }


        theRequest.setAttribute(SIMID, simId)
        theRequest.setAttribute(SIMDB, simDb)

        List<String> headers = theRequest.headerNames.collect { String headerName ->
            "${headerName}: ${theRequest.getHeader(headerName)}"
        }

        String headerblock = headers.join('\r\n')

        simDb.putRequestHeaderFile(headerblock.bytes)
        InputStream ins = theRequest.inputStream
        assert ins.markSupported(), 'Mark not supported on inputstream'

        ins.mark(5*1024*1000)
        String request = Io.getStringFromInputStream(ins)
        ins.reset()  // so the REST processor can re-read

        simDb.putRequestBodyFile(request.bytes)

        return true
    }

    @Override
    boolean outgoingResponse(RequestDetails theRequestDetails) {
        return true
    }

    @Override
    boolean outgoingResponse(RequestDetails theRequest, Bundle theResponseObject) {
        return true
    }

    @Override
    boolean outgoingResponse(RequestDetails theRequestDetails, Bundle theResponseObject, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse) throws AuthenticationException {
        return true
    }

    @Override
    boolean outgoingResponse(RequestDetails theRequestDetails, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse) throws AuthenticationException {
        return true
    }

    @Override
    boolean outgoingResponse(RequestDetails theRequestDetails, IBaseResource theResponseObject) {
        // log single resource response



        return true
    }

    @Override
    boolean outgoingResponse(RequestDetails theRequestDetails, IBaseResource theResponseObject, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse) throws AuthenticationException {
        return true
    }

    @Override
    boolean outgoingResponse(RequestDetails theRequestDetails, TagList theResponseObject) {
        return true
    }

    @Override
    boolean outgoingResponse(RequestDetails theRequestDetails, TagList theResponseObject, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse) throws AuthenticationException {
        return true
    }

    @Override
    BaseServerResponseException preProcessOutgoingException(RequestDetails theRequestDetails, Throwable theException, HttpServletRequest theServletRequest) throws ServletException {
        return null
    }

    @Override
    void processingCompletedNormally(ServletRequestDetails theRequestDetails) {

    }
}
