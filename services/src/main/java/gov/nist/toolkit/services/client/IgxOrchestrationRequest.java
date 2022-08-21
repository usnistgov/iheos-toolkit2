package gov.nist.toolkit.services.client;

/**
 *
 */
public class IgxOrchestrationRequest extends AbstractOrchestrationRequest {
    private boolean includeLinkedIGX;

    public IgxOrchestrationRequest() {}

    public boolean isIncludeLinkedIGX() {
        return includeLinkedIGX;
    }

    public void setIncludeLinkedIGX(boolean includeLinkedIG) {
        this.includeLinkedIGX = includeLinkedIG;
    }
}
