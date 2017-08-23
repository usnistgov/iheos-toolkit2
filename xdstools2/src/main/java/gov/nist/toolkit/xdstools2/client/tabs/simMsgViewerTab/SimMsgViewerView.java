package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.services.shared.Message;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;
import gov.nist.toolkit.xdstools2.client.abstracts.MessagePanel;
import gov.nist.toolkit.xdstools2.client.util.ASite;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.client.widgets.SystemSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SimMsgViewerView extends AbstractView<SimMsgViewerPresenter> {
    private HorizontalPanel simDisplayPanel = new HorizontalPanel();
    private FlowPanel simControlPanel = new FlowPanel();
    private FlowPanel detailPanel = new FlowPanel();
    private HorizontalFlowPanel inOutPanel = new HorizontalFlowPanel();
    private FlowPanel transInPanel = new FlowPanel();
    private FlowPanel transOutPanel = new FlowPanel();
    private FlowPanel logPanel = new FlowPanel();
    private ScrollPanel scrollInPanel = new ScrollPanel();
    private ScrollPanel scrollOutPanel = new ScrollPanel();
    private ScrollPanel scrollLogPanel = new ScrollPanel();
    private HorizontalFlowPanel simSelectionDisplayPanel = new HorizontalFlowPanel();
    private HorizontalFlowPanel eventLinkPanel = new HorizontalFlowPanel();
    private HorizontalFlowPanel filterPanel = new HorizontalFlowPanel();
    private TextBox filterField = new TextBox();

    private MessagePanel messagePanel = new MessagePanel();

    private VerticalPanel transactionDisplayPanel = new VerticalPanel();
    VerticalPanel transactionNamesPanel = new VerticalPanel();
    ListBox eventListBox = new ListBox();

    private HorizontalFlowPanel linkPanel = new HorizontalFlowPanel();

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

    private SystemSelector systemSelector = new SystemSelector("Simulator") {
        @Override
        public void doSiteSelected(String label) {
            getPresenter().doUpdateChosenSimulator(label);
        }
    };


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

    private String selectedEvent() {
        return eventListBox.getSelectedValue();
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

    List<Tab> tabs = new ArrayList<>();
    private TabLayoutPanel detailsTabPanel;

    static private final String requestHeaderTabName = "[Request Header]";
    static private final String requestBodyTabName = "[Request Body]";
    static private final String responseHeaderTabName = "[Response Header]";
    static private final String responseBodyTabName = "[Response Body]";
    static private final String logTabName = "[Log]";

    static private final List<String> tabNames = new ArrayList<>();
    static {
        tabNames.add(requestHeaderTabName);
        tabNames.add(requestBodyTabName);
        tabNames.add(responseHeaderTabName);
        tabNames.add(responseBodyTabName);
        tabNames.add(logTabName);
    }

    private void buildTabs() {
        for (String name : tabNames)
            new Tab(name);
        detailsTabPanel.selectTab(tabNames.indexOf(requestBodyTabName));
    }

    private Map<String, Tab> tabMap = new HashMap<>();

    class Tab {
        String title;
        FlowPanel outerPanel = new FlowPanel();
        ScrollPanel scrollPanel = new ScrollPanel();
        FlowPanel contentPanel = new FlowPanel();

        Tab(String title) {
            this.title = title;
//            outerPanel.add(contentPanel);
//            scrollPanel.add(contentPanel);
            scrollPanel.add(contentPanel);
            outerPanel.add(scrollPanel);
            detailsTabPanel.add(outerPanel, title);

            outerPanel.setWidth("100%");
            outerPanel.setHeight("100%");
            scrollPanel.setWidth("100%");
            scrollPanel.setHeight("100%");
            contentPanel.setWidth("100%");
            contentPanel.setHeight("100%");

            tabMap.put(title, this);
        }

        Tab clear() {
            contentPanel.clear();
            return this;
        }

        FlowPanel newContent() {
            clear();
            return contentPanel;
        }
    }

    private Tab getTab(String title) {
        return tabMap.get(title);
    }

    @Override
    protected Widget buildUI() {
        FlowPanel tabTopPanel = new FlowPanel();

        tabTopPanel.add(HtmlMarkup.html(HtmlMarkup.h2("Transaction Log")));

        tabTopPanel.add(messagePanel);

        tabTopPanel.add(new HTML("<br />"));
        tabTopPanel.add(systemSelector.asWidget());

        tabTopPanel.add(new HTML("<br />"));
        tabTopPanel.add(linkPanel);
        tabTopPanel.add(new HTML("<br />"));

        tabTopPanel.add(simDisplayPanel);
        simDisplayPanel.add(detailPanel);

        detailsTabPanel = new TabLayoutPanel(1.5, Style.Unit.EM);
        detailsTabPanel.setWidth("800px");
        detailsTabPanel.setHeight("400px");

        simDisplayPanel.add(simControlPanel);
        simDisplayPanel.add(detailsTabPanel);

        buildTabs();


//        detailPanel.add(inOutPanel);
//        detailPanel.add(logPanel);

        scrollInPanel.setWidth("500px");
        scrollInPanel.setHeight("300px");
//        transInPanel.setBorderWidth(1);
        transInPanel.add(scrollInPanel);

        scrollOutPanel.setWidth("500px");
        scrollOutPanel.setHeight("300px");
        //transOutPanel.setBorderWidth(1);
        transOutPanel.add(scrollOutPanel);

        scrollLogPanel.setWidth("1000px");
        scrollLogPanel.setHeight("300px");
        //logPanel.setBorderWidth(1);
        logPanel.add(scrollLogPanel);

        inOutPanel.add(transInPanel);
        inOutPanel.add(transOutPanel);

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

    void clearAllTabs() {
        for (Tab tab : tabMap.values()) {
            tab.clear();
        }
//        scrollInPanel.clear();
//        scrollOutPanel.clear();
//        scrollLogPanel.clear();
    }


//    private void updateTransactionsDisplay() {
//        String filterText = filterField.getText().trim().toLowerCase();
//        eventListBox.clearAllTabs();
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

    void selectSimulator(String simId) {
        systemSelector.updateSiteSelectedView(simId);
    }

    void displayEvents(List<EventInfo> events) {
        eventListBox.clear();
        for (EventInfo e : events) {
            eventListBox.addItem(e.getDisplay(), e.getId());
        }
        if (events.size() > 0) {
            eventListBox.setItemSelected(0, true);
            EventInfo eventInfo = events.get(0);
            String id = eventInfo.getId();
            getPresenter().doUpdateChosenEvent(id);
        }
    }

    /**
     * This updates which transaction is displayed but does not change
     * which part of the transaction is displayed
     * @param message
     */
    void setRequestMessageDetail(Message message) {
        GWT.log("Request Message Detail");
        getTab(requestHeaderTabName).newContent().add(htmlize(message.getParts().get(0)));
        if (message.getParts().size() > 1)
        getTab(requestBodyTabName).newContent().add(htmlize(message.getParts().get(1)));
    }

    /**
     * This updates which transaction is displayed but does not change
     * which part of the transaction is displayed
     * @param message
     */
    void setResponseMessageDetail(Message message) {
        GWT.log("Response Message Detail");
        getTab(responseHeaderTabName).newContent().add(htmlize(message.getParts().get(0)));
        if (message.getParts().size() > 1)
            getTab(responseBodyTabName).newContent().add(htmlize(message.getParts().get(1)));
    }

    void setLogDetail(String details) {
        getTab(logTabName).newContent().add(htmlize(details));
    }

    void message(String msg) { messagePanel.addMessage(msg); }

    void message(HTML msg) { messagePanel.addMessage(msg); }

    void clearMessages() { messagePanel.clear(); }

    void setSiteNames(List<ASite> sites) {
        systemSelector.setSiteNames(sites);
    }

    void selectSite(String siteName) {
        systemSelector.updateSiteSelectedView(siteName);
    }

}
