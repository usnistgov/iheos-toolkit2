package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestdataSetListingCommand;
import gov.nist.toolkit.xdstools2.client.command.command.SubmitRegistryTestdataCommand;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestdataSetListingRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.SubmitTestdataRequest;

import java.util.ArrayList;
import java.util.List;

public class RegistryTestdataTab  extends GenericQueryTab {

    static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
    static {
        transactionTypes.add(TransactionType.REGISTER);
    }

    static CoupledTransactions couplings = new CoupledTransactions();

    ListBox testlistBox;

    String help = "Submit selected test data set to the selected Registry " +
            "in a Register transaction";

    public RegistryTestdataTab() {
        super(new GetDocumentsSiteActorManager());
    }

    @Override
    protected Widget buildUI() {
        FlowPanel flowPanel=new FlowPanel();
        flowPanel.add(new HTML("<h2>Send XDS Register transaction</h2>"));

        mainGrid = new FlexTable();
        int row = 0;

        flowPanel.add(mainGrid);

        HTML dataLabel = new HTML();
        dataLabel.setText("Select Test Data Set");
        mainGrid.setWidget(row,0, dataLabel);

        testlistBox = new ListBox();
        mainGrid.setWidget(row, 1, testlistBox);
        row++;

        testlistBox.setVisibleItemCount(1);
        return flowPanel;
    }

    @Override
    protected void bindUI() {
        new GetTestdataSetListingCommand(){
            @Override
            public void onComplete(List<String> result) {
                testlistBox.addItem("");
                for (String testName : result) {
                    testlistBox.addItem(testName);
                }
            }
        }.run(new GetTestdataSetListingRequest(getCommandContext(),"testdata-registry"));
    }

    @Override
    protected void configureTabView() {
        queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
    }

    class Runner implements ClickHandler {

        public void onClick(ClickEvent event) {
            resultPanel.clear();

            if (!verifySiteProvided()) return;
            if (!verifyPidProvided()) return;

            int selected = testlistBox.getSelectedIndex();
            if (selected < 1 || selected >= testlistBox.getItemCount()) {
                new PopupMessage("You must select Test Data Set first");
                return;
            }

            String testdataSetName = testlistBox.getItemText(selected);

            rigForRunning();
            new SubmitRegistryTestdataCommand(){
                @Override
                public void onComplete(List<Result> result) {
                    queryCallback.onSuccess(result);
                }
            }.run(new SubmitTestdataRequest(getCommandContext(),getSiteSelection(),testdataSetName,pidTextBox.getValue().trim()));
        }

    }



    public String getWindowShortName() {
        return "regtestdata";
    }

}
