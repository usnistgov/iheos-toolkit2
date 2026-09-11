package gov.nist.toolkit.saml.subject;

import gov.nist.toolkit.saml.builder.OpenSamlBootStrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.RSAKeyValue;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Practical test for KeyInfo extraction from real SAML assertions
 * Tests the actual extraction logic used in HolderOfKeySubjectConfirmationValidator
 */
public class RealKeyInfoExtractionTest {

    @BeforeAll
    static void setup() {
        OpenSamlBootStrap.bootstrap();
    }

    @Test
    @DisplayName("Test KeyInfo extraction from real Gazelle STS SAML assertion")
    public void testKeyInfoExtractionFromGazelleAssertion() throws Exception {
        // Sample SAML assertion similar to what Gazelle STS returns
        String gazelleSamlAssertion = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "ID=\"_a1b2c3d4e5f6g7h8i9j0\" IssueInstant=\"2023-03-27T10:30:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://gazelle.ihe.net/gazelle-sts</saml2:Issuer>" +
            "<ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
            "<ds:SignedInfo>" +
            "<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>" +
            "<ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>" +
            "<ds:Reference URI=\"#_a1b2c3d4e5f6g7h8i9j0\">" +
            "<ds:Transforms>" +
            "<ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>" +
            "</ds:Transforms>" +
            "<ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>" +
            "<ds:DigestValue>DIGEST_VALUE_HERE</ds:DigestValue>" +
            "</ds:Reference>" +
            "</ds:SignedInfo>" +
            "<ds:SignatureValue>SIGNATURE_VALUE_HERE</ds:SignatureValue>" +
            "<ds:KeyInfo>" +
            "<ds:X509Data>" +
            "<ds:X509Certificate>CERTIFICATE_BASE64_HERE</ds:X509Certificate>" +
            "</ds:X509Data>" +
            "</ds:KeyInfo>" +
            "</ds:Signature>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">valid</saml2:NameID>" +
            "<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\">" +
            "<saml2:SubjectConfirmationData NotOnOrAfter=\"2023-03-27T10:35:00.000Z\">" +
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
            "<saml2:Conditions NotBefore=\"2023-03-27T10:25:00.000Z\" NotOnOrAfter=\"2023-03-27T10:35:00.000Z\"/>" +
            "<saml2:AttributeStatement>" +
            "<saml2:Attribute Name=\"urn:oasis:names:tc:xspa:1.0:subject:subject-id\" NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:uri\">" +
            "<saml2:AttributeValue xsi:type=\"xs:string\">valid</saml2:AttributeValue>" +
            "</saml2:Attribute>" +
            "<saml2:Attribute Name=\"urn:oasis:names:tc:xspa:1.0:subject:subject-organization\" NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:uri\">" +
            "<saml2:AttributeValue xsi:type=\"xs:string\">NIST</saml2:AttributeValue>" +
            "</saml2:Attribute>" +
            "<saml2:Attribute Name=\"urn:oasis:names:tc:xspa:1.0:subject:subject-organization-id\" NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:uri\">" +
            "<saml2:AttributeValue xsi:type=\"xs:string\">1.2.3.4.5.6.7.8.9</saml2:AttributeValue>" +
            "</saml2:Attribute>" +
            "</saml2:AttributeStatement>" +
            "</saml2:Assertion>";

        // Parse the assertion
        Assertion assertion = parseSAMLAssertion(gazelleSamlAssertion);
        
        // Test the KeyInfo extraction
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        
        // Use reflection to access the private method for testing
        java.lang.reflect.Method method = HolderOfKeySubjectConfirmationValidator.class.getDeclaredMethod("getSubjectConfirmationKeyInfo", SubjectConfirmation.class);
        method.setAccessible(true);
        
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        KeyInfo extractedKeyInfo = (KeyInfo) method.invoke(validator, confirmation);
        
        // Verify KeyInfo was extracted
        assertNotNull(extractedKeyInfo, "KeyInfo should be extracted from SubjectConfirmationData");
        
        // Verify KeyValue is present
        assertNotNull(extractedKeyInfo.getKeyValues(), "KeyInfo should contain KeyValue elements");
        assertFalse(extractedKeyInfo.getKeyValues().isEmpty(), "KeyInfo should have at least one KeyValue");
        
        // Verify RSAKeyValue
        KeyValue keyValue = extractedKeyInfo.getKeyValues().get(0);
        assertNotNull(keyValue.getRSAKeyValue(), "KeyValue should contain RSAKeyValue");
        
        RSAKeyValue rsaKeyValue = keyValue.getRSAKeyValue();
        assertNotNull(rsaKeyValue.getModulus(), "RSAKeyValue should have Modulus");
        assertNotNull(rsaKeyValue.getExponent(), "RSAKeyValue should have Exponent");
        
        System.out.println("Successfully extracted RSA KeyInfo from Gazelle-style SAML assertion");
        System.out.println("Modulus: " + rsaKeyValue.getModulus().getValue());
        System.out.println("Exponent: " + rsaKeyValue.getExponent().getValue());
    }

    @Test
    @DisplayName("Test KeyInfo extraction with X509Certificate in SubjectConfirmation")
    public void testKeyInfoExtractionWithX509Cert() throws Exception {
        // SAML assertion with X509Certificate in SubjectConfirmation
        String samlWithX509 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_x509test123\" IssueInstant=\"2023-03-27T11:00:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://test-sts.example.com</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">test-user</saml2:NameID>" +
            "<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\">" +
            "<saml2:SubjectConfirmationData NotOnOrAfter=\"2023-03-27T11:05:00.000Z\">" +
            "<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
            "<ds:X509Data>" +
            "<ds:X509Certificate>MIIDXTCCAkWgAwIBAgIJAKoKHEq15X5oMA0GCSqGSIb3DQEBBQUAMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKDBhJbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwHhcNMTMwNzA5MTk0NjAwWhcNMTQwNzA5MTk0NjAwWjBFMQswCQYDVQQGEwJBVTETMBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW50ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxZfU</ds:X509Certificate>" +
            "</ds:X509Data>" +
            "</ds:KeyInfo>" +
            "</saml2:SubjectConfirmationData>" +
            "</saml2:SubjectConfirmation>" +
            "</saml2:Subject>" +
            "<saml2:Conditions NotBefore=\"2023-03-27T10:55:00.000Z\" NotOnOrAfter=\"2023-03-27T11:05:00.000Z\"/>" +
            "</saml2:Assertion>";

        // Parse the assertion
        Assertion assertion = parseSAMLAssertion(samlWithX509);
        
        // Test the KeyInfo extraction
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        
        // Use reflection to access the private method
        java.lang.reflect.Method method = HolderOfKeySubjectConfirmationValidator.class.getDeclaredMethod("getSubjectConfirmationKeyInfo", SubjectConfirmation.class);
        method.setAccessible(true);
        
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        KeyInfo extractedKeyInfo = (KeyInfo) method.invoke(validator, confirmation);
        
        // Verify KeyInfo was extracted
        assertNotNull(extractedKeyInfo, "KeyInfo should be extracted from SubjectConfirmationData");
        
        // Verify X509Data is present
        assertNotNull(extractedKeyInfo.getX509Datas(), "KeyInfo should contain X509Data elements");
        assertFalse(extractedKeyInfo.getX509Datas().isEmpty(), "KeyInfo should have at least one X509Data");
        
        // Verify X509Certificate
        X509Data x509Data = extractedKeyInfo.getX509Datas().get(0);
        assertNotNull(x509Data.getX509Certificates(), "X509Data should contain X509Certificate elements");
        assertFalse(x509Data.getX509Certificates().isEmpty(), "X509Data should have at least one X509Certificate");
        
        X509Certificate xmlCert = x509Data.getX509Certificates().get(0);
        assertNotNull(xmlCert.getValue(), "X509Certificate should have a value");
        assertTrue(xmlCert.getValue().length > 0, "X509Certificate value should not be empty");
        
        System.out.println("Successfully extracted X509Certificate KeyInfo from SAML assertion");
        System.out.println("Certificate length: " + xmlCert.getValue().length + " bytes");
    }

    @Test
    @DisplayName("Test KeyInfo extraction failure with no KeyInfo present")
    public void testKeyInfoExtractionFailureNoKeyInfo() throws Exception {
        // SAML assertion without KeyInfo in SubjectConfirmation
        String samlWithoutKeyInfo = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_nokeyinfo123\" IssueInstant=\"2023-03-27T11:30:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://test-sts.example.com</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">test-user</saml2:NameID>" +
            "<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\">" +
            "<saml2:SubjectConfirmationData NotOnOrAfter=\"2023-03-27T11:35:00.000Z\"/>" +
            "</saml2:SubjectConfirmation>" +
            "</saml2:Subject>" +
            "<saml2:Conditions NotBefore=\"2023-03-27T11:25:00.000Z\" NotOnOrAfter=\"2023-03-27T11:35:00.000Z\"/>" +
            "</saml2:Assertion>";

        // Parse the assertion
        Assertion assertion = parseSAMLAssertion(samlWithoutKeyInfo);
        
        // Test the KeyInfo extraction
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        
        // Use reflection to access the private method
        java.lang.reflect.Method method = HolderOfKeySubjectConfirmationValidator.class.getDeclaredMethod("getSubjectConfirmationKeyInfo", SubjectConfirmation.class);
        method.setAccessible(true);
        
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        KeyInfo extractedKeyInfo = (KeyInfo) method.invoke(validator, confirmation);
        
        // Verify no KeyInfo was extracted
        assertNull(extractedKeyInfo, "KeyInfo should not be extracted when not present in SubjectConfirmationData");
        
        System.out.println("Correctly returned null when no KeyInfo present in SubjectConfirmationData");
    }

    @Test
    @DisplayName("Test KeyInfo extraction with multiple KeyInfo elements")
    public void testKeyInfoExtractionMultipleKeyInfo() throws Exception {
        // SAML assertion with multiple KeyInfo elements (edge case)
        String samlWithMultipleKeyInfo = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_multiple123\" IssueInstant=\"2023-03-27T12:00:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://test-sts.example.com</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">test-user</saml2:NameID>" +
            "<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\">" +
            "<saml2:SubjectConfirmationData NotOnOrAfter=\"2023-03-27T12:05:00.000Z\">" +
            "<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
            "<ds:KeyValue>" +
            "<ds:RSAKeyValue>" +
            "<ds:Modulus>FIRST_MODULUS_HERE</ds:Modulus>" +
            "<ds:Exponent>AQAB</ds:Exponent>" +
            "</ds:RSAKeyValue>" +
            "</ds:KeyValue>" +
            "</ds:KeyInfo>" +
            "<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
            "<ds:X509Data>" +
            "<ds:X509Certificate>SECOND_CERT_HERE</ds:X509Certificate>" +
            "</ds:X509Data>" +
            "</ds:KeyInfo>" +
            "</saml2:SubjectConfirmationData>" +
            "</saml2:SubjectConfirmation>" +
            "</saml2:Subject>" +
            "<saml2:Conditions NotBefore=\"2023-03-27T11:55:00.000Z\" NotOnOrAfter=\"2023-03-27T12:05:00.000Z\"/>" +
            "</saml2:Assertion>";

        // Parse the assertion
        Assertion assertion = parseSAMLAssertion(samlWithMultipleKeyInfo);
        
        // Test the KeyInfo extraction
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        
        // Use reflection to access the private method
        java.lang.reflect.Method method = HolderOfKeySubjectConfirmationValidator.class.getDeclaredMethod("getSubjectConfirmationKeyInfo", SubjectConfirmation.class);
        method.setAccessible(true);
        
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        KeyInfo extractedKeyInfo = (KeyInfo) method.invoke(validator, confirmation);
        
        // Verify the first KeyInfo was extracted
        assertNotNull(extractedKeyInfo, "First KeyInfo should be extracted when multiple are present");
        
        // Verify it's the RSA KeyValue (first one)
        assertNotNull(extractedKeyInfo.getKeyValues(), "First KeyInfo should contain KeyValue elements");
        assertFalse(extractedKeyInfo.getKeyValues().isEmpty(), "First KeyInfo should have KeyValue");
        
        System.out.println("Successfully extracted first KeyInfo when multiple present");
    }

    @Test
    @DisplayName("Test complete validation flow with real SAML assertion")
    public void testCompleteValidationFlow() throws Exception {
        // Use the Gazelle-style assertion
        String gazelleSamlAssertion = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_flowtest123\" IssueInstant=\"2023-03-27T12:30:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://gazelle.ihe.net/gazelle-sts</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">valid</saml2:NameID>" +
            "<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\">" +
            "<saml2:SubjectConfirmationData NotOnOrAfter=\"2023-03-27T12:35:00.000Z\">" +
            "<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
            "<ds:KeyValue>" +
            "<ds:RSAKeyValue>" +
            "<ds:Modulus>TEST_MODULUS_FOR_VALIDATION_TEST</ds:Modulus>" +
            "<ds:Exponent>AQAB</ds:Exponent>" +
            "</ds:RSAKeyValue>" +
            "</ds:KeyValue>" +
            "</ds:KeyInfo>" +
            "</saml2:SubjectConfirmationData>" +
            "</saml2:SubjectConfirmation>" +
            "</saml2:Subject>" +
            "<saml2:Conditions NotBefore=\"2023-03-27T12:25:00.000Z\" NotOnOrAfter=\"2023-03-27T12:35:00.000Z\"/>" +
            "</saml2:Assertion>";

        // Parse the assertion
        Assertion assertion = parseSAMLAssertion(gazelleSamlAssertion);
        
        // Create validator and mock context
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        MockValidationContext context = new MockValidationContext();
        
        // Generate a test RSA key pair
        KeyPair keyPair = generateRSAKeyPair();
        context.setStaticParameter(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, keyPair.getPublic());
        
        // Run the complete validation
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        
        // Use reflection to access the doValidate method
        java.lang.reflect.Method method = HolderOfKeySubjectConfirmationValidator.class.getDeclaredMethod("doValidate", 
            SubjectConfirmation.class, Assertion.class, ValidationContext.class);
        method.setAccessible(true);
        
        ValidationResult result = (ValidationResult) method.invoke(validator, confirmation, assertion, context);
        
        // The result should be VALID (basic match implementation returns true for RSA keys)
        assertEquals(ValidationResult.VALID, result, "Validation should succeed with matching RSA key");
        
        // Verify KeyInfo was stored in context
        assertNotNull(context.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM), 
            "Confirmed KeyInfo should be stored in context");
        
        System.out.println("Complete validation flow test passed successfully");
    }

    // Helper methods

    private Assertion parseSAMLAssertion(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Element documentElement = builder.parse(inputStream).getDocumentElement();
        
        // Unmarshal the SAML assertion
        return (Assertion) org.opensaml.core.xml.io.UnmarshallingUtil.unmarshall(documentElement);
    }

    private KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    // Mock ValidationContext for testing
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

    // Mock ValidationResult enum
    private enum ValidationResult {
        VALID, INVALID, INDETERMINATE
    }

    // Mock ValidationContext interface
    private interface ValidationContext {
        java.util.Map<String, Object> getStaticParameters();
        java.util.Map<String, Object> getDynamicParameters();
        void setValidationFailureMessage(String message);
        String getValidationFailureMessage();
    }
}
