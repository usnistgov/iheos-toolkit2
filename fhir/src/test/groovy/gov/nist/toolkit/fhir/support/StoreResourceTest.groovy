package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.testSupport.InitEC
import spock.lang.Specification
/**
 *
 */
class StoreResourceTest extends Specification {
    SimId simId = new SimId('fhir')

    def setup() {
        InitEC.init()
        new ResDb().mkSim(simId)
    }

    def 'store and verify'() { // store a resource and get contents back
        when:
        def contents = 'not even json'
        ResDb resDb = new ResDb(simId, ResDb.BASE_TYPE, ResDb.STORE_TRANSACTION)
        File file = resDb.storeNewResource('simple', contents)

        then:
        file.text == contents
    }
}
