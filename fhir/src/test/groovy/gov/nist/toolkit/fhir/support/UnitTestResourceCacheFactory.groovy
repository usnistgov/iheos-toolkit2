package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.fhir.mhd.ResourceCache
import gov.nist.toolkit.fhir.mhd.ResourceCacheMgr
import gov.nist.toolkit.fhir.mhd.ResourceMgr
import org.hl7.fhir.dstu3.model.Resource

/**
 *
 */
class UnitTestResourceCacheFactory {

    static ResourceCacheMgr getResourceCacheMgr() {
        String path = new UnitTestResourceCacheFactory().getClass().getResource('/resourceCache/unittestingresourcecache.txt').getFile()
        File cacheCollectionFile = new File(path).parentFile
        return new ResourceCacheMgr(cacheCollectionFile)
    }

}
