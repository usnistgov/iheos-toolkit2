package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig

import gov.nist.toolkit.sitemanagement.SeparateSiteLoader
import gov.nist.toolkit.sitemanagement.client.Site
import spock.lang.Specification

/**
 *
 */
class GenerateAllSystemsTest extends Specification {
    File cache = new File('/Users/bill/tmp/actors')
    def testingSession = '35'
    def gazelleBaseUrl = 'https://gazelle.ihe.net/EU-CAT/systemConfigurations.seam?testingSessionId=' + testingSession
    GazellePull gazellePull = new GazellePull(gazelleBaseUrl)

    def 'run a single'() {
        when:
        GenerateSystems generator = new GenerateSystems(gazellePull, cache)
        GeneratedSystems generatedSystems = generator.generateSingleSystem('OTHER_TPLUS_2016_EU')
        println generatedSystems.log.toString()

        generatedSystems.systems.each { Site site ->
            new SeparateSiteLoader().saveToFile(cache, site)
        }

        then:
        true

    }

    def 'run'() {
        when:
        GenerateSystems generator = new GenerateSystems(gazellePull, cache)
        GeneratedSystems generatedSystems = generator.generateAllSystems()
//        println generatedSystems.log.toString()

        generatedSystems.systems.each { Site site ->
            println "Saving site ${site.name}"
            try {
                new SeparateSiteLoader().saveToFile(cache, site)
            } catch (Exception e) {
                println "Site ${site.name} cannot be saved - conflicts:"
                println e.getMessage()
            }
        }

        then:
        true
    }
}
