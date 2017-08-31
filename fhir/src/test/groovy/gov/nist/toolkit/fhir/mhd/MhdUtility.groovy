package gov.nist.toolkit.fhir.mhd

import org.hl7.fhir.dstu3.model.Binary
import org.hl7.fhir.dstu3.model.DocumentReference
import org.hl7.fhir.dstu3.model.Identifier
import org.hl7.fhir.dstu3.model.Patient
import org.hl7.fhir.dstu3.model.Practitioner

import java.text.SimpleDateFormat

/**
 *
 */
class MhdUtility {
    int newIdCounter = 1

    Map<String, DocumentReference> docRefs = [:]   // index is fullUrl
    Map<String, Binary> binaries = [:]
    Map<String, Patient> patients = [:]
    Map<String, Practitioner> practitioners = [:]

    def newId() { String.format("ID%02d", newIdCounter++) }

    String translateDateTime(Date theDate) {
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

    def trimTrailingZeros(String input) {
        while (input.size() > 0 && input[input.size()-1] == '0') {
            input = input.substring(0, input.size()-1)
        }
        input
    }

    Identifier getOfficial(List<Identifier> identifiers) {
        if (identifiers.size() ==1) return identifiers[0]
        return identifiers.find { it.getUse() == Identifier.IdentifierUse.OFFICIAL }
    }

    String resourceTypeFromUrl(String fullUrl) {
        fullUrl.reverse().split('/')[1].reverse()
    }

    String resourceIdFromUrl(String fullUrl) {
        fullUrl.reverse().split('/')[0].reverse()
    }

    String relativeUrl(String fullUrl) {
        List<String> parts = fullUrl.split('/')
        [parts[parts.size() - 2], parts.size() - 1].join('/')
    }

    String baseUrlFromUrl(String fullUrl) {
        List<String> parts = fullUrl.split('/')
        parts.remove(parts.size() - 1)
        parts.remove(parts.size() - 1)
        parts.join('/')
    }

    boolean isRelative(String url) {
        !url.startsWith('http')
    }

    String buildFullUrl(String baseUrl, theUrl) {
        if (!isRelative(theUrl)) return theUrl
        return baseUrl + '/' + theUrl
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

    def 'is uuid test'() {
        when:
        def value = '3fdc72f4-a11d-4a9d-9260-a9f745779e1d'

        then:
        value[0] == '3'
        hexChars.contains('7')
        isUUID(value)
    }

    def asUUID(String uuid) {
        if (uuid.startsWith('urn:uuid:')) return uuid
        return 'urn:uuid:' + uuid
    }

    def unURN(String uuid) {
        if (uuid.startsWith('urn:uuid:')) return uuid.substring(9)
        if (uuid.startsWith('urn:oid:')) return uuid.substring(8)
        return uuid
    }

    def addTitle(builder, value) {
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

    def addExtrinsicObject(builder, fullUrl, dr, bin) {
        String drId = asUUID(resourceIdFromUrl(fullUrl))
        builder.ExtrinsicObject(
                id: drId,
                objectType:'urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1',
                mimeType: bin.contentType) {
            // 20130701231133
            if (dr.indexed)
                addSlot(builder, 'creationTime', [translateDateTime(dr.indexed)])

            if (dr.context?.period?.start)
                addSlot(builder, 'serviceStartTime', [translateDateTime(dr.context.period.start)])

            if (dr.context?.period?.end)
                addSlot(builder, 'serviceStopTime', [translateDateTime(dr.context.period.end)])

            if (dr.description)
                addTitle(builder, dr.description)

            if (dr.masterIdentifier?.value)
                addExternalIdentifier(builder, 'urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab', unURN(dr.masterIdentifier.value), newId(), drId, 'XDSDocumentEntry.uniqueId')

            if (dr.subject) {
                org.hl7.fhir.dstu3.model.Reference subject = dr.subject
                String ref = subject.getReference()
                ref = buildFullUrl(baseUrlFromUrl(fullUrl), ref)
                assert ref

                Patient patient = patients[ref]
                assert patient

                List<Identifier> identifiers = patient.getIdentifier()
                Identifier official = getOfficial(identifiers)
                assert official

                addExternalIdentifier(builder, 'urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427', official.value, newId(), drId, 'XDSDocumentEntry.patientId')
            }
        }
    }

}
