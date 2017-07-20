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
import gov.nist.toolkit.xdstools2.client.abstracts.MessagePanel;
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

    private MessagePanel messagePanel = new MessagePanel();

    private VerticalPanel transactionDisplayPanel = new VerticalPanel();
    VerticalPanel transactionNamesPanel = new VerticalPanel();
    ListBox eventListBox = new ListBox();

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
    private String selectedMessageId = null;

    private SimId simid = null;


    String selectedEvent;

    void setDownloadLink(String link) {
        download.setHTML(link);
    }



//    class FilterClickHandler implements ClickHandler {
//
//        @Override
//        public void onClick(ClickEvent clickEvent) {
//            updateTransactionsDisplay();
//        }
//    }

//    class FilterKeyDownHandler implements KeyDownHandler {
//
//        @Override
//        public void onKeyDown(KeyDownEvent event) {
//            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
//                updateTransactionsDisplay();
//            }
//        }
//    }

    private String selectedSimId() {
        String simId = simulatorNamesListBox.getSelectedValue();
        if (simId ==null || simId.equals("")) return null;
        return simId;
    }

    private String selectedEvent() {
        return eventListBox.getSelectedValue();
    }

    public SimId getSimid() { return simid; }
    public void setSimId(SimId simid) {
        this.simid = simid;
        simulatorNameLabel.setText(simid.toString());
        simNameOrNamesPanel.clear();
        simNameOrNamesPanel.add(simulatorNameLabel);
        refreshButton.setEnabled(false);
    }

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

        tabTopPanel.add(messagePanel);
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

//        filterPanel.add(filterField);
//        Button filterButton = new Button("Filter");
//        filterButton.addClickHandler(new FilterClickHandler());
//        filterField.addKeyDownHandler(new FilterKeyDownHandler());
//        filterPanel.add(filterButton);
//        transactionDisplayPanel.add(filterPanel);

        transactionDisplayPanel.add(HtmlMarkup.html(HtmlMarkup.bold("Messages")));
        eventListBox.setVisibleItemCount(20);
        transactionDisplayPanel.add(eventListBox);

        transactionDisplayPanel.add(eventLinkPanel);

        transactionDisplayPanel.add(refreshButton);

        transactionDisplayPanel.add(inspectRequestButton);

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

        eventListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) { getPresenter().doUpdateChosenEvent(selectedEvent()); }
        });

        inspectRequestButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                getPresenter().doInspectRequest();
            }
        });

        inspectResponseButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) { getPresenter().doInspectResponse(); }
        });

        refreshButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                getPresenter().doRefresh();
            }
        });

    }

    void clear() {
        scrollInPanel.clear();
        scrollOutPanel.clear();
        scrollLogPanel.clear();
    }


//    private void updateTransactionsDisplay() {
//        String filterText = filterField.getText().trim().toLowerCase();
//        eventListBox.clear();
//        currentTransactionInstance = null;
//        updateEventLink();
//
//        for (TransactionInstance ti : events) {
//            String displayText = ti.toString();
//            String displayTextForComparison = displayText.toLowerCase();
//            if (selectedMessageId == null || selectedMessageId.equals("all")) { // no message selectedValue
//                if (filterText.isEmpty() || displayTextForComparison.contains(filterText)) {
//                    eventListBox.addItem(displayText, ti.messageId);
//                    currentTransactionInstance = ti;
//                }
//            }
//            else {
//                if (selectedMessageId.equals(ti.messageId)) { // this is the selectedValue message
//                    if (filterText.isEmpty() || displayTextForComparison.contains(filterText)) {
//                        eventListBox.addItem(displayText, ti.messageId);
//                        currentTransactionInstance = ti;
//                        updateEventLink();
//                    }
//                }
//            }
//        }
//    }

    void updateEventLink(Widget w) {
        linkPanel.clear();
        linkPanel.add(w);
    }

    void displaySimulators(List<String> names) {
        simulatorNamesListBox.clear();
        simulatorNamesListBox.addItem("--choose--");
        for (String name : names) simulatorNamesListBox.addItem(name);
    }

    void selectSimulator(int i) {
        simulatorNamesListBox.setSelectedIndex(i);

    }

    void displayEvents(List<EventInfo> events) {
        eventListBox.clear();
        for (EventInfo e : events) {
            eventListBox.addItem(e.getDisplay(), e.getId());
        }
    }

    void setRequestMessageDetail(Message message) {
        FlowPanel panel = new FlowPanel();
        panel.add(htmlize("Request Message<br />", message.getParts().get(0)));
        scrollInPanel.add(panel);
    }

    void setResponseMessageDetail(Message message) {
        FlowPanel panel = new FlowPanel();
        panel.add(htmlize("Response Message<br />", message.getParts().get(0)));
        scrollOutPanel.add(panel);
    }

    void setLogDetail(String details) {
        scrollLogPanel.add(htmlize("Log", details));
    }

    void message(String msg) { messagePanel.addMessage(msg); }

    void message(HTML msg) { messagePanel.addMessage(msg); }

    void clearMessages() { messagePanel.clear(); }

}
