package gov.nist.toolkit.saml.subject;

import gov.nist.toolkit.saml.builder.OpenSamlBootStrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.RSAKeyValue;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test for KeyInfo extraction from SAML assertions
 * Tests real-world scenarios with Holder-of-Key subject confirmation
 */
public class KeyInfoExtractionTest {

    @BeforeAll
    static void setup() {
        OpenSamlBootStrap.bootstrap();
    }

    @Test
    @DisplayName("Test KeyInfo extraction with RSA KeyValue")
    public void testKeyInfoExtractionWithRSAKeyValue() throws Exception {
        // Create a test assertion with RSA KeyValue in KeyInfo
        Assertion assertion = createAssertionWithRSAKeyInfo();
        
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        MockValidationContext context = new MockValidationContext();
        
        // Add test RSA public key to context
        KeyPair keyPair = generateRSAKeyPair();
        context.setStaticParameter(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, keyPair.getPublic());
        
        // Get the first subject confirmation
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        
        // Test the validation
        ValidationResult result = validator.doValidate(confirmation, assertion, context);
        
        // Verify the result
        assertEquals(ValidationResult.VALID, result);
        assertNotNull(context.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM));
    }

    @Test
    @DisplayName("Test KeyInfo extraction with X509Certificate")
    public void testKeyInfoExtractionWithX509Certificate() throws Exception {
        // Create a test assertion with X509Certificate in KeyInfo
        Assertion assertion = createAssertionWithX509KeyInfo();
        
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        MockValidationContext context = new MockValidationContext();
        
        // Add test certificate to context
        X509Certificate cert = generateTestCertificate();
        context.setStaticParameter(HolderOfKeySubjectConfirmationValidator.PRESENTER_CERT_PARAM, cert);
        
        // Get the first subject confirmation
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        
        // Test the validation
        ValidationResult result = validator.doValidate(confirmation, assertion, context);
        
        // Verify the result
        assertEquals(ValidationResult.VALID, result);
        assertNotNull(context.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM));
    }

    @Test
    @DisplayName("Test KeyInfo extraction from real SAML assertion XML")
    public void testKeyInfoExtractionFromRealXML() throws Exception {
        // Real SAML assertion XML with KeyInfo (simulated)
        String samlXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_123456789\" IssueInstant=\"2023-01-01T12:00:00Z\" Version=\"2.0\">" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">test-user</saml2:NameID>" +
            "<saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\">" +
            "<saml2:SubjectConfirmationData NotOnOrAfter=\"2023-01-01T12:05:00Z\">" +
            "<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
            "<ds:KeyValue>" +
            "<ds:RSAKeyValue>" +
            "<ds:Modulus>BASE64_MODULUS_HERE</ds:Modulus>" +
            "<ds:Exponent>AQAB</ds:Exponent>" +
            "</ds:RSAKeyValue>" +
            "</ds:KeyValue>" +
            "</ds:KeyInfo>" +
            "</saml2:SubjectConfirmationData>" +
            "</saml2:SubjectConfirmation>" +
            "</saml2:Subject>" +
            "<saml2:Conditions NotBefore=\"2023-01-01T11:55:00Z\" NotOnOrAfter=\"2023-01-01T12:05:00Z\"/>" +
            "</saml2:Assertion>";

        // Parse the XML into an Assertion object
        Assertion assertion = parseSAMLAssertion(samlXml);
        
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        MockValidationContext context = new MockValidationContext();
        
        // Add test public key to context
        KeyPair keyPair = generateRSAKeyPair();
        context.setStaticParameter(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, keyPair.getPublic());
        
        // Get the subject confirmation
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        
        // Test the validation
        ValidationResult result = validator.doValidate(confirmation, assertion, context);
        
        // Verify the result
        assertEquals(ValidationResult.VALID, result);
    }

    @Test
    @DisplayName("Test KeyInfo extraction failure with missing KeyInfo")
    public void testKeyInfoExtractionFailureMissingKeyInfo() throws Exception {
        // Create a test assertion without KeyInfo
        Assertion assertion = createAssertionWithoutKeyInfo();
        
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        MockValidationContext context = new MockValidationContext();
        
        // Add test public key to context
        KeyPair keyPair = generateRSAKeyPair();
        context.setStaticParameter(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, keyPair.getPublic());
        
        // Get the first subject confirmation
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        
        // Test the validation - should fail
        ValidationResult result = validator.doValidate(confirmation, assertion, context);
        
        // Verify the result
        assertEquals(ValidationResult.INVALID, result);
        assertTrue(context.getValidationFailureMessage().contains("No key information"));
    }

    @Test
    @DisplayName("Test KeyInfo extraction failure with wrong key")
    public void testKeyInfoExtractionFailureWrongKey() throws Exception {
        // Create a test assertion with RSA KeyInfo
        Assertion assertion = createAssertionWithRSAKeyInfo();
        
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        MockValidationContext context = new MockValidationContext();
        
        // Add a different RSA public key to context
        KeyPair differentKeyPair = generateRSAKeyPair(); // Different key
        context.setStaticParameter(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, differentKeyPair.getPublic());
        
        // Get the first subject confirmation
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        
        // Test the validation - should fail due to key mismatch
        ValidationResult result = validator.doValidate(confirmation, assertion, context);
        
        // Verify the result
        assertEquals(ValidationResult.INVALID, result);
        assertTrue(context.getValidationFailureMessage().contains("Neither presenter's key nor certificate matched"));
    }

    @Test
    @DisplayName("Test KeyInfo extraction with no presenter key provided")
    public void testKeyInfoExtractionNoPresenterKey() throws Exception {
        // Create a test assertion with RSA KeyInfo
        Assertion assertion = createAssertionWithRSAKeyInfo();
        
        HolderOfKeySubjectConfirmationValidator validator = new HolderOfKeySubjectConfirmationValidator();
        MockValidationContext context = new MockValidationContext();
        
        // Don't add any key or certificate to context
        
        // Get the first subject confirmation
        SubjectConfirmation confirmation = assertion.getSubject().getSubjectConfirmations().get(0);
        
        // Test the validation - should fail due to missing presenter key
        ValidationResult result = validator.doValidate(confirmation, assertion, context);
        
        // Verify the result
        assertEquals(ValidationResult.INDETERMINATE, result);
        assertTrue(context.getValidationFailureMessage().contains("Neither the presenter's certificate nor its public key were provided"));
    }

    // Helper methods

    private Assertion createAssertionWithRSAKeyInfo() throws Exception {
        Assertion assertion = buildAssertion();
        
        // Create SubjectConfirmation with KeyInfo containing RSA KeyValue
        SubjectConfirmation confirmation = buildSubjectConfirmationWithRSAKeyInfo();
        assertion.getSubject().getSubjectConfirmations().add(confirmation);
        
        return assertion;
    }

    private Assertion createAssertionWithX509KeyInfo() throws Exception {
        Assertion assertion = buildAssertion();
        
        // Create SubjectConfirmation with KeyInfo containing X509Certificate
        SubjectConfirmation confirmation = buildSubjectConfirmationWithX509KeyInfo();
        assertion.getSubject().getSubjectConfirmations().add(confirmation);
        
        return assertion;
    }

    private Assertion createAssertionWithoutKeyInfo() throws Exception {
        Assertion assertion = buildAssertion();
        
        // Create SubjectConfirmation without KeyInfo
        SubjectConfirmation confirmation = buildSubjectConfirmationWithoutKeyInfo();
        assertion.getSubject().getSubjectConfirmations().add(confirmation);
        
        return assertion;
    }

    private Assertion buildAssertion() {
        // Create assertion builder
        Assertion assertion = new org.opensaml.saml.saml2.core.impl.AssertionBuilder().buildObject();
        
        assertion.setID("_" + System.currentTimeMillis());
        assertion.setIssueInstant(new java.util.Date());
        assertion.setVersion(SAMLVersion.VERSION_20);
        
        // Create subject
        Subject subject = new org.opensaml.saml.saml2.core.impl.SubjectBuilder().buildObject();
        
        // Create NameID
        NameID nameID = new org.opensaml.saml.saml2.core.impl.NameIDBuilder().buildObject();
        nameID.setValue("test-user");
        nameID.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
        subject.setNameID(nameID);
        
        assertion.setSubject(subject);
        
        // Create conditions
        Conditions conditions = new org.opensaml.saml.saml2.core.impl.ConditionsBuilder().buildObject();
        conditions.setNotBefore(new java.util.Date());
        conditions.setNotOnOrAfter(new java.util.Date(System.currentTimeMillis() + 300000)); // 5 minutes
        assertion.setConditions(conditions);
        
        return assertion;
    }

    private SubjectConfirmation buildSubjectConfirmationWithRSAKeyInfo() throws Exception {
        SubjectConfirmation confirmation = new org.opensaml.saml.saml2.core.impl.SubjectConfirmationBuilder().buildObject();
        confirmation.setMethod("urn:oasis:names:tc:SAML:2.0:cm:holder-of-key");
        
        SubjectConfirmationData confirmationData = new org.opensaml.saml.saml2.core.impl.SubjectConfirmationDataBuilder().buildObject();
        confirmationData.setNotOnOrAfter(new java.util.Date(System.currentTimeMillis() + 300000));
        
        // Create KeyInfo with RSA KeyValue
        KeyInfo keyInfo = new org.opensaml.xmlsec.signature.impl.KeyInfoBuilder().buildObject();
        
        KeyValue keyValue = new org.opensaml.xmlsec.signature.impl.KeyValueBuilder().buildObject();
        RSAKeyValue rsaKeyValue = new org.opensaml.xmlsec.signature.impl.RSAKeyValueBuilder().buildObject();
        
        // Generate RSA key pair for the test
        KeyPair keyPair = generateRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        
        if (publicKey instanceof java.security.interfaces.RSAPublicKey) {
            java.security.interfaces.RSAPublicKey rsaPublicKey = (java.security.interfaces.RSAPublicKey) publicKey;
            
            // Create modulus and exponent elements
            org.opensaml.xmlsec.signature.impl.ModulusBuilder modulusBuilder = new org.opensaml.xmlsec.signature.impl.ModulusBuilder();
            org.opensaml.xmlsec.signature.impl.ExponentBuilder exponentBuilder = new org.opensaml.xmlsec.signature.impl.ExponentBuilder();
            
            modulusBuilder.buildObject().setBase64EncodedValue(Base64.getEncoder().encodeToString(rsaPublicKey.getModulus().toByteArray()));
            exponentBuilder.buildObject().setBase64EncodedValue(Base64.getEncoder().encodeToString(rsaPublicKey.getPublicExponent().toByteArray()));
            
            rsaKeyValue.setModulus(modulusBuilder.buildObject());
            rsaKeyValue.setExponent(exponentBuilder.buildObject());
        }
        
        keyValue.setRSAKeyValue(rsaKeyValue);
        keyInfo.getKeyValues().add(keyValue);
        
        // Add KeyInfo to confirmation data (as unknown XML object)
        confirmationData.getUnknownXMLObjects().add(keyInfo);
        
        confirmation.setSubjectConfirmationData(confirmationData);
        
        return confirmation;
    }

    private SubjectConfirmation buildSubjectConfirmationWithX509KeyInfo() throws Exception {
        SubjectConfirmation confirmation = new org.opensaml.saml.saml2.core.impl.SubjectConfirmationBuilder().buildObject();
        confirmation.setMethod("urn:oasis:names:tc:SAML:2.0:cm:holder-of-key");
        
        SubjectConfirmationData confirmationData = new org.opensaml.saml.saml2.core.impl.SubjectConfirmationDataBuilder().buildObject();
        confirmationData.setNotOnOrAfter(new java.util.Date(System.currentTimeMillis() + 300000));
        
        // Create KeyInfo with X509Certificate
        KeyInfo keyInfo = new org.opensaml.xmlsec.signature.impl.KeyInfoBuilder().buildObject();
        
        X509Data x509Data = new org.opensaml.xmlsec.signature.impl.X509DataBuilder().buildObject();
        X509Certificate xmlCertificate = new org.opensaml.xmlsec.signature.impl.X509CertificateBuilder().buildObject();
        
        // Generate test certificate
        X509Certificate cert = generateTestCertificate();
        xmlCertificate.setValue(cert.getEncoded());
        
        x509Data.getX509Certificates().add(xmlCertificate);
        keyInfo.getX509Datas().add(x509Data);
        
        // Add KeyInfo to confirmation data (as unknown XML object)
        confirmationData.getUnknownXMLObjects().add(keyInfo);
        
        confirmation.setSubjectConfirmationData(confirmationData);
        
        return confirmation;
    }

    private SubjectConfirmation buildSubjectConfirmationWithoutKeyInfo() {
        SubjectConfirmation confirmation = new org.opensaml.saml.saml2.core.impl.SubjectConfirmationBuilder().buildObject();
        confirmation.setMethod("urn:oasis:names:tc:SAML:2.0:cm:holder-of-key");
        
        SubjectConfirmationData confirmationData = new org.opensaml.saml.saml2.core.impl.SubjectConfirmationDataBuilder().buildObject();
        confirmationData.setNotOnOrAfter(new java.util.Date(System.currentTimeMillis() + 300000));
        
        // No KeyInfo added
        
        confirmation.setSubjectConfirmationData(confirmationData);
        
        return confirmation;
    }

    private KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    private X509Certificate generateTestCertificate() throws Exception {
        // This is a simplified test certificate generation
        // In a real scenario, you would use a proper certificate generator
        KeyPair keyPair = generateRSAKeyPair();
        
        // For testing purposes, we'll create a mock certificate
        // In production, use Bouncy Castle or similar for proper certificate generation
        throw new UnsupportedOperationException("Test certificate generation not implemented - use real certificate for testing");
    }

    private Assertion parseSAMLAssertion(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Element documentElement = builder.parse(inputStream).getDocumentElement();
        
        // Unmarshal the SAML assertion
        return (Assertion) org.opensaml.core.xml.io.UnmarshallingUtil.unmarshall(documentElement);
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

    // Mock ValidationResult enum (simplified for testing)
    private enum ValidationResult {
        VALID, INVALID, INDETERMINATE
    }
}
