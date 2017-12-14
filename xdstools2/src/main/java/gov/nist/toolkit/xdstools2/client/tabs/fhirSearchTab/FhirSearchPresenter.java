package gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.xdstools2.client.command.command.FhirReadCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetAllDatasetsCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetDatasetElementContentCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetTransactionOfferingsCommand;
import gov.nist.toolkit.xdstools2.client.util.ASite;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.SiteFilter;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirReadRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetDatasetElementContentRequest;

import java.util.List;

/**
 *
 */
public class FhirSearchPresenter extends AbstractPresenter<FhirSearchView> {
    private String selectedSite = null;
    private DatasetElement selectedDatasetElement = null;
    private String resourceReference = null;

    public FhirSearchPresenter() {
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

    void doSetResourceReference(String ref) {
        resourceReference = ref;
        getView().setRunEnabled(isRunable());
    }

    void doSiteSelected(String siteName) {
        selectedSite = siteName;
        getView().setRunEnabled(isRunable());
    }

    void doResourceSelected(DatasetElement datasetElement) {
        selectedDatasetElement = datasetElement;
        getView().setRunEnabled(isRunable());

        new GetDatasetElementContentCommand() {

            @Override
            public void onComplete(String result) {
                getView().setContent(new HTML(result));
            }
        }.run(new GetDatasetElementContentRequest(ClientUtils.INSTANCE.getCommandContext(), selectedDatasetElement));
    }

    void doRun() {
        getView().clearLog();

        new FhirReadCommand() {

            @Override
            public void onComplete(List<Result> results) {
                Result result = results.get(0);
                displayResult(result);
            }
        }.run(new FhirReadRequest(getCommandContext(), new SiteSpec(selectedSite), resourceReference));
    }

    private void displayResult(Result result) {
        getView().addLog("At " + result.getTimestamp());
        String prefix = "ReportBuilder: ";
        for (AssertionResult ar: result.assertions.assertions) {
            String content = ar.assertion;
            if (content.startsWith(prefix))
                content = content.substring(prefix.length());
            if (ar.status) {
                content = content.trim();
                if (content.startsWith("Ref =")) {
                    String link = content.substring("Ref =".length()).trim();
                    Anchor anchor = new Anchor();
                    anchor.setTarget("_blank");
                    anchor.setHref(link);
                    anchor.setText(link);
                    HorizontalFlowPanel fp = new HorizontalFlowPanel();
                    fp.add(new Label("Ref = "));
                    fp.add(anchor);
                    getView().addLog(fp);
                } else
                    getView().addLog(content);
            }
            else {
                Label l = new Label(content);
                l.setStyleName("testFail");
                getView().addLog(l);
            }
        }
    }

    private boolean isRunable() { return selectedSite != null ; }
}
