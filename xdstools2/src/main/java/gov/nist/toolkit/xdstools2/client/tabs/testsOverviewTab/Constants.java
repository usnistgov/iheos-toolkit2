package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

/**
 * Created by Diane Azais local on 11/27/2015.
 */
public class Constants {

    public enum RowColor {
        WHITE, YELLOW, GREEN, RED;
        private static RowColor[] vals = values();


        public static String getTestRowCss(RowColor color) {

            switch (color) {
                case WHITE:
                    return "test-row-white";
                case YELLOW:
                    return "test-row-yellow";
                case GREEN:
                    return "test-row-green";
                case RED:
                    return "test-row-red";
                default:
                    return "test-row-white";
            }
        }

        // Iterator on colors
        public RowColor next() {
            return vals[(this.ordinal()+1) % vals.length];
        }
    }

    public enum Status {
        NOT_RUN, RUN_WITH_WARNINGS, PASSED, FAILED
    }
}

