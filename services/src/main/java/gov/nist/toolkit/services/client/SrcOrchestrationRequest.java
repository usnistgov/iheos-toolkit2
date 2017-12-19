package gov.nist.toolkit.services.client;

import gov.nist.toolkit.actortransaction.client.ActorOption;

/**
 *
 */
public class SrcOrchestrationRequest extends AbstractOrchestrationRequest {

    public SrcOrchestrationRequest() {}

    public SrcOrchestrationRequest(ActorOption actorOption) { setActorOption(actorOption);}

}
