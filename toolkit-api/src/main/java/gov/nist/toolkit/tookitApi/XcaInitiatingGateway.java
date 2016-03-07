package gov.nist.toolkit.tookitApi;

import java.io.IOException;

import javax.ws.rs.core.Response;

import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import gov.nist.toolkit.toolkitServicesCommon.LeafClassList;
import gov.nist.toolkit.toolkitServicesCommon.resource.LeafClassListResource;

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
