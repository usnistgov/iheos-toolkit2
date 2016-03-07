package gov.nist.toolkit.tookitApi;

import java.io.IOException;

import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import gov.nist.toolkit.toolkitServicesCommon.LeafClassRegistryResponse;
import gov.nist.toolkit.toolkitServicesCommon.RetrieveRequest;
import gov.nist.toolkit.toolkitServicesCommon.RetrieveResponse;
import gov.nist.toolkit.toolkitServicesCommon.StoredQueryRequest;

/**
 *
 */
public class XdsDocumentConsumer extends AbstractActor implements DocumentConsumer {
    @Override
    public LeafClassRegistryResponse queryForLeafClass(StoredQueryRequest request) throws ToolkitServiceException {
        return engine.queryForLeafClass(request);
    }

//    @Override
//    public RefList queryForObjectRef(String queryId, Map<String, List<String>> parameters) {
//        return null;
//    }

    @Override
    public RetrieveResponse retrieve(RetrieveRequest request) throws ToolkitServiceException {
        return engine.retrieve(request);
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
