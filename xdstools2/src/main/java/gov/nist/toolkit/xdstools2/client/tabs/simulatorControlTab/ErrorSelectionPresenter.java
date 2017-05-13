package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actortransaction.client.Severity;
import gov.nist.toolkit.xdstools2.client.command.command.GetTransactionErrorCodeRefsCommand;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTransactionErrorCodeRefsRequest;

import java.util.ArrayList;
import java.util.List;


/**
 * Uses SingleSelectionView to build specifics.
 */
public class ErrorSelectionPresenter {

    SelectionDisplay view;
    List<String> errorCodes;
    final static String none = "None";

    public ErrorSelectionPresenter(String transactionName, final List<String> selected, final Panel panel) {
        new GetTransactionErrorCodeRefsCommand(){
            @Override
            public void onComplete(List<String> results) {
                errorCodes = results;
                errorCodes.add(0, none);
                view = new SingleSelectionView();
                view.setData(errorCodes);

                List<Integer> selectedRows = new ArrayList<>();
                for (String sel : selected) {
                    if (errorCodes.contains(sel))
                        selectedRows.add(errorCodes.indexOf(sel));
                }
                view.setSelectedRows(selectedRows);

                panel.add(view.asWidget());
            }
        }.run(new GetTransactionErrorCodeRefsRequest(XdsTools2Presenter.data().getCommandContext(),transactionName,Severity.Error));
    }

    public List<String> getSelected() {
        List<String> selected = new ArrayList<>();
        for (int row : view.getSelectedRows()) {
            String code = errorCodes.get(row);
            if (!none.equals(code))
                selected.add(code);
        }
        return selected;
    }
}
