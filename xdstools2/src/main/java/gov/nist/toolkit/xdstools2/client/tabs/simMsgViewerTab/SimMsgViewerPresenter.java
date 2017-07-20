package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.xdstools2.client.command.command.GetSimIdForUser;
import gov.nist.toolkit.xdstools2.client.command.command.GetTransactionInstancesCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetTransactionsForSimulatorCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimIdsForUserRequest;
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

    // remove this later
    public SimId getSimId() {
        return simId;
    }

    private SimId simId;
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
        List<String> names = new ArrayList<>();
        new GetSimIdForUser(){
            @Override
            public void onComplete(List<SimId> result) {
                simIds = result;
                for (SimId simId : result) {
                    names.add(simId.toString());
                }
                getView().displaySimulators(names);


                // Auto-load if there is only one entry
                if (result.size()==1) {
                    getView().selectSimulator(0);
                    simId = result.get(0);
                    loadTransactionTypes();
                }
            }
        }.run(new GetSimIdsForUserRequest(getCommandContext(), null));

    }

    // not sure why this level is useful. Maybe for filters?
    void loadTransactionTypes() {
        assert(simId != null);
        getView().transInstanceListBox.clear();
        //getSimulatorTransactionNames
        new GetTransactionsForSimulatorCommand(){
            @Override
            public void onComplete(List<String> result) {
                GWT.log("Loaded " + result.size() + " events");
                getView().transactionNamesPanel.clear();
                loadEventsForSimulator("all");
                // Auto-load if there is only one entry
                if (result.size()==1) {
                    GWT.log("Event is " + result.get(0));
                    getView().transInstanceListBox.setSelectedIndex(0);
                    getView().transactionInstanceSelected();
                }
            }
        }.run(new GetTransactionRequest(ClientUtils.INSTANCE.getCommandContext(),simId));
    }

    private void displayEvents(List<TransactionInstance> events) {
        List<EventInfo> eventInfos = new ArrayList<>();
        for (TransactionInstance ti : events) {
            eventInfos.add(new EventInfo(ti.messageId, ti.toString()));
        }
        getView().displayEvents(eventInfos);
    }

    private void loadEventsForSimulator() {
        loadEventsForSimulator("all");
    }

    void loadEventsForSimulator(String transNameFilter) {
        assert(simId != null);
        currentTransaction = transNameFilter;
        getView().clear();

        if ("all".equalsIgnoreCase(transNameFilter))
            transNameFilter = null;
        new GetTransactionInstancesCommand(){
            @Override
            public void onComplete(List<TransactionInstance> events) {
                displayEvents(events);
            }
        }.run(new GetTransactionRequest(getCommandContext(),simId,"",transNameFilter));
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

    void doUpdateChosenSimulator(String simName) {
        assert(simName != null);
        assert(!simName.equals(""));
        // translates to server version of simid
        simId = getServerSimId(new SimId(simName));
        loadEventsForSimulator();
    }

}
