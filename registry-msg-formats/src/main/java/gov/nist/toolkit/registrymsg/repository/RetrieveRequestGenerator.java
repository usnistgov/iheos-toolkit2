package gov.nist.toolkit.registrymsg.repository;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import org.apache.axiom.om.OMElement;

import java.util.List;

/**
 *
 */
public class RetrieveRequestGenerator {
    List<RetrieveItemRequestModel> items;

    public RetrieveRequestGenerator(List<RetrieveItemRequestModel> items) {
        this.items = items;
    }

    public OMElement get() {
        OMElement request = XmlUtil.createElement("RetrieveDocumentSetRequest", MetadataSupport.xdsB);
        for (RetrieveItemRequestModel item : items) {
            OMElement docReq = XmlUtil.createElement("DocumentRequest", MetadataSupport.xdsB);
            request.addChild(docReq);

            if (item.hasHomeId()) {
                OMElement homeEle = MetadataSupport.om_factory.createOMElement(MetadataSupport.home_community_id_qname);
                docReq.addChild(homeEle);
                homeEle.setText(item.getHomeId());
            }

            OMElement repoEle = MetadataSupport.om_factory.createOMElement(MetadataSupport.repository_unique_id_qnamens);
            docReq.addChild(repoEle);
            repoEle.setText(item.getRepositoryId());

            OMElement docEle = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_unique_id_qnamens);
            docReq.addChild(docEle);
            docEle.setText(item.getDocumentId());
        }


        return request;
    }
}
