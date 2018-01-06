package gov.nist.toolkit.results

import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.registrymetadata.client.DocumentEntry
import gov.nist.toolkit.registrymetadata.client.MetadataCollection
import gov.nist.toolkit.registrymetadata.client.ResourceItem
import gov.nist.toolkit.registrymetadata.client.SubmissionSet
import gov.nist.toolkit.simcoresupport.mhd.MhdGenerator
import org.apache.log4j.Logger
import org.hl7.fhir.dstu3.model.Binary
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.DocumentManifest
import org.hl7.fhir.dstu3.model.DocumentReference
import org.hl7.fhir.instance.model.api.IBaseResource

class ResourceToMetadataCollectionParser {
    static private final Logger logger = Logger.getLogger(ResourceToMetadataCollectionParser.class);
    MetadataCollection col = null
    boolean translateCodes = false


    def add(IBaseResource res, fullUrl) {
        if (res instanceof DocumentReference) {
            initCollection()
//            DocumentEntry de = new DocumentEntry()
//            parse(de, res)
//            col.docEntries.add(de)
            DocumentEntry de = null // = new DocumentEntry()
            de = parse(res, fullUrl)
            if (de!=null)
                col.docEntries.add(de)
        } else if (res instanceof DocumentManifest) {
            initCollection()
            SubmissionSet ss = new SubmissionSet()
            parse(ss, res)
            col.submissionSets.add(ss)
        } else if (res instanceof Bundle) {
            res.entry.each { Bundle.BundleEntryComponent comp ->
                add(comp.getResource(), comp.fullUrl)
            }
        } else {
            // not a resource we have a GWT model for - add the JSON
            initCollection()
//            String json = ToolkitFhirContext.get().newJsonParser().encodeResourceToString(res)
            String json = ToolkitFhirContext.get().newJsonParser().setPrettyPrint(true).encodeResourceToString(res);
            String htmljson = formatJson(json)
            ResourceItem item = new ResourceItem(res.class.simpleName, json, htmljson)
            item.id = res.id
            col.resources.add(item)
        }
    }

    def initCollection() {
        if (!col)
            col = new MetadataCollection()
        col.isFhir = true;
    }

    /**
     * translate the DocumentReference to the DocumentEntry
     * @param de
     * @param dr
     */
    DocumentEntry parse(DocumentReference dr, String fullUrl) {
        // use MhdGenerator.buildSubmission at line 582
        // where it calls addExtrinsicObject
        if (dr instanceof DocumentReference) {
            DocumentEntry de = new DocumentEntry()
            de.isFhir = true
            translateExtrinsicObject(de, fullUrl, dr)
            return de
//            def writer = new StringWriter()
//            def xml = new MarkupBuilder(writer)
//
//            xml.RegistryObjectList(xmlns: 'urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0') {
//                new MhdGenerator(new SimProxyBase(), new ResourceCacheMgr(null))
//                        .setTranslateCodes(translateCodes)
//                        .setTranslateForDisplay(true)
//                        .addExtrinsicObject(xml, dr.getId(), dr)
//            }
//            Metadata m = MetadataParser
//                    .parseNonSubmission(writer.toString())
//            if (m.getAllObjects().size() > 0) {
//                MetadataToMetadataCollectionParser mcp = new MetadataToMetadataCollectionParser(
//                        m, "stepName")
//                MetadataCollection mc = mcp.get()
//                mc.setAllIsFhir(true);
//
//                if (mc.docEntries.size() > 0)
//                    return mc.docEntries.get(0)
//            }
        }
        return null
    }

    /**
     * TODO this is a duplicate of the translation in MhdGenerator - they need to be merged into one
     * @param de
     * @param fullUrl
     * @param dr
     * @return
     */
    def translateExtrinsicObject(DocumentEntry de,  fullUrl, DocumentReference dr) {
        boolean translateForDisplay = true
        String json = ToolkitFhirContext.get().newJsonParser().setPrettyPrint(true).encodeResourceToString(dr);
        json = formatJson(json);
        MhdGenerator g = new MhdGenerator(null, null)

        try {
//        if (fullUrl && (fullUrl instanceof String))
//            fullUrl = UriBuilder.build(fullUrl)
            // 20130701231133
            if (dr.indexed) {
                de.creationTime = g.translateDateTime(dr.indexed)
                de.creationTimeX = json
            }

            de.fullUrl = fullUrl

            de.status = dr.status
            de.statusX = json

            de.id = dr.identifierFirstRep?.id
            de.idX = json

            if (dr.context?.period?.start) {
                de.serviceStartTime = g.translateDateTime(dr.context.period.start)
                de.serviceStartTimeX = json
            }

            if (dr.context?.period?.end) {
                de.serviceStopTime = g.translateDateTime(dr.context.period.end)
                de.serviceStopTimeX = json
            }

            if (dr.content?.attachment?.language) {
                de.lang = dr.content.attachment.language
                de.langX = json
            }

            if (dr.content?.attachment?.url && translateForDisplay) {
                de.repositoryUniqueId = dr.content.attachment.url
                de.repositoryUniqueIdX = json
            }

            if (dr.content?.attachment?.get(0)?.contentType) {
                de.mimeType = dr.content?.attachment?.get(0)?.contentType
                de.mimeTypeX = json
            }

            if (dr.content?.attachment?.get(0)?.size) {
                de.size = dr.content?.attachment?.get(0)?.size
                de.sizeX = json
            }

            if (dr.content?.attachment?.get(0)?.hash) {
                de.hash = dr.content?.attachment?.get(0)?.hash
                de.hashX = json
            }

            if (dr.context?.sourcePatientInfo) {
                de.sourcePatientId = dr.context.sourcePatientInfo.reference
                de.sourcePatientIdX = json
            }

            if (dr.description) {
                de.title = dr.description
                de.titleX = json
            }

            if (dr.type?.hasCoding()) {
                def codes = dr.type.coding.collect {
                    displayValue(it) as String
                }
                de.typeCode = codes
                de.typeCodeX = [json]
            }

            if (dr.class_?.hasCoding()) {
                def codes = dr.class_.coding.collect {
                    displayValue(it) as String
                }
                de.classCode = codes
                de.classCodeX = [json]
            }

            if (dr.securityLabel) {
                def codes = dr.securityLabel.coding.collect {
                    displayValue(it) as String
                }
                de.confCodes = codes
                de.confCodesX = [json]
            }

            if (dr.content?.format) {
                def codes = [displayValue(dr.content?.format?.get(0)) as String]
                de.formatCode = codes
                de.formatCodeX = [json]
            }

            if (dr.context?.facilityType?.hasCoding()) {
                def codes = dr.context?.facilityType?.coding?.collect {
                    displayValue(it) as String
                }
                de.hcftc = codes
                de.hcftcX = [json]
            }

            if (dr.context?.practiceSetting?.hasCoding()) {
                def codes = dr.context?.practiceSetting?.coding?.collect {
                    displayValue(it) as String
                }
                de.pracSetCode = codes
                de.pracSetCodeX = [json]
            }

            if (!dr.context?.event?.empty && dr.context.event.get(0)?.hasCoding()) {
                def codes = dr.context?.event?.coding?.collect {
                    displayValue(it) as String
                }
                de.eventCodeList = codes
                de.eventCodeListX = [json]
            }

            if (dr.masterIdentifier) {
                de.uniqueId = dr.masterIdentifier.value
//                de.uniqueId = "${dr.masterIdentifier.system.toString()}|${dr.masterIdentifier.id.toString()}"
                de.uniqueIdX = json
            }

            if (dr.subject?.hasReference()) {
                de.patientId = dr.subject.reference
                de.patientIdX = json
            }

        } catch (Throwable e) {
            logger.error(e.getMessage())
            throw new Exception("Building of display format (DocumentReference) failed", e)
        }
    }

    static displayValue(def it) {
        def val = it.display
        if (!val || val == 'null')
            val = "${it.system}|${it.code}"
        val
    }

    String formatJson(String json) {
        json = json.replaceAll(" ", "&nbsp;");
        json = json.replaceAll("<", "&lt;");
        json = json.replaceAll("\\n", "<br />");
        return json;
    }


    /**
     * translate the DocmentReference to the SubmissionSet
     * @param ss
     * @param dm
     */
    def parse(SubmissionSet ss, DocumentManifest dm) {
        // use MhdGenerator.buildSubmission at line 560
        // where it calls addSubmissionSet
    }

    /**
     * fill in details from Binary
     * @param de
     * @param b
     */
    def parse(DocumentEntry de, Binary b) {
        // some details at MhdGenerator.buildSubmission at line 572
    }

    public MetadataCollection get() { return col; }

}
