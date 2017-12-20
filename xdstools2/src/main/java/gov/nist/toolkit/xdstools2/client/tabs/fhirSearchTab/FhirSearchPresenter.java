package gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.shared.Message;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;
import gov.nist.toolkit.xdstools2.client.command.command.*;
import gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab.MessageDisplay;
import gov.nist.toolkit.xdstools2.client.util.ASite;
import gov.nist.toolkit.xdstools2.client.util.AnnotatedItem;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.SiteFilter;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirReadRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.FhirSearchRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetDatasetElementContentRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetRawLogsRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class FhirSearchPresenter extends AbstractPresenter<FhirSearchView> {
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
                displayResult(result);
                loadTestLogs(result.logId);
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
//        codesSpec.put(resourceTypeNameLabel, asList(selectedResourceTypeName));

        new FhirSearchCommand() {

            @Override
            public void onComplete(List<Result> results) {
                Result result = results.get(0);
                displayResult(result);
                loadTestLogs(result.logId);
            }
        }.run(new FhirSearchRequest(getCommandContext(), new SiteSpec(selectedSite), selectedResourceTypeName, codesSpec));
    }

//    private void loadTestLogs(TestInstance testInstance) {
//		/*this.metadataInspectorTab.data.*/
//        new GetRawLogsCommand(){
//            @Override
//            public void onComplete(TestLogs testLogs) {
//                getView().setResourceDisplayPanel(testLogs.getTestLog(0).result);
//            }
//        }.run(new GetRawLogsRequest(ClientUtils.INSTANCE.getCommandContext(), testInstance));
//    }

    private void loadTestLogs(TestInstance testInstance) {
		/*this.metadataInspectorTab.data.*/
        new GetFhirResultCommand(){
            @Override
            public void onComplete(Message message) {
                getView().setContent(new MessageDisplay(message).asSinglePanel());
            }
        }.run(new GetRawLogsRequest(ClientUtils.INSTANCE.getCommandContext(), testInstance));
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

    private boolean isReadRunable() { return selectedSite != null ; }

    private boolean isSearchRunable() { return selectedSite != null && selectedResourceTypeName != null ; }
}
