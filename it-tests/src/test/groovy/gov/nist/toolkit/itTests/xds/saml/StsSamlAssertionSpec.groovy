package gov.nist.toolkit.itTests.xds.saml

import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.simcommon.server.SimCache
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Ignore
import spock.lang.Shared

/**
 * Test the STS as provided by Gazelle using Toolkit's HttpTransaction step instruction
 * References:
 * For WSSE, See https://docs.oasis-open.org/wss/v1.1/wss-v1.1-spec-os-SOAPMessageSecurity.pdf
 * For Gazelle STS, See https://github.com/usnistgov/iheos-toolkit2/wiki/SAML-Validation-against-Gazele
 * And https://gazelle.ihe.net/content/sts
 */
class StsSamlAssertionSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi

    @Shared String patientId = 'SKB1^^^&1.2.960&ISO'

    @Shared TestSession testSession = new TestSession(prefixNonce('sunil'))
    @Shared String reg = testSession.value + '__reg'
    @Shared SimId simId = SimIdFactory.simIdBuilder(reg)
    @Shared Site gazelleStsSite
    @Shared Session tkSession
    @Shared SimConfig rrConfig = null

    @Shared String gazelleSiteName = "GazelleSts" // Two choices: Use "GazelleSts" or "GazelleSts-bad"

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
        String transName = "sts";
        String endpoint = gazelleStsEndpoint
        boolean isSecure = true;
        boolean isAsync = false;

        gazelleStsSite = new Site(gazelleSiteName, TestSession.DEFAULT_TEST_SESSION)
        gazelleStsSite.addTransaction(transName, endpoint, isSecure, isAsync);

        // Adding a site dynamically doesn't work. Must use Actors.xml file.
//       .allSites.getAllSites().add(gazelleStsSite)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
//        server.stop()
//        ListenerFactory.terminateAll()
    }

    def 'Make sure the GazelleSts site can be retrieved from the Actor file'() {
        when:
        Collection<Site> sites = new SimCache().getAllSites(TestSession.DEFAULT_TEST_SESSION)

        for (Site site : sites) {
            if (gazelleSiteName.equals(site.getName())) {
                gazelleStsSite = site;
                System.out.println("Found site: <" + site.getName() +"> endpoint: " + site.getEndpoint(TransactionType.STS, true, false))
            }
        }
        System.out.println("Endpoint is ----" + gazelleStsSite.getEndpoint(TransactionType.STS, true, false))

        then:
        gazelleStsSite!=null
    }

    def 'set Truststore'() {
        when:
        URL trustStoreURL = getClass().getResource("/war/toolkitx/environment/default/gazelle_sts_cert_truststore.jks")
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
        TestInstance testId = new TestInstance("GazelleSts", TestSession.DEFAULT_TEST_SESSION)
        List<String> sections = new ArrayList<>()
        sections.add("samlassertion-issue")
        Map<String, String> params = new HashMap<>()
        params.put('$saml-username$',"Xuagood")
        boolean stopOnFirstError = true

        and: 'Run samlassertion-issue'
//        No need to set this: session.setTls(true)
        // Main parameter for the TLS is the isTls flag in the runTest method
        List<Result> results = api.runTest(TestSession.DEFAULT_TEST_SESSION.value, siteName, true, testId, sections, params, stopOnFirstError)

        then:
        results.size() == 1
        results.get(0).passed()
    }

    def 'Validate SAML Assertion'() {
        when:
        String siteName = gazelleStsSite.getName()
        TestInstance testId = new TestInstance("GazelleSts", TestSession.DEFAULT_TEST_SESSION)
        List<String> sections = new ArrayList<>()
        sections.add("samlassertion-validate")
        Map<String, String> params = new HashMap<>()
        boolean stopOnFirstError = true

        and: 'Run samlassertion-validate'
//        For it-tests, there is no need to set this: session.setTls(true) BUT it is required for the non-api type main code.
        // Main parameter for the TLS is the isTls flag in the runTest method
        List<Result> results = api.runTest(TestSession.DEFAULT_TEST_SESSION.value, siteName, true, testId, sections, params, stopOnFirstError)

        then:
        results.size() == 1
        results.get(0).passed()
    }


    /* --------------------------------------------------------------------- */
    /* headless */
    /* --------------------------------------------------------------------- */

    @Ignore
    def 'headless0 - Create sim'() {
        when:
        // Begin headless
        rrConfig = spi.create(
                'rr',
                testSession.value,
                SimulatorActorType.REPOSITORY_REGISTRY,
                'default')

        then:
        true
    }

    @Ignore
    def 'headless1 - Submit Pid transaction to Registry simulator'() {
            when:
            String siteName = testSession + '__rr'
            TestInstance testId = new TestInstance("15804",testSession)
            List<String> sections = new ArrayList<>()
            sections.add("section")
            Map<String, String> params = new HashMap<>()
            params.put('$patientid$', patientId)
            boolean stopOnFirstError = true

            and: 'Run pid transaction test'
            List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

            then:
            true
            results.size() == 1
            results.get(0).passed()
    }


    @Ignore
    def 'headless1.1 - Run SQ initialization'() {
        when:
        String siteName =  testSession + '__rr'
        TestInstance testId = new TestInstance("tc:Initialize_for_Stored_Query",testSession)
        List<String> sections = new ArrayList<>()
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        results.size() == 1
        results.get(0).passed()
    }

    @Ignore
    def 'headless1.2 Setup test with submissions'() {
        when:
        String siteName = testSession + '__rr'
        TestInstance testId = new TestInstance("15816",testSession)
        List<String> sections = ['Register_Stable', 'PnR']
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        params.put('$repuid$', repUid)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }

    @Ignore
    def 'headless2 - Run until killed'() {
        when:


        // Start requiring SAML from this point on after initial data load is completed.
        rrConfig.setProperty(SimulatorProperties.requiresStsSaml, true)
        spi.update(rrConfig)

        // Loop forever until killed
        while (1) {
            sleep(20000)
        }

        then:
        true

    }

    @Ignore
    def 'noOp'() {
    }

}
