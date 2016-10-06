package gov.nist.toolkit.services.client;

/**
 *
 */
public class RegOrchestrationRequest extends AbstractOrchestrationRequest {
    private PifType pifType;
    private boolean useExistingSimulator = true;

    public RegOrchestrationRequest() {
    }

    public PifType getPifType() {
        return pifType;
    }

    public void setPifType(PifType pifType) {
        this.pifType = pifType;
    }

    public boolean isUseExistingSimulator() {
        return useExistingSimulator;
    }

    public void setUseExistingSimulator(boolean useExistingSimulator) {
        this.useExistingSimulator = useExistingSimulator;
    }
}
