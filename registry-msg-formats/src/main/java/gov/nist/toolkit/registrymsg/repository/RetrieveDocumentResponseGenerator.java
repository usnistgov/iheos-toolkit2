package gov.nist.toolkit.registrymsg.repository;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;

/**
 *
 */
public class RetrieveDocumentResponseGenerator {
    RetrievedDocumentsModel model;
    OMAttribute statusAtt;

    public RetrieveDocumentResponseGenerator(RetrievedDocumentsModel model) {
        this.model = model;
    }

    public OMElement get() {
        return generate();
    }

    OMElement generate() {
        OMElement response;
        response = MetadataSupport.om_factory.createOMElement("RegistryResponse", MetadataSupport.ebRSns3);
        OMElement rdsr = MetadataSupport.om_factory.createOMElement("RetrieveDocumentSetResponse", MetadataSupport.xdsB);
        rdsr.addChild(response);
        statusAtt = MetadataSupport.om_factory.createOMAttribute("status", null, MetadataSupport.status_success);
        response.addAttribute(statusAtt);

        for (RetrievedDocumentModel item : model.values()) {
            rdsr.addChild(new DocumentResponseGenerator(item).get());
        }
        return rdsr;
    }

    public OMAttribute getStatusAtt() { return statusAtt; }
}
