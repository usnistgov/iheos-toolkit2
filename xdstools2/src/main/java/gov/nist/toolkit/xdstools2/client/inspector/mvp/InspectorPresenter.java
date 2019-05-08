package gov.nist.toolkit.xdstools2.client.inspector.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.DocumentEntryDiff;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.xdstools2.client.inspector.DataModel;
import gov.nist.toolkit.xdstools2.client.inspector.DataNotification;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataObjectType;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataObjectWrapper;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.FilterFeature;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryFilterDisplay;
import gov.nist.toolkit.xdstools2.client.util.AnnotatedItem;
import gov.nist.toolkit.xdstools2.client.widgets.ButtonListSelector;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

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
    List<AnnotatedItem> metadataObjectTypeSelectionItems;
    Map<MetadataObjectType, List<? extends MetadataObject>> dataMap = new HashMap<>();
    List<AnnotatedItem> filterSelectionItems;

    DataModel dmTemp;
    Collection<Result> resultsTemp;
    MetadataCollection mcTemp;

    @Override
    public void init() {
        GWT.log("Init InspectorPresenter: " + getTitle());

        setData();
    }

    public void setupDefaultInspector() {
        // Normal data browsing/inspector
        setupInspectorWidget(results, metadataCollection, siteSpec, view.metadataInspectorRight);
        setupInspectorWidget(results, metadataCollection, siteSpec, view.metadataInspectorLeft);
    }

    public void postInit() {
        setDataMap(metadataCollection);
        doSelectorSetup();
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

        if (results != null) {
            GWT.log("Result mode. Result list size is: " + results.size());
        }

        view.setDeFilterFeature(new DocumentEntryFilterDisplay() {
            boolean isFilterApplied = false;

            @Override
            public String getFilterName() {
                if ("ResultInspector".equals(getTitle())) {
                   return "Apply Filter";
                } else if ("SimIndexInspector".equals(getTitle())) {
                   return "Display Results";
                }
                return "Unknown Filter";
            }

            @Override
            public boolean isRemoveEnabled() {
                if ("ResultInspector".equals(getTitle())) {
                    return true;
                } else if ("SimIndexInspector".equals(getTitle())) {
                    return false;
                }
                return false;
            }

            @Override
            public void applyFilter() {
                try {
                    doApplyFilter(getFilteredData());
                    isFilterApplied = true;
                    if ("ResultInspector".equals(getTitle())) {
                        displayResultNotice( "", true);
                    } else if ("SimIndexInspector".equals(getTitle())) {
                        displayResultNotice( "Note: DocumentEntry Author information may not be persisted in the SimIndex.", true);
                    }
                } catch (Exception ex) {
                    new PopupMessage(ex.toString());
                }
            }

            @Override
            public void removeFilter() {
                if (isFilterApplied) {
                    try {
                        doRemoveFilter();
                        doSelectorSetup();
                        isFilterApplied = false;
                    } catch (Exception ex) {
                        new PopupMessage(ex.toString());
                    }
                }
            }

            @Override
            public boolean isActive() {
                return isFilterApplied;
            }
        });


        view.metadataInspectorLeft.setDataNotification(new DataNotification() {
            @Override
            public boolean inCompare() {
                return false;
            }

            @Override
            public void onAddToHistory(MetadataCollection metadataCollection) {
                InspectorPresenter.this.metadataCollection = metadataCollection;
                setDataMap(metadataCollection);
                metadataObjectTypeSelectionItems = getMetadataObjectSelectionItems();
                view.metadataObjectSelector.setNames(metadataObjectTypeSelectionItems);
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

                    view.getTableMap().get(requestedObjectType).compareSelect.setValue(false,true);
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
                DataTable dataTable = view.getTableMap().get(objectType);
                if (dataTable.lastSelectedObject.equals(toClose)) {
                    toFocus = (MetadataObject)dataTable.compareObject;
                } else {
                    toFocus = (MetadataObject)dataTable.lastSelectedObject;
                }
                doSingleMode();
                dataTable.lastSelectedObject = toFocus;
                dataTable.compareObject = null;
                dataTable.compareSelect.setValue(false,true);
                TreeItem treeItem = doFocusTreeItem(objectType, view.metadataInspectorLeft.getTreeList(), null, toFocus, view.metadataInspectorLeft.getCurrentSelectedTreeItem());
                if (treeItem!=null)
                    view.metadataInspectorLeft.setCurrentSelectedTreeItem(treeItem);
            }

            @Override
            public void onViewModeChanged(MetadataInspectorTab.SelectedViewMode viewMode, MetadataObjectWrapper objectWrapper) {
                if (MetadataInspectorTab.SelectedViewMode.CONTENT.equals(viewMode)) {
                    if (view.filterObjectSelector.getCurrentSelection() == null) {
                        filterSelectionItems = getFilterObjectSelectionItems();
                        view.filterObjectSelector.setNames(filterSelectionItems);
                    }
                    if (view.filterObjectSelector.getItems() != null && view.filterObjectSelector.getItems().size() > 0) {
                        // enable the filter ctl
                        view.showFilterCtl(true);
                    }
                } else {
                    /**
                     * Filter Feature Availability Note
                     * As to why the filter feature is not available in History view mode:
                     * The History mode expects Results object which is dependent on a query-tool and further not necessary to perform two things:
                     * 1) Browsing the registry simulator without any context
                     * 2) Filtering once the Inspector is already opened from a query-tool.
                     */
                    // close panel if open
                    if (view.contentFilterPanel.isVisible()) {
                        doFilterOptionToggle(view.contentFilterCtl, view.contentFilterPanel);
                    }
                    // disable the filter ctl
                    view.showFilterCtl(false);

                }

                if (objectWrapper==null) return;

              TreeItem treeItem = doFocusTreeItem(objectWrapper.getType(), view.metadataInspectorLeft.getTreeList(), null, objectWrapper.getObject(), null /* Passing a Null will select/focus even if it is the same node */);
              if (treeItem!=null)
                  view.metadataInspectorLeft.setCurrentSelectedTreeItem(treeItem);
            }

            @Override
            public MetadataObject getComparable() {
                return null;
            }
        });
        view.metadataInspectorRight.setDataNotification(new DataNotification() {
            @Override
            public void onAddToHistory(MetadataCollection metadataCollection) {}

            @Override
            public void onObjectSelected(MetadataObjectWrapper objectWrapper) {}

            @Override
            public boolean inCompare() {
                String currentObjectType = view.metadataObjectSelector.getCurrentSelection();
                if (currentObjectType!=null) {
                    MetadataObjectType currentObjectTypeSelection = MetadataObjectType.valueOf(currentObjectType);
                    if (currentObjectTypeSelection != null) {
                        return view.getTableMap().get(currentObjectTypeSelection).compareSelect.getValue();
                    }
                }
                return false;
            }

            @Override
            public void onCloseOffDetail(TreeItem currentTreeItem) {
                view.metadataInspectorLeft.getDataNotification().onCloseOffDetail(currentTreeItem);
            }

            @Override
            public void onViewModeChanged(MetadataInspectorTab.SelectedViewMode viewMode, MetadataObjectWrapper objectWrapper) {}

            /**
             *
             * @return Null if nothing to compare against
             */
            @Override
            public MetadataObject getComparable() {
                if (inCompare()) {
                   return view.metadataInspectorRight.getComparableMetadata();
                } else {
                    return null;
                }
            }
        });


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
        for (AnnotatedItem annotatedItem : metadataObjectTypeSelectionItems) {
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
            view.metadataInspectorRight.setResults(null);
            view.metadataInspectorRight.setData(view.metadataInspectorLeft.getData());
            view.metadataInspectorRight.init();
        }
        // hide history pane when Diff is selected
        view.metadataInspectorRight.showHistory(!isSelected);
        view.metadataInspectorRight.showStructure(!isSelected);

        // DocEntry highlight indicator
        if (isSelectedType(MetadataObjectType.DocEntries)) {
            doShowHighlightIndicator(isSelected, "Limited capability: Excludes ReferenceIdList and extraMetadata.");
        } else {
            doShowHighlightIndicator(false, "");
        }
    }

    private void setupInspectorWidget(Collection<Result> results, MetadataCollection metadataCollection, SiteSpec siteSpec, MetadataInspectorTab inspector) {
        inspector.setResults(results);
        inspector.setMetadataCollection(metadataCollection);
        inspector.setSiteSpec(siteSpec);
        inspector.preInit();
        if ("SimIndexInspector".equals(getTitle())) {
            inspector.setExclusiveViewMode(MetadataInspectorTab.SelectedViewMode.CONTENT);
        }
        inspector.init();

    }

    public void doDiffAction(MetadataObjectType metadataObjectType, MetadataObject left, MetadataObject right) {
        if (isSelectedType(MetadataObjectType.DocEntries)) {
            /*
             add Mo.right to insp.right
                Call stack to the method that needs to compare:
                Hyperlink h = HyperlinkFactory.link(tab, de);
                HistorySelector(MetadataInspectorTab it, MetadataObject o)
             access Mo.right from new DetailDisplay(it).displayDetail(mo, MetadataDiff.nullObject(mo));
                call compare from detaildisp
             */
          view.metadataInspectorRight.setComparableMetadata(left);
        }
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

    /**
     *  DocumentEntry has a highlight differences feature
     */
    void doShowHighlightIndicator(boolean isSelected, String toolTip) {
        DataTable dataTable = getDataTable(getCurrentSelectedType());
        CheckBox highlightDiffCbx = dataTable.highlightDifferences;
        highlightDiffCbx.setVisible(isSelected);
        highlightDiffCbx.setValue(isSelected);
        highlightDiffCbx.setTitle(toolTip);
        highlightDiffCbx.setEnabled(false);
    }

    MetadataObjectType getCurrentSelectedType() {
        String currentObjectType = view.metadataObjectSelector.getCurrentSelection();
        if (currentObjectType != null) {
            MetadataObjectType currentObjectTypeSelection = MetadataObjectType.valueOf(currentObjectType);
            return currentObjectTypeSelection;
        }
        throw new ToolkitRuntimeException("Object type not selected");
    }

    boolean isSelectedType(MetadataObjectType metadataObjectType) {
        return getCurrentSelectedType().equals(metadataObjectType);
    }
    DataTable getDataTable(MetadataObjectType metadataObjectType) {
        if (metadataObjectType==null)
            metadataObjectType = getCurrentSelectedType();

        if (view.getTableMap().containsKey(metadataObjectType))
            return view.getTableMap().get(metadataObjectType);
        throw new ToolkitRuntimeException("DataTable for " + metadataObjectType.name() + " does not exist.");
    }

    /*
    public void resize() {
        GWT.log("Resizing objectRef table");
        getView().objectRefTable.resizeTable();
    }
    */

    List<AnnotatedItem> getMetadataObjectSelectionItems() {
        List<AnnotatedItem> annotatedItems = new ArrayList<>();

        for (MetadataObjectType type : MetadataObjectType.values()) {
            List<? extends MetadataObject> metadataObject = dataMap.get(type);
            annotatedItems.add(new AnnotatedItem(metadataObject != null && metadataObject.size()>0, type.name()));
        }

        return annotatedItems;
    }

    List<AnnotatedItem> getFilterObjectSelectionItems() {
       List<AnnotatedItem> annotatedItems = new ArrayList<>();

       MetadataObjectType type = MetadataObjectType.DocEntries;
       List<? extends MetadataObject> metadataObject = dataMap.get(type);
       annotatedItems.add(new AnnotatedItem(metadataObject != null && metadataObject.size()>0, type.name()));

        return annotatedItems;
    }


    public void setDataModel(MetadataCollection mc) {
        this.metadataCollection = mc;
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
                // 1. refresh the table data for the current selected metadata type
                if (currentObjectTypeSelection != null) {
                    view.getTableMap().get(currentObjectTypeSelection).setData(dataMap.get(currentObjectTypeSelection));
                }
                // This is probably not needed since doSwitchTable takes care of it.
                // 2. refresh the table data for the other metadata types
//                for (MetadataObjectType type : MetadataObjectType.values()) {
//                   if (!type.equals(currentObjectTypeSelection)) {
//                      view.getTableMap().get(type).setData(dataMap.get(type));
//                   }
//                }

            }
        } catch (Exception ex) {}
    }

    void doUpdateChosenMetadataObjectType(String type) {
        MetadataObjectType metadataObjectType = MetadataObjectType.valueOf(type);
        doSwitchTable(metadataObjectType);
    }

    void doUpdateChosenFilterObjectType(String type) {
        MetadataObjectType metadataObjectType = MetadataObjectType.valueOf(type);
        doSwitchFilter(metadataObjectType);
    }

    void doApplyFilter(List<DocumentEntry> fDeList) {
        if (fDeList != null && ! fDeList.isEmpty()) {
            // backup the current state off the inspector
            // 1. data, 2. results, 3. mc

            if ("ResultInspector".equals(getTitle())) {
                backupCurrentDataToTemp();
            }

            if (view.metadataObjectSelector.getItems().contains(new AnnotatedItem(true, MetadataObjectType.DocEntries.name()))) {
                doSwitchTable(MetadataObjectType.DocEntries);
                DataTable<DocumentEntry> deDt = view.getTableMap().get(MetadataObjectType.DocEntries);
                if (deDt.compareSelect.getValue()) {
                    deDt.compareSelect.setValue(false, true);
                }

                MetadataCollection fMc = new MetadataCollection();
                fMc.init();
                fMc.docEntries.addAll(fDeList);

                try {
                    // use the left inspector as the primary inspector
                    setupInspectorWidget(null, fMc, siteSpec, view.metadataInspectorLeft);
                    // the right inspector is a secondary one for comparison purposes
                    setupInspectorWidget(null, fMc, siteSpec, view.metadataInspectorRight);

                    setDataMap(fMc);

                    if ("SimIndexInspector".equals(getTitle())) {
                        view.metadataInspectorLeft.setExclusiveViewMode(MetadataInspectorTab.SelectedViewMode.CONTENT);
                    }

                    // restrict metadata selection to the same as the filter options
                    view.metadataObjectSelector.setNames(getFilterObjectSelectionItems()); // This will create the button list
                    view.metadataObjectSelector.doSelected(MetadataObjectType.DocEntries.name());

                } catch (Exception ex) {
                    new PopupMessage("Filter could not be applied.");
                    if ("ResultInspector".equals(getTitle())) {
                        doRemoveFilter();
                        doSelectorSetup();
                    }
                }
            }
        }
    }

    private void backupCurrentDataToTemp() {
        dmTemp =  view.metadataInspectorLeft.getData();
        mcTemp = dmTemp.getCombinedMetadata();
        resultsTemp = view.metadataInspectorLeft.getResults();
    }

    void doRemoveFilter() {
        if ("ResultInspector".equals(getTitle())) {
            // Restore setDataMap with original mc only if in the Result Inspector

            MetadataInspectorTab inspector = view.metadataInspectorRight;
            inspector.setData(dmTemp);
            inspector.setResults(null);
            inspector.setMetadataCollection(dmTemp.getCombinedMetadata());
            inspector.setSiteSpec(siteSpec);
            inspector.init();

            inspector = view.metadataInspectorLeft;
            inspector.setData(dmTemp);
            inspector.setResults(null);
            inspector.setMetadataCollection(dmTemp.getCombinedMetadata());
            inspector.setSiteSpec(siteSpec);
            inspector.init();

            setDataMap(mcTemp);
        }
    }

    void doSelectorSetup() {
        metadataObjectTypeSelectionItems = getMetadataObjectSelectionItems();
        view.metadataObjectSelector.setNames(metadataObjectTypeSelectionItems); // This will create the button list
        filterSelectionItems = getFilterObjectSelectionItems();
        view.filterObjectSelector.setNames(filterSelectionItems); // This will create the button list
    }


    public void doSwitchFilter(MetadataObjectType targetObjectType) {
       for (MetadataObjectType key : view.getFilterFeatureMap().keySet()) {
           FilterFeature filterFeature = view.getFilterFeatureMap().get(key);
           if (filterFeature !=null && key.equals(targetObjectType)) {
                if (! filterFeature.isActive()) {
                   filterFeature.setData(dataMap.get(key));
                   filterFeature.displayFilter();
                }
                filterFeature.asWidget().setVisible(true);
           } else {
               filterFeature.asWidget().setVisible(false);
           }
       }
    }

    public void doSwitchTable(MetadataObjectType targetObjectType) {
        for (MetadataObjectType key : view.getTableMap().keySet()) {
            DataTable dataTable = view.getTableMap().get(key);
            if (key.equals(targetObjectType)) {
                // Redisplay current selection in table selection. Without this there is no data display.
                if (dataTable != null) {
                    dataTable.setData(dataMap.get(targetObjectType));
                    dataTable.asWidget().setVisible(true);
                    if (! dataTable.compareSelect.getValue()) {
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
        }

    }

    public void doSingleMode() {
        if ("ResultInspector".equals(getTitle())) {
            try {
                view.metadataInspectorLeft.showHistory(true);
                view.metadataInspectorLeft.showStructure(true);

                if (view.inspectorWrapper.getWidgetCount() > 1)
                    view.inspectorWrapper.remove(1);
            } catch (Exception ex) {
                GWT.log("doSingleMode error: " + ex.toString());
            }
        }
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
           if (treeItemSelector.compareTo((MetadataObjectWrapper)currentSelection.getUserObject(), target) ) {
               treeItemSelector.removeSelectedStyleFromCurrentSelection();
               treeItemSelector.applySelectedStyle(currentSelection);
               return null;
           }
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
                    removeSelectedStyleFromCurrentSelection();
                    applySelectedStyle(treeItem);
                    return treeItem;
                }
            }
            return null;
        }

        void removeSelectedStyleFromCurrentSelection() {
            if (currentSelection!=null) {
                currentSelection.getWidget().removeStyleName("insetBorder");
            }
        }

        void applySelectedStyle(TreeItem treeItem) {
            treeItem.getWidget().addStyleName("insetBorder");

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

                if (target instanceof DocumentEntry) {
                    return (new DocumentEntryDiff().compare(source,target).isEmpty());
                }

                return source.home != null ? source.home.equals(target.home) : target.home == null || "".equals(target.home);
            }
            return false;


        }

    }

    public void doFilterOptionToggle(HTML ctl, FlowPanel panel) {
        boolean isLinkDisabled =  ctl.getStyleName().contains("inlineLinkDisabled");
        if (! isLinkDisabled) {
           boolean isFilterViewExpanded  = panel.isVisible();

           if (isFilterViewExpanded) {
               ctl.removeStyleName("insetBorder");
           } else {
               ctl.addStyleName("insetBorder");

               // Auto select DocEntries
               ButtonListSelector filterSelector = view.filterObjectSelector;
               if (filterSelector.getItems().contains(new AnnotatedItem(true, MetadataObjectType.DocEntries.name()))) {
                   String currentSelection = filterSelector.getCurrentSelection();
                   if (currentSelection == null || (currentSelection != null && ! MetadataObjectType.DocEntries.name().equals(currentSelection))) {
                       filterSelector.updateSiteSelectedView(MetadataObjectType.DocEntries.name());
                   }
               } else {
                   new PopupMessage("DocEntries not available to select from this list: " + filterSelector.getItems().toString());
               }
           }
           panel.setVisible(! isFilterViewExpanded);
        }
    }

    public void doAdvancedOptionToggle(HTML ctl, FlowPanel panel) {
        boolean isPanelVisible = panel.isVisible();
        if (isPanelVisible) {
            ctl.removeStyleName("insetBorder");
            doSingleMode();
        } else {
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

    public void setMetadataCollection(MetadataCollection metadataCollection) {
        this.metadataCollection = metadataCollection;
    }
}
