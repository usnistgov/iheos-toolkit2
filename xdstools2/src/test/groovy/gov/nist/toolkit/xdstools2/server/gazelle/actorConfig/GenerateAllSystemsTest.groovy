package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig

import spock.lang.Specification
/**
 *
 */
class GenerateAllSystemsTest extends Specification {
    File cache = new File('/Users/bill/tmp/actors')
    def testingSession = '35'
    def gazelleBaseUrl = 'https://gazelle.ihe.net/EU-CAT/systemConfigurations.seam?testingSessionId=' + testingSession
    GazellePull gazellePull = new GazellePull(gazelleBaseUrl)

    def 'run'() {
        when:
        GenerateSystemShell shell = new GenerateSystemShell(cache, gazelleBaseUrl)
        boolean status = shell.run()

        then:
        status
    }
}
