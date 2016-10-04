package gov.nist.toolkit.services.client;

/**
 *
 */
public class IgOrchestrationRequest extends AbstractOrchestrationRequest {
    private boolean includeLinkedIG;

    public IgOrchestrationRequest() {}

    public boolean isIncludeLinkedIG() {
        return includeLinkedIG;
    }

    public void setIncludeLinkedIG(boolean includeLinkedIG) {
        this.includeLinkedIG = includeLinkedIG;
    }
}
