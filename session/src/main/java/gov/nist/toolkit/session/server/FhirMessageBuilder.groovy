package gov.nist.toolkit.session.server

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.session.shared.Message
import gov.nist.toolkit.session.shared.SubMessage
import gov.nist.toolkit.utilities.message.MultipartFormatter
import groovy.json.JsonOutput
import org.hl7.fhir.dstu3.model.*
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.instance.model.api.IBaseResource
import org.xml.sax.SAXException

import javax.xml.parsers.ParserConfigurationException

class FhirMessageBuilder {

    Message build(IBaseResource resource) {
        FhirContext ctx = ToolkitFhirContext.get()
        String msgStr = ctx.newJsonParser().encodeResourceToString(resource)
        Message message = new Message('', formatMessage(msgStr))
        if (resource instanceof Bundle) {
            Bundle bundle = (Bundle) resource;
            for (Bundle.BundleEntryComponent c : bundle.getEntry()) {
                String fullUrl = c.getFullUrl();
                Resource theResource = c.getResource();
                String str = ctx.newJsonParser().encodeResourceToString(theResource);
                SubMessage subMessage = new SubMessage(theResource.fhirType() + ": " + fullUrl, formatMessage(str));
                message.addSubMessage(subMessage);

                subMessage.addSubMessages(extractReferences(theResource));
            }
        }
        return message
    }

    private List<SubMessage> extractReferences(Resource resource) throws FHIRException {
        List<SubMessage> subMessages = new ArrayList<>();
        String type = resource.fhirType();
        switch (type) {
            case "DocumentManifest":
                DocumentManifest x = (DocumentManifest) resource;
                addReference(subMessages, "Subject", x.getSubject());
                addReference(subMessages, "Author", x.getAuthor());
                addReference(subMessages, "Recipient", x.getRecipient());
                for (DocumentManifest.DocumentManifestContentComponent comp: x.getContent()) {
                    addReference(subMessages, "Content", comp.getPReference());
                }
                break;
            case "DocumentReference":
                DocumentReference xdr = (DocumentReference) resource;
                addReference(subMessages, "Subject", xdr.getSubject());
                addReference(subMessages, "Author", xdr.getAuthor());
                addReference(subMessages, "Authenticator", xdr.getAuthenticator());
                addReference(subMessages, "Custodian", xdr.getCustodian());
                for (DocumentReference.DocumentReferenceRelatesToComponent dr : xdr.getRelatesTo()) {
                    addReference(subMessages, dr.getCode().getDisplay(), dr.getTarget());
                }
                addReference(subMessages, "Context/Encounter", xdr.getContext().getEncounter());
                break;
        }
        return subMessages;
    }

    private void addReference(List<SubMessage> subMessages, String type, List<Reference> references) {
        for (Reference reference : references)
            addReference(subMessages, type, reference);
    }

    private void addReference(List<SubMessage> subMessages, String type, Reference reference) {
        if (reference == null)
            return;
        String ref = reference.getReference();
        if (ref != null && !ref.equals(""))
            subMessages.add(new SubMessage(type + " " + ref, ""));
//		Identifier id = reference.getIdentifier();
//		if (id != null)
//			subMessages.add(new SubMessage("Id: " + id.toString(),""));
    }

    static String formatMessage(String message) throws IOException, SAXException, ParserConfigurationException {
        String trimBody = message.trim();
        boolean isJson = trimBody.startsWith("{");
        boolean isXml = trimBody.startsWith("<");
        boolean isMultipart = trimBody.startsWith("--");
        if (isJson) {
            // format json but leave embedded HTML alone
            message = JsonOutput.prettyPrint(message);
        } else if (isXml) {
            message = formatXml(message);
        } else if (isMultipart) {
            message = MultipartFormatter.format(message);
        }
        return message;
    }

    private static String formatXml(String xml) throws ParserConfigurationException, SAXException, IOException {
        StringWriter xmlOutput = new StringWriter();
        XmlNodePrinter printer = new XmlNodePrinter(new PrintWriter(xmlOutput));
        printer.print(new XmlParser().parseText(xml));
        xml = xmlOutput.toString();
        return xml;
    }


}
