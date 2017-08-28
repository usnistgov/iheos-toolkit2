package gov.nist.toolkit.xdstools2.tooltabconfig

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.TabConfig
import gov.nist.toolkit.xdstools2.server.TabConfigLoader
import spock.lang.Specification

class ToolTabConfigVerifyTcCodesTest extends Specification {

    def setupSpec() {
    }
    def cleanupSpec() {
    }

    def setup() {

    }

    def 'Verify if Tc codes in ToolTabConfig match values in ActorType'() {

        when:
        String toolId = "ConfTests"
        TabConfigLoader.init(new File(System.getProperty("confTestsTabsConfigFile")))
        TabConfig confTestsTabConfig = TabConfigLoader.getTabConfig(toolId)

        then:
        confTestsTabConfig != null

        when:
        "Actors".equals(confTestsTabConfig.getLabel())

        then:
        for (TabConfig actorTabConfig : confTestsTabConfig.getChildTabConfigs()) {
            print "Checking actor " + actorTabConfig.getLabel() + "... "

            // All we can verify is the actor (ActorType.shortname) and the option (separate actor_option enum) from the ActorType enum.
            // Only XDS profile is supported at this moment

            TabConfig profiles = actorTabConfig.getFirstChildTabConfig()
            "Profiles".equals(profiles.getLabel())
            TabConfig xdsProfile = profiles.getFirstChildTabConfig()
            "XDS".equals(xdsProfile.getLabel())
            TabConfig options = xdsProfile.getFirstChildTabConfig()
            "Options".equals(options.getLabel())

            int optionCt = 0
            for (TabConfig option: options.getChildTabConfigs()) {
                String actorTypeCode = actorTabConfig.getTcCode()
                if (!"".equals(option.getTcCode())) {
                    actorTypeCode += "_" + option.getTcCode()
                }
                boolean foundMatch = false
                for (ActorType actorType : ActorType) {
                    if (actorType.getShortName().equals(actorTypeCode))
                        foundMatch = true
                }
                if (!foundMatch)
                    print "Code: " + actorTypeCode + " not found!"
                assert foundMatch
                optionCt++
            }

            println optionCt + " option(s) verified using ActorType enum."
        }

        /*
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
        */
    }



}