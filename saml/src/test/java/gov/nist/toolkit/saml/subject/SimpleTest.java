package gov.nist.toolkit.saml.subject;

import gov.nist.toolkit.saml.builder.OpenSAMLInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test to verify compilation fixes work
 */
public class SimpleTest {

    @BeforeAll
    static void setup() {
        OpenSAMLInitializer.ensureInitializedUnchecked();
        System.out.println("=== Simple Test ===");
        System.out.println("Testing basic compilation fixes");
    }

    @Test
    @DisplayName("Test basic SAML parsing functionality")
    public void testBasicSamlParsing() throws Exception {
        System.out.println("\n--- Test: Basic SAML Parsing ---");
        
        // Test that we can parse a simple assertion
        String sampleAssertion = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_sample123\" IssueInstant=\"2023-03-27T10:00:00.000Z\" Version=\"2.0\">" +
            "<saml2:Issuer>https://test-sts.example.com</saml2:Issuer>" +
            "<saml2:Subject>" +
            "<saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\">test-user</saml2:NameID>" +
            "</saml2:Subject>" +
            "<saml2:Conditions NotBefore=\"2023-03-27T09:55:00.000Z\" NotOnOrAfter=\"2023-03-27T10:05:00.000Z\"/>" +
            "</saml2:Assertion>";
        
        Assertion assertion = parseSAMLAssertion(sampleAssertion);
        assertNotNull(assertion, "SAML assertion should be parsed successfully");
        
        System.out.println("Successfully parsed basic SAML assertion");
        System.out.println("  - ID: " + assertion.getID());
        System.out.println("  - Issuer: " + assertion.getIssuer());
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
}
