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
import gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab.IContentHolder;
import gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab.TabbedContentPanel;
import gov.nist.toolkit.xdstools2.client.util.ASite;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.client.widgets.SystemSelector;
import gov.nist.toolkit.xdstools2.client.widgets.datasetTreeModel.DatasetTreeModel;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class SubmitResourceView extends AbstractView<SubmitResourcePresenter> implements IContentHolder{
    private MessagePanel messagePanel = new MessagePanel();
    private VerticalPanel tabTopPanel = new VerticalPanel();
    private HTML selected = new HTML();
//    private FlexTable siteTable = new FlexTable();
    private SimplePanel datasetsPanel = new SimplePanel();
    private Button runButton = new Button("Send Resource (CREATE)");
    private FlowPanel thePanel = new FlowPanel();
    private FlowPanel siteTablePanel = new FlowPanel();
    private FlowPanel logPanel = new FlowPanel();
    private FlowPanel inspectorPanel = new FlowPanel();
    private FlowPanel selectionDisplayPanel = new FlowPanel();
//    private FlowPanel requestDisplayPanel = new FlowPanel();
//    private FlowPanel responseDisplayPanel = new FlowPanel();
    private FlowPanel simLogPanel = new FlowPanel();

    private HTML datasetLabel = new HTML("Data Set");
    private HTML resourceTypeLabel = new HTML("Resource Type");
    private HTML resourceLabel = new HTML("Resource");
    private FlowPanel contentPanel = new FlowPanel();
    private TabbedContentPanel bottomPanel;


    private SystemSelector systemSelector = new SystemSelector("To System") {
        @Override
        public void doSelected(String label) {
            getPresenter().doSiteSelected(label);
        }
    };


    public SubmitResourceView() {
        super();
        GWT.log("BuildSubmitResourceView");
    }

    public SystemSelector getSystemSelector() {
        return systemSelector;
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

        thePanel.add(systemSelector.asWidget());

        thePanel.setWidth("100%");
        tabTopPanel.add(thePanel);

        VerticalPanel datasetWrapper = new VerticalPanel();
        datasetWrapper.setWidth("100%");
        thePanel.add(new HTML("<br />"));
        HTML datasetTitle = new HTML("<b>Send Resource</b>");
        datasetTitle.addStyleName("tool-section-header");
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

        tabTopPanel.add(contentPanel);

        HTML buttonPanelTitle = new HTML("Actions");
        buttonPanelTitle.addStyleName("tool-section-header");
        buttonPanelTitle.setWidth("100%");
        thePanel.add(new HTML("<br />"));
        thePanel.add(buttonPanelTitle);

        HorizontalFlowPanel buttonPanel = new HorizontalFlowPanel();
        runButton.setEnabled(false);
        buttonPanel.add(runButton);
        thePanel.add(buttonPanel);

        thePanel.add(new HTML("<br />"));
        bottomPanel = new TabbedContentPanel("Logs", "400px");
        thePanel.add(bottomPanel.asWidget());

        bottomPanel.addBaseTab(logPanel, "[Log]");
        bottomPanel.addBaseTab(selectionDisplayPanel, "[Selection]");

        return inScrollPanel(tabTopPanel);
    }

    @Override
    protected void bindUI() {

        inspectorPanel.add(new HTML("Coming Soon."));
        simLogPanel.add(new HTML("Coming Soon."));

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

    void setSiteNames(List<ASite> sites) {
        systemSelector.setNames(sites);
    }

    MessagePanel getMessagePanel() { return messagePanel; }

    void setRunEnabled(boolean enabled) {
        runButton.setEnabled(enabled);
    }

    void addLog(String msg) {
        addLog(new HTML(msg));
    }

    void addLog(Widget msg) {
        logPanel.add(msg);
    }

    void clearLog() { logPanel.clear(); }

    void setContent(Widget content) {
        selectionDisplayPanel.clear();
        selectionDisplayPanel.add(content);
    }

    void clearContent() {
        contentPanel.setVisible(false);
    }

    private ScrollPanel inScrollPanel(Widget w) {
        w.setWidth("100%");
        w.setHeight("100%");
        ScrollPanel sp = new ScrollPanel();
        sp.add(w);
        return sp;
    }

    @Override
    public void addContent(Widget w, String title) {
        bottomPanel.addTab(w, title);
    }

    @Override
    public void clearLogContent() {
        bottomPanel.clearContent();
    }
}
