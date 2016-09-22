package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetImagingDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * toolkit tab class for RSNA Imaging Source SUT testing
 * 
 * @author Matt Kelsey / MIR WUSTL IHE Development Project <a
 * href="mailto:kelseym@wustl.edu">kelseym@wustl.edu</a>
 *
 */
public class RSNAEdgeTestTab extends GenericQueryTab implements GatewayTool {
//    final protected ToolkitServiceAsync toolkitService = GWT
//            .create(ToolkitService.class);
    String selectedActor = ActorType.RSNA_EDGE_DEVICE.getShortName();
    SimulatorConfig config;
    GenericQueryTab genericQueryTab;
    static final String COLLECTION_NAME =  "rsnaedgetool";
    final TestSelectionManager testSelectionManager;

    public RSNAEdgeTestTab() {
        super(new GetImagingDocumentsSiteActorManager()); //TODO: Update to correct ActorManager
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
           
           "<p><strike>When the test is run a Retrieve Imaging Document Set (RAD-69) " +
           "transaction will be sent to the Initiating Imaging Gateway " +
           "selected below. This will start the test. Before running a test, " +
           "make sure your Initiating Imaging Gateway is configured to send " +
           "to the Responding Imaging Gateways above. </stike></p>" +
                        
           "<p> This tool uses only non-TLS endpoints. TLS selection is disabled.</p>" +

           "<p>It may ne necessary to refresh the selection list at times.  " +
           "The Reload button at the top of the screen performs this refresh.</p>"

        );
        addResultsPanel = false;  // manually done below

        ////////////////////////////////////////////////////////////////////////////////////////////////
        tabTopPanel.add(new HTML("<h1>RSNA Edge Device Test Tool</h1>"));

        Image initiatingGatewayDiagram=new Image();
        initiatingGatewayDiagram.setUrl("diagrams/RSNAEdgeDiagram.png");
        initiatingGatewayDiagram.setHeight("300px");
        tabTopPanel.add(initiatingGatewayDiagram);

        tabTopPanel.add(new HTML(
           
           "<p>This tool tests an RSNA Edge Device.  <strike>The tests are " +
           "driven by an Imaging Document Consumer simulator. The Initiating " +
           "Imaging Gateway System Under Test (SUT) will be configured to " +
           "relay requests to three Responding Imaging Gateways. This tool " +
           "supplies the Imaging Document Consumer and Responding Imaging " +
           "Gateways as Toolkit supported simulators. Each of the Responding " +
           "Imaging Gateways is backed by one or more Imaging Document " +
           "Source actors loaded with supporting test data.</strike> </p>" +

           "<h2>Create supporting test session</h2>" +
                
           "<p>These simulators and their logs will be maintained in a test " +
           "session you create for this test. At the top of the window, " +
           "create a new test session and select it. </p>"
        ));

        ////////////////////////////////////////////////////////////////////////////////////////////////
        tabTopPanel.add(new HTML(
                
           "<hr />" +
           
           "<h2>Build Test Environment</h2>" +
           
           "<p><The Build Test Environment button will create the necessary " +
           "simulators to test your RSNA Edge Device:  <strike>an Imaging " +
           "Document Consumer to drive the test, three Responding Imaging " +
           "Gateways, along with necessary Imaging Document Sources to " + 
           "service requests from your Initiating Imaging Gateway. The " +
           "generated test configuration will be displayed below. Once the " +
           "test environment is built, configure your Initiating Imaging " +
           "Gateway to forward requests to the three generated Responding " +
           "Imaging Gateways.</strike></p>"

        ));

        HorizontalPanel testEnvironmentsPanel = new HorizontalPanel();
        tabTopPanel.add(testEnvironmentsPanel);

        new BuildRSNAEdgeTestOrchestrationButton(this, testEnvironmentsPanel, "Build Test Environment", false);


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
            resultPanel.clear();

			if (getCurrentTestSession().isEmpty()) {
				new PopupMessage("Test Session must be selected");
				return;
			}

            if (!verifySiteProvided()) return;

			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

            Map<String, String> parms = new HashMap<>();
            parms.put("$testdata_home$", config.get(SimulatorProperties.homeCommunityId).asString());

            Panel logLaunchButtonPanel = rigForRunning();
            logLaunchButtonPanel.clear();
//            List configs = Arrays.asList(config);
//            logLaunchButtonPanel.add(testSelectionManager.buildLogLauncher(configs));
            String testToRun = selectedTest;
            if (TestSelectionManager.ALL.equals(testToRun)) {
                testToRun = "tc:" + COLLECTION_NAME;
            }

            TestInstance testInstance = new TestInstance(testToRun);
            testInstance.setUser(getCurrentTestSession());
            getToolkitServices().runMesaTest(getCurrentTestSession(), getSiteSelection(), new TestInstance(testToRun), testSelectionManager.getSelectedSections(), parms, true, queryCallback);
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
                getToolkitServices().getTestResults(tests, getCurrentTestSession(), new AsyncCallback<Map<String, Result>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        new PopupMessage(throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(Map<String, Result> stringResultMap) {
                        Result result = stringResultMap.get("15807");
                        if (result == null) {
                            new PopupMessage("Results not available");
                            return;
                        }
                        SiteSpec siteSpec = new SiteSpec(siteName, ActorType.RESPONDING_GATEWAY, null);

                        MetadataInspectorTab itab = new MetadataInspectorTab();
                        List<Result> results = new ArrayList<Result>();
                        results.add(result);
                        itab.setResults(results);
                        itab.setSiteSpec(siteSpec);
//                        itab.setToolkitService(toolkitService);
                        itab.onTabLoad(true, "Insp");
                    }
                });
            }
        });
        return button;
    }

    public String getWindowShortName() {
        return "igtests";
    }

}
