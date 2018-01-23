package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actorfactory.PatientIdentityFeedServlet
import gov.nist.toolkit.installation.server.ExternalCacheManager
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.services.client.IgOrchestrationRequest
import gov.nist.toolkit.services.client.IgOrchestrationResponse
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.simcommon.server.SimDb
import spock.lang.Shared
import spock.lang.Specification

/**
 * External cache set to match running toolkit in dev mode
 *
 * The class name is *Spec so it is not run automatically by mvn test
 */
class IgTestBuilderSpec extends Specification {
    @Shared ToolkitApi api = ToolkitApi.forServiceUse()
    @Shared String user = 'igtestbuilder'
    @Shared String environmentName = 'default'
    @Shared String patientId = 'BR14^^^&1.2.360&ISO'
    @Shared TestSession testSession = new TestSession(user)

    def setupSpec() {
        new PatientIdentityFeedServlet().initPatientIdentityFeed()
        Installation.instance().overrideToolkitPort('8888')  // match toolkit
        ExternalCacheManager.reinitialize(new File('/Users/bill/tmp/toolkit2a'))  // match toolkit
    }

    def setup() {
        new SimDb().deleteAllSims(testSession)
//        SimDb.deleteSims(new SimDb().getSimIdsForUser(testSession))
    }

    def 'create sims for user'() {
        when:
        def request = new IgOrchestrationRequest()
        request.environmentName = environmentName
        request.includeLinkedIG = true
        request.testSession = testSession
        IgOrchestrationBuilder builder = new IgOrchestrationBuilder(api, api.session, request)

        RawResponse rawResponse = builder.buildTestEnvironment()

        then:
        rawResponse instanceof IgOrchestrationResponse

        when:
        IgOrchestrationResponse resp = (IgOrchestrationResponse) rawResponse
        println resp.simulatorConfigs.get(0)

        then:
        true
    }
}
