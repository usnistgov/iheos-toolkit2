package gov.nist.toolkit.toolkitApi;

import java.io.IOException;

import javax.ws.rs.core.Response;

import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import gov.nist.toolkit.toolkitServicesCommon.DocumentContent;
import gov.nist.toolkit.toolkitServicesCommon.RefList;
import gov.nist.toolkit.toolkitServicesCommon.resource.DocumentContentResource;
import gov.nist.toolkit.toolkitServicesCommon.resource.RefListResource;

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

    @Override
    public DocumentContent getDocument(String uniqueId) throws ToolkitServiceException {
        Response response = engine.getTarget()
                .path(String.format("simulators/%s/document/%s", getConfig().getFullId(), uniqueId))
                .request().get();
        if (response.getStatus() != 200)
            throw new ToolkitServiceException(response);
        return response.readEntity(DocumentContentResource.class);
    }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.toolkitServicesCommon.SimConfig#setPatientErrorMap(gov.nist.toolkit.configDatatypes.client.PatientErrorMap)
    */
   @Override
   public void setPatientErrorMap(PatientErrorMap errorMap) throws IOException {
      // TODO Auto-generated method stub
      
   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.toolkitServicesCommon.SimConfig#getPatientErrorMap()
    */
   @Override
   public PatientErrorMap getPatientErrorMap() throws IOException {
      // TODO Auto-generated method stub
      return null;
   }

}
