package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SingleSelectionView implements SelectionDisplay {
    FlowPanel namesPanel = new FlowPanel();
    List<RadioButton> selections = new ArrayList<>();
    String group = "MyGroup";

    public SingleSelectionView() {
    }

    @Override
    public void setData(List<String> data) {

        for (String value : data) {
            RadioButton cb = new RadioButton(getGroup(), value);
            selections.add(cb);
            namesPanel.add(cb);
        }
    }

    @Override
    public void setData(String group, List<String> data) {
        setGroup(group);
        setData(data);
    }

    @Override
    public List<Integer> getSelectedRows() {
        List<Integer> rows = new ArrayList<>();
        for (RadioButton box : selections) {
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
