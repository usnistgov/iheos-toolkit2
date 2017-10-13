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
        TabConfigLoader.init(new File(System.getProperty("confTestsTabsConfigFile")))  // property set in POM file
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

            String actorTypeCode = actorTabConfig.getTcCode()
            for (TabConfig profile : profiles.getChildTabConfigs()) {
                String profileTypeCode = profile.getTcCode();
                int optionCt = 0
                TabConfig options = profile.getFirstChildTabConfig()
               for (TabConfig option : options.getChildTabConfigs())  {
                   StringBuilder actorTypeShortName =  new StringBuilder(actorTypeCode)
                   if (!"".equals(profileTypeCode)&&!"xds".equals(profileTypeCode)) {
                      actorTypeShortName.append("_")
                      actorTypeShortName.append(profileTypeCode)
                   }
                   if (!"".equals(option.getTcCode())) { // "" translates to Required option
                       actorTypeShortName.append("_")
                       actorTypeShortName.append(option.getTcCode())
                   }
                   boolean foundMatch = false
                   String shortNameStr = actorTypeShortName.toString()
                   for (ActorType actorType : ActorType) {
                       if (actorType.getShortName().equals(shortNameStr))
                           foundMatch = true
                   }
                   if (!foundMatch)
                       print "ActorTypeCode: " + shortNameStr+ " not found!"
                   assert foundMatch
                   optionCt++
               }
               println optionCt + " " + profileTypeCode + " option(s) verified using ActorType enum."
            }


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