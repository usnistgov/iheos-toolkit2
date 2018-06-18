package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actortransaction.client.ActorOption
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.sitemanagement.Sites
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.testkitutilities.OrchestrationDefinition
import gov.nist.toolkit.testkitutilities.TestDefinition
import gov.nist.toolkit.testkitutilities.TestKitSearchPath
import gov.nist.toolkit.testkitutilities.client.SectionDefinitionDAO
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import groovy.transform.TypeChecked

@TypeChecked
class OrchEngine {
    OrchestrationDefinition orchestrationDefinition
    Util util
    TestSession testSession
    TestInstance testInstance

    def buildGeneralizedOrchestration(Session session, String environment, TestSession testSession, ActorOption type) {
        ToolkitApi api = getToolkitApi(session)
        this.testSession = testSession
        TestKitSearchPath testKitSearchPath = new TestKitSearchPath(environment, testSession)
        TestDefinition testDefinition = testKitSearchPath.getTestDefinition(type.label)
        if (testDefinition instanceof OrchestrationDefinition)
            orchestrationDefinition = (OrchestrationDefinition) testDefinition
        else
            throw new ToolkitRuntimeException('oops')
        util = new Util(api)
        testInstance = new TestInstance(type.label)
    }

    def run(SiteSpec siteSpec) {
        for (SectionDefinitionDAO d : orchestrationDefinition.getSections()) {
            run(d, siteSpec)
            if (siteSpec.name == Sites.FAKE_SITE_NAME && util.transactionSettings?.siteSpec)
                siteSpec = util.transactionSettings.siteSpec
        }
    }

    def run(SectionDefinitionDAO section, SiteSpec siteSpec) {
        Map<String, String> parameters = new HashMap<>()
        util.submit(testSession.value, siteSpec, testInstance, section.sectionName, parameters)
    }

    private ToolkitApi getToolkitApi(Session session) {
        (Installation.instance().warHome()) ?
                ToolkitApi.forNormalUse(session) :
                ToolkitApi.forInternalUse()
    }
}
