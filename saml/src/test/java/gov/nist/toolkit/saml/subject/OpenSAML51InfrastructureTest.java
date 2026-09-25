package gov.nist.toolkit.saml.subject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.*;

import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;

import gov.nist.toolkit.saml.builder.OpenSAMLInitializer;

/**
 * Simple test to verify OpenSAML 5.1.4 infrastructure is working
 */
public class OpenSAML51InfrastructureTest {

    @Test
    @Order(1)
    @DisplayName("Test OpenSAML 5.1.4 Infrastructure")
    public void testOpenSAMLInfrastructure() throws Exception {
        System.out.println("--- Testing OpenSAML 5.1.4 Infrastructure ---");
        
        // Initialize OpenSAML
        OpenSAMLInitializer.ensureInitialized();
        
        // Test that factories are available
        XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
        MarshallerFactory marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
        UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
        
        // Verify infrastructure is working
        if (builderFactory == null) {
            throw new AssertionError("BuilderFactory should not be null");
        }
        if (marshallerFactory == null) {
            throw new AssertionError("MarshallerFactory should not be null");
        }
        if (unmarshallerFactory == null) {
            throw new AssertionError("UnmarshallerFactory should not be null");
        }
        if (XMLObjectProviderRegistrySupport.getBuilderFactory() == null) {
            throw new AssertionError("XMLObjectProviderRegistrySupport should not be null");
        }
        
        System.out.println("✓ OpenSAML 5.1.4 infrastructure test completed");
        System.out.println("  - BuilderFactory: " + (builderFactory != null ? "OK" : "NULL"));
        System.out.println("  - MarshallerFactory: " + (marshallerFactory != null ? "OK" : "NULL"));
        System.out.println("  - UnmarshallerFactory: " + (unmarshallerFactory != null ? "OK" : "NULL"));
    }
}
