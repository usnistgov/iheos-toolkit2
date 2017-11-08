package gov.nist.toolkit.fhir.resourceMgr
/**
 *
 */
class TestResourceCacheFactory {

    static public ResourceCacheMgr getResourceCacheMgr() {
        String path = new TestResourceCacheFactory().getClass().getResource('/resourceCache/unittestingresourcecache.txt').getFile()
        File cacheCollectionFile = new File(path).parentFile
        return new ResourceCacheMgr(cacheCollectionFile)
    }

}
