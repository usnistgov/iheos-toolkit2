package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MultiSelectionView implements SelectionDisplay {
    FlowPanel namesPanel = new FlowPanel();
    List<CheckBox> selections = new ArrayList<>();

    public MultiSelectionView() {
    }

    @Override
    public void setData(List<String> data) {
        for (String value : data) {
            CheckBox cb = new CheckBox(value);
            selections.add(cb);
            namesPanel.add(cb);
        }
    }

    @Override
    public List<Integer> getSelectedRows() {
        List<Integer> rows = new ArrayList<>();
        for (CheckBox box : selections) {
            if (box.getValue())
                rows.add(selections.indexOf(box));
        }
        return rows;
    }

    @Override
    public void setSelectedRows(List<Integer> rows) {
        for (Integer row : rows) {
            selections.get(row).setValue(true);
        }
    }

    @Override
    public Widget asWidget() {
        return namesPanel;
    }

    @Override
    public List<String> getValue() {
        return null;
    }

    @Override
    public void setValue(List<String> strings) {

    }

    @Override
    public void setValue(List<String> strings, boolean b) {

    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<String>> valueChangeHandler) {
        return null;
    }

    @Override
    public void fireEvent(GwtEvent<?> gwtEvent) {

    }

}
