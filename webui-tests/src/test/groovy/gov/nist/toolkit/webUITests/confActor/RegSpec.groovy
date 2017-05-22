package gov.nist.toolkit.webUITests.confActor

import com.gargoylesoftware.htmlunit.AjaxController
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.html.*
import org.w3c.dom.html.HTMLElement
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class RegSpec extends Specification {
    @Shared
    WebClient webClient
    @Shared
    HtmlPage page
    static final int maxWaitTimeInMills = 60000*5


    def setupSpec() {
        webClient = new WebClient(BrowserVersion.FIREFOX_52)



        page = webClient.getPage("http://localhost:8888/#ConfActor:default/default/reg")



//         page = webClient.getPage("http://localhost:8888/Xdstools2.html#ConfActor:default/default/reg");
//           page = webClient.getPage("http://localhost:8888/")
//        page = webClient.getPage("http://ihexds.nist.gov:12093/xdstools4/#ConfActor:default/default/reg");


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


    }
    def cleanupSpec() {
    }
    def setup() {
    }
    def cleanup() {
    }

    def 'Test Website Title'() {
        setup:
        println "Test Website title"

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

    def 'Click Reset (or Initialize) Environment using defaults'() {
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

        then:
        HtmlCheckBoxInput resetCkbx = page.getElementById(resetLabel.getForAttribute())
        resetCkbx.isChecked()
    }

    def 'Click Initialize'() {
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
        List<HTMLElement> elementList = page.getByXPath("//div[contains(@class, 'orchestrationTest') and contains(@class, 'testOverviewHeaderFail')]")  // Substring match, other CSS class must not contain this string.
        // Use this for order dependent selection: "//div[@class='testOverviewHeaderFail orchestrationTest']")

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


    def 'Test Registry ConfActor RunAll'() {

        when:
        List<HTMLElement> elementList = page.getByXPath("//div[contains(@class,'gwt-DialogBox')]")

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

        for (HtmlImage img : nodesIterator) {
               if ("icons2/play-32.png".equals(img.getSrcAttribute())) { // The big Run All HTML image button
                   println "img src is --> " + img.getSrcAttribute()
                   page = img.click(false,false,false)
                   webClient.waitForBackgroundJavaScript(30000)

                   println "Running"
                   while(page.asText().contains("Running...")){
                       print "."
                       webClient.waitForBackgroundJavaScript(500)
                   }
                   println "done."
                   break
               }
       }

        then:
        page != null
        int failuresIdx2 = page.asText().indexOf("Failures")
        println page.asText().substring(failuresIdx2,failuresIdx2+20)

    }


}