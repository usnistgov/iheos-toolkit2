package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import gov.nist.toolkit.xdstools2.client.toolLauncher.ToolLauncher;

/**
 * TabPlace and its tokenizer are used for browser history and URL creation.
 *
 * The URL will be created as http://app-main-url#TestInstance:tabId
 * The different tabId are defined in {@link ToolLauncher}.
 *
 * Created by onh2 on 9/22/2014.
 */
public class TestInstance extends Place{
    public static class Tokenizer implements PlaceTokenizer<TestInstance> {

        @Override
        public TestInstance getPlace(String s) {
            return new TestInstance(s);
        }

        @Override
        public String getToken(TestInstance tabPlace) {
            return tabPlace.getTabId().toString();
        }
    }

    private String tabId;

    public TestInstance(String tabId){
        this.tabId=tabId;
    }

    public String getTabId(){
        return tabId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TestInstance) {
            return tabId.equals(((TestInstance) obj).tabId);
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

