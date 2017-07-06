package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitServicesCommon.DocumentContent;
import gov.nist.toolkit.toolkitServicesCommon.resource.DocumentContentResource;

import javax.ws.rs.core.Response;

/**
 *
 */
public class XdsDocumentRepository extends AbstractActor implements DocumentRepository {

    @Override
    public DocumentContent getDocument(String uniqueId) throws ToolkitServiceException {
        Response response = engine.getTarget()
                .path(String.format("simulators/%s/document/%s", getConfig().getFullId(), uniqueId))
                .request().get();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(DocumentContentResource.class);
    }


}
