package gov.nist.toolkit.fhir.simulators.mhd

import gov.nist.toolkit.fhir.simulators.proxy.util.SimProxyBase
import gov.nist.toolkit.fhir.utility.IFhirSearch
import gov.nist.toolkit.registrymetadata.client.DocumentEntry
import org.apache.log4j.Logger
import org.hl7.fhir.dstu3.model.*
import org.hl7.fhir.instance.model.api.IBaseResource

class MetadataToDocumentReferenceTranslator {
    List<String> supportServerBases  // FHIR servers to hunt for Patient references
    IFhirSearch searcher
    SimProxyBase base
    static private final Logger logger = Logger.getLogger(MetadataToDocumentReferenceTranslator.class);

    MetadataToDocumentReferenceTranslator(SimProxyBase base, List<String> supportServerBases, IFhirSearch searcher) {
        assert supportServerBases
        assert supportServerBases.size() > 0
        assert searcher
        this.supportServerBases = supportServerBases
        this.searcher = searcher
        this.base = base
        if (base)
            logger.info("MetadataToDocumentReferenceTranslator: base is ${base.endpoint.baseAddress}")
    }

    DocumentReference run(DocumentEntry de) {
        DocumentReference dr = new DocumentReference()

        // TODO - legalAuth
        // TODO - sourcePatientId
        // TODO - authors
        // TODO sourcePatientInfo
        // TODO reference to binary

        dr.id = stripUrnPrefix(de.id)
        dr.addContent(new DocumentReference.DocumentReferenceContentComponent())
        org.hl7.fhir.dstu3.model.Attachment attachment = dr.content.attachment[0]
        attachment.contentType = de.mimeType
        if (de.hash)
            attachment.hash = HashTranslator.toByteArray(de.hash)
        if (de.lang)
            attachment.language = de.lang
        if (de.size)
            attachment.size = Integer.parseInt(de.size)
        if (base)
            attachment.url = "${base.endpoint.baseAddress}/fhir/Binary/${de.uniqueId}"   // TODO having to add fhir is a big screwup - but only need when deployed to tomcat - under intellij the 'fhir' gets in the way
        dr.context = new DocumentReference.DocumentReferenceContextComponent()
        if (de.serviceStartTime || de.serviceStopTime) {
            dr.context.period = new Period()
            if (de.serviceStartTime)
                dr.context.period.setStart(DateTransform.dtmToDate(de.serviceStartTime))
            if (de.serviceStopTime)
                dr.context.period.setEnd(DateTransform.dtmToDate(de.serviceStopTime))
        }
        if (de.creationTime)
            dr.indexed = DateTransform.dtmToDate(de.creationTime)
        if (de.classCode?.size() > 0) {
            def (code, display, system) = de.classCode[0].split('\\^', 3)
            dr.class_ = new CodeableConcept().addCoding(new Coding(system, code, display))
        }
        if (de.confCodes?.size() > 0) {
            dr.securityLabel =
                de.confCodes.collect { String theCode ->
                    def (code, display, system) = theCode.split('\\^', 3)
                    new CodeableConcept().addCoding(new Coding(system, code, display))
                }
        }
        if (de.eventCodeList?.size() > 0) {
            dr.context.event =
                    de.eventCodeList.collect { String theCode ->
                        def (code, display, system) = theCode.split('\\^', 3)
                        new CodeableConcept().addCoding(new Coding(system, code, display))
                    }
        }
        if (de.formatCode?.size() > 0) {
            def (code, display, system) = de.formatCode[0].split('\\^', 3)
            dr.content[0].format = new Coding(system, code, display)
        }
        if (de.hcftc?.size() > 0) {
            def (code, display, system) = de.hcftc[0].split('\\^', 3)
            dr.context.facilityType = new CodeableConcept().addCoding(new Coding(system, code, display))
        }
        if (de.pracSetCode?.size() > 0) {
            def (code, display, system) = de.pracSetCode[0].split('\\^', 3)
            dr.context.practiceSetting = new CodeableConcept().addCoding(new Coding(system, code, display))
        }
        if (de.typeCode?.size() > 0) {
            def (code, display, system) = de.typeCode[0].split('\\^', 3)
            dr.type = new CodeableConcept().addCoding(new Coding(system, code, display))
        }
        if (de.status) {
            if (de.status.endsWith('Approved'))
                dr.setStatus(Enumerations.DocumentReferenceStatus.CURRENT)
            else if (de.status.endsWith('Deprecated'))
                dr.setStatus(Enumerations.DocumentReferenceStatus.SUPERSEDED)
        }
        if (de.title) {
            dr.description = de.title
        }
        if (de.comments) {
            dr.content[0].attachment.title = de.comments
        }
        if (de.patientId) {
            def (value, String rest) = de.patientId.split('\\^\\^\\^', 2)
            if (rest) {
                def (x1, aa, iso) = rest.split('\\&', 3)
                def system = "urn:oid:${aa}"
                // lookup Patient Resource in FHIR Support server based on system|value
                def params = ["identifier=${system}|${value}"]
//                Map<String, IBaseResource> searchResults = searcher.search(supportServerBase, 'Patient', params)
//                if (searchResults.size() == 0)
//                    throw new Exception("Did not find Patient identiied by ${params} on server ${supportServerBase}")
//                if (searchResults.size() > 1)
//                    throw new Exception("Search for Patient identiied by ${params} on server ${supportServerBase} returned ${searchResults.size()} Patients")
//                IBaseResource theResource = searchResults.values()[0]

                def (fullUrl, theResource) = find(supportServerBases, 'Patient', params)
//                theResource = null
                if (!(theResource instanceof Patient))
                    throw new Exception("Trying to find Patient resource to match returned XDS Patient ID. Search for Patient identiied by ${params} on servers ${supportServerBases} returned Resource of type ${theResource.getClass().simpleName} instead of Patient")


                dr.subject = new Reference(fullUrl.toString())
            } else
                throw new Exception("Patient ID ${de.patientId} is improperly formatted")
        }
        if (de.uniqueId) {
            Identifier idr = new Identifier()
            idr.setSystem('urn:ietf:rfc:3986')
            idr.setId("urn:oid:${de.uniqueId}")
            dr.setMasterIdentifier(idr)
        }
        if (de.id) {
            Identifier idr = new Identifier()
            idr.setSystem('urn:ietf:rfc:3986')
            idr.setId("urn:uuid:${de.id}")
            dr.setIdentifier([idr])
        }

        return dr
    }

    // returns [URI, IBaseResource]
    def find(List bases, String resourceType, params) {
        Map<String, IBaseResource> searchResults
        for (String base : bases) {
            searchResults = searcher.search(base, resourceType, params)
            if (searchResults.size() != 0)
                return [searchResults.keySet()[0], searchResults.values()[0]]
        }
        throw new Exception("Did not find ${resourceType} identiied by ${params} on servers ${bases}")
    }

    static stripUrnPrefix(String id) {
        if (!id) return id
        if (id.startsWith('urn:uuid:')) return id.substring('urn:uuid:'.size())
        if (id.startsWith('urn:oid:')) return id.substring('urn:oid:'.size())
        return id
    }

}
