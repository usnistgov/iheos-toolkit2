package gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.xdstools2.client.command.command.*;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab.ResponseLoader;
import gov.nist.toolkit.xdstools2.client.util.ASite;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.SiteFilter;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirCreateRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirTransactionRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetDatasetElementContentRequest;

import java.util.List;

/**
 *
 */
public class SubmitResourcePresenter extends AbstractPresenter<SubmitResourceView> implements ILogger {
    private String selectedSite = null;
    private DatasetElement selectedDatasetElement = null;

    public SubmitResourcePresenter() {
        super();
        GWT.log("Build SumbitResourcePresenter");
    }

    private SubmitResourcePresenter getPresenter() {
        return this;
    }

    @Override
    public void reveal() {
        loadSystems();
    }

    @Override
    public void init() {

        ClientUtils.INSTANCE.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
            @Override
            public void onTestSessionChanged(TestSessionChangedEvent event) {
                if (event.getChangeType() == TestSessionChangedEvent.ChangeType.SELECT) {
                    getView().getSystemSelector().clearSelection();
                    loadSystems();
                    getView().lateBindUI();
                }
            }
        });

        new GetAllDatasetsCommand() {
            @Override
            public void onComplete(List<DatasetModel> result) {
                getView().setData(result);
            }
        }.run(getCommandContext());

        loadSystems();
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
                    ResultDisplay.display(result, getPresenter());
                    ResponseLoader.load(result.logId, "Results", getView());
                }
            }.run(new FhirTransactionRequest(getCommandContext(), new SiteSpec(selectedSite, ClientUtils.INSTANCE.getCurrentTestSession()), selectedDatasetElement));
        } else {
            new FhirCreateCommand() {
                @Override
                public void onComplete(List<Result> results) {
                    Result result = results.get(0);
                    ResultDisplay.display(result, getPresenter());
                    ResponseLoader.load(result.logId, "Results", getView());
                }
            }.run(new FhirCreateRequest(getCommandContext(), new SiteSpec(selectedSite, ClientUtils.INSTANCE.getCurrentTestSession()), selectedDatasetElement));
        }
    }


    private boolean isRunable() { return selectedDatasetElement != null && selectedSite != null ; }

    @Override
    public void addLog(String content) {
        getView().addLog(content);
    }

    @Override
    public void addLog(Widget content) {
        getView().addLog(content);
    }
}
