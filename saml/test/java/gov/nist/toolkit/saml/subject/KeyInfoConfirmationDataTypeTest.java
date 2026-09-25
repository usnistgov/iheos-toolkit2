package gov.nist.toolkit.saml.subject;

import gov.nist.toolkit.saml.builder.OpenSamlBootStrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;

import javax.xml.namespace.QName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify KeyInfoConfirmationDataType validation functionality
 */
public class KeyInfoConfirmationDataTypeTest {

    @BeforeAll
    static void setup() {
        OpenSamlBootStrap.bootstrap();
    }

    @Test
    @DisplayName("Test valid KeyInfoConfirmationDataType")
    public void testValidKeyInfoConfirmationDataType() throws Exception {
        System.out.println("--- Testing Valid KeyInfoConfirmationDataType ---");
        
        // Create assertion with correct KeyInfoConfirmationDataType
        String samlAssertion = createAssertionWithCorrectKeyInfoType();
        Assertion assertion = parseSAMLAssertion(samlAssertion);
        
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        
        // Test the validation method
        boolean isValid = validator.isValidConfirmationDataType(confirmation);
        
        assertTrue(isValid, "KeyInfoConfirmationDataType should be valid");
        System.out.println("✓ Valid KeyInfoConfirmationDataType correctly identified");
    }

    @Test
    @DisplayName("Test invalid KeyInfoConfirmationDataType")
    public void testInvalidKeyInfoConfirmationDataType() throws Exception {
        System.out.println("--- Testing Invalid KeyInfoConfirmationDataType ---");
        
        // Create assertion with incorrect schema type
        String samlAssertion = createAssertionWithIncorrectKeyInfoType();
        Assertion assertion = parseSAMLAssertion(samlAssertion);
        
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        
        // Test the validation method
        boolean isValid = validator.isValidConfirmationDataType(confirmation);
        
        assertFalse(isValid, "Incorrect KeyInfoConfirmationDataType should be invalid");
        System.out.println("✓ Invalid KeyInfoConfirmationDataType correctly identified");
    }

    @Test
    @DisplayName("Test null SubjectConfirmation")
    public void testNullSubjectConfirmation() throws Exception {
        System.out.println("--- Testing Null SubjectConfirmation ---");
        
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        
        // Test with null confirmation
        boolean isValid = validator.isValidConfirmationDataType(null);
        
        assertFalse(isValid, "Null SubjectConfirmation should be invalid");
        System.out.println("✓ Null SubjectConfirmation correctly handled");
    }

    @Test
    @DisplayName("Test null SubjectConfirmationData")
    public void testNullSubjectConfirmationData() throws Exception {
        System.out.println("--- Testing Null SubjectConfirmationData ---");
        
        // Create assertion with null SubjectConfirmationData
        String samlAssertion = createAssertionWithNullConfirmationData();
        Assertion assertion = parseSAMLAssertion(samlAssertion);
        
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        
        // Test the validation method
        boolean isValid = validator.isValidConfirmationDataType(confirmation);
        
        assertFalse(isValid, "Null SubjectConfirmationData should be invalid");
        System.out.println("✓ Null SubjectConfirmationData correctly handled");
    }

    @Test
    @DisplayName("Test complete validation with KeyInfoConfirmationDataType")
    public void testCompleteValidationWithKeyInfoType() throws Exception {
        System.out.println("--- Testing Complete Validation with KeyInfoConfirmationDataType ---");
        
        // Create assertion with correct KeyInfoConfirmationDataType
        String samlAssertion = createAssertionWithCorrectKeyInfoType();
        Assertion assertion = parseSAMLAssertion(samlAssertion);
        
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        MockValidationContext context = new MockValidationContext();
        
        // Add test key to context
        java.security.KeyPair keyPair = java.security.KeyPairGenerator.getInstance("RSA").generateKeyPair();
        context.setStaticParameter(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, keyPair.getPublic());
        
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        
        // Use reflection to call the private doValidate method
        java.lang.reflect.Method method = HolderOfKeySubjectConfirmationValidator.class.getDeclaredMethod("doValidate", 
            SubjectConfirmation.class, Assertion.class, ValidationContext.class);
        method.setAccessible(true);
        
        ValidationResult result = (ValidationResult) method.invoke(validator, confirmation, assertion, context);
        
        // Should pass KeyInfoConfirmationDataType validation and reach key matching
        System.out.println("Validation result: " + result);
        System.out.println("✓ Complete validation with KeyInfoConfirmationDataType completed");
    }

    // Helper methods

    private String createAssertionWithCorrectKeyInfoType() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "ID=\"_correct_type123\" IssueInstant=\"2023-03-27T10:00:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://test-sts.example.com</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">test-user</saml2:NameID>" +
            "<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\">" +
            "<saml2:SubjectConfirmationData NotOnOrAfter=\"2023-03-27T10:05:00.000Z\" " +
            "xsi:type=\"saml2:KeyInfoConfirmationDataType\">" +
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

    private String createAssertionWithIncorrectKeyInfoType() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "ID=\"_incorrect_type123\" IssueInstant=\"2023-03-27T10:00:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://test-sts.example.com</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">test-user</saml2:NameID>" +
            "<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\">" +
            "<saml2:SubjectConfirmationData NotOnOrAfter=\"2023-03-27T10:05:00.000Z\" " +
            "xsi:type=\"saml2:IncorrectDataType\">" +
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

    private String createAssertionWithNullConfirmationData() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_null_data123\" IssueInstant=\"2023-03-27T10:00:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://test-sts.example.com</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">test-user</saml2:NameID>" +
            "<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\">" +
            "</saml2:SubjectConfirmation>" +
            "</saml2:Subject>" +
            "<saml2:Conditions NotBefore=\"2023-03-27T09:55:00.000Z\" NotOnOrAfter=\"2023-03-27T10:05:00.000Z\"/>" +
            "</saml2:Assertion>";
    }

    private Assertion parseSAMLAssertion(String xml) throws Exception {
        javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        org.w3c.dom.Element documentElement = builder.parse(inputStream).getDocumentElement();
        
        return (Assertion) org.opensaml.core.xml.io.UnmarshallingUtil.unmarshall(documentElement);
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
