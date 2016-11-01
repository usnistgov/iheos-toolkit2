package gov.nist.toolkit.services.client;

/**
 *
 */
public class RegOrchestrationRequest extends AbstractOrchestrationRequest {
    private PifType pifType;

    public RegOrchestrationRequest() {
    }

    public PifType getPifType() {
        return pifType;
    }

    public void setPifType(PifType pifType) {
        this.pifType = pifType;
    }

}
