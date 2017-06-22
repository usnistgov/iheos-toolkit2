package gov.nist.toolkit.itTests.fhir

import gov.nist.toolkit.fhir.support.ResDb
import gov.nist.toolkit.itTests.support.FhirSpecification
import gov.nist.toolkit.simcommon.client.SimId
import spock.lang.Shared

/**
 *
 */
class ResDbTest extends FhirSpecification {
    @Shared SimId simId = new SimId('default', 'test')

    def 'build/delete fhir sim'() {
        when:
        ResDb.delete(simId)  // just in case

        new ResDb().mkSim(simId)

        then:
        ResDb.exists(simId)

        when:
        ResDb.delete(simId)

        then:
        !ResDb.exists(simId)
    }
}
