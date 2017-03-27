package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.actorfactory.client.SimId
import spock.lang.Specification

/**
 *
 */
class StoreResourceTest extends Specification {

    def 'store and verify'() {
        when:
        def contents = 'not even json'
        SimId simId = new SimId('fhir')
        ResDb resDb = new ResDb(simId, ResDb.BASE_TYPE, ResDb.STORE_TRANSACTION)
        File file = resDb.storeNewResource('simple', contents)

        then:
        file.text == contents
    }
}
