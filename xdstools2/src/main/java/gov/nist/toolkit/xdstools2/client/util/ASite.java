package gov.nist.toolkit.xdstools2.client.util;

import gov.nist.toolkit.simcommon.client.SimId;

import java.util.ArrayList;
import java.util.List;

/**
 * A Site name annotated with enabled status
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
