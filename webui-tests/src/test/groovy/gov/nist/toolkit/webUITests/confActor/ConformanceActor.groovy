package gov.nist.toolkit.webUITests.confActor

import com.gargoylesoftware.htmlunit.html.*
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Timeout
/**
 * Created by skb1 on 6/5/2017.
 */
@Stepwise
abstract class ConformanceActor extends ToolkitWebPage {

    @Shared String actorPage
    @Shared int testCount
    abstract void setupSim()
    abstract String getSimId()

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

    def 'Select session.'() {
        when:
        loadPage(String.format("%s/#Tool:toolId=Simulators",toolkitBaseUrl))
        HtmlOption newlySelectedSessionOption = useTestSession(simUser)

        then:
        newlySelectedSessionOption !=null
        newlySelectedSessionOption.isSelected()
    }

    def 'Check if Simulators page contains the newly created actor sim.'() {
        expect:
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
        webClient.waitForBackgroundJavaScript(2000)

        then:
        page.asText().contains("Test Context")

    }

    def 'Cancel off any unexpected dialog box.'() {
        when:
        List<HtmlDivision> divList = getDialogBox()
        boolean unexpectedDialogBox = divList!=null && divList.size()==1
        if (unexpectedDialogBox) {
            println "Error: unexpected dialog box found! --> " + divList.toString()
            List<HtmlButton> okButtonList = page.getByXPath("//button[contains(@class,'gwt-Button') and text()='Ok']")
            listHasOnlyOneItem(okButtonList)
            page = okButtonList.get(0).click()
        }
        // Cancel it of
        divList = getDialogBox()
        unexpectedDialogBox = divList!=null && divList.size()==1

        // No more dialog boxes should be there
        then:
        !unexpectedDialogBox
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

    def 'Select the test session in Test Context.'() {
        when:
        System.out.println("in test context TS selector.")
        List<HtmlSelect> selectList = page.getByXPath("//select[contains(@class, 'gwt-ListBox') and contains(@class, 'confActorTestSessionSelectorMc')]")  // Substring match. No other CSS class must contain this string.

        then:
        selectList!=null && selectList.size()==1 // Should be only one

        when:
        HtmlSelect tsSelector = selectList.get(0)

        List<HtmlOption> optionsList= tsSelector.getOptions()
        HtmlOption tsOption = null
        for (HtmlOption optionElement : optionsList) {
            if (simUser == optionElement.getText() && simUser == optionElement.getValueAttribute()) {
                page = optionElement.setSelected(true)
                tsOption = optionElement
                break
            }
        }

        then:
        tsOption !=null
        tsOption.isSelected()

    }

    def 'Select the newly created sim.'() {
        when:
        List<HtmlSelect> selectList = page.getByXPath("//select[contains(@class, 'gwt-ListBox') and contains(@class, 'confActorSutSelectorMc')]")  // Substring match. No other CSS class must contain this string.

        then:
        selectList!=null && selectList.size()==1 // Should be only one

        when:
        HtmlSelect sutSelector = selectList.get(0)

        List<HtmlOption> optionsList= sutSelector.getOptions()
        HtmlOption newlyCreatedSutOption = null
        for (HtmlOption optionElement : optionsList) {
            if (getSimId() == optionElement.getText() && getSimId() == optionElement.getValueAttribute()) {
                page = optionElement.setSelected(true)
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
        page != null

    }

    def 'Verify SUT selection'() {
        when:
        loadPage(actorPage)

        then:
        page != null
        page.asText().contains("SUT: " + getSimId())
    }

}