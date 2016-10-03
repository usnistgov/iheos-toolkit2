package gov.nist.toolkit.itTests.xds.saml

import gov.nist.toolkit.actorfactory.SimCache
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import spock.lang.Shared
/**
 * Test the STS as provided by Gazelle using Toolkit's HttpTransaction step instruction
 */
class StsSamlAssertionSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String reg = 'sunil__reg'
    @Shared SimId simId = new SimId(reg)
    @Shared String testSession = 'sunil'
    @Shared Site gazelleStsSite
    @Shared Session tkSession

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

//        tkSession = UnitTestEnvironmentManager.setupLocalToolkit()
//        tkSession.setTls(true)

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        // local customization

        new BuildCollections().init(null)

        setGazelleStsSite()
    }

    def setGazelleStsSite() {

        String gazelleStsEndpoint = "https://gazelle.ihe.net/picketlink-sts"
        String transName = "r.b";
        String endpoint = gazelleStsEndpoint
        boolean isSecure = true;
        boolean isAsync = false;

        gazelleStsSite = new Site("gazelleSts")
        gazelleStsSite.addTransaction(transName, endpoint, isSecure, isAsync);

        new SimCache().getSimManagerForSession("sunil",true)

        // Adding a site dynamically doesn't work. Must use Actors.xml file.
//       .allSites.getAllSites().add(gazelleStsSite)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
//        ListenerFactory.terminateAll()
    }

    def 'Make sure the GazelleSts site can be retrieved'() {
        when:
        Collection<Site> sites = new SimCache().getAllSites()

        for (Site site : sites) {
            if ("gazelleSts".equals(site.getName())) {
                gazelleStsSite = site;
                System.out.println("Found site: <" + site.getName() +"> endpoint: " + site.getEndpoint(TransactionType.STS, true, false))
            }
        }
        System.out.println("----" + gazelleStsSite.getEndpoint(TransactionType.STS, true, false))

        then:
        gazelleStsSite!=null
    }

    def 'set Truststore'() {
        when:
        URL trustStoreURL = getClass().getResource("/war/toolkitx/environment/default/cacerts")
        File trustStoreFile = new File(trustStoreURL.getFile())

        System.out.println(trustStoreFile)

        System.setProperty("javax.net.ssl.trustStore",trustStoreFile.toString())
        System.setProperty("javax.net.ssl.trustStorePassword","changeit")
        System.setProperty("javax.net.ssl.trustStoreType","JKS")

        then:
        trustStoreFile.toString().equals(System.getProperty("javax.net.ssl.trustStore"))

    }

    def 'Get SAML Assertion'(){
        when:
        String siteName = gazelleStsSite.getName()
        TestInstance testId = new TestInstance("GazelleSts")
        List<String> sections = new ArrayList<>()
        sections.add("samlassertion-issue")
        Map<String, String> params = new HashMap<>()
        boolean stopOnFirstError = true

        and: 'Run samlassertion-issue'
//        No need to set this: session.setTls(true)
        // Main parameter for the TLS is the isTls flag in the runTest method
        List<Result> results = api.runTest(testSession, siteName, true, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }




}
