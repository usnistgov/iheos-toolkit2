package gov.nist.toolkit.fhirServer.config;

import gov.nist.toolkit.actorfactory.client.SimId;

/**
 *
 */
public class SimContext {
    private SimId simId;

    public SimContext(SimId _simId) {
        simId = _simId;
    }

    public SimId getSimId() {
        return simId;
    }

}
