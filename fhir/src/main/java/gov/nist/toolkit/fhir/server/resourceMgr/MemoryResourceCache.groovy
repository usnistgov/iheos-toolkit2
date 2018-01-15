package gov.nist.toolkit.fhir.server.resourceMgr

import org.hl7.fhir.instance.model.api.IBaseResource

class MemoryResourceCache implements ResourceCache {
    Map<URI, IBaseResource> cache = [:]

    def add(URI uri, IBaseResource res) {
        cache[uri] = res
    }

    @Override
    IBaseResource readResource(URI uri) {
        return cache[uri]
    }
}
