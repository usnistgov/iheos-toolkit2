package gov.nist.toolkit.webUITests

import com.gargoylesoftware.htmlunit.html.*
import gov.nist.toolkit.actortransaction.shared.ActorType
import spock.lang.Stepwise
import spock.lang.Timeout

@Stepwise
@Timeout(612)
class A1_RunFirst_SimulatorManagerSpec extends ToolkitWebPage {

    def setupSpec() {
        // Load sim man page here.
        // http://127.0.0.1:8888/Xdstools2.html#Tool:toolId=Simulators
        // localhost:8080/xdstools2-7.7.0-SNAPSHOT/#Tool:toolId=Simulators;env=default;testSession=manualwebuitest;
        loadPage(String.format("%s/#Tool:toolId=Simulators;env=default;testSession=%s;",toolkitBaseUrl, ToolkitWebPage.testSessionName))
    }

    def 'No unexpected popup after initial page load'() {
        when:
        List<HtmlDivision> divList = getDialogBox()

        then:
        divList!=null && divList.size()==0
    }

    /*
    def 'Use test session.'() {
        when:
        HtmlOption newlySelectedSessionOption = useTestSession(testSessionName)

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
    */


    def 'Check sim man page title'() {
        expect:
        page.getVisibleText().contains("Simulator Manager") and page.getVisibleText().contains("Add new simulator to this test session")
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
                    if (
//                    testSimActorShortName.equals(ActorType.EDGE_SERVER.getShortName()) ||
                            testSimActorShortName.equals(ActorType.ISR.getShortName())  ||
                            testSimActorShortName.equals(ActorType.RSNA_EDGE_DEVICE.getShortName())
                        /*
                        We ignore certain actors because we get these types of errors on Toolkit 7:
Cannot build simulator of type RSNA Image Sharing Source - cannot find Factory for ActorType
Exception Exception
	at gov.nist.toolkit.simcommon.server.AbstractActorFactory.buildNewSimulator(AbstractActorFactory.java:203)
	at gov.nist.toolkit.simcommon.server.AbstractActorFactory.buildNewSimulator(AbstractActorFactory.java:183)
	at gov.nist.toolkit.simcommon.server.GenericSimulatorFactory.buildNewSimulator(GenericSimulatorFactory.java:30)
	at gov.nist.toolkit.services.server.SimulatorApi.create(SimulatorApi.java:39)
	at gov.nist.toolkit.services.server.SimulatorServiceManager.getNewSimulator(SimulatorServiceManager.java:289)
	at gov.nist.toolkit.xdstools2.server.ToolkitServiceImpl.getNewSimulator(ToolkitServiceImpl.java:1415)
Error: This simulator type could not be created --> webuitest__ris
                         */
                    ) {
                        println("Skipping actor type (short name) due to known defect: " + testSimActorShortName)
                        defectCt++
                        continue
                    }
                    String simId = ToolkitWebPage.testSessionName + "__" + testSimActorShortName

                    getSpi().delete(testSimActorShortName, ToolkitWebPage.testSessionName)
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
                    } else {
                        println(simId + " was created successfully.")
                        addedSims++
                    }

//                    if (page.getVisibleText().contains(simId)) { This method doesn't take into consideration that Simulator list can span multiple pages
//                    }
                }
            }
        }
        if (addedSims==0)
            println page.getVisibleText()

        then:
        addedSims == actorCt - defectCt // If this failed, a good place to check is the log for "ThreadPoolExhaustedException". In this case, increase your Listener_Port_Range setting in Toolkit.properties.
    }
}
