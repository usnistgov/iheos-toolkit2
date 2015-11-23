package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.ObjectRefList;
import gov.nist.toolkit.toolkitServicesCommon.ObjectRefListResource;

import javax.ws.rs.core.Response;

/**
 *
 */
public class XdsDocumentRegRep extends AbstractActor implements DocumentRegRep {
    @Override
    public ObjectRefList findDocumentsForPatientID(String patientID) throws ToolkitServiceException {
        Response response = engine.getTarget()
                .path(String.format("simulators/%s/xds/GetAllDocs/%s", getConfig().getFullId(), patientID))
                .request().get();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(ObjectRefListResource.class);
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
