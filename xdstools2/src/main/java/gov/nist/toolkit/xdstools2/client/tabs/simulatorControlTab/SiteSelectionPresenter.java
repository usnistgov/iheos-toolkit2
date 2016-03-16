package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SiteSelectionPresenter {

    SelectionDisplay view;
    List<String> siteNames;
    final static String none = "None";

    /**
     * This constructor uses transaction type
     * @param toolkitService
     * @param transactionType
     * @param selected
     * @param panel
     */
    public SiteSelectionPresenter(ToolkitServiceAsync toolkitService, String transactionType, final List<String> selected, final Panel panel) {
        toolkitService.getSiteNamesByTranType(transactionType, new AsyncCallback<List<String>>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("getProfileErrorCodeRefs:" + caught.getMessage());
            }

            public void onSuccess(List<String> results) {
                siteNames = results;
                siteNames.add(0, none);
                view = new SingleSelectionView();
                view.setData(siteNames);

                List<Integer> selectedRows = new ArrayList<>();
                for (String sel : selected) {
                    if (siteNames.contains(sel))
                        selectedRows.add(siteNames.indexOf(sel));
                }
                view.setSelectedRows(selectedRows);

                bind();
                panel.add(view.asWidget());
            }
        });
    }

    /**
     * "Sorry, this method is not yet implemented."
     * @param toolkitService
     * @param selected
     * @param panel
     */
    public SiteSelectionPresenter(ToolkitServiceAsync toolkitService, final List<String> selected, final Panel panel) {
        //"Sorry, this method is not yet implemented.";
    }

    void bind() {}

    public List<String> getSelected() {
        List<String> selected = new ArrayList<>();
        for (int row : view.getSelectedRows()) {
            String code = siteNames.get(row);
            if (!none.equals(code))
                selected.add(code);
        }
        return selected;
    }
}
