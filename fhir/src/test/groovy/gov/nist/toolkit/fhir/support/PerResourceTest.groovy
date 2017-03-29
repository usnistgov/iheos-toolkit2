package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.actorfactory.PerResource
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.testSupport.InitEC
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import spock.lang.Specification
/**
 *
 */
class PerResourceTest extends Specification {
    SimId simId = new SimId('fhir')
    def resourceCount = 0

    def setup() {
        InitEC.init()
        new ResDb(simId).delete()  // delete simuilator
        new ResDb().mkSim(simId)   // build new one
    }

    def 'scan resources - single object'() {
        when:
        ResDb resDb = new ResDb(simId, ResDb.BASE_TYPE, ResDb.STORE_TRANSACTION)
        resDb.storeNewResource('simple', buildResource('foo', 'patient'))
        resDb.perResource(null, null, new CountingEventHandler())

        then:
        resourceCount == 1
    }

    def 'scan resources - two objects'() {
        when:
        ResDb resDb = new ResDb(simId, ResDb.BASE_TYPE, ResDb.STORE_TRANSACTION)
        resDb.storeNewResource('simple', buildResource('foo', 'patient'))
        resDb.storeNewResource('simple', buildResource('bar', 'patient'))
        resDb.perResource(null, null, new CountingEventHandler())

        then:
        resourceCount == 2
    }


    class CountingEventHandler implements PerResource {

        @Override
        void resource(SimId simId, ActorType actorType, TransactionType transactionType, File eventDir, File resourceFile) {
            resourceCount++
        }
    }

    def buildResource(String id, String type) {
        "{ id: ${id}  resourceType: ${type} }"
    }

}
