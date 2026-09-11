package gov.nist.toolkit.saml.test;

import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;

/**
 * Simple test to verify OpenSAML 5.1.4 infrastructure
 */
public class OpenSAML51Test {
    
    public static void main(String[] args) {
        System.out.println("Testing OpenSAML 5.1.4 infrastructure...");
        
        try {
            XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
            MarshallerFactory marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
            UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
            
            System.out.println("BuilderFactory: " + (builderFactory != null ? "OK" : "NULL"));
            System.out.println("MarshallerFactory: " + (marshallerFactory != null ? "OK" : "NULL"));
            System.out.println("UnmarshallerFactory: " + (unmarshallerFactory != null ? "OK" : "NULL"));
            
            if (builderFactory != null && marshallerFactory != null && unmarshallerFactory != null) {
                System.out.println("SUCCESS: OpenSAML 5.1.4 infrastructure is working!");
            } else {
                System.out.println("FAILURE: OpenSAML 5.1.4 infrastructure is not working!");
            }
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
