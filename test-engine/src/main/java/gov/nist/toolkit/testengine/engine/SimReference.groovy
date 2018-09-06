package gov.nist.toolkit.testengine.engine

import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.simcommon.client.SimId

class SimReference {
    SimId simId
    TransactionType transactionType
    ActorType actorType

    SimReference(SimId _simId, TransactionType _transactionType) {
        simId = _simId
        transactionType = _transactionType
    }
    SimReference(SimId _simId, TransactionType _transactionType, ActorType _actorType) {
       this(_simId, _transactionType)
       actorType = _actorType
    }

    String toString() {
        "[SimReference ${simId}: ${actorType}: ${transactionType}]"
    }
}
