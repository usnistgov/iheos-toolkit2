package gov.nist.toolkit.xdstools2.tooltabconfig

import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.actortransaction.shared.IheItiProfile
import gov.nist.toolkit.actortransaction.shared.OptionType
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

    def 'Verify if TestCollection codes in ToolTabConfig match values in ActorType'() {
        when:
        String toolId = "ConfTests"
        TabConfigLoader.init(new File(System.getProperty("confTestsTabsConfigFile")))  // property set in POM file
        TabConfig confTestsTabConfig = TabConfigLoader.getTabConfig(toolId)

        then:
        confTestsTabConfig != null

        when:
        "Actors".equals(confTestsTabConfig.getLabel())
        print "Note: ActorType is disconnected from Profile and OptionType. It allows for test collection flexibility without hard-coding such combinations in ActorType. This test only checks each Actor/Profile/Option type independently."

        then:
        for (TabConfig actorTabConfig : confTestsTabConfig.getChildTabConfigs()) {
            print "Checking actor " + actorTabConfig.getLabel() + "..."

            // Both the collections.txt and the actorCode must be lower-cased
            String actorTypeCode = actorTabConfig.getTcCode()
            if (actorTypeCode!=null) {
                assert actorTypeCode.equals(actorTypeCode.toLowerCase())
            }

            TabConfig profiles = actorTabConfig.getFirstChildTabConfig()
            "Profiles".equals(profiles.getLabel())

            for (TabConfig profile : profiles.getChildTabConfigs()) {
                String profileTypeCode = profile.getTcCode()
                IheItiProfile itiProfile = IheItiProfile.find(profileTypeCode)
                int optionCt = 0
                TabConfig options = profile.getFirstChildTabConfig()
                for (TabConfig option : options.getChildTabConfigs())  {
                    OptionType optionType1 = OptionType.find(option.tcCode)

                    boolean foundActor = false
                    boolean foundProfile = false
                    boolean foundOption = false

                    ActorType actorType1 = null
                    for (ActorType actorType : ActorType.values()) {
                        if (actorType.getActorCode().equals(actorTypeCode)) {
                            actorType1 = actorType
                            foundActor = true
                            for (IheItiProfile p: IheItiProfile.values()) {
                                if (p.equals(itiProfile)) {
                                    foundProfile = true
                                    for (OptionType optionType : OptionType.values()) {
                                        if (optionType.equals(optionType1))  {
                                            foundOption = true
                                        }
                                    }
                                }
                            }
                            break
                        }
                    }

                    if (!foundActor || !foundProfile || !foundOption)
                        println "ActorTypeCode: '" + actorTypeCode  + "' : " + actorType1.shortName \
                             + " Profile: '" + profileTypeCode + "' : " + itiProfile.toString() \
                            + " Option: '" + option.tcCode + "' : " + optionType1.toString() \
                             +  " is missing one or more Enum mapping."

                    assert foundActor
                    assert foundProfile
                    assert foundOption

                    optionCt++
                }
                println optionCt + " " + profileTypeCode + " option(s) - Ok."
            }


        }

    }

}