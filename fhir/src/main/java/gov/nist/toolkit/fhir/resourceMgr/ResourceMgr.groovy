package gov.nist.toolkit.fhir.resourceMgr

import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.fhir.validators.BundleFullUrlValidator
import gov.nist.toolkit.utilities.id.UuidAllocator
import org.hl7.fhir.dstu3.model.*
/**
 *
 */
class ResourceMgr {
    Bundle bundle = null
    // Object is some Resource type
    def resources = [:]   // url -> resource
    int newIdCounter = 1

    // for current resource
    def containedResources = [:]
    def fullUrl

    // resource cache mgr
    ResourceCacheMgr resourceCacheMgr = null

    ErrorRecorder er = null;

    ResourceMgr() {
    }

    ResourceMgr(Bundle bundle, ErrorRecorder er) {
        this.bundle = bundle
        this.er = er;
        if (bundle)
            parseBundle()
    }

    def addResourceCacheMgr(ResourceCacheMgr resourceCacheMgr) {
        this.resourceCacheMgr = resourceCacheMgr
    }

    def parseBundle() {
        bundle.getEntry().each { Bundle.BundleEntryComponent component ->
            if (component.hasResource()) {
                assignId(component.getResource())
                addResource(component.fullUrl, component.getResource())
            }
        }
        if (er) {
            er.sectionHeading('Load Resources')
            er.detail(toString())
        }
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

    def currentResource(resource) {
        clearContainedResources()
        assert resource instanceof DomainResource
        def contained = resource.contained
        contained?.each { Resource r ->
            addContainedResource(r)
        }

        fullUrl = url(resource)
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

    // TODO needs to be real UUID
    String assignId(Resource resource) {
        if (!resource.id || isUUID(resource.id)) {
            if (resource instanceof DocumentReference)
                resource.id = UuidAllocator.allocate()  //'Document_' + newId()
            else if (resource instanceof DocumentManifest)
                resource.id = UuidAllocator.allocate()   // 'SubmissionSet_' + newId()
            else
                resource.id = newId()
        }
        return resource.id
    }

    def newId() { String.format("ID%02d", newIdCounter++) }

    String toString() {
        StringBuilder buf = new StringBuilder()
        buf.append("Resources:\n")

        resources.each { url, resource ->
            buf.append(url).append('   ').append(resource.class.simpleName).append('\n')
        }
        buf
    }

    static resolveUrl(containingUrl, referenceUrl) {
        if (isAbsolute(containingUrl) && isAbsolute(referenceUrl)) return referenceUrl
        if (isAbsolute(containingUrl) && isRelative(referenceUrl)) return baseUrlFromUrl(containingUrl) + '/' + referenceUrl
        if (isRelative(containingUrl) && isAbsolute(referenceUrl)) return referenceUrl
        if (isRelative(containingUrl) && isRelative(referenceUrl)) return referenceUrl
        if (containingUrl.startsWith('urn')) {
            return referenceUrl
        }
        assert false, 'Impossible'
    }

    Object getResource(referenceUrl) {
        return resources[referenceUrl]
    }

    Object getResource(containingUrl, referenceUrl) {
        def url = resolveUrl(containingUrl, referenceUrl)
        if (url)
            return resources[url]
        return null
    }

    List getResourceObjects() {
        resources.values() as List
    }

    def addResource(url, resource) {
        resources[url] = resource
    }

    def addContainedResource(resource) {
        assert resource instanceof DomainResource
        containedResources[resource.id] = resource
    }

    def clearContainedResources() {
        containedResources = [:]
    }

    def getContainedResource(id) {
        assert id
        return containedResources[id]
    }

    def url(resource) {
        resources.entrySet().find { Map.Entry entry ->
            entry.value == resource
        }.key
    }


    /**
     *
     * @param type
     * @return list of [url, Resource]
     */
    def getResourcesByType(type) {
        def all = []
        resources.each { url, resource -> if (type == resource.class.simpleName) all.add([url, resource])}
        all
    }

    def resolveReference(String referenceUrl) {
        resolveReference(fullUrl, referenceUrl, true, false)
    }

    /**
     *
     * @param containingUrl  (fullUrl)
     * @param referenceUrl   (reference)
     * @return [url, Resource]
     */
    def resolveReference(String containingUrl, String referenceUrl, relativeReferenceOk, relativeReferenceRequired) {
        assert referenceUrl
        if (relativeReferenceOk && referenceUrl.startsWith('#')) {
            def res = getContainedResource(referenceUrl)
            def val =  [referenceUrl, res]
            return val
        }
        if (resources[referenceUrl]) return [referenceUrl, resources[referenceUrl]]
        def isRelativeReference = isRelative(referenceUrl)
        if (relativeReferenceRequired && !isRelativeReference)
            return [null, null]
        def type = resourceTypeFromUrl(referenceUrl)
        if (!isAbsolute(containingUrl) && isRelative(referenceUrl)) {
            def x = resources.find {
                def key = it.key
                // for Patient, it must be absolute reference
                if ('Patient' == type && isRelativeReference && !relativeReferenceOk) return false
                key.endsWith(referenceUrl)
            }
            if (x) return [x.key, x.value]
        }
        if (isAbsolute(containingUrl) && isRelative(referenceUrl)) {
            def url = rebase(containingUrl, referenceUrl)
            if (resources[url])
                return [url, resources[url]]
            def resource = resourceCacheMgr.getResource(url)
            if (resource)
                return [url, resource]
        }

        // external


        [null, null]
    }

    static String rebase(String containingUrl, String referenceUrl) {
        baseUrlFromUrl(containingUrl) + '/' + referenceUrl
    }

    static String resourceTypeFromUrl(String fullUrl) {
        assert fullUrl
        if (fullUrl.startsWith('#')) return null
        fullUrl.reverse().split('/')[1].reverse()
    }

    static String resourceIdFromUrl(String fullUrl) {
        fullUrl.reverse().split('/')[0].reverse()
    }

    static String relativeUrl(String fullUrl) {
        List<String> parts = fullUrl.split('/')
        [parts[parts.size() - 2], parts[parts.size() - 1]].join('/')
    }

    static id(url) {
        List<String> parts = url.split('/')
        return parts[parts.size() - 1]
    }

    static withNewId(fullUrl, newid) {
        def base = baseUrlFromUrl(fullUrl)
        def rel = relativeUrl(fullUrl)
        def type = resourceTypeFromUrl(fullUrl)
        return base + '/' + type + '/' + newid
    }

    static String baseUrlFromUrl(String fullUrl) {
        if (fullUrl.startsWith('urn')) return fullUrl
        List<String> parts = fullUrl.split('/')
        parts.remove(parts.size() - 1)
        parts.remove(parts.size() - 1)
        parts.join('/')
    }

    static boolean isRelative( url) {
        assert url
        url && !url.startsWith('http') && url.split('/').size() ==2
    }

    static boolean isAbsolute(url) {
        assert url
        url.startsWith('http')
    }

    /**
     *
     * @param id
     * @return [Resource]
     */
    def resolveId(id) {
        assert id
        return resources.values().find { it.id == id }
    }

}
