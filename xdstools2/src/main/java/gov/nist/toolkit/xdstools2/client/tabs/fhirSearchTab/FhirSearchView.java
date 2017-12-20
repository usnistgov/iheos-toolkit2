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
public class FhirSearchView extends AbstractView<FhirSearchPresenter> {
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

        /**
         * Read Resource
         */


        buildReadResourcePanel();


        /**
         * Search
         */


        buildSearchResourcePanel();


        thePanel.add(new HTML("<br />"));
        HTML logTitle = new HTML("<b>Logs</b>");
        logTitle.addStyleName("tool-section-header");
        thePanel.add(logTitle);

        ScrollPanel logWrapperPanel = new ScrollPanel();
        logWrapperPanel.add(logPanel);

        TabLayoutPanel bottomTabPanel = new TabLayoutPanel(1.5, Style.Unit.EM);
        bottomTabPanel.setWidth("100%");
//        bottomTabPanel.setWidth("800px");
        bottomTabPanel.setHeight("400px");
        thePanel.add(bottomTabPanel);
        logPanel.setWidth("100%");
        logPanel.setHeight("100%");
        bottomTabPanel.add(logWrapperPanel, "[Log]");

        bottomTabPanel.add(resourceDisplayOuterPanel, "[Resource]");

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

        patientTextBox.setVisibleLength(60);
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

        refTextBox.setVisibleLength(60);
        referencePanel.add(refTextBox);
        referencePanel.add(new Label("(ResourceType/ID)"));
        innerPanel.add(referencePanel);

        HTML buttonPanelTitle = new HTML("Actions");
        buttonPanelTitle.addStyleName(actionsBackgroundStyle);
        buttonPanelTitle.setWidth("100%");
        innerPanel.add(buttonPanelTitle);

        HorizontalFlowPanel buttonPanel = new HorizontalFlowPanel();
        readRunButton.setEnabled(false);
        buttonPanel.add(readRunButton);
        innerPanel.add(buttonPanel);
        thePanel.add(readPanel);
    }

    @Override
    protected void bindUI() {

        readRunButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                getPresenter().doReadRun();
            }
        });


        searchRunButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                getPresenter().doSearchRun();
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

    void setContentWithScrollBar(Widget content) {
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(content);
        resourceDisplayOuterPanel.clear();
        resourceDisplayOuterPanel.add(scrollPanel);
    }

    VerticalPanel getTabTopPanel() {
        return tabTopPanel;
    }
}
