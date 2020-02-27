package gov.nist.toolkit.webUITests

import com.gargoylesoftware.htmlunit.html.*
import gov.nist.toolkit.toolkitApi.DocumentRegRep
import spock.lang.Stepwise
import spock.lang.Timeout
/**
 * Created by skb1 on 6/5/2017.
 */
@Stepwise
@Timeout(612) // Keep this to accommodate slow computers (Sunil's Windows 10 laptop).
class RegistryActorA1SimulatorSpec extends RegistryConformanceActor {

    @Override
    void setupSim() {
        setActorPage(String.format("%s/#ConfActor:env=default;testSession=%s;actor=reg;systemId=%s",toolkitBaseUrl,ToolkitWebPage.testSessionName,simName))
        deleteOldRegSim()
        sleep(5000) // Why we need this -- Problem here is that the Delete request via REST could be still running before we execute the next Create REST command. The PIF Port release timing will be off causing a connection refused error.
        regRepSim = createNewRegSim()
    }

    void deleteOldRegSim() {
        getSpi().delete(simName, ToolkitWebPage.testSessionName)
    }

    DocumentRegRep createNewRegSim() {
        return getSpi().createDocumentRegRep(simName, ToolkitWebPage.testSessionName, "default")
    }


    // Registry actor specific

    // Was there a previous SUT selected but doesn't exist now?
    def 'No unexpected popup or error message presented in a dialog box.'() {
        when:
        List<HtmlDivision> elementList = page.getByXPath("//div[contains(@class,'gwt-DialogBox')]")

        then:
        elementList!=null && elementList.size()==0
    }

    def 'Check Conformance page loading status and its title.'() {
        when:
        while(page.asText().contains("Initializing...")){
            webClient.waitForBackgroundJavaScript(ToolkitWebPage.maxWaitTimeInMills)
        }

        while(!page.asText().contains("complete")){
            webClient.waitForBackgroundJavaScript(500)
        }

        then:
        "XDS Toolkit" == page.getTitleText()
        page.asText().contains("complete")
    }

    def 'Click V2 Pif Mode.'() {
        when:
        HtmlLabel v2PifLabel = null
        NodeList labelNl = page.getElementsByTagName("label")
        final Iterator<HtmlLabel> nodesIterator = labelNl.iterator()
        for (HtmlLabel label : nodesIterator) {
            if (label.getTextContent().contains("V2 Patient Identity Feed")) {
                v2PifLabel = label
            }
        }

        then:
        v2PifLabel != null

        when:
        v2PifLabel.click()
        webClient.waitForBackgroundJavaScript(ToolkitWebPage.maxWaitTimeInMills)
        HtmlRadioButtonInput v2Option = page.getElementById(v2PifLabel.getForAttribute())

        then:
        v2Option.isChecked()
    }

    def 'Click Reset (or Initialize) Environment using defaults.'() {
        when:
        HtmlLabel resetLabel = null
        NodeList labelNl = page.getElementsByTagName("label")
        final Iterator<HtmlLabel> nodesIterator = labelNl.iterator()
        for (HtmlLabel label : nodesIterator) {
            if (label.getTextContent().contains("Reset")) {
                resetLabel = label
            }
        }

        then:
        resetLabel != null

        when:
        resetLabel.click()
        webClient.waitForBackgroundJavaScript(ToolkitWebPage.maxWaitTimeInMills)
        HtmlCheckBoxInput resetCkbx = page.getElementById(resetLabel.getForAttribute())

        then:
        resetCkbx.isChecked()
    }

    def 'Click Initialize.'() {
        when:
        HtmlButton initializeBtn = null

        NodeList btnNl = page.getElementsByTagName("button")
        final Iterator<HtmlButton> nodesIterator = btnNl.iterator()
        for (HtmlButton button: nodesIterator) {
            if (button.getTextContent().contains("Initialize Testing Environment")) {
                initializeBtn = button
            }
        }

        then:
        initializeBtn != null

        when:
        page = initializeBtn.click(false,false,false)
        webClient.waitForBackgroundJavaScript(ToolkitWebPage.maxWaitTimeInMills)

        while(!page.asText().contains("Initialization complete")){
            webClient.waitForBackgroundJavaScript(500)
        }

        then:
        page.asText().contains("Initialization complete")

        when:
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

    def 'Count tests to verify later'() { // A complete run Jetty Log should have about 46K lines.
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
        while(page.asText().contains("Initializing...") || page.asText().contains("Loading...")){
            waitingMessageFound = true
            webClient.waitForBackgroundJavaScript(500)
        }

        if (!waitingMessageFound) {
//            print page.asXml()
            println "waitingMessage is not Found. Retrying"
            while(!page.asText().contains("Testing Environment") && page.asText().contains("Option")){
                webClient.waitForBackgroundJavaScript(1000)
            }
            print "done."
        }



        // now iterate

//        println "begin page.asText()"
//        println page.asText()
//        println "end."

        NodeList imgNl = page.getElementsByTagName("img")
        final Iterator<HtmlImage> nodesIterator = imgNl.iterator()
        println "Test statistics before Run All"
        int failuresIdx = page.asText().indexOf("Failures")
        println page.asText().substring(failuresIdx,failuresIdx+20)

        boolean runAllButtonWasFound = false
        boolean runAllButtonWasClicked = false

        for (HtmlImage img : nodesIterator) {
            if ("icons2/play-32.png".equals(img.getSrcAttribute())) { // The big Run All HTML image button
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
        testFail == 0
    }

    def 'Reload page'() {
        when:
        loadPage(actorPage)

        then:
        page != null
    }

    def 'Count tests to make sure all tests are still present'() { // A complete run Jetty Log should have about 46K lines.
        when:
        List<HtmlDivision> nodeList = page.getByXPath("//div[@class='testCount']")
        int testCountToVerify = -1

        if (nodeList!=null && nodeList.size()==1) {
            testCountToVerify = Integer.parseInt(nodeList.get(0).getTextContent())
        }

        then:
        testCountToVerify == testCount
        println ("Total tests: " + testCount)
    }


}