package gov.nist.toolkit.webUITests

import com.gargoylesoftware.htmlunit.html.HtmlDivision
import com.gargoylesoftware.htmlunit.html.HtmlImage
import spock.lang.Shared

abstract class RegistryConformanceActorOption extends RegistryConformanceActor {
    static protected final enum RunButtonEnum {
        RUN_ALL_BUTTON("icons2/play-32.png"),
        VALIDATE_CLIENT_INITIATED_TX_BUTTON("icons2/validate-32.png")

        String imageFile

        RunButtonEnum(String imageFile) {
            this.imageFile = imageFile
        }

        @Override
        String toString() {
            return imageFile
        }
    }

    @Shared
    protected RunButtonEnum runButton
    @Shared
    protected int knownTestFailures = 0

    abstract void setRunButton()
    abstract void setOptions()
    abstract void setActorPageUrl()

    @Override
    void setupSim() {

        setRunButton()
        setOptions()
        setActorPageUrl()
    }

    // Was there a previous SUT selected but doesn't exist now?
    def 'No unexpected popup or error message presented in a dialog box.'() {
        when:
        List<HtmlDivision> elementList = page.getByXPath("//div[contains(@class,'gwt-DialogBox')]")

        then:
        elementList!=null && elementList.size()==0
    }

    def 'Check Conformance page loading status and its title.'() {
        when:
        while(page.getVisibleText().contains("Initializing...")){
            webClient.waitForBackgroundJavaScript(ToolkitWebPage.maxWaitTimeInMills)
        }

        while(!page.getVisibleText().contains("complete")){
            webClient.waitForBackgroundJavaScript(500)
        }

        then:
        "XDS Toolkit" == page.getTitleText()
        page.getVisibleText().contains("complete")
    }

    def 'Orchestration should already be Initialized.'() {
        when:
        page.getVisibleText().contains("Initialization complete")

        then:
        List<HtmlDivision> elementList = page.getByXPath("//div[contains(@class, 'orchestrationTestMc') and contains(@class, 'testOverviewHeaderFail')]")  // Substring match, other CSS class must not contain this string.
        // Use this for order dependent selection: "//div[@class='testOverviewHeaderFail orchestrationTest']"

        /*
        If Initialization failed...
        1. Check PIF port numbers
            - Is there a java.net.ConnectException: Connection refused: connect message in the jetty log? Try a different port number range in toolkit.properties.
        2. Check SUT URLs for the two stored query orchestration tests.
         */
        then: 'There should be no orchestration test failures'
        elementList!=null && elementList.size()==0

        when:
        elementList = page.getByXPath("//div[contains(@class, 'orchestrationTestMc') and contains(@class, 'testOverviewHeaderNotRun')]")

        then:
        elementList!=null && elementList.size()==0

        when:
        elementList = page.getByXPath("//div[contains(@class, 'orchestrationTestMc') and contains(@class, 'testOverviewHeaderSuccess')]")

        then:
        elementList!=null && elementList.size()==8 // Orchestration tests
    }

    def 'Count tests to verify later'() {
        when:
        List<HtmlDivision> nodeList = page.getByXPath("//div[@class='testCount']")
        testCount = -1

        if (nodeList!=null && nodeList.size()==1) {
            testCount = Integer.parseInt(nodeList.get(0).getTextContent())
        }

        then:
        testCount > -1
    }

    def 'Find and Click the RunAll Test Registry Conformance Actor image button.'() {

        when:
        List<HtmlDivision> elementList = page.getByXPath("//div[contains(@class,'gwt-DialogBox')]")

        then:
        elementList!=null && elementList.size()==0


        when:
        boolean waitingMessageFound = false
        while(page.getVisibleText().contains("Initializing...") || page.getVisibleText().contains("Loading...")){
            waitingMessageFound = true
            webClient.waitForBackgroundJavaScript(500)
        }

        if (!waitingMessageFound) {
//            print page.asXml()
            println "waitingMessage is not Found. Retrying"
            while(!page.getVisibleText().contains("Testing Environment") && page.getVisibleText().contains("Option")){
                webClient.waitForBackgroundJavaScript(1000)
            }
            print "done."
        }

        NodeList imgNl = page.getElementsByTagName("img")
        final Iterator<HtmlImage> nodesIterator = imgNl.iterator()
        println "Test statistics before Run All"
        int failuresIdx = page.getVisibleText().indexOf("Failures")
        println page.getVisibleText().substring(failuresIdx,failuresIdx+20)

        boolean runAllButtonWasFound = false
        boolean runAllButtonWasClicked = false

        for (HtmlImage img : nodesIterator) {
            if (runButton.toString().equals(img.getSrcAttribute())) {
                runAllButtonWasFound = true
                println "img src is --> " + img.getSrcAttribute()
                page = img.click(false,false,false)
                runAllButtonWasClicked = true
                webClient.waitForBackgroundJavaScript(1000)
                break
            }
        }

        then:
        runAllButtonWasFound
        runAllButtonWasClicked
        page != null
    }

    def 'Number of failed tests count should be zero.'() { // A complete run Jetty Log should have about 46K lines.
        when:
        List<HtmlDivision> nodeList = page.getByXPath("//div[@class='testFail']")
        int testFail = -1

        if (nodeList!=null && nodeList.size()==1) {
            testFail = Integer.parseInt(nodeList.get(0).getTextContent())
        }

        then:
        testFail == knownTestFailures
    }

    def 'Reload page'() {
        when:
        loadPage(actorPage)

        then:
        page != null
    }

    def 'Count tests to make sure all tests are still present'() {
        when:
        List<HtmlDivision> nodeList = page.getByXPath("//div[@class='testCount']")
        int testCountToVerify = -1

        if (nodeList!=null && nodeList.size()==1) {
            testCountToVerify = Integer.parseInt(nodeList.get(0).getTextContent())
        }

        then:
        testCountToVerify == testCount
        println ("Total tests: " + testCount)

        if (knownTestFailures > 0) {
            println("Known test failures (actual failed tests): " + knownTestFailures)
        }
    }


}
