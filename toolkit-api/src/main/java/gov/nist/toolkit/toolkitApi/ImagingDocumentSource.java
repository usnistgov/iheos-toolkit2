/**
 * 
 */
package gov.nist.toolkit.toolkitApi;

import gov.nist.toolkit.toolkitApi.AbstractActorInterface;
import gov.nist.toolkit.toolkitServicesCommon.DcmImageSet;

/**
 * XDSI Image Document Source API
 */
public interface ImagingDocumentSource extends AbstractActorInterface {

   DcmImageSet retrieveImagingDocumentSet(DcmImageSet request);
}
