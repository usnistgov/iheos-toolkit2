package gov.nist.toolkit.webUITests.confActor

import com.gargoylesoftware.htmlunit.html.HtmlButton
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput
import com.gargoylesoftware.htmlunit.html.HtmlDivision
import com.gargoylesoftware.htmlunit.html.HtmlImage
import com.gargoylesoftware.htmlunit.html.HtmlLabel
import gov.nist.toolkit.toolkitApi.DocumentRecipient
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Timeout

/**
 * Created by skb1 on 6/22/2017.
 */
@Stepwise
@Timeout(360)
class RecipientActorSimulatorSpec extends ConformanceActor {

    static final String simName = "recip" /* Sim names should be lowered cased */

    @Shared DocumentRecipient recipSim

    @Override
    void setupSim() {
        deleteOldRecipSim()
        sleep(5000) // Why we need this -- Problem here is that the Delete request via REST could be still running before we execute the next Create REST command. The PIF Port release timing will be off causing a connection refused error in the Jetty log.
        recipSim = createNewRecipSim()
        sleep(5000) // Why we need this -- Problem here is that the Delete request via REST could be still running before we execute the next Create REST command. The PIF Port release timing will be off causing a connection refused error in the Jetty log.
    }

   @Override
    String getSimId()     {
       return simUser + "__" + simName
    }

    void deleteOldRecipSim() {
        getSpi().delete(simName, simUser)
    }

    DocumentRecipient createNewRecipSim() {
        return getSpi().createDocumentRecipient(simName, simUser, "default")
    }

    // Recipient actor specific

    def 'Get recipient actor page.'() {
        when:
        loadPage(String.format("%s/#ConfActor:default/%s/rec", toolkitBaseUrl, simUser))

        then:
        page != null
    }

    def 'No unexpected popup or error message presented in a dialog box.'() {
        when:
        List<HtmlDivision> elementList = page.getByXPath("//div[contains(@class,'gwt-DialogBox')]")

        then:
        elementList!=null && elementList.size()==0
    }

    def 'Check Conformance page loading status and its title.'() {
        when:
        while(page.asText().contains("Initializing...")){
            webClient.waitForBackgroundJavaScript(maxWaitTimeInMills)
        }

        while(!page.asText().contains("Initialization Complete")){
            webClient.waitForBackgroundJavaScript(500)
        }

        then:
        "XDS Toolkit" == page.getTitleText()
        page.asText().contains("Initialization Complete")
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
        webClient.waitForBackgroundJavaScript(maxWaitTimeInMills)
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
        webClient.waitForBackgroundJavaScript(maxWaitTimeInMills)

        while(!page.asText().contains("Initialization Complete")){
            webClient.waitForBackgroundJavaScript(500)
        }

        then:
        page.asText().contains("Initialization Complete")

        when:
        List<HtmlDivision> elementList = page.getByXPath("//div[contains(@class, 'orchestrationTestMc') and contains(@class, 'testOverviewHeaderFail')]")  // Substring match, other CSS class must not contain this string.
        // Use this for order dependent selection: "//div[@class='testOverviewHeaderFail orchestrationTest']"

        /*
        If Initialization failed...
        1. Check PIF port numbers
            - Is there a java.net.ConnectException: Connection refused: connect message in the jetty log? Try a different port number range in toolkit.properties.
        2. Check SUT URLs for the two stored query orchestration tests.
         */
        then:
        elementList!=null && elementList.size()==0

        when:
        elementList = page.getByXPath("//div[contains(@class, 'orchestrationTestMc') and contains(@class, 'testOverviewHeaderNotRun')]")

        then:
        elementList!=null && elementList.size()==1
        /* Special case: The Patient Identity Feed is not sent (so this is never displayed as run (green color in UI). */

        when:
        elementList = page.getByXPath("//div[contains(@class, 'orchestrationTestMc') and contains(@class, 'testOverviewHeaderSuccess')]")

        then:
        elementList!=null && elementList.size()==0
    }

    def 'Find and Click the RunAll Test Recipient Conformance Actor image button.'() {

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
}
