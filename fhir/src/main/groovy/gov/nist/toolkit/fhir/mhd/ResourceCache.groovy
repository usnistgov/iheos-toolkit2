package gov.nist.toolkit.fhir.mhd

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.utilities.io.Io
import org.hl7.fhir.dstu3.model.Resource

/**
 * Local cache of FHIR resources
 */
class ResourceCache {
    static FhirContext ctx = FhirContext.forDstu3()

    File cacheDir
    String baseUrl

    ResourceCache(File cacheDir) {
        this.cacheDir = cacheDir
        File propFile = new File(cacheDir, 'cache.properties')
        assert propFile
        Properties props = new Properties()
        props.load(Io.getInputStreamFromFile(propFile))
        baseUrl = props.getProperty('baseUrl')
    }

    Resource getResource(relativeUrl) {
        File cacheFile = cacheFile(relativeUrl, 'xml')
        return ctx.newXmlParser().parseResource(cacheFile.text)
    }

    File cacheFile(relativeUrl, fileType) {
        assert ResourceMgr.isRelative(relativeUrl)
        def type = ResourceMgr.resourceTypeFromUrl(relativeUrl)
        def id = ResourceMgr.id(relativeUrl) + ((fileType) ? ".${fileType}" : '')
        return new File(new File(cacheDir, type), id)
    }
}
