package gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.TreeViewModel;
import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;
import gov.nist.toolkit.xdstools2.client.abstracts.MessagePanel;
import gov.nist.toolkit.xdstools2.client.widgets.DatasetTreeModel;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class SubmitResourceView extends AbstractView<SubmitResourcePresenter> {
    private MessagePanel messagePanel = new MessagePanel();
    private VerticalPanel tabTopPanel = new VerticalPanel();
    private String selectedResourcePath = "None";
    private HTML selected = new HTML();

    private CellBrowser browser;

    public SubmitResourceView() {
        super();
        GWT.log("BuildSubmitResourceView");
    }

    @Override
    protected Map<String, Widget> getPathToWidgetsMap() {
        return null;
    }

    @Override
    protected Widget buildUI() {
        tabTopPanel.add(messagePanel);
        tabTopPanel.add(new HTML("<h2>Submit Resource</h2>"));

        return tabTopPanel;
    }

    @Override
    protected void bindUI() {

    }

    private void lateBindUI() {

    }

    void setData(List<DatasetModel> content) {
        DatasetTreeModel datasetTreeModel = new DatasetTreeModel() {

            @Override
            protected void doSelect(String path) {
                selectedResourcePath = path;
                showSelection();
            }
        };

        datasetTreeModel.init(content);

        browser = new CellBrowser(datasetTreeModel, null);
        browser.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);
        browser.setHeight("200px");
        browser.setWidth("630px");

        VerticalPanel panel = new VerticalPanel();
        panel.setBorderWidth(1);
        panel.add(browser);

        showSelection();
        panel.add(selected);

        tabTopPanel.add(panel);

        lateBindUI();
    }

    private void showSelection() {
        selected.setHTML("<br /><b>Selected resource: <b /> " + selectedResourcePath + "<br /><br />");
    }

}
