package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.actortransaction.shared.IheItiProfile;
import gov.nist.toolkit.installation.shared.TestCollectionCode;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestsResultEnvelopeCommand;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestsOverviewRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ConformanceToolMenu {
    private TabConfig tabConfig;
    static final int menuCols = 3;

    public abstract void onMenuSelect(TabConfig actor, Map<String,TabConfig> target);
    abstract CommandContext getCommandContext();
    abstract void updateTestStatistics(Map<ActorOptionConfig, List<TestInstance>> testsPerActorOption, Map<TestInstance, TestOverviewDTO> testOverviewDTOs, TestStatistics testStatistics, ActorOptionConfig actorOption);

    private void setTestStatistics(final HTML statsBar,  final ActorOptionConfig actorOptionConfig) {
        if (getCommandContext().getTestSessionName()==null || "".equals(getCommandContext().getTestSessionName()))
            return;


        final TestStatistics testStatistics = new TestStatistics();
        final Map<TestInstance, TestOverviewDTO> myTestOverviewDTOs = new HashMap<>();
        final Map<ActorOptionConfig, List<TestInstance>> myTestsPerActorOption = new HashMap<>();

        String loadImgHtmlStr = "<img style=\"width:10px;height:14px;border:1px solid white;float:left;margin-right:2px;\" src=\"icons2/ajax-loader.gif\"/>";

        statsBar.setHTML(loadImgHtmlStr);

        actorOptionConfig.loadTests(new AsyncCallback<List<TestInstance>>() {
            @Override
            public void onFailure(Throwable throwable) {
                statsBar.setVisible(false);
                new PopupMessage("getTestStatistics: " + throwable.getMessage());
            }

            @Override
            public void onSuccess(List<TestInstance> testInstances) {
                myTestsPerActorOption.put(actorOptionConfig, testInstances);
                getTestLogEnvelope(myTestOverviewDTOs, myTestsPerActorOption, testStatistics, actorOptionConfig, statsBar, testInstances);
            }
        });
    }

    private void getTestLogEnvelope(final Map<TestInstance, TestOverviewDTO> myTestOverviewDTOs, final Map<ActorOptionConfig, List<TestInstance>> myTestsPerActorOption, final TestStatistics testStatistics, ActorOptionConfig actorOptionConfig, final HTML statsBar, final List<TestInstance> testInstances) {

        new GetTestsResultEnvelopeCommand() {
            @Override
            public void onComplete(List<TestOverviewDTO> testOverviews) {
                for (TestOverviewDTO testOverview : testOverviews) {
                    myTestOverviewDTOs.put(testOverview.getTestInstance(), testOverview);
                }

                updateTestStatistics(myTestsPerActorOption, myTestOverviewDTOs, testStatistics, actorOptionConfig);
                String htmlStr = "<div style=\"width:10px;height:13px;border:1px solid;float:left;margin-right:2px;";
                if (testStatistics.getTestCount()>0 && testStatistics.getNotRun() != testStatistics.getTestCount()) { // Don't show anything if not run is 100%
                    htmlStr += "border-color:black;\">\n";
                    if (testStatistics.getSuccesses()==testStatistics.getTestCount()) {
                        htmlStr += "<div style=\"background-color:cyan;height:100%\"></div>\n";
                    } else if (testStatistics.getFailures()==testStatistics.getTestCount()) {
                        htmlStr += "<div style=\"background-color:coral;height:100%\"></div>\n";
                    } else {
                        float ts[] = new float[3];
                        ts[0] = (float)testStatistics.getNotRun() / (float)testStatistics.getTestCount();
                        ts[1] = (float)testStatistics.getSuccesses() / (float)testStatistics.getTestCount();
                        ts[2] = (float)testStatistics.getFailures() / (float)testStatistics.getTestCount();

                        // Boost small values below $boostVal to make more visible
                        float adjustedVal = 0.0F;
                        float boostVal = .25F;
                        for (int idx=0; idx < ts.length; idx++) {
                            if (ts[idx]>0 && ts[idx]<boostVal) {
                                adjustedVal  += (boostVal-ts[idx]);
                                ts[idx] = boostVal;
                            }
                        }
                        // Compensate for boosting from the majority index
                        if (adjustedVal >0.0F) {
                            int majorityIdx = -1;
                            for (int idx = 0; idx < ts.length; idx++) {
                                if (ts[idx] >= .33F) {
                                    if (majorityIdx==-1)  {
                                        majorityIdx = idx;
                                    } else {
                                        if (ts[idx]>ts[majorityIdx]) {
                                            majorityIdx=idx;
                                        }
                                    }
                                }
                            }
                            if (majorityIdx>-1)
                                ts[majorityIdx] -= adjustedVal ;
                        }

                        htmlStr +=
                                ((testStatistics.getNotRun() > 0) ?
                                        "<div style=\"background-color:white;height:" + ts[0]*100F + "%;\"></div>\n" : "") +
                                        (testStatistics.getSuccesses()>0?
                                                "<div style=\"background-color:cyan;height:" + ts[1]*100F  + "%;\"></div>\n"	:"") +
                                        (testStatistics.getFailures()>0?
                                                "<div style=\"background-color:coral;height:" + ts[2]*100F + "%;\"></div>\n" :"");
                    }
                    htmlStr += "</div>\n";
                    statsBar.setHTML(htmlStr);
                } else {
                    htmlStr += "border-color:white;\">\n";
                    htmlStr += "<div style=\"background-color:white;height:100%\"></div>\n";
                    statsBar.setHTML(htmlStr);
                }
            }
        }.run(new GetTestsOverviewRequest(getCommandContext(), testInstances));
    }

    public boolean displayMenu(Panel destinationPanel) {
        destinationPanel.clear();
        destinationPanel.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        HTML testNavigationTip = new HTML("Tests are organized as: Actor Profile Option. Select the option you are interested in. ");
        destinationPanel.add(testNavigationTip);

        if (tabConfig!=null) {

            int colWidth = 100 / menuCols;
            int menuCt = 0;
            /**
             * A row of actor/option trees
             */
            boolean rowAdded = false;

            FlowPanel rowPanel = new FlowPanel();

//            for (String code : displayOrder) {
                for (TabConfig tc : tabConfig.getChildTabConfigs()) {
//                    if (code.equals(tc.getTcCode())) {
                        if (!tc.isVisible())  {
                            continue;
                        }
                        if (menuCt % menuCols == 0) {
                            rowAdded = true;
                            destinationPanel.add(rowPanel);
                            rowPanel =  new FlowPanel();
                        }
                        SimplePanel simplePanel = new SimplePanel();
                        Tree tree = new Tree();
                        final TabConfigTreeItem root = flattenedTabConfig(new HashMap<String, String>(),null, tc);
                        tree.addItem(root);

                        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
                            @Override
                            public void onSelection(SelectionEvent<TreeItem> treeItem) {
                                TabConfigTreeItem tcTreeItem = (TabConfigTreeItem)treeItem.getSelectedItem();
//                                TabConfig tabConfig = ((TabConfig)tcTreeItem.getUserObject());
                                Map<String,TabConfig> tcMap = new HashMap<>();
                                setParentTcCodePath(tcMap,tcTreeItem);
                                onMenuSelect(root.getTabConfig(), tcMap);
                            }
                        });

                        expandAllTreeItems(root);

                        simplePanel.setWidget(tree);
                        simplePanel.setWidth("" + colWidth + "%");
                        simplePanel.getElement().getStyle().setMarginBottom(16, Style.Unit.PX);
                        simplePanel.getElement().getStyle().setFloat(Style.Float.LEFT);
                        rowPanel.add(simplePanel);
                        rowPanel.getElement().addClassName("tabConfigRow");
                        rowPanel.getElement().getStyle().setMargin(30, Style.Unit.PX);
                        menuCt++;
                        rowAdded = false;
//                        destinationPanel .add(simplePanel);
//                    }
                }
//            }

            if (!rowAdded) {
                destinationPanel.add(rowPanel);
            }

            if (menuCt!=0) {

                // Add legend row
                FlowPanel legendRowPanel = new FlowPanel();
                legendRowPanel.add(new HTML("<div>Legend:</div>"
                        + "<div style='margin:3px'><div style=\"width:10px;height:13px;border:1px solid white;float:left;margin-right:2px;background-color:white;\"></div>Not Run</div>"
                        + "<div style='margin:3px'><div style=\"width:10px;height:13px;border:1px solid;float:left;margin-right:2px;background-color:cyan;\"></div><span>Successes</span></div>\n"
                        + "<div style='margin:3px'><div style=\"width:10px;height:13px;border:1px solid;float:left;margin-right:2px;background-color:coral;\"></div><span>Failures</span></div>\n"
                ));
                legendRowPanel.getElement().addClassName("tabConfigRow");
                destinationPanel.add(legendRowPanel);
            } else {
                    testNavigationTip.setHTML("No tests found for the given combination of Ignore_internal_testkit Toolkit Property, Environment, and/or Test Session.");
                    return false;
            }
            return true;
        } else {
            testNavigationTip.setHTML("Unexpected Null TabConfig.");
            return false;
        }
    }

    static void setParentTcCodePath(Map<String,TabConfig> rs, TabConfigTreeItem treeItem) {

        TabConfig tabConfig = treeItem.getTabConfig();
        if (tabConfig.getTcCode()!=null)
            rs.put(tabConfig.getType(), tabConfig);
       if (treeItem.getParentItem()!=null)  {
           setParentTcCodePath(rs,(TabConfigTreeItem)treeItem.getParentItem());
       }
    }

    private void expandAllTreeItems(TreeItem ti) {
       ti.setState(true);
       int childCt = ti.getChildCount();
          for (int cx=0; cx<childCt; cx++)  {
              expandAllTreeItems(ti.getChild(cx));
          }
    }

    private TabConfigTreeItem flattenedTabConfig(Map<String, String> tcCodeMap, TabConfigTreeItem treeItem, TabConfig tabConfig) {
        if (tabConfig!=null) {

            if (treeItem==null) {
                treeItem = new TabConfigTreeItem(tabConfig);
                treeItem.setState(true);
                treeItem.setText(tabConfig.getLabel());
                // Actor
                treeItem.setHTML("<span class='gwt-TabBarItem' style='font-size:16px;background-color:#D0E4F6'><span style='margin:5px'>"+treeItem.getText()+"</span></span>");
                if (tabConfig.getType()!=null)
                    tcCodeMap.put(tabConfig.getType(), tabConfig.getTcCode());
//                tabConfig.getDisplayColorCode()
            }

            if (tabConfig.hasChildren()) {
                int idx = 0;

                // Profile
                for (TabConfig tc : tabConfig.getChildTabConfigs()) {
                    TabConfigTreeItem ti = new TabConfigTreeItem(tc);
                    ti.setState(true);

                    if (tabConfig.getType()!=null)
                        tcCodeMap.put(tabConfig.getType(), tabConfig.getTcCode());
                    if (tc.getType()!=null)
                        tcCodeMap.put(tc.getType(), tc.getTcCode());

                    FlowPanel flowPanel = new FlowPanel();
                    HTML statsBar = new HTML();
                    flowPanel.add(statsBar);

                    if (!tc.hasChildren()) {

//                        Window.alert(""+tcCodeMap.get("actor") + tcCodeMap.get("profile") + tcCodeMap.get("option"));

                        if (tcCodeMap.get("actor")!=null) {
                            ActorOptionConfig actorOption = new ActorOptionConfig(tcCodeMap.get("actor"));
                            if (tcCodeMap.get("profile")!=null) {
                                actorOption.setProfileId(IheItiProfile.find(tcCodeMap.get("profile")));
                                if (tcCodeMap.get("option")!=null) {
                                    actorOption.setOptionId(tcCodeMap.get("option"));
                                    try {
                                        setTestStatistics(statsBar, actorOption);
                                    } catch (Throwable t) {}
                                }
                            }
                        }
                    }

                    // Do not display group headers in the tree.
                    if (tc.isHeader()) {
                        if (tc.hasChildren()) {
                            flattenedTabConfig(tcCodeMap, treeItem, tc);
                        }
                    } else if (tc.isVisible()) {
                        // Option
                        HTML label = new HTML("<span style='font-size:14px;'>" + tc.getLabel() + "</span>");
                        flowPanel.add(label);
                        ti.setWidget(flowPanel);
                        ti.setIndex(idx++);

                        if (treeItem!=null)
                            treeItem.addItem(ti);

                        if (tc.hasChildren()) {
                            flattenedTabConfig(tcCodeMap, ti, tc);
                        }
                    }

                }

            } else if (!tabConfig.isHeader() && !tabConfig.hasChildren() && tabConfig.isVisible()){
                TabConfigTreeItem ti = new TabConfigTreeItem(tabConfig);
                ti.setState(true);
                ti.setHTML("<span style='font-size:14px;'>" + tabConfig.getLabel() + "</span>");
                if (tabConfig.getType()!=null)
                    tcCodeMap.put(tabConfig.getType(), tabConfig.getTcCode());

                return ti;
            }

        }
        return treeItem;
    }

    protected class TabConfigTreeItem extends TreeItem {
        private int index;
        public TabConfigTreeItem(TabConfig tabConfig) {
            setUserObject(tabConfig);
        }
        public TabConfig getTabConfig() {
            return (TabConfig)getUserObject();
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public TabConfig getTabConfig() {
        return tabConfig;
    }

    public void setTabConfig(TabConfig tabConfig) {
        this.tabConfig = tabConfig;
    }

}
