package gov.nist.toolkit.xdstools2.sites

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.services.server.SimulatorServiceManager
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.simcommon.server.InitEC
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SiteFactory
import gov.nist.toolkit.simcommon.server.SiteServiceManager
import gov.nist.toolkit.sitemanagement.Sites
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.TransactionBean
import gov.nist.toolkit.utilities.io.Io
import spock.lang.Shared
import spock.lang.Specification

class SimSiteTest extends Specification {
    @Shared TestSession defaultTestSession = TestSession.DEFAULT_TEST_SESSION
    @Shared TestSession fooTestSession = new TestSession('foo')
    @Shared SiteServiceManager siteServiceManager = SiteServiceManager.siteServiceManager
    @Shared String sessionid = 'session'
    @Shared Session session
    @Shared SimulatorServiceManager simMgr

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

    def deleteCommonSites(TestSession testSession) {
        File dir = Installation.instance().actorsDir(testSession)
        println "Deleting all sites from test session ${testSession} - dir is ${dir}"
        Io.delete(dir)
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

    def 'build repo'() {
        when:
        SimId simId = SimIdFactory.simIdBuilder('bill__foo')
        simMgr.delete(simId)

        then:
        !simMgr.exists(simId)

        when:
        simMgr.getNewSimulator(ActorType.REPOSITORY.name, simId)

        then:
        simMgr.exists(simId)
    }

    def 'inherited sim'() {
        when:
        SimId defaultSimId = SimIdFactory.simIdBuilder('default__one')
        simMgr.getNewSimulator(ActorType.REPOSITORY.name, defaultSimId)

        SimId fooSimId = SimIdFactory.simIdBuilder('foo__two')
        simMgr.getNewSimulator(ActorType.REPOSITORY.name, fooSimId)

        then:
        SimDb.getAllSimIds(defaultTestSession) as Set == [defaultSimId] as Set
        SimDb.getAllSimIds(fooTestSession) as Set == [defaultSimId, fooSimId] as Set
    }

    def 'inherited sim and common site'() {
        when:
        SimId defaultSimId = SimIdFactory.simIdBuilder('default__one')
        simMgr.getNewSimulator(ActorType.REPOSITORY.name, defaultSimId)

        SimId fooSimId = SimIdFactory.simIdBuilder('foo__two')
        simMgr.getNewSimulator(ActorType.REPOSITORY.name, fooSimId)

        and:
        addRegistry(defaultTestSession, 'bob')
        addRegistry(fooTestSession, 'sam')

        and:
        List<Site> defaultSites = siteServiceManager.getAllSites(sessionid, defaultTestSession)
        List<String> defaultSiteNames = SiteFactory.getSiteNames(defaultSites)

        then:
        defaultSiteNames as Set == ['default__one', 'bob', Sites.ALL_REPOSITORIES] as Set

        when:
        List<Site> fooSites = siteServiceManager.getAllSites(sessionid, fooTestSession)
        List<String> fooSiteNames = SiteFactory.getSiteNames(fooSites)

        then:
        fooSiteNames as Set == ['default__one', 'bob', 'foo__two', 'sam', Sites.ALL_REPOSITORIES] as Set

    }

}
