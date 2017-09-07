package gov.nist.toolkit.toolkitApi;

/**
 *
 */
public class FhirServer extends AbstractActor implements IFhirServer {
    @Override
    public boolean isFhir() {
        return true;
    }

}
