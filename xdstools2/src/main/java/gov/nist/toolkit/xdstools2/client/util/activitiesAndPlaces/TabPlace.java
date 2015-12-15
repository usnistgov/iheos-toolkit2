package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * TabPlace and its tokenizer are used for browser history and URL creation.
 *
 * The URL will be created as http://app-main-url#TabPlace:tabId
 * The different tabId are defined in {@link gov.nist.toolkit.xdstools2.client.tabs.TabLauncher}.
 *
 * Created by onh2 on 9/22/2014.
 */
public class TabPlace extends Place{
    public static class Tokenizer implements PlaceTokenizer<TabPlace> {

        @Override
        public TabPlace getPlace(String s) {
            return new TabPlace(s);
        }

        @Override
        public String getToken(TabPlace tabPlace) {
            return tabPlace.getTabId().toString();
        }
    }

    private String tabId;

    public TabPlace(String tabId){
        this.tabId=tabId;
    }

    public String getTabId(){
        return tabId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TabPlace) {
            return tabId.equals(((TabPlace) obj).tabId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return tabId.hashCode();
    }

    @Override
    public String toString() {
        return "Place: "+this.getClass().getName() +
                ":'" + tabId +" loaded.";
    }
}

