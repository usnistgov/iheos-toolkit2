package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.services.shared.Message;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.xdstools2.client.command.command.*;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.ToolkitLink;
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

    // remove this later
    public SimId getCurrentSimId() {
        return currentSimId;
    }

    private SimId currentSimId = null;
    private String currentTransaction;

    @Inject
    public SimMsgViewerPresenter() {
        super();
        GWT.log("Build SimMsgViewerPresenter");
    }

    @Override
    public void init() {
        GWT.log("Init SimMsgViewerPresenter");
        loadSimulatorNames();
    }

    private void loadSimulatorNames() {
        final List<String> names = new ArrayList<>();
        new GetSimIdForUser(){
            @Override
            public void onComplete(List<SimId> result) {
                simIds = result;
                for (SimId simId : result) {
                    names.add(simId.toString());
                }
                getView().displaySimulators(names);

                if (currentSimId != null) {
                    int i = names.indexOf(currentSimId.toString());
                    if (i != -1) {
                        getView().selectSimulator(i);
                        loadTransactionTypes();
                    }
                }
                else if (result.size()==1) {
                    getView().selectSimulator(0);
                    currentSimId = result.get(0);
                    loadTransactionTypes();
                }
            }
        }.run(new GetSimIdsForUserRequest(getCommandContext(), null));

    }

    // not sure why this level is useful. Maybe for filters?
    private void loadTransactionTypes() {
        assert(currentSimId != null);
        getView().eventListBox.clear();
        //getSimulatorTransactionNames
        new GetTransactionsForSimulatorCommand(){
            @Override
            public void onComplete(List<String> result) {
                GWT.log("Loaded " + result.size() + " events");
                getView().transactionNamesPanel.clear();
                loadEventsForSimulator("all");
            }
        }.run(new GetTransactionRequest(ClientUtils.INSTANCE.getCommandContext(), currentSimId));
    }

    private void displayEvents(List<TransactionInstance> events) {
        this.events = events;
        List<EventInfo> eventInfos = new ArrayList<>();
        for (TransactionInstance ti : events) {
            eventInfos.add(new EventInfo(ti.messageId, ti.toString()));
        }
        getView().displayEvents(eventInfos);
    }

    void doUpdateChosenEvent(String messageId) {
        TransactionInstance transactionInstance = null;

        for (TransactionInstance ti : events) {
            if (ti.messageId.equals(messageId)) {
                transactionInstance = ti;
                break;
            }
        }
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
            getView().updateEventLink(new ToolkitLink("SimResource Link: ", "#SimLog:" + (new SimLog.Tokenizer()).getToken(simLog)).asWidget());
        }
    }

    private void loadEventsForSimulator() {
        loadEventsForSimulator("all");
    }

    private void loadEventsForSimulator(String transNameFilter) {
        if (currentSimId == null) {
            getView().message("Select Simulator from the list");
            return;
        }
        currentTransaction = transNameFilter;
        getView().clear();

        if ("all".equalsIgnoreCase(transNameFilter))
            transNameFilter = null;
        new GetTransactionInstancesCommand(){
            @Override
            public void onComplete(List<TransactionInstance> events) {
                displayEvents(events);
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
        assert(simName != null);
        assert(!simName.equals(""));
        // translates to server version of simid
        currentSimId = getServerSimId(new SimId(simName));
        loadEventsForSimulator();
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
        MetadataInspectorTab tab = new MetadataInspectorTab();
        tab.setResults(results);
        SiteSpec siteSpec = new SiteSpec(currentSimId.toString(), currentTransactionInstance.actorType, null);
        tab.setSiteSpec(siteSpec);
        tab.onTabLoad(true, "Insp");
    }


    private void loadTransactionInstanceDetails(TransactionInstance ti) {
        if (ti.actorType == null) return;
        String actor = ti.actorType.getShortName();
        String trans = ti.trans;
        String messageId = ti.messageId;

        getView().clear();

        new GetTransactionRequestCommand(){
            @Override
            public void onComplete(Message message) {
                getView().setRequestMessageDetail(message);
            }
        }.run(new GetTransactionRequest(getCommandContext(), currentSimId,actor,trans,messageId));

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
        loadEventsForSimulator(currentTransaction);

        getView().clear();

    }
}
