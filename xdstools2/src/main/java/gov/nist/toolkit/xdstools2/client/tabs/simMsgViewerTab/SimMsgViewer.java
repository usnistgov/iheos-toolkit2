package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 *
 */
public class SimMsgViewer extends Place {
    private String name = null;

    public SimMsgViewer() {
        super();
        GWT.log("SimMsgViewer Place");
        this.name = "SimMsgViewer";
    }

    public SimMsgViewer(String name) {
        super();
        GWT.log("SimMsgViewer Place: " + name);
        this.name = name;
    }

    // This is necessary!
    // The GWT place controller uses this to see if the newly requested place already exists.
    // If it exists it reuses it. Toolkit doesn't work this way. We use Places to represent
    // a tool in a tab and we want to be able to have multiple copies of a tool/tab running
    // at the same time. The easy way to accomplish this is to break the idea
    // of Place equality.  (Vote for equal rights for Places!)
    @Override
    public boolean equals(Object o) {
        return false;
    }

    public String getName() {
        return name;
    }


    public static class Tokenizer implements PlaceTokenizer<SimMsgViewer> {
        @Override
        public SimMsgViewer getPlace(String s) {
            return new SimMsgViewer(s);
        }

        @Override
        public String getToken(SimMsgViewer toyPlace) {
            return toyPlace.name;
        }
    }
}
