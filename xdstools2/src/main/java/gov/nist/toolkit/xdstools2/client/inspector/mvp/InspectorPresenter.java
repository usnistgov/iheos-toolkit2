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
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.util.AnnotatedItem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class InspectorPresenter extends AbstractPresenter<InspectorView> {
    public enum MetadataObjectType {ObjectRefs, DocEntries, SubmissionSets, Folders, Assocs}
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

    public void setupResizeTableTimer() {

        /*
        Scheduler is required because view is built before it is actually displayed.
        https://www.mail-archive.com/google-web-toolkit@googlegroups.com/msg75253.html
         */
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                if (isTableCellHeightAvailable()) {
                    view.objectRefTable.resizeTable();
                    return false;
                } else return true;
            }

            boolean isTableCellHeightAvailable() {
                try {
                    int height = view.objectRefTable.dataTable.getRowElement(0).getClientHeight();
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
        setupInspectorWidget(view.metadataInspectorLeft);
        metadataCollection = setupInspectorWidget(view.metadataInspectorRight);
        annotatedItems = getMetadataObjectAnnotatedItems(metadataCollection);
        view.metadataObjectSelector.setNames(annotatedItems);
    }

    public void autoSelectIfOnlyOneTypeOfObjectIsAvail() {
        int cx = 0;
        AnnotatedItem defaultItem = null;
        for (AnnotatedItem annotatedItem : annotatedItems) {
           if (annotatedItem.isEnabled()) {
               if (defaultItem==null)
                   defaultItem = annotatedItem;
               cx++;
           }
        }
        if (cx==1) {
            view.metadataObjectSelector.updateSiteSelectedView(defaultItem.getName());
        }
    }

    public void doSetupDiffMode(boolean isSelected) {
        view.metadataInspectorLeft.showHistory(true);
        view.metadataInspectorRight.showHistory(true);
        if (isSelected) {
        }
        if (!isSelected) {
            view.metadataInspectorLeft.asWidget().setVisible(false);
        }
    }

    private MetadataCollection setupInspectorWidget(MetadataInspectorTab inspector) {
        inspector.setResults(results);
        inspector.setSiteSpec(siteSpec);
        inspector.preInit();
        return inspector.init();
    }

    public void doDiffAction(MetadataObject left, MetadataObject right) {

        view.inspectorWrapper.add(view.metadataInspectorRight.asWidget());
       view.metadataInspectorLeft.showHistory(false);
       view.metadataInspectorRight.showHistory(false);

       doFocusTreeItem(view.metadataInspectorLeft.getTreeList(), null, left);
       doFocusTreeItem(view.metadataInspectorRight.getTreeList(), null, right);
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

    public List<ObjectRef> composeTableData() {
        /*
        List<ObjectRef> objectRefs = new ArrayList<>();

            for (Result result : results)  {
               for (StepResult stepResult : result.stepResults)  {
                    if (stepResult.toBeRetrieved!=null && !stepResult.toBeRetrieved.isEmpty())  {
                       objectRefs.addAll(stepResult.toBeRetrieved);
                    }
                    // TODO: There should be only one StepResult for Utilities BUT there are multiple for the Conformance tool
                   // Possibly one solution is to use a tree on the left that shows the existing style of tree menu nav --- but selecting a step will show the new inspector, which is step-focused, in a split layout panel
                    break;
                }
                // TODO:
                break;
            }
        return objectRefs;
        */

        return metadataCollection.objectRefs;
    }



    public void setDataModel(List<Result> results) {
        this.results = results;
    }

    public void setSiteSpec(SiteSpec siteSpec) {
        this.siteSpec = siteSpec;
    }

    void doUpdateChosenMetadataObjectType(String type) {
        MetadataObjectType metadataObjectType = MetadataObjectType.valueOf(type);
        if (MetadataObjectType.ObjectRefs.equals(metadataObjectType)) {
            view.objectRefTable.asWidget().setVisible(true);
            view.objectRefTable.setData(metadataCollection.objectRefs);
        }
    }

    public void doSingleMode() {
        view.metadataInspectorLeft.showHistory(true);

        if (view.inspectorWrapper.getWidgetCount()>1)
            view.inspectorWrapper.remove(1);
    }


    /**
     *
     * @param root
     * @param target
     * @return True if object was located/found in the tree, selected
     */
    public boolean doFocusTreeItem(List<Tree> treeList, final TreeItem root, final MetadataObject target) {
       return new TreeItemSelector<ObjectRef>(treeList).doFocusTreeItem(root, target);
    }

    class TreeItemSelector<T> {
        private List<Tree> treeList;

        public TreeItemSelector(List<Tree> treeList) {
            this.treeList = treeList;
        }

        public boolean doFocusTreeItem(final TreeItem root, final MetadataObject target) {
            if (root == null) {

                for (Tree tree : treeList) {
                    if (doFocusTreeItem(tree.getItem(0), target)) {
                        break;
                    }
                }
            } else {
                int childCt = root.getChildCount();
                if (attemptSelect(root, root.getUserObject(), target)) {
                    root.setState(true);
                    return true;
                }
                if (childCt>0) {
                    for (int cx = 0; cx < childCt; cx++) {
                        TreeItem child = root.getChild(cx);
                        Object userObject = child.getUserObject();
                        if (attemptSelect(child, userObject, target)) {
                            root.setState(true);
                            return true;
                        } else if (child.getChildCount()>0) {
                            return doFocusTreeItem(child, target);
                        }
                    }
                }
            }
            return false;
        }

        boolean attemptSelect(TreeItem treeItem, Object userObject, MetadataObject target) {
            if (userObject != null && target!=null) {
                if (compareTo((MetadataObject)userObject,target)) {
                    ((Hyperlink)treeItem.getWidget()).fireEvent(new ClickEvent() {});
                    treeItem.setSelected(true);
                    treeItem.setState(true, true);
                    return true;
                }
            }
            return false;
        }

        public boolean compareTo(MetadataObject source, MetadataObject target) {
            if (source == target) return true;
            if (!(target instanceof MetadataObject)) return false;

            MetadataObject that = (MetadataObject) target;

            if (source!=null && that!=null) {
                if (source.id== null || that.id==null)
                    return false;
            }


            if (source.id != null ? !source.id.equals(that.id) : that.id != null) {
                return false;
            }

            return source.home != null ? source.home.equals(that.home) : that.home == null;
        }

    }

    public void doAdvancedOptionToggle(HTML ctl, FlowPanel panel) {
        boolean isPanelVisible = panel.isVisible();
        if (isPanelVisible) {
            ctl.removeStyleName("insetBorder");
            ctl.addStyleName("outsetBorder");
        } else {
            ctl.removeStyleName("outsetBorder");
            ctl.addStyleName("insetBorder");
            setupResizeTableTimer();
        }
        panel.setVisible(!isPanelVisible);
    }

}
