package gov.nist.toolkit.xdstools2.client.inspector.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.registrymetadata.client.Association;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.Folder;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ResourceItem;
import gov.nist.toolkit.registrymetadata.client.SubmissionSet;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataObjectType;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.FilterFeature;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryFilterDisplay;
import gov.nist.toolkit.xdstools2.client.widgets.ButtonListSelector;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InspectorView extends AbstractView<InspectorPresenter> implements ProvidesResize, RequiresResize {
    FlowPanel mainHeaderPanel = new FlowPanel();

    final HTML contentFilterCtl = new HTML("Content Filter");
    final FlowPanel contentFilterPanel = new FlowPanel();
    final HTML advancedOptionCtl = new HTML("Advanced Options");
    final FlowPanel advancedOptionPanel = new FlowPanel();

    private Map<MetadataObjectType, FilterFeature> filterFeatureMap = new HashMap<>();
    private Map<MetadataObjectType, DataTable> tableMap = new HashMap<>();

    ActivityItem activityItem;
    int rowsPerPage = 10;

    HorizontalPanel inspectorWrapper = new HorizontalPanel();
    MetadataInspectorTab metadataInspectorLeft = new MetadataInspectorTab(true);
    MetadataInspectorTab metadataInspectorRight = new MetadataInspectorTab(true);

    ButtonListSelector metadataObjectSelector = new ButtonListSelector("Select Metadata Type") {
        @Override
        public void doSelected(String label) {
            getPresenter().doUpdateChosenMetadataObjectType(label);
        }
    };

    private FilterFeature deFilterFeature = null;

    ButtonListSelector filterObjectSelector = new ButtonListSelector("Select Metadata Type") {
        @Override
        public void doSelected(String label) {
            getPresenter().doUpdateChosenFilterObjectType(label);
        }
    };

    private AssocDataTable assocDataTable = new AssocDataTable(rowsPerPage) {
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

    private FoldersDataTable foldersDataTable = new FoldersDataTable(rowsPerPage) {
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

    private SubmissionSetDataTable submissionSetDataTable = new SubmissionSetDataTable(rowsPerPage) {
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

    private DocEntryDataTable docEntryDataTable = new DocEntryDataTable(rowsPerPage) {
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

    private ObjectRefDataTable objectRefTable = new ObjectRefDataTable(rowsPerPage) {
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

    private ResourceDataTable resourceDataTable = new ResourceDataTable(rowsPerPage) {
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
        // If the inspector is starting out in Content mode, then this can only happen from the registry browser.
        // Otherwise, the normal mode is History and the Filter should be disabled.
//        if (! MetadataInspectorTab.SelectedViewMode.CONTENT.equals(metadataInspectorLeft.getViewMode())) {
//            contentFilterCtl.addStyleName("inlineLinkDisabled");
//        }
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

        // The filter panel
        FlowPanel contentFilterWrapper = new FlowPanel();
        filterObjectSelector.displayShowAll(false);
        contentFilterWrapper.add(filterObjectSelector.asWidget());
        contentFilterWrapper.add(new HTML("<br/>"));
        contentFilterWrapper.add(deFilterFeature.asWidget());
        contentFilterPanel.add(contentFilterWrapper);
        contentFilterPanel.addStyleName("paddedHorizontalPanel");

        // The advanced option panel
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
        filterFeatureMap.put(MetadataObjectType.DocEntries, deFilterFeature);

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
        contentFilterCtl.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                getPresenter().doFilterOptionToggle(contentFilterCtl, contentFilterPanel);
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

    public Map<MetadataObjectType, FilterFeature> getFilterFeatureMap() {
        return filterFeatureMap;
    }

    void showFilterCtl(boolean isEnabled) {
        if (isEnabled) {
            contentFilterCtl.removeStyleName("inlineLinkDisabled");
            contentFilterCtl.addStyleName("inlineLink");
        } else {
            contentFilterCtl.removeStyleName("inlineLink");
            contentFilterCtl.addStyleName("inlineLinkDisabled");
        }
    }

    public void setDeFilterFeature(FilterFeature deFilterFeature) {
        this.deFilterFeature = deFilterFeature;
    }
}
