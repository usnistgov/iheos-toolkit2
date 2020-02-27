package gov.nist.toolkit.webUITests

import spock.lang.Shared
import spock.lang.Stepwise

/**
 * Created by skb1 on 6/5/2017.
 */
@Stepwise
abstract class ConformanceActor extends ToolkitWebPage {

    @Shared String actorPage
    @Shared int testCount
    abstract void setupSim()
    abstract String getSimIdAsString()

    def setupSpec() {
        setupSim()
        if (actorPage==null)
            throw new Exception("ActorPage needs to be initialized")
    }
    def cleanupSpec() {
    }
    def setup() {
    }
    def cleanup() {
    }

    def 'Verify SUT selection'() {
        when:
        loadPage(actorPage)

        then:
        page != null

        "XDS Toolkit" == page.getTitleText()
        page.asText().contains("complete")

        page.asText().contains("SUT: " + getSimIdAsString())
    }

}