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
import java.util.List;

/**
 *
 */
public class InspectorPresenter extends AbstractPresenter<InspectorView> implements DataNotification {
    private List<Result> results;
    private SiteSpec siteSpec;
    private MetadataCollection metadataCollection;
    List<AnnotatedItem> annotatedItems;

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
        // At this point there should be only one Result.TODO: Need to revisit this if there are more!? -- Conformance tests!
//        view.metadataInspectorRight.preInit();
//        view.metadataInspectorRight.init();
        view.metadataInspectorLeft.setDataNotification(this);
        setupInspectorWidget(view.metadataInspectorRight);
        metadataCollection = setupInspectorWidget(view.metadataInspectorLeft);
        annotatedItems = getMetadataObjectAnnotatedItems(metadataCollection);
        view.metadataObjectSelector.setNames(annotatedItems); // This will create the button list
    }

    @Override
    public void onAddToHistory(MetadataCollection metadataCollection) {
        this.metadataCollection = metadataCollection;
        annotatedItems = getMetadataObjectAnnotatedItems(metadataCollection);
        view.metadataObjectSelector.refreshEnabledStatus(annotatedItems);
    }

    @Override
    public void onObjectSelected(MetadataObjectWrapper objectWrapper) {
        try {
            MetadataObjectType currentObjectTypeSelection = MetadataObjectType.valueOf(view.metadataObjectSelector.getCurrentSelection());
            MetadataObjectType requestedObjectType = objectWrapper.getType();
            if (!currentObjectTypeSelection.equals(requestedObjectType)) {
                view.metadataObjectSelector.updateSiteSelectedView(requestedObjectType.name());
            }
            view.getTableMap().get(requestedObjectType).diffSelect.setValue(false,true);
            view.getTableMap().get(requestedObjectType).setSelectedRow(objectWrapper.getObject(), true);
        } catch (Exception ex) {
           GWT.log("onObjectSelected" + ex.toString());
        }
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

       doFocusTreeItem(metadataObjectType, view.metadataInspectorLeft.getTreeList(), null, left);
       doFocusTreeItem(metadataObjectType, view.metadataInspectorRight.getTreeList(), null, right);
    }

    /*
    public void resize() {
        GWT.log("Resizing objectRef table");
        getView().objectRefTable.resizeTable();
    }
    */

    List<AnnotatedItem> getMetadataObjectAnnotatedItems(MetadataCollection metadataCollection) {
        List<AnnotatedItem> annotatedItems = new ArrayList<>();
        annotatedItems.add(new AnnotatedItem(metadataCollection.objectRefs!=null && metadataCollection.objectRefs.size()>0, MetadataObjectType.ObjectRefs.name()));
        annotatedItems.add(new AnnotatedItem(metadataCollection.docEntries!=null && metadataCollection.docEntries.size()>0, MetadataObjectType.DocEntries.name()));
        annotatedItems.add(new AnnotatedItem(metadataCollection.submissionSets!=null && metadataCollection.submissionSets.size()>0, MetadataObjectType.SubmissionSets.name()));
        annotatedItems.add(new AnnotatedItem(metadataCollection.folders!=null && metadataCollection.folders.size()>0, MetadataObjectType.Folders.name()));
        annotatedItems.add(new AnnotatedItem(metadataCollection.assocs!=null && metadataCollection.assocs.size()>0, MetadataObjectType.Assocs.name()));
        return annotatedItems;
    }


    public void setDataModel(List<Result> results) {
        this.results = results;
    }

    public void setSiteSpec(SiteSpec siteSpec) {
        this.siteSpec = siteSpec;
    }

    void doUpdateChosenMetadataObjectType(String type) {
        MetadataObjectType metadataObjectType = MetadataObjectType.valueOf(type);
        doSwitchTable(metadataObjectType);
    }

    public void doSwitchTable(MetadataObjectType objectType) {
        view.objectRefTable.setData(metadataCollection.objectRefs);
        view.docEntryDataTable.setData(metadataCollection.docEntries);

        for (MetadataObjectType key : view.tableMap.keySet()) {
            DataTable dataTable = view.tableMap.get(key);
            if (key.equals(objectType)) {

                // Just redisplay current selection in table selection
                dataTable.asWidget().setVisible(true);
                if (dataTable!=null) {
                    if (!dataTable.diffSelect.getValue()) {
                        doSingleMode();
                        doFocusTreeItem(objectType, view.metadataInspectorLeft.getTreeList(), null, (MetadataObject)dataTable.lastSelectedObject);
                    } else {
                        doSetupDiffMode(true);
                        doDiffAction(key, (MetadataObject)dataTable.lastSelectedObject, (MetadataObject)dataTable.compareObject);
                    }
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
    public boolean doFocusTreeItem(MetadataObjectType metadataObjectType, List<Tree> treeList, final TreeItem root, MetadataObject target) {
        if (target!=null)
            return new TreeItemSelector(metadataObjectType, treeList).doFocusTreeItem(root, target);
        return false;
    }

    class TreeItemSelector {
        private List<Tree> treeList;
        private MetadataObjectType metadataObjectType;

        public TreeItemSelector(MetadataObjectType metadataObjectType, List<Tree> treeList) {
            this.metadataObjectType = metadataObjectType;
            this.treeList = treeList;
        }

        public boolean doFocusTreeItem(final TreeItem root, final MetadataObject target) {

            if (root == null) {

                for (Tree tree : treeList) {
                    // Assume only 1 child at the root level
//                    GWT.log("tree has " + tree.getItemCount() + " items.");
                    if (doFocusTreeItem(tree.getItem(0), target)) {
                        tree.getItem(0).setState(true);
                        return true;
                    }
                }
            } else {
                int childCt = root.getChildCount();
                if (root.getUserObject()!=null) {
                    if (attemptSelect(root,(MetadataObjectWrapper)root.getUserObject(), target)) {
                        root.setState(true);
                        return true;
                    }
                }
                if (childCt>0) {
                    for (int cx = 0; cx < childCt; cx++) {
                        TreeItem child = root.getChild(cx);
                        if (child.getUserObject()!=null) {
                            MetadataObjectWrapper userObject = (MetadataObjectWrapper) child.getUserObject();
                            if (attemptSelect(child, userObject, target)) {
                                root.setState(true);
                                return true;
                            }
                        }
                        if (child.getChildCount()>0) {
                            if (doFocusTreeItem(child, target)) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

        boolean attemptSelect(TreeItem treeItem, MetadataObjectWrapper userObject, MetadataObject target) {
            if (userObject != null && target!=null) {
                if (compareTo(userObject,target)) {
                    ((Hyperlink)treeItem.getWidget()).fireEvent(new ClickEvent() {});
                    treeItem.setSelected(true);
                    treeItem.setState(false, true);
                    if (view.metadataInspectorLeft.getCurrentSelectedTreeItem()!=null) {
                        view.metadataInspectorLeft.getCurrentSelectedTreeItem().getWidget().removeStyleName("insetBorder");
                    }
                    treeItem.getWidget().addStyleName("insetBorder");
                    view.metadataInspectorLeft.setCurrentSelectedTreeItem(treeItem);
                    return true;
                }
            }
            return false;
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
