package gov.nist.toolkit.services.server;

import gov.nist.toolkit.simcommon.client.NoSimException;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.fhir.simulators.servlet.SimServlet;
import gov.nist.toolkit.fhir.simulators.sim.rep.RepIndex;
import gov.nist.toolkit.fhir.simulators.support.StoredDocument;

import java.io.IOException;

/**
 *
 *
 */
public class RepositorySimApi {
    SimId simId;

    public RepositorySimApi(SimId simId) {
        this.simId = simId;
    }

    private RepIndex repIndex() throws IOException, NoSimException {
        return SimServlet.getRepIndex(simId);
    }

    public StoredDocument getDocument(String uniqueId) throws IOException, NoSimException {
        return repIndex().getDocumentCollection().getStoredDocument(uniqueId);
    }

}
