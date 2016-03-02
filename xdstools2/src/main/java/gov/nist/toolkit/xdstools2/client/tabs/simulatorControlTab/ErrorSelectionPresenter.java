package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actortransaction.client.Severity;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses SingleSelectionView to build specifics.
 */
public class ErrorSelectionPresenter {

    SelectionDisplay view;
    List<String> errorCodes;
    final static String none = "None";

    public ErrorSelectionPresenter(ToolkitServiceAsync toolkitService, String transactionName, final List<String> selected, final Panel panel) {
        toolkitService.getTransactionErrorCodeRefs(transactionName, Severity.Error, new AsyncCallback<List<String>>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("getTransactionErrorCodeRefs:" + caught.getMessage());
            }

            public void onSuccess(List<String> results) {
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

                bind();
                panel.add(view.asWidget());
            }
        });
    }

    void bind() {}

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
