package gov.nist.toolkit.xdstools2.client.util;

/**
 * An object name annotated with enabled status
 */
public class AnnotatedItem {
    private boolean enabled;
    private String name;

    public AnnotatedItem(boolean enabled, String name) {
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
