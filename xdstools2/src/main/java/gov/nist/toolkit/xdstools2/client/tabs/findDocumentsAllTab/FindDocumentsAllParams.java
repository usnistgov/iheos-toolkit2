package gov.nist.toolkit.xdstools2.client.tabs.findDocumentsAllTab;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.AuthorFilter;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.CodeFilterBank;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.TimeFilter;

import java.util.List;
import java.util.Map;

/**
 * Created by Diane Azais local on 9/24/2015.
 *
 * Widget for capturing the parameters for the full FindDocuments (incl. all parameters) query. This does not include PatientID
 * since that is handled elsewhere.
 */
public class FindDocumentsAllParams {

    // container for the composite widget being built
    FlexTable paramGrid = new FlexTable();
    int prow = 0;

    TimeFilter creationTimeFromFilter;
    TimeFilter creationTimeToFilter;
    TimeFilter serviceStartTimeFromFilter;
    TimeFilter serviceStartTimeToFilter;
    TimeFilter serviceStopTimeFromFilter;
    TimeFilter serviceStopTimeToFilter;
    AuthorFilter authorFilter;
    CodeFilterBank codeFilterBank;

    public FindDocumentsAllParams(ToolkitServiceAsync toolkitService, GenericQueryTab genericQueryTab){
        // The collective filter bank being assembled
        codeFilterBank = new CodeFilterBank(toolkitService, genericQueryTab);

        // ------- Parameters to include in the search --------
        // Date parameters
        paramGrid.setText(prow, 0, "Select search parameters:");
        prow++;

        paramGrid.setText(prow, 1, "Creation Time, From:");
        creationTimeFromFilter = new TimeFilter("CreationTimeFrom");
        paramGrid.setWidget(prow, 2, creationTimeFromFilter.asWidget());

        paramGrid.setText(prow, 3, "To:");
        creationTimeToFilter = new TimeFilter("CreationTimeTo");
        paramGrid.setWidget(prow, 4, creationTimeToFilter.asWidget());
        prow++;

        paramGrid.setText(prow, 1, "Service Start Time, From:");
        serviceStartTimeFromFilter = new TimeFilter("ServiceStartTimeFrom");
        paramGrid.setWidget(prow, 2, serviceStartTimeFromFilter.asWidget());

        paramGrid.setText(prow, 3, "To:");
        serviceStartTimeToFilter = new TimeFilter("ServiceStartTimeTo");
        paramGrid.setWidget(prow, 4, serviceStartTimeToFilter.asWidget());
        prow++;

        paramGrid.setText(prow, 1, "Service Stop Time, From:");
        serviceStopTimeFromFilter = new TimeFilter("ServiceStopTimeFrom");
        paramGrid.setWidget(prow, 2, serviceStopTimeFromFilter.asWidget());

        paramGrid.setText(prow, 3, "To:");
        serviceStopTimeToFilter = new TimeFilter("ServiceStopTimeTo");
        paramGrid.setWidget(prow, 4, serviceStopTimeToFilter.asWidget());
        prow++;


        // Author
        authorFilter = new AuthorFilter(paramGrid, prow, 1, "Author Person", "AuthorPerson");
        prow++;


        // XDS Codes
        codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.DocumentEntryType);
        prow++;
        codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.DocumentEntryStatus);
        prow++;
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
        creationTimeFromFilter.addToCodeSpec(codeSpec, CodesConfiguration.CreationTimeFrom);
        creationTimeToFilter.addToCodeSpec(codeSpec, CodesConfiguration.CreationTimeTo);
        serviceStartTimeFromFilter.addToCodeSpec(codeSpec, CodesConfiguration.ServiceStartTimeFrom);
        serviceStartTimeToFilter.addToCodeSpec(codeSpec, CodesConfiguration.ServiceStartTimeTo);
        serviceStopTimeFromFilter.addToCodeSpec(codeSpec, CodesConfiguration.ServiceStopTimeFrom);
        serviceStopTimeToFilter.addToCodeSpec(codeSpec, CodesConfiguration.ServiceStopTimeTo);
        authorFilter.addToCodeSpec(codeSpec, CodesConfiguration.AuthorPerson);
        codeFilterBank.addToCodeSpec(codeSpec);
    }

    public Widget asWidget() { return paramGrid; }
}
