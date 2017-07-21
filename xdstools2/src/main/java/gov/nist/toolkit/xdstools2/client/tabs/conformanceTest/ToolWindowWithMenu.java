package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import gov.nist.toolkit.xdstools2.client.ToolWindow;

import java.util.List;

public abstract class ToolWindowWithMenu extends ToolWindow {

    // Tab config
    protected TabConfig tabConfig;
    protected int menuCols = 4;

    public ToolWindowWithMenu() {
    }

    public ToolWindowWithMenu(double east, double west) {
        super(east, west);
    }

    public boolean displayMenu(Panel destinationPanel, List<String> displayOrder) {
        if (tabConfig!=null) {
            destinationPanel .clear();
            destinationPanel.getElement().getStyle().setMarginTop(10, Style.Unit.PX);

            int colWidth = 100 / menuCols;
            int menuCt = 0;
            boolean rowAdded = false;

            FlowPanel rowPanel = new FlowPanel();

            for (String code : displayOrder) {
                for (TabConfig tc : tabConfig.getChildTabConfigs()) {
                    if (code.equals(tc.getTcCode())) {
                        if (menuCt % menuCols == 0) {
                            rowAdded = true;
                            destinationPanel.add(rowPanel);
                            rowPanel =  new FlowPanel();
                        }
                        SimplePanel simplePanel = new SimplePanel();
                        Tree tree = new Tree();
                        TreeItem root = flattenedTabConfig(null, tc);
                        tree.addItem(root);
                        expandAllTreeItems(root);
                        simplePanel.setWidget(tree);
                        simplePanel.setWidth("" + colWidth + "%");
                        simplePanel.getElement().getStyle().setMarginBottom(16, Style.Unit.PX);
                        simplePanel.getElement().getStyle().setFloat(Style.Float.LEFT);
                        rowPanel.add(simplePanel);
                        rowPanel.getElement().addClassName("tabConfigRow");
                        menuCt++;
                        rowAdded = false;
//                        destinationPanel .add(simplePanel);
                    }
                }
            }

            if (!rowAdded) {
                destinationPanel.add(rowPanel);
            }

            return true;
        }
        return false;
    }

    private void expandAllTreeItems(TreeItem ti) {
       ti.setState(true);
       int childCt = ti.getChildCount();
          for (int cx=0; cx<childCt; cx++)  {
              expandAllTreeItems(ti.getChild(cx));
          }
    }

    private TreeItem flattenedTabConfig(TreeItem treeItem, TabConfig tabConfig) {
        if (tabConfig!=null) {

            if (treeItem==null) {
                treeItem = new TreeItem();
                treeItem.setState(true);
                treeItem.setText(tabConfig.getLabel());
                treeItem.setHTML("<span style='font-size:16px;background-color:"+tabConfig.getDisplayColorCode()+"'>"+treeItem.getText()+"</span>");
            }

            if (tabConfig.hasChildren()) {

                for (TabConfig tc : tabConfig.getChildTabConfigs()) {
                    TreeItem ti = new TreeItem();
                    ti.setState(true);
                    ti.setText(tc.getLabel());

                    if (treeItem!=null)
                        treeItem.addItem(ti);

                    if (tc.hasChildren()) {
                        flattenedTabConfig(ti, tc);
                    }
                }

            } else if (!tabConfig.isHeader() && !tabConfig.hasChildren()){

                TreeItem ti = new TreeItem();
                ti.setState(true);
                ti.setText(tabConfig.getLabel());

                return ti;
            }

        }
        return treeItem;
    }

}
