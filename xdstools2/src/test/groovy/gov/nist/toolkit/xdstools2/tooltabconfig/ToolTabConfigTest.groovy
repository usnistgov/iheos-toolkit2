package gov.nist.toolkit.xdstools2.tooltabconfig

import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.TabConfig
import gov.nist.toolkit.xdstools2.server.TabConfigLoader
import spock.lang.Specification

class ToolTabConfigTest extends Specification {

    def setupSpec() {
    }
    def cleanupSpec() {
    }

    def setup() {

    }

    def 'Test ToolTabConfig transformation'() {

        when:
        String toolId = "ConfTests"
        TabConfigLoader.init(Installation.instance().getToolTabConfigFile(toolId))
        TabConfig confTestsTabConfig = TabConfigLoader.getTabConfig(toolId)

        then:
        confTestsTabConfig != null

        then:
        "Actors".equals(confTestsTabConfig.getLabel())

        for (TabConfig tabConfig : confTestsTabConfig.getChildTabConfigs()) {
            println tabConfig.getLabel()
        }

        when:
        println "Checking structure..."
        TabConfig rep = confTestsTabConfig.getFirstChildTabConfig()

        then:
        "Repository".equals(rep.getLabel())

        when:
        TabConfig profiles = rep.getFirstChildTabConfig()
        then:
        "Profiles".equals(profiles.getLabel())

        when:
        TabConfig xdsProfile = profiles.getFirstChildTabConfig()
        then:
        "XDS".equals(xdsProfile.getLabel())

        when:
        TabConfig options = xdsProfile.getFirstChildTabConfig()
        then:
        "Options".equals(options.getLabel())

        when:
        TabConfig requiredOpt = options.getFirstChildTabConfig()
        then:
        "Required".equals(requiredOpt.getLabel())
        "option".equals(requiredOpt.getType())
        "".equals(requiredOpt.getTcCode())
        !requiredOpt.getExternalStart()

        println "done."
    }



}