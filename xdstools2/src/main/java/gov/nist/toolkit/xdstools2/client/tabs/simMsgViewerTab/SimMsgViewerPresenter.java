package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.session.shared.Message;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimIdFactory;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.xdstools2.client.command.command.*;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.ResultInspector;
import gov.nist.toolkit.xdstools2.client.toolLauncher.NewToolLauncher;
import gov.nist.toolkit.xdstools2.client.util.*;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.SimLog;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimIdsForUserRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimulatorEventRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTransactionRequest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class SimMsgViewerPresenter extends AbstractPresenter<SimMsgViewerView> {
    /**
     * SimIds loaded from server - fully loaded with actor type and isFhir status
     */
    private List<SimId> simIds;
    private List<TransactionInstance> events = new ArrayList<>();
    private TransactionInstance currentTransactionInstance = null;


    private SimId currentSimId = null;
    private String currentTransaction;
    private String preSelectEvent;

    @Inject
    public SimMsgViewerPresenter() {
        super();
        GWT.log("Build SimMsgViewerPresenter");
    }

    @Override
    public void init() {
        GWT.log("Init SimMsgViewerPresenter");
        if (currentSimId == null)  // setCurrentSimId was already called so Sims are not listed
            loadSimulatorNames();
    }

    /**
     * Request is coming from UI so this is only partial info in the SimId. Get full context
     * from server
     * @param simId
     */
    void setCurrentSimId(SimId simId) {
        currentSimId = simId;  // this prevents init() from loading full context
        if (simId == null)
            setTitle("Log");
        else
            setTitle("Log " + simId.toString());
        loadSimulatorNames();
    }

    private void loadSimulatorNames() {
        final List<String> names = new ArrayList<>();
        new GetSimIdForUser(){
            @Override
            public void onComplete(List<SimId> result) {
                simIds = result;
                List<ASite> aSites = new SiteFilter(result).sorted();
                getView().setSiteNames(aSites);
                for (SimId simId : result) {
                    names.add(simId.toString());
                }
//                getView().displaySimulators(names);

                if (currentSimId != null) {
                    int i = names.indexOf(currentSimId.toString());
                    GWT.log("loadSimulatorNames - preselect " + i);
                    if (i != -1) {
                        getView().selectSimulator(currentSimId.toString());
                        loadTransactionTypes();
                    }
                }
                else if (result.size()==1) {
                    currentSimId = result.get(0);
                    getView().selectSimulator(currentSimId.toString());
                    loadTransactionTypes();
                }
//                if(currentSimId != null)
//                    getView().selectSite(currentSimId.toString());
            }
        }.run(new GetSimIdsForUserRequest(getCommandContext(), null));
    }

    // not sure why this level is useful. Maybe for filters?
    private void loadTransactionTypes() {
        getView().eventListBox.clear();
        getView().transactionNamesPanel.clear();
        if (currentSimId != null) {
//            assert (currentSimId != null);
            //getSimulatorTransactionNames
            new GetTransactionsForSimulatorCommand() {
                @Override
                public void onComplete(List<String> result) {
                    GWT.log("Loaded " + result.size() + " events");
                    loadEventsForSimulator("all", null);
                }

            }.run(new GetTransactionRequest(ClientUtils.INSTANCE.getCommandContext(), currentSimId));
        }
    }

    private void displayEvents(List<TransactionInstance> events, String preselectedEventId) {
        this.events = events;
        List<EventInfo> eventInfos = new ArrayList<>();
        for (TransactionInstance ti : events) {
            eventInfos.add(new EventInfo(ti.messageId, ti.toString()));
        }
        getView().displayEvents(eventInfos,preselectedEventId);

        if (preselectedEventId!=null) {
            doUpdateChosenEvent(preselectedEventId);
        } else if (eventInfos.size()>0) {
            doUpdateChosenEvent(eventInfos.get(0).getId());
        }
    }

    void doUpdateChosenEvent(String messageId) {
        GWT.log("doUpdateChosenEvent " + messageId);
        TransactionInstance transactionInstance = null;

        for (TransactionInstance ti : events) {
            if (ti.messageId.equals(messageId)) {
                transactionInstance = ti;
                break;
            }
        }
        GWT.log("transactionInstance is " + transactionInstance);
        if (transactionInstance == null) return;
        currentTransactionInstance = transactionInstance;

        updateEventLink();
        loadTransactionInstanceDetails(currentTransactionInstance);

        assert(transactionInstance.getActorType() != null);
        assert(transactionInstance.getTransactionTypeName() != null);

        String u = "<a href=\"" +
                "message/" + currentSimId + "/" +
                transactionInstance.getActorType().getShortName() + "/" +
                transactionInstance.getTransactionTypeName() + "/" +
                messageId + "\"" +
                ">Download Message</a>";
        getView().setDownloadLink(u);

    }

    private void updateEventLink() {
        if (currentTransactionInstance != null) {
            SimLog simLog = new SimLog(currentTransactionInstance);
            getView().updateEventLink(new ToolkitLink("SimResource Link: ", "#SimMsgViewer:" + (new SimLog.Tokenizer()).getToken(simLog)).asWidget());
        } else {
            getView().updateEventLink(new HTML(""));
        }
    }

    private void loadEventsForSimulator() {
        loadEventsForSimulator((SimpleCallback) null);
    }

    private void loadEventsForSimulator(SimpleCallback callback) {
        loadEventsForSimulator("all", callback);
    }

    private void loadEventsForSimulator(String transNameFilter) {
        loadEventsForSimulator(transNameFilter, null);
    }

    private void loadEventsForSimulator(String transNameFilter, final SimpleCallback callback) {
        GWT.log("loadEventsForSimulator transNameFilter: " + transNameFilter + " preselect: " + preSelectEvent);
        if (currentSimId == null) {
            getView().message("Select Simulator from the list");
            return;
        }
        currentTransaction = transNameFilter;
        getView().clearAllTabs();

        if ("all".equalsIgnoreCase(transNameFilter))
            transNameFilter = null;
        new GetTransactionInstancesCommand(){
            @Override
            public void onComplete(List<TransactionInstance> events) {
                displayEvents(events, preSelectEvent);
                preSelectEvent = null; // reset
                if (callback != null)
                    callback.run();
            }
        }.run(new GetTransactionRequest(getCommandContext(), currentSimId,"",transNameFilter));
    }

    /**
     * this simIds returned from the server have full content so they should be used in all
     * calls to the server.  This call looks up the SimId
     * @param simId - minimal SimId (only needs user and id attributes)
     * @return - fully configured SimId instance loaded from server
     */
    private SimId getServerSimId(SimId simId) {
        for (SimId sid : simIds) {
            if (sid.equals(simId)) return sid;
        }
        return null;
    }

    private TransactionInstance findTransactionInstance(String label) {
        if (label == null) return null;
        for (TransactionInstance ti : events) {
            if (label.equals(ti.messageId)) return ti;
            if (label.equals(ti.labelInterpretedAsDate)) return ti;
        }
        return null;
    }

    void doUpdateChosenSimulator(String simName) {
        GWT.log("doUpdateChosenSimulator - " + simName + " preSelectEvent: " + preSelectEvent);
        if (preSelectEvent==null) {
            assert (simName != null);
            assert (!simName.equals(""));
            // translates to server version of simid
            currentSimId = getServerSimId(SimIdFactory.simIdBuilder(simName));
            loadEventsForSimulator();
            getView().clearAllTabs();
            currentTransactionInstance = null;
            updateEventLink();
            setTitle("Log " + currentSimId.toString());
        }
    }

    void doInspectRequest() {
        new GetSimulatorEventRequestCommand(){
            @Override
            public void onComplete(Result result) {
                inspectResult(result);
            }
        }.run(new GetSimulatorEventRequest(getCommandContext(),currentTransactionInstance));
    }

    void doInspectResponse() {
        new GetSimulatorEventResponseCommand(){
            @Override
            public void onComplete(Result result) {
                inspectResult(result);
            }
        }.run(new GetSimulatorEventRequest(getCommandContext(),currentTransactionInstance));
    }

    void inspectResult(Result result) {
        List<Result> results = new ArrayList<Result>();
        results.add(result);

//        MetadataInspectorTab tab = new MetadataInspectorTab();
//        tab.setResults(results);
//        SiteSpec siteSpec = new SiteSpec(currentSimId.toString(), currentTransactionInstance.actorType, null);
//        tab.setSiteSpec(siteSpec);
//        tab.onTabLoad(true, "Insp");

        ResultInspector resultInspector = new ResultInspector();
        resultInspector.setResults(results);
        SiteSpec siteSpec = new SiteSpec(currentSimId.toString(), currentTransactionInstance.actorType, null, currentSimId.getTestSession());
        resultInspector.setSiteSpec(siteSpec);
        new NewToolLauncher().launch(resultInspector);
    }


    private void loadTransactionInstanceDetails(TransactionInstance ti) {
        if (ti.actorType == null) return;
        String actor = ti.actorType.getShortName();
        String trans = ti.trans;
        String messageId = ti.messageId;

        getView().clearAllTabs();

        new GetTransactionRequestCommand(){
            @Override
            public void onComplete(Message message) {
                getView().setRequestMessageDetail(message);
            }
        }.run(new GetTransactionRequest(getCommandContext(), currentSimId,actor,trans,messageId));

        /*
         * Update which message is displayed - does not change which part of the
         * message is currently in the display.
         */
        new GetTransactionResponseCommand(){
            @Override
            public void onComplete(Message message) {
                getView().setResponseMessageDetail(message);
            }
        }.run(new GetTransactionRequest(getCommandContext(), currentSimId,actor,trans,messageId));

        new GetTransactionLogCommand(){
            @Override
            public void onComplete(String result) {
                getView().setLogDetail(result);
            }
        }.run(new GetTransactionRequest(getCommandContext(), currentSimId,actor,trans,messageId));
    }

    void doRefresh() {
        loadSimulatorNames();
        loadTransactionTypes();
        loadEventsForSimulator("all");

        getView().clearAllTabs();

    }

    public void setPreSelectEvent(String preSelectEvent) {
        this.preSelectEvent = preSelectEvent;
    }

    public void testSessionChanged() {
        currentTransactionInstance = null;
        getView().clearAllTabs();
        getView().eventListBox.clear();
        getView().transactionNamesPanel.clear();
        setCurrentSimId(null);
//        doRefresh();
        getView().eventListBox.clear();
        loadSimulatorNames();
        updateEventLink();
    }
}
