package gov.nist.toolkit.desktop.client.toolkit;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 *
 */
public interface ToolkitView extends IsWidget {
    public void setPresenter(Presenter presenter);
    public void setName(String name);

    // This is the Presenter that corresponds to this View
    public interface Presenter {
        void goTo(Place place);
    }
}
