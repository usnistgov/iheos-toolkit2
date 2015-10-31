package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.RawSendRequest;
import gov.nist.toolkit.toolkitServicesCommon.RawSendResponse;

/**
 * Created by bill on 10/31/15.
 */
public class XdrDocumentSource extends AbstractActor implements DocumentSource {

    @Override
    public RawSendResponse sendRawProvideAndRegister(RawSendRequest request) throws ToolkitServiceException {
        return engine.sendXdr(request);
    }

}
