/**
 * 
 */
package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import gov.nist.toolkit.toolkitServicesCommon.DcmImageSet;

import java.io.IOException;

/**
 * Implementation class for XDSI Image Document Source
 */
public class XdsiImagingDocumentSource extends AbstractActor implements ImagingDocumentSource {

   /* (non-Javadoc)
    * @see gov.nist.toolkit.tookitApi.ImageDocumentSource#retrieveImagingDocumentSet(gov.nist.toolkit.toolkitServicesCommon.DcmImageSet)
    */
   @Override
   public DcmImageSet retrieveImagingDocumentSet(DcmImageSet request) {
      // TODO Auto-generated method stub
      return null;
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
