package gov.nist.toolkit.fhir.mhd

/**
 *
 */
class ResourceMgr {
    // Object is some Resource type
    def resources = [:]

    String toString() {
        StringBuilder buf = new StringBuilder()

        resources.each { url, resource ->
            buf.append(url).append('   ').append(resource.class.name).append('\n')
        }
        buf
    }

    static resolveUrl(containingUrl, referenceUrl) {
        if (MhdUtility.isAbsolute(containingUrl) && MhdUtility.isAbsolute(referenceUrl)) return referenceUrl
        if (MhdUtility.isAbsolute(containingUrl) && MhdUtility.isRelative(referenceUrl)) return MhdUtility.baseUrlFromUrl(containingUrl) + '/' + referenceUrl
        if (MhdUtility.isRelative(containingUrl) && MhdUtility.isAbsolute(referenceUrl)) return referenceUrl
        if (MhdUtility.isRelative(containingUrl) && MhdUtility.isRelative(referenceUrl)) return referenceUrl
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

    def addResource(url, resource) {
        resources[url] = resource
    }

    /**
     *
     * @param type
     * @return list of [url, Resource]
     */
    def getAllOfType(type) {
        def all = []
        resources.each { url, resource -> if (type == resource.class.simpleName) all.add([url, resource])}
        all
    }

    /**
     *
     * @param containingUrl
     * @param referenceUrl
     * @return [url, Resource]
     */
    def resolveReference(String containingUrl, String referenceUrl) {
        if (resources[referenceUrl]) return [referenceUrl, resources[referenceUrl]]
        if (!MhdUtility.isAbsolute(containingUrl) && MhdUtility.isRelative(referenceUrl)) {
            def x = resources.find {
                def key = it.key
                key.endsWith(referenceUrl)
            }
            if (x) return [x.key, x.value]
        }
        if (MhdUtility.isAbsolute(containingUrl) && MhdUtility.isRelative(referenceUrl)) {
            def url = MhdUtility.rebase(containingUrl, referenceUrl)
            if (resources[url])
                return [url, resources[url]]
        }
        [null, null]
    }
}
