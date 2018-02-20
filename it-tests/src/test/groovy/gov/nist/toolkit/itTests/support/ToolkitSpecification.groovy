package gov.nist.toolkit.itTests.support

import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.grizzlySupport.GrizzlyController
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.server.TestSessionFactory
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.results.client.*
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.session.server.serviceManager.TestSessionServiceManager
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimId
import gov.nist.toolkit.utilities.io.Io
import org.junit.Rule
import org.junit.rules.TestName
import spock.lang.Shared
import spock.lang.Specification
/**
 *
 */

class ToolkitSpecification extends Specification {
    @Rule TestName name = new TestName()
    // these are usable by the specification that extend this class
    @Shared GrizzlyController server = null
    @Shared ToolkitApi api
    @Shared Session session
    @Shared String remoteToolkitPort = null
    @Shared boolean runASingleTestInIde = false;

    def setupSpec() {  // there can be multiple setupSpec() fixture methods - they all get run
        Installation.instance().setServletContextName("");
        session = UnitTestEnvironmentManager.setupLocalToolkit()
        api = UnitTestEnvironmentManager.localToolkitApi()

        Installation.setTestRunning(true)
        cleanupDir()
    }

    def setup() {
        println 'Running method: ' + name.methodName
    }

    // clean out simdb, testlogcache, and actors
    def cleanupDir() {
        if (runASingleTestInIde) {
            TestSessionServiceManager.INSTANCE.inTestLogs().each { String testSessionName ->
                Io.delete(Installation.instance().testLogCache(new TestSession(testSessionName)))
            }
            Installation.instance().simDbFile(TestSession.DEFAULT_TEST_SESSION).mkdirs()
            Installation.instance().actorsDir(TestSession.DEFAULT_TEST_SESSION).mkdirs()
            Installation.instance().testLogCache(TestSession.DEFAULT_TEST_SESSION).mkdirs()
        }
    }

    def startGrizzly(String port) {
        remoteToolkitPort = port
        if (runASingleTestInIde) {
            server = new GrizzlyController()
            server.start(remoteToolkitPort);
            server.withToolkit()
        }
        Installation.instance().overrideToolkitPort(remoteToolkitPort)  // ignore toolkit.properties
    }

    def startGrizzlyWithFhir(String port) {
        remoteToolkitPort = port
        if (runASingleTestInIde) {
            server = new GrizzlyController()
            server.start(remoteToolkitPort);
            server.withToolkit()
            server.withFhirServlet()
        }
        Installation.instance().overrideToolkitPort(remoteToolkitPort)  // ignore toolkit.properties
    }

    static String prefixNonce(String name) {
        if ("default".equals(name))
            throw new Exception("Default session cannot be prefixed with a nonce.")

        return name + TestSessionFactory.nonce()
    }


    SimulatorBuilder getSimulatorApi(String remoteToolkitPort) {
        String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
        new SimulatorBuilder(urlRoot)
    }

    SimulatorBuilder getSimulatorApi(String host, String remoteToolkitPort) {
        String urlRoot = String.format("http://%s:%s/xdstools2", host, remoteToolkitPort)
        new SimulatorBuilder(urlRoot)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        if (runASingleTestInIde) {
            if (server) {
                server.stop()
                server = null
            }
            ListenerFactory.terminateAll()

            assert TestSessionServiceManager.INSTANCE.isConsistant()
        }
    }

    def initializeRegistryWithPatientId(String testSession, SimId simId, Pid pid) {
        TestInstance testId = new TestInstance("15804")
        List<String> sections = new ArrayList<>()
        sections.add("section")
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', pid.toString())
        boolean stopOnFirstError = true

        List<Result> results = api.runTest(testSession, simId.fullId, testId, sections, params, stopOnFirstError)

        assert results.size() == 1
        assert results.get(0).passed()
    }

    TestLogs initializeRepository(String testSession, SimId simId, Pid pid, TestInstance testInstance) {
        List<String> sections = new ArrayList<>()
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', pid.toString())
        boolean stopOnFirstError = true

        List<Result> results = api.runTest(testSession, simId.fullId, testInstance, sections, params, stopOnFirstError)

        TestLogs testLogs = api.getTestLogs(testInstance)

        assert testLogs
        assert results.size() == 1
        assert results.get(0).passed()
        return testLogs
    }

    boolean assertionsContain(List<Result> results, String target) {
        boolean found = false

        results.each { Result result ->
            result.assertions.each { AssertionResults ars->
                ars.assertions.each { AssertionResult ar ->
                    if (ar.assertion.contains(target)) found = true
                }
            }
        }

        return found
    }

}
