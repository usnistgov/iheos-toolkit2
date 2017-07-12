package gov.nist.toolkit.desktop.client.tools.getDocuments;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.desktop.client.commands.GetDocumentsCommand;
import gov.nist.toolkit.desktop.client.legacy.CoupledTransactions;
import gov.nist.toolkit.desktop.client.legacy.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.desktop.client.legacy.widgets.PopupMessage;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.server.shared.command.request.GetDocumentsRequest;

import java.util.ArrayList;
import java.util.List;

public class GetDocumentsTab
//        extends AbstractTab
        extends GenericQueryTab
{

    GenericQueryTab gqt;
    // The TransactionTypes to list as actor categories
    private static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
    static {
        transactionTypes.add(TransactionType.STORED_QUERY);
        transactionTypes.add(TransactionType.IG_QUERY);
        transactionTypes.add(TransactionType.XC_QUERY);
    }

    private static CoupledTransactions couplings = new CoupledTransactions();
    static {
        // If an Initiating Gateway is selected (IG_QUERY) then
        // a Responding Gateway (XC_QUERY) must also be selected
        // to determine the homeCommunityId to put in the
        // query request to be sent to the Initiating Gateway
        couplings.add(TransactionType.IG_QUERY, TransactionType.XC_QUERY);
    }

    private TextArea textArea;
    GetDocumentsTab tab;
    String help ="Retrieve full metadata for list of DocumentEntry UUIDs. " +
            "UUIDs can be separated by any of [,;() \\t\\n\\r']";


    public GetDocumentsTab() {
//        super(new GetDocumentsSiteActorManager());
    }

//    @Override
    public Widget buildUI() {
        FlowPanel panel = getTabTopPanel();
        panel.add(new HTML("<h2>Get Documents</h2>"));

        mainGrid = new FlexTable();

        panel.add(mainGrid);

        mainGrid.setWidget(0,0, new HTML("Document Entry UUIDs or UIDs"));

        textArea = new TextArea();
        textArea.setCharacterWidth(40);
        textArea.setVisibleLines(10);
        mainGrid.setWidget(0, 1, textArea);

        return panel;
    }

//    @Override
    public void bindUI() {
    }

//    @Override
    protected void configureTabView() {
        queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings);
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
            new GetDocumentsCommand(){
                @Override
                public void onFailure(Throwable caught){
                    queryCallback.onFailure(caught);
                }

                @Override
                public void onComplete(List<Result> result) {
                    queryCallback.onSuccess(result);
                }
            }.run(new GetDocumentsRequest(getCommandContext(),getSiteSelection(),getAnyIds(values)));
        }

    }

    public String getWindowShortName() {
        return "getdocuments";
    }



}
