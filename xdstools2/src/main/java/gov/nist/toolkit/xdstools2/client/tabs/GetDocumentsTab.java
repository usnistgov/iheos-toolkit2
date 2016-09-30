package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.event.TabSelectedEvent;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.ArrayList;
import java.util.List;

public class GetDocumentsTab  extends GenericQueryTab {
    // The TransactionTypes to list as actor categories
    static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
    static {
        transactionTypes.add(TransactionType.STORED_QUERY);
        transactionTypes.add(TransactionType.IG_QUERY);
        transactionTypes.add(TransactionType.XC_QUERY);
    }

    static CoupledTransactions couplings = new CoupledTransactions();
    static {
        // If an Initiating Gateway is selected (IG_QUERY) then
        // a Responding Gateway (XC_QUERY) must also be selected
        // to determine the homeCommunityId to put in the
        // query request to be sent to the Initiating Gateway
        couplings.add(TransactionType.IG_QUERY, TransactionType.XC_QUERY);
    }

    TextArea textArea;
    GetDocumentsTab tab;
    String help ="Retrieve full metadata for list of DocumentEntry UUIDs. " +
            "UUIDs can be separated by any of [,;() \\t\\n\\r']";

    public GetDocumentsTab() {
        super(new GetDocumentsSiteActorManager());
    }

    @Override
    protected Widget buildUI() {
        FlowPanel panel=new FlowPanel();
        tabTopPanel.add(new HTML("<h2>Get Documents</h2>"));

        mainGrid = new FlexTable();
        int row = 0;

        panel.add(mainGrid);

        mainGrid.setWidget(row,0, new HTML("Document Entry UUIDs or UIDs"));

        textArea = new TextArea();
        textArea.setCharacterWidth(40);
        textArea.setVisibleLines(10);
        mainGrid.setWidget(row, 1, textArea);
        row++;

        return panel;
    }

    @Override
    protected void bindUI() {
        ((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).addTabSelectedEventHandler(new TabSelectedEvent.TabSelectedEventHandler() {
            @Override
            public void onTabSelection(TabSelectedEvent event) {
                redisplay(true);
            }
        });
    }

    @Override
    protected void configureTabView() {
        queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings, false);
    }

    class Runner implements ClickHandler {

        public void onClick(ClickEvent event) {
            resultPanel.clear();

            if (!verifySiteProvided()) return;

            List<String> values = formatIds(textArea.getValue());

            if (!verifyUuids(values)) {
                new PopupMessage("All values must be a UUID (have urn:uuid: prefix) or be UIDs (not have urn:uuid: prefix)");
                return;
            }

            String errMsg = transactionSelectionManager.verifySelection();
            if (errMsg != null) {
                new PopupMessage(errMsg);
                return;
            }

            rigForRunning();
            getToolkitServices().getDocuments(getSiteSelection(), getAnyIds(values), queryCallback);
        }

    }

    public String getWindowShortName() {
        return "getdocuments";
    }



}
