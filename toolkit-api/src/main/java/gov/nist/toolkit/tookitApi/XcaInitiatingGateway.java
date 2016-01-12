package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.LeafClassList;
import gov.nist.toolkit.toolkitServicesCommon.LeafClassListResource;

import javax.ws.rs.core.Response;

/**
 *
 */
public class XcaInitiatingGateway  extends AbstractActor implements InitiatingGateway {
    @Override
    public LeafClassList FindDocuments(String patientID) throws ToolkitServiceException {
        Response response = engine.getTarget()
                .path(String.format("simulators/%s/xds/GetAllDocs/%s", getConfig().getFullId(), patientID))
                .request().get();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(LeafClassListResource.class);
    }

}
