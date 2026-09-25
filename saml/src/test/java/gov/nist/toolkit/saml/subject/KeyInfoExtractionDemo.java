package gov.nist.toolkit.saml.subject;

import gov.nist.toolkit.saml.builder.OpenSAMLInitializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;

import java.io.ByteArrayInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Demonstration of KeyInfo extraction testing with real SAML assertions
 * This class shows practical usage patterns for testing KeyInfo extraction
 */
public class KeyInfoExtractionDemo {

    public static void main(String[] args) {
        System.out.println("=== KeyInfo Extraction Demo ===");
        System.out.println("Demonstrating KeyInfo extraction from real SAML assertions\n");
        
        try {
            // Initialize OpenSAML
            OpenSAMLInitializer.ensureInitializedUnchecked();
            
            // Run demonstrations
            demonstrateRSAKeyInfoExtraction();
            demonstrateX509KeyInfoExtraction();
            demonstrateGazelleStyleAssertion();
            demonstrateValidationWorkflow();
            
            System.out.println("\n=== Demo completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demonstrateRSAKeyInfoExtraction() throws Exception {
        System.out.println("--- RSA KeyInfo Extraction Demo ---");
        
        // Create SAML assertion with RSA KeyInfo
        String samlAssertion = createRSAAssertion();
        System.out.println("Created SAML assertion with RSA KeyInfo");
        
        // Parse the assertion
        Assertion assertion = parseSAMLAssertion(samlAssertion);
        System.out.println("✓ Parsed SAML assertion successfully");
        
        // Extract KeyInfo
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        KeyInfo keyInfo = extractKeyInfo(validator, assertion);
        
        if (keyInfo != null) {
            System.out.println("✓ Extracted KeyInfo successfully");
            System.out.println("  - KeyValue elements: " + keyInfo.getKeyValues().size());
            
            if (!keyInfo.getKeyValues().isEmpty() && keyInfo.getKeyValues().get(0).getRSAKeyValue() != null) {
                org.opensaml.xmlsec.signature.RSAKeyValue rsaKey = keyInfo.getKeyValues().get(0).getRSAKeyValue();
                System.out.println("  - RSA Modulus length: " + rsaKey.getModulus().getValue().length());
                System.out.println("  - RSA Exponent: " + rsaKey.getExponent().getValue());
            }
        } else {
            System.out.println("✗ Failed to extract KeyInfo");
        }
        
        System.out.println();
    }

    private static void demonstrateX509KeyInfoExtraction() throws Exception {
        System.out.println("--- X509 KeyInfo Extraction Demo ---");
        
        // Create SAML assertion with X509 KeyInfo
        String samlAssertion = createX509Assertion();
        System.out.println("Created SAML assertion with X509 KeyInfo");
        
        // Parse the assertion
        Assertion assertion = parseSAMLAssertion(samlAssertion);
        System.out.println("✓ Parsed SAML assertion successfully");
        
        // Extract KeyInfo
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        KeyInfo keyInfo = extractKeyInfo(validator, assertion);
        
        if (keyInfo != null) {
            System.out.println("✓ Extracted KeyInfo successfully");
            System.out.println("  - X509Data elements: " + keyInfo.getX509Datas().size());
            
            if (!keyInfo.getX509Datas().isEmpty() && !keyInfo.getX509Datas().get(0).getX509Certificates().isEmpty()) {
                org.opensaml.xmlsec.signature.X509Certificate cert = keyInfo.getX509Datas().get(0).getX509Certificates().get(0);
                System.out.println("  - Certificate length: " + cert.getValue().length() + " bytes");
            }
        } else {
            System.out.println("✗ Failed to extract KeyInfo");
        }
        
        System.out.println();
    }

    private static void demonstrateGazelleStyleAssertion() throws Exception {
        System.out.println("--- Gazelle STS Style Assertion Demo ---");
        
        // Create Gazelle-style SAML assertion
        String gazelleAssertion = createGazelleStyleAssertion();
        System.out.println("Created Gazelle STS style assertion");
        
        // Parse the assertion
        Assertion assertion = parseSAMLAssertion(gazelleAssertion);
        System.out.println("✓ Parsed Gazelle assertion successfully");
        System.out.println("  - Subject: " + assertion.getSubject().getNameID().getValue());
        System.out.println("  - Issuer: " + assertion.getIssuer());
        System.out.println("  - Subject Confirmation Method: " + 
            assertion.getSubject().getSubjectConfirmations().get(0).getMethod());
        
        // Extract KeyInfo
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        KeyInfo keyInfo = extractKeyInfo(validator, assertion);
        
        if (keyInfo != null) {
            System.out.println("✓ Extracted KeyInfo from Gazelle assertion");
            
            // Check if RSA or X509
            if (!keyInfo.getKeyValues().isEmpty()) {
                System.out.println("  - Found RSA KeyInfo (typical for Gazelle STS)");
            }
            if (!keyInfo.getX509Datas().isEmpty()) {
                System.out.println("  - Found X509 Certificate KeyInfo");
            }
        } else {
            System.out.println("✗ Failed to extract KeyInfo from Gazelle assertion");
        }
        
        System.out.println();
    }

    private static void demonstrateValidationWorkflow() throws Exception {
        System.out.println("--- Complete Validation Workflow Demo ---");
        
        // Create test assertion
        String samlAssertion = createRSAAssertion();
        Assertion assertion = parseSAMLAssertion(samlAssertion);
        System.out.println("Created and parsed test assertion");
        
        // Create validator and context
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        MockValidationContext context = new MockValidationContext();
        
        // Generate test key pair
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        context.setStaticParameter(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, keyPair.getPublic());
        System.out.println("Generated RSA key pair for validation");
        
        // Run validation
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        
        try {
            java.lang.reflect.Method method = HolderOfKeySubjectConfirmationValidator.class.getDeclaredMethod("doValidate", 
                SubjectConfirmation.class, Assertion.class, ValidationContext.class);
            method.setAccessible(true);
            
            ValidationResult result = (ValidationResult) method.invoke(validator, confirmation, assertion, context);
            
            System.out.println("✓ Validation workflow completed");
            System.out.println("  - Result: " + result);
            
            if (result == ValidationResult.VALID) {
                System.out.println("  - KeyInfo stored in context: " + 
                    (context.getDynamicParameters().containsKey(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM) ? "Yes" : "No"));
            }
            
        } catch (Exception e) {
            System.out.println("✗ Validation workflow failed: " + e.getMessage());
        }
        
        System.out.println();
    }

    // Helper methods

    private static KeyInfo extractKeyInfo(HolderOfKeySubjectConfirmationValidator validator, Assertion assertion) throws Exception {
        java.lang.reflect.Method method = HolderOfKeySubjectConfirmationValidator.class.getDeclaredMethod("getSubjectConfirmationKeyInfo", SubjectConfirmation.class);
        method.setAccessible(true);
        
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        return (KeyInfo) method.invoke(validator, confirmation);
    }

    private static Assertion parseSAMLAssertion(String xml) throws Exception {
        javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        org.xml.sax.InputSource inputSource = new org.xml.sax.InputSource(inputStream);
        org.w3c.dom.Element documentElement = builder.parse(inputSource).getDocumentElement();
        
        Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory()
                .getUnmarshaller(documentElement);
        
        if (unmarshaller == null) {
            throw new Exception("No Unmarshaller registered for element " + 
                    documentElement.getNamespaceURI() + ":" + documentElement.getLocalName());
        }
        
        try {
            return (Assertion) unmarshaller.unmarshall(documentElement);
        } catch (UnmarshallingException ex) {
            throw new Exception("Error unmarshalling a SAML assertion", ex);
        }
    }

    private static String createRSAAssertion() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_rsa_demo123\" IssueInstant=\"2023-03-27T10:00:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://demo-sts.example.com</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">demo-user</saml2:NameID>" +
            "<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\">" +
            "<saml2:SubjectConfirmationData NotOnOrAfter=\"2023-03-27T10:05:00.000Z\">" +
            "<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
            "<ds:KeyValue>" +
            "<ds:RSAKeyValue>" +
            "<ds:Modulus>MIIBCgKCAQEA1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ</ds:Modulus>" +
            "<ds:Exponent>AQAB</ds:Exponent>" +
            "</ds:RSAKeyValue>" +
            "</ds:KeyValue>" +
            "</ds:KeyInfo>" +
            "</saml2:SubjectConfirmationData>" +
            "</saml2:SubjectConfirmation>" +
            "</saml2:Subject>" +
            "<saml2:Conditions NotBefore=\"2023-03-27T09:55:00.000Z\" NotOnOrAfter=\"2023-03-27T10:05:00.000Z\"/>" +
            "</saml2:Assertion>";
    }

    private static String createX509Assertion() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_x509_demo123\" IssueInstant=\"2023-03-27T10:00:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://demo-sts.example.com</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">demo-user</saml2:NameID>" +
            "<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\">" +
            "<saml2:SubjectConfirmationData NotOnOrAfter=\"2023-03-27T10:05:00.000Z\">" +
            "<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
            "<ds:X509Data>" +
            "<ds:X509Certificate>MIIDXTCCAkWgAwIBAgIJAKoKHEq15X5oMA0GCSqGSIb3DQEBBQUAMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKDBhJbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwHhcNMTMwNzA5MTk0NjAwWhcNMTQwNzA5MTk0NjAwWjBFMQswCQYDVQQGEwJBVTETMBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW50ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxZfU</ds:X509Certificate>" +
            "</ds:X509Data>" +
            "</ds:KeyInfo>" +
            "</saml2:SubjectConfirmationData>" +
            "</saml2:SubjectConfirmation>" +
            "</saml2:Subject>" +
            "<saml2:Conditions NotBefore=\"2023-03-27T09:55:00.000Z\" NotOnOrAfter=\"2023-03-27T10:05:00.000Z\"/>" +
            "</saml2:Assertion>";
    }

    private static String createGazelleStyleAssertion() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_gazelle_demo123\" IssueInstant=\"2023-03-27T10:30:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://gazelle.ihe.net/gazelle-sts</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">valid</saml2:NameID>" +
            "<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\">" +
            "<saml2:SubjectConfirmationData NotOnOrAfter=\"2023-03-27T10:35:00.000Z\">" +
            "<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
            "<ds:KeyValue>" +
            "<ds:RSAKeyValue>" +
            "<ds:Modulus>MIIBCgKCAQEAxZfU5K9NxYcZyJ8K7V8L3m2Q9R1p2w3s4t5u6v7w8x9y0z1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0t1u2v3w4x5y6z7a8b9c0d1e2f3g4h5i6j7k8l9m0n1o2p3q4r5s6t7u8v9w0x1y2z3</ds:Modulus>" +
            "<ds:Exponent>AQAB</ds:Exponent>" +
            "</ds:RSAKeyValue>" +
            "</ds:KeyValue>" +
            "</ds:KeyInfo>" +
            "</saml2:SubjectConfirmationData>" +
            "</saml2:SubjectConfirmation>" +
            "</saml2:Subject>" +
            "<saml2:Conditions NotBefore=\"2023-03-27T10:25:00.000Z\" NotOnOrAfter=\"2023-03-27T10:35:00.000Z\"/>" +
            "<saml2:AttributeStatement>" +
            "<saml2:Attribute Name=\"urn:oasis:names:tc:xspa:1.0:subject:subject-id\" NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:uri\">" +
            "<saml2:AttributeValue>valid</saml2:AttributeValue>" +
            "</saml2:Attribute>" +
            "<saml2:Attribute Name=\"urn:oasis:names:tc:xspa:1.0:subject:subject-organization\" NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:uri\">" +
            "<saml2:AttributeValue>NIST</saml2:AttributeValue>" +
            "</saml2:Attribute>" +
            "</saml2:AttributeStatement>" +
            "</saml2:Assertion>";
    }

    // Mock classes
    private static class MockValidationContext {
        private java.util.Map<String, Object> staticParameters = new java.util.HashMap<>();
        private java.util.Map<String, Object> dynamicParameters = new java.util.HashMap<>();
        private String validationFailureMessage;

        public java.util.Map<String, Object> getStaticParameters() {
            return staticParameters;
        }

        public java.util.Map<String, Object> getDynamicParameters() {
            return dynamicParameters;
        }

        public void setStaticParameter(String key, Object value) {
            staticParameters.put(key, value);
        }

        public String getValidationFailureMessage() {
            return validationFailureMessage;
        }

        public void setValidationFailureMessage(String message) {
            this.validationFailureMessage = message;
        }
    }

    private enum ValidationResult {
        VALID, INVALID, INDETERMINATE
    }

    private interface ValidationContext {
        java.util.Map<String, Object> getStaticParameters();
        java.util.Map<String, Object> getDynamicParameters();
        void setValidationFailureMessage(String message);
        String getValidationFailureMessage();
    }
}
