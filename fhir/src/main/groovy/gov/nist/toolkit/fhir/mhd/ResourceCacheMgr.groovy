package gov.nist.toolkit.fhir.mhd

import org.hl7.fhir.dstu3.model.Resource

/**
 * build by a factory - either UnitTestResourceCacheFactory or ResourceCacheFactory
 */
class ResourceCacheMgr {
    Map<String, ResourceCache> caches = [:]  // baseUrl -> cache

    ResourceCacheMgr(File cacheCollectionDir) {
        cacheCollectionDir.listFiles().each {File cache ->
            if (cache.isDirectory() && new File(cache, 'cache.properties').exists()) {
                println "Scanning Resource Cache directory ${cache}"
                ResourceCache rcache = new ResourceCache(cache)
                caches[rcache.baseUrl] = rcache
            }
        }
    }

    Resource getResource(fullUrl) {
        assert ResourceMgr.isAbsolute(fullUrl)
        def baseUrl = ResourceMgr.baseUrlFromUrl(fullUrl)
        ResourceCache cache = caches[baseUrl]
        assert cache, "No cache defined for baseUrl ${baseUrl}\nCaches exist for ${caches.keySet()}"
        return cache.getResource(ResourceMgr.relativeUrl(fullUrl))
    }

}
