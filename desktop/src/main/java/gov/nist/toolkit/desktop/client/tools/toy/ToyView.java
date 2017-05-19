package gov.nist.toolkit.desktop.client.tools.toy;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.desktop.client.abstracts.AbstractView;

import java.util.Map;

/**
 *
 */
public class ToyView extends AbstractView<ToyPressnter> {
    @Override
    protected Map<String, Widget> getPathToWidgetsMap() {
        return null;
    }

    @Override
    protected Widget buildUI() {
        SimpleLayoutPanel panel = new SimpleLayoutPanel();
        presenter.myIndex = presenter.counter++;
        Label label = new Label("Toy " + presenter.myIndex + " " + presenter.name);
        panel.add(label);
        return label;
    }

    @Override
    protected void bindUI() {

    }
}
