package gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;
import gov.nist.toolkit.xdstools2.client.abstracts.MessagePanel;
import gov.nist.toolkit.xdstools2.client.widgets.DatasetTreeModel;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SubmitResourceView extends AbstractView<SubmitResourcePresenter> {
    private MessagePanel messagePanel = new MessagePanel();
    private VerticalPanel tabTopPanel = new VerticalPanel();
    private HTML selected = new HTML();
    private FlexTable siteTable = new FlexTable();
    private SimplePanel datasetsPanel = new SimplePanel();
    private Button runButton = new Button("Send Resource (CREATE)");
    private Button viewResourceButton = new Button("View Resource");

    private HTML datasetLabel = new HTML("Data Set");
    private HTML resourceTypeLabel = new HTML("Resource Type");
    private HTML resourceLabel = new HTML("Resource");


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

        DecoratorPanel siteTableDecoration = new DecoratorPanel();
        FlowPanel siteTablePanel = new FlowPanel();
        siteTableDecoration.add(siteTablePanel);
        HTML siteTableTitle = new HTML("<br /><b>To System:</b><br /><hr />");
        siteTablePanel.add(siteTableTitle);
        siteTablePanel.add(siteTable);
        tabTopPanel.add(siteTableDecoration);

//        tabTopPanel.add(new HTML("<br />"));

        DecoratorPanel datasetDecoration = new DecoratorPanel();
        VerticalPanel datasetWrapper = new VerticalPanel();
        HTML datasetTitle = new HTML("<br /><b>Select Resource:</b><br /><hr />");
        datasetWrapper.add(datasetTitle);

        HorizontalFlowPanel datasetCaption = new HorizontalFlowPanel();

        String datasetSectionWidth = "210px";
        datasetLabel.setWidth(datasetSectionWidth);
        resourceTypeLabel.setWidth(datasetSectionWidth);
        resourceLabel.setWidth(datasetSectionWidth);

        datasetCaption.add(datasetLabel);
        datasetCaption.add(resourceTypeLabel);
        datasetCaption.add(resourceLabel);

        datasetWrapper.add(datasetCaption);

        datasetWrapper.add(new HTML("<hr />"));

        datasetWrapper.add(datasetsPanel);
        datasetDecoration.add(datasetWrapper);
        tabTopPanel.add(datasetDecoration);

        tabTopPanel.add(new HTML("<br />"));

        HorizontalFlowPanel buttonPanel = new HorizontalFlowPanel();
        runButton.setEnabled(false);
        viewResourceButton.setEnabled(false);
        buttonPanel.add(runButton);
        buttonPanel.add(viewResourceButton);
        tabTopPanel.add(buttonPanel);

        //siteTableDecoration.setWidth("100%");
        return tabTopPanel;
    }

    @Override
    protected void bindUI() {

        runButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                getPresenter().doRun();
            }
        });
    }

    void lateBindUI() {

    }

    void setData(List<DatasetModel> content) {

        DatasetTreeModel datasetTreeModel = new DatasetTreeModel() {

            @Override
            public void doSelect(DatasetElement datasetElement) {
                getPresenter().doResourceSelected(datasetElement);
            }
        };

        datasetTreeModel.init(content);

        CellBrowser browser = new CellBrowser(datasetTreeModel, null);
        browser.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);
        browser.setHeight("200px");
        browser.setWidth("640px");

        VerticalPanel panel = new VerticalPanel();
        panel.add(browser);

        panel.add(selected);

        datasetsPanel.add(panel);

    }

    private List<Button> siteButtons = new ArrayList<>();

    void setSiteNames(List<ASite> sites) {
        int width = 3;
        int row = 0;
        int col = 0;
        int length = 0;
        for (ASite site : sites) {
            Button b = new Button(site.getName());
            b.setText(site.getName());
            b.setEnabled(site.isEnabled());
            siteButtons.add(b);
            siteTable.setWidget(row, col, b);
            col++;
            if (col%width == 0) {
                row++;
                col = 0;
            }
        }
        bindSites();
    }

    /**
     * I tried using the defined style SiteButtonSelected defined in css
     * but it is ignored so removing the default style gives a dark
     * grey to the background (on MAC) which is good enough for now.  Needs
     * to be tested on Windows and Linux.
     */
    private void bindSites() {
        for (Button b : siteButtons) {
            b.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    Object o = clickEvent.getSource();
                    if (o instanceof Button) {
                        updateSiteSelectedView((Button) o);
                    }
                }
            });
        }
    }

    private void updateSiteSelectedView(Button button) {
        for (Button u : siteButtons) {
            u.setStyleName("gwt-Button");
        }
        button.setStyleName("siteSelected");
        getPresenter().doSiteSelected(button.getText());
    }

    MessagePanel getMessagePanel() { return messagePanel; }

    void setRunEnabled(boolean enabled) {
        runButton.setEnabled(enabled);
        viewResourceButton.setEnabled((enabled));
    }

}
