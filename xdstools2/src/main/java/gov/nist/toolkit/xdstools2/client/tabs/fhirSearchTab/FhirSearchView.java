package gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;
import gov.nist.toolkit.xdstools2.client.abstracts.MessagePanel;
import gov.nist.toolkit.xdstools2.client.util.ASite;
import gov.nist.toolkit.xdstools2.client.util.AnnotatedItem;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.client.widgets.SystemSelector;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class FhirSearchView extends AbstractView<FhirSearchPresenter> implements IContentHolder{
    private MessagePanel messagePanel = new MessagePanel();
    private VerticalPanel tabTopPanel = new VerticalPanel();
    private HTML selected = new HTML();
    private Button readRunButton = new Button("Read Resource");
    private Button searchRunButton = new Button("Search");
    private FlowPanel thePanel = new FlowPanel();
    private FlowPanel logPanel = new FlowPanel();
    private FlowPanel resourceDisplayOuterPanel = new FlowPanel();
//    private FlowPanel resourceDisplayPanel = new FlowPanel();
    private FlowPanel contentPanel = new FlowPanel();
    private TabbedContentPanel bottomPanel;
    private Button searchInspectButton = new Button("Inspect");
    private Button readInspectButton = new Button("Inspect");


    private TabLayoutPanel bottomTabPanel = new TabLayoutPanel(1.5, Style.Unit.EM);

    // READ Stuff
    private TextBox refTextBox = new TextBox();

    // SEARCH Stuff
    private TextBox patientTextBox = new TextBox();

    private SystemSelector systemSelector = new SystemSelector("To System") {
        @Override
        public void doSelected(String label) {
            getPresenter().doSiteSelected(label);
        }
    };

    private SystemSelector resourceTypeSelector = new SystemSelector("Resource Type", null) {
        @Override
        public void doSelected(String label) {
            getPresenter().doResourceTypeSelected(label);
        }
    };


    public SystemSelector getSystemSelector() {
        return systemSelector;
    }

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
        tabTopPanel.add(new HTML("<h2>FHIR Search/Read</h2>"));

        thePanel.add(systemSelector.asWidget());

        thePanel.setWidth("100%");
        tabTopPanel.add(thePanel);

        VerticalPanel datasetWrapper = new VerticalPanel();
        datasetWrapper.setWidth("100%");
        thePanel.add(new HTML("<br />"));

        /**
         * Read Resource
         */


        buildReadResourcePanel();


        /**
         * Search
         */


        buildSearchResourcePanel();


        thePanel.add(new HTML("<br />"));
        bottomPanel = new TabbedContentPanel("Logs", "400px");
        thePanel.add(bottomPanel.asWidget());

        bottomPanel.addBaseTab(logPanel, "[Log]");
        bottomPanel.addBaseTab(resourceDisplayOuterPanel, "[Content]");

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(tabTopPanel);

        return scrollPanel;
    }

    private String backgroundStyle = "my-table-noband";
    private String actionsBackgroundStyle = "my-table-noband";//"tool-section-header";

    private void buildSearchResourcePanel() {
        FlowPanel searchPanel = new FlowPanel();
        searchPanel.setWidth("100%");
        searchPanel.addStyleName(backgroundStyle);
        FlowPanel innerPanel = new FlowPanel();
        searchPanel.add(innerPanel);

        HTML datasetTitle = new HTML("<h2>Search</h2>");
        datasetTitle.addStyleName("tool-section-header");
        datasetTitle.setWidth("100%");
        innerPanel.add(datasetTitle);

        innerPanel.add(resourceTypeSelector.asWidget());


        HorizontalFlowPanel referencePanel = new HorizontalFlowPanel();
        referencePanel.add(new Label("Patient ID:"));

        patientTextBox.setVisibleLength(90);
        referencePanel.add(patientTextBox);
        referencePanel.add(new Label("(system|value or id^^^&oid&ISO or Patient Resource URL)"));
        innerPanel.add(referencePanel);



        HTML buttonPanelTitle = new HTML("Actions");
        buttonPanelTitle.addStyleName(actionsBackgroundStyle);
        buttonPanelTitle.setWidth("100%");
        innerPanel.add(buttonPanelTitle);

        HorizontalFlowPanel buttonPanel = new HorizontalFlowPanel();
        searchRunButton.setEnabled(false);
        buttonPanel.add(searchRunButton);
        buttonPanel.add(searchInspectButton);
        searchInspectButton.setEnabled(false);
        innerPanel.add(buttonPanel);
        thePanel.add(searchPanel);
    }

    private void buildReadResourcePanel() {
        FlowPanel readPanel = new FlowPanel();
        readPanel.setWidth("100%");
        readPanel.addStyleName(backgroundStyle);
        FlowPanel innerPanel = new FlowPanel();
        readPanel.add(innerPanel);

        HTML datasetTitle = new HTML("<h2>Read Resource</h2>");
        datasetTitle.addStyleName("tool-section-header");
        datasetTitle.setWidth("100%");
        innerPanel.add(datasetTitle);

        HorizontalFlowPanel referencePanel = new HorizontalFlowPanel();
        referencePanel.add(new Label("Resource Reference:"));

        refTextBox.setVisibleLength(90);
        referencePanel.add(refTextBox);
        referencePanel.add(new Label("(fullURL or ResourceType/ID - ResourceType/ID requires system selection above)"));
        innerPanel.add(referencePanel);

        HTML buttonPanelTitle = new HTML("Actions");
        buttonPanelTitle.addStyleName(actionsBackgroundStyle);
        buttonPanelTitle.setWidth("100%");
        innerPanel.add(buttonPanelTitle);

        HorizontalFlowPanel buttonPanel = new HorizontalFlowPanel();
        readRunButton.setEnabled(true);
        buttonPanel.add(readRunButton);
        buttonPanel.add(readInspectButton);
        readInspectButton.setEnabled(false);
        innerPanel.add(buttonPanel);
        thePanel.add(readPanel);
    }

    @Override
    protected void bindUI() {

        readRunButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                getPresenter().doReadRun();
                readInspectButton.setEnabled(true);
            }
        });


        searchRunButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                getPresenter().doSearchRun();
                searchInspectButton.setEnabled(true);
            }
        });

        searchInspectButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                getPresenter().doSearchInspect();
            }
        });

        readInspectButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                getPresenter().doReadInspect();
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

        patientTextBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                getPresenter().doSetPatientId(patientTextBox.getText());
            }
        });

    }

    void setSiteNames(List<ASite> sites) {
        systemSelector.setNames(sites);
    }

    void setResourceTypeNames(List<AnnotatedItem> names) {
        resourceTypeSelector.setNames(names);
    }

    MessagePanel getMessagePanel() { return messagePanel; }

    void setReadEnabled(boolean enabled) {
        readRunButton.setEnabled(enabled);
    }

    void setSearchEnabled(boolean enabled) {
        searchRunButton.setEnabled(enabled);
    }

    void addLog(String msg) {
        addLog(new HTML(msg));
    }

    void addLog(Widget msg) {
        logPanel.add(msg);
    }

    void clearLog() { logPanel.clear(); }

    void setContent(Widget content) {
        resourceDisplayOuterPanel.clear();
        resourceDisplayOuterPanel.add(content);
    }

    private ScrollPanel inScrollPanel(Widget w) {
        w.setWidth("100%");
        w.setHeight("100%");
        ScrollPanel sp = new ScrollPanel();
        sp.add(w);
        return sp;
    }

    VerticalPanel getTabTopPanel() {
        return tabTopPanel;
    }

    @Override
    public void addContent(Widget w, String title) {
        bottomPanel.addTab(inScrollPanel(w), title);
    }

    @Override
    public void clearLogContent() {
        bottomPanel.clearContent();
    }
}
