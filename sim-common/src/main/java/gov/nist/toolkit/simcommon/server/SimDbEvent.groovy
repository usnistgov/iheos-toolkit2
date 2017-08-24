package gov.nist.toolkit.simcommon.server

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.simcommon.client.SimId

/**
 * See SimDbEvents for how this is used
 */
class SimDbEvent {
    SimId simId
    String actor
    String trans
    String eventId // These sort in time order

    SimDbEvent(SimId simId, String actor, String trans, String eventId) {
        this.simId = simId
        this.actor = actor
        this.trans = trans
        this.eventId = eventId
    }

    SimDb open() {
        return SimDb.open(this)
    }

    ActorType getActorType() { return ActorType.findActor(actor) }

    TransactionType getTransactionType() { return TransactionType.find(trans) }

    File getRequestBodyFile() {
        return open().getRequestBodyFile()
    }

    File getRequestHeaderFile() {
        return open().getRequestHeaderFile()
    }

    File getResponseBodyFile() {
        return open().getResponseBodyFile()
    }

    File getResponseHeaderFile() {
        return open().getResponseHdrFile()
    }

    byte[] getRequestBody() {
        return getRequestBodyFile().bytes
    }

    String getRequestHeader() {
        return getRequestHeaderFile().text
    }

    byte[] getResponseBody() {
        return getResponseBodyFile().bytes
    }

    String getResponseHeader() {
        return getResponseHeaderFile().text
    }
}
