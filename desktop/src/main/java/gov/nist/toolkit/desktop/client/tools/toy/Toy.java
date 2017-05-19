package gov.nist.toolkit.desktop.client.tools.toy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 *
 */
public class Toy extends Place {
    private String name = null;

    public Toy() {
        super();
        GWT.log("Toy Place");
    }

    Toy(String toyname) {
        super();
        GWT.log("Toy Place: " + toyname);
        this.name = toyname;
    }


    public String getName() {
        return name;
    }


    public static class Tokenizer implements PlaceTokenizer<Toy> {
        @Override
        public Toy getPlace(String s) {
            return new Toy(s);
        }

        @Override
        public String getToken(Toy toyPlace) {
            return toyPlace.name;
        }
    }
}
