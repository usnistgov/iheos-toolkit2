package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import java.util.ArrayList;
import java.util.List;

import static gov.nist.toolkit.xdstools2.client.ToolWindow.toolkitService;

/**
 *
 */
public class RGSelectionPresenter {

    MultiSelectionView view;
    List<String> sites;

    public RGSelectionPresenter(/*ToolkitServiceAsync toolkitService, */final List<String> selected, final Panel panel) {
        toolkitService.getSiteNamesWithRG(new AsyncCallback<List<String>>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("getSiteNamesWithRG:" + caught.getMessage());
            }

            public void onSuccess(List<String> siteNames) {
                sites = siteNames;
                view = new MultiSelectionView();
                view.setData(siteNames);

                List<Integer> selectedRows = new ArrayList<>();
                for (String sel : selected) {
                    if (sites.contains(sel))
                        selectedRows.add(sites.indexOf(sel));
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
            selected.add(sites.get(row));
        }
        return selected;
    }
}
