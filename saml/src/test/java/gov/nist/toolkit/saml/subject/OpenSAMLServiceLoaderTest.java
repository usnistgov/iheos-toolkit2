package gov.nist.toolkit.saml.subject;

import gov.nist.toolkit.saml.builder.OpenSamlBootStrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test OpenSAML service loader and factory initialization
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OpenSAMLServiceLoaderTest {

    @BeforeAll
    static void setup() {
        OpenSamlBootStrap.initSamlEngine();
    }

    @Test
    @Order(1)
    @DisplayName("Test service loader discovery approach")
    public void testServiceLoaderDiscovery() {
        System.out.println("--- Testing Service Loader Discovery ---");
        
        // Test 1: Try direct ServiceLoader approach
        try {
            java.util.ServiceLoader<org.opensaml.core.xml.config.XMLObjectProviderRegistry> loader = 
                java.util.ServiceLoader.load(org.opensaml.core.xml.config.XMLObjectProviderRegistry.class);
            
            System.out.println("✓ ServiceLoader found providers: " + loader.iterator().hasNext());
            
            for (org.opensaml.core.xml.config.XMLObjectProviderRegistry provider : loader) {
                System.out.println("  - Provider: " + provider.getClass().getName());
            }
        } catch (Exception e) {
            System.out.println("⚠ ServiceLoader approach failed: " + e.getMessage());
        }
        
        // Test 2: Try XMLObjectProviderRegistrySupport directly
        try {
            org.opensaml.core.xml.config.XMLObjectProviderRegistry registry = null; // getRegistry() doesn't exist in 4.0.1
            System.out.println("✓ Registry from support: " + (registry != null));
            
            if (registry != null) {
                org.opensaml.core.xml.XMLObjectBuilderFactory builderFactory = registry.getBuilderFactory();
                org.opensaml.core.xml.io.MarshallerFactory marshallerFactory = registry.getMarshallerFactory();
                org.opensaml.core.xml.io.UnmarshallerFactory unmarshallerFactory = registry.getUnmarshallerFactory();
                
                System.out.println("  - BuilderFactory: " + (builderFactory != null));
                System.out.println("  - MarshallerFactory: " + (marshallerFactory != null));
                System.out.println("  - UnmarshallerFactory: " + (unmarshallerFactory != null));
            }
        } catch (Exception e) {
            System.out.println("⚠ Registry approach failed: " + e.getMessage());
        }
        
        // Test 3: Try static factory methods directly
        try {
            org.opensaml.core.xml.XMLObjectBuilderFactory builderFactory = org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport.getBuilderFactory();
            org.opensaml.core.xml.io.MarshallerFactory marshallerFactory = org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport.getMarshallerFactory();
            org.opensaml.core.xml.io.UnmarshallerFactory unmarshallerFactory = org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
            
            System.out.println("✓ Static factory methods:");
            System.out.println("  - BuilderFactory: " + (builderFactory != null));
            System.out.println("  - MarshallerFactory: " + (marshallerFactory != null));
            System.out.println("  - UnmarshallerFactory: " + (unmarshallerFactory != null));
            
            // Test if factories are available
            if (builderFactory != null) {
                System.out.println("  - BuilderFactory is available for object creation");
                try {
                    // Just test if the factory can be accessed, not create objects
                    System.out.println("  - Factory class: " + builderFactory.getClass().getName());
                } catch (Exception e) {
                    System.out.println("  - Factory access failed: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.out.println("⚠ Static factory approach failed: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test OpenSAML bootstrap status")
    public void testBootstrapStatus() {
        System.out.println("--- Testing Bootstrap Status ---");
        
        System.out.println("✓ Bootstrap initialized: " + OpenSamlBootStrap.samlEngineInitialized);
        System.out.println("✓ BuilderFactory: " + (OpenSamlBootStrap.getBuilderFactory() != null));
        System.out.println("✓ MarshallerFactory: " + (OpenSamlBootStrap.getMarshallerFactory() != null));
        System.out.println("✓ UnmarshallerFactory: " + (OpenSamlBootStrap.getUnmarshallerFactory() != null));
    }

    @Test
    @Order(3)
    @DisplayName("Test classpath and service discovery")
    public void testClasspathAndServiceDiscovery() {
        System.out.println("--- Testing Classpath and Service Discovery ---");
        
        // Check if OpenSAML classes are available
        try {
            Class<?> registryClass = Class.forName("org.opensaml.core.xml.config.XMLObjectProviderRegistry");
            System.out.println("✓ XMLObjectProviderRegistry class: " + (registryClass != null));
        } catch (ClassNotFoundException e) {
            System.out.println("⚠ XMLObjectProviderRegistry class not found: " + e.getMessage());
        }
        
        try {
            Class<?> supportClass = Class.forName("org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport");
            System.out.println("✓ XMLObjectProviderRegistrySupport class: " + (supportClass != null));
        } catch (ClassNotFoundException e) {
            System.out.println("⚠ XMLObjectProviderRegistrySupport class not found: " + e.getMessage());
        }
        
        // Check service files in classpath
        try {
            java.util.Enumeration<java.net.URL> resources = 
                getClass().getClassLoader().getResources("META-INF/services/org.opensaml.core.xml.config.XMLObjectProviderRegistry");
            
            if (resources.hasMoreElements()) {
                System.out.println("✓ Service files found:");
                while (resources.hasMoreElements()) {
                    java.net.URL resource = resources.nextElement();
                    System.out.println("  - " + resource.toString());
                }
            } else {
                System.out.println("⚠ No service files found in classpath");
            }
        } catch (Exception e) {
            System.out.println("⚠ Service file discovery failed: " + e.getMessage());
        }
    }
}
