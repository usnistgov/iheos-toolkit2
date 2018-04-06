package gov.nist.toolkit.xdstools2.sites

import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.services.server.SimulatorServiceManager
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.InitEC
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SiteServiceManager
import gov.nist.toolkit.sitemanagement.SeparateSiteLoader
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.TransactionBean
import gov.nist.toolkit.utilities.io.Io
import spock.lang.Shared
import spock.lang.Specification

class SitePromotionTest extends Specification {
    @Shared TestSession defaultTestSession = TestSession.DEFAULT_TEST_SESSION
    @Shared TestSession fooTestSession = new TestSession('foo')
    @Shared SiteServiceManager siteServiceManager = SiteServiceManager.siteServiceManager
    String sessionid = 'session'
    @Shared SimulatorServiceManager simMgr
    @Shared Session session

    def setupSpec() {
        InitEC.init()
        session = UnitTestEnvironmentManager.setupLocalToolkit('default')
        simMgr = new SimulatorServiceManager(session)
    }

    def setup() {
        deleteCommonSites(defaultTestSession)
        deleteCommonSites(fooTestSession)
        [defaultTestSession, fooTestSession]. each { TestSession testSession ->
            SimDb.getAllSimIds(testSession).each { SimId simId ->
                simMgr.delete(simId)
            }
        }
    }

    def 'promote' () {
        setup:
        File fooDir = Installation.instance().actorsDir(fooTestSession)
        File defaultDir = Installation.instance().actorsDir(TestSession.DEFAULT_TEST_SESSION)

        when:
        addRepository(fooTestSession,'dog', '1.2.4')

        then:
        new File(fooDir, 'dog.xml').exists()
        !new File(defaultDir, 'dog.xml').exists()

        when:
        siteServiceManager.promoteSiteToDefault('dog', fooTestSession)

        then:
        !new File(fooDir, 'dog.xml').exists()
        new File(defaultDir, 'dog.xml').exists()

        when:
        SeparateSiteLoader loader = new SeparateSiteLoader(TestSession.DEFAULT_TEST_SESSION)
        Site site = loader.load(defaultDir, null).getSite('dog', TestSession.DEFAULT_TEST_SESSION)

        then:
        site.getOwner() == fooTestSession.value
    }

    def addRepository(TestSession testSession, String name, String oid) {
        Site site = new Site(name, defaultTestSession)
        def tb = new TransactionBean(oid, TransactionBean.RepositoryType.REPOSITORY, 'http://example.com', false, false)
        site.addTransaction(tb)
        siteServiceManager.saveSite(sessionid, site, testSession)
    }


    def deleteCommonSites(TestSession testSession) {
        File dir = Installation.instance().actorsDir(testSession)
        println "Deleting all sites from test session ${testSession} - dir is ${dir}"
        Io.delete(dir)
    }
}
