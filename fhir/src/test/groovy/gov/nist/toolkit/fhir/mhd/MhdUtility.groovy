package gov.nist.toolkit.fhir.mhd

import ca.uhn.fhir.context.FhirContext
import groovy.xml.MarkupBuilder
import org.hl7.fhir.dstu3.model.Binary
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.CodeableConcept
import org.hl7.fhir.dstu3.model.Coding
import org.hl7.fhir.dstu3.model.DocumentManifest
import org.hl7.fhir.dstu3.model.DocumentReference
import org.hl7.fhir.dstu3.model.Identifier
import org.hl7.fhir.dstu3.model.Patient
import org.hl7.fhir.dstu3.model.Practitioner
import org.hl7.fhir.dstu3.model.Resource
import org.hl7.fhir.instance.model.api.IBaseResource
import sun.net.ResourceManager

import java.text.SimpleDateFormat

/**
 *
 */

// TODO - add hash
// TODO - add languageCode
// TODO - add legalAuthenticator
// TODO - add sourcePatientInfo
// TODO - add referenceIdList
// TODO - add case where Patient not in bundle

class MhdUtility {
    FhirContext ctx = FhirContext.forDstu3()

    int newIdCounter = 1


    ResourceMgr rMgr = new ResourceMgr()

    def clear() {
        rMgr = new ResourceMgr()
    }

    def newId() { String.format("ID%02d", newIdCounter++) }

    static String translateDateTime(Date theDate) {
        // TODO - hour is not right - don't know why
        SimpleDateFormat isoFormat = new SimpleDateFormat('yyyyMMddHHmmssSSS')
//        isoFormat.setTimeZone(TimeZone.getTimeZone('America/New_York'))   // UTC
//        String nyTime = isoFormat.format(theDate)
//        println "NYC time is ${nyTime}"
        isoFormat.setTimeZone(TimeZone.getTimeZone('UTC'))   // UTC
        String utcTime = isoFormat.format(theDate)
        println "UTC time is ${utcTime}"
        utcTime = trimTrailingZeros(utcTime)
        return utcTime
    }

    static trimTrailingZeros(String input) {
        while (input.size() > 0 && input[input.size()-1] == '0') {
            input = input.substring(0, input.size()-1)
        }
        input
    }

    static Identifier getOfficial(List<Identifier> identifiers) {
        if (identifiers.size() ==1) return identifiers[0]
        return identifiers.find { it.getUse() == Identifier.IdentifierUse.OFFICIAL }
    }

    static boolean isUuidUrn(ref) {
        ref.startsWith('urn:uuid') || ref.startsWith('urn:oid')
    }

    static String rebase(String containingUrl, String referenceUrl) {
        baseUrlFromUrl(containingUrl) + '/' + referenceUrl
    }

    static String resourceTypeFromUrl(String fullUrl) {
        fullUrl.reverse().split('/')[1].reverse()
    }

    static String resourceIdFromUrl(String fullUrl) {
        fullUrl.reverse().split('/')[0].reverse()
    }

    static String relativeUrl(String fullUrl) {
        List<String> parts = fullUrl.split('/')
        [parts[parts.size() - 2], parts.size() - 1].join('/')
    }


    static String baseUrlFromUrl(String fullUrl) {
        if (fullUrl.startsWith('urn')) return fullUrl
        List<String> parts = fullUrl.split('/')
        parts.remove(parts.size() - 1)
        parts.remove(parts.size() - 1)
        parts.join('/')
    }

    static boolean isRelative( url) {
        url && !url.startsWith('http') && url.split('/').size() ==2
    }

    static boolean isAbsolute(url) {
        url.startsWith('http')
    }

    def hexChars = ('0'..'9') + ('a'..'f')
    boolean isUUID(String u) {
        if (u.startsWith('urn:uuid:')) return true
        int total = 0
        total += (0..7).sum { (hexChars.contains(u[it])) ? 0 : 1  }
        total += (9..12).sum { (hexChars.contains(u[it])) ? 0 : 1  }
        total += (14..17).sum { (hexChars.contains(u[it])) ? 0 : 1  }
        total += (19..22).sum { (hexChars.contains(u[it])) ? 0 : 1  }
        total += (24..35).sum { (hexChars.contains(u[it])) ? 0 : 1  }
        total += (u[8]) ? 0 : 1
        total += (u[13]) ? 0 : 1
        total += (u[18]) ? 0 : 1
        total += (u[23]) ? 0 : 1
        total == 0
    }

    static asUUID(String uuid) {
        if (uuid.startsWith('urn:uuid:')) return uuid
        return 'urn:uuid:' + uuid
    }

    static unURN(String uuid) {
        if (uuid.startsWith('urn:uuid:')) return uuid.substring(9)
        if (uuid.startsWith('urn:oid:')) return uuid.substring(8)
        return uuid
    }

    /**
     * resolveUrl fullUrl for referenceUrl
     * @param containingUrl - fullUrl of entry
     * @param referenceUrl - reference found within containing
     */

    def addName(builder, value) {
        builder.Name() {
            LocalizedString(value: "${value}")
        }
    }

    def addSlot(builder, name, values) {
        builder.Slot(name: name) {
            ValueList {
                values.each {
                    Value "${it}"
                }
            }
        }
    }

    def addExternalIdentifier(builder, scheme, value, id, registryObject, name) {
        builder.ExternalIdentifier(
                identificationScheme: scheme,
                value: "${value}",
                id: "${id}",
                objectType: 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier',
                registryObject: "${registryObject}") {
            Name() {
                LocalizedString(value: "${name}")
            }
        }
    }

    def addClassification(builder, scheme, id ,registryObject, value, codeScheme, displayName) {
        builder.Classification(
                classificationScheme: "${scheme}",
                id: "${id}",
                objectType: 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification',
                nodeRepresentation: "${value}",
                classifiedObject: "${registryObject}"
        ) {
            addName(builder, displayName)
            addSlot(builder, 'codingScheme', [codeScheme])
        }
    }


    def addExtrinsicObject(builder, fullUrl, dr) {
        String drId = asUUID(resourceIdFromUrl(fullUrl))
        assert dr.content
        assert dr.content[0]
        assert dr.content[0].attachment
        builder.ExtrinsicObject(
                id: drId,
                objectType:'urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1',
                mimeType: dr.content[0].attachment.contentType) {
            // 20130701231133
            if (dr.indexed)
                addSlot(builder, 'creationTime', [translateDateTime(dr.indexed)])

            if (dr.context?.period?.start)
                addSlot(builder, 'serviceStartTime', [translateDateTime(dr.context.period.start)])

            if (dr.context?.period?.end)
                addSlot(builder, 'serviceStopTime', [translateDateTime(dr.context.period.end)])

            if (dr.description)
                addName(builder, dr.description)

            if (dr.masterIdentifier?.value)
                addExternalIdentifier(builder, 'urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab', unURN(dr.masterIdentifier.value), newId(), drId, 'XDSDocumentEntry.uniqueId')

            if (dr.subject) {
                org.hl7.fhir.dstu3.model.Reference subject = dr.subject
                def ref1 = subject.getReference()
                def (url, ref) = rMgr.resolveReference(fullUrl, ref1)
                assert ref
                assert ref instanceof Patient

                Patient patient = (Patient) ref
                assert patient

                List<Identifier> identifiers = patient.getIdentifier()
                Identifier official = getOfficial(identifiers)
                assert official

                addExternalIdentifier(builder, 'urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427', official.value, newId(), drId, 'XDSDocumentEntry.patientId')
            }

            if (dr.type) {
                CodeableConcept cc = dr.type
                assert cc.coding
                Coding coding = cc.coding[0]
                addClassification(builder, 'urn:uuid:f0306f51-975f-434e-a61c-c59651d33983', newId(), drId, coding.code, coding.system, coding.display)
            }

            if (dr.class_) {
                CodeableConcept cc = dr.class_
                assert cc.coding
                Coding coding = cc.coding[0]
                addClassification(builder, 'urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a', newId(), drId, coding.code, coding.system, coding.display)
            }

            if (dr.content?.format) {
                List<Coding> codings = dr.content.format
                Coding coding = codings[0]
                addClassification(builder, 'urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d', newId(), drId, coding.code, coding.system, coding.display)
            }

            if (dr.context?.facilityType) {
                CodeableConcept cc = dr.context.facilityType
                if (cc.coding) {
                    Coding coding = cc.coding[0]
                    addClassification(builder, 'urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1', newId(), drId, coding.code, coding.system, coding.display)
                }
            }

            if (dr.context?.practiceSetting) {
                CodeableConcept cc = dr.context.practiceSetting
                assert cc.coding
                Coding coding = cc.coding[0]
                addClassification(builder, 'urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead', newId(), drId, coding.code, coding.system, coding.display)
            }

            if (dr.context?.event) {
                CodeableConcept cc = dr.format
                assert cc.coding
                cc.coding.each { Coding coding ->
                    addClassification(builder, 'urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4', newId(), drId, coding.code, coding.system, coding.display)
                }
            }
        }
    }

    static addSubmissionSet(builder, fullUrl, dm) {
        String dmId = asUUID(resourceIdFromUrl(fullUrl))
        builder.RegistryPackage(
                id: dmId,
                objectType: ''
        )
    }

    def loadBundle(IBaseResource bundle) {
        assert bundle
        assert bundle instanceof Bundle
        assert bundle.type == Bundle.BundleType.TRANSACTION

        bundle.getEntry().each { Bundle.BundleEntryComponent component ->
            if (component.hasResource()) {
                rMgr.addResource(component.fullUrl, component.getResource())
            }
        }
    }

    def translateBundle(def xml, IBaseResource bundle) {
        loadBundle(bundle)

        xml.RegistryObjectList(xmlns: 'urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0') {
            rMgr.getAllOfType('DocumentManifest').each { url, resource ->
                DocumentManifest dm = (DocumentManifest) resource
                addSubmissionSet(xml, dm.getId(), dm)
            }
            rMgr.getAllOfType('DocumentReference').each { url, resource ->
                DocumentReference dr = (DocumentReference) resource
                def (ref, binary) = rMgr.resolveReference(url, dr.content[0].attachment.url)
                assert binary instanceof Binary
                addExtrinsicObject(xml, dr.getId(), dr)
            }
        }
    }

    String translateBundle(Bundle bundle) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        translateBundle(xml, bundle)

        return writer.toString()
    }

}
