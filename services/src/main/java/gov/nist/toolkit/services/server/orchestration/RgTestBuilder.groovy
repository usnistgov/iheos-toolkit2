package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actorfactory.SimCache
import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.Pid
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.client.RgOrchestrationManagerRequest
import gov.nist.toolkit.services.client.RgOrchestrationResponse
import gov.nist.toolkit.services.server.RawResponseBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import groovy.transform.TypeChecked

/**
 * Build environment for testing Responding Gateway SUT.
 */
@TypeChecked
class RgTestBuilder {
    Session session
    RgOrchestrationManagerRequest request
    Pid pid
    ToolkitApi api
    Util util

    public RgTestBuilder(ToolkitApi api, Session session, RgOrchestrationManagerRequest request) {
        this.api = api
        this.session = session
        this.request = request
        this.util = new Util(api)
    }

    RawResponse buildTestEnvironment() {
        try {
            new SimDb().getSimIdsForUser(request.userName).each { SimId simId -> api.deleteSimulator(simId) }

            String rrId = 'rr'

            if (request.useExposedRR) {
                if (request.useSimAsSUT) {
                    // RG sim defines site
                } else {  // has real SUT
                    // RR and RG in same site - part of SUT
                }
            } else {  // external RR
                if (request.useSimAsSUT) {
                    // ignore external RR setting.  Use RG sim
                } else {  // has real SUT
                    // need site holding RR
                }
            }

            SimId simId = new SimId(request.userName, rrId, ActorType.REPOSITORY_REGISTRY.name, request.environmentName)
            SimulatorConfig simConfig = api.createSimulator(simId).getConfig(0)

            SimCache.addToSession(Installation.defaultSessionName(), simConfig)

            pid = session.allocateNewPid()

            // register patient ids with registry
            util.submit(request.userName, simConfig.id, new TestInstance("15807"), 'onedoc1', pid, null)

            RgOrchestrationResponse response = new RgOrchestrationResponse();
            response.setPid(pid)
            response.setRegrepConfig(simConfig)

            return response;
        }
        catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }



}

