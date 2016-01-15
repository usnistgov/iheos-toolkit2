package gov.nist.toolkit.services.server.orchestration
import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.services.client.IgOrchestationManagerRequest
import gov.nist.toolkit.services.client.IgOrchestrationResponse
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.server.RawResponseBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import groovy.transform.TypeChecked
/**
 *
 */
@TypeChecked
class OrchestrationManager {

    public RawResponse buildIgTestEnvironment(IgOrchestationManagerRequest request) {
        try {
            ToolkitApi api = ToolkitApi.forInternalUse()
            SimDb.deleteSims(new SimDb().getSimIdsForUser(request.userName))
            List<SimulatorConfig> allConfigs = IgTestBuilder.build(api, 1, request.userName, request.environmentName, request.patientId, request.includeLinkedIG)
            // remove IG config
            if (request.includeLinkedIG)
                allConfigs.remove(0)
            return new IgOrchestrationResponse(allConfigs)
        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }

}
