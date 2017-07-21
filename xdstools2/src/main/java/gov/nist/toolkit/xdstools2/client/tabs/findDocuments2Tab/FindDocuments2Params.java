package gov.nist.toolkit.xdstools2.client.tabs.findDocuments2Tab;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Diane Azais local on 9/24/2015.
 *
 * Widget for capturing the parameters for the full FindDocuments (incl. all parameters) query. This does not include
 * the PatientID which is handled elsewhere.
 */
public class FindDocuments2Params {

    // container for the composite widget being built
    FlexTable paramGrid = new FlexTable();
    int prow = 0;

    StatusFilter deStatusFilter;
    OnDemandFilter onDemandFilter;
    TimeFilter creationTimeFromFilter;
    TimeFilter creationTimeToFilter;
    TimeFilter serviceStartTimeFromFilter;
    TimeFilter serviceStartTimeToFilter;
    TimeFilter serviceStopTimeFromFilter;
    TimeFilter serviceStopTimeToFilter;
    AuthorFilter authorFilter;
    CodeFilterBank codeFilterBank;
    ReturnTypeFilter returnFilter;
    Label errorLabel;

    public FindDocuments2Params(/*ToolkitServiceAsync toolkitService, */GenericQueryTab genericQueryTab){
        // The collective filter bank being assembled
        codeFilterBank = new CodeFilterBank(/*toolkitService, */genericQueryTab);

        errorLabel = new Label();

        // ------- Parameters to include in the search --------
        paramGrid.setText(prow, 0, "Select search parameters:");
        prow++;

        // DocumentEntry Status
        paramGrid.setText(prow, 1, "DocumentEntry Status");
        deStatusFilter = new StatusFilter("DocumentEntries");
        paramGrid.setWidget(prow, 2, deStatusFilter.asWidget());
        prow++;

        // On Demand
        paramGrid.setText(prow, 1, "DocumentEntry Type");
        onDemandFilter = new OnDemandFilter("Type");
        paramGrid.setWidget(prow, 2, onDemandFilter.asWidget());
        prow++;

        // What format to return
        paramGrid.setText(prow, 1, "Return");
        returnFilter = new ReturnTypeFilter("Return");
        paramGrid.setWidget(prow, 2, returnFilter.asWidget());
        prow++;


        // Date parameters
        paramGrid.setText(prow, 1, "Creation Time, From:");
        creationTimeFromFilter = new TimeFilter(errorLabel, "CreationTimeFrom");
        paramGrid.setWidget(prow, 2, creationTimeFromFilter.asWidget());

        paramGrid.setText(prow, 3, "To:");
        creationTimeToFilter = new TimeFilter(errorLabel, "CreationTimeTo");
        paramGrid.setWidget(prow, 4, creationTimeToFilter.asWidget());

        // TODO manage error display on UI
        paramGrid.setWidget(prow, 5, errorLabel);
        prow++;

        paramGrid.setText(prow, 1, "Service Start Time, From:");
        serviceStartTimeFromFilter = new TimeFilter(errorLabel, "ServiceStartTimeFrom");
        paramGrid.setWidget(prow, 2, serviceStartTimeFromFilter.asWidget());

        paramGrid.setText(prow, 3, "To:");
        serviceStartTimeToFilter = new TimeFilter(errorLabel, "ServiceStartTimeTo");
        paramGrid.setWidget(prow, 4, serviceStartTimeToFilter.asWidget());
        prow++;

        paramGrid.setText(prow, 1, "Service Stop Time, From:");
        serviceStopTimeFromFilter = new TimeFilter(errorLabel, "ServiceStopTimeFrom");
        paramGrid.setWidget(prow, 2, serviceStopTimeFromFilter.asWidget());

        paramGrid.setText(prow, 3, "To:");
        serviceStopTimeToFilter = new TimeFilter(errorLabel, "ServiceStopTimeTo");
        paramGrid.setWidget(prow, 4, serviceStopTimeToFilter.asWidget());
        prow++;


        // Author
        authorFilter = new AuthorFilter(paramGrid, prow, 1, "Author Person", "AuthorPerson");
        prow++;


        // XDS Codes
        codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.ClassCode);
        prow++;
        codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.TypeCode);
        prow++;
        codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.FormatCode);
        prow++;
        codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.HealthcareFacilityTypeCode);
        prow++;
        codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.PracticeSettingCode);
        prow++;
        codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.ConfidentialityCode);
        prow++;
        codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.EventCodeList);
        prow++;
    }

    /**
     * Assemble all the parameters into the codeSpec format for passing to the server. Called from inside the related tab.
     * @param codeSpec includes the name of a parameter as a String and the acceptable values of that parameter
     *                 as a List<String>.
     */
    public void addToCodeSpec(Map<String, List<String>> codeSpec) {
        deStatusFilter.addToCodeSpec(codeSpec, CodesConfiguration.DocumentEntryStatus);
        creationTimeFromFilter.addToCodeSpec(codeSpec, CodesConfiguration.CreationTimeFrom);
        creationTimeToFilter.addToCodeSpec(codeSpec, CodesConfiguration.CreationTimeTo);
        serviceStartTimeFromFilter.addToCodeSpec(codeSpec, CodesConfiguration.ServiceStartTimeFrom);
        serviceStartTimeToFilter.addToCodeSpec(codeSpec, CodesConfiguration.ServiceStartTimeTo);
        serviceStopTimeFromFilter.addToCodeSpec(codeSpec, CodesConfiguration.ServiceStopTimeFrom);
        serviceStopTimeToFilter.addToCodeSpec(codeSpec, CodesConfiguration.ServiceStopTimeTo);
        authorFilter.addToCodeSpec(codeSpec, CodesConfiguration.AuthorPerson);
        onDemandFilter.addToCodeSpec(codeSpec, CodesConfiguration.DocumentEntryType);
        returnFilter.addToCodeSpec(codeSpec, CodesConfiguration.ReturnsType);
        codeFilterBank.addToCodeSpec(codeSpec);
    }

    public Widget asWidget() { return paramGrid; }
}
