package gov.nist.toolkit.xdstools2.server.gazelle.sysconfig

import gov.nist.toolkit.xdstools2.server.gazelle.sysconfig.ConfigDef
import gov.nist.toolkit.xdstools2.server.gazelle.sysconfig.ConfigParser
import gov.nist.toolkit.xdstools2.server.gazelle.sysconfig.GazelleGet
import gov.nist.toolkit.xdstools2.server.gazelle.sysconfig.GazellePull
import gov.nist.toolkit.xdstools2.server.gazelle.sysconfig.OidDef
import gov.nist.toolkit.xdstools2.server.gazelle.sysconfig.OidsParser
import spock.lang.Specification
/**
 * The simName is *Tst instead of Test because I don't want this run automatically.
 */
class PullTst extends Specification {
    File cache = new File('/Users/bill/tmp/actors')
    def testingSession = '35'
    def gazelleBaseUrl = 'https://gazelle.ihe.net/EU-CAT/systemConfigurations.seam?testingSessionId=' + testingSession
    GazellePull gazellePull = new GazellePull(gazelleBaseUrl)
    def singleConfigName = 'EHR_A-thon_9'

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

        when:
        ConfigParser parser = new ConfigParser()
        parser.parse(getter.configFile().toString())
        (0..20).each { println parser.get(it).system + "  " + parser.get(it).url + "  " + parser.get(it).secured}

        then:
        parser.values.size() > 1
    }

    def 'pull a config test'() {
        when:
        String config = new GazelleGet(gazellePull, cache).getSingleConfig(singleConfigName)
        int lines = 0
        config.eachLine { lines++ }
        println 'Found ' + lines + ' configs'

        then:
        lines > 0
    }


    def 'verify http boolean'() {
        when:
        initCParserWithTestData()
        ConfigDef config = pickFirstHttpEntry()
        println config

        then:
        !config.secured
    }

    def 'verify https boolean'() {
        when:
        initCParserWithTestData()
        ConfigDef config = pickFirstHttpsEntry()

        then:
        config.secured
    }

    def 'verify home id'() {
        when:
        initOParserWithTestData()
        OidDef odef = pickOidDef('OTHER_TPLUS_2016_EU', OidDef.HomeIdOid)

        then:
        odef.oid == 'urn:oid:1.3.6.1.4.1.21367.2011.2.6.144'
    }

    def 'verify oid has def'() {
        setup:
        initOParserWithTestData()

        when:
        def odef = oparser.hasDef('foo', 'bar')

        then:
        !odef

        when:
        def odef1 = oparser.hasDef('OTHER_TPLUS_2016_EU', OidDef.HomeIdOid)

        then:
        odef1
    }

    OidDef pickOidDef(String system, String type) {
        oparser.all.find { ele -> ele.system == system && ele.type == type}
    }

    ConfigParser cparser = new ConfigParser()
    OidsParser oparser = new OidsParser()

    ConfigDef pickFirstHttpEntry() {
        cparser.values.find { config -> config.url.startsWith('http:') }
    }

    ConfigDef pickFirstHttpsEntry() {
        cparser.values.find { config -> config.url.startsWith('https:') }
    }

    /**
     * Resulting data accessed through
     * int parser.size()
     * ConfigDef parser.get(int)
     * @return
     */
    def initCParserWithTestData() {
        GazelleGet getter = new GazelleGet(gazellePull, cache)
        cparser.parse(getter.configFile().toString())
    }

    def initOParserWithTestData() {
        GazelleGet getter = new GazelleGet(gazellePull, cache)
        oparser.parse(getter.oidsFile().toString())
    }

}
