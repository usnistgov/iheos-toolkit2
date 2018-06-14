package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import groovy.transform.TypeChecked

@TypeChecked
class OrchEngine {

    def buildGeneralizedOrchestration(Session session, OrchestrationType type) {
        ToolkitApi api = getToolkitApi(session)
    }

    private ToolkitApi getToolkitApi(Session session) {
        (Installation.instance().warHome()) ?
                ToolkitApi.forNormalUse(session) :
                ToolkitApi.forInternalUse()
    }
}
