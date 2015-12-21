package gov.nist.toolkit.registrymsg.repository
import gov.nist.toolkit.registrysupport.MetadataSupport
import groovy.transform.TypeChecked
import org.apache.axiom.om.OMElement
import sun.misc.BASE64Encoder
/**
 *
 */
@TypeChecked
public class DocumentResponseGenerator {
    RetrievedDocumentModel model;

    public DocumentResponseGenerator(RetrievedDocumentModel model) {
        this.model = model;
    }

    public OMElement get() {
        OMElement response = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_response_qnamens);

        OMElement homeId = MetadataSupport.om_factory.createOMElement(MetadataSupport.home_community_id_qname);
        homeId.setText(model.getHome());
        response.addChild(homeId);

        OMElement repId = MetadataSupport.om_factory.createOMElement(MetadataSupport.repository_unique_id_qnamens);
        repId.setText(model.getRep_uid());
        response.addChild(repId);

        OMElement docId = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_unique_id_qnamens);
        docId.setText(model.getDoc_uid());
        response.addChild(docId);

        OMElement mimeType = MetadataSupport.om_factory.createOMElement(MetadataSupport.mimetype_qnamens);
        mimeType.setText(model.getContent_type());
        response.addChild(mimeType);

        OMElement doc = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_qnamens);
        response.addChild(doc)

        BASE64Encoder e  = new BASE64Encoder()
        String base64 = e.encodeBuffer(model.contents)
        doc.setText(base64)


        // do not generate optimized format - let Axis2 do that
//        OMElement include = MetadataSupport.om_factory.createOMElement(MetadataSupport.xop_include_qnamens);
//        OMAttribute href = MetadataSupport.om_factory.createOMAttribute("href", null, "cid:" + model.getCid());
//        include.addAttribute(href);
//        doc.addChild(include);



        return response;
    }
}
