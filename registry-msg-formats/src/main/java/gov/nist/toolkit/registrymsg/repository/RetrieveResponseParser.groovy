package gov.nist.toolkit.registrymsg.repository

import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.client.MetadataException
import groovy.transform.TypeChecked;
import org.apache.axiom.om.OMElement;

/**
 *
 */
@TypeChecked
public class RetrieveResponseParser {
    OMElement response;
    RetrievedDocumentsModel model = new RetrievedDocumentsModel();

    RetrieveResponseParser(String responseStr) {
        response = Util.parse_xml(responseStr)
    }

    public RetrieveResponseParser(OMElement response) {
        this.response = response;
    }

    public RetrievedDocumentsModel get() throws Exception {
        run();
        return model;
    }

    // model key is docUid
    void run() throws Exception {
        for (OMElement doc_response : XmlUtil.childrenWithLocalName(response, "DocumentResponse")) {
            RetrievedDocumentModel rr = new RetrievedDocumentModel();

            OMElement doc_uid_ele = XmlUtil.firstChildWithLocalName(doc_response, "DocumentUniqueId");
            rr.setDocUid((doc_uid_ele != null) ? doc_uid_ele.getText() : null);

            OMElement home_ele = XmlUtil.firstChildWithLocalName(doc_response, "HomeCommunityId");
            rr.setHome((home_ele != null) ? home_ele.getText() : null);

            OMElement rep_uid_ele = XmlUtil.firstChildWithLocalName(doc_response, "RepositoryUniqueId");
            rr.setRepUid((rep_uid_ele != null) ? rep_uid_ele.getText() : null);

            OMElement mime_type_ele = XmlUtil.firstChildWithLocalName(doc_response, "mimeType");
            rr.setContent_type((mime_type_ele != null) ? mime_type_ele.getText() : null);

            OMElement newDoc_uid_ele = XmlUtil.firstChildWithLocalName(doc_response, "NewDocumentUniqueId");
            rr.setNewDoc_uid((newDoc_uid_ele != null) ? newDoc_uid_ele.getText() : null);

            OMElement newRep_uid_ele = XmlUtil.firstChildWithLocalName(doc_response, "NewRepositoryUniqueId");
            rr.setNewRep_uid((newRep_uid_ele != null) ? newRep_uid_ele.getText() : null);

            OMElement document_content_ele = XmlUtil.firstChildWithLocalName(doc_response, "Document");

            Mtom mtom = new Mtom();
            mtom.decode(document_content_ele);

                String mtom_mime = mtom.getContent_type();
                boolean isOptimized = mtom.isOptimized();

                // MTOM encoding does not require correct/accurate content type.  If MTOM package punted
                // and used application/octet-stream then take mime type from retrieve response metadata
                if (mtom_mime != null && mtom_mime.equals("application/octet-stream") && isOptimized)
                    mtom_mime = rr.getContent_type();
                else if (mtom_mime != null && rr.getContent_type() != null && !rr.getContent_type().equals(mtom_mime))
                    rr.addError("Mime Type attribute (" + rr.getContent_type() + ") does not match Content-Type (" + mtom_mime + ")");
                rr.setContents(mtom.getContents());

            if (rr.getDocUid() == null)
                throw new MetadataException("parse_rep_result(): Document uniqueId not found in response", null);

            model.put(rr.getDocUid(), rr);
        }

    }

}
