package gov.nist.toolkit.xdstools2.client.toolLauncher;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.util.ClientFactoryImpl;

/**
 *
 */
public class NewToolLauncher {

    private PlaceController placeController = new ClientFactoryImpl().getPlaceController();

    public ToolWindow launch(Place place) {
        assert(placeController != null);
        placeController.goTo(place);
        return null;  // This new style must adapt to this!
    }
}
