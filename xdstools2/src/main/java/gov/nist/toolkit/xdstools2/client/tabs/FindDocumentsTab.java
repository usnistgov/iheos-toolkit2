package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.user.client.ui.CheckBox;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.command.command.FindDocumentsCommand;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.AbstractTool;
import gov.nist.toolkit.xdstools2.shared.command.request.FindDocumentsRequest;

import java.util.ArrayList;
import java.util.List;

public class FindDocumentsTab extends AbstractTool {

    static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
    static {
        transactionTypes.add(TransactionType.STORED_QUERY);
        transactionTypes.add(TransactionType.IG_QUERY);
        transactionTypes.add(TransactionType.XC_QUERY);
    }

    static CoupledTransactions couplings = new CoupledTransactions();

    CheckBox selectOnDemand;
    private Pid patientId;
    private SiteSpec site;
    private boolean onDemand;

    @Override
    public void initTool() {
        int row = 0;

        selectOnDemand = new CheckBox();
        selectOnDemand.setText("Include On-Demand DocumentEntries");
        mainGrid.setWidget(row, 0, selectOnDemand);
        row++;

        requirePatientId();
        declareTransactionTypes(transactionTypes);
    }

    @Override
    protected void bindUI() {
//        addOnTabSelectionRedisplay();
    }

    @Override
    public String getWindowShortName() {
        return "finddocuments";
    }

    @Override
    public String getTabTitle() { return "FindDocs"; }

    @Override
    public String getToolTitle() { return "Find Documents Stored Query"; }

    /**
     * run as a utility from another tool
     * @param patientID
     * @param siteSpec
     * @param onDemand
     */
    public void run(Pid patientID, SiteSpec siteSpec, boolean onDemand) {
        this.patientId=patientID;
        this.site=siteSpec;
        this.onDemand=onDemand;
        new FindDocumentsCommand() {
            @Override
            public void onComplete(List<Result> results) {
                queryCallback.onSuccess(results);
                transactionSelectionManager.selectSite(site);
            }
        }.run(new FindDocumentsRequest(getCommandContext(), siteSpec, patientID.asString(), onDemand));
    }

    @Override
    public void run() {
        new FindDocumentsCommand(){
            @Override
            public void onComplete(List<Result> results) {
                queryCallback.onSuccess(results);
            }
        }.run(new FindDocumentsRequest(getCommandContext(),queryBoilerplate.getSiteSelection(), pidTextBox.getValue().trim(), selectOnDemand.getValue()));
    }
}
