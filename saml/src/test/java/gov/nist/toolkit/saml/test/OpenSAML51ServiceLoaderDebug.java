package gov.nist.toolkit.saml.test;

import java.util.logging.Logger;

import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.config.InitializationService;

/**
 * Test to debug OpenSAML 5.1.4 service loader initialization
 */
public class OpenSAML51ServiceLoaderDebug {
    
    private static final Logger log = Logger.getLogger(OpenSAML51ServiceLoaderDebug.class.getName());
    
    public static void main(String[] args) {
        System.out.println("=== OpenSAML 5.1.4 Service Loader Debug ===");
        
        try {
            // Test 1: Basic initialization
            System.out.println("1. Testing InitializationService.initialize()...");
            InitializationService.initialize();
            System.out.println("   InitializationService.initialize() completed");
            
            // Test 2: Check XMLObjectProviderRegistrySupport
            System.out.println("2. Testing XMLObjectProviderRegistrySupport...");
            XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
            MarshallerFactory marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
            UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
            
            System.out.println("   BuilderFactory: " + (builderFactory != null ? "NOT NULL" : "NULL"));
            System.out.println("   MarshallerFactory: " + (marshallerFactory != null ? "NOT NULL" : "NULL"));
            System.out.println("   UnmarshallerFactory: " + (unmarshallerFactory != null ? "NOT NULL" : "NULL"));
            
            // Test 3: Try to get registry
            System.out.println("3. Testing registry access...");
            try {
                // In OpenSAML 5.1.4, there's no getRegistry() method
                // The registry should be available after InitializationService.initialize()
                System.out.println("   Registry access method not available in 5.1.4");
                System.out.println("   Testing direct factory access...");
                
                System.out.println("   Direct BuilderFactory: " + (builderFactory != null ? "NOT NULL" : "NULL"));
                System.out.println("   Direct MarshallerFactory: " + (marshallerFactory != null ? "NOT NULL" : "NULL"));
                System.out.println("   Direct UnmarshallerFactory: " + (unmarshallerFactory != null ? "NOT NULL" : "NULL"));
                
            } catch (Exception e) {
                System.out.println("   Registry access failed: " + e.getMessage());
            }
            
            // Test 4: Check service loader
            System.out.println("4. Testing service loader...");
            java.util.ServiceLoader<org.opensaml.core.config.Initializer> loader = 
                java.util.ServiceLoader.load(org.opensaml.core.config.Initializer.class);
            
            System.out.println("   ServiceLoader: " + (loader != null ? "NOT NULL" : "NULL"));
            
            int count = 0;
            for (org.opensaml.core.config.Initializer initializer : loader) {
                count++;
                System.out.println("   Initializer " + count + ": " + initializer.getClass().getName());
            }
            System.out.println("   Total initializers found: " + count);
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
