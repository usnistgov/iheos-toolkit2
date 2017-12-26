package gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.xdstools2.client.command.command.FhirReadCommand;
import gov.nist.toolkit.xdstools2.client.command.command.FhirSearchCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetDatasetElementContentCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetTransactionOfferingsCommand;
import gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab.ILogger;
import gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab.ResultDisplay;
import gov.nist.toolkit.xdstools2.client.util.ASite;
import gov.nist.toolkit.xdstools2.client.util.AnnotatedItem;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.SiteFilter;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirReadRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirSearchRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetDatasetElementContentRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class FhirSearchPresenter extends AbstractPresenter<FhirSearchView> implements ILogger {
    private String selectedSite = null;
    private String selectedResourceTypeName = null;
    private DatasetElement selectedDatasetElement = null;

    private String resourceReference = null;
    private String patient = null;
    private String resourceTypeName = null;

    public FhirSearchPresenter() {
        super();
        GWT.log("Build SumbitResourcePresenter");
    }

    public FhirSearchPresenter getPresenter() {
        return this;
    }

    @Override
    public void init() {
        loadSystems();
        loadResourceTypes();

//        getView().getTabTopPanel().add

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
            }
        }.run(ClientUtils.INSTANCE.getCommandContext());
    }

    private void loadResourceTypes() {
        List<AnnotatedItem> resourceTypeNames = new ArrayList<>();
        resourceTypeNames.add(new AnnotatedItem(true, "DocumentReference"));
        resourceTypeNames.add(new AnnotatedItem(false, "DocumentManifest"));

        getView().setResourceTypeNames(resourceTypeNames);
    }

    @Override
    public void reveal() {
        loadSystems();
    }

    void doSetResourceReference(String ref) {
        resourceReference = ref;
        getView().setReadEnabled(isReadRunable());
    }

    void doSiteSelected(String siteName) {
        selectedSite = siteName;
        getView().setReadEnabled(isReadRunable());
        getView().setSearchEnabled(isSearchRunable());
    }

    void doResourceTypeSelected(String name) {
        selectedResourceTypeName = name;
        getView().setSearchEnabled(isSearchRunable());
    }

    void doResourceSelected(DatasetElement datasetElement) {
        selectedDatasetElement = datasetElement;
        getView().setReadEnabled(isReadRunable());

        new GetDatasetElementContentCommand() {

            @Override
            public void onComplete(String result) {
                getView().setContent(new HTML(result));
            }
        }.run(new GetDatasetElementContentRequest(ClientUtils.INSTANCE.getCommandContext(), selectedDatasetElement));
    }

    /**
     *
     * @param text system|value or id^^^&oid&ISO or Patient Resource URL
     */
    public void doSetPatientId(String text) {
        this.patient = text;
    }

    public void doSetResourceTypeName(String resourceTypeName) {
        this.resourceTypeName = resourceTypeName;
    }


    void doReadRun() {
        getView().clearLog();

        new FhirReadCommand() {

            @Override
            public void onComplete(List<Result> results) {
                Result result = results.get(0);
                ResultDisplay.display(result, getPresenter());
                ResponseLoader.load(result.logId, "Results", getView());
            }
        }.run(new FhirReadRequest(getCommandContext(), new SiteSpec(selectedSite), resourceReference));
    }

    final public static String resourceTypeNameLabel = "resourcetype";
    final public static String patientIdLabel = "patient.identifier";

    private List<String> asList(String value) {
        List<String> theList = new ArrayList<>();
        theList.add(value);
        return theList;
    }

    void doSearchRun() {
        getView().clearLog();

        Map<String, List<String>> codesSpec = new HashMap<>();
        codesSpec.put(patientIdLabel, asList(patient));

        new FhirSearchCommand() {

            @Override
            public void onComplete(List<Result> results) {
                Result result = results.get(0);
                ResultDisplay.display(result, getPresenter());
                ResponseLoader.load(result.logId, "Results", getView());
            }
        }.run(new FhirSearchRequest(getCommandContext(), new SiteSpec(selectedSite), selectedResourceTypeName, codesSpec));
    }



    private boolean isReadRunable() { return selectedSite != null ; }

    private boolean isSearchRunable() { return selectedSite != null && selectedResourceTypeName != null ; }

    @Override
    public void addLog(String content) {
        getView().addLog(content);
    }

    @Override
    public void addLog(Widget content) {
        getView().addLog(content);
    }
}
