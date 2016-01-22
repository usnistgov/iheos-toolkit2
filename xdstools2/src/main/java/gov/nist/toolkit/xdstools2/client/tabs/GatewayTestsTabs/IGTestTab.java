package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actorfactory.SimulatorProperties;
import gov.nist.toolkit.actorfactory.client.Pid;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.services.client.IgOrchestationManagerRequest;
import gov.nist.toolkit.services.client.IgOrchestrationResponse;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.SimulatorMessageViewTab;
import gov.nist.toolkit.xdstools2.client.tabs.TextViewerTab;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.ReportableButton;

import java.util.*;

public class IGTestTab extends GenericQueryTab {
    final protected ToolkitServiceAsync toolkitService = GWT
            .create(ToolkitService.class);

    final static String ALL_SELECTION = "-- All --";
    final String chooseSelection = "-- Choose --";

    Button selectSectionViewButton = new Button("View this section's testplan");
    ScrollPanel readmeBox = new ScrollPanel();
    TextBox patientIdBox = new TextBox();
    TextBox altPatientIdBox = new TextBox();
    Map<String, String> actorCollectionMap;  // name => description
    String selectedActor = ActorType.INITIATING_GATEWAY.getShortName();
    String selectedSection = ALL_SELECTION;
    Map<String, String> testCollectionMap;  // name => description for selected actor
    List<String> sections = new ArrayList<String>();
    int row = 0;
    Button buildTestEnvButton = new Button("Build Test Environment");
    List<String> assigningAuthorities = null;
    Pid patientId;
    List<SimulatorConfig> rgConfigs;
    GenericQueryTab genericQueryTab;
    static final String ALL = "All";
    static final String COLLECTION_NAME =  "igtool1rg";

    public IGTestTab() {
        super(new GetDocumentsSiteActorManager());
    }

    public void onTabLoad(TabContainer container, boolean select) {
    }

    public void onTabLoad(TabContainer container, boolean select, String eventName) {
        myContainer = container;
        topPanel = new VerticalPanel();
        genericQueryTab = this;

        container.addTab(topPanel, eventName, select);
        addCloseButton(container,topPanel, null);

        genericQueryTab.reloadTransactionOfferings();

        // customization of GenericQueryTab
        autoAddRunnerButtons = false;  // want them in a different place
        genericQueryTitle = "Select System Under Test";
        genericQueryInstructions = new HTML(
                "<p>When the test is run a Stored Query or Retrieve transaction will be sent to the " +
                        "Initiating Gateway " +
                        "selected below. This will start the test. Before running a test, make sure your " +
                        "Initiating Gateway is configured to send to the Responding Gateway above.  This " +
                        "test only uses non-TLS endpoints (for now).</p>"
        );
        addResultsPanel = false;  // manually done below



        loadAssigningAuthorities();

        ////////////////////////////////////////////////////////////////////////////////////////////////
        topPanel.add(new HTML("<h1>Initiating Gateway Test Tool</h1>"));

        topPanel.add(new HTML("<p>" +
                "This tool tests an Initiating Gateway with Affinity Domain option by surrounding it with " +
                "a Document Consumer simulator and a Responding Gateway simulator. The Responding Gateway has " +
                "a Document Registry simulator and Document Repository simulator behind it. " +
                "These simulators are created by this tool." +

                "<h2>Create supporting test session</h2>" +
                "These simulators and " +
                "their logs will be maintained in a test session you create for this test. At the top of the window, " +
                "create a new test session and select it - name it for your company. " +
                "This tool deletes all logs and simulators in the selected test session.  " +
                "</p>" +
                "<p>" +
                "Build test environment using the button below.  This will create the simulators and populate " +
                "the Registry/Repository with test data" +
                "</p>"
        ));

        ////////////////////////////////////////////////////////////////////////////////////////////////
        topPanel.add(new HTML(
                "<hr />" +
                "<h2>Build Test Environment</h2>" +
                "<p>" +
                "This will delete the contents of the selected test session and initialize it. " +
                "The Build Test Environment button will create the necessary simulators to test your Initiating Gateway.  " +
                "the Build Demonstration Environment button will do the same and also build an Initiating Gateway for " +
                "demonstration and training purposes. Only one can be used." +
                        "The generated test environment will be displayed below. " +
                        "Once the test environment is built, configure your Initiating Gateway to forward requests " +
                        "to the generated Responding Gateway simulator. The Demonstration Environment builds this " +
                        "configuration automatically." +
                "</p>"
        ));

        HorizontalPanel testEnvironmentsPanel = new HorizontalPanel();
        topPanel.add(testEnvironmentsPanel);

        new BuildTestOrchestrationButton(testEnvironmentsPanel, "Build Test Environment", false);

        new BuildTestOrchestrationButton(testEnvironmentsPanel, "Build Demonstration Environment", true);

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

        TestSelectionContext testSelectionContext = new TestSelectionContext();

        VerticalPanel testSelectionPanel = buildTestSelector(topPanel, testSelectionContext);

        loadTestsForActor(topPanel, testSelectionContext);

        buildSectionSelector(testSelectionPanel, testSelectionContext);

        topPanel.add(mainGrid);

        ////////////////////////////////////////////////////////////////////////////////////////////////
        topPanel.add(new HTML(
                "<hr />" +
                        "<h2>Run Test</h2>" +
                        "<p>" +
                        "Initiate the test from the Toolkit Document Consumer. After the test is run " +
                        "the Document Consumer's logs can be displayed with Inspect Results." +
                        "</p>"
        ));

        addRunnerButtons(topPanel);

        topPanel.add(resultPanel);
    }

    class BuildTestOrchestrationButton extends ReportableButton {
        boolean includeIG;

        BuildTestOrchestrationButton(Panel topPanel, String label, boolean includeIG) {
            super(topPanel, label);
            this.includeIG = includeIG;
        }

        public void handleClick(ClickEvent event) {
            IgOrchestationManagerRequest request = new IgOrchestationManagerRequest();
            if (empty(getCurrentTestSession())) {
                new PopupMessage("Must select test session first");
                return;
            }
            request.setUserName(getCurrentTestSession());
            request.setIncludeLinkedIG(includeIG);
            toolkitService.buildIgTestOrchestration(request, new AsyncCallback<RawResponse>() {
                @Override
                public void onFailure(Throwable throwable) { handleError(throwable); }

                @Override
                public void onSuccess(RawResponse rawResponse) {
                    if (handleError(rawResponse, IgOrchestrationResponse.class)) return;
                    IgOrchestrationResponse orchResponse = (IgOrchestrationResponse)rawResponse;

                    rgConfigs = orchResponse.getSimulatorConfigs();

                    panel().add(new HTML("<h2>Generated Environment</h2>"));
                    FlexTable table = new FlexTable();
                    panel().add(table);
                    int row = 0;

                    table.setHTML(row++, 0, "<h3>Patient IDs</h3>");

                    table.setText(row, 0, "Single document Patient ID");
                    table.setText(row++, 1, orchResponse.getOneDocPid().asString());

                    table.setText(row, 0, "Two document Patient ID");
                    table.setText(row++, 1, orchResponse.getTwoDocPid().asString());

                    table.setHTML(row++, 0, "<h3>Simulators</h3>");

                    for (SimulatorConfig config : rgConfigs) {
                        table.setText(row, 0, "Simulator ID");
                        table.setWidget(row++, 1, new HTML(config.getId().toString()));

                        table.setText(row, 0, "homeCommunityId");
                        table.setWidget(row++, 1, new HTML(config.get(SimulatorProperties.homeCommunityId).asString()));

                        table.setText(row, 0, "Responding Gateway");
                        table.setText(row, 1, "Query");
                        table.setText(row++, 2, config.getConfigEle(SimulatorProperties.xcqEndpoint).asString());

                        table.setText(row, 1, "Retrieve");
                        table.setText(row++, 2, config.getConfigEle(SimulatorProperties.xcrEndpoint).asString());

                        table.setText(row, 0, "Repository");
                        table.setText(row, 1, "Provide and Register");
                        table.setText(row++, 2, config.getConfigEle(SimulatorProperties.pnrEndpoint).asString());

                        table.setText(row, 1, "Retrieve");
                        table.setText(row++, 2, config.getConfigEle(SimulatorProperties.retrieveEndpoint).asString());

                        table.setText(row, 0, "Registry");
                        table.setText(row, 1, "Register");
                        table.setText(row++, 2, config.getConfigEle(SimulatorProperties.registerEndpoint).asString());

                        table.setText(row, 1, "Query");
                        table.setText(row++, 2, config.getConfigEle(SimulatorProperties.storedQueryEndpoint).asString());

                    }

                    // generate log launcher buttons
                    panel().add(addTestEnvironmentInspectorButton(rgConfigs.get(0).getId().toString()));
                    panel().add(buildLogLauncher(rgConfigs));

                    genericQueryTab.reloadTransactionOfferings();
                }
            });
        }
    }

    Panel buildLogLauncher(List<SimulatorConfig> simConfigs) {
        HorizontalPanel panel = new HorizontalPanel();
        for (SimulatorConfig config : simConfigs) {
            final String simIdString = config.getId().toString();
            Button button = new Button("Launch " + simIdString + " Log");
            panel.add(button);
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    SimulatorMessageViewTab viewTab = new SimulatorMessageViewTab();
                    viewTab.onTabLoad(myContainer, true, simIdString);
                }
            });
        }
        return panel;
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

            List<String> selectedSections = new ArrayList<String>();
            if (selectedSection.equals(ALL_SELECTION)) {
                selectedSections.addAll(sections);
            } else
                selectedSections.add(selectedSection);

            Map<String, String> parms = new HashMap<>();
            parms.put("$testdata_home$", rgConfigs.get(0).get(SimulatorProperties.homeCommunityId).asString());

            Panel logLaunchButtonPanel = rigForRunning();
            logLaunchButtonPanel.clear();
            logLaunchButtonPanel.add(buildLogLauncher(rgConfigs));
            String testToRun = selectedTest;
            if (ALL.equals(testToRun)) {
                testToRun = "tc:" + COLLECTION_NAME;
            }

            TestInstance testInstance = new TestInstance(testToRun);
            testInstance.setUser(getCurrentTestSession());
            toolkitService.runMesaTest(getCurrentTestSession(), getSiteSelection(), new TestInstance(testToRun), selectedSections, parms, true, queryCallback);
        }

    }


    class SelectSectionViewButtonClickHandler implements ClickHandler {

        public void onClick(ClickEvent event) {
            toolkitService.getTestplanAsText(new TestInstance(selectedTest), selectedSection, new AsyncCallback<String>() {

                public void onFailure(Throwable caught) {
                    new PopupMessage("getTestplanAsText: " + caught.getMessage());
                }

                public void onSuccess(String result) {
                    new TextViewerTab().onTabLoad(myContainer, true, result, selectedTest + "#" + selectedSection);
                }

            });
        }

    }

    void loadSectionNames(final TestSelectionContext testSelectionContext) {
        toolkitService.getTestIndex(selectedTest, new AsyncCallback<List<String>>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("getTestIndex: " + caught.getMessage());
            }

            public void onSuccess(List<String> result) {
                sections.clear();
                testSelectionContext.selectSectionList.clear();
                if (result == null) {
                    // no index.idx - so sections
                    selectSectionViewButton.setEnabled(false);
                } else {
                    sections.addAll(result);
                    testSelectionContext.selectSectionList.addItem(ALL_SELECTION, ALL_SELECTION);
                    for (String section : result) {
                        testSelectionContext.selectSectionList.addItem(section, section);
                    }
                    selectSectionViewButton.setEnabled(true);
                }
            }

        });
    }

    class SectionSelectionChangeHandler implements ChangeHandler {
        TestSelectionContext testSelectionContext;

        SectionSelectionChangeHandler(TestSelectionContext testSelectionContext) {
            this.testSelectionContext = testSelectionContext;
        }

        public void onChange(ChangeEvent event) {
            int i = testSelectionContext.selectSectionList.getSelectedIndex();
            selectedSection  = testSelectionContext.selectSectionList.getValue(i);
            if ("".equals(selectedSection))
                return;
        }

    }

    class TestSelectionContext {
        ListBox selectTestList = new ListBox();
        HTML documentation = new HTML();
        ListBox selectSectionList = new ListBox();

        TestSelectionContext() {
            selectSectionList.addChangeHandler(new SectionSelectionChangeHandler(this));
        }
    }

    class TestSelectionChangeHandler implements ChangeHandler {
        TestSelectionContext testSelectionContext;

        TestSelectionChangeHandler(TestSelectionContext testSelectionContext) {
            this.testSelectionContext = testSelectionContext;
        }

        public void onChange(ChangeEvent event) {
            int selectedI = testSelectionContext.selectTestList.getSelectedIndex();
            selectedTest = testSelectionContext.selectTestList.getValue(selectedI);
            if ("".equals(selectedTest))
                return;
            if (ALL.equals(selectedTest)) {
                testSelectionContext.documentation.setText("");
                sections.clear();
                return;
            }
            loadTestReadme(testSelectionContext.documentation);
            loadSectionNames(testSelectionContext);
            selectedSection = ALL_SELECTION;
        }

    }

    void loadTestReadme(final HTML documentation) {
        toolkitService.getTestReadme(selectedTest, new AsyncCallback<String>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("getTestReadme: " + caught.getMessage());
            }

            public void onSuccess(String result) {
                documentation.setHTML(htmlize(result));
            }

        });
    }

    boolean isFilled(String x) {
        return x != null && !x.equals("");
    }

    boolean isRunable() {
        return isFilled(selectedActor) &&
                isFilled(selectedTest) &&
                isFilled(patientIdBox.getText());

    }

    HorizontalPanel buildSectionSelector(VerticalPanel topPanel, TestSelectionContext testSelectionContext) {
        HorizontalPanel selectSectionPanel = new HorizontalPanel();

        topPanel.add(selectSectionPanel);

        HTML selectSectionLabel = new HTML();
        selectSectionLabel.setText("Select test section to run: ");
        selectSectionPanel.add(selectSectionLabel);

        selectSectionPanel.add(testSelectionContext.selectSectionList);

        selectSectionPanel.add(selectSectionViewButton);
        selectSectionViewButton.addClickHandler(new SelectSectionViewButtonClickHandler());
        return selectSectionPanel;
    }

    VerticalPanel buildTestSelector(VerticalPanel topPanel, TestSelectionContext testSelectionContext) {
        FlexTable table = new FlexTable();
        topPanel.add(table);

        table.setWidget(0, 0, new HTML("<h2>Select Test to run</h2>"));
        VerticalPanel selectionPanel = new VerticalPanel();
        table.setWidget(1, 0, selectionPanel);

        table.setWidget(0, 1, new HTML("<h3>Test Documentation</h3>"));

        SimplePanel documentationPanel = new ScrollPanel();
        documentationPanel.add(testSelectionContext.documentation);
        table.setWidget(1, 1, documentationPanel);

        documentationPanel.setHeight("450px");

        selectionPanel.add(testSelectionContext.selectTestList);

        testSelectionContext.selectTestList.clear();
        testSelectionContext.selectTestList.addChangeHandler(new TestSelectionChangeHandler(testSelectionContext));

        selectionPanel.add(addResultsInspectorButton());
        return selectionPanel;
    }

    Button addTestEnvironmentInspectorButton(final String siteName) {
        Button button = new Button("Inspect Test Data");
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

    Button addResultsInspectorButton() {
        Button button = new Button("Inspect Results");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                List<TestInstance> tests = new ArrayList<TestInstance>();
                tests.add(new TestInstance(selectedTest));
                toolkitService.getTestResults(tests, getCurrentTestSession(), new AsyncCallback<Map<String, Result>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        new PopupMessage(throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(Map<String, Result> stringResultMap) {
                        Result result = stringResultMap.get(selectedTest);
                        if (result == null) {
                            new PopupMessage("Results not available");
                            return;
                        }
                        if (!verifySiteProvided()) return;

                        SiteSpec siteSpec = getSiteSelection();

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

    void loadTestsForActor(VerticalPanel topPanel, final TestSelectionContext testSelectionContext) {

        toolkitService.getCollection("collections", COLLECTION_NAME, new AsyncCallback<Map<String, String>>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("getCollection(" + COLLECTION_NAME + "): " +  " -----  " + caught.getMessage());
            }

            public void onSuccess(Map<String, String> result) {
                testCollectionMap = result;

                Set<String> testNumsSet = testCollectionMap.keySet();
                List<String> testNums = new ArrayList<String>();
                testNums.addAll(testNumsSet);
                testNums = new StringSort().sort(testNums);

                for (String name : testNums) {
                    String description = testCollectionMap.get(name);
                    testSelectionContext.selectTestList.addItem(name + " - " + description, name);
                }
//                testSelectionContext.selectTestList.addItem(ALL);  does not work yet
                testSelectionContext.selectTestList.setVisibleItemCount(testSelectionContext.selectTestList.getItemCount());
                if (testSelectionContext.selectTestList.getItemCount() > 0) {
                    testSelectionContext.selectTestList.setSelectedIndex(0);
                    new TestSelectionChangeHandler(testSelectionContext).onChange(null);
                }
            }
        });
    }

    void loadAssigningAuthorities() {
        try {
            toolkitService.getAssigningAuthorities(new AsyncCallback<List<String>>() {
                @Override
                public void onFailure(Throwable e) {
                    new PopupMessage("Error loading Assigning Authorities - usually caused by session timeout - " + e.getMessage());
                }

                @Override
                public void onSuccess(List<String> s) {
                    assigningAuthorities = s;
                    generatePid(assigningAuthorities.get(0));
                }
            });
        } catch (Exception e) {
            new PopupMessage(e.getMessage());
        }
    }

    void generatePid(String assigningAuthority) {
        try {
            toolkitService.createPid(assigningAuthority, new AsyncCallback<Pid>() {
                @Override
                public void onFailure(Throwable throwable) {
                    new PopupMessage(throwable.getMessage());
                }

                @Override
                public void onSuccess(Pid pid) {
                    patientId = pid;
                }
            });
        } catch (NoServletSessionException e) {
            new PopupMessage(e.getMessage());
        }
    }



    String htmlize(String header, String in) {
        return
                        "<h2>" + header + "</h2>" +

                        in.replaceAll("<", "&lt;")
                                .replaceAll("\t", "&nbsp;&nbsp;&nbsp;")
                                .replaceAll(" ", "&nbsp;")
                                .replaceAll("\n", "<br />");
    }

    String htmlize(String in) {
        return
                        in.replaceAll("<", "&lt;")
                                .replaceAll("\t", "&nbsp;&nbsp;&nbsp;")
                                .replaceAll(" ", "&nbsp;")
                                .replaceAll("\n", "<br />");
    }

    public String getWindowShortName() {
        return "igtests";
    }

}
