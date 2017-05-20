package gov.nist.toolkit.desktop.client.home;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 *
 */
public class WelcomePlace extends Place {

    private String welcomeName;

    public WelcomePlace(String token) {
        this.welcomeName = token;
    }

    public WelcomePlace() {
        super();
    }

    public String getWelcomeName() {
        return this.welcomeName;
    }

    public static class Tokenizer implements PlaceTokenizer<WelcomePlace> {
        @Override
        public String getToken(WelcomePlace place) {
            return place.getWelcomeName();
        }

        @Override
        public WelcomePlace getPlace(String token) {
            return new WelcomePlace(token);
        }
    }

}