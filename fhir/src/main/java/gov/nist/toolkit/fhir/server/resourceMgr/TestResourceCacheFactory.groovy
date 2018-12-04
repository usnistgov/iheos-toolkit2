package gov.nist.toolkit.fhir.server.resourceMgr

import java.nio.file.Paths

/**
 *
 */
class TestResourceCacheFactory {

    static public ResourceCacheMgr getResourceCacheMgr() {
        File cacheCollectionFile = Paths.get(new TestResourceCacheFactory().getClass().getResource('/').toURI()).resolve('resourceCache/unittestingresourcecache.txt').toFile().parentFile
        return new ResourceCacheMgr(cacheCollectionFile)
    }

}
