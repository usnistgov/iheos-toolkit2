/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon.resource;

import gov.nist.toolkit.toolkitServicesCommon.ImgRetrieveRequest;

/**
 *
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class RetrieveImgRequestResource extends SimIdResource implements ImgRetrieveRequest {
    RequestFlavorResource flavor = new RequestFlavorResource();

    public boolean isTls() {
        return flavor.isTls();
    }

    public void setTls(boolean tls) {
        flavor.setTls(tls);
    }
}
