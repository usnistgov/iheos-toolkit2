package gov.nist.toolkit.installation
/**
 *
 */
class TestResourceCacheFactory {

    static protected ResourceCacheMgr getResourceCacheMgr() {
        String path = new TestResourceCacheFactory().getClass().getResource('/resourceCache/unittestingresourcecache.txt').getFile()
        File cacheCollectionFile = new File(path).parentFile
        return new ResourceCacheMgr(cacheCollectionFile)
    }

}
