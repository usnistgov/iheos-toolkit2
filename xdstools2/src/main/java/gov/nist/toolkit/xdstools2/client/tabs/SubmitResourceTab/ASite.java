package gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab;

/**
 *
 */
public class ASite {
    private boolean enabled;
    private String name;

    public ASite(boolean enabled, String name) {
        this.enabled = enabled;
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }
}
