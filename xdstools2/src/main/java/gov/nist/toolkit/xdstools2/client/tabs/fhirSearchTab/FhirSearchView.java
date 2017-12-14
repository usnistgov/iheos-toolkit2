package gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;
import gov.nist.toolkit.xdstools2.client.abstracts.MessagePanel;
import gov.nist.toolkit.xdstools2.client.util.ASite;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.client.widgets.SystemSelector;
import gov.nist.toolkit.xdstools2.client.widgets.datasetTreeModel.DatasetTreeModel;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class FhirSearchView extends AbstractView<FhirSearchPresenter> {
    private MessagePanel messagePanel = new MessagePanel();
    private VerticalPanel tabTopPanel = new VerticalPanel();
    private HTML selected = new HTML();
//    private FlexTable siteTable = new FlexTable();
    private SimplePanel datasetsPanel = new SimplePanel();
    private Button runButton = new Button("Read Resource");
    TextBox refTextBox = new TextBox();
    private FlowPanel thePanel = new FlowPanel();
    private FlowPanel siteTablePanel = new FlowPanel();
    private FlowPanel logPanel = new FlowPanel();
    private FlowPanel inspectorPanel = new FlowPanel();
    private FlowPanel viewPanel = new FlowPanel();
    private FlowPanel simLogPanel = new FlowPanel();

    private HTML datasetLabel = new HTML("Data Set");
    private HTML resourceTypeLabel = new HTML("Resource Type");
    private HTML resourceLabel = new HTML("Resource");
    private FlowPanel contentPanel = new FlowPanel();

    private SystemSelector systemSelector = new SystemSelector("To System") {
        @Override
        public void doSelected(String label) {
            getPresenter().doSiteSelected(label);
        }
    };


    public FhirSearchView() {
        super();
        GWT.log("BuildFhirSearchView");
    }

    @Override
    protected Map<String, Widget> getPathToWidgetsMap() {
        return null;
    }

    @Override
    protected Widget buildUI() {
        tabTopPanel.setWidth("100%");
        tabTopPanel.add(messagePanel);
        tabTopPanel.add(new HTML("<h2>FHIR Search</h2>"));

        thePanel.add(systemSelector.asWidget());

        thePanel.setWidth("100%");
        tabTopPanel.add(thePanel);

        VerticalPanel datasetWrapper = new VerticalPanel();
        datasetWrapper.setWidth("100%");
        thePanel.add(new HTML("<br />"));

        DecoratorPanel readPanel = new DecoratorPanel();
        readPanel.setWidth("100%");
        FlowPanel readPanel2 = new FlowPanel();
        readPanel.add(readPanel2);

        HTML datasetTitle = new HTML("<h2>Read Resource</h2>");
        datasetTitle.addStyleName("tool-section-header");
        datasetTitle.setWidth("100%");
        readPanel2.add(datasetTitle);

        HorizontalFlowPanel referencePanel = new HorizontalFlowPanel();
        referencePanel.add(new Label("Resource Reference:"));

        refTextBox.setVisibleLength(60);
        referencePanel.add(refTextBox);
        referencePanel.add(new Label("(ResourceType/ID)"));
        readPanel2.add(referencePanel);

        HTML buttonPanelTitle = new HTML("Actions");
        buttonPanelTitle.addStyleName("tool-section-header");
        buttonPanelTitle.setWidth("100%");
        readPanel2.add(buttonPanelTitle);

        HorizontalFlowPanel buttonPanel = new HorizontalFlowPanel();
        runButton.setEnabled(false);
        buttonPanel.add(runButton);
        readPanel2.add(buttonPanel);
        thePanel.add(readPanel);


        thePanel.add(new HTML("<br />"));
        HTML logTitle = new HTML("<b>Logs</b>");
        logTitle.addStyleName("tool-section-header");
        thePanel.add(logTitle);

        ScrollPanel logWrapperPanel = new ScrollPanel();
        logWrapperPanel.add(logPanel);

        TabLayoutPanel bottomPanel = new TabLayoutPanel(1.5, Style.Unit.EM);
        bottomPanel.setWidth("100%");
//        bottomPanel.setWidth("800px");
        bottomPanel.setHeight("400px");
        thePanel.add(bottomPanel);
        logPanel.setWidth("100%");
        logPanel.setHeight("100%");
        bottomPanel.add(logWrapperPanel, "[Log]");
        bottomPanel.add(inspectorPanel, "[Inspector]");

        ScrollPanel viewScrollPanel = new ScrollPanel();
        viewScrollPanel.add(viewPanel);
        bottomPanel.add(viewScrollPanel, "[Resource]");

        ScrollPanel simLogScrollPanel = new ScrollPanel();
        simLogScrollPanel.add(simLogPanel);
        bottomPanel.add(simLogScrollPanel, "[SimLog");

        inspectorPanel.setWidth("100%");

        return tabTopPanel;
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
        refTextBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                getPresenter().doSetResourceReference(refTextBox.getText());
            }
        });
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
        viewPanel.clear();
        viewPanel.add(content);
    }

    void clearContent() {
        contentPanel.setVisible(false);
    }

}
