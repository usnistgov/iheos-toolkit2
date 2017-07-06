package gov.nist.toolkit.webUITests.confActor

import com.gargoylesoftware.htmlunit.AjaxController
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.html.HtmlAnchor
import com.gargoylesoftware.htmlunit.html.HtmlButton
import com.gargoylesoftware.htmlunit.html.HtmlDivision
import com.gargoylesoftware.htmlunit.html.HtmlOption
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlSelect
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Timeout
/**
 * Created by skb1 on 6/5/2017.
 */
@Stepwise
@Timeout(120)
abstract class ConformanceActor extends Specification implements ConformanceActorSimulatorIntf {
    @Shared WebClient webClient
    @Shared HtmlPage page
    @Shared int toolkitPort = 8888
    @Shared String toolkitHostName = "http://localhost"
    @Shared String toolkitBaseUrl
    @Shared SimulatorBuilder spi

    static final int maxWaitTimeInMills = 60000*5 // 5 minutes

    void composeToolkitBaseUrl() {
        this.toolkitBaseUrl = String.format("%s:%s", toolkitHostName, toolkitPort)
    }


    def setupSpec() {
        composeToolkitBaseUrl()
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

        webClient.getCache().clear();
        webClient.setAjaxController(new AjaxController(){
            @Override
            public boolean processSynchron(HtmlPage page, WebRequest request, boolean async)
            {
                return true;
            }
        });
        webClient.waitForBackgroundJavaScript(maxWaitTimeInMills)
    }

    void setupSpi() {
        spi = new SimulatorBuilder(getToolkitBaseUrl())
    }



    def 'Check if Simulators page contains the newly created Rep actor sim.'() {
        when:
        loadPage(String.format("%s/#Tool:Simulators",toolkitBaseUrl))

        then:
        page.asText().contains("Simulator Manager") and page.asText().contains("Add new simulator to this test session")
        page.asText().contains(getSimId())
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
            if (getSimId() == optionElement.getText() && getSimId() == optionElement.getValueAttribute()) {
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
        page.asText().contains("SUT: " + getSimId())
    }



}