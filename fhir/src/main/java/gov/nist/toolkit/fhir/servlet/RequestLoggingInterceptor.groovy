package gov.nist.toolkit.fhir.servlet

import ca.uhn.fhir.model.api.Bundle
import ca.uhn.fhir.model.api.TagList
import ca.uhn.fhir.rest.api.RestOperationTypeEnum
import ca.uhn.fhir.rest.method.RequestDetails
import ca.uhn.fhir.rest.server.IRestfulResponse
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import org.hl7.fhir.instance.model.api.IBaseResource

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
/**
 *
 */
class RequestLoggingInterceptor implements IServerInterceptor {


    @Override
    boolean handleException(RequestDetails theRequestDetails, BaseServerResponseException theException, HttpServletRequest theServletRequest, HttpServletResponse theServletResponse) throws ServletException, IOException {
        return true
    }

    @Override
    boolean incomingRequestPostProcessed(RequestDetails theRequestDetails, HttpServletRequest theRequest, HttpServletResponse theResponse) throws AuthenticationException {

        // Pass SIM attributes from the HTTP Request to the REST Attributes maps
        Attributes attributes = new Attributes(theRequestDetails)
        attributes.simId = theRequest.getAttribute(Attributes.SIMID)
        attributes.simDb = theRequest.getAttribute(Attributes.SIMDB)


//        //
//        // log incoming message
//        //
//        SimId simId = HttpRequestParser.simIdFromRequest(theRequestDetails)
//        if (!simId) return true // note: id not added to request
//
//        SimDb simDb
//        try {
//            simDb = new SimDb(simId, SimDb.BASE_TYPE, SimDb.ANY_TRANSACTION)
//        } catch (Exception e) {
//            return true;// this will be noticed in the main flow and handled separately
//        }
//
//        Attributes a = new Attributes(theRequestDetails)
//        a.simId = simId
//        a.simDb = simDb
//
//        List<String> headers = theRequest.headerNames.collect { String headerName ->
//            "${headerName}: ${theRequest.getHeader(headerName)}"
//        }
//
//        String headerblock = headers.join('\r\n')
//
//        simDb.putRequestHeaderFile(headerblock.bytes)
//        InputStream ins = theRequest.inputStream
//        assert ins.markSupported(), "Mark not supported on inputstream - implementing class is ${ins.getClass().getName()}"
//
//        ins.mark(1*1024*1000)
//        String request = Io.getStringFromInputStream(ins)
//        ins.reset()  // so the REST processor can re-read
//
//        simDb.putRequestBodyFile(request.bytes)


        return true
    }

    @Override
    void incomingRequestPreHandled(RestOperationTypeEnum theOperation, IServerInterceptor.ActionRequestDetails theProcessedRequest) {

    }

    @Override
    boolean incomingRequestPreProcessed(HttpServletRequest theRequest, HttpServletResponse theResponse) {
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
        //
        // log outgoing message
        //

        if (theResponseObject) {
            Attributes a = new Attributes(theRequestDetails)
            SimId simId = a.simId
            SimDb simDb = a.simDb

            assert simId, 'SimId not available to outgoing logger'
            assert simDb, 'SimDb not available to outgoing logger'

            String resourceString = ToolkitFhirContext.get().newJsonParser().encodeResourceToString(theResponseObject)

            simDb.putResponseBody(resourceString)
        }

        if (theRequestDetails.getResponse()) {
            assert theRequestDetails
            IRestfulResponse restfulResponse = theRequestDetails.getResponse()
        }

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
