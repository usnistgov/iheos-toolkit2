package gov.nist.toolkit.fhir.server.resourceMgr

import gov.nist.toolkit.installation.Installation

/**
 *
 */
class ResourceCacheFactory {

    static ResourceCacheMgr getResourceCacheMgr() {
        File cacheCollectionFile = Installation.instance().resourceCacheFile()
        return new ResourceCacheMgr(cacheCollectionFile)
    }
}
