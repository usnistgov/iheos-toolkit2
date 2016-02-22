package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.StringSort;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.tabs.SimulatorMessageViewTab;
import gov.nist.toolkit.xdstools2.client.tabs.TextViewerTab;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
class TestSelectionManager {
    ListBox selectTestList = new ListBox();
    HTML documentation = new HTML();
    ListBox selectSectionList = new ListBox();
    Button selectSectionViewButton = new Button("View this section's testplan");
    private List<String> selectedSections = new ArrayList<>();
    GatewayTool tool;
    final static public String ALL = "All";
    final static public String ALL_SELECTION = "-- All --";
    List<String> sections = new ArrayList<String>();
//    String selectedSection = TestSelectionManager.ALL_SELECTION;
    ToolkitServiceAsync toolkitService;
    Map<String, String> testCollectionMap;  // name => description for selected actor
    List<String> assigningAuthorities = null;
    TestSelectionManager me;

    TestSelectionManager(GatewayTool tool) {
        me = this;
        this.tool = tool;
        toolkitService = tool.getToolkitService();
        loadAssigningAuthorities();

        selectSectionList.addChangeHandler(new SectionSelectionChangeHandler());
    }

    void loadTestsFromCollection(final String testCollectionName) {
        toolkitService.getCollection("collections", testCollectionName, new AsyncCallback<Map<String, String>>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("getCollection(" + testCollectionName + "): " +  " -----  " + caught.getMessage());
            }

            public void onSuccess(Map<String, String> result) {
                testCollectionMap = result;

                Set<String> testNumsSet = testCollectionMap.keySet();
                List<String> testNums = new ArrayList<String>();
                testNums.addAll(testNumsSet);
                testNums = new StringSort().sort(testNums);

                for (String name : testNums) {
                    String description = testCollectionMap.get(name);
                    selectTestList.addItem(name + " - " + description, name);
                }
//                testSelectionContext.selectTestList.addItem(ALL);  does not work yet
                selectTestList.setVisibleItemCount(selectTestList.getItemCount());
                if (selectTestList.getItemCount() > 0) {
                    selectTestList.setSelectedIndex(0);
                    new TestSelectionChangeHandler(me).onChange(null);
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
                }
            });
        } catch (Exception e) {
            new PopupMessage(e.getMessage());
        }
    }

    Widget buildSectionSelector() {
        HorizontalPanel selectSectionPanel = new HorizontalPanel();

        HTML selectSectionLabel = new HTML();
        selectSectionLabel.setText("Select test section to run: ");
        selectSectionPanel.add(selectSectionLabel);

        selectSectionPanel.add(selectSectionList);

        selectSectionPanel.add(selectSectionViewButton);
        selectSectionViewButton.addClickHandler(new SelectSectionViewButtonClickHandler());
        return selectSectionPanel;
    }

    Widget buildTestSelector() {
        FlexTable table = new FlexTable();

        table.setWidget(0, 0, new HTML("<h2>Select Test to run</h2>"));
        VerticalPanel selectionPanel = new VerticalPanel();
        table.setWidget(1, 0, selectionPanel);

        table.setWidget(0, 1, new HTML("<h3>Test Documentation</h3>"));

        DecoratorPanel panel = new DecoratorPanel();
        SimplePanel documentationPanel = new ScrollPanel();
        documentationPanel.setWidget(documentation);
        panel.setWidget(documentationPanel);
        table.setWidget(1, 1, panel);

        documentationPanel.setHeight("450px");

        selectionPanel.add(selectTestList);

        selectTestList.clear();
        selectTestList.addChangeHandler(new TestSelectionChangeHandler(me));

        selectionPanel.add(addResultsInspectorButton());
        return table;
    }

    Button addResultsInspectorButton() {
        Button button = new Button("Inspect Results");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                List<TestInstance> tests = new ArrayList<TestInstance>();
                tests.add(new TestInstance(tool.getSelectedTest()));
                toolkitService.getTestResults(tests, tool.getCurrentTestSession(), new AsyncCallback<Map<String, Result>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        new PopupMessage(throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(Map<String, Result> stringResultMap) {
                        Result result = stringResultMap.get(tool.getSelectedTest());
                        if (result == null) {
                            new PopupMessage("Results not available");
                            return;
                        }
                        if (!tool.verifySiteProvided()) return;

                        SiteSpec siteSpec = tool.getSiteSelection();

                        MetadataInspectorTab itab = new MetadataInspectorTab();
                        List<Result> results = new ArrayList<Result>();
                        results.add(result);
                        itab.setResults(results);
                        itab.setSiteSpec(siteSpec);
                        itab.setToolkitService(toolkitService);
                        itab.onTabLoad(tool.getToolContainer(), true, null);
                    }
                });
            }
        });
        return button;
    }

    void loadSectionNames() {
        toolkitService.getTestIndex(tool.getSelectedTest(), new AsyncCallback<List<String>>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("getTestIndex: " + caught.getMessage());
            }

            public void onSuccess(List<String> result) {
                sections.clear();
                selectSectionList.clear();
                if (result == null) {
                    // no index.idx - so sections
                    selectSectionViewButton.setEnabled(false);
                } else {
                    sections.addAll(result);
                    selectSectionList.addItem(ALL_SELECTION, ALL_SELECTION);
                    for (String section : result) {
                        selectSectionList.addItem(section, section);
                    }
                    selectSectionViewButton.setEnabled(true);
                }
                selectedSections.clear();
            }
        });
    }

    public List<String> getSelectedSections() {
        selectedSections.clear();
        String selectedSection = selectSectionList.getSelectedItemText();
        if (selectedSection != null) {
            if (ALL_SELECTION.equals(selectedSection))
                return selectedSections;
            selectedSections.add(selectedSection);
        }
        return selectedSections;
    }

    class SelectSectionViewButtonClickHandler implements ClickHandler {

        public void onClick(ClickEvent event) {
            toolkitService.getTestplanAsText(new TestInstance(tool.getSelectedTest()), selectSectionList.getSelectedItemText(), new AsyncCallback<String>() {

                public void onFailure(Throwable caught) {
                    new PopupMessage("getTestplanAsText: " + caught.getMessage());
                }

                public void onSuccess(String result) {
                    new TextViewerTab().onTabLoad(tool.getToolContainer(), true, result, tool.getSelectedTest() + "#" + selectSectionList.getSelectedItemText());
                }

            });
        }
    }

    class SectionSelectionChangeHandler implements ChangeHandler {

        @Override
        public void onChange(ChangeEvent changeEvent) {
            selectedSections.clear();
            selectedSections.add(selectSectionList.getSelectedValue());
        }
    }

    class TestSelectionChangeHandler implements ChangeHandler {
        TestSelectionManager testSelectionManager;

        TestSelectionChangeHandler(TestSelectionManager testSelectionManager) {
            this.testSelectionManager = testSelectionManager;
        }

        public void onChange(ChangeEvent event) {
            int selectedI = testSelectionManager.selectTestList.getSelectedIndex();
            tool.setSelectedTest(testSelectionManager.selectTestList.getValue(selectedI));
            if ("".equals(tool.getSelectedTest()))
                return;
            if (ALL.equals(tool.getSelectedTest())) {
                testSelectionManager.documentation.setText("");
                sections.clear();
                return;
            }
            loadTestReadme(testSelectionManager.documentation);
            loadSectionNames();
//            selectedSection = ALL_SELECTION;
        }
    }

    void loadTestReadme(final HTML documentation) {
        toolkitService.getTestReadme(tool.getSelectedTest(), new AsyncCallback<String>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("getTestReadme: " + caught.getMessage());
            }

            public void onSuccess(String result) {
                documentation.setHTML(Util.htmlize(result));
            }

        });
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
                    viewTab.onTabLoad(tool.getToolContainer(), true, simIdString);
                }
            });
        }
        return panel;
    }

}
