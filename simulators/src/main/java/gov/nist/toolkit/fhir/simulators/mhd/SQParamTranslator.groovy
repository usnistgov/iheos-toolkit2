package gov.nist.toolkit.fhir.simulators.mhd
/**
 * generate SQ parameters from MHD query spec.  Inputs and outputs are in a Map-based model.
 */
class SQParamTranslator {
    // SQ parameters
    static statusKey = '$XDSDocumentEntryStatus'
    static patientIdKey = '$XDSDocumentEntryPatientId'
    static creationFromKey = '$XDSDocumentEntryCreationTimeFrom'
    static creationToKey = '$XDSDocumentEntryCreationTimeTo'
    static classKey = '$XDSDocumentEntryClassCode'
    static typeKey = '$XDSDocumentEntryTypeCode'
    static settingKey = '$XDSDocumentEntryPracticeSettingCode'
    static serviceStartFromKey = '$XDSDocumentEntryServiceStartTimeFrom'
    static serviceStartToKey = '$XDSDocumentEntryServiceStartTimeTo'
    static serviceStopFromKey = '$XDSDocumentEntryServiceStopTimeFrom'
    static serviceStopToKey = '$XDSDocumentEntryServiceStopTimeTo'
    static facilityKey = '$XDSDocumentEntryHealthcareFacilityTypeCode'
    static eventKey = '$XDSDocumentEntryEventCodeList'
    static confKey = '$XDSDocumentEntryConfidentialityCode'
    static formatKey = '$XDSDocumentEntryFormatCode'
    static relatedKey = '$XDSDocumentEntryReferenceIdList'
    static authorKey = '$XDSDocumentEntryAuthorPerson'
    static String queryType = 'QueryType'

    // coded types
    static codedTypes = [
            classKey,
            typeKey,
            settingKey,
            facilityKey,
            eventKey,
            confKey,
            formatKey,
            relatedKey
    ]

    static acceptsMultiple = [
            classKey,
            typeKey,
            settingKey,
            facilityKey,
            eventKey,
            confKey,
            authorKey,
            formatKey,
            statusKey
    ]

    // Query Types
    static FindDocsKey = 'urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d'
    static FindDocsByRefIdKey = 'urn:uuid:12941a89-e02e-4be5-967c-ce4bfc8fe492'

    // shows default query - may be upgraded to FindDocsByRefIdKey if related-id is used
    def result = ['QueryType':[FindDocsKey]]

    def addResult(key, value) {
        if (!(value instanceof List))
            value = [value]
        result[key] = value
    }

    // translation table between FHIR and XDS coding styles
    // TODO FHIR code/identifier matching is not case sensitive
    static codes = [
            // FHIR, XDS
            ['class=urn:class:system|class1', '$XDSDocumentEntryClassCode:class1^^1.2.3.6677'],
            ['related-id=urn:relatedid:system|relatedid1', '$XDSDocumentEntryReferenceIdList:relatedid1^^1.2.3.6677.7'],
            ['format=urn:format:system|format1', '$XDSDocumentEntryFormatCode:format1^^1.2.3.6677.6'],
            ['securityLabel=urn:securityLabel:system|securityLabel1', '$XDSDocumentEntryConfidentialityCode:securityLabel1^^1.2.3.6677.5'],
            ['event=urn:event:system|event1', '$XDSDocumentEntryEventCodeList:event1^^1.2.3.6677.4'],
            ['facility=urn:facility:system|facility1', '$XDSDocumentEntryHealthcareFacilityTypeCode:facility1^^1.2.3.6677.3'],
            ['setting=urn:setting:system|setting1', '$XDSDocumentEntryPracticeSettingCode:setting1^^1.2.3.6677.2'],
            ['type=urn:type:system|type1', '$XDSDocumentEntryTypeCode:type1^^1.2.3.6677.1']
    ]

    private class Code {
        def type
        def system
        def code
    }

    private class FhirCode extends Code {

        FhirCode(String encoded) {
            String coded
            (type, coded) = encoded.split('=', 2)
            (system, code) = coded.split('\\|', 2)
        }

        XdsCode findXds() {
            def coded = asCode()
            def entry = codes.find { it[0] == coded }
            if (entry)
                return new XdsCode(entry[1])
            return null
        }

        String asCode() { "${type}=${system}|${code}" }

        String toString() { asCode() }
    }

    private class XdsCode extends Code {

        XdsCode(String encoded) {
            String coded
            (type, coded) = encoded.split(':', 2)
            (code, system) = coded.split('\\^\\^', 2)
        }

        FhirCode findFhir() {
            def coded = asCode()
            def entry = codes.find { it[1] == coded }
            if (entry)
                return new FhirCode(entry[0])
            return null
        }

        String asCode() { "${type}:${code}^^${system}"}

        String toString() { asCode() }
    }

    static cannotTranslateFhir(param) {
        throw new Exception("Cannot translate code ${param} to XDS, no mapping defined for this code.")
    }

    Map run(List<String> params) {
        Map result = [:]
        params.each {
            def x = run(it )
            result << x
        }
        return result
    }

    Map run(String param) {

        def (String name, String value) = param.split('=', 2)

        switch (name) {
            case 'patient.identifier':
                def (String system, String code) = value.split('\\|', 2)
                if (system?.startsWith('urn:oid:'))
                    system = system.substring('urn:oid:'.size())
                addResult(patientIdKey, "${code}^^^&${system}&ISO")
                break;

            case 'status':
                if (value == 'current')
                    addResult(statusKey,'urn:oasis:names:tc:ebxml-regrep:StatusType:Approved')
                if (value == 'superseded')
                    addResult(statusKey, 'urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated')
                break;

            case 'indexed':
                def op = value.substring(0, 2)
                def date = value.substring(2)
                def dtm = DateTransform.fhirToDtm(date)
                switch (op) {
                    case 'eq':
                    case 'ap':  // approximate is in the eys of the beholder
                        addResult(creationFromKey, dtm)
                        addResult(creationToKey, dtm)
                        break;

                    case 'ne':
                        // TODO - cannot code this in SQ
                        break;

                    case 'lt':
                    case 'le':   // TODO - could be better
                    case 'eb': // same as le I think
                        addResult(creationToKey, dtm)
                        break;

                    case 'gt':
                    case 'ge':   // TODO - could be better
                    case 'sa': // I think this is right
                        addResult(creationFromKey, dtm)
                        break;
                    default:
                        throw new Exception("${param} cannot be translated into Stored Query parameters.")
                }
                break;

            case 'period':
                def op = value.substring(0, 2)
                def date = value.substring(2)
                def dtm = DateTransform.fhirToDtm(date)
                switch (op) {
                    case 'eq':
                    case 'ap':  // approximate is in the eye of the beholder
                        addResult(serviceStartFromKey, dtm)
                        addResult(serviceStopToKey, dtm)
                        break;

                    case 'ne':
                        // TODO - cannot code this in SQ
                        break;

                    case 'lt':
                    case 'le':   // TODO - could be better
                    case 'eb': // same as le I think
                        addResult(serviceStopToKey, dtm)
                        break;

                    case 'gt':
                    case 'ge':   // TODO - could be better
                    case 'sa': // I think this is right
                        addResult(serviceStartFromKey, dtm)
                        break;
                    default:
                        throw new Exception("${param} cannot be translated into Stored Query parameters.")
                }
                break

            case 'author.given':   // TODO - not implemented yet
                break

            case 'author.family':   // TODO - not implemented yet
                break

            case 'class':
                FhirCode fcode = new FhirCode(param)
                XdsCode xcode = fcode.findXds()
                if (xcode)
                    addResult(classKey, "${xcode.code}^^${xcode.system}")
                else
                    cannotTranslateFhir(param)
                break

            case 'type':
                FhirCode fcode = new FhirCode(param)
                XdsCode xcode = fcode.findXds()
                if (xcode)
                    addResult(typeKey, "${xcode.code}^^${xcode.system}")
                else
                    cannotTranslateFhir(param)
                break

            case 'setting':
                FhirCode fcode = new FhirCode(param)
                XdsCode xcode = fcode.findXds()
                if (xcode)
                    addResult(settingKey, "${xcode.code}^^${xcode.system}")
                else
                    cannotTranslateFhir(param)
                break

            case 'facility':
                FhirCode fcode = new FhirCode(param)
                XdsCode xcode = fcode.findXds()
                if (xcode)
                    addResult(facilityKey, "${xcode.code}^^${xcode.system}")
                else
                    cannotTranslateFhir(param)
                break

            case 'event':
                FhirCode fcode = new FhirCode(param)
                XdsCode xcode = fcode.findXds()
                if (xcode)
                    addResult(eventKey, "${xcode.code}^^${xcode.system}")
                else
                    cannotTranslateFhir(param)
                break

            case 'securityLabel':
                FhirCode fcode = new FhirCode(param)
                XdsCode xcode = fcode.findXds()
                if (xcode)
                    addResult(confKey, "${xcode.code}^^${xcode.system}")
                else
                    cannotTranslateFhir(param)
                break

            case 'format':
                FhirCode fcode = new FhirCode(param)
                XdsCode xcode = fcode.findXds()
                if (xcode)
                    addResult(formatKey, "${xcode.code}^^${xcode.system}")
                else
                    cannotTranslateFhir(param)
                break

            case 'related-id':
                FhirCode fcode = new FhirCode(param)
                XdsCode xcode = fcode.findXds()
                if (xcode) {
                    addResult(relatedKey, "${xcode.code}^^${xcode.system}")
                    addResult(queryType, FindDocsByRefIdKey)
                }
                else
                    cannotTranslateFhir(param)
                break

            default:
                throw new Exception("Query parameter ${name} cannot be translated into Stored Query parameters.")

        }
        return result
    }
}
