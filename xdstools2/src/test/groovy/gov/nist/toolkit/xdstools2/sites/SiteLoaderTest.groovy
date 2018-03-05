package gov.nist.toolkit.xdstools2.sites

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.services.server.SimulatorServiceManager
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.InitEC
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SiteServiceManager
import gov.nist.toolkit.sitemanagement.Sites
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.TransactionBean
import gov.nist.toolkit.utilities.io.Io
import spock.lang.Shared
import spock.lang.Specification

class SiteLoaderTest extends Specification {
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

    def addRegistry(TestSession testSession, String name) {
        Site site = new Site(name , testSession)
        def tb = new TransactionBean()
        tb.setEndpoint('http://example.com')
        tb.transType = TransactionType.REGISTER
        site.addTransaction(tb)
        siteServiceManager.saveSite(sessionid, site, testSession)
    }

    def addRepository(TestSession testSession, String name, String oid) {
        Site site = new Site(name, defaultTestSession)
        def tb = new TransactionBean(oid, TransactionBean.RepositoryType.REPOSITORY, 'http://example.com', false, false)
        site.addTransaction(tb)
        siteServiceManager.saveSite(sessionid, site, testSession)
    }

    Set<String> reload(TestSession testSession) {
        def sitenames = siteServiceManager.reloadSites(sessionid, false, testSession)
        return sitenames as Set
    }

    def 'create/read single site in default'() {
        when: 'create a site'
        addRegistry(defaultTestSession, 'bob')

        then:
        reload(defaultTestSession) == ['bob', Sites.ALL_REPOSITORIES] as Set
    }

    def 'create site in default and another in foo'() {
        when:
        addRegistry(defaultTestSession, 'bob')
        addRegistry(fooTestSession, 'sam')

        then:
        reload(fooTestSession) == ['bob', 'sam', Sites.ALL_REPOSITORIES] as Set
    }

    def 'foo testsession reports foo and default repositories'() {
        when: 'create site with cat repo in default'
        addRepository(defaultTestSession, 'cat', '1.2.3')

        and: 'create site with dog repo in foo'
        addRepository(fooTestSession,'dog', '1.2.4')

        and:
        def defaultRepoNames = siteServiceManager.getRepositoryNames(sessionid, defaultTestSession) as Set

        then: 'default should have only repo cat'
        siteServiceManager.getSiteNames(sessionid, true, false, defaultTestSession) as Set == ['cat', Sites.ALL_REPOSITORIES] as Set
        defaultRepoNames == ['cat'] as Set

        siteServiceManager.getSiteNames(sessionid, true, false, fooTestSession) as Set == ['cat', Sites.ALL_REPOSITORIES, 'dog'] as Set
        siteServiceManager.getRepositoryNames(sessionid, fooTestSession) as Set == ['cat', 'dog'] as Set
    }

    def 'get site' () {
        when:
        addRegistry(defaultTestSession, 'bob')
        addRegistry(fooTestSession, 'sam')

        then:
        siteServiceManager.getSite(sessionid, 'bob', defaultTestSession) != null
        siteServiceManager.getSite(sessionid, 'sam', fooTestSession) != null
        siteServiceManager.getSite(sessionid, 'bob', fooTestSession) != null
    }

    def 'get all sites'() {
        when:
        addRegistry(defaultTestSession, 'bob')
        addRegistry(fooTestSession, 'sam')

        then:
        siteServiceManager.getAllSites(sessionid, defaultTestSession).collect {it.name} as Set == ['bob', Sites.ALL_REPOSITORIES] as Set
        siteServiceManager.getAllSites(sessionid, fooTestSession).collect {it.name} as Set == ['bob', 'sam', Sites.ALL_REPOSITORIES] as Set
    }

    // TODO need test including sims


    def deleteCommonSites(TestSession testSession) {
        File dir = Installation.instance().actorsDir(testSession)
        println "Deleting all sites from test session ${testSession} - dir is ${dir}"
        Io.delete(dir)
    }
}
