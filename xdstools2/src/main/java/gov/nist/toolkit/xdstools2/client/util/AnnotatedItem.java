package gov.nist.toolkit.xdstools2.client.util;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotatedItem that = (AnnotatedItem) o;
        return enabled == that.enabled &&
                Objects.equals(name, that.name);
    }


    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "AnnotatedItem{" +
                "enabled=" + enabled +
                ", name='" + name + '\'' +
                '}';
    }
}
