package gov.nist.toolkit.simcommon.server.services

import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.installation.PropertyServiceManager
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.log4j.Logger

/**
 * API to start key services
 * This exists because sometimes the need for a service arisies in low level code that cannot access the
 * top side.  Using HTTP requests gets around the code dependency issues
 */
class ServiceApi {
    static Logger logger = Logger.getLogger(ServiceApi.class);

    /**
     *
     * @param testSession
     * @return success?
     */
    static boolean startFhirSupportServer(String testSession) {
        Installation i = Installation.instance()
        PropertyServiceManager p = i.propertyServiceManager()
        String contextName = (i.servletContextName) ? "${i.servletContextName}/" : ''
        String uri = "http://${p.toolkitHost}:${p.toolkitPort}/${contextName}services/start?name=fhir_support;session=${testSession}"
        logger.info("Service API request to ${uri}")
        try {
            HttpClient client = HttpClientBuilder.create().build()
            HttpGet request = new HttpGet(uri)
            HttpResponse response = client.execute(request)
            def statusLine = response.getStatusLine()
            return statusLine.statusCode in [HttpStatus.SC_CREATED, HttpStatus.SC_OK]
        } catch (Exception e) {
            logger.fatal("Cannot start FHIR Support server: ${e.message}")
            throw e
        }
    }
}
