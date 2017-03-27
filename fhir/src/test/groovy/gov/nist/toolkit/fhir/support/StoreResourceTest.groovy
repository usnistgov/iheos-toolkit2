package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.testSupport.InitEC
import spock.lang.Specification

/**
 *
 */
class StoreResourceTest extends Specification {

    def setup() {
        InitEC.init()
    }

    def 'store and verify'() { // store a resource and get contents back
        when:
        def contents = 'not even json'
        SimId simId = new SimId('fhir')
        new ResDb().mkSim(simId)
        ResDb resDb = new ResDb(simId, ResDb.BASE_TYPE, ResDb.STORE_TRANSACTION)
        File file = resDb.storeNewResource('simple', contents)

        then:
        file.text == contents
    }
}
