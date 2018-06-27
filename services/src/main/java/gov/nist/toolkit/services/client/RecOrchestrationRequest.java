package gov.nist.toolkit.services.client;

import gov.nist.toolkit.actortransaction.shared.ActorOption;

/**
 *
 */
public class RecOrchestrationRequest extends AbstractOrchestrationRequest {
    private boolean useExistingSimulator;

    public RecOrchestrationRequest() {}

    public RecOrchestrationRequest(ActorOption actorOption) {
        setActorOption(actorOption);
    }

    public boolean isUseExistingSimulator() {
        return useExistingSimulator;
    }

    public void setUseExistingSimulator(boolean useExistingSimulator) {
        this.useExistingSimulator = useExistingSimulator;
    }

}
