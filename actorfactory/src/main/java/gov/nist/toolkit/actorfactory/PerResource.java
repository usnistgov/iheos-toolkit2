package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;

import java.io.File;

/**
 * Callback event for scanning SimDb
 */
public interface PerResource {
    /**
     *
     * @param simId - required
     * @param actorType
     * @param transactionType
     */
    public void index(SimId simId, ActorType actorType, TransactionType transactionType, File eventDir, File resourceFile);
}
