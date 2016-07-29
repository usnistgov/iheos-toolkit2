package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetImagingDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

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
public class IIGTestTab extends GenericQueryTab implements GatewayTool {
    final protected ToolkitServiceAsync toolkitService = GWT
            .create(ToolkitService.class);
    String selectedActor = ActorType.INITIATING_IMAGING_GATEWAY.getShortName();
    List<SimulatorConfig> rgConfigs;
    GenericQueryTab genericQueryTab;
    static final String COLLECTION_NAME =  "iigtool";
    final TestSelectionManager testSelectionManager;

    public IIGTestTab() {
        super(new GetImagingDocumentsSiteActorManager());
        testSelectionManager = new TestSelectionManager(this);
    }

    @Override
    public ToolkitServiceAsync getToolkitService() { return toolkitService; }

    @Override
    public TabContainer getToolContainer() { return myContainer; }

    public void onTabLoad(TabContainer container, boolean select) {
    }

    public void onTabLoad(TabContainer container, boolean select, String eventName) {
        myContainer = container;
        topPanel = new VerticalPanel();
        genericQueryTab = this;

        container.addTab(topPanel, eventName, select);


        addCloseButton(container,topPanel, null);
        tlsOptionEnabled = false;

        genericQueryTab.reloadTransactionOfferings();

        // customization of GenericQueryTab
        autoAddRunnerButtons = false;  // want them in a different place
        genericQueryTitle = "Select System Under Test";
        genericQueryInstructions = new HTML(
           
           "<p>When the test is run a Retrieve Imaging Document Set (RAD-69) " +
           "transaction will be sent to the Initiating Imaging Gateway " +
           "selected below. This will start the test. Before running a test, " +
           "make sure your Initiating Imaging Gateway is configured to send " +
           "to the Responding Imaging Gateways above. </p>" +
                        
           "<p> This tool uses only non-TLS endpoints. TLS selection is disabled.</p>" +

           "<p>It may ne necessary to refresh the selection list at times.  " +
           "The Reload button at the top of the screen performs this refresh.</p>"

        );
        addResultsPanel = false;  // manually done below

        ////////////////////////////////////////////////////////////////////////////////////////////////
        topPanel.add(new HTML("<h1>Initiating Imaging Gateway Test Tool</h1>"));

        Image initiatingGatewayDiagram=new Image();
        initiatingGatewayDiagram.setUrl("diagrams/IIGdiagram.png");
        initiatingGatewayDiagram.setHeight("300px");
        topPanel.add(initiatingGatewayDiagram);

        topPanel.add(new HTML(
           
           "<p>This tool tests an Initiating Imaging Gateway.  The tests are " +
           "driven by an Imaging Document Consumer simulator. The Initiating " +
           "Imaging Gateway System Under Test (SUT) will be configured to " +
           "relay requests to three Responding Imaging Gateways. This tool " +
           "supplies the Imaging Document Consumer and Responding Imaging " +
           "Gateways as Toolkit supported simulators. Each of the Responding " +
           "Imaging Gateways is backed by one or more Imaging Document " +
           "Source actors loaded with supporting test data. </p>" +

           "<h2>Create supporting test session</h2>" +
                
           "<p>These simulators and their logs will be maintained in a test " +
           "session you create for this test. At the top of the window, " +
           "create a new test session and select it. </p>"
        ));

        ////////////////////////////////////////////////////////////////////////////////////////////////
        topPanel.add(new HTML(
                
           "<hr />" +
           
           "<h2>Build Test Environment</h2>" +
           
           "<p>The Build Test Environment button will create the necessary " +
           "simulators to test your Initiating Imaging Gateway:  an Imaging " +
           "Document Consumer to drive the test, three Responding Imaging " +
           "Gateways, along with necessary Imaging Document Sources to " + 
           "service requests from your Initiating Imaging Gateway. The " +
           "generated test configuration will be displayed below. Once the " +
           "test environment is built, configure your Initiating Imaging " +
           "Gateway to forward requests to the three generated Responding " +
           "Imaging Gateways.</p>"

        ));

        HorizontalPanel testEnvironmentsPanel = new HorizontalPanel();
        topPanel.add(testEnvironmentsPanel);

        new BuildIIGTestOrchestrationButton(this, testEnvironmentsPanel, "Build Test Environment", false);


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

        topPanel.add(testSelectionManager.buildTestSelector());

        topPanel.add(testSelectionManager.buildSectionSelector());

        topPanel.add(mainGrid);

        testSelectionManager.loadTestsFromCollection(COLLECTION_NAME);


        ////////////////////////////////////////////////////////////////////////////////////////////////
        topPanel.add(new HTML(
                
           "<hr />" +
                        
           "<h2>Run Test</h2>" +
           
           "<p>Initiate the test from the Toolkit Image Document Consumer. " +
           "After the test is run the Image Document Consumer's logs can be " +
           "displayed with Inspect Results.</p>"
        ));

        addRunnerButtons(topPanel);

        topPanel.add(resultPanel);
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
            parms.put("$testdata_home$", rgConfigs.get(0).get(SimulatorProperties.homeCommunityId).asString());

            Panel logLaunchButtonPanel = rigForRunning();
            logLaunchButtonPanel.clear();
            logLaunchButtonPanel.add(testSelectionManager.buildLogLauncher(rgConfigs));
            String testToRun = selectedTest;
            if (TestSelectionManager.ALL.equals(testToRun)) {
                testToRun = "tc:" + COLLECTION_NAME;
            }

            TestInstance testInstance = new TestInstance(testToRun);
            testInstance.setUser(getCurrentTestSession());
            toolkitService.runMesaTest(getEnvironmentSelection(), getCurrentTestSession(), getSiteSelection(), new TestInstance(testToRun), testSelectionManager.getSelectedSections(), parms, true, queryCallback);
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
                toolkitService.getTestResults(tests, getCurrentTestSession(), new AsyncCallback<Map<String, Result>>() {
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
                        itab.setToolkitService(toolkitService);
                        itab.onTabLoad(myContainer, true, null);
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
