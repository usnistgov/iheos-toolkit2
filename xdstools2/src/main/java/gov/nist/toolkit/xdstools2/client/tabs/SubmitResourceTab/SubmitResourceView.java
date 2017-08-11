package gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
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
//    private FlexTable siteTable = new FlexTable();
    private SimplePanel datasetsPanel = new SimplePanel();
    private Button runButton = new Button("Send Resource (CREATE)");
    private Button viewResourceButton = new Button("View selected resource");
    private FlowPanel thePanel = new FlowPanel();
    private FlowPanel siteTablePanel = new FlowPanel();
    private FlowPanel logPanel = new FlowPanel();
    private FlowPanel inspectorPanel = new FlowPanel();

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
        tabTopPanel.setWidth("100%");
        tabTopPanel.add(messagePanel);
        tabTopPanel.add(new HTML("<h2>Submit Resource</h2>"));

        HTML siteTableTitle = new HTML("<b>To System</b>");
        siteTableTitle.setWidth("100%");
        siteTableTitle.addStyleName("my-table-header");
        thePanel.add(siteTableTitle);
        thePanel.add(siteTablePanel);

        thePanel.setWidth("100%");
        tabTopPanel.add(thePanel);

        VerticalPanel datasetWrapper = new VerticalPanel();
        datasetWrapper.setWidth("100%");
        thePanel.add(new HTML("<br />"));
        HTML datasetTitle = new HTML("<b>Send Resource</b>");
        datasetTitle.addStyleName("my-table-header");
        datasetTitle.setWidth("100%");
        thePanel.add(datasetTitle);

        HorizontalFlowPanel datasetCaption = new HorizontalFlowPanel();

        String datasetSectionWidth = "210px";
        datasetLabel.setWidth(datasetSectionWidth);
        resourceTypeLabel.setWidth(datasetSectionWidth);
        resourceLabel.setWidth(datasetSectionWidth);

        datasetCaption.add(datasetLabel);
        datasetCaption.add(resourceTypeLabel);
        datasetCaption.add(resourceLabel);

        datasetWrapper.add(datasetCaption);

        DecoratorPanel datasetWrapper2 = new DecoratorPanel();

        datasetWrapper.add(datasetsPanel);
        datasetWrapper2.add(datasetWrapper);
        thePanel.add(datasetWrapper2);

        HTML buttonPanelTitle = new HTML("Actions");
        buttonPanelTitle.addStyleName("my-table-header");
        buttonPanelTitle.setWidth("100%");
        thePanel.add(new HTML("<br />"));
        thePanel.add(buttonPanelTitle);

        HorizontalFlowPanel buttonPanel = new HorizontalFlowPanel();
        runButton.setEnabled(false);
        viewResourceButton.setEnabled(false);
        buttonPanel.add(runButton);
        buttonPanel.add(viewResourceButton);
        thePanel.add(buttonPanel);

        thePanel.add(new HTML("<br />"));
        HTML logTitle = new HTML("<b>Logs</b>");
        logTitle.addStyleName("my-table-header");
        thePanel.add(logTitle);

        TabLayoutPanel bottomPanel = new TabLayoutPanel(1.5, Style.Unit.EM);
        bottomPanel.setWidth("800px");
        bottomPanel.setHeight("400px");
        thePanel.add(bottomPanel);
        logPanel.setWidth("100%");
        logPanel.setHeight("100%");
//        logPanel.add(new HTML("<p>Hello Internet</p>"));
//        logPanel.add(new Button("Goodby"));
        bottomPanel.add(logPanel, "[Log]");

        bottomPanel.add(inspectorPanel, "[Inspector]");
        inspectorPanel.setWidth("100%");

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
        for (ASite site : sites) {
            Button b = new Button(site.getName());
            b.setText(site.getName());
            b.setEnabled(site.isEnabled());
            siteButtons.add(b);
            siteTablePanel.add(b);
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

    void addLog(String msg) {
        addLog(new HTML(msg));
    }

    void addLog(Widget msg) {
        logPanel.add(msg);
    }

    void clearLog() { logPanel.clear(); }

}
