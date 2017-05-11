package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.xdstools2.client.command.command.GetSiteNamesWithRGCommand;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class RGSelectionPresenter {

    MultiSelectionView view;
    List<String> sites;

    public RGSelectionPresenter(final List<String> selected, final Panel panel) {
        new GetSiteNamesWithRGCommand(){
            @Override
            public void onComplete(List<String> siteNames) {
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
        }.run(FrameworkInitialization.data().getCommandContext());
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
