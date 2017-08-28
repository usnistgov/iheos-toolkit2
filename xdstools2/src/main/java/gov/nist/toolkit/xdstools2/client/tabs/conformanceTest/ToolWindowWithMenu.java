package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import gov.nist.toolkit.xdstools2.client.ToolWindow;

import java.util.HashMap;
import java.util.Map;

public abstract class ToolWindowWithMenu extends ToolWindow {

    // Tab config
    protected TabConfig tabConfig;
    protected int menuCols = 3;

    public ToolWindowWithMenu() {
    }

    public ToolWindowWithMenu(double east, double west) {
        super(east, west);
    }

    public abstract void onMenuSelect(TabConfig actor, Map<String,TabConfig> target);
    public abstract void setTestStatistics(HTML statsBar, ActorOption actorOption);

    public boolean displayMenu(Panel destinationPanel) {
        if (tabConfig!=null) {
            destinationPanel .clear();
            destinationPanel.getElement().getStyle().setMarginTop(10, Style.Unit.PX);

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

            // Add legend row
            FlowPanel legendRowPanel = new FlowPanel();
                legendRowPanel.add(new HTML("<div>Legend:</div>"
                        + "<div style='margin:3px'><div style=\"width:10px;height:14px;border:1px solid white;float:left;margin-right:2px;background-color:white;\"></div>Not Run</div>"
                        + "<div style='margin:3px'><div style=\"width:10px;height:14px;border:1px solid;float:left;margin-right:2px;background-color:cyan;\"></div><span>Successes</span></div>\n"
                        + "<div style='margin:3px'><div style=\"width:10px;height:14px;border:1px solid;float:left;margin-right:2px;background-color:coral;\"></div><span>Failures</span></div>\n"
                ));
            legendRowPanel.getElement().addClassName("tabConfigRow");
            destinationPanel.add(legendRowPanel);

            return true;
        }
        return false;
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
                treeItem.setHTML("<span class='gwt-TabBarItem' style='font-size:16px;background-color:#D0E4F6'><span style='margin:5px'>"+treeItem.getText()+"</span></span>");
                if (tabConfig.getType()!=null)
                    tcCodeMap.put(tabConfig.getType(), tabConfig.getTcCode());
//                tabConfig.getDisplayColorCode()
            }

            if (tabConfig.hasChildren()) {
                int idx = 0;
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
                            ActorOption actorOption = new ActorOption(tcCodeMap.get("actor"));
                            if (tcCodeMap.get("profile")!=null) {
                                actorOption.setProfileId(tcCodeMap.get("profile"));

                                if (tcCodeMap.get("option")!=null) {
                                    actorOption.setOptionId(tcCodeMap.get("option"));
                                    try {
                                        setTestStatistics(statsBar, actorOption);
                                    } catch (Throwable t) {}

                                }

                            }

                        }
                    }

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

            } else if (!tabConfig.isHeader() && !tabConfig.hasChildren()){

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

    // . TODO: create TabConfigTreeItem class, set user object (widget code ref. line 1390, line 1818)
    // . TODO add tree onselection event handler, open the tab


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


}
