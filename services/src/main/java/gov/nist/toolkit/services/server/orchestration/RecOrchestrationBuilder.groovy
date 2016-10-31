package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.client.RecOrchestrationRequest
import gov.nist.toolkit.services.client.RecOrchestrationResponse
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import groovy.transform.TypeChecked
/**
 *
 */
@TypeChecked
class RecOrchestrationBuilder {
    ToolkitApi api
    private Session session
    private RecOrchestrationRequest request
    private Util util

    public RecOrchestrationBuilder(ToolkitApi api, Session session, RecOrchestrationRequest request) {
        this.api = api
        this.request = request
        this.session = session
        this.util = new Util(api)
    }

    RawResponse buildTestEnvironment() {
        RecOrchestrationResponse response = new RecOrchestrationResponse()
        Map<String, TestInstanceManager> pidNameMap = new HashMap<>();

        boolean forceNewPatientIds = !request.isUseExistingState()

        OrchestrationProperties orchProps = new OrchestrationProperties(session, request.userName, ActorType.DOCUMENT_RECIPIENT, pidNameMap.keySet(), forceNewPatientIds)

        Pid registerPid
        if (forceNewPatientIds) {
            registerPid = session.allocateNewPid()
            orchProps.setProperty("registerPid", registerPid.asString())
        } else {
            registerPid = PidBuilder.createPid(orchProps.getProperty("registerPid"))
        }

        response.setRegisterPid(registerPid)

        orchProps.save();

        return response
    }



}
