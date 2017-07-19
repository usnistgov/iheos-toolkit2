package gov.nist.toolkit.xdstools2.tooltabconfig

import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.TabConfig
import gov.nist.toolkit.xdstools2.server.TabConfigLoader
import spock.lang.Specification

import java.util.concurrent.ConcurrentHashMap

class ToolTabConfigTest extends Specification {

    def setupSpec() {
    }
    def cleanupSpec() {
    }

    def setup() {

    }

    def 'Test ToolTabConfig transformation'() {

        when:
        ConcurrentHashMap<String,TabConfig> confTestsTabMap = new ConcurrentHashMap<>();
        TabConfigLoader.init(confTestsTabMap,Installation.instance().getToolTabConfigFile("ConfTests"))


        then:
        confTestsTabMap.size() > 0

        TabConfig actorsGroup = confTestsTabMap.get("Actors")
        actorsGroup != null
        "Actors".equals(actorsGroup.getLabel())

        for (TabConfig tabConfig : actorsGroup.getChildTabConfigs()) {
            println tabConfig.getLabel()
        }

        println "Checking structure..."

        TabConfig rep = actorsGroup.getChildTabConfigs().get(0)
        "Repository".equals(rep.getLabel())

        TabConfig profiles = rep.getChildTabConfigs().get(0)
        "Profiles".equals(profiles.getLabel())

        TabConfig xdsProfile = profiles.getChildTabConfigs().get(0)
        "XDS".equals(xdsProfile.getLabel())

        TabConfig options = xdsProfile.getChildTabConfigs().get(0)
        "Options".equals(options.getLabel())

        TabConfig requiredOpt = options.getChildTabConfigs().get(0)
        "Required".equals(requiredOpt.getLabel())
        "option".equals(requiredOpt.getType())
        "".equals(requiredOpt.getTcCode())
        !requiredOpt.getExternalStart()

        println "done."
    }



}