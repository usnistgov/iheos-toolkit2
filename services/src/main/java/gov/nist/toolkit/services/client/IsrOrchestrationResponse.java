package gov.nist.toolkit.services.client;


/**
 *
 */
public class IsrOrchestrationResponse extends OrchestratedRegSiteResponse {

    @Override
    public boolean isExternalStart() {
        return true;
    }

}
