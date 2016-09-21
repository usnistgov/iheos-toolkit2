package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.PifType
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.client.RegOrchestrationRequest
import gov.nist.toolkit.services.client.RegOrchestrationResponse
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import groovy.transform.TypeChecked
/**
 *
 */
@TypeChecked
class RegOrchestrationBuilder {
    ToolkitApi api
    private Session session
    private RegOrchestrationRequest request
    private Util util

    public RegOrchestrationBuilder(ToolkitApi api, Session session, RegOrchestrationRequest request) {
        this.api = api
        this.request = request
        this.session = session
        this.util = new Util(api)
    }

    RawResponse buildTestEnvironment() {
        RegOrchestrationResponse response = new RegOrchestrationResponse()

        boolean initialize = false;

        File orchestrationPropFile = Installation.installation().orchestrationPropertiesFile(request.userName, ActorType.REPOSITORY.shortName)
        Properties orchProps = new Properties()
        if (orchestrationPropFile.exists())
            orchProps.load(new FileInputStream(orchestrationPropFile))

        Pid pid

        if (request.isUseExistingSimulator() && orchProps.getProperty("pid") != null) {
            pid = PidBuilder.createPid(orchProps.getProperty("pid"))
        } else {
            orchProps.clear()
            pid  = session.allocateNewPid()
            orchProps.setProperty("pid", pid.asString())
            orchestrationPropFile.parentFile.mkdirs()
            orchProps.store(new FileOutputStream(orchestrationPropFile), null)
            initialize = true;
        }
        response.setPid(pid)

        if (initialize) {
            // register patient id with registry
            if (request.pifType == PifType.V2) {
                try {
                    util.submit(request.userName, request.registrySut, new TestInstance("15817"), 'pif', pid, null)
                }
                catch (Exception e) {
                    response.setMessage("V2 Patient Identity Feed to " + request.registrySut.name + " failed");
                }
            }
            try {
                // Initialize Registry for Stored Query testing
                Map<String, String> parms = new HashMap<>();
                parms.put('$patientid$', pid.toString())
                util.submit(request.userName, request.registrySut, new TestInstance("12346"), parms)
            }
            catch (Exception e) {
                response.setMessage("V2 Patient Identity Feed to " + request.registrySut.name + " failed");
            }
        }

        response.addOrchestrationTest(new TestInstance("12346"));

        return response
    }


}
