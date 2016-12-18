package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig

import spock.lang.Specification
/**
 * The name is *Tst instead of Test because I don't want this run automatically.
 */
class PullTst extends Specification {
    File cache = new File('/Users/bill/tmp/actors')
    def testingSession = '35'
    def gazelleBaseUrl = 'https://gazelle.ihe.net/EU-CAT/systemConfigurations.seam?testingSessionId=' + testingSession
    GazellePull gazellePull = new GazellePull(gazelleBaseUrl)

    def 'Pull Configs test'() {
        when:
        String configs = new GazelleGet(gazellePull, cache).getAllConfigs()
        int lines = 0
        configs.eachLine { lines++ }
        println 'Found ' + lines + ' configs'

        then:
        lines > 0
    }

    def 'Pull OIDs test'() {
        when:
        String oids = new GazelleGet(gazellePull, cache).getAllOids()
        int lines = 0
        oids.eachLine { lines++ }
        println 'Found ' + lines + ' configs'

        then:
        lines > 0
    }

    def 'Parse test'() {
        setup:
        GazelleGet getter = new GazelleGet(gazellePull, cache)
        String configs = getter.getAllConfigs()

        when:
        ConfigParser parser = new ConfigParser()
        parser.parse(getter.configFile().toString())
        (0..20).each { println parser.get(it).system + "  " + parser.get(it).url + "  " + parser.get(it).secured}

        then:
        parser.all.size() > 1
    }

}
