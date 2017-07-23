package gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.TreeViewModel;
import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;
import gov.nist.toolkit.xdstools2.client.abstracts.MessagePanel;
import gov.nist.toolkit.xdstools2.client.widgets.DatasetTreeModel;

import java.util.Map;

/**
 *
 */
public class SubmitResourceView extends AbstractView<SubmitResourcePresenter> {
    private MessagePanel messagePanel = new MessagePanel();

    private DatasetTreeModel datasetTreeModel = new DatasetTreeModel();

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
        FlowPanel tabTopPanel = new FlowPanel();

        tabTopPanel.add(messagePanel);
        tabTopPanel.add(new HTML("<h2>Submit Resource</h2>"));

        CellBrowser browser = new CellBrowser(datasetTreeModel, null);
        browser.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);
        browser.setHeight("200");
        browser.setWidth("630");

        tabTopPanel.add(browser);

        return tabTopPanel;
    }

    @Override
    protected void bindUI() {

    }

    DatasetTreeModel getDatasetTreeModel() {
        return datasetTreeModel;
    }

    void setData(Map<String, DatasetModel> content) {
        datasetTreeModel.init(content);
    }

}
