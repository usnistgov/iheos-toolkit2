package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import groovy.transform.TypeChecked

/**
 *
 */
@TypeChecked
class Util {
    ToolkitApi api

    public Util(ToolkitApi api) {
        this.api = api
        api.withEnvironment(api.session.currentEnvironment);
    }

    public void submit(TestSession testSession, SiteSpec site, TestInstance testId, String section, Pid patientId, String home) {
        // load the reg/rep with two documents
        List<String> sections = [ section ]
        Map<String, String> qparams = new HashMap<>()
        qparams.put('$patientid$', patientId.asString())
        qparams.put('$testdata_home$', home);

        List<Result> results = api.runTest(testSession.value, site, testId, sections, qparams, true)
        if (!results.get(0).passed())
            throw new Exception(results.get(0).toString())
    }

    public void submit(String userName, SiteSpec site, TestInstance testId, Pid patientId) {
        // load the reg/rep with two documents
        List<String> sections = null
        Map<String, String> qparams = new HashMap<>()
        qparams.put('$patientid$', patientId.asString())

        List<Result> results = api.runTest(userName, site, testId, sections, qparams, true)
        if (!results.get(0).passed())
            throw new Exception(results.get(0).toString())
    }

    public void submit(String userName, SiteSpec site, TestInstance testId, Pid patientId, String home) {
        // load the reg/rep with two documents
        List<String> sections = null
        Map<String, String> qparams = new HashMap<>()
        qparams.put('$patientid$', patientId.asString())
        qparams.put('$testdata_home$', home);

        List<Result> results = api.runTest(userName, site, testId, sections, qparams, true)
        if (!results.get(0).passed())
            throw new Exception(results.get(0).toString())
    }

    public void submit(String userName, SiteSpec site, TestInstance testId, String section, Map<String, String> parameters) {
        // load the reg/rep with two documents
        List<String> sections = [ section ]
        List<Result> results = api.runTest(userName, site, testId, sections, parameters, true)
        if (!results.get(0).passed())
            throw new Exception(results.get(0).toString())
    }

    public void submit(String userName, SiteSpec site, TestInstance testId, Map<String, String> parameters) {
        // load the reg/rep with two documents
        List<Result> results = api.runTest(userName, site, testId, null, parameters, true)
        if (!results.get(0).passed())
            throw new Exception(results.get(0).toString())
    }

    public void submit(TestSession testSession, SiteSpec site, TestInstance testId) {
       Map<String, String> parameters = new HashMap<>();
        List<Result> results = api.runTest(testSession.value, site, testId, null, parameters, true)
        if (!results.get(0).passed())
            throw new Exception(results.get(0).toString())
    }
}
