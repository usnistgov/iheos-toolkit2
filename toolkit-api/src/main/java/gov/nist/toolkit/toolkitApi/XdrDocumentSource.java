package gov.nist.toolkit.toolkitApi;

import java.io.IOException;

import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import gov.nist.toolkit.toolkitServicesCommon.RawSendRequest;
import gov.nist.toolkit.toolkitServicesCommon.RawSendResponse;
import gov.nist.toolkit.toolkitServicesCommon.resource.RawSendRequestResource;

/**
 *
 */
class XdrDocumentSource extends AbstractActor implements DocumentSource {

    /**
     * Send a raw Provide and Register request.
     * @param request raw request
     * @return raw response
     * @throws ToolkitServiceException if something goes wrong
     */
    @Override
    public RawSendResponse sendProvideAndRegister(RawSendRequest request) throws ToolkitServiceException {
        return engine.sendXdr(request);
    }

    /**
     * Create empty raw send request for this actor. This request can be filled in and then sent to the actor.
     * @return the empty request
     */
    public RawSendRequest newRawSendRequest() { return new RawSendRequestResource(config); }

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
