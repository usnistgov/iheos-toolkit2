package gov.nist.toolkit.session.server

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.fhir.server.utility.FhirClient
import gov.nist.toolkit.session.shared.Message
import gov.nist.toolkit.session.shared.SubMessage
import gov.nist.toolkit.testengine.fhir.FhirSupport
import gov.nist.toolkit.utilities.message.MultipartFormatter
import groovy.json.JsonOutput
import org.hl7.fhir.dstu3.model.*
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.instance.model.api.IBaseResource
import org.xml.sax.SAXException

import javax.xml.parsers.ParserConfigurationException

class FhirMessageBuilder {
    Bundle bundle  = null;
    Resource resource = null;

    Message build(String name, IBaseResource resource) {
        FhirContext ctx = ToolkitFhirContext.get()
        String msgStr = ctx.newJsonParser().encodeResourceToString(resource)
        Message message = new Message().add('', '').add(name, formatMessage(msgStr))
        if (resource instanceof Bundle) {
            bundle = (Bundle) resource;
            for (Bundle.BundleEntryComponent c : bundle.getEntry()) {
                String fullUrl = c.getFullUrl();
                Resource theResource = c.getResource();
                if (theResource) {
                    String str = ctx.newJsonParser().encodeResourceToString(theResource);
                    SubMessage subMessage = new SubMessage(theResource.fhirType() + ": " + fullUrl, formatMessage(str));
                    message.addSubMessage(subMessage);
                    subMessage.addSubMessages(extractReferences(theResource));
                }
                if (c.response?.location) {
                    SubMessage subMessage = new SubMessage(c.response.location, c.response.status)
                    message.addSubMessage(subMessage)
                }
            }
        }
        return message
    }

    private IBaseResource findInBundle(String ref) {
        if (!ref)
            return null;
        for (Bundle.BundleEntryComponent c : bundle.getEntry()) {
            if (ref == c.fullUrl) {
                return c.getResource()
            }
        }
        return null
    }

    private IBaseResource findContained(String ref) {
        if (!ref)
            return null
        if (resource instanceof DocumentReference) {
            DocumentReference dr = resource
            return dr.contained.find { Resource con ->
                con.id == ref
            }
        }
        if (resource instanceof DocumentManifest) {
            DocumentManifest dm = resource
            return dm.contained.find { Resource con ->
                con.id == ref
            }
        }
        return null
    }


    private List<SubMessage> extractReferences(Resource resource) throws FHIRException {
        this.resource = resource
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
        if (ref != null && !ref.equals("")) {
            String refType = 'Ref'
            String value = "";
            try {
                IBaseResource refres
                refres = findInBundle(ref)
                refType = 'Bundle'
                if (!refres) {
                    refres = findContained(ref)
                    refType = 'Contained'
                }
                if (!refres) {
                    refres = FhirClient.readResource(ref);
                    refType = 'External'
                }
                value = FhirSupport.format(refres);
            } catch (Exception e) {
                value = "Unreadable";
            }
            subMessages.add(new SubMessage(type + " (${refType}) " + ref, value));
        }
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
