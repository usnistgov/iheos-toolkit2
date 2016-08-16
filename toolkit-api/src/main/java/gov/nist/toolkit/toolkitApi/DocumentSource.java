package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitServicesCommon.RawSendRequest;
import gov.nist.toolkit.toolkitServicesCommon.RawSendResponse;

/**
 * Service interface for a Document Source.
 */
public interface DocumentSource extends AbstractActorInterface {

    /**
     * Send a raw Provide and Register transaction. Raw means that the user provides the SOAP Header,
     * SOAP Body and document map to send.  No updates are done the the metadata.  It is sent as provided.
     * The transactionType in the send request model is overwritten and need not be supplied.
     * See <a href="https://bitbucket.org/bmajur/toolkit2/src/tip/it-tests/src/test/groovy/gov/nist/toolkit/itTests/xdr/XdrSrcSpec.groovy?at=develop&fileviewer=file-view-default">here</a>
     * for test showing how this is used.
     *
     * @param request raw request
     * @return raw response
     * @throws ToolkitServiceException if something goes wrong in the sending of the transaction. If the transaction
     * return errors those are reflected in the RegistryResponse returned.
     */
    RawSendResponse sendProvideAndRegister(RawSendRequest request) throws ToolkitServiceException;

    RawSendRequest newRawSendRequest();
}
