package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType

/**
 * This contains details about where the resource is stored and
 * what actor/transaction/event it belongs to.
 */
class SimResource {
    String actor
    String transaction
    String event
    String filename

    SimResource(SimDb simDb) {
        actor = simDb.actor
        transaction = simDb.transaction
        event = simDb.event
    }

    SimResource(ActorType actorType, TransactionType transactionType, String event, String filename) {
        actor = actorType ? actorType.shortName : ResDb.BASE_TYPE
        transaction = transactionType ? transactionType.shortName : ResDb.STORE_TRANSACTION
        this.event = event
        this.filename = filename
    }

    SimResource(String _path) {
        (actor, transaction, event, filename) = _path.split('/')
    }

    SimResource(File _file) {
        def path = _file.toString().split('/')
        filename = path[-1]
        event = path[-2]
        transaction = path[-3]
        actor = path[-4]
    }

    String asPath() {
        File file = new File(filename)
        "${actor}/${transaction}/${event}/${file.name}"
    }

    SimDb asSimDb(SimId simId) {
        SimDb simDb = new SimDb(simId, actor, transaction)
        simDb.event = event
        return simDb
    }

}
