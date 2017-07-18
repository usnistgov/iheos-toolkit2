package gov.nist.toolkit.itTests.fhirUtil

import gov.nist.toolkit.installation.ExternalCacheManager
import gov.nist.toolkit.itTests.support.FhirSpecification
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import spock.lang.Shared
import spock.util.concurrent.PollingConditions

/**
 * Utility that helps with bootstrapping the environment since we don't have all the utilities in place yet
 * This is specific to my machine
 *
 * This is used to start the FHIR engine within toolkit quickly.  It will run for 10000 seconds or until
 * you kill it.  Good for testing, well for now,  event logging.
 */
class TestUtil extends FhirSpecification {
    @Shared SimId simId = new SimId('default', 'test')

    def setupSpec() {
        startGrizzlyWithFhir('8889')
        ExternalCacheManager.reinitialize(new File('/Users/bill/tmp/toolkit2a'))
    }

    def 'build fhir sim'() {
        when:
//        SimDb.fdelete(simId)  // just in case

        new SimDb().mkfSim(simId)

        then:
        SimDb.fexists(simId)

        when:
        def conditions = new PollingConditions()
        conditions.timeout = 10000
        conditions.delay = 9000
        println 'RUNNING...'

        then:
        conditions.eventually {
            assert false
        }
    }
}