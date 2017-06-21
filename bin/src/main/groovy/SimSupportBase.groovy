

import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.results.client.TestLogs
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimId
import groovy.transform.TypeChecked

/**
 *
 */
@TypeChecked
class SimSupportBase {

    static SimulatorBuilder getSimulatorApi(String remoteToolkitPort) {
        String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
        new SimulatorBuilder(urlRoot)
    }

    static SimulatorBuilder getSimulatorApi(String host, String remoteToolkitPort, String toolkitName) {
        String urlRoot = String.format("http://%s:%s/%s", host, remoteToolkitPort, toolkitName)
        new SimulatorBuilder(urlRoot)
    }

    def initializeRegistryWithPatientId(String testSession, SimId simId, Pid pid) {
        TestInstance testId = new TestInstance("15804")
        List<String> sections = new ArrayList<>()
        sections.add("section")
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', pid.toString())
        boolean stopOnFirstError = true

        ToolkitApi api = UnitTestEnvironmentManager.localToolkitApi()

        List<Result> results = api.runTest(testSession, simId.fullId, testId, sections, params, stopOnFirstError)

        assert results.size() == 1
        assert results.get(0).passed()
    }

    TestLogs initializeRepository(String testSession, SimId simId, Pid pid, TestInstance testInstance) {
        List<String> sections = new ArrayList<>()
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', pid.toString())
        boolean stopOnFirstError = true

        ToolkitApi api = UnitTestEnvironmentManager.localToolkitApi()

        List<Result> results = api.runTest(testSession, simId.fullId, testInstance, sections, params, stopOnFirstError)

        TestLogs testLogs = api.getTestLogs(testInstance)

        assert testLogs
        assert results.size() == 1
        assert results.get(0).passed()
        return testLogs
    }

}
