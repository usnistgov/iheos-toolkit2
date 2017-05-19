package gov.nist.toolkit.desktop.client.environment;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.desktop.client.abstracts.AbstractView;
import gov.nist.toolkit.desktop.client.widgets.HorizontalFlowPanel;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class EnvironmentView extends AbstractView<EnvironmentPresenter> {
    private HorizontalFlowPanel environmentPanel = new HorizontalFlowPanel();
    private ListBox listBox = new ListBox();

    @Override
    protected Map<String, Widget> getPathToWidgetsMap() {
        return null;
    }

    @Override
    protected Widget buildUI() {
        HTML environmentLabel = new HTML();
        environmentLabel.setText("Environment: ");
        environmentPanel.add(environmentLabel);

        environmentPanel.add(listBox);

        return environmentPanel;
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
        return environmentPanel;
    }

}
