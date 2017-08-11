package gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.xdstools2.client.command.command.FhirCreateCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetAllDatasetsCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetTransactionOfferingsCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.SiteFilter;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirCreateRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                List<TransactionType> transactionTypes = new ArrayList<>();
                transactionTypes.add(TransactionType.FHIR);

                List<ASite> sites = new SiteFilter(to)
                        .transactionTypesOnly(transactionTypes, false, true)
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
        new FhirCreateCommand() {
            @Override
            public void onComplete(List<Result> results) {
                Result result = results.get(0);
                getView().addLog("At " + result.getTimestamp());
                getView().addLog("Status is " + result.passed());
            }
        }.run(new FhirCreateRequest(getCommandContext(), new SiteSpec(selectedSite), selectedDatasetElement));
    }

    private boolean isRunable() { return selectedDatasetElement != null && selectedSite != null ; }
}
