package gov.nist.toolkit.soap.axis2;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.soap.Document;
import gov.nist.toolkit.soap.DocumentMap;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.log4j.Logger;

/**
 *
 */
public class MtomBuilder {
    static final Logger logger = Logger.getLogger(MtomBuilder.class);
    OMElement metadataEle;
    // id ==> Document Content
    // ids must be represented in metadata as ExtrinsicObjects
//    Map<String, ByteArrayDataSource> documents;
    DocumentMap documentMap = null;


    public OMElement getBody() {
        OMElement sor = MetadataSupport.firstChildWithLocalName(metadataEle, "SubmitObjectsRequest");
        if (sor == null) {
            String msg = "MtomBuilder: cannot find SubmitObjectsRequest child in PnR";
            logger.error(msg);
            throw new ToolkitRuntimeException(msg);
        }
        for (String id : documentMap.getIds()) {
            Document document = documentMap.getDocument(id);
            javax.activation.DataHandler dataHandler = new javax.activation.DataHandler(document.getDataSource());
            OMText t = MetadataSupport.om_factory.createOMText(dataHandler, true);
            t.setOptimize(true);
            OMElement documentEle = MetadataSupport.om_factory.createOMElement("Document", MetadataSupport.xdsB);
            documentEle.addAttribute("id", id, null);
            documentEle.addChild(t);

//            metadataEle.addChild(documentEle);

            sor.insertSiblingAfter(documentEle);
        }
        return metadataEle;
    }

//    public OMElement getBody() {
//        for (String id : documents.keySet()) {
//            ByteArrayDataSource content = documents.get(id);
//
//            javax.activation.DataHandler dataHandler = new javax.activation.DataHandler((DataSource)content);
//            OMText t = MetadataSupport.om_factory.createOMText(dataHandler, true);
//            t.setOptimize(true);
//            OMElement document = MetadataSupport.om_factory.createOMElement("Document", MetadataSupport.xdsB);
//            document.addAttribute("id", id, null);
//            document.addChild(t);
//            metadataEle.addChild(document);
//        }
//        return metadataEle;
//    }

//    public void addDocument(String id, ByteArrayDataSource dataSource) {
//        documents.put(id, dataSource);
//    }

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

    public DocumentMap getDocumentMap() {
        return documentMap;
    }

    public void setDocumentMap(DocumentMap documentMap) {
        this.documentMap = documentMap;
    }
}
