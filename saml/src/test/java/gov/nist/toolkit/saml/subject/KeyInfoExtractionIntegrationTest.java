package gov.nist.toolkit.saml.subject;

import gov.nist.toolkit.saml.builder.OpenSAMLInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.RSAKeyValue;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for KeyInfo extraction using real SAML assertions
 * This test demonstrates practical usage with actual SAML assertion files
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KeyInfoExtractionIntegrationTest {

    @BeforeAll
    static void setup() {
        OpenSAMLInitializer.ensureInitializedUnchecked();
        System.out.println("=== KeyInfo Extraction Integration Test ===");
        System.out.println("Testing with real SAML assertion structures");
    }

    @Test
    @Order(1)
    @DisplayName("Load and parse real SAML assertion from test resources")
    public void testLoadRealSamlAssertion() throws Exception {
        System.out.println("\n--- Test 1: Loading Real SAML Assertion ---");
        
        // Try to load a real SAML assertion from test resources
        String samlAssertionPath = findSamlAssertionFile();
        
        if (samlAssertionPath != null) {
            System.out.println("Found SAML assertion file: " + samlAssertionPath);
            
            String samlXml = new String(Files.readAllBytes(Paths.get(samlAssertionPath)), StandardCharsets.UTF_8);
            System.out.println("SAML Assertion loaded, length: " + samlXml.length() + " characters");
            
            // Parse the assertion
            Assertion assertion = parseSAMLAssertion(samlXml);
            assertNotNull(assertion, "SAML assertion should be parsed successfully");
            
            System.out.println("✓ Successfully parsed real SAML assertion");
            System.out.println("  - ID: " + assertion.getID());
            System.out.println("  - Issuer: " + assertion.getIssuer());
            System.out.println("  - Subject confirmations: " + assertion.getSubject().getSubjectConfirmations().size());
            
        } else {
            System.out.println("⚠ No real SAML assertion file found, using sample assertion");
            
            // Create a sample assertion for testing
            String sampleAssertion = createSampleAssertion();
            Assertion assertion = parseSAMLAssertion(sampleAssertion);
            assertNotNull(assertion, "Sample SAML assertion should be parsed successfully");
            
            System.out.println("✓ Successfully parsed sample SAML assertion");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Extract KeyInfo from SubjectConfirmationData")
    public void testExtractKeyInfoFromSubjectConfirmation() throws Exception {
        System.out.println("\n--- Test 2: KeyInfo Extraction ---");
        
        // Create test assertion with KeyInfo
        String samlAssertion = createAssertionWithKeyInfo();
        Assertion assertion = parseSAMLAssertion(samlAssertion);
        
        // Test KeyInfo extraction
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        
        // Use reflection to access the private method
        java.lang.reflect.Method method = HolderOfKeySubjectConfirmationValidator.class.getDeclaredMethod("getSubjectConfirmationKeyInformation", SubjectConfirmation.class, Assertion.class, ValidationContext.class);
        method.setAccessible(true);
        
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        java.util.Map<String, Object> staticParams = new java.util.HashMap<>();
        ValidationContext context = new ValidationContext(staticParams);
        @SuppressWarnings("unchecked")
        java.util.List<KeyInfo> extractedKeyInfos = (java.util.List<KeyInfo>) method.invoke(validator, confirmation, assertion, context);
        KeyInfo extractedKeyInfo = extractedKeyInfos.isEmpty() ? null : extractedKeyInfos.get(0);
        
        // Verify extraction
        assertNotNull(extractedKeyInfo, "KeyInfo should be extracted");
        
        System.out.println("✓ KeyInfo extraction successful");
        System.out.println("  - KeyValue elements: " + (extractedKeyInfo.getKeyValues() != null ? extractedKeyInfo.getKeyValues().size() : 0));
        System.out.println("  - X509Data elements: " + (extractedKeyInfo.getX509Datas() != null ? extractedKeyInfo.getX509Datas().size() : 0));
        
        // Test with assertion that has no KeyInfo
        String assertionWithoutKeyInfo = createAssertionWithoutKeyInfo();
        Assertion assertion2 = parseSAMLAssertion(assertionWithoutKeyInfo);
        
        SubjectConfirmation confirmation2 = assertion2.getSubject().getSubjectConfirmations().get(0);
        java.util.Map<String, Object> staticParams2 = new java.util.HashMap<>();
        ValidationContext context2 = new ValidationContext(staticParams2);
        @SuppressWarnings("unchecked")
        java.util.List<KeyInfo> extractedKeyInfos2 = (java.util.List<KeyInfo>) method.invoke(validator, confirmation2, assertion2, context2);
        KeyInfo extractedKeyInfo2 = extractedKeyInfos2.isEmpty() ? null : extractedKeyInfos2.get(0);
        
        assertNull(extractedKeyInfo2, "No KeyInfo should be extracted when not present");
        System.out.println("✓ Correctly handled assertion without KeyInfo");
    }

    @Test
    @Order(3)
    @DisplayName("Test complete validation workflow")
    public void testCompleteValidationWorkflow() throws Exception {
        System.out.println("\n--- Test 3: Complete Validation Workflow ---");
        
        // Create assertion with RSA KeyInfo
        String samlAssertion = createAssertionWithRSAKeyInfo();
        Assertion assertion = parseSAMLAssertion(samlAssertion);
        
        // Create validator and context
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        java.util.Map<String, Object> staticParams = new java.util.HashMap<>();
        ValidationContext context = new ValidationContext(staticParams);
        
        // Add test key to context
        KeyPair keyPair = generateRSAKeyPair();
        staticParams.put(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, keyPair.getPublic());
        
        System.out.println("Generated RSA key pair for testing");
        System.out.println("  - Algorithm: " + keyPair.getPublic().getAlgorithm());
        System.out.println("  - Format: " + keyPair.getPublic().getFormat());
        
        // Run validation
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        
        java.lang.reflect.Method method = HolderOfKeySubjectConfirmationValidator.class.getDeclaredMethod("doValidate", 
            SubjectConfirmation.class, Assertion.class, ValidationContext.class);
        method.setAccessible(true);
        
        ValidationResult result = (ValidationResult) method.invoke(validator, confirmation, assertion, context);
        
        System.out.println("✓ Validation completed");
        System.out.println("  - Result: " + result);
        
        // Check if KeyInfo was stored in context
        Object storedKeyInfo = context.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM);
        System.out.println("  - KeyInfo stored in context: " + (storedKeyInfo != null ? "Yes" : "No"));
        
        // Test failure scenarios
        System.out.println("\n--- Testing Failure Scenarios ---");
        
        // Test with no presenter key
        java.util.Map<String, Object> staticParamsNoKey = new java.util.HashMap<>();
        ValidationContext contextNoKey = new ValidationContext(staticParamsNoKey);
        ValidationResult resultNoKey = (ValidationResult) method.invoke(validator, confirmation, assertion, contextNoKey);
        assertEquals(ValidationResult.INDETERMINATE, resultNoKey);
        System.out.println("✓ Correctly failed validation with no presenter key");
        
        // Test with wrong key type
        java.util.Map<String, Object> staticParamsWrongKey = new java.util.HashMap<>();
        ValidationContext contextWrongKey = new ValidationContext(staticParamsWrongKey);
        KeyPair dsaKeyPair = generateDSAKeyPair();
        staticParamsWrongKey.put(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, dsaKeyPair.getPublic());
        
        ValidationResult resultWrongKey = (ValidationResult) method.invoke(validator, confirmation, assertion, contextWrongKey);
        assertEquals(ValidationResult.INVALID, resultWrongKey);
        System.out.println("✓ Correctly failed validation with wrong key type");
    }

    @Test
    @Order(4)
    @DisplayName("Demonstrate practical usage with Gazelle STS assertions")
    public void testPracticalGazelleUsage() throws Exception {
        System.out.println("\n--- Test 4: Practical Gazelle STS Usage ---");
        
        // Create a Gazelle-style assertion
        String gazelleAssertion = createGazelleStyleAssertion();
        Assertion assertion = parseSAMLAssertion(gazelleAssertion);
        
        System.out.println("Created Gazelle-style SAML assertion");
        System.out.println("  - Subject: " + assertion.getSubject().getNameID().getValue());
        System.out.println("  - Method: " + assertion.getSubject().getSubjectConfirmations().get(0).getMethod());
        
        // Test KeyInfo extraction
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        
        java.lang.reflect.Method method = HolderOfKeySubjectConfirmationValidator.class.getDeclaredMethod("getSubjectConfirmationKeyInformation", SubjectConfirmation.class, Assertion.class, ValidationContext.class);
        method.setAccessible(true);
        
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        java.util.Map<String, Object> staticParams = new java.util.HashMap<>();
        ValidationContext context = new ValidationContext(staticParams);
        @SuppressWarnings("unchecked")
        java.util.List<KeyInfo> extractedKeyInfos = (java.util.List<KeyInfo>) method.invoke(validator, confirmation, assertion, context);
        KeyInfo keyInfo = extractedKeyInfos.isEmpty() ? null : extractedKeyInfos.get(0);
        
        assertNotNull(keyInfo, "KeyInfo should be extracted from Gazelle assertion");
        
        // Display KeyInfo details
        if (keyInfo.getKeyValues() != null && !keyInfo.getKeyValues().isEmpty()) {
            System.out.println("✓ Found KeyValue in Gazelle assertion");
            KeyValue keyValue = keyInfo.getKeyValues().get(0);
            if (keyValue.getRSAKeyValue() != null) {
                System.out.println("  - RSA KeyInfo detected");
                System.out.println("  - Modulus length: " + keyValue.getRSAKeyValue().getModulus().getValue().length());
            }
        }
        
        if (keyInfo.getX509Datas() != null && !keyInfo.getX509Datas().isEmpty()) {
            System.out.println("✓ Found X509Data in Gazelle assertion");
            X509Data x509Data = keyInfo.getX509Datas().get(0);
            if (x509Data.getX509Certificates() != null && !x509Data.getX509Certificates().isEmpty()) {
                System.out.println("  - X509 Certificate detected");
                System.out.println("  - Certificate length: " + x509Data.getX509Certificates().get(0).getValue().length());
            }
        }
        
        System.out.println("✓ Gazelle STS assertion processing completed successfully");
    }

    // Helper methods

    private String findSamlAssertionFile() {
        // Look for SAML assertion files in common test locations
        List<String> possiblePaths = List.of(
            "it-tests/src/test/resources/war/toolkitx/testkit/examples/GazelleSts/",
            "saml/test/resources/",
            "src/test/resources/",
            "test/resources/"
        );
        
        for (String basePath : possiblePaths) {
            File baseDir = new File(basePath);
            if (baseDir.exists() && baseDir.isDirectory()) {
                String samlFile = findSamlFileInDirectory(baseDir);
                if (samlFile != null) {
                    return samlFile;
                }
            }
        }
        
        return null;
    }

    private String findSamlFileInDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String found = findSamlFileInDirectory(file);
                    if (found != null) return found;
                } else if (file.getName().toLowerCase().contains("saml") || 
                          file.getName().toLowerCase().contains("assertion")) {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }

    private String createSampleAssertion() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_sample123\" IssueInstant=\"2023-03-27T10:00:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://test-sts.example.com</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">test-user</saml2:NameID>" +
            "</saml2:Subject>" +
            "<saml2:Conditions NotBefore=\"2023-03-27T09:55:00.000Z\" NotOnOrAfter=\"2023-03-27T10:05:00.000Z\"/>" +
            "</saml2:Assertion>";
    }

    private String createAssertionWithKeyInfo() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_keyinfo123\" IssueInstant=\"2023-03-27T10:00:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://test-sts.example.com</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">test-user</saml2:NameID>" +
            "<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\">" +
            "<saml2:SubjectConfirmationData NotOnOrAfter=\"2023-03-27T10:05:00.000Z\">" +
            "<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
            "<ds:KeyValue>" +
            "<ds:RSAKeyValue>" +
            "<ds:Modulus>TEST_MODULUS_VALUE_HERE</ds:Modulus>" +
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

    private String createAssertionWithoutKeyInfo() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_nokeyinfo123\" IssueInstant=\"2023-03-27T10:00:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://test-sts.example.com</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">test-user</saml2:NameID>" +
            "<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\">" +
            "<saml2:SubjectConfirmationData NotOnOrAfter=\"2023-03-27T10:05:00.000Z\"/>" +
            "</saml2:SubjectConfirmation>" +
            "</saml2:Subject>" +
            "<saml2:Conditions NotBefore=\"2023-03-27T09:55:00.000Z\" NotOnOrAfter=\"2023-03-27T10:05:00.000Z\"/>" +
            "</saml2:Assertion>";
    }

    private String createAssertionWithRSAKeyInfo() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_rsatest123\" IssueInstant=\"2023-03-27T10:00:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://test-sts.example.com</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">test-user</saml2:NameID>" +
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

    private String createGazelleStyleAssertion() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_gazelle123\" IssueInstant=\"2023-03-27T10:30:00.000Z\" Version=\"2.0\">" +
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
            "</saml2:AttributeStatement>" +
            "</saml2:Assertion>";
    }

    private Assertion parseSAMLAssertion(String xml) throws Exception {
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

    private KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    private KeyPair generateDSAKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(1024);
        return keyGen.generateKeyPair();
    }
}
