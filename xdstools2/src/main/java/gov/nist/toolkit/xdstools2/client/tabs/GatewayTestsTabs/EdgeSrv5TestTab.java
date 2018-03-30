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
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.command.command.RunMesaTestCommand;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetImagingDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.RunTestRequest;

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
public class EdgeSrv5TestTab extends GenericQueryTab implements GatewayTool {
//    final protected ToolkitServiceAsync toolkitService = GWT
//            .create(ToolkitService.class);
    String selectedActor = ActorType.EDGE_SERVER_5.getShortName();
    List<SimulatorConfig> rgConfigs;
    public GenericQueryTab genericQueryTab;
    static final String COLLECTION_NAME =  "es5";
    public final TestSelectionManager testSelectionManager;

    public EdgeSrv5TestTab() {
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

    public TestSelectionManager getTestSelectionManager() {
        return testSelectionManager;
    }

    public void setRgConfigs(List<SimulatorConfig> list) {
        rgConfigs = list;
    }
    public List<SimulatorConfig> getRgConfigs() {
        return rgConfigs;
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
           
           "<p>When the test is run Lorem ipsum dolor sit amet, consectetur " +
               "adipiscing elit. Integer nec odio. Praesent libero. Sed " +
               "cursus ante dapibus diam. Sed nisi. Nulla quis sem at nibh " +
               "elementum imperdiet. Duis sagittis ipsum. Praesent mauris. " +
               "Fusce nec tellus sed augue semper porta. </p>" +
                        
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
           
           "<p>Initiate the test from the Edge Server. " +
           "After the test is run the simulated Clearing House logs can be " +
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

//			addStatusBox();
//			getGoButton().setEnabled(false);
//			getInspectButton().setEnabled(false);

            Map<String, String> parms = new HashMap<>();
//      parms.put("$testdata_home$", config.get(SimulatorProperties.homeCommunityId).asString());

            Panel logLaunchButtonPanel = rigForRunning();
            logLaunchButtonPanel.clear();
//            List configs = Arrays.asList(config);
//            logLaunchButtonPanel.addTest(testSelectionManager.buildLogLauncher(configs));
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

    public Button addTestEnvironmentInspectorButton(final String siteName, String label) {
        Button button = new Button(label);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                new PopupMessage("TODO");
            }
        });
        return button;
    }

    public String getWindowShortName() {
        return "estests";
    }

}
