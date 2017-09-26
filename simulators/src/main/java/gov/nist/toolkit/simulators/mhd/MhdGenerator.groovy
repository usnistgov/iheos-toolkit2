package gov.nist.toolkit.simulators.mhd

import gov.nist.toolkit.simulators.fhir.validators.BundleFullUrlValidator
import gov.nist.toolkit.simulators.mhd.errors.ResourceNotAvailable
import gov.nist.toolkit.installation.ResourceCacheMgr
import gov.nist.toolkit.installation.ResourceMgr
import gov.nist.toolkit.simulators.proxy.util.SimProxyBase
import groovy.xml.MarkupBuilder
import org.hl7.fhir.dstu3.model.*
import org.hl7.fhir.dstu3.model.codesystems.DocumentReferenceStatus
import org.hl7.fhir.instance.model.api.IBaseResource

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

/**
 * Association id = ID06
 *    source = SubmissionSet_ID02
 *    target = Document_ID01
 *
 * RegistryPackage id = 234...
 *
 * ExtrinsicObject id = ID07
 */

class MhdGenerator {
    ErrorLogger errorLogger = new ErrorLogger()
    int newIdCounter = 1
    ResourceCacheMgr resourceCacheMgr
    SimProxyBase proxyBase

    ResourceMgr rMgr = new ResourceMgr()

    MhdGenerator(SimProxyBase proxyBase, ResourceCacheMgr resourceCacheMgr1) {
        this.proxyBase = proxyBase
        resourceCacheMgr = resourceCacheMgr1
    }

    def clear() {
        rMgr = new ResourceMgr()
        errorLogger = new ErrorLogger()
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
        if (utcTime.size() > 14)
            utcTime = utcTime.substring(0, 14)
        println "UTC time is ${utcTime}"
        //utcTime = trimTrailingZeros(utcTime)
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


    def hexChars = ('0'..'9') + ('a'..'f')
    boolean isUUID(String u) {
        if (u.startsWith('urn:uuid:')) return true
        try {
            int total = 0
            total += (0..7).sum { (hexChars.contains(u[it])) ? 0 : 1 }
            total += (9..12).sum { (hexChars.contains(u[it])) ? 0 : 1 }
            total += (14..17).sum { (hexChars.contains(u[it])) ? 0 : 1 }
            total += (19..22).sum { (hexChars.contains(u[it])) ? 0 : 1 }
            total += (24..35).sum { (hexChars.contains(u[it])) ? 0 : 1 }
            total += (u[8]) ? 0 : 1
            total += (u[13]) ? 0 : 1
            total += (u[18]) ? 0 : 1
            total += (u[23]) ? 0 : 1
            return total == 0
        } catch (Exception e) {
            return false
        }
    }

    static asUUID(String uuid) {
        if (uuid.startsWith('urn:uuid:')) return uuid
        return 'urn:uuid:' + uuid
    }

    static unURN(def uuid) {
        assert uuid instanceof String
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

    def addClassification(builder, node, id, classifiedObject) {
        builder.Classification(
                classifiedObject: "${classifiedObject}",
                classificationNode: "${node}",
                id: "${id}",
                objectType: 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification')
    }

    /**
     * add external classification (see ebRIM for definition)
     * @param builder
     * @param scheme
     * @param id
     * @param registryObject
     * @param value
     * @param codeScheme
     * @param displayName
     * @return
     */
    def addClassification(builder, scheme, id ,registryObject, value, codeScheme, displayName) {
        builder.Classification(
                classificationScheme: "${scheme}",
                id: "${id}",
                objectType: 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification',
                nodeRepresentation: "${value}",
                classifiedObject: "${registryObject}"
        ) {
            addSlot(builder, 'codingScheme', [codeScheme])
            addName(builder, displayName)
        }
    }

    def getEntryUuidValue(fullUrl, List<Identifier> identifier) {
        if (fullUrl && ResourceMgr.isAbsolute(fullUrl))  {
            def id = ResourceMgr.resourceIdFromUrl(fullUrl)
            if (isUUID(id)) {
                return asUUID(id)
            }
        }
        if (identifier) {
            Identifier officialEntryUuidId = getOfficial(identifier)
            if (officialEntryUuidId) return officialEntryUuidId.value
        }
        if (fullUrl && isUuidUrn(fullUrl)) return fullUrl
        return newId()
    }

    static getStatus(obj) {
        (obj?.status == DocumentReferenceStatus.SUPERSEDED) ? 'urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated' : 'urn:oasis:names:tc:ebxml-regrep:StatusType:Approved'
    }

    /**
     * see Appendix Z section Z.9.1.2
     * @param patient
     * @return
     */
    static cxiFromPatient(patient) {
        assert patient instanceof Patient
        List<Identifier> identifiers = patient.getIdentifier()
        Identifier identifier = getOfficial(identifiers)
        assert identifier
        def cxi_1 = identifier.value
        def cxi_4 = identifier.system
        List<Coding> codings = identifier.type.coding
        Coding theCoding = codings.find {it.system == 'urn:ietf:rfc:3986'}
        assert theCoding
        def cxi_5 = theCoding.code

        def val = cxi_1 + '^^^&' + unURN(cxi_4) + '&ISO'+ ( (cxi_5) ? "^${cxi_5}" : '')
        return val
    }

    // TODO - no profile guidance on how to convert coding.system URL to existing OIDs

    def addClassificationFromCodeableConcept(builder, CodeableConcept cc, scheme, classifiedObjectId) {
        assert cc
        Coding coding = cc.coding[0]
        if (coding)
            addClassificationFromCoding(builder, coding, scheme, classifiedObjectId)
    }

    def addClassificationFromCoding(builder, Coding coding, scheme, classifiedObjectId) {
        assert coding
        addClassification(builder, scheme, newId(), classifiedObjectId, coding.code, coding.system, coding.display)
    }

    def addDocument(builder, drId, contentId) {
        builder.Document(id:drId, xmlns: 'urn:ihe:iti:xds-b:2007') {
            Include(href: "cid:${contentId}", xmlns: 'http://www.w3.org/2004/08/xop/include')
        }
    }

    def addExtrinsicObject(builder, fullUrl, dr) {
        String drId = dr.id // getEntryUuidValue(fullUrl, dr.identifier)
        assert dr.content
        assert dr.content[0]
        assert dr.content[0].attachment
        builder.ExtrinsicObject(
                id: drId,
                objectType:'urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1',
                mimeType: dr.content[0].attachment.contentType,
                status: getStatus(dr)) {
            // 20130701231133
            if (dr.indexed)
                addSlot(builder, 'creationTime', [translateDateTime(dr.indexed)])

            if (dr.context?.period?.start)
                addSlot(builder, 'serviceStartTime', [translateDateTime(dr.context.period.start)])

            if (dr.context?.period?.end)
                addSlot(builder, 'serviceStopTime', [translateDateTime(dr.context.period.end)])

            if (dr.content?.attachment?.language)
                addSlot(builder, 'languageCode', dr.content.attachment.language)

            if (dr.subject)
                addSourcePatient(builder, fullUrl, drId, 'urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427', dr.subject, 'XDSDocumentEntry.patientId')

            if (dr.description)
                addName(builder, dr.description)

            if (dr.type)
                addClassificationFromCodeableConcept(builder, dr.type, 'urn:uuid:f0306f51-975f-434e-a61c-c59651d33983', drId)

            if (dr.class_)
                addClassificationFromCodeableConcept(builder, dr.class_, 'urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a', drId)

            if (dr.securityLabel?.coding)
                addClassificationFromCoding(builder, dr.securityLabel[0].coding[0], 'urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f', drId)

            if (dr.content?.format) {
                addClassificationFromCoding(builder, dr.content[0].format, 'urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d', drId)
            }

            if (dr.context?.facilityType)
                addClassificationFromCodeableConcept(builder, dr.context.facilityType, 'urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1', drId)

            if (dr.context?.practiceSetting)
                addClassificationFromCodeableConcept(builder, dr.context.practiceSetting, 'urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead', drId)

            if (dr.context?.event)
                addClassificationFromCodeableConcept(builder, dr.context.event, 'urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4', drId)

            if (dr.masterIdentifier?.value)
                addExternalIdentifier(builder, 'urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab', unURN(dr.masterIdentifier.value), newId(), drId, 'XDSDocumentEntry.uniqueId')

            if (dr.subject)
                addSubject(builder, fullUrl, drId, 'urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427', dr.subject, 'XDSDocumentEntry.patientId')


        }
    }

    /**
     * Patient resources shall not be in the bundle so don't look there.  Must have fullUrl reference
     * @param builder
     * @param fullUrl
     * @param containingObjectId
     * @param scheme
     * @param subject
     * @param attName
     * @return
     */

    // TODO this is supposed to be constrained to be 'contained'
    def addSourcePatient(builder, fullUrl, containingObjectId, scheme,  org.hl7.fhir.dstu3.model.Reference subject, attName) {
        def ref1 = subject.getReference()
        def (url, ref) = resolveReference(fullUrl, ref1, false, true)
        if (!ref) {
            new ResourceNotAvailable(errorLogger, fullUrl, ref1, 'All DocumentReference.subject and DocumentManifest.subject values shall be\nReferences to FHIR Patient Resources identified by an absolute external reference (URL).', '3.65.4.1.2.2 Patient Identity')
            return
        }
        assert ref instanceof Patient

        Patient patient = (Patient) ref

        List<Identifier> identifiers = patient.getIdentifier()
        Identifier official = getOfficial(identifiers)
        assert official, 'Appendix Z, section E.3 Identifier Type'

        assert official.value, 'Error: Patient Resource identifier value is null. Appendix E, section E.3 Identifier Type'
        assert official.system, 'Error: Patient Resource identifier system is null. See Appendix E, section E.3 Identifier Type'

        String value = official.value
        String system = official.system
        String oid = unURN(system)
        def pid = "${value}^^^&${oid}&ISO"

        addSlot(builder, 'sourcePatientId', [pid])
    }

    // TODO must be absolute reference
    def addSubject(builder, fullUrl, containingObjectId, scheme,  org.hl7.fhir.dstu3.model.Reference subject, attName) {
        def ref1 = subject.getReference()
        def (url, ref) = resolveReference(fullUrl, ref1, false, true)
        if (!ref) {
            new ResourceNotAvailable(errorLogger, fullUrl, ref1, 'All DocumentReference.subject and DocumentManifest.subject values shall be\nReferences to FHIR Patient Resources identified by an absolute external reference (URL).', '3.65.4.1.2.2 Patient Identity')
            return
        }
        assert ref instanceof Patient

        Patient patient = (Patient) ref

        List<Identifier> identifiers = patient.getIdentifier()
        Identifier official = getOfficial(identifiers)
        assert official, 'Appendix Z, section E.3 Identifier Type'

        assert official.value, 'Appendix Z, section E.3 Identifier Type'
        assert official.system, 'Appendix Z, section E.3 Identifier Type'

        String value = official.value
        String system = official.system
        String oid = unURN(system)
        def pid = "${value}^^^&${oid}&ISO"

        addExternalIdentifier(builder, scheme, pid, newId(), containingObjectId, attName)
    }



    /**
     *
     * @param fullUrl
     * @param reference
     * @return [url, Resource]
     */
    def resolveReference(fullUrl, reference, relativeReferenceOk, relativeReferenceRequired) {
        def (url, ref) = rMgr.resolveReference(fullUrl, reference, relativeReferenceOk, relativeReferenceRequired)
        if (!ref) {
            // not available in bundle - try local cache next
            if (ResourceMgr.isAbsolute(reference)) {
                ref = resourceCacheMgr.getResource(reference)
            }
        }
        return [url, ref]
    }

    def resolveId(id) {
        return rMgr.resolveId(id)
    }

    def resolveReference(fullUrl, reference) {
        return resolveReference(fullUrl, reference, true, false)
    }

    def buildXonXcnXtn(resource) {
        if (resource instanceof Practitioner) {

        }
    }

    def addSubmissionSet(builder, fullUrl, dm) {
        String dmId = dm.id // getEntryUuidValue(fullUrl, dm.identifier)
        builder.RegistryPackage(
                id: dmId,
                objectType: 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:RegistryPackage',
                status: 'urn:oasis:names:tc:ebxml-regrep:StatusType:Approved') {

//            if (dm.recipient) {
//                List<Reference> refs = dm.recipient
//                def values = []
//                refs.each {  r ->
//                    String reference = r.reference
//                    def (url, resource) = resolveReference(fullUrl, reference, true)
//                    if (!resource) {
//                        new ResourceNotAvailable(errorLogger, fullUrl, reference)
//                        return // really continue
//                    }
//                    assert (resource instanceof Patient) || (resource instanceof Practitioner) || (resource instanceof Group) || (resource instanceof Device)
//                    // TODO - does this work if not a patient?
//                    assert resource instanceof Patient//XON XCN XTN
//                    values << cxiFromPatient(resource)
//                }
//                addSlot(builder, 'intendedRecipient', values)
//            }

            if (dm.created)
                addSlot(builder, 'submissionTime', [translateDateTime(dm.created)])

            if (dm.description)
                addName(builder, dm.description)

            addClassification(builder, 'urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd', newId(), dmId)

            if (dm.type)
                addClassificationFromCodeableConcept(builder, dm.type, 'urn:uuid:aa543740-bdda-424e-8c96-df4873be8500', dmId)

            if (dm.masterIdentifier?.value)
                addExternalIdentifier(builder, 'urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8', unURN(dm.masterIdentifier.value), newId(), dmId, 'XDSSubmissionSet.uniqueId')

            if (dm.source?.value) {
                addExternalIdentifier(builder, 'urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832', unURN(dm.source), newId(), dmId, 'XDSSubmissionSet.sourceId')
            }

            if (dm.subject)
                addSubject(builder, fullUrl, dmId, 'urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446', dm.subject, 'XDSSubmissionSet.patientId')

        }
    }

    def addAssociation(xml, type, source, target, slotName, slotValues) {
        def assoc = xml.Association(
                sourceObject: "${source}",
                targetObject: "${target}",
                associationType: "${type}",
                objectType: 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Association',
                id: "${newId()}"
        ) {
            if (slotName) {
                addSlot(xml, slotName, slotValues)
            }
        }
        return assoc
    }

    def addSubmissionSetAssociations(xml, DocumentManifest dm) {
        if (!dm.content) return
        dm.content.each { DocumentManifest.DocumentManifestContentComponent component ->
            Reference ref = component.PReference
            def (url, res) = resolveReference(null, ref.reference, true, true)
            assert res
            def assoc = addAssociation(xml, 'urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember', dm.id, res.id, 'SubmissionSetStatus', ['Original'])
        }
    }

    def loadContained(resource) {
        rMgr.clearContainedResources()
        assert resource instanceof DomainResource
        def contained = resource.contained
        contained?.each { Resource r ->
            rMgr.addContainedResource(r)
        }
    }

    // TODO needs to be real UUID
    String assignId(Resource resource) {
        if (!resource.id || isUUID(resource.id)) {
            if (resource instanceof DocumentReference)
                resource.id = 'Document_' + newId()
            else if (resource instanceof DocumentManifest)
                resource.id = 'SubmissionSet_' + newId()
            else
                resource.id = newId()
        }
        return resource.id
    }

    def loadBundle(IBaseResource bundle) {
        assert bundle
        assert bundle instanceof Bundle
        assert bundle.type == Bundle.BundleType.TRANSACTION

        bundle.getEntry().each { Bundle.BundleEntryComponent component ->
            if (component.hasResource()) {
                assignId(component.getResource())
                rMgr.addResource(component.fullUrl, component.getResource())
            }
        }
        println rMgr.toString()
        bundleValidations(bundle)
    }

    def bundleValidations(Bundle bundle) {
        fullUrlValidation(bundle)
    }

    def fullUrlValidation(Bundle bundle) {
        bundle.getEntry().each { Bundle.BundleEntryComponent component ->
            new BundleFullUrlValidator(component, null)
        }
    }

    // only used for unit test
    def translateResource(def xml, Resource resource) {
        assert (resource instanceof DocumentManifest) || (resource instanceof DocumentReference)
        loadContained(resource)
        if (resource instanceof DocumentManifest) {
            assignId(resource)
            addSubmissionSet(xml, resource.getId(), resource)
        } else if (resource instanceof DocumentReference) {
            assignId(resource)
            addExtrinsicObject(xml, resource.getId(), resource)
        }
    }


    //**************************************************************************
    //
    // Entry points start here. Everything above could be labeled private later
    //
    //**************************************************************************


    def translateBundle(def xml, IBaseResource bundle, isSubmission) {
        loadBundle(bundle)

        xml.RegistryObjectList(xmlns: 'urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0') {
            rMgr.getAllOfType('DocumentManifest').each { url, resource ->
                assignId(resource)
                loadContained(resource)
                DocumentManifest dm = (DocumentManifest) resource
                addSubmissionSet(xml, dm.getId(), dm)
                if (isSubmission)
                    addSubmissionSetAssociations(xml, dm)
            }
            rMgr.getAllOfType('DocumentReference').each { url, resource ->
                assignId(resource)
                loadContained(resource)
                DocumentReference dr = (DocumentReference) resource
                def (ref, binary) = rMgr.resolveReference(url, dr.content[0].attachment.url, true, false)
                assert binary instanceof Binary
                addExtrinsicObject(xml, dr.getId(), dr)

            }
        }
    }

    String translateBundle(Bundle bundle, isSubmission) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        translateBundle(xml, bundle, isSubmission)

        return writer.toString()
    }

    def baseContentId = '.de1e4efca5ccc4886c8528535d2afb251e0d5fa31d58a815@ihexds.nist.gov'

    Submission buildSubmission(Bundle bundle) {

        loadBundle(bundle)

        Submission submission = new Submission()
        submission.contentId = 'm' + baseContentId

        int index = 1

        def documents = [:]

        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.RegistryObjectList(xmlns: 'urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0') {
            rMgr.resources.each { url, resource ->
                if (resource instanceof DocumentManifest) {
                    assignId(resource)
                    proxyBase.resourcesSubmitted << resource
                    loadContained(resource)
                    DocumentManifest dm = (DocumentManifest) resource
                    addSubmissionSet(xml, dm.getId(), dm)
                    addSubmissionSetAssociations(xml, dm)
                }
                if (resource instanceof DocumentReference) {
                    assignId(resource)
                    proxyBase.resourcesSubmitted << resource
                    loadContained(resource)
                    DocumentReference dr = (DocumentReference) resource
                    def (ref, binary) = rMgr.resolveReference(url, dr.content[0].attachment.url, true, false)
                    assert binary instanceof Binary
                    Binary b = binary
                    Attachment a = new Attachment()
                    a.contentId = Integer.toString(index) + baseContentId
                    a.contentType = b.contentType
                    a.content = b.content
                    submission.attachments << a
                    index++

                    addExtrinsicObject(xml, dr.getId(), dr)
                    documents[dr.getId()] = a.contentId
                }
            }
        }
        submission.registryObjectList = writer.toString()

        writer = new StringWriter()
        xml = new MarkupBuilder(writer)
        documents.each { id, contentId ->
            addDocument(xml, id, contentId)
        }
        submission.documentDefinitions = writer.toString()


        return submission
    }

}
