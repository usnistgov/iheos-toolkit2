package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Builds a widget that displays current Patient ID favorites.  Coordinates
 * with PidManager (who holds them) and GenericQueryTab which displays/uses them.
 */
public class PidFavorites {
    VerticalPanel panel = new VerticalPanel();




    public Widget asWidget() { return panel; }
}
