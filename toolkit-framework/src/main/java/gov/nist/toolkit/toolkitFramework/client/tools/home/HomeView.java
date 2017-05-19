package gov.nist.toolkit.toolkitFramework.client.tools.home;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.toolkitFramework.client.mvp.AbstractView;

import java.util.Map;

/**
 *
 */
public class HomeView extends AbstractView<HomePresenter> {
    private Label myLabel = new Label("No place like home");

    @Override
    protected Map<String, Widget> getPathToWidgetsMap() {
        return null;
    }

    @Override
    protected Widget buildUI() {
        return myLabel;
    }

    @Override
    protected void bindUI() {

    }
}
