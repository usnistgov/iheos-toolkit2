package gov.nist.toolkit.fhir.simulators.mhd

import gov.nist.toolkit.errorrecording.GwtErrorRecorder
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder
import gov.nist.toolkit.fhir.resourceMgr.ResolverConfig
import gov.nist.toolkit.fhir.resourceMgr.ResourceCacheMgr
import gov.nist.toolkit.fhir.resourceMgr.ResourceMgr
import gov.nist.toolkit.fhir.simulators.mhd.Attachment
import gov.nist.toolkit.fhir.simulators.mhd.errors.ResourceNotAvailable
import gov.nist.toolkit.fhir.simulators.proxy.util.ReturnableErrorException
import gov.nist.toolkit.fhir.simulators.proxy.util.SimProxyBase
import gov.nist.toolkit.fhir.utility.UriBuilder
import gov.nist.toolkit.xdsexception.ExceptionUtil
import groovy.xml.MarkupBuilder
import org.apache.log4j.Logger
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
    static private final Logger logger = Logger.getLogger(MhdGenerator.class);
    ErrorLogger errorLogger = new ErrorLogger()
    ResourceCacheMgr resourceCacheMgr
    SimProxyBase proxyBase
    GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();
    GwtErrorRecorder er = gerb.buildNewErrorRecorder()
    ResourceMgr rMgr

    MhdGenerator(SimProxyBase proxyBase, ResourceCacheMgr resourceCacheMgr1) {
        this.proxyBase = proxyBase
        resourceCacheMgr = resourceCacheMgr1
        er.sectionHeading('MhdGenerator started')
        rMgr = new ResourceMgr()
        rMgr.addResourceCacheMgr(resourceCacheMgr)
    }

    def clear() {
        errorLogger = new ErrorLogger()
    }

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
//        println "UTC time is ${utcTime}"
        //utcTime = trimTrailingZeros(utcTime)
        return utcTime
    }

//    static trimTrailingZeros(String input) {
//        while (input.size() > 0 && input[input.size()-1] == '0') {
//            input = input.substring(0, input.size()-1)
//        }
//        input
//    }

    static Identifier getOfficial(List<Identifier> identifiers) {
        if (identifiers.size() ==1) return identifiers[0]
        return identifiers.find { it.getUse() == Identifier.IdentifierUse.OFFICIAL }
    }

    static Identifier getUsual(List<Identifier> identifiers) {
        if (identifiers.size() ==1) return identifiers[0]
        return identifiers.find { it.getUse() == Identifier.IdentifierUse.USUAL }
    }

    static boolean isUuidUrn(ref) {
        ref.startsWith('urn:uuid') || ref.startsWith('urn:oid')
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

//    def getEntryUuidValue(fullUrl, List<Identifier> identifier) {
//        if (fullUrl && ResourceMgr.isAbsolute(fullUrl))  {
//            def id = ResourceMgr.resourceIdFromUrl(fullUrl)
//            if (isUUID(id)) {
//                return asUUID(id)
//            }
//        }
//        if (identifier) {
//            Identifier officialEntryUuidId = getOfficial(identifier)
//            if (officialEntryUuidId) return officialEntryUuidId.value
//        }
//        if (fullUrl && isUuidUrn(fullUrl)) return fullUrl
//        return newId()
//    }

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
        if (proxyBase) {  // will be null during unit tests - just skip code translation in that case
            def systemCode = proxyBase.codeTranslator.findCodeByClassificationAndSystem(scheme, coding.system, coding.code)
            assert systemCode, "Cannot find translation for code ${coding.system}|${coding.code} (FHIR) into XDS coding scheme ${scheme} in configured codes.xml file"
            addClassification(builder, scheme, rMgr.newId(), classifiedObjectId, coding.code, systemCode.codingScheme, coding.display)
        } else {
            addClassification(builder, scheme, rMgr.newId(), classifiedObjectId, coding.code, coding.system, coding.display)
        }
    }

    def addDocument(builder, drId, contentId) {
        builder.Document(id:drId, xmlns: 'urn:ihe:iti:xds-b:2007') {
            Include(href: "cid:${contentId}", xmlns: 'http://www.w3.org/2004/08/xop/include')
        }
    }

    def addExtrinsicObject(builder,  fullUrl, dr) {
        if (fullUrl && (fullUrl instanceof String))
            fullUrl = UriBuilder.build(fullUrl)
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

            if (dr.context?.sourcePatientInfo)
                this.addSourcePatient(builder, dr.context.sourcePatientInfo)

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
                addExternalIdentifier(builder, 'urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab', unURN(dr.masterIdentifier.value), rMgr.newId(), drId, 'XDSDocumentEntry.uniqueId')

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

    // TODO sourcePatientInfo is not populated
    def addSourcePatient(builder, Reference sourcePatient) {
        if (!sourcePatient.reference)
            return
        logger.info("Resolve ${sourcePatient.reference} as SourcePatient")
        def extra = 'DocumentReference.context.sourcePatientInfo must reference Contained Patient resource with Patient.identifier.use element set to "usual"'
        def (url, ref) = rMgr.resolveReference(null, sourcePatient.reference, new ResolverConfig().containedRequired())
        if (!ref) {
            new ResourceNotAvailable(errorLogger, null, sourcePatient.reference, extra, 'Table 4.5.1.1-1: FHIR DocumentReference mapping to DocumentEntry')
            return
        }
        logger.info("SourcePatient found")

        if (!(ref instanceof Patient)) {
            new ResourceNotAvailable(errorLogger, null, sourcePatient.reference, extra + "\nReference to ${ref.getClass().getSimpleName()} instead of Patient", 'Table 4.5.1.1-1: FHIR DocumentReference mapping to DocumentEntry')
            return
        }

        Patient patient = (Patient) ref

        List<Identifier> identifiers = patient.getIdentifier()
        Identifier usual = getUsual(identifiers)
        if (!usual) {
            new ResourceNotAvailable(errorLogger, null, sourcePatient.reference, extra + "\nPatient Identifier must be labeled 'usual'", 'Table 4.5.1.1-1: FHIR DocumentReference mapping to DocumentEntry')
            return
        }

        if (!usual.value) {
            new ResourceNotAvailable(errorLogger, null, sourcePatient.reference, extra + "\nPatient Identifier.value is null", 'Appendix E, section E.3 Identifier Type')
            return
        }
        if (!usual.system) {
            new ResourceNotAvailable(errorLogger, null, sourcePatient.reference, extra + "\nPatient Identifier.system is null", 'Appendix E, section E.3 Identifier Type')
            return
        }

        String value = usual.value
        String system = usual.system
        String oid = unURN(system)
        def pid = "${value}^^^&${oid}&ISO"

        addSlot(builder, 'sourcePatientId', [pid])
    }

    // TODO must be absolute reference
    def addSubject(builder, URI fullUrl, containingObjectId, scheme,  org.hl7.fhir.dstu3.model.Reference subject, attName) {
        def ref1 = UriBuilder.build(subject.getReference())
        def (url, ref) = rMgr.resolveReference(fullUrl, ref1, new ResolverConfig().externalRequired())
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

        addExternalIdentifier(builder, scheme, pid, rMgr.newId(), containingObjectId, attName)
    }

    def resolveId(id) {
        return rMgr.resolveId(id)
    }

    def addSubmissionSet(builder, fullUrl, dm) {
        if (fullUrl && (fullUrl instanceof String))
            fullUrl = UriBuilder.build(fullUrl)
        String dmId = dm.id // getEntryUuidValue(fullUrl, dm.identifier)
        er.detail("New SubmissionSet(${dmId})")
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

            addClassification(builder, 'urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd', rMgr.newId(), dmId)

            if (dm.type)
                addClassificationFromCodeableConcept(builder, dm.type, 'urn:uuid:aa543740-bdda-424e-8c96-df4873be8500', dmId)

            if (dm.masterIdentifier?.value)
                addExternalIdentifier(builder, 'urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8', unURN(dm.masterIdentifier.value), rMgr.newId(), dmId, 'XDSSubmissionSet.uniqueId')

            if (dm.source?.value) {
                addExternalIdentifier(builder, 'urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832', unURN(dm.source), rMgr.newId(), dmId, 'XDSSubmissionSet.sourceId')
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
                id: "${rMgr.newId()}"
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
            def (url, res) = rMgr.resolveReference(null, ref.reference, new ResolverConfig().internalRequired())
            assert res
            def assoc = addAssociation(xml, 'urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember', dm.id, res.id, 'SubmissionSetStatus', ['Original'])
        }
    }



    def loadBundle(IBaseResource bundle) {
        assert bundle
        assert bundle instanceof Bundle
        assert bundle.type == Bundle.BundleType.TRANSACTION

        rMgr = new ResourceMgr(bundle, er)
        rMgr.addResourceCacheMgr(resourceCacheMgr)
    }

    // only used for unit test
    def translateResource(def xml, Resource resource) {
        assert (resource instanceof DocumentManifest) || (resource instanceof DocumentReference)
        rMgr.currentResource(resource)
        if (resource instanceof DocumentManifest) {
            rMgr.assignId(resource)
            addSubmissionSet(xml, resource.getId(), resource)
        } else if (resource instanceof DocumentReference) {
            rMgr.assignId(resource)
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
            rMgr.getResourcesByType('DocumentManifest').each { url, resource ->
                rMgr.assignId(resource)
                rMgr.currentResource(resource)
                DocumentManifest dm = (DocumentManifest) resource
                addSubmissionSet(xml, dm.getId(), dm)
                if (isSubmission)
                    addSubmissionSetAssociations(xml, dm)
            }
            rMgr.getResourcesByType('DocumentReference').each { url, resource ->
                rMgr.assignId(resource)
                rMgr.currentResource(resource)
                DocumentReference dr = (DocumentReference) resource
                def (ref, binary) = rMgr.resolveReference(url, dr.content[0].attachment.url, new ResolverConfig().internalRequired())
                assert binary instanceof Binary
                addExtrinsicObject(xml, dr.getId(), dr)
            }
        }

        close()
    }

    String translateBundle(Bundle bundle, isSubmission) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        translateBundle(xml, bundle, isSubmission)

        return writer.toString()
    }

    def baseContentId = '.de1e4efca5ccc4886c8528535d2afb251e0d5fa31d58a815@ihexds.nist.gov'

    Submission buildSubmission(Bundle bundle) {
        try {
            loadBundle(bundle)

            Submission submission = new Submission()
            submission.contentId = 'm' + baseContentId

            int index = 1

            def documents = [:]

            def writer = new StringWriter()
            def xml = new MarkupBuilder(writer)
            xml.RegistryObjectList(xmlns: 'urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0') {
                rMgr.resources.each { URI url, IBaseResource resource ->
                    if (resource instanceof DocumentManifest) {
                        rMgr.assignId(resource)
                        er.sectionHeading("DocumentManifest(${resource.id})  URL is ${url}")
                        proxyBase.resourcesSubmitted << resource
                        rMgr.currentResource(resource)
                        DocumentManifest dm = (DocumentManifest) resource
                        addSubmissionSet(xml, dm.getId(), dm)
                        addSubmissionSetAssociations(xml, dm)
                    }
                    else if (resource instanceof DocumentReference) {
                        rMgr.assignId(resource)
                        er.sectionHeading("DocumentReference(${resource.id})  URL is ${url}")
                        proxyBase.resourcesSubmitted << resource
                        rMgr.currentResource(resource)
                        DocumentReference dr = (DocumentReference) resource
                        def (ref, binary) = rMgr.resolveReference(url, UriBuilder.build(dr.content[0].attachment.url), new ResolverConfig().internalRequired())
                        er.detail("References Binary ${ref}")
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
                    } else {
                        proxyBase.resourcesSubmitted << resource

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

            close()

            if (!errorLogger.hasErrors())
                return submission
        } catch (Throwable e) {
            logger.error(ExceptionUtil.exception_details(e))
            throw new Exception("Provide Document Bundle to Provide and Register translation failed - ${e.message}", e)
        }
        throw new ReturnableErrorException(ErrorLoggerAsHttpResponse.buildHttpResponse(proxyBase, errorLogger))

    }

    def close() {
        er.sectionHeading("MhdGenerator done")
            proxyBase?.simDb?.logErrorRecorder(er)
    }

}
