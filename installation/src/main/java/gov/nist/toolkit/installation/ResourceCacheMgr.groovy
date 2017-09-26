package gov.nist.toolkit.installation

import org.apache.log4j.Logger
import org.hl7.fhir.dstu3.model.Resource

/**
 * build by a factory - either TestResourceCacheFactory or ResourceCacheFactory
 */
class ResourceCacheMgr {
    private static final Logger logger = Logger.getLogger(ResourceCacheMgr.class)
    static Map<String, ResourceCache> caches = [:]  // baseUrl -> cache

    ResourceCacheMgr(File cacheCollectionDir) {
        if (!cacheCollectionDir)
            return
        cacheCollectionDir.listFiles().each {File cache ->
            if (cache.isDirectory() && new File(cache, 'cache.properties').exists()) {
                logger.info("Scanning Resource Cache directory ${cache}")
                ResourceCache rcache = new ResourceCache(cache)
                caches[rcache.baseUrl] = rcache
            }
        }
    }

    Resource getResource(fullUrl) {
        assert ResourceMgr.isAbsolute(fullUrl)
        def baseUrl = ResourceMgr.baseUrlFromUrl(fullUrl)
        ResourceCache cache = caches[baseUrl]
        assert cache, "Cannot access ${fullUrl}\nNo cache defined for baseUrl ${baseUrl}\nCaches exist for ${caches.keySet()}"
        return cache.getResource(ResourceMgr.relativeUrl(fullUrl))
    }

}
