package gov.nist.toolkit.installation.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manage configuration properties.
 * These control the services available and are not generally user
 * editable.
 */
public class Configuration {
    static Logger logger = Logger.getLogger(Configuration.class);
    static Properties properties = new Properties();

    static {
        InputStream is = Configuration.class.getResourceAsStream("/config.properties");
        try {
            if (is != null)
                properties.load(is);
        } catch (IOException e) {
            logger.info("Cannot load config.properties");
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
