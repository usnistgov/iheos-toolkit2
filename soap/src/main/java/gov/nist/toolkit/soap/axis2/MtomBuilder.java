package gov.nist.toolkit.soap.axis2;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.ds.ByteArrayDataSource;

import javax.activation.DataSource;
import java.util.Map;

/**
 *
 */
public class MtomBuilder {
    OMElement metadataEle;
    // id ==> Document Content
    // ids must be represented in metadata as ExtrinsicObjects
    Map<String, ByteArrayDataSource> documents;

    public OMElement getBody() {
        for (String id : documents.keySet()) {
            ByteArrayDataSource content = documents.get(id);

            javax.activation.DataHandler dataHandler = new javax.activation.DataHandler((DataSource)content);
            OMText t = MetadataSupport.om_factory.createOMText(dataHandler, true);
            t.setOptimize(true);
            OMElement document = MetadataSupport.om_factory.createOMElement("Document", MetadataSupport.xdsB);
            document.addAttribute("id", id, null);
            document.addChild(t);
            metadataEle.addChild(document);
        }
        return metadataEle;
    }

    public void addDocument(String id, ByteArrayDataSource dataSource) {
        documents.put(id, dataSource);
    }

    public OMElement getMetadataEle() {
        return metadataEle;
    }

    /**
     *
     * @param metadataEle XML part - will be modified
     */
    public void setMetadataEle(OMElement metadataEle) {
        this.metadataEle = metadataEle;
    }
}
