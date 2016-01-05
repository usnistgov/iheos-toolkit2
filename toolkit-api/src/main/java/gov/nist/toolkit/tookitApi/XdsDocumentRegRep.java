package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.RefList;
import gov.nist.toolkit.toolkitServicesCommon.RefListResource;

import javax.ws.rs.core.Response;

/**
 *
 */
public class XdsDocumentRegRep extends AbstractActor implements DocumentRegRep {
    @Override
    public RefList findDocumentsForPatientID(String patientID) throws ToolkitServiceException {
        Response response = engine.getTarget()
                .path(String.format("simulators/%s/xds/GetAllDocs/%s", getConfig().getFullId(), patientID))
                .request().get();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(RefListResource.class);
    }

    @Override
    public String getDocEntry(String id) throws ToolkitServiceException {
        Response response = engine.getTarget()
                .path(String.format("simulators/%s/xds/GetDoc/%s", getConfig().getFullId(), id))
                .request().get();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(String.class);
    }

}
