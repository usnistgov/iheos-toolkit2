package gov.nist.toolkit.xdstools2.client.inspector.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.xdstools2.client.inspector.DataNotification;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataObjectType;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataObjectWrapper;
import gov.nist.toolkit.xdstools2.client.util.AnnotatedItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class InspectorPresenter extends AbstractPresenter<InspectorView> {
    private Collection<Result> results;
    private SiteSpec siteSpec;
    private MetadataCollection metadataCollection;
    List<AnnotatedItem> annotatedItems;
    Map<MetadataObjectType, List<? extends MetadataObject>> dataMap = new HashMap<>();

    @Override
    public void init() {
        GWT.log("Init InspectorPresenter");

        setData();

        // The provided EventBus doesn't seem to work ?


//        doResizeTable();


    }

    public void setupResizeTableTimer(final MetadataObjectType objectType) {

        /*
        Scheduler is required because view is built before it is actually displayed.
        https://www.mail-archive.com/google-web-toolkit@googlegroups.com/msg75253.html
         */
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                if (isTableCellHeightAvailable()) {
                    view.getTableMap().get(objectType).resizeTable();
                    view.getTableMap().get(objectType).pageToBeIn();
                    return false;
                } else return true;
            }

            boolean isTableCellHeightAvailable() {
                try {
                    int height = view.getTableMap().get(objectType).dataTable.getRowElement(0).getClientHeight();
                    if (height > 0) {
//                        GWT.log("Table cell height is available! cell height is:" + height);
                      return true;
                    }
                } catch (Throwable t) {
                }
                return false;
            }

        },200);
    }

    private void setData() {
        GWT.log("In setData");

        GWT.log("result list size is: " + results.size());
        view.metadataInspectorLeft.setDataNotification(new DataNotification() {
            @Override
        public boolean inCompare() {
            String currentObjectType = view.metadataObjectSelector.getCurrentSelection();
            if (currentObjectType!=null) {
                MetadataObjectType currentObjectTypeSelection = MetadataObjectType.valueOf(currentObjectType);
                if (currentObjectTypeSelection != null) {
                    return view.getTableMap().get(currentObjectTypeSelection).diffSelect.getValue();
                }
            }
            return false;
        }

            @Override
            public void onAddToHistory(MetadataCollection metadataCollection) {
                InspectorPresenter.this.metadataCollection = metadataCollection;
                setDataMap(metadataCollection);
                annotatedItems = getMetadataObjectAnnotatedItems(metadataCollection);
                view.metadataObjectSelector.refreshEnabledStatus(annotatedItems);
                doRefreshTable();
            }

            @Override
            public void onObjectSelected(MetadataObjectWrapper objectWrapper) {
                if (objectWrapper==null) return;
                try {
                    MetadataObjectType requestedObjectType = objectWrapper.getType();
                    String currentObjectTypeSelectionStr = view.metadataObjectSelector.getCurrentSelection();
                    if (currentObjectTypeSelectionStr!=null) {
                        MetadataObjectType currentObjectTypeSelection = MetadataObjectType.valueOf(currentObjectTypeSelectionStr);
                        if (!currentObjectTypeSelection.equals(requestedObjectType)) {
                            view.metadataObjectSelector.updateSiteSelectedView(requestedObjectType.name());
                        }
                    } else {
                        view.metadataObjectSelector.updateSiteSelectedView(requestedObjectType.name());
                    }

                    view.getTableMap().get(requestedObjectType).diffSelect.setValue(false,true);
                    view.getTableMap().get(requestedObjectType).setSelectedRow(objectWrapper.getObject(), true);
                    view.getTableMap().get(requestedObjectType).pageToBeIn();
                } catch (Exception ex) {
                    GWT.log("onObjectSelected" + ex.toString());
                }
            }

            @Override
            public void onCloseOffDetail(TreeItem currentTreeItem) {
                MetadataObjectType objectType = MetadataObjectType.valueOf(view.metadataObjectSelector.getCurrentSelection());
                MetadataObject toClose = ((MetadataObjectWrapper)currentTreeItem.getUserObject()).getObject();
                MetadataObject toFocus = null;
                DataTable dataTable = view.tableMap.get(objectType);
                if (dataTable.lastSelectedObject.equals(toClose)) {
                    toFocus = (MetadataObject)dataTable.compareObject;
                } else {
                    toFocus = (MetadataObject)dataTable.lastSelectedObject;
                }
                doSingleMode();
                dataTable.lastSelectedObject = toFocus;
                dataTable.compareObject = null;
                dataTable.diffSelect.setValue(false,true);
                TreeItem treeItem = doFocusTreeItem(objectType, view.metadataInspectorLeft.getTreeList(), null, toFocus, view.metadataInspectorLeft.getCurrentSelectedTreeItem());
                if (treeItem!=null)
                    view.metadataInspectorLeft.setCurrentSelectedTreeItem(treeItem);
            }

            @Override
            public void onHistoryContentModeChanged(MetadataObjectWrapper objectWrapper) {
                if (objectWrapper==null) return;
              TreeItem treeItem = doFocusTreeItem(objectWrapper.getType(), view.metadataInspectorLeft.getTreeList(), null, objectWrapper.getObject(), null /* Passing a Null will select/focus even if it is the same node */);
              if (treeItem!=null)
                view.metadataInspectorLeft.setCurrentSelectedTreeItem(treeItem);
            }
        });
        view.metadataInspectorRight.setDataNotification(new DataNotification() {
            @Override
            public void onAddToHistory(MetadataCollection metadataCollection) {}

            @Override
            public void onObjectSelected(MetadataObjectWrapper objectWrapper) {}

            @Override
            public boolean inCompare() {
                return view.metadataInspectorLeft.getDataNotification().inCompare();
            }

            @Override
            public void onCloseOffDetail(TreeItem currentTreeItem) {
                view.metadataInspectorLeft.getDataNotification().onCloseOffDetail(currentTreeItem);
            }

            @Override
            public void onHistoryContentModeChanged(MetadataObjectWrapper objectWrapper) {}
        });
        setupInspectorWidget(view.metadataInspectorRight);
        metadataCollection = setupInspectorWidget(view.metadataInspectorLeft);
        setDataMap(metadataCollection);
        annotatedItems = getMetadataObjectAnnotatedItems(metadataCollection);
        view.metadataObjectSelector.setNames(annotatedItems); // This will create the button list
    }

    void setDataMap(MetadataCollection metadataCollection) {
        dataMap.put(MetadataObjectType.ObjectRefs, metadataCollection.objectRefs);
        dataMap.put(MetadataObjectType.DocEntries, metadataCollection.docEntries);
        dataMap.put(MetadataObjectType.SubmissionSets, metadataCollection.submissionSets);
        dataMap.put(MetadataObjectType.Folders, metadataCollection.folders);
        dataMap.put(MetadataObjectType.Assocs, metadataCollection.assocs);
    }


    public MetadataObjectType autoSelectObjectType() {
        AnnotatedItem defaultItem = null;
        // Make DocEntry the default object type selection when more than one are available at loading time.
        for (AnnotatedItem annotatedItem : annotatedItems) {
           if (annotatedItem.isEnabled()) {
               if (MetadataObjectType.DocEntries.equals(MetadataObjectType.valueOf(annotatedItem.getName()))) {
                   defaultItem = annotatedItem;
                   break;
               }
               if (defaultItem==null)
                   defaultItem = annotatedItem;
           }
        }
        if (defaultItem!=null) {
            view.metadataObjectSelector.updateSiteSelectedView(defaultItem.getName());
            return MetadataObjectType.valueOf(defaultItem.getName());
        }
        return null;
    }


    public void doSetupDiffMode(boolean isSelected) {
        if (isSelected) {
//            view.metadataInspectorRight.setSiteSpec(siteSpec);
//            view.metadataInspectorRight.setResults(view.metadataInspectorLeft.getResults());
            view.metadataInspectorRight.setResults(null);
            view.metadataInspectorRight.setData(view.metadataInspectorLeft.getData());
            view.metadataInspectorRight.init();
        }
        // hide history pane when Diff is selected
        view.metadataInspectorRight.showHistory(!isSelected);
        view.metadataInspectorRight.showStructure(!isSelected);
    }

    private MetadataCollection setupInspectorWidget(MetadataInspectorTab inspector) {
        inspector.setResults(results);
        inspector.setSiteSpec(siteSpec);
        inspector.preInit();
        return inspector.init();
    }

    public void doDiffAction(MetadataObjectType metadataObjectType, MetadataObject left, MetadataObject right) {

        view.inspectorWrapper.add(view.metadataInspectorRight.asWidget());

       view.metadataInspectorLeft.showHistory(false);
       view.metadataInspectorLeft.showStructure(false);

       TreeItem treeItem = doFocusTreeItem(metadataObjectType, view.metadataInspectorLeft.getTreeList(), null, left, view.metadataInspectorLeft.getCurrentSelectedTreeItem());
       if (treeItem!=null)
            view.metadataInspectorLeft.setCurrentSelectedTreeItem(treeItem);
       TreeItem compare = doFocusTreeItem(metadataObjectType, view.metadataInspectorRight.getTreeList(), null, right, view.metadataInspectorLeft.getCurrentSelectedTreeItem());
       if (compare!=null)
            view.metadataInspectorRight.setCurrentSelectedTreeItem(compare);
    }

    /*
    public void resize() {
        GWT.log("Resizing objectRef table");
        getView().objectRefTable.resizeTable();
    }
    */

    List<AnnotatedItem> getMetadataObjectAnnotatedItems(MetadataCollection metadataCollection) {
        List<AnnotatedItem> annotatedItems = new ArrayList<>();

        for (MetadataObjectType type : MetadataObjectType.values()) {
            List<? extends MetadataObject> metadataObject = dataMap.get(type);
            annotatedItems.add(new AnnotatedItem(metadataObject!=null && metadataObject.size()>0, type.name()));
        }

        return annotatedItems;
    }


    public void setDataModel(Collection<Result> results) {
        this.results = results;
    }

    public void setSiteSpec(SiteSpec siteSpec) {
        this.siteSpec = siteSpec;
    }

    void doRefreshTable() {
        try {
            String currentObjectType = view.metadataObjectSelector.getCurrentSelection();
            if (currentObjectType != null) {
                MetadataObjectType currentObjectTypeSelection = MetadataObjectType.valueOf(currentObjectType);
                if (currentObjectTypeSelection != null) {
                    view.getTableMap().get(currentObjectTypeSelection).setData(dataMap.get(currentObjectTypeSelection));
                }
            }
        } catch (Exception ex) {}
    }

    void doUpdateChosenMetadataObjectType(String type) {
        MetadataObjectType metadataObjectType = MetadataObjectType.valueOf(type);
        doSwitchTable(metadataObjectType);
    }

    public void doSwitchTable(MetadataObjectType targetObjectType) {
        for (MetadataObjectType key : view.tableMap.keySet()) {
            DataTable dataTable = view.tableMap.get(key);
            if (key.equals(targetObjectType)) {
                // Redisplay current selection in table selection. Without this there is no data display.
                if (dataTable!=null) {
                    dataTable.setData(dataMap.get(targetObjectType));
                    dataTable.asWidget().setVisible(true);
                    if (!dataTable.diffSelect.getValue()) {
                        doSingleMode();
                        MetadataObject lastSelection = (MetadataObject)dataTable.lastSelectedObject;
                        if (lastSelection!=null) {
                            TreeItem treeItem = doFocusTreeItem(targetObjectType, view.metadataInspectorLeft.getTreeList(), null, lastSelection, view.metadataInspectorLeft.getCurrentSelectedTreeItem());
                            if (treeItem!=null) {
                                  view.metadataInspectorLeft.setCurrentSelectedTreeItem(treeItem);
                            }
                        }
                        // If there is nothing, clear previous selection.
                        /* else {
                            TreeItem currentSelectedTreeItem = view.metadataInspectorLeft.getCurrentSelectedTreeItem();
                            if (currentSelectedTreeItem!=null) {
                                currentSelectedTreeItem.getWidget().removeStyleName("insetBorder");
                            }
                        } */
                    } else {
                        doSetupDiffMode(true);
                        doDiffAction(key, (MetadataObject)dataTable.lastSelectedObject, (MetadataObject)dataTable.compareObject);
                    }
                    setupResizeTableTimer(key);
                }

            } else {
                dataTable.asWidget().setVisible(false);
            }
//            dataTable.diffSelect.setValue(false,true);
        }

    }

    public void doSingleMode() {
        view.metadataInspectorLeft.showHistory(true);
        view.metadataInspectorLeft.showStructure(true);

        if (view.inspectorWrapper.getWidgetCount()>1)
            view.inspectorWrapper.remove(1);
    }


    /**
     *
     * @param root
     * @param target
     * @return True if object was located/found in the tree, selected
     */
    public TreeItem doFocusTreeItem(MetadataObjectType metadataObjectType, List<Tree> treeList, final TreeItem root, MetadataObject target, TreeItem currentSelection) {

        TreeItemSelector treeItemSelector = new TreeItemSelector(metadataObjectType, treeList, currentSelection);
       // compare existing selectedTreeItem and the target, if compareTo returns true, or meaning they are same, exit focus. There is a case where tree two nodes can have the same id, object type.
        // Selecting from the tree triggers a custom event to select the corresponding row in the advance option table, then in turn which triggers a selection change event that calls this method.
        // The effect is the 'duplicate' node may be selected in case if it appears before the one that was actually selected.
        if (currentSelection!=null && currentSelection.getUserObject()!=null && currentSelection.getUserObject() instanceof MetadataObjectWrapper) {
           if (treeItemSelector.compareTo((MetadataObjectWrapper)currentSelection.getUserObject(), target) )
               return null;
        }

        if (target!=null) {
            return treeItemSelector.doFocusTreeItem(root, target);
        }
        return null;
    }

    class TreeItemSelector {
        private TreeItem currentSelection;
        private List<Tree> treeList;
        private MetadataObjectType metadataObjectType;

        public TreeItemSelector(MetadataObjectType metadataObjectType, List<Tree> treeList, TreeItem currentSelection) {
            this.metadataObjectType = metadataObjectType;
            this.treeList = treeList;
            this.currentSelection = currentSelection;
        }

        public TreeItem doFocusTreeItem(final TreeItem root, final MetadataObject target) {

            if (root == null) {

                for (Tree tree : treeList) {
                    // In the History mode, there is only 1 child at the root level
//                    GWT.log("tree has " + tree.getItemCount() + " items.");
                    // However, In the Compare mode, there is only 1 tree but many items.

                    int childCt = tree.getItemCount();
                    for (int cx = 0; cx < childCt; cx++) {
                        TreeItem treeItem = doFocusTreeItem(tree.getItem(cx), target);
                        if (treeItem!=null) {
                            tree.getItem(0).setState(true);
                            return treeItem;
                        }
                    }
                }
            } else {
                int childCt = root.getChildCount();
                if (root.getUserObject()!=null) {
                    TreeItem treeItem = attemptSelect(root,(MetadataObjectWrapper)root.getUserObject(), target);
                    if (treeItem!=null) {
                        root.setState(true);
                        return treeItem;
                    }
                }
                if (childCt>0) {
                    for (int cx = 0; cx < childCt; cx++) {
                        TreeItem child = root.getChild(cx);
                        if (child.getUserObject()!=null) {
                            MetadataObjectWrapper userObject = (MetadataObjectWrapper) child.getUserObject();
                            TreeItem treeItem = attemptSelect(child, userObject, target);
                            if (treeItem!=null) {
                                root.setState(true);
                                return treeItem;
                            }
                        }
                        if (child.getChildCount()>0) {
                            TreeItem treeItem = doFocusTreeItem(child, target);
                            if (treeItem!=null) {
                                return treeItem;
                            }
                        }
                    }
                }
            }
            return null;
        }

        TreeItem attemptSelect(TreeItem treeItem, MetadataObjectWrapper userObject, MetadataObject target) {
            if (userObject != null && target!=null) {
                if (compareTo(userObject,target)) {
                    ((Hyperlink)treeItem.getWidget()).fireEvent(new ClickEvent() {});
                    treeItem.setSelected(true);
//                    treeItem.setState(false, true);
                    if (currentSelection!=null) {
                        currentSelection.getWidget().removeStyleName("insetBorder");
                    }
                    treeItem.getWidget().addStyleName("insetBorder");
                    return treeItem;
                }
            }
            return null;
        }

        public boolean compareTo(MetadataObjectWrapper userObject, MetadataObject target) {

            if (metadataObjectType.equals(userObject.getType())) {
                MetadataObject source = userObject.getObject();
                if (source == target) return true;
                if (!(target instanceof MetadataObject)) return false;



                if (source!=null && target!=null) {
                    if (source.id== null || target.id==null)
                        return false;
                }


                if (source.id != null ? !source.id.equals(target.id) : target.id != null) {
                    return false;
                }

                return source.home != null ? source.home.equals(target.home) : target.home == null || "".equals(target.home);
            }
            return false;


        }

    }

    public void doAdvancedOptionToggle(HTML ctl, FlowPanel panel) {
        boolean isPanelVisible = panel.isVisible();
        if (isPanelVisible) {
            ctl.removeStyleName("insetBorder");
            ctl.addStyleName("outsetBorder");
            doSingleMode();
        } else {
            ctl.removeStyleName("outsetBorder");
            ctl.addStyleName("insetBorder");
            if (view.metadataObjectSelector.getCurrentSelection()==null) {
                MetadataObjectType objectType = autoSelectObjectType();
                if (objectType != null) {
                    setupResizeTableTimer(objectType);
                }
            } else {
                String currentObjectType = view.metadataObjectSelector.getCurrentSelection();
                if (currentObjectType != null && !"".equals(currentObjectType)) {
                    setupResizeTableTimer(MetadataObjectType.valueOf(currentObjectType));
                }
            }
        }
        panel.setVisible(!isPanelVisible);
    }

}
