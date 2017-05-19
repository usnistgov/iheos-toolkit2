package gov.nist.toolkit.desktop.client.tools.toy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 *
 */
public class Toy extends Place {

    public Toy() {
        super();
        GWT.log("Toy Place");
    }

    public Toy(String toyname) {
        super();
        GWT.log("Toy Place: " + toyname);
    }


    public String getPlaceName() {
        return "Toy";
    }


    public static class Tokenizer implements PlaceTokenizer<Toy> {
        @Override
        public Toy getPlace(String s) {
            return new Toy(s);
        }

        @Override
        public String getToken(Toy toyPlace) {
            return "";
        }
    }
}
