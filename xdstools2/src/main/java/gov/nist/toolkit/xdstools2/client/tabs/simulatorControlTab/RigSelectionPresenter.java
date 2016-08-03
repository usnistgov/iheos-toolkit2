/**
 * 
 */
package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.ArrayList;
import java.util.List;

/**
 * Load RIG actors for selection by IIG actor
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class RigSelectionPresenter {

   MultiSelectionView view;
   List<String> sites;

   public RigSelectionPresenter(ToolkitServiceAsync toolkitService, final List<String> selected, final Panel panel) {
       try {
           toolkitService.getSiteNamesWithRIG(new AsyncCallback<List<String>>() {

               public void onFailure(Throwable caught) {
                   new PopupMessage("getSiteNamesWithRIG:" + caught.getMessage());
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
       } catch (Exception e) {
           new PopupMessage("getSiteNamesWithRIG:" + e.getMessage());
       }
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