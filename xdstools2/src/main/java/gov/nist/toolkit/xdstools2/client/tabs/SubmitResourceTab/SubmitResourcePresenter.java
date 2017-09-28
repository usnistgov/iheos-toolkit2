package gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.xdstools2.client.command.command.FhirCreateCommand;
import gov.nist.toolkit.xdstools2.client.command.command.FhirTransactionCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetAllDatasetsCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetTransactionOfferingsCommand;
import gov.nist.toolkit.xdstools2.client.util.ASite;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.SiteFilter;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirCreateRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirTransactionRequest;

import java.util.List;

/**
 *
 */
public class SubmitResourcePresenter extends AbstractPresenter<SubmitResourceView> {
    private String selectedSite = null;
    private DatasetElement selectedDatasetElement = null;

    public SubmitResourcePresenter() {
        super();
        GWT.log("Build SumbitResourcePresenter");
    }

    @Override
    public void init() {

        new GetAllDatasetsCommand() {
            @Override
            public void onComplete(List<DatasetModel> result) {
                getView().setData(result);
            }
        }.run(getCommandContext());

        new GetTransactionOfferingsCommand() {
            @Override
            public void onComplete(TransactionOfferings to) {
                List<TransactionType> transactionTypes = TransactionType.asList();
//                transactionTypes.add(TransactionType.FHIR);

                List<ASite> sites = new SiteFilter(to)
                  //      .transactionTypesOnly(transactionTypes, false, true)
                        .fhirOnly(transactionTypes)
                        .sorted();

                getView().setSiteNames(sites);
            }
        }.run(ClientUtils.INSTANCE.getCommandContext());

        getView().lateBindUI();
    }

    void doSiteSelected(String siteName) {
        selectedSite = siteName;
        getView().setRunEnabled(isRunable());
    }

    void doResourceSelected(DatasetElement datasetElement) {
        selectedDatasetElement = datasetElement;
        getView().setRunEnabled(isRunable());
    }

    void doRun() {
        getView().clearLog();
        if (selectedDatasetElement.getType().equals("pdb")) {
            new FhirTransactionCommand() {
                @Override
                public void onComplete(List<Result> results) {
                    Result result = results.get(0);
                    displayResult(result);
                }
            }.run(new FhirTransactionRequest(getCommandContext(), new SiteSpec(selectedSite), selectedDatasetElement));
        } else {
            new FhirCreateCommand() {
                @Override
                public void onComplete(List<Result> results) {
                    Result result = results.get(0);
                    displayResult(result);
                }
            }.run(new FhirCreateRequest(getCommandContext(), new SiteSpec(selectedSite), selectedDatasetElement));
        }
    }

    private void displayResult(Result result) {
        getView().addLog("At " + result.getTimestamp());
        for (AssertionResult ar: result.assertions.assertions) {
            if (ar.status)
                getView().addLog(ar.assertion);
            else {
                Label l = new Label(ar.assertion);
                l.setStyleName("testFail");
                getView().addLog(l);
            }
        }
    }

    private boolean isRunable() { return selectedDatasetElement != null && selectedSite != null ; }
}
