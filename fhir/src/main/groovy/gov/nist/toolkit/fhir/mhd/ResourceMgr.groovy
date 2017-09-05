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
        if (!isAbsolute(containingUrl) && isRelative(referenceUrl)) {
            def x = resources.find {
                def key = it.key
                key.endsWith(referenceUrl)
            }
            if (x) return [x.key, x.value]
        }
        if (isAbsolute(containingUrl) && isRelative(referenceUrl)) {
            def url = rebase(containingUrl, referenceUrl)
            if (resources[url])
                return [url, resources[url]]
        }
        [null, null]
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
        url && !url.startsWith('http') && url.split('/').size() ==2
    }

    static boolean isAbsolute(url) {
        url.startsWith('http')
    }

}
