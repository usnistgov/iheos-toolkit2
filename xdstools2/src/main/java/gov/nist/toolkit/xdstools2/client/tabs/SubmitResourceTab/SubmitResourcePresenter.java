package gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab;

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
import gov.nist.toolkit.xdstools2.client.command.command.*;
import gov.nist.toolkit.xdstools2.client.util.ASite;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.SiteFilter;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirCreateRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirTransactionRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetDatasetElementContentRequest;

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
    public void reveal() {
        loadSystems();
    }

    @Override
    public void init() {

        new GetAllDatasetsCommand() {
            @Override
            public void onComplete(List<DatasetModel> result) {
                getView().setData(result);
            }
        }.run(getCommandContext());

        loadSystems();
//        new GetTransactionOfferingsCommand() {
//            @Override
//            public void onComplete(TransactionOfferings to) {
//                List<TransactionType> transactionTypes = TransactionType.asList();
////                transactionTypes.add(TransactionType.FHIR);
//
//                List<ASite> sites = new SiteFilter(to)
//                  //      .transactionTypesOnly(transactionTypes, false, true)
//                        .fhirOnly(transactionTypes)
//                        .sorted();
//
//                getView().setSiteNames(sites);
//            }
//        }.run(ClientUtils.INSTANCE.getCommandContext());

        getView().lateBindUI();
    }

    private void loadSystems() {
        new GetTransactionOfferingsCommand() {
            @Override
            public void onComplete(TransactionOfferings to) {
                List<TransactionType> transactionTypes = TransactionType.asList();

                List<ASite> sites = new SiteFilter(to)
                        .fhirOnly(transactionTypes)
                        .sorted();

                getView().setSiteNames(sites);
                GWT.log("Systems reloaded");
                updateWithCurrentSelection();
            }
        }.run(ClientUtils.INSTANCE.getCommandContext());
    }


    void doSiteSelected(String siteName) {
        selectedSite = siteName;
        getView().setRunEnabled(isRunable());
    }

    void updateWithCurrentSelection() {
        String currentSelection = getView().getSystemSelector().getCurrentSelection();
        if (currentSelection!=null) {
            getView().getSystemSelector().updateSiteSelectedView(currentSelection);
        }
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

    private boolean isRunable() { return selectedDatasetElement != null && selectedSite != null ; }
}
