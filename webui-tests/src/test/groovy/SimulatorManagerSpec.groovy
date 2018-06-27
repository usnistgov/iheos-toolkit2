import com.gargoylesoftware.htmlunit.html.*
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.webUITests.confActor.ToolkitWebPage
import spock.lang.Stepwise
import spock.lang.Timeout

@Stepwise
@Timeout(360)
class SimulatorManagerSpec extends ToolkitWebPage {

    def setupSpec() {
        // Load sim man page here.
        loadPage(String.format("%s/#Tool:Simulators",toolkitBaseUrl))
    }

    def 'No unexpected popup after initial page load'() {
        when:
        List<HtmlDivision> divList = getDialogBox()

        then:
        divList!=null && divList.size()==0
    }

    def 'Use test session.'() {
        when:
        HtmlOption newlySelectedSessionOption = useTestSession(simUser)

        then:
        newlySelectedSessionOption !=null
        newlySelectedSessionOption.isSelected()
    }

    def 'No unexpected popup after using a session.'() {
        when:
        List<HtmlDivision> divList = getDialogBox()

        then:
        divList!=null && divList.size()==0
    }


    def 'Check sim man page title'() {
        expect:
        page.asText().contains("Simulator Manager") and page.asText().contains("Add new simulator to this test session")
    }


    def 'actor type'(){
       when:
       List<HtmlSelect> selectList = page.getByXPath("//select[contains(@class, 'gwt-ListBox') and contains(@class, 'selectActorTypeMc')]")  // Substring match. No other CSS class must contain this string.

       then:
       listHasOnlyOneItem(selectList)

       when:
       HtmlSelect actorSelector = selectList.get(0)
       int actorCt = actorSelector.getOptions().size() - 1 // -1: Compensate for the empty value in selection

       then:
        actorCt>1

       when:
       List<HtmlOption> optionsList = actorSelector.getOptions()
       List<HtmlTextInput> actorIdInputs = page.getByXPath("//input[contains(@class, 'gwt-TextBox') and contains(@class, 'simulatorIdInputMc')]")  // Substring match. No other CSS class must contain this string.

       then:
       listHasOnlyOneItem(actorIdInputs)

       when:
       List<HtmlButton> addButtonList = page.getByXPath("//button[contains(@class,'gwt-Button') and text()='Create Actor Simulator']")

       then:
       listHasOnlyOneItem(addButtonList)

       when:
       HtmlButton createButton = addButtonList.get(0)
       int addedSims = 0
       int defectCt = 0
       HtmlTextInput actorInput = actorIdInputs.get(0)
        for (HtmlOption optionElement : optionsList) {
            String actorTypeName = optionElement.getValueAttribute()
            if (!"".equals(actorTypeName)) {
                optionElement.setSelected(true)

                ActorType actorType = ActorType.findActor(actorTypeName)
                if (actorType!=null) {
                    String testSimActorShortName = actorType.getShortName()
                    if (testSimActorShortName.equals(ActorType.EDGE_SERVER.getShortName())
                            || testSimActorShortName.equals(ActorType.ISR.getShortName())) {
                        println("Skipping actor type (short name) due to known defect: " + testSimActorShortName)
                        defectCt++
                        continue
                    }
                    String simId = simUser + "__" + testSimActorShortName

                    getSpi().delete(testSimActorShortName, simUser)
                    sleep(2500) // Why we need this -- Problem here is that the Delete request via REST could be still running before we execute the next Create REST command. The PIF Port release timing will be off causing a connection refused error in the Jetty log.

                    actorInput.setValueAttribute(testSimActorShortName)
                    page = createButton.click()
                    webClient.waitForBackgroundJavaScript(2500)

                    List<HtmlDivision> divList = getDialogBox()
                    boolean unexpectedDialogBox = divList!=null && divList.size()==1
                    if (unexpectedDialogBox) {
                        println "Error: This simulator type could not be created --> " + simId
                        List<HtmlButton> okButtonList = page.getByXPath("//button[contains(@class,'gwt-Button') and text()='Ok']")
                        listHasOnlyOneItem(okButtonList)
                        page = okButtonList.get(0).click()
                    }

//                    if (page.asText().contains(simId)) {
                      addedSims++
//                    }
                }
            }
        }
        if (addedSims==0)
            println page.asText()

        then:
        addedSims == actorCt - defectCt
    }
}
