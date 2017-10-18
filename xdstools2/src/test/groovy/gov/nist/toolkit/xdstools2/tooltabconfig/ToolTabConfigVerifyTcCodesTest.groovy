package gov.nist.toolkit.xdstools2.tooltabconfig

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.IheItiProfile
import gov.nist.toolkit.actortransaction.client.OptionType
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


            TabConfig profiles = actorTabConfig.getFirstChildTabConfig()
            "Profiles".equals(profiles.getLabel())

            String actorTypeCode = actorTabConfig.getTcCode()
            for (TabConfig profile : profiles.getChildTabConfigs()) {
                String profileTypeCode = profile.getTcCode();
                IheItiProfile itiProfile = IheItiProfile.find(profileTypeCode)
                int optionCt = 0
                TabConfig options = profile.getFirstChildTabConfig()
               for (TabConfig option : options.getChildTabConfigs())  {
                   OptionType optionType1 = OptionType.find(option.tcCode)

                   boolean foundMatch = false
                   ActorType actorType1
                   for (ActorType actorType : ActorType.values()) {
                       if (actorType.getActorCode().equals(actorTypeCode)) {
                           actorType1 = actorType
                           if (actorType.profile.equals(itiProfile)) {
                               for (OptionType optionType : actorType.options) {
                                  if (optionType.equals(optionType1))  {
                                      foundMatch = true
                                  }
                               }
                           }
                       }
                   }

                   if (!foundMatch)
                       print "ActorTypeCode: " + actorTypeCode  + "/" + actorType1.shortName \
                             + " Profile: " + profileTypeCode + "/" + itiProfile.toString() \
                            + " Option: " + option.tcCode + "/" + optionType1.toString() \
                             +  " not found!"
                   assert foundMatch
                   optionCt++
               }
               println optionCt + " " + profileTypeCode + " option(s) verified using ActorType enum."
            }


        }

    }

}