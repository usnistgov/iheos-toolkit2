package gov.nist.toolkit.xdstools2.client.inspector.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.registrymetadata.client.*;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataObjectType;
import gov.nist.toolkit.xdstools2.client.widgets.ButtonListSelector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InspectorView extends AbstractView<InspectorPresenter> implements ProvidesResize, RequiresResize {
    FlowPanel mainHeaderPanel = new FlowPanel();

    final HTML contentFilterCtl = new HTML("Content Filter");
    final FlowPanel contentFilterPanel = new FlowPanel();
    final HTML advancedOptionCtl = new HTML("Advanced Options");
    final FlowPanel advancedOptionPanel = new FlowPanel();

    Map<MetadataObjectType, DataTable> tableMap = new HashMap<>();

    ActivityItem activityItem;
    int rowsPerPage = 10;

    HorizontalPanel inspectorWrapper = new HorizontalPanel();
    MetadataInspectorTab metadataInspectorLeft = new MetadataInspectorTab(true);
    MetadataInspectorTab metadataInspectorRight = new MetadataInspectorTab(true);

    ButtonListSelector metadataObjectSelector = new ButtonListSelector("Select a Metadata Object") {
        @Override
        public void doSelected(String label) {
            getPresenter().doUpdateChosenMetadataObjectType(label);
        }
    };

    AssocDataTable assocDataTable = new AssocDataTable(rowsPerPage) {
        @Override
        void defaultSingleClickAction(Association row) {
            getPresenter().doSingleMode();
            TreeItem treeItem = getPresenter().doFocusTreeItem(MetadataObjectType.valueOf(metadataObjectSelector.getCurrentSelection()), metadataInspectorLeft.getTreeList(), null,row, metadataInspectorLeft.getCurrentSelectedTreeItem());
            if (treeItem!=null) {
                metadataInspectorLeft.setCurrentSelectedTreeItem(treeItem);
            }
        }

        @Override
        void defaultDoubleClickAction(Association row) {

        }

        @Override
        void setupDiffMode(boolean isSelected) {
            getPresenter().doSetupDiffMode(isSelected);
        }

        @Override
        void diffAction(Association left, Association right) {
            getPresenter().doDiffAction(MetadataObjectType.valueOf(metadataObjectSelector.getCurrentSelection()), left, right);
        }

        @Override
        int getWidthInPx() {
            return getParentContainerWidth();
        }
    };

    FoldersDataTable foldersDataTable = new FoldersDataTable(rowsPerPage) {
        @Override
        void defaultSingleClickAction(Folder row) {
            getPresenter().doSingleMode();
            TreeItem treeItem = getPresenter().doFocusTreeItem(MetadataObjectType.valueOf(metadataObjectSelector.getCurrentSelection()), metadataInspectorLeft.getTreeList(), null,row, metadataInspectorLeft.getCurrentSelectedTreeItem());
            if (treeItem!=null) {
                metadataInspectorLeft.setCurrentSelectedTreeItem(treeItem);
            }
        }

        @Override
        void defaultDoubleClickAction(Folder row) {

        }

        @Override
        void setupDiffMode(boolean isSelected) {
            getPresenter().doSetupDiffMode(isSelected);
        }

        @Override
        void diffAction(Folder left, Folder right) {
            getPresenter().doDiffAction(MetadataObjectType.valueOf(metadataObjectSelector.getCurrentSelection()), left, right);
        }

        @Override
        int getWidthInPx() {
            return getParentContainerWidth();
        }
    };

    SubmissionSetDataTable submissionSetDataTable = new SubmissionSetDataTable(rowsPerPage) {
        @Override
        void defaultSingleClickAction(SubmissionSet row) {
            getPresenter().doSingleMode();
            TreeItem treeItem = getPresenter().doFocusTreeItem(MetadataObjectType.valueOf(metadataObjectSelector.getCurrentSelection()), metadataInspectorLeft.getTreeList(), null,row, metadataInspectorLeft.getCurrentSelectedTreeItem());
            if (treeItem!=null) {
                metadataInspectorLeft.setCurrentSelectedTreeItem(treeItem);
            }
        }

        @Override
        void defaultDoubleClickAction(SubmissionSet row) {
        }

        @Override
        void setupDiffMode(boolean isSelected) {
            getPresenter().doSetupDiffMode(isSelected);
        }

        @Override
        void diffAction(SubmissionSet left, SubmissionSet right) {
            getPresenter().doDiffAction(MetadataObjectType.valueOf(metadataObjectSelector.getCurrentSelection()), left, right);
        }

        @Override
        int getWidthInPx() {
            return getParentContainerWidth();
        }
    };

    DocEntryDataTable docEntryDataTable = new DocEntryDataTable(rowsPerPage) {
        @Override
        void defaultSingleClickAction(DocumentEntry row) {
            getPresenter().doSingleMode();
            TreeItem treeItem = getPresenter().doFocusTreeItem(MetadataObjectType.valueOf(metadataObjectSelector.getCurrentSelection()), metadataInspectorLeft.getTreeList(), null,row, metadataInspectorLeft.getCurrentSelectedTreeItem());
            if (treeItem!=null) {
                metadataInspectorLeft.setCurrentSelectedTreeItem(treeItem);
            }
        }

        @Override
        void defaultDoubleClickAction(DocumentEntry row) {

        }

        @Override
        void setupDiffMode(boolean isSelected) {
            getPresenter().doSetupDiffMode(isSelected);
        }

        @Override
        void diffAction(DocumentEntry left, DocumentEntry right) {
            getPresenter().doDiffAction(MetadataObjectType.valueOf(metadataObjectSelector.getCurrentSelection()), left, right);
        }

        @Override
        int getWidthInPx() {
            return getParentContainerWidth();
        }
    };

    ObjectRefDataTable objectRefTable = new ObjectRefDataTable(rowsPerPage) {
        @Override
        void doGetDocuments(List<ObjectRef> objectRefs) { // TODO: remove this
//            getPresenter().do
        }

        @Override
        void defaultDoubleClickAction(ObjectRef row) {
//            getPresenter().do
        }

        @Override
        void defaultSingleClickAction(ObjectRef row) {
            getPresenter().doSingleMode();
           TreeItem treeItem = getPresenter().doFocusTreeItem(MetadataObjectType.valueOf(metadataObjectSelector.getCurrentSelection()), metadataInspectorLeft.getTreeList(), null,row, metadataInspectorLeft.getCurrentSelectedTreeItem());
            if (treeItem!=null) {
                metadataInspectorLeft.setCurrentSelectedTreeItem(treeItem);
            }
        }

        @Override
        void setupDiffMode(boolean isSelected) {
           getPresenter().doSetupDiffMode(isSelected);
        }

        @Override
        void diffAction(ObjectRef left, ObjectRef right) {
            getPresenter().doDiffAction(MetadataObjectType.valueOf(metadataObjectSelector.getCurrentSelection()), left, right);
        }

        @Override
        int getWidthInPx() {
            return getParentContainerWidth();
        }
    };

    ResourceDataTable resourceDataTable = new ResourceDataTable(rowsPerPage) {
        @Override
        void doGetDocuments(List<ResourceItem> objectRefs) { // TODO: remove this
//            getPresenter().do
        }

        @Override
        void defaultDoubleClickAction(ResourceItem row) {
//            getPresenter().do
        }

        @Override
        void defaultSingleClickAction(ResourceItem row) {
            getPresenter().doSingleMode();
            TreeItem treeItem = getPresenter().doFocusTreeItem(MetadataObjectType.valueOf(metadataObjectSelector.getCurrentSelection()), metadataInspectorLeft.getTreeList(), null,row, metadataInspectorLeft.getCurrentSelectedTreeItem());
            if (treeItem!=null) {
                metadataInspectorLeft.setCurrentSelectedTreeItem(treeItem);
            }
        }

        @Override
        void setupDiffMode(boolean isSelected) {
            getPresenter().doSetupDiffMode(isSelected);
        }

        @Override
        void diffAction(ResourceItem left, ResourceItem right) {
            getPresenter().doDiffAction(MetadataObjectType.valueOf(metadataObjectSelector.getCurrentSelection()), left, right);
        }

        @Override
        int getWidthInPx() {
            return getParentContainerWidth();
        }
    };

    private int getParentContainerWidth() {
        int width;
        try {
            width = (int)(mainHeaderPanel.getParent().getElement().getClientWidth() * .80);
        } catch (Exception ex) {
            GWT.log("containerPanel error: " + ex.toString());
            width = (int)(Window.getClientWidth() * .80);
        }
        return width;
    }



    @Override
    public void onResize() {
        objectRefTable.resizeTable();
        docEntryDataTable.resizeTable();
    }

    @Override
    protected Widget buildUI() {

//        HeaderPanel mainPanel = new HeaderPanel();

        FlowPanel topNavPanel = new FlowPanel();
        int resultPanelHeight = 0;

//        WorkflowDiagram activityDiagram = new WorkflowDiagram(activityItem);
//        resultPanel.add(activityDiagram);

        final HTML title = new HTML();
        title.setHTML("<h2>Inspector</h2>");
        topNavPanel.add(title);

        contentFilterCtl.setTitle("This feature is only available in the Inspector Contents View Mode.");
        contentFilterCtl.addStyleName("iconStyle");
        contentFilterCtl.addStyleName("inlineLinkDisabled");
        contentFilterCtl.addStyleName("roundedButton1");
        contentFilterPanel.addStyleName("with-border");
        contentFilterPanel.setVisible(false);

        advancedOptionCtl.addStyleName("iconStyle");
        advancedOptionCtl.addStyleName("inlineLink");
        advancedOptionCtl.addStyleName("roundedButton1");
        advancedOptionPanel.addStyleName("with-border");
        advancedOptionPanel.setVisible(false);

        topNavPanel.add(contentFilterCtl);
        topNavPanel.add(contentFilterPanel);
        topNavPanel.add(advancedOptionCtl);
        topNavPanel.add(advancedOptionPanel);

        FlowPanel contentFilter
                // skb TODO: pickup here

        FlowPanel advancedOptionWrapper = new FlowPanel();
        metadataObjectSelector.displayShowAll(false);
        advancedOptionWrapper.add(metadataObjectSelector.asWidget());
        advancedOptionWrapper.add(new HTML("<br/>"));

        advancedOptionWrapper.add(objectRefTable.asWidget());
        advancedOptionWrapper.add(docEntryDataTable.asWidget());
        advancedOptionWrapper.add(submissionSetDataTable.asWidget());
        advancedOptionWrapper.add(foldersDataTable.asWidget());
        advancedOptionWrapper.add(assocDataTable.asWidget());
        objectRefTable.asWidget().setVisible(false);
        docEntryDataTable.asWidget().setVisible(false);
        submissionSetDataTable.asWidget().setVisible(false);
        foldersDataTable.asWidget().setVisible(false);
        assocDataTable.asWidget().setVisible(false);
        advancedOptionPanel.add(advancedOptionWrapper);
        advancedOptionPanel.addStyleName("paddedHorizontalPanel");

        mainHeaderPanel.add(topNavPanel);

        inspectorWrapper.add(metadataInspectorLeft.asWidget());

//        inspectorWrapper.add(metadataInspectorRight.asWidget());
//        getPresenter().doSetInspectorVisibility(metadataInspectorRight,false);

        mainHeaderPanel.add(inspectorWrapper);
        return new ScrollPanel(mainHeaderPanel);
    }

    @Override
    protected void bindUI() {

        tableMap.put(MetadataObjectType.ObjectRefs, objectRefTable);
        tableMap.put(MetadataObjectType.DocEntries, docEntryDataTable);
        tableMap.put(MetadataObjectType.SubmissionSets, submissionSetDataTable);
        tableMap.put(MetadataObjectType.Folders, foldersDataTable);
        tableMap.put(MetadataObjectType.Assocs, assocDataTable);
        tableMap.put(MetadataObjectType.Resources, resourceDataTable);

        advancedOptionCtl.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                getPresenter().doAdvancedOptionToggle(advancedOptionCtl, advancedOptionPanel);
            }
        });
    }

    @Override
    protected Map<String, Widget> getPathToWidgetsMap() {
        return null;
    }


    public void setActivityItem(ActivityItem activityItem) {
        this.activityItem = activityItem;
    }

    public HorizontalPanel getInspectorWrapper() {
        return inspectorWrapper;
    }

    public Map<MetadataObjectType,DataTable> getTableMap() {
        return tableMap;
    }

}
