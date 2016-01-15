package gov.nist.toolkit.services.server.orchestration
import gov.nist.toolkit.actorfactory.PatientIdentityFeedServlet
import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.SimulatorProperties
import gov.nist.toolkit.actorfactory.client.PidBuilder
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
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

    def setupSpec() {
        new PatientIdentityFeedServlet().initPatientIdentityFeed()
        Installation.installation().overrideToolkitPort('8888')
    }

    def setup() {
        SimDb.deleteAllSims()
//        SimDb.deleteSims(new SimDb().getSimIdsForUser(user))
    }

    def 'create sims for user'() {
        when:
        def configs = IgTestBuilder.build(api, 1, user, environmentName, PidBuilder.createPid(patientId), true)

        then:
        configs
        configs.size() == 2

        when:
        def igConfig = configs.get(0)
        println igConfig
        SimulatorConfigElement rgs = igConfig.getConfigEle(SimulatorProperties.respondingGateways)
        println 'LINKED RESPONDING GATEWAYS'
        println rgs

        then:
        true
    }
}
