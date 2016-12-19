package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig

import spock.lang.Specification

/**
 *
 */
class GenerateSingleSystemTest extends Specification {
    File cache = new File('/Users/bill/tmp/actors')
    def testingSession = '35'
    def gazelleBaseUrl = 'https://gazelle.ihe.net/EU-CAT/systemConfigurations.seam?testingSessionId=' + testingSession
    GazellePull gazellePull = new GazellePull(gazelleBaseUrl)

    def singleConfigName = 'EHR_A-thon_9'

    def 'test single system'() {
        when:
        GenerateSingleSystem gen = new GenerateSingleSystem(gazellePull, cache)
        GeneratedSystems systems = gen.generate(singleConfigName)
        println systems.log.toString()

        then:
        true

    }
}
