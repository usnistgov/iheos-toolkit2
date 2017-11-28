package gov.nist.toolkit.xdstools2.client.inspector.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;
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

    @Override
    public void init() {
        GWT.log("Init InspectorPresenter");

        setData();

        // The provided EventBus doesn't seem to work ?

        /*
        Scheduler is required because view is built before it is actually displayed.
        https://www.mail-archive.com/google-web-toolkit@googlegroups.com/msg75253.html
         */

        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                if (tableExists()) {
                    view.objectRefTable.resizeTable();
                    return false;
                } else return true;
            }

            boolean tableExists() {
                try {
                   int height = view.objectRefTable.dataTable.getRowElement(0).getClientHeight();
//                   GWT.log("table row height is: " + height);
                   return true;
                } catch (Exception ex) {
                    return false;
                }
            }

        },200);


    }

    private void setData() {
        GWT.log("In setData");

        GWT.log("result list size is: " + results.size());
        // At this point there should be only one Result.TODO: Need to revisit this if there are more!? -- Conformance tests!
        view.metadataInspector.setResults(results);
        view.metadataInspector.setSiteSpec(siteSpec);
        view.metadataInspector.preInit();
        metadataCollection = view.metadataInspector.init();
        view.metadataObjectSelector.setNames(getMetadataObjectAnnotatedItems(metadataCollection));
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

    /**
     *
     * @param root
     * @param target
     * @return True if object was located/found in the tree, selected
     */
    public boolean doFocusTreeItem(final TreeItem root, final ObjectRef target) {
       return new TreeItemSelector<ObjectRef>().doFocusTreeItem(root, target);
    }

    class TreeItemSelector<T> {
        public boolean doFocusTreeItem(final TreeItem root, final T target) {
            if (root == null) {
                List<Tree> treeList = view.metadataInspector.getTreeList();

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

        boolean attemptSelect(TreeItem treeItem, Object userObject, T target) {
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

        public boolean compareTo(MetadataObject source, T target) {
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

}
