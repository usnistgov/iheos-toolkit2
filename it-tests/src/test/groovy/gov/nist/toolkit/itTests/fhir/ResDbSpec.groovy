package gov.nist.toolkit.itTests.fhir

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.FhirSpecification
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import spock.lang.Shared

/**
 *
 */
class ResDbSpec extends FhirSpecification {
    @Shared SimId simId = new SimId(new TestSession('default'), 'test')

    def 'build/delete fhir sim'() {
        when:
        SimDb.fdelete(simId)  // just in case

        new SimDb().mkfSim(simId)

        then:
        SimDb.fexists(simId)

        when:
        SimDb.fdelete(simId)

        then:
        !SimDb.fexists(simId)
    }
}
