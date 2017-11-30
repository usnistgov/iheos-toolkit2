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
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataObjectType;
import gov.nist.toolkit.xdstools2.client.widgets.ButtonListSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InspectorView extends AbstractView<InspectorPresenter> implements ProvidesResize, RequiresResize {
    FlowPanel mainHeaderPanel = new FlowPanel();

    final HTML advancedOptionCtl = new HTML("Advanced Options");
    final FlowPanel advancedOptionPanel = new FlowPanel();

    List<DataTable> tables = new ArrayList<>();

    ActivityItem activityItem;
    int rowsPerPage = 10;

    /* TODO: create a new widget to select Object Types (using the same style as the SystemSelector).
     Clicking on it should display the data table.
     */

    ButtonListSelector metadataObjectSelector = new ButtonListSelector("Select a Metadata Object","Metadata Object") {
        @Override
        public void doSelected(String label) {
            getPresenter().doUpdateChosenMetadataObjectType(label);
        }
    };

    DocEntryDataTable docEntryDataTable = new DocEntryDataTable(rowsPerPage) {
        @Override
        void defaultSingleClickAction(DocumentEntry row) {
            getPresenter().doSingleMode();
            getPresenter().doFocusTreeItem(MetadataObjectType.valueOf(metadataObjectSelector.getCurrentSelection()), metadataInspectorLeft.getTreeList(), null,row);
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
        void defaultDoubleClickAction(ObjectRef row) { // TODO: 1. change to just (single) Click. 2. Wire this into inspector to navigate (or focus) object into view
//            getPresenter().do
        }

        @Override
        void defaultSingleClickAction(ObjectRef row) {
            getPresenter().doSingleMode();
           getPresenter().doFocusTreeItem(MetadataObjectType.valueOf(metadataObjectSelector.getCurrentSelection()), metadataInspectorLeft.getTreeList(), null,row);
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

    HorizontalPanel inspectorWrapper = new HorizontalPanel();
    MetadataInspectorTab metadataInspectorLeft = new MetadataInspectorTab(true);
    MetadataInspectorTab metadataInspectorRight = new MetadataInspectorTab(true);

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
        // TODO: how to tie the NamedBox ClickHandler to the show-data table?
//        resultPanel.add(activityDiagram);

        final HTML title = new HTML();
        title.setHTML("<h2>Inspector</h2>");
        topNavPanel.add(title);

        advancedOptionCtl.addStyleName("iconStyle");
        advancedOptionCtl.addStyleName("inlineLink");
        advancedOptionCtl.addStyleName("outsetBorder");
        advancedOptionCtl.addStyleName("roundedButton1");
        advancedOptionPanel.addStyleName("with-border");

        advancedOptionPanel.setVisible(false);
        topNavPanel.add(advancedOptionCtl);
        topNavPanel.add(advancedOptionPanel);

        FlowPanel advancedOptionWrapper = new FlowPanel();
        advancedOptionWrapper.add(metadataObjectSelector.asWidget());
        advancedOptionWrapper.add(new HTML("<br/>"));
        objectRefTable.asWidget().setVisible(false);
        docEntryDataTable.asWidget().setVisible(false);
        advancedOptionWrapper.add(objectRefTable.asWidget());
        advancedOptionWrapper.add(docEntryDataTable.asWidget());
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

        tables.add(objectRefTable);
        tables.add(docEntryDataTable);

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

    public List<DataTable> getTables() {
        return tables;
    }
}
