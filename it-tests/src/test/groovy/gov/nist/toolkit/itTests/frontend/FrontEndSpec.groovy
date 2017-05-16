package gov.nist.toolkit.itTests.frontend

import com.gargoylesoftware.htmlunit.AjaxController
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.html.HtmlImage
import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.w3c.dom.html.HTMLElement
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class FrontEndSpec extends Specification {
    @Shared
    WebClient webClient
    @Shared
    HtmlPage page


    def setupSpec() {
        webClient = new WebClient(BrowserVersion.FIREFOX_52)



        page = webClient.getPage("http://localhost:8888/#ConfActor:default/default/reg")



//         page = webClient.getPage("http://localhost:8888/Xdstools2.html#ConfActor:default/default/reg");
//           page = webClient.getPage("http://localhost:8888/")
//        page = webClient.getPage("http://ihexds.nist.gov:12093/xdstools4/#ConfActor:default/default/reg");


        webClient.getOptions().setJavaScriptEnabled(true)
        webClient.getOptions().setTimeout(60000*5)
        webClient.setJavaScriptTimeout(60000*5)
        webClient.waitForBackgroundJavaScript(60000*5)
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
        when:
        final xdsTitle = "XDS Toolkit"

        then:
        println "Test Website title is running"
        xdsTitle == page.getTitleText()
    }


    def 'Test Registry ConfActor RunAll'() {
        when:
        HtmlPage page2 = null

        List<HTMLElement> elementList = page.getByXPath("//div[@class='gwt-DialogBox']")
        if (elementList!=null && elementList.size()>0) {
            println "*** Found an unexpected dialogBox!!" + elementList.size()
            HTMLElement el = elementList.get(0)
            println el.getTextContent()
            println el.toString()
        }


        boolean waitingMessageFound = false
        while(page.asText().contains("Initializing...") || page.asText().contains("Loading...")){
            waitingMessageFound = true
            webClient.waitForBackgroundJavaScript(500)
        }

        if (!waitingMessageFound) {
            print page.asXml()
            println "waitingMessage is not Found. Retrying"
            while(!page.asText().contains("Testing Environment") && page.asText().contains("Option")){
                webClient.waitForBackgroundJavaScript(1000)
            }
            print "done."
        }


        NodeList imgNl = page.getElementsByTagName("img")
        final Iterator<HtmlImage> nodesIterator = imgNl.iterator()
        // now iterate

        println "begin page.asText()"
        println page.asText()
        int failuresIdx = page.asText().indexOf("Failures")
        println page.asText().substring(failuresIdx,failuresIdx+20)
        println "end."
        for (HtmlImage img : nodesIterator) {
               if ("icons2/play-32.png".equals(img.getSrcAttribute())) { // The big Run All HTML image button
                   println "img src is --> " + img.getSrcAttribute()
                   page2 = img.click(false,false,false)
                   webClient.waitForBackgroundJavaScript(60000)

                   println "Running"
                   while(page2.asText().contains("Running...")){
                       print "."
                       webClient.waitForBackgroundJavaScript(500)
                   }
                   println "done."

               }
       }

        then:
        page2 != null
        int failuresIdx2 = page2.asText().indexOf("Failures")
        println page2.asText().substring(failuresIdx2,failuresIdx2+20)

    }


}