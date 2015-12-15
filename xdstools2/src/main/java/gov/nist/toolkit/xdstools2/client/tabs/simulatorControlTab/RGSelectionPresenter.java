package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RGSelectionPresenter {

    public interface Display extends HasValue<List<String>> {
        HasClickHandlers getList();
        void setData(List<String> data);
        int getClickedRow(ClickEvent event);
        List<Integer> getSelectedRows();
        void setSelectedRows(List<Integer> rows);
        Widget asWidget();
    }

    MultiSelectionView view;
    List<String> sites;

    public RGSelectionPresenter(ToolkitServiceAsync toolkitService, final List<String> selected, final Panel panel) {
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
