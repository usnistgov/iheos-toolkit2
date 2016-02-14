package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.RawSendRequest;
import gov.nist.toolkit.toolkitServicesCommon.resource.RawSendRequestResource;
import gov.nist.toolkit.toolkitServicesCommon.RawSendResponse;

/**
 *
 */
class XdrDocumentSource extends AbstractActor implements DocumentSource {

    /**
     * Send a raw Provide and Register request.
     * @param request raw request
     * @return raw response
     * @throws ToolkitServiceException if something goes wrong
     */
    @Override
    public RawSendResponse sendProvideAndRegister(RawSendRequest request) throws ToolkitServiceException {
        return engine.sendXdr(request);
    }

    /**
     * Create empty raw send request for this actor. This request can be filled in and then sent to the actor.
     * @return the empty request
     */
    public RawSendRequest newRawSendRequest() { return new RawSendRequestResource(config); }

}
