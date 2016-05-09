/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon;

/**
 * Retrieve Imaging Document Set Response Document  corresponds to
 * {@code <DocumentResponse>} element in {@code <RetrieveDocumentSetResponse>}
 * message. 
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public interface RetImgDocSetRespDocument {
   String getDocumentUid();
   String getRepositoryUid();
   String getHomeCommunityUid();
    String getMimeType();
    byte[] getDocumentContents();

}
