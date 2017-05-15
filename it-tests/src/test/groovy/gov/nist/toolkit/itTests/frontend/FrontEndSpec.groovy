package gov.nist.toolkit.itTests.frontend

import com.gargoylesoftware.htmlunit.AjaxController
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.html.HtmlImage
import com.gargoylesoftware.htmlunit.html.HtmlPage
import spock.lang.Shared
import spock.lang.Specification

class FrontEndSpec extends Specification {
    @Shared
    WebClient webClient
    @Shared
    HtmlPage page


    def setupSpec() {
        webClient = new WebClient(BrowserVersion.FIREFOX_52);

        webClient.setAjaxController(new AjaxController(){
            @Override
            public boolean processSynchron(HtmlPage page, WebRequest request, boolean async)
            {
                return true;
            }
        });

        webClient.setJavaScriptTimeout(600000)
        webClient.waitForBackgroundJavaScript(600000)
        page = webClient.getPage("http://ihexds.nist.gov:12093/xdstools4/#ConfActor:default/default/reg");
    }
    def cleanupSpec() {
    }
    def setup() {
    }
    def cleanup() {
    }

    def 'Test Access'() {
        when:
        final xdsTitle = "XDS Toolkit"

        then:
        xdsTitle == page.getTitleText()
    }

    def 'Test FindElement'() {
        when:
        HtmlPage page2 = null
        while(page.asText().contains("Initializing...") || page.asText().contains("Loading...")){
            webClient.waitForBackgroundJavaScript(500)
        }

        and:
        NodeList imgNl = page.getElementsByTagName("img")
        final Iterator<HtmlImage> nodesIterator = imgNl.iterator()
        // now iterate

        and:
        println "begin page.asText()"
        int failuresIdx = page.asText().indexOf("Failures")
        println page.asText().substring(failuresIdx,failuresIdx+20)
        println "end."
        for (HtmlImage img : nodesIterator) {
               if ("icons2/play-32.png".equals(img.getSrcAttribute())) {
                   println "img src is --> " + img.getSrcAttribute()
                   page2 = img.click(false,false,false);
                   webClient.waitForBackgroundJavaScript(600000)

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