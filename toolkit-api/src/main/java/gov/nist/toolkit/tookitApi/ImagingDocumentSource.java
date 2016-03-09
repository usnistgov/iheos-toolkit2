/**
 * 
 */
package gov.nist.toolkit.tookitApi;

import gov.nist.toolkit.toolkitServicesCommon.DcmImageSet;

/**
 * XDSI Image Document Source API
 */
public interface ImagingDocumentSource extends AbstractActorInterface {

   DcmImageSet retrieveImagingDocumentSet(DcmImageSet request);
}
