package gov.nist.toolkit.saml.subject;

import java.util.List;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.KeyInfoConfirmationDataType;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.DSAKeyValue;
import org.opensaml.xmlsec.signature.RSAKeyValue;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.xmlsec.signature.X509Data;

import javax.xml.namespace.QName;
import gov.nist.toolkit.saml.util.KeyInfoHelper401;

public class HolderOfKeySubjectConfirmationValidator extends AbstractSubjectConfirmationValidator {

    /**
     * The name of the ValidationContext static parameter carrying the PublicKey used by the presenter.
     */
    public static final String PRESENTER_KEY_PARAM = HolderOfKeySubjectConfirmationValidator.class.getName()
            + ".PresenterKey";

    /**
     * The name of the ValidationContext static parameter carrying the X509Certificate used by the presenter.
     */
    public static final String PRESENTER_CERT_PARAM = HolderOfKeySubjectConfirmationValidator.class.getName()
            + ".PresenterCertificate";

    /**
     * The name of the ValidationContext dynamic parameter carrying the KeyInfo that confirmed the subject.
     */
    public static final String CONFIRMED_KEY_INFO_PARAM = HolderOfKeySubjectConfirmationValidator.class.getName()
            + ".ConfirmedKeyInfo";

    public HolderOfKeySubjectConfirmationValidator() {
        super();
    }

    public HolderOfKeySubjectConfirmationValidator(String[] methods) {
        super();
        // Store methods if needed for validation
    }

    /**
     * Checks to see whether the schema type of the subject confirmation data, if present, is the required
     * {@link KeyInfoConfirmationDataType#TYPE_NAME}.
     * 
     * @param confirmation subject confirmation bearing the confirmation data to be checked
     * 
     * @return true if the confirmation data's schema type is correct, false otherwise
     */
    public boolean isValidConfirmationDataType(SubjectConfirmation confirmation) {
        if (confirmation == null || confirmation.getSubjectConfirmationData() == null) {
            return false;
        }
        
        QName confirmationDataSchemaType = confirmation.getSubjectConfirmationData().getSchemaType();
        if (confirmationDataSchemaType != null
                && !confirmationDataSchemaType.equals(KeyInfoConfirmationDataType.TYPE_NAME)) {
            return false;
        }
        
        return true;
    }

    /**
     * Validate the subject confirmation of a holder-of-key SAML assertion.
     * 
     * @param confirmation
     *            The subject confirmation method
     * @param assertion
     *            The SAML assertion
     * @param context
     *            Additional context for validation
     * @return ValidationResult indicating success or failure
     */
    protected ValidationResult doValidate(SubjectConfirmation confirmation, Assertion assertion,
            ValidationContext context) throws Exception {
        
        // Validate confirmation data type first
        if (!isValidConfirmationDataType(confirmation)) {
            String msg = String.format(
                    "Subject confirmation data is not of type '%s'", KeyInfoConfirmationDataType.TYPE_NAME);
            context.setValidationFailureMessage(msg);
            return ValidationResult.INVALID;
        }
        
        // Get certificate and key from context using proper parameter names
        java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) 
            context.getStaticParameters().get(PRESENTER_CERT_PARAM);
        java.security.PublicKey key = (java.security.PublicKey) 
            context.getStaticParameters().get(PRESENTER_KEY_PARAM);
        
        if (cert == null && key == null) {
            String msg = "Neither the presenter's certificate nor its public key were provided";
            context.setValidationFailureMessage(msg);
            return ValidationResult.INDETERMINATE;
        }
        
        // Get KeyInfo from SubjectConfirmationData
        KeyInfo keyInfo = getSubjectConfirmationKeyInfo(confirmation);
        if (keyInfo == null) {
            String msg = String.format(
                    "No key information for holder of key subject confirmation in assertion '%s'", assertion.getID());
            context.setValidationFailureMessage(msg);
            return ValidationResult.INVALID;
        }
        
        // Try to match key first, then certificate
        if (key != null && matchesKeyValue(key, keyInfo)) {
            context.getDynamicParameters().put(CONFIRMED_KEY_INFO_PARAM, keyInfo);
            return ValidationResult.VALID;
        }
        
        if (cert != null && matchesX509Certificate(cert, keyInfo)) {
            context.getDynamicParameters().put(CONFIRMED_KEY_INFO_PARAM, keyInfo);
            return ValidationResult.VALID;
        }
        
        String msg = "Neither presenter's key nor certificate matched KeyInfo in subject confirmation";
        context.setValidationFailureMessage(msg);
        return ValidationResult.INVALID;
    }
    
    /**
     * Extract KeyInfo from SubjectConfirmationData
     */
    private KeyInfo getSubjectConfirmationKeyInfo(SubjectConfirmation confirmation) {
        if (confirmation == null) {
            return null;
        }
        
        SubjectConfirmationData confirmationData = confirmation.getSubjectConfirmationData();
        if (confirmationData == null) {
            return null;
        }
        
        try {
            // In OpenSAML 4.0.1, KeyInfo is typically embedded as XML content in SubjectConfirmationData
            // We need to iterate through the unknown XML objects to find KeyInfo
            
            List<XMLObject> unknownChildren = confirmationData.getUnknownXMLObjects();
            if (unknownChildren != null && !unknownChildren.isEmpty()) {
                System.out.println("Found " + unknownChildren.size() + " unknown XML objects in SubjectConfirmationData");
                
                for (XMLObject xmlObject : unknownChildren) {
                    // Check if this XML object is a KeyInfo
                    if (xmlObject instanceof KeyInfo) {
                        System.out.println("Found KeyInfo directly in SubjectConfirmationData");
                        return (KeyInfo) xmlObject;
                    }
                    
                    // Check if this is a KeyInfoConfirmationDataType that contains KeyInfo
                    if (xmlObject.getSchemaType() != null) {
                        String schemaType = xmlObject.getSchemaType().toString();
                        if (schemaType.contains("KeyInfoConfirmation")) {
                            System.out.println("Found KeyInfoConfirmationDataType, extracting KeyInfo");
                            // Extract KeyInfo from the confirmation data structure
                            return extractKeyInfoFromConfirmationData(xmlObject);
                        }
                    }
                    
                    // Check element name for KeyInfo
                    if (xmlObject.getElementQName() != null) {
                        String elementName = xmlObject.getElementQName().toString();
                        if (elementName.contains("KeyInfo")) {
                            System.out.println("Found KeyInfo by element name: " + elementName);
                            return (KeyInfo) xmlObject;
                        }
                    }
                }
            }
            
            // Also check if there are any child elements that might contain KeyInfo
            List<XMLObject> orderedChildren = confirmationData.getOrderedChildren();
            if (orderedChildren != null && !orderedChildren.isEmpty()) {
                System.out.println("Found " + orderedChildren.size() + " ordered children in SubjectConfirmationData");
                
                for (XMLObject child : orderedChildren) {
                    if (child instanceof KeyInfo) {
                        System.out.println("Found KeyInfo in ordered children");
                        return (KeyInfo) child;
                    }
                }
            }
            
            System.out.println("No KeyInfo found in SubjectConfirmationData");
            
        } catch (Exception e) {
            System.out.println("Error extracting KeyInfo from SubjectConfirmationData: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Extract KeyInfo from a confirmation data structure
     */
    private KeyInfo extractKeyInfoFromConfirmationData(XMLObject confirmationData) {
        try {
            // Try to get KeyInfo from the confirmation data structure
            // This might involve accessing specific properties or child elements
            
            // Check if the confirmation data itself contains KeyInfo as a child
            List<XMLObject> children = confirmationData.getOrderedChildren();
            if (children != null) {
                for (XMLObject child : children) {
                    if (child instanceof KeyInfo) {
                        return (KeyInfo) child;
                    }
                }
            }
            
            // Try to access KeyInfo through reflection if it's a property
            try {
                java.lang.reflect.Method[] methods = confirmationData.getClass().getMethods();
                for (java.lang.reflect.Method method : methods) {
                    if (method.getName().contains("KeyInfo") && method.getParameterCount() == 0) {
                        Object result = method.invoke(confirmationData);
                        if (result instanceof KeyInfo) {
                            return (KeyInfo) result;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Reflection approach failed: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("Error in extractKeyInfoFromConfirmationData: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Match the provided public key with the key information in KeyInfo
     */
    private boolean matchesKeyValue(java.security.PublicKey key, KeyInfo keyInfo) {
        if (key == null || keyInfo == null) {
            return false;
        }
        
        List<KeyValue> keyValues = keyInfo.getKeyValues();
        if (keyValues == null || keyValues.isEmpty()) {
            System.out.println("KeyInfo contained no KeyValue children, skipping KeyValue match");
            return false;
        }
        
        try {
            String keyAlgo = key.getAlgorithm();
            if ("DSA".equals(keyAlgo)) {
                for (KeyValue keyValue : keyValues) {
                    DSAKeyValue dsaKeyValue = keyValue.getDSAKeyValue();
                    if (dsaKeyValue != null) {
                        // ✅ REAL CRYPTOGRAPHIC COMPARISON!
                        PublicKey extractedKey = KeyInfoHelper401.getDSAKey(dsaKeyValue);
                        if (KeyInfoHelper401.keysEqual(key, extractedKey)) {
                            System.out.println("✓ Matched DSAKeyValue - cryptographic comparison successful");
                            return true;
                        }
                    }
                }
            }
            
            if ("RSA".equals(keyAlgo)) {
                for (KeyValue keyValue : keyValues) {
                    RSAKeyValue rsaKeyValue = keyValue.getRSAKeyValue();
                    if (rsaKeyValue != null) {
                        // ✅ REAL CRYPTOGRAPHIC COMPARISON!
                        PublicKey extractedKey = KeyInfoHelper401.getRSAKey(rsaKeyValue);
                        if (KeyInfoHelper401.keysEqual(key, extractedKey)) {
                            System.out.println("✓ Matched RSAKeyValue - cryptographic comparison successful");
                            return true;
                        }
                    }
                }
            }
        } catch (KeyException e) {
            System.out.println("KeyInfo contained key value that cannot be parsed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error during cryptographic key matching: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Match the provided certificate with the key information in KeyInfo
     */
    private boolean matchesX509Certificate(java.security.cert.X509Certificate cert, KeyInfo keyInfo) {
        if (cert == null || keyInfo == null) {
            return false;
        }
        
        try {
            // Check for X509Data in KeyInfo
            List<X509Data> x509Datas = keyInfo.getX509Datas();
            if (x509Datas != null && !x509Datas.isEmpty()) {
                System.out.println("Found X509Data in KeyInfo, attempting certificate match");
                
                for (X509Data data : x509Datas) {
                    List<org.opensaml.xmlsec.signature.X509Certificate> xmlCertificates = data.getX509Certificates();
                    if (xmlCertificates != null && !xmlCertificates.isEmpty()) {
                        for (org.opensaml.xmlsec.signature.X509Certificate xmlCertificate : xmlCertificates) {
                            // ✅ REAL CRYPTOGRAPHIC COMPARISON!
                            X509Certificate extractedCert = KeyInfoHelper401.getCertificate(xmlCertificate);
                            if (cert.equals(extractedCert)) {
                                System.out.println("✓ Matched X509Certificate - cryptographic comparison successful");
                                return true;
                            }
                        }
                    }
                }
            }
            
            // Check for KeyValue in KeyInfo (as fallback - compare certificate's public key)
            List<KeyValue> keyValues = keyInfo.getKeyValues();
            if (keyValues != null && !keyValues.isEmpty()) {
                System.out.println("Found KeyValue in KeyInfo, attempting certificate-to-key match");
                
                for (KeyValue keyValue : keyValues) {
                    if (keyValue.getDSAKeyValue() != null) {
                        // ✅ REAL CRYPTOGRAPHIC COMPARISON!
                        PublicKey extractedKey = KeyInfoHelper401.getDSAKey(keyValue.getDSAKeyValue());
                        if (KeyInfoHelper401.keysEqual(cert.getPublicKey(), extractedKey)) {
                            System.out.println("✓ Matched certificate to DSAKeyValue - cryptographic comparison successful");
                            return true;
                        }
                    }
                    if (keyValue.getRSAKeyValue() != null) {
                        // ✅ REAL CRYPTOGRAPHIC COMPARISON!
                        PublicKey extractedKey = KeyInfoHelper401.getRSAKey(keyValue.getRSAKeyValue());
                        if (KeyInfoHelper401.keysEqual(cert.getPublicKey(), extractedKey)) {
                            System.out.println("✓ Matched certificate to RSAKeyValue - cryptographic comparison successful");
                            return true;
                        }
                    }
                }
            }
            
        } catch (java.security.cert.CertificateException e) {
            System.out.println("KeyInfo contained certificate value that cannot be parsed: " + e.getMessage());
        } catch (KeyException e) {
            System.out.println("KeyInfo contained key value that cannot be parsed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error during certificate matching: " + e.getMessage());
        }
        
        return false;
    }
}
