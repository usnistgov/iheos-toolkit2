package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.services.shared.Message;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;
import gov.nist.toolkit.xdstools2.client.command.command.*;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.util.ToolkitLink;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.SimLog;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimulatorEventRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTransactionRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SimMsgViewerView extends AbstractView<SimMsgViewerPresenter> {
    private HorizontalPanel simDisplayPanel = new HorizontalPanel();
    private VerticalPanel simControlPanel = new VerticalPanel();
    private VerticalPanel detailPanel = new VerticalPanel();
    private HorizontalPanel inOutPanel = new HorizontalPanel();
    private VerticalPanel transInPanel = new VerticalPanel();
    private VerticalPanel transOutPanel = new VerticalPanel();
    private VerticalPanel logPanel = new VerticalPanel();
    private ScrollPanel scrollInPanel = new ScrollPanel();
    private ScrollPanel scrollOutPanel = new ScrollPanel();
    private ScrollPanel scrollLogPanel = new ScrollPanel();
    private HorizontalFlowPanel simSelectionDisplayPanel = new HorizontalFlowPanel();
    private FlowPanel simSelectionPanel = new FlowPanel();
    private HorizontalFlowPanel eventLinkPanel = new HorizontalFlowPanel();
    private HorizontalFlowPanel filterPanel = new HorizontalFlowPanel();
    private TextBox filterField = new TextBox();

    private VerticalPanel transactionDisplayPanel = new VerticalPanel();
    VerticalPanel transactionNamesPanel = new VerticalPanel();
    ListBox transInstanceListBox = new ListBox();

    private HorizontalFlowPanel linkPanel = new HorizontalFlowPanel();

    //	Only one of these will be displayed depending on whether simId is set
    private ListBox simulatorNamesListBox = new ListBox();
    private Label simulatorNameLabel = new Label();

    private FlowPanel simNameOrNamesPanel = new FlowPanel();

    private Button refreshButton = new Button("Refresh");
    private Button inspectRequestButton = new Button("Inspect Request");
    private Button inspectResponseButton = new Button("Inspect Response");
    private Button deleteButton = new Button("Delete");

    private HTML download = new HTML();

    ///////////////////////////////////////////////////////
    //
    // stuff that belongs in presenter
    //
    ///////////////////////////////////////////////////////
    private String currentActor;
    private String currentTransaction;
    private String transName = "";
//    private SimId simidFinal = new SimId("");
    private List<TransactionInstance> transactionInstances = new ArrayList<>();
    private TransactionInstance currentTransactionInstance = null;
    private String selectedMessageId = null;

    private SimId simid = null;


    String selectedEvent;

    void transactionInstanceSelected() {
        int selectedItem = transInstanceListBox.getSelectedIndex();
        if (selectedItem == -1) return;
        String value = transInstanceListBox.getValue(selectedItem);
        TransactionInstance ti = findTransactionInstance(value);
        if (ti == null) return;
        currentTransactionInstance = ti;
        updateEventLink();
        loadTransactionInstanceDetails(ti);

        String messageId = getMessageIdFromLabel(value);
        currentTransaction = getTransactionFromLabel(value);

        String u = "<a href=\"" +
                "message/" + simid + "/" + currentActor + "/" + currentTransaction + "/" + messageId + "\"" +
//			" target=\"_blank\"" +
                ">Download Message</a>";
        download.setHTML(u);
    }

    public void loadTransactionInstanceDetails(TransactionInstance ti) {
        SimId simid = getPresenter().getSimId();
        if (ti.actorType == null) return;
        String actor = ti.actorType.getShortName();
        String trans = ti.trans;
        String messageId = ti.messageId;

        scrollInPanel.clear();
        scrollOutPanel.clear();
        scrollLogPanel.clear();

        new GetTransactionRequestCommand(){
            @Override
            public void onComplete(Message message) {
                FlowPanel panel = new FlowPanel();
                panel.add(htmlize("Request Message<br />", message.getParts().get(0)));
                scrollInPanel.add(panel);
            }
        }.run(new GetTransactionRequest(getCommandContext(),simid,actor,trans,messageId));

        new GetTransactionResponseCommand(){
            @Override
            public void onComplete(Message message) {
                FlowPanel panel = new FlowPanel();
                panel.add(htmlize("Response Message<br />", message.getParts().get(0)));
                scrollOutPanel.add(panel);
            }
        }.run(new GetTransactionRequest(getCommandContext(),simid,actor,trans,messageId));

        new GetTransactionLogCommand(){
            @Override
            public void onComplete(String result) {
                scrollLogPanel.add(htmlize("Log", result));
            }
        }.run(new GetTransactionRequest(getCommandContext(),simid,actor,trans,messageId));
    }


    class FilterClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            updateTransactionsDisplay();
        }
    }

    class FilterKeyDownHandler implements KeyDownHandler {

        @Override
        public void onKeyDown(KeyDownEvent event) {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                updateTransactionsDisplay();
            }
        }
    }


    private String selectedSimId() {
        int selectedI = simulatorNamesListBox.getSelectedIndex();
        if (selectedI == -1) return null;
        String simId = simulatorNamesListBox.getItemText(selectedI);
        if (simId.equals("")) return null;
        return simId;
    }

    private ChangeHandler transactionInstanceChoiceChanged = new ChangeHandler() {

        public void onChange(ChangeEvent event) {
            transactionInstanceSelected();
        }
    };

    ClickHandler refreshClickHandler = new ClickHandler() {

        public void onClick(ClickEvent event) {
            getPresenter().loadTransactionTypes();
            getPresenter().loadEventsForSimulator(transName);

            clear();
        }

    };

    private ClickHandler inspectRequestClickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent clickEvent) {
            new GetSimulatorEventRequestCommand(){
                @Override
                public void onComplete(Result result) {
                    displayResult(result);
                }
            }.run(new GetSimulatorEventRequest(getCommandContext(),currentTransactionInstance));
        }
    };

    private void displayResult(Result result) {
        List<Result> results = new ArrayList<Result>();
        results.add(result);
        MetadataInspectorTab tab = new MetadataInspectorTab();
        tab.setResults(results);
        SiteSpec siteSpec = new SiteSpec(getSimid().toString(), currentTransactionInstance.actorType, null);
        tab.setSiteSpec(siteSpec);
        tab.onTabLoad(true, "Insp");
    }

    public SimId getSimid() { return simid; }
    public void setSimId(SimId simid) {
        this.simid = simid;
        simulatorNameLabel.setText(simid.toString());
        simNameOrNamesPanel.clear();
        simNameOrNamesPanel.add(simulatorNameLabel);
        refreshButton.setEnabled(false);
    }

    private ClickHandler inspectResponseClickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent clickEvent) {
            new GetSimulatorEventResponseCommand(){
                @Override
                public void onComplete(Result result) {
                    displayResult(result);
                }
            }.run(new GetSimulatorEventRequest(getCommandContext(),currentTransactionInstance));
        }
    };

    ///////////////////////////////////////////////////////
    //
    // end of presenter stuff
    //
    ///////////////////////////////////////////////////////


    public SimMsgViewerView() {
        super();
        GWT.log("Create SimMsgViewerView");
    }

    @Override
    protected Map<String, Widget> getPathToWidgetsMap() {
        return null;
    }

    @Override
    protected Widget buildUI() {
        FlowPanel tabTopPanel = new FlowPanel();

        simSelectionDisplayPanel.add(HtmlMarkup.html(HtmlMarkup.h2("Simulator:")));
        simSelectionDisplayPanel.add(simNameOrNamesPanel);
        simNameOrNamesPanel.add(simulatorNamesListBox);  // will be removed later is simId is set

        tabTopPanel.add(linkPanel);

        tabTopPanel.add(simDisplayPanel);
        simDisplayPanel.add(simControlPanel);
        simDisplayPanel.add(detailPanel);

        detailPanel.add(inOutPanel);
        detailPanel.add(logPanel);

        scrollInPanel.setWidth("500px");
        scrollInPanel.setHeight("300px");
        transInPanel.setBorderWidth(1);
        transInPanel.add(scrollInPanel);

        scrollOutPanel.setWidth("500px");
        scrollOutPanel.setHeight("300px");
        transOutPanel.setBorderWidth(1);
        transOutPanel.add(scrollOutPanel);

        scrollLogPanel.setWidth("1000px");
        scrollLogPanel.setHeight("300px");
        logPanel.setBorderWidth(1);
        logPanel.add(scrollLogPanel);

        inOutPanel.add(transInPanel);
        inOutPanel.add(transOutPanel);

        simControlPanel.add(HtmlMarkup.html(HtmlMarkup.h2("Transaction Log")));
        simControlPanel.add(simSelectionDisplayPanel);

        simControlPanel.add(transactionDisplayPanel);

        transactionDisplayPanel.add(transactionNamesPanel);

        filterPanel.add(filterField);
        Button filterButton = new Button("Filter");
        filterButton.addClickHandler(new FilterClickHandler());
        filterField.addKeyDownHandler(new FilterKeyDownHandler());
        filterPanel.add(filterButton);
        transactionDisplayPanel.add(filterPanel);

        transactionDisplayPanel.add(HtmlMarkup.html(HtmlMarkup.bold("Messages")));
        transInstanceListBox.setVisibleItemCount(20);
        transactionDisplayPanel.add(transInstanceListBox);

        transInstanceListBox.addChangeHandler(transactionInstanceChoiceChanged);

        transactionDisplayPanel.add(eventLinkPanel);

        refreshButton.addClickHandler(refreshClickHandler);
        transactionDisplayPanel.add(refreshButton);

        inspectRequestButton.addClickHandler(inspectRequestClickHandler);
        transactionDisplayPanel.add(inspectRequestButton);

        inspectResponseButton.addClickHandler(inspectResponseClickHandler);
        transactionDisplayPanel.add(inspectResponseButton);

        transactionDisplayPanel.add(download);

        return tabTopPanel;
    }

    @Override
    protected void bindUI() {
        simulatorNamesListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                getPresenter().doUpdateChosenSimulator(selectedSimId());
            }
        });

    }

    void clear() {
        scrollInPanel.clear();
        scrollOutPanel.clear();
        scrollLogPanel.clear();
    }


    private void updateTransactionsDisplay() {
        String filterText = filterField.getText().trim().toLowerCase();
        transInstanceListBox.clear();
        currentTransactionInstance = null;
        updateEventLink();

        for (TransactionInstance ti : transactionInstances) {
            String displayText = ti.toString();
            String displayTextForComparison = displayText.toLowerCase();
            if (selectedMessageId == null || selectedMessageId.equals("all")) { // no message selected
                if (filterText.isEmpty() || displayTextForComparison.contains(filterText)) {
                    transInstanceListBox.addItem(displayText, ti.messageId);
                    currentTransactionInstance = ti;
                }
            }
            else {
                if (selectedMessageId.equals(ti.messageId)) { // this is the selected message
                    if (filterText.isEmpty() || displayTextForComparison.contains(filterText)) {
                        transInstanceListBox.addItem(displayText, ti.messageId);
                        currentTransactionInstance = ti;
                        updateEventLink();
                    }
                }
            }
        }
    }

    private void updateEventLink() {
        linkPanel.clear();
        if (currentTransactionInstance != null) {
            SimLog simLog = new SimLog(currentTransactionInstance);
            linkPanel.add(new ToolkitLink("SimResource Link: ", "#SimLog:" + (new SimLog.Tokenizer()).getToken(simLog)));
        }
    }

    private TransactionInstance findTransactionInstance(String label) {
        if (label == null) return null;
        for (TransactionInstance ti : transactionInstances) {
            if (label.equals(ti.messageId)) return ti;
            if (label.equals(ti.labelInterpretedAsDate)) return ti;
        }
        return null;
    }
    private String getMessageIdFromLabel(String label) {
        String[] parts = label.split(" ");
        if (parts.length == 2)
            return parts[0];
        return label;
    }


    private String getTransactionFromLabel(String label) {
        String[] parts = label.split(" ");
        if (parts.length == 2)
            return parts[1];
        return label;
    }

    void displaySimulators(List<String> names) {
        simulatorNamesListBox.clear();
        simulatorNamesListBox.addItem("");
        for (String name : names) simulatorNamesListBox.addItem(name);
    }

    void selectSimulator(int i) {
        simulatorNamesListBox.setSelectedIndex(i);

    }

    void displayEvents(List<EventInfo> events) {
        transInstanceListBox.clear();
        for (EventInfo e : events) {
            transInstanceListBox.addItem(e.getDisplay(), e.getId());
        }
    }

}
