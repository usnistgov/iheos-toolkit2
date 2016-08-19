package gov.nist.toolkit.xdstools2.client.tabs.getAllTab;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.CodeFilterBank;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.OnDemandFilter;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.ReturnTypeFilter;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.StatusFilter;

import java.util.List;
import java.util.Map;

/**
 * Widget for capturing the parameters for the GetAll query. This does not include PatientID
 * since that is handled elsewhere.
 * Use codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.***Code); to add XDS codes as widgets
 */
public class GetAllParams {
    // container for the composite widget being built
    FlexTable paramGrid = new FlexTable();
    int prow = 0;

    // The query parameters are organized into three groups:
    //    What to include
    //       Approved?  Deprecated?  Both?
    //       Stable/OnDemand is through a separate filter - OnDemandFilter
    //    Filters to run this data through
    //       Restrict the return based on code selections
    //    What return format do you want
    //       LeafClass or ObjectRef

    StatusFilter deStatusFilter;
    StatusFilter ssStatusFilter;
    StatusFilter folStatusFilter;
    OnDemandFilter onDemandFilter;
    ReturnTypeFilter returnFilter;

    CodeFilterBank codeFilterBank;
    final int idHashCode = System.identityHashCode(this);

    public GetAllParams(/*ToolkitServiceAsync toolkitService, */GenericQueryTab genericQueryTab) {
        // The collective filter bank being assembled
        codeFilterBank = new CodeFilterBank(/*toolkitService, */genericQueryTab);

        // What to include
        paramGrid.setText(prow, 0, "Include:");
        prow++;

        paramGrid.setText(prow, 1, "DocumentEntries");
        deStatusFilter = new StatusFilter("DocumentEntries");
        paramGrid.setWidget(prow, 2, deStatusFilter.asWidget());
        prow++;

        paramGrid.setText(prow, 1, "");
        onDemandFilter = new OnDemandFilter("GetAllParams_"+idHashCode);
        paramGrid.setWidget(prow, 2, onDemandFilter.asWidget());
        prow++;

        paramGrid.setText(prow, 1, "Folders");
        folStatusFilter = new StatusFilter("Folders");
        paramGrid.setWidget(prow, 2, folStatusFilter.asWidget());
        prow++;

        paramGrid.setText(prow, 1, "SubmissionSets");
        ssStatusFilter = new StatusFilter("SubmissionSets");
        paramGrid.setWidget(prow, 2, ssStatusFilter.asWidget());
        prow++;

        // Filters to run this data through
        paramGrid.setText(prow, 0, "Filter by:");
        prow++;

        codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.FormatCode);
        prow++;
        codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.ConfidentialityCode);
        prow++;

        // What format to return
        paramGrid.setText(prow, 0, "Return");
        returnFilter = new ReturnTypeFilter("Return");
        paramGrid.setWidget(prow, 2, returnFilter.asWidget());

    }

    /**
     * Assemble all this detail into the codeSpec format for passing to the server
     * @param codeSpec includes the name of a parameter as a String and the acceptable values of that parameter
     *                 as a List<String>.
     */
    public void addToCodeSpec(Map<String, List<String>> codeSpec) {
        deStatusFilter.addToCodeSpec(codeSpec, CodesConfiguration.DocumentEntryStatus);
        folStatusFilter.addToCodeSpec(codeSpec, CodesConfiguration.FolderStatus);
        ssStatusFilter.addToCodeSpec(codeSpec, CodesConfiguration.SubmissionSetStatus);

        onDemandFilter.addToCodeSpec(codeSpec, CodesConfiguration.DocumentEntryType);
        returnFilter.addToCodeSpec(codeSpec, CodesConfiguration.ReturnsType);

        // Codes
        codeFilterBank.addToCodeSpec(codeSpec);
    }

    public Widget asWidget() { return paramGrid; }
}
