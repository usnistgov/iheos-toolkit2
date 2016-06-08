package gov.nist.toolkit.registrymsg.repository;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import org.apache.axiom.om.OMElement;

/**
 *
 */
public class RetrieveRequestParser {
    RetrieveRequestModel model = new RetrieveRequestModel();
    OMElement ele;

    public RetrieveRequestParser(OMElement ele) {
        this.ele = ele;
    }

    public RetrieveRequestModel getRequest() {
        parse();
        return model;
    }

    void parse() {
        for (OMElement requestEle : XmlUtil.decendentsWithLocalName(ele, "DocumentRequest")) {
            RetrieveItemRequestModel item = new RetrieveItemRequestModel();

            OMElement homeEle = requestEle.getFirstChildWithName(MetadataSupport.home_community_id_qname);
            if (homeEle != null) item.setHomeId(homeEle.getText());
            OMElement repoEle = requestEle.getFirstChildWithName(MetadataSupport.repository_unique_id_qnamens);
            if (repoEle != null) item.setRepositoryId(repoEle.getText());
            OMElement docEle = requestEle.getFirstChildWithName(MetadataSupport.document_unique_id_qnamens);
            if (docEle != null) item.setDocumentId(docEle.getText());

            model.add(item);
        }

    }
}
