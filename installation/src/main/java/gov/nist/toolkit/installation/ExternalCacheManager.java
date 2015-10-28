package gov.nist.toolkit.installation;

import java.io.File;

/**
 * Initialize External Cache
 */
public class ExternalCacheManager {
    synchronized public static void initialize(File location) {
        if (Installation.installation().externalCache() != null) return;
        if (!location.exists()) return;
        if (!location.isDirectory()) return;
        Installation.installation().externalCache(location);
    }

    synchronized public static void reinitialize(File location) {
        Installation.installation().externalCache(null);
        initialize(location);
    }

    public static void initialize() {
        File location = new File(Installation.installation().propertyServiceManager().getPropertyManager().getExternalCache());
        initialize(location);
    }
}
