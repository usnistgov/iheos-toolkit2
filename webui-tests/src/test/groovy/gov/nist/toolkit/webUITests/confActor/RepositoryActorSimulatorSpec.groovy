package gov.nist.toolkit.webUITests.confActor

import com.gargoylesoftware.htmlunit.html.*
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.toolkitApi.DocumentRepository
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Timeout
/**
 * Created by skb1 on 6/5/2017.
 */
@Stepwise
@Timeout(360)
class RepositoryActorSimulatorSpec extends ConformanceActor {

    static final String simName = "rep" /* Sim names should be lowered cased */
    final String actorPage = String.format("%s/#ConfActor:default/%s/rep", toolkitBaseUrl, simUser)

    @Shared DocumentRepository repSim

    @Override
    void setupSim() {
        deleteOldRepSim()
        sleep(5000) // Why we need this -- Problem here is that the Delete request via REST could be still running before we execute the next Create REST command. The PIF Port release timing will be off causing a connection refused error in the Jetty log.
        repSim = createNewRepSim()
        sleep(5000) // Why we need this -- Problem here is that the Delete request via REST could be still running before we execute the next Create REST command. The PIF Port release timing will be off causing a connection refused error in the Jetty log.
    }

    @Override
    String getSimId() {
        return simUser + "__" + simName
    }

    void deleteOldRepSim() {
        getSpi().delete(simName, simUser)
    }

    DocumentRepository createNewRepSim() {
        return getSpi().createDocumentRepository(simName, simUser, "default")
    }



    // Repository actor specific
    def 'Get repository conformance actor page.'() {
        when:
        loadPage(actorPage)

        then:
        page != null
    }

    // Was there a previous SUT selected but doesn't exist now?
    def 'No weird popup or error message presented in a dialog box.'() {
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
        for (HtmlButton button : nodesIterator) {
            if (button.getTextContent().contains("Initialize Testing Environment")) {
                initializeBtn = button
            }
        }

        then:
        initializeBtn != null

        when:
        page = initializeBtn.click(false, false, false)
        webClient.waitForBackgroundJavaScript(maxWaitTimeInMills)

        while (!page.asText().contains("Initialization Complete")) {
            webClient.waitForBackgroundJavaScript(500)
        }

        then:
        page.asText().contains("Initialization Complete")

        // There are no tests for repository orchestration tests to look for.
    }

    def 'Update register endpoint using the supporting sim.'() {
        // However, we need to pick up the Supporting Environment Configuration and plug it in to the Repository Sim Config.

        when:
        List<HtmlTable> tableList = page.getByXPath("//table[contains(@class,'SupportingEnvironmentConfiguration')]")

        then:
        tableList!=null && tableList.size()==1

        when:
        String registerEndpoint = null
        HtmlTable supportingEnvironmentConfiguration = tableList.get(0)
        for (HtmlTableRow row : supportingEnvironmentConfiguration.getRows()) {
            for (HtmlTableCell cell : row.getCells()) {
                if (cell.asText()!=null) {
                    if (cell.asText() == "Register") {
                        if (row.getCell(2) != null) {
                           registerEndpoint = row.getCell(2).asText() // Index 2 should have the HTTP endpoint
                           break
                       }
                    } else {
                        continue
                    }
                }
            }
        }

        then:
        registerEndpoint!=null && registerEndpoint.length()>0

        when:
        SimConfig updatedConfig = updateRegistryEndpoint(registerEndpoint)
        String updatedEndpoint = null
        if (updatedConfig.isString(SimulatorProperties.registerEndpoint))
            updatedEndpoint = updatedConfig.asString(SimulatorProperties.registerEndpoint)

        then:
        updatedEndpoint == registerEndpoint
    }

    SimConfig updateRegistryEndpoint(String endPoint) {
//        repSimConfig.setProperty(SimulatorProperties.registerEndpoint, supportConfig.getConfigEle(SimulatorProperties.registerEndpoint).asString())
//        repSimConfig = spi.update(repSimConfig)

        SimConfig simConfig = repSim.getConfig()
        simConfig.setProperty(SimulatorProperties.registerEndpoint, endPoint)
        return getSpi().update(simConfig)
    }

    def 'Get (again) repository conformance actor page.'() {
        when:
        loadPage(actorPage)

        then:
        page != null
    }

    def 'Click Reset Environment (again) using defaults after updating the Register endpoint.'() {
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

    def 'Click Initialize (again) after updating the Register endpoint.'() {
        when:
        HtmlButton initializeBtn = null

        NodeList btnNl = page.getElementsByTagName("button")
        final Iterator<HtmlButton> nodesIterator = btnNl.iterator()
        for (HtmlButton button : nodesIterator) {
            if (button.getTextContent().contains("Initialize Testing Environment")) {
                initializeBtn = button
            }
        }

        then:
        initializeBtn != null

        when:
        page = initializeBtn.click(false, false, false)
        webClient.waitForBackgroundJavaScript(maxWaitTimeInMills)

        while (!page.asText().contains("Initialization Complete")) {
            webClient.waitForBackgroundJavaScript(500)
        }

        then:
        page.asText().contains("Initialization Complete")

        // There are no tests for repository orchestration tests to look for.
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
}