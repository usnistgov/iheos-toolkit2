package gov.nist.toolkit.xdstools2.client.inspector.mvp;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;

import java.util.List;
import java.util.Map;

public class InspectorView extends AbstractView<InspectorPresenter> {
    FlowPanel containerPanel = new FlowPanel();

    private ObjectRefDataTable objectRefTable = new ObjectRefDataTable(10) {
        @Override
        void doGetDocuments(List<ObjectRef> objectRefs) {
//            getPresenter().do
        }

        @Override
        void defaultDoubleClickAction(ObjectRef row) {
//            getPresenter().do
        }

        @Override
        int getWidthInPx() {
            int width;
            try {
                width = (int)(containerPanel.getParent().getElement().getClientWidth() * .80);
            } catch (Exception ex) {
                width = (int)(Window.getClientWidth() * .80);
            }
            return width;
        }
    };

    @Override
    protected Widget buildUI() {

        containerPanel.add(new HTML("Step"));
        containerPanel.add(new HTML("# ObjectRefs "));
//        containerPanel.add(objectRefTable.asWidget());

        containerPanel.add(new HTML("Load Logs"));

//        objectRefTable.setData(null);

        return containerPanel;
    }

    @Override
    protected void bindUI() {

    }

    @Override
    protected Map<String, Widget> getPathToWidgetsMap() {
        return null;
    }
}
