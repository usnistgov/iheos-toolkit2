package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.session.shared.Message;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;
import gov.nist.toolkit.xdstools2.client.abstracts.MessagePanel;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.util.ASite;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
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
//    private Button deleteButton = new Button("Delete");
    private Map<String, MessageDisplayView> tabMap = new HashMap<>();

    private HTML download = new HTML();

    List<MessageDisplayView> tabs = new ArrayList<>();
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


    ///////////////////////////////////////////////////////
    //
    // stuff that belongs in presenter
    //
    ///////////////////////////////////////////////////////
    private String currentActor;
    private String currentTransaction;
    private String transName = "";
    private String selectedMessageId = null;

//    private SimId simid = null;
//    String selectedEvent;

    void setDownloadLink(String link) {
        download.setHTML(link);
    }

    private SystemSelector systemSelector = new SystemSelector("Simulator") {
        @Override
        public void doSelected(String label) {
            getPresenter().doUpdateChosenSimulator(label);
        }
    };

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


    private void buildTabs() {
        for (String name : tabNames) {
            MessageDisplayView mdv = new MessageDisplayView(name);
            detailsTabPanel.add(mdv.asWidget(), name);
            tabMap.put(name, mdv);
        }
        detailsTabPanel.selectTab(tabNames.indexOf(requestBodyTabName));
    }

    private MessageDisplayView getTab(String title) {
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
        ClientUtils.INSTANCE.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
            @Override
            public void onTestSessionChanged(TestSessionChangedEvent event) {
                if (event.getChangeType() == TestSessionChangedEvent.ChangeType.SELECT) {
                    GWT.log("SimMsgViewer - test session changed");
                    getPresenter().testSessionChanged(new TestSession(event.getValue()));
                }
            }
        });

        eventListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                getPresenter().doUpdateChosenEvent(selectedEvent()); }
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
        for (MessageDisplayView tab : tabMap.values()) {
            tab.clear();
        }
    }

    void updateEventLink(Widget w) {
        linkPanel.clear();
        linkPanel.add(w);
    }

    void selectSimulator(String simId) {
        systemSelector.updateSiteSelectedView(simId);
    }

    void displayEvents(List<EventInfo> events, String preselectedEventId) {
        eventListBox.clear();
        for (EventInfo e : events) {
            eventListBox.addItem(e.getDisplay(), e.getId());
            if (preselectedEventId!=null && e.getId().equals(preselectedEventId)) {
               eventListBox.setSelectedIndex(eventListBox.getItemCount()-1);
            }
        }
        if (preselectedEventId==null && eventListBox.getItemCount()>0) {
            eventListBox.setItemSelected(0, true);
        }

    }

    /**
     * This updates which transaction is displayed but does not change
     * which part of the transaction is displayed
     * @param message
     */
    void setRequestMessageDetail(Message message) {
        GWT.log("Request Message Detail");
        setHeader(requestHeaderTabName, message);
        setMessageDetail(requestBodyTabName, message);
    }

    /**
     * This updates which transaction is displayed but does not change
     * which part of the transaction is displayed
     * @param message
     */
    void setResponseMessageDetail(Message message) {
        GWT.log("Response Message Detail");
        setHeader(responseHeaderTabName, message);
        setMessageDetail(responseBodyTabName, message);
    }

    private void setHeader(String tabName, Message message) {
        getTab(tabName).newContent().add(htmlize(message.getParts().get(0)));
    }

    private void setMessageDetail(String tabName, Message message) {
        MessageDisplayView tab = getTab(tabName);
//        tab.menuPanel.clear();
        MessageDisplay messageDisplay = new MessageDisplay(message);

        //tab.menuPanel.add(messageDisplay.getMenuPanel());
        tab.newMenu().add(messageDisplay.getMenuPanel());
        tab.getContentPanel().add(messageDisplay.getContentPanel());
    }

    void setLogDetail(String details) {
        getTab(logTabName).newContent().add(htmlize(details));
    }

    void message(String msg) { messagePanel.addMessage(msg); }

    void message(HTML msg) { messagePanel.addMessage(msg); }

    void clearMessages() { messagePanel.clear(); }

    void setSiteNames(List<ASite> sites) {
        systemSelector.setNames(sites);
    }

    void selectSite(String siteName) {
        systemSelector.updateSiteSelectedView(siteName);
    }

}
