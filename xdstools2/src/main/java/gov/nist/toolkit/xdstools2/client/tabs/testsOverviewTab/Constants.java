package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

/**
 * Created by Diane Azais local on 11/27/2015.
 */
public class Constants {

    public static enum RowColor {
        LIGHT_BLUE, DARK_BLUE;
        private static RowColor[] vals = values();


        public static String getTestRowCss(RowColor color) {

            switch (color) {
                case LIGHT_BLUE:
                    return "test-row-lightblue";
                case DARK_BLUE:
                    return "test-row-darkblue";
                default:
                    return "test-row-lightblue";
            }
        }

        // Iterator on colors
        public RowColor next() {
            return vals[(this.ordinal()+1) % vals.length];
        }

    }
}

