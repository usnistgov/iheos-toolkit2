package gov.nist.toolkit.webUITests.confActor

import com.gargoylesoftware.htmlunit.AjaxController
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.html.HtmlButton
import com.gargoylesoftware.htmlunit.html.HtmlDivision
import com.gargoylesoftware.htmlunit.html.HtmlOption
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlSelect
import com.gargoylesoftware.htmlunit.html.HtmlTextInput
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.webUITests.confActor.exceptions.TkWtNotFoundEx
import spock.lang.Shared
import spock.lang.Specification

abstract class ToolkitWebPage extends Specification  {
    @Shared WebClient webClient
    @Shared HtmlPage page
    @Shared int toolkitPort = 8888
    @Shared String toolkitHostName = "http://localhost"
    @Shared String toolkitBaseUrl
    @Shared SimulatorBuilder spi

    static final String simUser = "webuitest"


    static final int maxWaitTimeInMills = 60000* 2 // 2 minute (s)

    void composeToolkitBaseUrl() {
        this.toolkitBaseUrl = String.format("%s:%s", toolkitHostName, toolkitPort)
    }

    void setupSpi() {
        spi = new SimulatorBuilder(getToolkitBaseUrl())
    }

    void loadPage(String url) {
        System.out.println("Loading page: " + url)

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
            public boolean processSynchron(HtmlPage page, WebRequest request, boolean async)
            {
                return true
            }
        })
        webClient.waitForBackgroundJavaScript(maxWaitTimeInMills)
    }

    def setupSpec() {
        composeToolkitBaseUrl()
        setupSpi()
    }
    def cleanupSpec() {
    }
    def setup() {
    }
    def cleanup() {
    }


    HtmlOption selectOptionByValue(HtmlSelect htmlSelect, String optionValue) throws Exception {
        optionValue = optionValue.toLowerCase()
        List<HtmlOption> optionsList = htmlSelect.getOptions()
        for (HtmlOption optionElement : optionsList) {
            if (optionValue == optionElement.getText().toLowerCase() && optionValue == optionElement.getValueAttribute().toLowerCase()) {
                page = optionElement.setSelected(true)
                return optionElement
            }
        }

        throw new TkWtNotFoundEx()
    }


    List<HtmlDivision> getDialogBox() {
      return page.getByXPath("//div[contains(@class,'gwt-DialogBox')]")
    }

    void listHasOnlyOneItem(List<?> list) {
        assert list!=null && list.size()==1 // Should be only one
    }

    HtmlOption useTestSession(String sessionName) {
        List<HtmlSelect> selectList = page.getByXPath("//select[contains(@class, 'gwt-ListBox') and contains(@class, 'testSessionSelectorMc')]")  // Substring match. No other CSS class must contain this string.

        listHasOnlyOneItem(selectList)

        HtmlSelect sessionSelector = selectList.get(0)

        try {
            selectOptionByValue(sessionSelector, sessionName)
        } catch (TkWtNotFoundEx ex) {
            // Create new session if it doesn't exist.
            // Get the text box and enter, and save.
            List<HtmlTextInput> sessionInputs = page.getByXPath("//input[contains(@class, 'gwt-TextBox') and contains(@class, 'testSessionInputMc')]")  // Substring match. No other CSS class must contain this string.
            listHasOnlyOneItem(sessionInputs)

            HtmlTextInput sessionInput = sessionInputs.get(0)
            sessionInput.setValueAttribute(sessionName)

            List<HtmlButton> addButtonList = page.getByXPath("//button[contains(@class,'gwt-Button') and text()='Add']")
            listHasOnlyOneItem(addButtonList)

            HtmlButton addButton = addButtonList.get(0)
            webClient.waitForBackgroundJavaScript(1000)
            page = addButton.click()

            selectOptionByValue(sessionSelector, sessionName)
        }
    }

}
