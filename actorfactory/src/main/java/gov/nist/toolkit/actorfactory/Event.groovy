package gov.nist.toolkit.actorfactory

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType

/**
 *
 */
class Event {
    String actor
    String transaction
    String event

    Event(SimDb simDb) {
        actor = simDb.actor
        transaction = simDb.transaction
        event = simDb.event
    }

    Event(ActorType actorType, TransactionType transactionType, String event) {
        actor = actorType.shortName
        transaction = transactionType.shortName
        this.event = event
    }

    Event(String _path) {
        (actor, transaction, event) = _path.split('/')
    }

    Event(File _file) {
        def path = _file.toString().split('/')
        event = path[-1]
        transaction = path[-2]
        actor = path[-3]
    }

    String asPath() {
        "${actor}/${transaction}/${event}"
    }

    SimDb asSimDb(SimId simId) {
        SimDb simDb = new SimDb(simId, actor, transaction)
        simDb.event = event
        return simDb
    }

}
