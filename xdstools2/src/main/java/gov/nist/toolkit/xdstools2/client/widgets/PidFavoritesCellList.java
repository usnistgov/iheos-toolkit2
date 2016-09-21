package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.xdstools2.client.util.CookiesServices;

import java.util.LinkedList;

/**
 * Created by onh2 on 7/11/16.
 */
public class PidFavoritesCellList extends Composite{
    private SimplePanel container = new SimplePanel();
    // List widget.
    private CellList<Pid> cellList;
    // list data model.
    private ListDataProvider<Pid> model = new ListDataProvider<Pid>();
    // Selection model.
    private SingleSelectionModel<Pid> selectionModel = new SingleSelectionModel<Pid>();

    // Key provider to id each Pid in the list.
    private static final ProvidesKey<Pid> KEY_PROVIDER = new ProvidesKey<Pid>() {
        @Override
        public String getKey(Pid pid) {
            return pid.toString();
        }
    };

//    private EventBus eventBus = ((ClientFactory) GWT.create(ClientFactory.class)).getEventBus();

    public PidFavoritesCellList(){
        // Create a Cell renderer.
        PidCell pidCell = new PidCell();

        // Set a key provider that provides a unique key for each pid. If key is
        // used to identify pid.
        cellList = new CellList<Pid>(pidCell,KEY_PROVIDER);

        // this links the data model with the actual table widget
        model.addDataDisplay(cellList);
        //        cellList.setPageSize(30);
        //        cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
                cellList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.BOUND_TO_SELECTION);

        // Add a selection model so we can select cells.
        cellList.setSelectionModel(selectionModel);

        container.add(cellList);

        model.setList(new LinkedList<Pid>(CookiesServices.retrievePidFavoritesFromCookies()));

        model.refresh();
        cellList.redraw();

        container.addStyleName("list-border");

        initWidget(container);
    }

    public void addSelectionChangeHandler(SelectionChangeEvent.Handler handler){
        selectionModel.addSelectionChangeHandler(handler);
    }

    public Pid getSelectedPid(){
        return selectionModel.getSelectedObject();
    }

    public void clear() {
        selectionModel.clear();
    }

    /**
     * The Cell used to render a {@link Pid}.
     */
    static class PidCell extends AbstractCell<Pid> {

        public PidCell() {
        }

        @Override
        public void render(Context context, Pid pid, SafeHtmlBuilder safeHtmlBuilder) {
            // Value can be null, so do a null check..
            if (pid == null) {
                return;
            }

            safeHtmlBuilder.appendHtmlConstant("<table>");

            safeHtmlBuilder.appendHtmlConstant("<tr><td><span style='font-size:75%;'>");
            safeHtmlBuilder.appendEscaped(pid.toString());
            safeHtmlBuilder.appendHtmlConstant("</span>");
            // Add the name.
            if (!pid.getExtra().equals("")) {
                safeHtmlBuilder.appendHtmlConstant(" - ");
                safeHtmlBuilder.appendHtmlConstant("<span style='font-weight: bold;'>");
                safeHtmlBuilder.appendEscaped(pid.getExtra());
                safeHtmlBuilder.appendHtmlConstant("</span>");
            }
            safeHtmlBuilder.appendHtmlConstant("</td></tr></table>");
        }
    }


}
