package gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.command.command.GetAllCommand;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.shared.command.request.GetAllRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FhirSearchTab extends GenericQueryTab {

    static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
    static {
        transactionTypes.add(TransactionType.FHIR);
    }
    static CoupledTransactions couplings = new CoupledTransactions();

    GenericQueryTab genericQueryTab;
    FhirSearchParams qParams;


    public FhirSearchTab() {
        super(new FindDocumentsSiteActorManager());
    }

    @Override
    protected Widget buildUI() {
        // Panel1 to build inside of
        genericQueryTab = this;   // share with other methods

        FlowPanel container=new FlowPanel();

        // Tab contents starts here
        HTML title = new HTML();
        title.setHTML("<h2>FHIR Search</h2>");
        container.add(title);

        // Generate the composite widget that allows selection of all the GetAll query parameters. Below is the call
        // sqParams.asWidget() which gets the actual Widget.
        qParams = new FhirSearchParams(genericQueryTab);

        mainGrid = new FlexTable();  // this is important in some tabs, not this one.  This init should be moved to definition
        container.add(qParams.asWidget());

        container.add(mainGrid);
        return container;
    }

    @Override
    protected void bindUI() {

    }

    @Override
    protected void configureTabView() {
        // add below-the-line-stuff (PatientId, site selection etc.)
        // Also link in the Runner class (shown below) which is called when the user clicks on the Run button.
        // Since this call organizes the site selection grid, it needs the transactionTypes and couplings config
        addQueryBoilerplate(new FhirSearchTab.Runner(), transactionTypes, couplings, true);

    }

    class Runner implements ClickHandler {

        // Process the run button click
        public void onClick(ClickEvent event) {
            resultPanel.clear();

            if (!verifySiteProvided()) return;
            if (!verifyPidProvided()) return;

            // Capture the query-specific parameter details.  They have been generated in
            // sqParams and here they are formatted in the codeSpec layout which the server requires
            Map<String, List<String>> codeSpec = new HashMap<>();
            qParams.addToCodeSpec(codeSpec);

            // tell the server to run the query. The display is handled by GenericQueryTab which
            // is linked in via the queryCallback parameter
            rigForRunning();
            new GetAllCommand(){
                @Override
                public void onComplete(List<Result> result) {
                    queryCallback.onSuccess(result);
                }
            }.run(new GetAllRequest(getCommandContext(),getSiteSelection(),pidTextBox.getValue().trim(),codeSpec));
        }
    }


    @Override
    public String getWindowShortName() {
        return null;
    }
}
