package gov.nist.toolkit.desktop.client.environment;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.desktop.client.abstracts.AbstractView;
import gov.nist.toolkit.desktop.client.widgets.HorizontalFlowPanel;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class TestSessionView extends AbstractView<TestSessionPresenter> {
    private ListBox listBox = new ListBox();
    private TextBox textBox = new TextBox();
    private Button addButton = new Button("Add");
    private Button rmButton = new Button("Delete");
    private HorizontalFlowPanel panel = new HorizontalFlowPanel();

    @Override
    protected Map<String, Widget> getPathToWidgetsMap() {
        return null;
    }

    @Override
    protected Widget buildUI() {
        HTML label = new HTML("Test Session: ");
        panel.add(label);
        panel.add(listBox);
        panel.add(rmButton);
        panel.add(textBox);
        panel.add(addButton);
        return panel;
    }

    @Override
    protected void bindUI() {
        listBox.addChangeHandler(presenter);
    }

    void set(List<String> names) {
        listBox.clear();
        for (String name : names) {
            listBox.addItem(name);
        }
    }

    public void updateSelection(String value) {
        for (int i=0; i<listBox.getItemCount(); i++) {
            if (value.equals(listBox.getItemText(i))) {
                listBox.setSelectedIndex(i);
                return;
            }
        }
    }

    public String selection() {
        int selectionI = listBox.getSelectedIndex();
        return listBox.getItemText(selectionI);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

}
