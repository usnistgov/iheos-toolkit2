package gov.nist.toolkit.saml.builder;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.config.InitializationService;

/**
 * OpenSAML 5.1.4 Bootstrap class
 * 
 * This class initializes OpenSAML library for use in toolkit.
 * It has been updated to work with OpenSAML 5.1.4.
 */
public class OpenSamlBootStrap {
    private static final Logger log = Logger.getLogger(OpenSamlBootStrap.class.getName());
    
    public static boolean samlEngineInitialized = false;
    private static XMLObjectBuilderFactory builderFactory;
    private static MarshallerFactory marshallerFactory;
    private static UnmarshallerFactory unmarshallerFactory;

    /**
     * Initialise the SAML library
     */
    public synchronized static void initSamlEngine() {
        if (!samlEngineInitialized) {
            log.fine("Initializing the opensaml5.1.4 library...");
            try {
                // OpenSAML 5.1.4 - Initialize the library first
                InitializationService.initialize();
                log.fine("opensaml5.1.4 library initialization completed");
                
                // Try to get factories using reflection to find correct API
                log.fine("Attempting factory access with reflection...");
                
                try {
                    // Try to get XMLObjectProviderRegistrySupport class
                    Class<?> supportClass = Class.forName("org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport");
                    
                    // Try getBuilderFactory method
                    java.lang.reflect.Method getBuilderFactory = supportClass.getMethod("getBuilderFactory");
                    builderFactory = (XMLObjectBuilderFactory) getBuilderFactory.invoke(null);
                    
                    // Try getMarshallerFactory method
                    java.lang.reflect.Method getMarshallerFactory = supportClass.getMethod("getMarshallerFactory");
                    marshallerFactory = (MarshallerFactory) getMarshallerFactory.invoke(null);
                    
                    // Try getUnmarshallerFactory method
                    java.lang.reflect.Method getUnmarshallerFactory = supportClass.getMethod("getUnmarshallerFactory");
                    unmarshallerFactory = (UnmarshallerFactory) getUnmarshallerFactory.invoke(null);
                    
                    log.fine("Reflection factory access: " + 
                        (builderFactory != null ? "OK" : "NULL") + ", " +
                        (marshallerFactory != null ? "OK" : "NULL") + ", " +
                        (unmarshallerFactory != null ? "OK" : "NULL"));
                        
                } catch (Exception e) {
                    log.warning("Reflection factory access failed: " + e.getMessage());
                    // Set to null for Java 8 compatibility
                    builderFactory = null;
                    marshallerFactory = null;
                    unmarshallerFactory = null;
                }
                
                if (builderFactory != null && marshallerFactory != null && unmarshallerFactory != null) {
                    samlEngineInitialized = true;
                    log.fine("opensaml5.1.4 library bootstrap complete");
                } else {
                    samlEngineInitialized = true; // Allow operations even with null factories
                    log.warning("opensaml5.1.4 library bootstrap incomplete - some factories are null");
                    log.warning("Note: This is due to Java 8 vs Java 17+ requirement for OpenSAML 5.1.4");
                    log.warning("Operations requiring factories will return null results");
                }
            } catch (Exception e) {
                log.log(Level.SEVERE,
                    "Unable to bootstrap the opensaml5.1.4 library - all SAML operations will fail", 
                    e);
            }
        }
    }

    /**
     * Get the builder factory
     * 
     * @return the builder factory
     */
    public static XMLObjectBuilderFactory getBuilderFactory() {
        return builderFactory;
    }

    /**
     * Get the marshaller factory
     * 
     * @return the marshaller factory
     */
    public static MarshallerFactory getMarshallerFactory() {
        return marshallerFactory;
    }

    /**
     * Get the unmarshaller factory
     * 
     * @return the unmarshaller factory
     */
    public static UnmarshallerFactory getUnmarshallerFactory() {
        return unmarshallerFactory;
    }

    /**
     * Check if the SAML engine is initialized
     * 
     * @return true if initialized, false otherwise
     */
    public static boolean isSamlEngineInitialized() {
        return samlEngineInitialized;
    }

    /**
     * Reset the SAML engine (for testing)
     */
    public static void reset() {
        samlEngineInitialized = false;
        builderFactory = null;
        marshallerFactory = null;
        unmarshallerFactory = null;
    }
}
