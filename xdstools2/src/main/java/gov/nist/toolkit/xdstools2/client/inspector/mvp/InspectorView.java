package gov.nist.toolkit.xdstools2.client.inspector.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.widgets.ButtonListSelector;

import java.util.List;
import java.util.Map;

public class InspectorView extends AbstractView<InspectorPresenter> implements ProvidesResize, RequiresResize {
    HeaderPanel containerPanel = new HeaderPanel();

    ActivityItem activityItem;

    /* TODO: create a new widget to select Object Types (using the same style as the SystemSelector).
     Clicking on it should display the data table.
     */

    ButtonListSelector metadataObjectSelector = new ButtonListSelector("Metadata Object","Metadata Object") {
        @Override
        public void doSelected(String label) {
            getPresenter().doUpdateChosenMetadataObjectType(label);
        }
    };

    ObjectRefDataTable objectRefTable = new ObjectRefDataTable(10) {
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
           getPresenter().doFocusTreeItem(null,row);
        }

        @Override
        void diffAction(ObjectRef left, ObjectRef right) {

        }

        @Override
        int getWidthInPx() {
            int width;
            try {
                width = (int)(containerPanel.getParent().getElement().getClientWidth() * .80);
            } catch (Exception ex) {
                GWT.log("containerPanel error: " + ex.toString());
                width = (int)(Window.getClientWidth() * .80);
            }
            return width;
        }
    };

    MetadataInspectorTab metadataInspector = new MetadataInspectorTab(true);

    @Override
    public void onResize() {
        objectRefTable.resizeTable();
    }

    @Override
    protected Widget buildUI() {

//        HeaderPanel mainPanel = new HeaderPanel();

        FlowPanel topNavPanel = new FlowPanel();
        int resultPanelHeight = 0;

//        WorkflowDiagram activityDiagram = new WorkflowDiagram(activityItem);
        // TODO: how to tie the NamedBox ClickHandler to the show-data table?
//        resultPanel.add(activityDiagram);

        topNavPanel.add(metadataObjectSelector.asWidget());
        topNavPanel.add(new HTML("<br/>"));
        topNavPanel.add(objectRefTable.asWidget());
//        resultPanelHeight = activityDiagram.getDiagramHeight() + (int)objectRefTable.guessTableHeight();
//        GWT.log("setting North height to: " + resultPanelHeight + ". activityDiagram height is: " + activityDiagram.getDiagramHeight() + ". objectRef height: " + objectRefTable.asWidget().getElement().getStyle().getHeight());

        containerPanel.setHeaderWidget(topNavPanel);
        containerPanel.setContentWidget(new ScrollPanel(metadataInspector.asWidget())); // TODO: put this in a scroll panel.
//        containerPanel.add(new HTML("add"));

        //

//        containerPanel.add(mainPanel);

        //
        return containerPanel;
    }

    @Override
    protected void bindUI() {

    }

    @Override
    protected Map<String, Widget> getPathToWidgetsMap() {
        return null;
    }


    public void setActivityItem(ActivityItem activityItem) {
        this.activityItem = activityItem;
    }
}
