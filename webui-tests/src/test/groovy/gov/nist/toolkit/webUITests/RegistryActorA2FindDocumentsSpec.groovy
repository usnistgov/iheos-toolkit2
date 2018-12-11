package gov.nist.toolkit.webUITests

import com.gargoylesoftware.htmlunit.html.*
import gov.nist.toolkit.webUITests.confActor.RegistryActorA1SimulatorSpec
import gov.nist.toolkit.webUITests.confActor.ToolkitWebPage
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Timeout

@Stepwise
@Timeout(360)
class RegistryActorA2FindDocumentsSpec extends ToolkitWebPage {

    @Shared String patientId

    def setupSpec() {
        // Load registry page here. We'll need to extract Patient Id used for tests.
        loadPage(String.format("%s/#ConfActor:env=default;testSession=%s;actor=reg;systemId=%s",toolkitBaseUrl,testSessionName,RegistryActorA1SimulatorSpec.simName))
    }

    def 'No unexpected popup after initial page load'() {
        when:
        List<HtmlDivision> divList = getDialogBox()

        then:
        divList!=null && divList.size()==0
    }

    def 'Check Conformance page loading status and its title.'() {
        when:
        while(page.asText().contains("Initializing...")){
            webClient.waitForBackgroundJavaScript(maxWaitTimeInMills)
        }

        while(!page.asText().contains("complete")){
            webClient.waitForBackgroundJavaScript(500)
        }

        then:
        "XDS Toolkit" == page.getTitleText()
        page.asText().contains("complete")
    }

    def 'Find the Patient Id Div and Extract the Id text.'() {
        when:
        List<HtmlDivision> divList = page.getByXPath("//div[contains(@class, 'gwt-HTML') and starts-with(., 'Patient ID for Register tests:')]")  // Substring CSS match. No other CSS class must contain this string.

        listHasOnlyOneItem(divList)

        HtmlDivision patientIdDiv = divList.get(0)

        String[] strings = patientIdDiv.getTextContent().split(":")

        then:
        strings.length == 2
        strings[1] != null

        when:
        patientId = strings[1].trim()

        then:
        patientId != ""
    }

    def 'Load FindDocuments tool page'() {
        when:
        loadPage(String.format("%s/#Tool:toolId=FindDocuments;env=default;testSession=%s;",toolkitBaseUrl, testSessionName))

        then:
        page.asText().contains('Find Documents Stored Query')
    }

    def 'Find Patient Id text box and paste Patient Id into the text box.'() {
        when:
        List<HtmlTextInput> patientIdInputs = page.getByXPath("//input[contains(@class, 'gwt-TextBox') and contains(@class, 'patientIdInputMc')]")  // Substring match. No other CSS class must contain this string.

        then:
        listHasOnlyOneItem(patientIdInputs)

        HtmlTextInput patientIdInput = patientIdInputs.get(0)
        patientIdInput.setValueAttribute(patientId)
    }

    def 'Find and select registry site'() {
        when:
        List<HtmlLabel> labelList = page.getByXPath(String.format("//label[text()='%s']", RegistryActorA1SimulatorSpec.getSimId()))

        then:
        listHasOnlyOneItem(labelList)

        when:
        HtmlLabel label = labelList.get(0)
        String forOptionId = label.getForAttribute()

        then:
        forOptionId != null

        when:
        HtmlOption siteOption = page.getElementById(forOptionId)

        then:
        siteOption.setSelected(true)
    }

    def 'Find and click Run button'() {
        when:
        List<HtmlButton> addButtonList = page.getByXPath("//button[contains(@class,'gwt-Button') and text()='Run']")
        listHasOnlyOneItem(addButtonList)

        HtmlButton runButton = addButtonList.get(0)
        webClient.waitForBackgroundJavaScript(1000)
        page = runButton.click()
        webClient.waitForBackgroundJavaScript(maxWaitTimeInMills)

        while(!page.asText().contains("Status: Success")){
            webClient.waitForBackgroundJavaScript(500)
        }

        then:
        page.asText().contains("Status: Pass")
    }



}
