package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitServicesCommon.DocumentContent;

/**
 *
 */
public interface DocumentRepository extends AbstractActorInterface {


    /**
     * Get contents of a document
     * @param uniqueId - DocumentEntry.uniqueId
     * @return contents of the document
     * @throws ToolkitServiceException
     */
    DocumentContent getDocument(String uniqueId) throws ToolkitServiceException;
}
