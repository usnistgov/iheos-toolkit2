package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.services.client.AbstractOrchestrationRequest
import gov.nist.toolkit.session.server.Session
import groovy.transform.TypeChecked

@TypeChecked
abstract class AbstractOrchestrationBuilder {

    AbstractOrchestrationBuilder(Session session, AbstractOrchestrationRequest request) {
        session.setTls(request.isUseTls())
    }
}
