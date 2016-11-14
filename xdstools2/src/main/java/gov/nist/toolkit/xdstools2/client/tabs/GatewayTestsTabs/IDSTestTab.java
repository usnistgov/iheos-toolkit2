package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestResultsCommand;
import gov.nist.toolkit.xdstools2.client.command.command.RunMesaTestCommand;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestResultsRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.RunTestRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * toolkit test tab for IDS SUT testing *
 */
public class IDSTestTab extends GenericQueryTab implements GatewayTool {
    String selectedActor = ActorType.IMAGING_DOC_SOURCE.getShortName();
    SimulatorConfig rrConfig;
    GenericQueryTab genericQueryTab;
    static final String COLLECTION_NAME =  "idstool";
    final TestSelectionManager testSelectionManager;

    public IDSTestTab() {
        super(new GetDocumentsSiteActorManager());
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

        // TODO: Refine HTML
        genericQueryTitle = "Select System Under Test";
        genericQueryInstructions = new HTML(

           "<p>Before running any tests, be sure to download the test images " +
           "... blather blather instructions for downloading them ..., and " +
           "load them in your Imaging Document Source under test.</p>" +

           "<p>Provide and Register (RAD-68) tests:<br/>" +
           "The PnR transaction should be run by the system under test before " +
           "the test is run. When the test is run, it will evaluated that " +
           "transaction.</p> " +

           "<p>Retrieve Imaging Document Set (RAD-69) tests:<br/>" +
           "When the test is run, a RAD-69 transaction will be sent to the " +
           "Imaging Document Source System under Test by an Imaging Document " +
           "Consumer simulator.</p>" +

           "<p> This tool uses only non-TLS endpoints. TLS selection is disabled.</p>" +

           "<p>It may ne necessary to refresh the selection list at times. " +
           "The Reload button at the top of the screen performs this refresh.</p>"

        );
        addResultsPanel = false;  // manually done below




        ////////////////////////////////////////////////////////////////////////////////////////////////
        tabTopPanel.add(new HTML("<h1>Imaging Document Source Test Tool</h1>"));

        Image initiatingGatewayDiagram=new Image();
        // TODO: Update Image
        initiatingGatewayDiagram.setUrl("diagrams/IDSdiagram.png");
        initiatingGatewayDiagram.setHeight("300px");
        tabTopPanel.add(initiatingGatewayDiagram);

        // TODO: Update HTML
        tabTopPanel.add(new HTML(

           "<p>This tool tests an Imaging Document Source.  Two types of  " +
           "transactions are tested:<ul>" +
           "<li/><b>Provide and Register Imaging Document Set (RAD-68) " +
           "transactions.</b> These tests are initiated by the Imaging " +
           "Document Source SUT (you), by sending a RAD-68 transaction to " +
           "a Registry-Repository. This tool supplies the Registry-Repository " +
           "as a toolkit supported simulator. When you have sent the RAD-68" +
           "transaction, the toolkit will evaluate it." +
           "<li/><b>Retrieve Imaging Document Set (RAD-69) transactions.</b> " +
           "These tests are initiated by an Imaging Document Consumer, which " +
           "will send a RAD-69 transaction to the Imaging Document Source SUT" +
           "and evaluate the result. This tool supplies the Imaging Document " +
           "source as a toolkit supported simulator.</ul></p>" +

           "<p>For both transactions, DICOM image files for testing are " +
           "provided by the toolkit. You will need to import them into your " +
           "Imaging Document Sourc (SUT) prior to testing.</p>" +

           "<h2>Create supporting test session</h2>" +

           "<p>These simulators and their logs will be maintained in a test " +
           "session you create for this test. At the top of the window, " +
            "create a new test session and select it. </p>"
        ));

        ////////////////////////////////////////////////////////////////////////////////////////////////
        // TODO: Update HTML
        tabTopPanel.add(new HTML(

           "<hr />" +

           "<h2>Build Test Environment</h2>" +

           "<p>The Build Test Environment button will create the necessary " +
           "simulators to test your Imaging Document Source: a Registry-" +
           "Repository for you to send Provide and Register Imaging Document " +
           "Set (RAD-68) transactions to, and an Imaging Document Consumer " +
           "to send Retrieve Imaging Document Set (RAD-69) transactions to " +
           "your Imaging Document Source SUT. The generated test configuration " +
           "will be displayed below. Once the test environment is built, " +
           "configure your Imaging Document Source to send Provide and " +
           "Register transactions to the Registry-Repository simulator, and " +
           "to process Retrieve Imaging Document Set transactions from the " +
           "Imaging Document Consumer Simulator.</p>" +
           "<p>Prior to running tests, you will also have to download the " +
           "test DICOM images and import them to your Imaging Document Source " +
           "SUT."

        ));

        HorizontalPanel testEnvironmentsPanel = new HorizontalPanel();
        tabTopPanel.add(testEnvironmentsPanel);

        new BuildIDSTestOrchestrationButton(this, testEnvironmentsPanel, "Build Test Environment", false);

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
        // TODO: Update HTML
        tabTopPanel.add(new HTML(

           "<hr />" +

           "<h2>Run Test</h2>" +

           "<p>For Provide and Register Imaging Document Set (RAD-68) " +
           "transactions: Initiate the transaction from your Imaging Document " +
           "source SUT. After the transaction is complete, run the test, " +
           "which will evaluate the results.</p>" +

           "<p/>For Retrieve Imaging Document Set (RAD-69) transactions: " +
           "Initiate the test from the toolkit, which will send the RAD-69 " +
           "to your Imaging Document Sources. "
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

            HorizontalPanel logLaunchButtonPanel = rigForRunning();
//            logLaunchButtonPanel.clear();
//            logLaunchButtonPanel.addTest(testSelectionManager.buildLogLauncher(new List<>(rrConfig)));
            String testToRun = selectedTest;
            if (TestSelectionManager.ALL.equals(testToRun)) {
                testToRun = "tc:" + COLLECTION_NAME;
            }

            new RunMesaTestCommand(){
                @Override
                public void onComplete(List<Result> result) {
                    queryCallback.onSuccess(result);
                }
            }.run(new RunTestRequest(getCommandContext(),getSiteSelection(),new TestInstance(testToRun),parms,true,testSelectionManager.getSelectedSections()));
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
                    public void onComplete(Map<String, Result> resultMap) {
                        Result result = resultMap.get("15807");
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
                        itab.onTabLoad(true, "Insp");
                    }
                }.run(new GetTestResultsRequest(getCommandContext(),tests));
            }
        });
        return button;
    }

    public String getWindowShortName() {
        return "idstests";
    }

}
