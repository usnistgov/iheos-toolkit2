package gov.nist.toolkit.xdstools2.server.casSessionBuilder

import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.server.TestSessionFactory
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.xdstools2.server.serviceManager.GazelleServiceManager
import groovy.transform.TypeChecked
import org.apache.http.HttpStatus
import org.apache.http.NameValuePair
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.log4j.Logger

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * create new CAS test session
 *
 * Can be tested against Steve's Gazelle installation by:
 * Initializing toolkit.properties with
 * Gazelle_Config_URL=http://ihe.wustl.edu/gazelle-na/systemConfigurations.seam
 * Cas_mode=true
 * Multiuser_mode=true
 * and making sure Nonce_size is set
 *
 * Then from a browser:
 *
 * http://localhost:8888/CasSessionBuilder?testingSessionId=57;systemName=OTHER_NIST_RED_2018
 */
@TypeChecked
class CasSessionBuilderServlet extends HttpServlet {
    static private Logger logger = Logger.getLogger(CasSessionBuilderServlet.class);

    @Override
    void doGet(HttpServletRequest request, HttpServletResponse response) {
        if (!Installation.instance().propertyServiceManager().casMode) {
            logger.error("Received Gazelle CAS request - not running in Gazelle mode")
            response.sendError(HttpStatus.SC_SERVICE_UNAVAILABLE, 'Toolkit not running in CAS Mode')
            return
        }

        if (!Installation.instance().propertyServiceManager().multiuserMode) {
            logger.error("Received Gazelle CAS request - not running in MultiUser mode")
            response.sendError(HttpStatus.SC_SERVICE_UNAVAILABLE, 'Toolkit not running in MultiUser Mode')
            return
        }

        String gazelleBaseUrl = Installation.instance().propertyServiceManager().propertyManager.getToolkitGazelleConfigURL()
        if (!gazelleBaseUrl) {
            logger.error("Received Gazelle CAS request - Gazelle Base URL not configured in toolkit.properties")
            response.sendError(HttpStatus.SC_SERVICE_UNAVAILABLE, 'Toolkit not configured with Gazelle Base Address')
            return
        }

        String gazelleSessionId = null
        String systemName = null

        String uri = 'http://foo/bar?' + request.queryString
        List<NameValuePair> params = URLEncodedUtils.parse(new URI(uri), "UTF-8")

        params.each { NameValuePair pair ->
            if (pair.name.equalsIgnoreCase('testingSessionId'))
                gazelleSessionId = pair.value.trim()
            if (pair.name.equalsIgnoreCase('systemName'))
                systemName = pair.value.trim()
        }

        if (!gazelleSessionId) {
            logger.error("Received Gazelle CAS request - Gazelle Session Id parameter missing")
            response.sendError(HttpStatus.SC_BAD_REQUEST, 'Did not receive testingSessionId parameter')
            return
        }

        if (!systemName) {
            logger.error('Received Gazelle CAS request - Gazelle systemName parameter missing')
            response.sendError(HttpStatus.SC_BAD_REQUEST, 'Did not receive systemName parameter')
            return
        }

        TestSession testSession = TestSessionFactory.build()

        // testingSessionId=57
        try {
            GazelleServiceManager mgr = new GazelleServiceManager(null)
            mgr.setTestSessionId(gazelleSessionId)
            mgr.setTestSession(testSession)
            mgr.reloadSystemFromGazelle(systemName);
        } catch (Exception e) {
            logger.error("Received Gazelle CAS request - trying to load system configurations - ${e.getMessage()}")
            response.sendError(HttpStatus.SC_BAD_REQUEST, e.getMessage())
            return
        }


        response.setContentType('text/plain')
        Writer writer = response.getWriter()
        writer.print(testSession.value)
        writer.close()
    }

}
