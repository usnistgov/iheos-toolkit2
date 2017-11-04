package gov.nist.toolkit.fhir.resourceMgr

import gov.nist.toolkit.installation.Installation
import org.apache.log4j.Logger
import org.hl7.fhir.instance.model.api.IBaseResource
/**
 * build by a factory - either TestResourceCacheFactory or ResourceCacheFactory
 */
class ResourceCacheMgr {
    static private ResourceCacheMgr theInstance = null;
    static ResourceCacheMgr instance() {
        if (theInstance == null)
            theInstance = new ResourceCacheMgr(Installation.instance().resourceCacheFile())
        return theInstance
    }


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

    IBaseResource getResource(fullUrl) {
        assert ResourceMgr.isAbsolute(fullUrl)
        def baseUrl = ResourceMgr.baseUrlFromUrl(fullUrl)
        ResourceCache cache = caches[baseUrl]
        if (!cache) throw new Exception("Cannot access ${fullUrl}\nNo cache defined for baseUrl ${baseUrl}\nCaches exist for ${caches.keySet()}")
        return cache.getResource(ResourceMgr.relativeUrl(fullUrl))
    }

}
