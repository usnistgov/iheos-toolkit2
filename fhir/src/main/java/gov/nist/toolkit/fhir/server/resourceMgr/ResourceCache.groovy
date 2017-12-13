package gov.nist.toolkit.fhir.server.resourceMgr

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.server.utility.UriBuilder
import gov.nist.toolkit.utilities.io.Io
import org.apache.log4j.Logger
import org.hl7.fhir.instance.model.api.IBaseResource
/**
 * Local cache of FHIR resources
 */
class ResourceCache {
    private static final Logger logger = Logger.getLogger(ResourceCache.class)
    static FhirContext ctx = FhirContext.forDstu3()

    File cacheDir
    URI baseUrl

    ResourceCache(File cacheDir) {
        this.cacheDir = cacheDir
        File propFile = new File(cacheDir, 'cache.properties')
        assert propFile
        Properties props = new Properties()
        props.load(Io.getInputStreamFromFile(propFile))
        def base = props.getProperty('baseUrl')
        baseUrl = UriBuilder.build(base)
        logger.info("New Resource cache: ${base}  --> ${cacheDir}")
    }

    IBaseResource readResource(relativeUrl) {
        File cacheFile = cacheFile(relativeUrl, 'xml')
        if (!cacheFile.exists())
            return null
        return ctx.newXmlParser().parseResource(cacheFile.text)
    }

    File cacheFile(URI relativeUrl, fileType) {
        assert ResourceMgr.isRelative(relativeUrl)
        def type = ResourceMgr.resourceTypeFromUrl(relativeUrl)
        def id = ResourceMgr.id(relativeUrl) + ((fileType) ? ".${fileType}" : '')
        return new File(new File(cacheDir, type), id)
    }
}
