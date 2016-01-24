package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.services.client.IgOrchestationManagerRequest
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.server.RawResponseBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import groovy.transform.TypeChecked
/**
 *
 */
@TypeChecked
class OrchestrationManager {

    public RawResponse buildIgTestEnvironment(Session session, IgOrchestationManagerRequest request) {
        try {
            ToolkitApi api
            if(Installation.installation().warHome()) {
                api = ToolkitApi.forNormalUse(session)
            } else {
                api = ToolkitApi.forInternalUse()
            }
            IgTestBuilder builder = new IgTestBuilder(api, session, request)
            return builder.buildTestEnvironment()
        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }


}
