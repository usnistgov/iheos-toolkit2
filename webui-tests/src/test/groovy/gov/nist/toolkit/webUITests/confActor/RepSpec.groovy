package gov.nist.toolkit.webUITests.confActor

import com.gargoylesoftware.htmlunit.AjaxController
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.html.*
import gov.nist.toolkit.toolkitApi.DocumentRegRep
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Timeout

/**
 * Test the Registry actor simulator using conformance tests
 */
@Stepwise
@Timeout(120) // Timeout if everything here doesn't complete in 5 minutes.
@Ignore
class RepSpec extends Specification {
    @Shared WebClient webClient
    @Shared HtmlPage page
    @Shared int toolkitPort = 8888
    @Shared String toolkitBaseUrl = String.format("http://localhost:%s",toolkitPort)
    @Shared SimulatorBuilder spi
    @Shared DocumentRegRep regRepSim
    static final int maxWaitTimeInMills = 60000*5 // 5 minutes
    static final String simName = "automatedwebuitestrep" /* Sim names should be lowered cased */
    static final String simUser = "default"
    static final String simId = simUser + "__" + simName

    /*
//         page = webClient.getPage("http://localhost:8888/Xdstools2.html#ConfActor:default/default/reg");
//           page = webClient.getPage("http://localhost:8888/")
//        page = webClient.getPage("http://ihexds.nist.gov:12093/xdstools4/#ConfActor:default/default/reg");
*/

    def setupSpec() {
        setupSpi()
        setupSim()
    }
    def cleanupSpec() {
    }
    def setup() {
    }
    def cleanup() {
    }

    void loadPage(String url) {

        if (webClient!=null) webClient.close()

        webClient = new WebClient(BrowserVersion.FIREFOX_52)

        // 1. Load the Simulator Manager tool page
        page = webClient.getPage(url)
        webClient.getOptions().setJavaScriptEnabled(true)
        webClient.getOptions().setTimeout(maxWaitTimeInMills)
        webClient.setJavaScriptTimeout(maxWaitTimeInMills)
        webClient.waitForBackgroundJavaScript(maxWaitTimeInMills)
        webClient.getOptions().setPopupBlockerEnabled(false)

        webClient.getCache().clear()
        webClient.setAjaxController(new AjaxController(){
            @Override
            boolean processSynchron(HtmlPage page, WebRequest request, boolean async)
            {
                return true
            }
        })
        webClient.waitForBackgroundJavaScript(maxWaitTimeInMills)
    }

    void setupSpi() {
        spi = new SimulatorBuilder(toolkitBaseUrl)
    }

    void setupSim() {
        deleteOldRegSim()
        regRepSim = createNewRepSim()
    }

    void deleteOldRegSim() {
        spi.delete(simName, simUser)
    }

    DocumentRegRep createNewRepSim() {
        return spi.createDocumentRegRep(simName, simUser, "default")
    }

    // -- Part 1. Check setup of the Registry simulator

    def 'Check if Simulators page contains the newly created Rep actor sim.'() {
        when:
        loadPage(String.format("%s/#Tool:Simulators",toolkitBaseUrl))

        then:
        page.asText().contains("Simulator Manager") and page.asText().contains("Add new simulator to this test session")
        page.asText().contains(simId)
    }


    // 3. Click the Conformance Tests link
    // 2. Set the SUT in the Test Context

    // -- Part 2. Registry conformance actor tests
    // (by this time, the SUT should have already been set in Testing Context)

    def 'Find and click the Conformance Tests link.'() {
        when:
        HtmlAnchor conformanceTestAnchor
        NodeList anchorNl = page.getElementsByTagName("a")
        final Iterator<HtmlAnchor> nodesIterator = anchorNl.iterator()
        for (HtmlAnchor anchor : nodesIterator) {
            if (anchor.getTextContent().equals("Conformance Tests")) {
                conformanceTestAnchor = anchor
                break
            }
        }

        then:
        conformanceTestAnchor != null

        when:
        page = conformanceTestAnchor.click()

        then:
        page.asText().contains("Test Context")

    }

    def 'Find and click the Test Context box.'() {
        when:
        HtmlDivision testContextDiv
        List<HtmlDivision> divList = page.getByXPath("//div[contains(@class, 'gwt-HTML')]")  // Substring match, other CSS class must not contain this string.

        for (HtmlDivision div : divList) {
            if (div.getTextContent().contains("Test Context")) {
               testContextDiv = div
                break
            }
        }

        page = testContextDiv.click()

        then:
        page.asText().contains("Conformance test context")

    }

    def 'Select the newly created sim.'() {
        when:
        List<HtmlSelect> selectList = page.getByXPath("//select[contains(@class, 'gwt-ListBox') and contains(@class, 'confActorSutSelector')]")  // Substring match, other CSS class must not contain this string.

        then:
        selectList!=null && selectList.size()==1 // Should be only one

        when:
        HtmlSelect sutSelector = selectList.get(0)

        List<HtmlOption> optionsList= sutSelector.getOptions()
        HtmlOption newlyCreatedSutOption = null
        for (HtmlOption optionElement : optionsList) {
            if (simId == optionElement.getText() && simId == optionElement.getValueAttribute()) {
                optionElement.setSelected(true)
                newlyCreatedSutOption = optionElement
                break
            }
        }

        then:
        newlyCreatedSutOption !=null
        newlyCreatedSutOption.isSelected()

    }

    def 'Click the Assign System... button'() {
        when:
        HtmlButton assignBtn = null

        NodeList btnNl = page.getElementsByTagName("button")
        final Iterator<HtmlButton> nodesIterator = btnNl.iterator()
        for (HtmlButton button: nodesIterator) {
            if (button.getTextContent() == "Assign System for Test Session") {
                assignBtn = button
            }
        }

        then:
        assignBtn != null

        when:
        page = assignBtn.click()

        then:
        page.asText().contains("SUT: " + simId)
    }

    def 'Get registry conformance actor page.'() {
        when:
        loadPage(String.format("%s/#ConfActor:default/default/reg",toolkitBaseUrl))

        then:
        page != null
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
        List<HtmlDivision> elementList = page.getByXPath("//div[contains(@class, 'orchestrationTest') and contains(@class, 'testOverviewHeaderFail')]")  // Substring match, other CSS class must not contain this string.
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
        elementList = page.getByXPath("//div[contains(@class, 'orchestrationTest') and contains(@class, 'testOverviewHeaderNotRun')]")

        then:
        elementList!=null && elementList.size()==0

        when:
        elementList = page.getByXPath("//div[contains(@class, 'orchestrationTest') and contains(@class, 'testOverviewHeaderSuccess')]")

        then:
        elementList!=null && elementList.size()==7
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

    def 'Number of failed tests count should be zero.'() {
        when:
        List<HtmlDivision> nodeList = page.getByXPath("//div[@class='testFail']")
        int testFail = -1

        if (nodeList!=null && nodeList.size()==1) {
            testFail = Integer.parseInt(nodeList.get(0).getTextContent())
        }

        then:
        testFail == 0
    }

    /*
    @Ignore
    def 'Select the Registry actor type simulator.'() {

        when:
        List<HtmlSelect> selectList = page.getByXPath("//select[contains(@class, 'actorSelector')]")  // Substring match, other CSS class must not contain this string.

        then:
        selectList!=null && selectList.size()==1 // Should be only one

        when:
        HtmlSelect actorSelector = selectList.get(0)

        List<HtmlOption> optionsList= actorSelector.getOptions()
        HtmlOption regOption = null
        for (HtmlOption optionElement : optionsList) {
           if ("Document Registry" == optionElement.getText() && "Document Registry" == optionElement.getValueAttribute()) {
              optionElement.setSelected(true)
               regOption = optionElement
               break
           }
        }

        then:
        regOption !=null
        regOption.isSelected()
    }


    @Ignore
    def 'Enter Registry actor simulator Name.'() {
        when:
        List<HtmlTextInput> inputList = page.getByXPath("//input[contains(@class, 'actorSimId')]")  // Substring match, other CSS class must not contain this string.

        then:
        inputList !=null && inputList.size() == 1

        when:
        HtmlTextInput newSimIdTextBox = inputList.get(0)
        newSimIdTextBox.setText("automatedRegTest")

        then:
        newSimIdTextBox.getText() == "automatedRegTest"
    }

    @Ignore
    def 'Click the Create Actor Simulator button.'() {

        when:
        int initialNumOfRows
        HtmlTable simConfigTable = page.getElementById("simConfigTable")

        then:
        simConfigTable != null

        when:
        initialNumOfRows = simConfigTable.getRowCount()

        then:
        initialNumOfRows > 1

        when:
        List<HtmlButton> buttonList = page.getByXPath("//button[contains(@class, 'gwt-Button')]")  // Substring match, other CSS class must not contain this string.

        then:
        buttonList !=null

        when:
        for (HtmlButton button : buttonList) {
            if ("Create Actor Simulator" == button.getTextContent()) {
               page = button.click()
               break
            }
        }

        then:
        page != null

        when:
        simConfigTable = page.getElementById("simConfigTable")
        int numOfRowsAfterAddingNewSim  = simConfigTable.getRowCount()

        then:
        simConfigTable != null
        numOfRowsAfterAddingNewSim == (initialNumOfRows + 1)
    }
    */

}