package gov.nist.toolkit.fhir.server.resourceMgr

import gov.nist.toolkit.fhir.server.utility.UriBuilder
import gov.nist.toolkit.installation.server.Installation
import org.apache.log4j.Logger
import org.hl7.fhir.instance.model.api.IBaseResource

/**
 * build by a factory - either TestResourceCacheFactory or ResourceCacheMgrFactory
 *
 * Manages multiple resource caches.  Each cache is identified by its BaseUrl
 * For now all caches are static - content loaded from maven build or from a test case
 */
class ResourceCacheMgr {
    static private ResourceCacheMgr theInstance = null;
    static ResourceCacheMgr instance() {
        if (theInstance == null)
            theInstance = new ResourceCacheMgr(Installation.instance().resourceCacheFile())
        return theInstance
    }


    private static final Logger logger = Logger.getLogger(ResourceCacheMgr.class)
    static Map<URI, ResourceCache> caches = [:]  // baseUrl -> cache

    ResourceCacheMgr() {}

    ResourceCacheMgr(File cacheCollectionDir) {
        if (!cacheCollectionDir)
            return
        cacheCollectionDir.listFiles().each {File cache ->
            if (cache.isDirectory() && new File(cache, 'cache.properties').exists()) {
                logger.info("Scanning Resource Cache directory ${cache}")
                ResourceCache rcache = new FileSystemResourceCache(cache)
                caches[rcache.baseUrl] = rcache
            }
        }
    }

    def addMemoryCacheElement(fullUrl, IBaseResource resource) {
        if (fullUrl instanceof String)
            fullUrl = UriBuilder.build(fullUrl)
        assert ResourceMgr.isAbsolute(fullUrl)
        URI baseUrl = ResourceMgr.baseUrlFromUrl(fullUrl)
        ResourceCache cache = caches[baseUrl]
        if (!cache) {
            cache = new MemoryResourceCache()
            caches[baseUrl] = cache
        }
        assert cache instanceof MemoryResourceCache
        cache.add(ResourceMgr.relativeUrl(fullUrl), resource)
    }

    ResourceCacheMgr(Map<URI, ResourceCache> caches) {

    }

    /**
     * return resource or throw exception
     * @param fullUrl
     * @return
     */
    static IBaseResource getResource(fullUrl) {
        if (!Installation.isTestRunning())
            throw new Exception("Resource Cache is disabled")
        if (fullUrl instanceof String)
            fullUrl = UriBuilder.build(fullUrl)
        assert ResourceMgr.isAbsolute(fullUrl)
        def baseUrl = ResourceMgr.baseUrlFromUrl(fullUrl)
        ResourceCache cache = caches[baseUrl]
        if (!cache) throw new Exception("Cannot access ${fullUrl}\nNo cache defined for baseUrl ${baseUrl}\nCaches exist for ${caches.keySet()}")
        return cache.readResource(ResourceMgr.relativeUrl(fullUrl))
    }

}
