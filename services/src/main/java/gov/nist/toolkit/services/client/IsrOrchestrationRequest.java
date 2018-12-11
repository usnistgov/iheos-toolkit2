package gov.nist.toolkit.services.client;

import gov.nist.toolkit.actortransaction.shared.ActorOption;

/**
 *
 */
public class IsrOrchestrationRequest extends AbstractOrchestrationRequest {

    public IsrOrchestrationRequest() {}

    public IsrOrchestrationRequest(ActorOption actorOption) { setActorOption(actorOption);}

}
