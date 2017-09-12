package gov.nist.toolkit.installation

import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.installation.ResourceCacheMgr

/**
 *
 */
class ResourceCacheFactory {

    static ResourceCacheMgr getResourceCacheMgr() {
        File cacheCollectionFile = Installation.instance().resourceCacheFile()
        return new ResourceCacheMgr(cacheCollectionFile)
    }
}
