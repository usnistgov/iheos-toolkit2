package gov.nist.toolkit.registrymsg.repository;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import org.apache.axiom.om.OMElement;

/**
 *
 */
public class ProvideAndRegisterParser {
    ProvideAndRegisterModel model = new ProvideAndRegisterModel();
    OMElement ele;

    public ProvideAndRegisterParser(OMElement ele) {
        this.ele = ele;
    }

    public ProvideAndRegisterModel getModel() {
        parse();
        return model;
    }


    void parse() {
//        model.setDocumentEntryMimeType("image/jpeg");
//        model.setDocumentEntryFirstAuthorName("My Author");
        try {
//            String documentEntryFirstAuthorName = XmlUtil.getStringFromXPath(ele, "");
            String documentEntryClassCode       = XmlUtil.getStringFromXPath(ele, "//*[local-name()='ExtrinsicObject']/*[local-name()='Classification'][@classificationScheme='urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a']","nodeRepresentation");
            String documentEntryCreationTime    = XmlUtil.getStringFromXPath(ele, "//*[local-name()='ExtrinsicObject']/*[local-name()='Slot'][@name='creationTime']/*[local-name()='ValueList']/*[local-name()='Value']");
            String documentEntryFormatCode      = XmlUtil.getStringFromXPath(ele, "//*[local-name()='ExtrinsicObject']/*[local-name()='Classification'][@classificationScheme='urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d']","nodeRepresentation");
            String documentEntryPatientId       = XmlUtil.getStringFromXPath(ele, "//*[local-name()='ExtrinsicObject']/*[local-name()='ExternalIdentifier'][@identificationScheme='urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427']", "value");
            String documentEntrySourcePatientId = XmlUtil.getStringFromXPath(ele, "//*[local-name()='ExtrinsicObject']/*[local-name()='Slot'][@name='sourcePatientId']/*[local-name()='ValueList']/*[local-name()='Value']");
            String documentEntryTypeCode        = XmlUtil.getStringFromXPath(ele, "//*[local-name()='ExtrinsicObject']/*[local-name()='Classification'][@classificationScheme='urn:uuid:f0306f51-975f-434e-a61c-c59651d33983']","nodeRepresentation");
            String documentEntryMimeType = XmlUtil.getStringFromXPath(ele, "//*[local-name()='ExtrinsicObject']", "mimeType");
            String documentEntryUniqueId = XmlUtil.getStringFromXPath(ele, "//*[local-name()='ExtrinsicObject']/*[local-name()='ExternalIdentifier'][@identificationScheme='urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab']", "value");
            model.setDocumentEntryClassCode(documentEntryClassCode);
            model.setDocumentEntryCreationTime(documentEntryCreationTime);
            model.setDocumentEntryFormatCode(documentEntryFormatCode);
            model.setDocumentEntryPatientId(documentEntryPatientId);
            model.setDocumentEntrySourcePatientId(documentEntrySourcePatientId);
            model.setDocumentEntryTypeCode(documentEntryTypeCode);
            model.setDocumentEntryMimeType(documentEntryMimeType);
            model.setDocumentEntryUniqueId(documentEntryUniqueId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
