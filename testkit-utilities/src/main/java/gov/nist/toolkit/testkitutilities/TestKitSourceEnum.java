package gov.nist.toolkit.testkitutilities;

/**
 * Created by skb1 on 7/5/2017.
 */
public enum TestKitSourceEnum {
    UNKNOWN,
    EMBEDDED,
    LOCAL;


    @Override
    public String toString() {
        switch (this) {
            case EMBEDDED: return "Embedded";
            case LOCAL: return "Local";
            default: return "Unknown";
        }
    }
}
