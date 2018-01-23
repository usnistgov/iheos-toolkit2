/**
 *
 */
package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestResultsCommand;
import gov.nist.toolkit.xdstools2.client.command.command.RunMesaTestCommand;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetImagingDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestResultsRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.RunTestRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * toolkit tab class for IIG SUT testing
 *
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class RIGTestTab extends GenericQueryTab implements GatewayTool {
    //    final protected ToolkitServiceAsync toolkitService = GWT
//            .create(ToolkitService.class);
    String selectedActor = ActorType.RESPONDING_IMAGING_GATEWAY.getShortName();
    List<SimulatorConfig> rgConfigs;
    GenericQueryTab genericQueryTab;
    static final String COLLECTION_NAME =  "rigtool";
    final TestSelectionManager testSelectionManager;

    public RIGTestTab() {
        super(new GetImagingDocumentsSiteActorManager());
        testSelectionManager = new TestSelectionManager(this);
    }

//    @Override
//    public ToolkitServiceAsync getToolkitService() { return toolkitService; }

    @Override
    public TabContainer getToolContainer() { return getTabContainer(); }

    public void onTabLoad(TabContainer container, boolean select) {
    }

    @Override
    protected Widget buildUI() {
        return null;
    }

    @Override
    protected void bindUI() {

    }

    @Override
    protected void configureTabView() {

    }

    @Override
    public void onTabLoad(boolean select, String eventName) {
        genericQueryTab = this;
        registerTab(select, eventName);  // link into container/tab management

        tlsOptionEnabled = false;

        genericQueryTab.reloadTransactionOfferings();

        // customization of GenericQueryTab
        autoAddRunnerButtons = false;  // want them in a different place
        genericQueryTitle = "Select System Under Test";
        genericQueryInstructions = new HTML(

                "<p>When the test is run a Cross Gateway Retrieve Imaging Document " +
                        "Set (RAD-75) transaction will be sent to the Responding Imaging " +
                        "Gateway selected below. This will start the test. Before running " +
                        "a test, make sure your Responding Imaging Gateway is configured " +
                        "to query the Imaging Document Sources above, and has the noted. " +
                        "Home Community UUID.</p>" +

                        "<p> This tool uses only non-TLS endpoints. TLS selection is disabled.</p>" +

                        "<p>It may be necessary to refresh the selection list at times.  " +
                        "The Reload button at the top of the screen performs this refresh.</p>"

        );
        addResultsPanel = false;  // manually done below

        ////////////////////////////////////////////////////////////////////////////////////////////////
        tabTopPanel.add(new HTML("<h1>Responding Imaging Gateway Test Tool</h1>"));

        Image initiatingGatewayDiagram=new Image();
        initiatingGatewayDiagram.setUrl("diagrams/RIGdiagram.png");
        initiatingGatewayDiagram.setHeight("300px");
        tabTopPanel.add(initiatingGatewayDiagram);

        tabTopPanel.add(new HTML(

                "<p>This tool tests a Responding Imaging Gateway.  The tests are " +
                        "driven by an Initiating Imaging Gateway simulator. The Responding " +
                        "Imaging Gateway System Under Test (SUT) will be configured to " +
                        "relay requests to three Imaging Document Sources. This tool " +
                        "supplies the Initiating Imaging Gateway and Imaging Document Sources " +
                        "as Toolkit supported simulators. Each of the Imaging Document Sources " +
                        "is loaded with supporting test data. </p>" +

                        "<h2>Create supporting test session</h2>" +

                        "<p>These simulators and their logs will be maintained in a test " +
                        "session you create for this test. At the top of the window, " +
                        "create a new test session and select it. </p>"
        ));

        ////////////////////////////////////////////////////////////////////////////////////////////////
        tabTopPanel.add(new HTML(

                "<hr />" +

                        "<h2>Build Test Environment</h2>" +

                        "<p>The Build Test Environment button will create the necessary " +
                        "simulators to test your Responding Imaging Gateway:  an Initiating " +
                        "Imaging Gateway to drive the test, and three Imaging Document Sources to " +
                        "service requests from your Responding Imaging Gateway. The " +
                        "generated test configuration will be displayed below. Once the " +
                        "test environment is built, configure your Responding Imaging " +
                        "Gateway to forward requests to the three generated Imaging Document Sources.</p>"

        ));

        HorizontalPanel testEnvironmentsPanel = new HorizontalPanel();
        tabTopPanel.add(testEnvironmentsPanel);

        new BuildRIGTestOrchestrationButton(this, testEnvironmentsPanel, "Build Test Environment", false);


        ////////////////////////////////////////////////////////////////////////////////////////////////
        // Query boilerplate
        ActorType act = ActorType.findActor(selectedActor);

        List<TransactionType> tt = act.getTransactions();

        // has to be before addQueryBoilerplate() which
        // references mainGrid
        mainGrid = new FlexTable();

        queryBoilerplate = addQueryBoilerplate(
                new Runner(),
                tt,
                new CoupledTransactions(),
                false  /* display patient id param */);

        tabTopPanel.add(testSelectionManager.buildTestSelector());

        tabTopPanel.add(testSelectionManager.buildSectionSelector());

        tabTopPanel.add(mainGrid);

        testSelectionManager.loadTestsFromCollection(COLLECTION_NAME);


        ////////////////////////////////////////////////////////////////////////////////////////////////
        tabTopPanel.add(new HTML(

                "<hr />" +

                        "<h2>Run Test</h2>" +

                        "<p>Initiate the test from the Toolkit Image Document Consumer. " +
                        "After the test is run the Image Document Consumer's logs can be " +
                        "displayed with Inspect Results.</p>"
        ));

        addRunnerButtons(tabTopPanel);

        tabTopPanel.add(resultPanel);
    }

    class Runner implements ClickHandler {

        public void onClick(ClickEvent event) {

            try {

                resultPanel.clear();

                if (getCurrentTestSession().isEmpty()) {
                    new PopupMessage("Test Session must be selected");
                    return;
                }

                if (!verifySiteProvided()) return;

//         addStatusBox();
//         getGoButton().setEnabled(false);
//         getInspectButton().setEnabled(false);

                Map <String, String> parms = new HashMap <>();
//         parms.put("$testdata_home$", rgConfigs.get(0).get(SimulatorProperties.homeCommunityId).asString());

                Panel logLaunchButtonPanel = rigForRunning();
                logLaunchButtonPanel.clear();
                logLaunchButtonPanel.add(testSelectionManager.buildLogLauncher(rgConfigs));
                String testToRun = selectedTest;
                if (TestSelectionManager.ALL.equals(testToRun)) {
                    testToRun = "tc:" + COLLECTION_NAME;
                }

                TestInstance testInstance = new TestInstance(testToRun);
                testInstance.setTestSession(new TestSession(getCurrentTestSession()));
                new RunMesaTestCommand(){
                    @Override
                    public void onComplete(List<Result> result) {
                        queryCallback.onSuccess(result);
                    }
                }.run(new RunTestRequest(getCommandContext(),getSiteSelection(),new TestInstance(testToRun),parms,true,testSelectionManager.getSelectedSections()));

            } catch (Exception e) {
                new PopupMessage(e.getMessage());
            }
        }
    }

    Button addTestEnvironmentInspectorButton(final String siteName) {
        return addTestEnvironmentInspectorButton(siteName, "Inspect Test Data - " + siteName);
    }

    Button addTestEnvironmentInspectorButton(final String siteName, String label) {
        Button button = new Button(label);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                List<TestInstance> tests = new ArrayList<TestInstance>();
                tests.add(new TestInstance("15807"));
                new GetTestResultsCommand(){
                    @Override
                    public void onComplete(Map<String, Result> stringResultMap) {
                        Result result = stringResultMap.get("15807");
                        if (result == null) {
                            new PopupMessage("Results not available");
                            return;
                        }
                        SiteSpec siteSpec = new SiteSpec(siteName, ActorType.RESPONDING_IMAGING_GATEWAY, null, new TestSession(getCurrentTestSession()));

                        MetadataInspectorTab itab = new MetadataInspectorTab();
                        List<Result> results = new ArrayList<Result>();
                        results.add(result);
                        itab.setResults(results);
                        itab.setSiteSpec(siteSpec);
                        itab.onTabLoad(true, "Insp");
                    }
                }.run(new GetTestResultsRequest(getCommandContext(),tests));
            }
        });
        return button;
    }

    public String getWindowShortName() {
        return "rigtests";
    }

}
