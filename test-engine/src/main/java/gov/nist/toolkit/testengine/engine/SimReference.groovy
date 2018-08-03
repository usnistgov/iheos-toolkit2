package gov.nist.toolkit.testengine.engine

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.simcommon.client.SimId

class SimReference {
    SimId simId
    TransactionType transactionType

    SimReference(SimId _simId, TransactionType _transactionType) {
        simId = _simId
        transactionType = _transactionType
    }

    String toString() {
        "[SimReference ${simId}: ${transactionType}]"
    }
}
