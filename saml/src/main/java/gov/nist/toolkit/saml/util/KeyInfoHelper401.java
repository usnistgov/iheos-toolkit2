package gov.nist.toolkit.saml.util;

import java.math.BigInteger;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import org.opensaml.xmlsec.signature.DSAKeyValue;
import org.opensaml.xmlsec.signature.RSAKeyValue;

/**
 * KeyInfoHelper implementation for OpenSAML 4.0.1
 * 
 * This class provides the same functionality as the original OpenSAML 2.x KeyInfoHelper
 * but adapted for the OpenSAML 4.0.1 API structure.
 */
public class KeyInfoHelper401 {

    /**
     * Extracts a DSA public key from a DSAKeyValue element.
     * 
     * @param dsaKeyValue the DSAKeyValue element
     * @return the reconstructed DSA public key
     * @throws KeyException if there is an error reconstructing the key
     */
    public static PublicKey getDSAKey(DSAKeyValue dsaKeyValue) throws KeyException {
        if (dsaKeyValue == null) {
            throw new KeyException("DSAKeyValue is null");
        }

        try {
            // Extract DSA parameters from OpenSAML 4.0.1 structure
            BigInteger p = new BigInteger(1, Base64.getDecoder().decode(dsaKeyValue.getP().getValue()));
            BigInteger q = new BigInteger(1, Base64.getDecoder().decode(dsaKeyValue.getQ().getValue()));
            BigInteger g = new BigInteger(1, Base64.getDecoder().decode(dsaKeyValue.getG().getValue()));
            BigInteger y = new BigInteger(1, Base64.getDecoder().decode(dsaKeyValue.getY().getValue()));

            // Create DSA public key specification
            DSAPublicKeySpec keySpec = new DSAPublicKeySpec(y, p, q, g);
            
            // Generate the public key
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            return keyFactory.generatePublic(keySpec);
            
        } catch (NoSuchAlgorithmException e) {
            throw new KeyException("DSA algorithm not available", e);
        } catch (InvalidKeySpecException e) {
            throw new KeyException("Invalid DSA key specification", e);
        } catch (Exception e) {
            throw new KeyException("Error extracting DSA key: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts an RSA public key from an RSAKeyValue element.
     * 
     * @param rsaKeyValue the RSAKeyValue element
     * @return the reconstructed RSA public key
     * @throws KeyException if there is an error reconstructing the key
     */
    public static PublicKey getRSAKey(RSAKeyValue rsaKeyValue) throws KeyException {
        if (rsaKeyValue == null) {
            throw new KeyException("RSAKeyValue is null");
        }

        try {
            // Extract RSA parameters from OpenSAML 4.0.1 structure
            BigInteger modulus = new BigInteger(1, Base64.getDecoder().decode(rsaKeyValue.getModulus().getValue()));
            BigInteger exponent = new BigInteger(1, Base64.getDecoder().decode(rsaKeyValue.getExponent().getValue()));

            // Create RSA public key specification
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
            
            // Generate the public key
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
            
        } catch (NoSuchAlgorithmException e) {
            throw new KeyException("RSA algorithm not available", e);
        } catch (InvalidKeySpecException e) {
            throw new KeyException("Invalid RSA key specification", e);
        } catch (Exception e) {
            throw new KeyException("Error extracting RSA key: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts an X509Certificate from an OpenSAML X509Certificate element.
     * 
     * @param xmlCertificate the OpenSAML X509Certificate element
     * @return the reconstructed X509Certificate
     * @throws CertificateException if there is an error reconstructing the certificate
     */
    public static X509Certificate getCertificate(org.opensaml.xmlsec.signature.X509Certificate xmlCertificate) throws CertificateException {
        if (xmlCertificate == null) {
            throw new CertificateException("X509Certificate is null");
        }

        try {
            // Get the certificate value from OpenSAML 4.0.1 structure
            String certValue = xmlCertificate.getValue();
            if (certValue == null || certValue.trim().isEmpty()) {
                throw new CertificateException("Certificate value is null or empty");
            }

            // Remove PEM headers/footers if present and decode
            String base64Cert = certValue.replaceAll("-----BEGIN CERTIFICATE-----", "")
                                       .replaceAll("-----END CERTIFICATE-----", "")
                                       .replaceAll("\\s", "");
            
            byte[] certBytes = Base64.getDecoder().decode(base64Cert);
            
            // Generate certificate from bytes
            java.security.cert.CertificateFactory certFactory = 
                java.security.cert.CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFactory.generateCertificate(new java.io.ByteArrayInputStream(certBytes));
            
        } catch (Exception e) {
            throw new CertificateException("Error extracting certificate: " + e.getMessage(), e);
        }
    }

    /**
     * Compares two public keys for cryptographic equality.
     * 
     * @param key1 first public key
     * @param key2 second public key
     * @return true if the keys are cryptographically equal
     */
    public static boolean keysEqual(PublicKey key1, PublicKey key2) {
        if (key1 == null || key2 == null) {
            return key1 == key2;
        }
        
        // Check algorithm first
        if (!key1.getAlgorithm().equals(key2.getAlgorithm())) {
            return false;
        }
        
        // For RSA keys, compare modulus and exponent
        if (key1 instanceof RSAPublicKey && key2 instanceof RSAPublicKey) {
            RSAPublicKey rsa1 = (RSAPublicKey) key1;
            RSAPublicKey rsa2 = (RSAPublicKey) key2;
            return rsa1.getModulus().equals(rsa2.getModulus()) &&
                   rsa1.getPublicExponent().equals(rsa2.getPublicExponent());
        }
        
        // For DSA keys, compare parameters
        if (key1 instanceof DSAPublicKey && key2 instanceof DSAPublicKey) {
            DSAPublicKey dsa1 = (DSAPublicKey) key1;
            DSAPublicKey dsa2 = (DSAPublicKey) key2;
            return dsa1.getY().equals(dsa2.getY()) &&
                   dsa1.getParams().getP().equals(dsa2.getParams().getP()) &&
                   dsa1.getParams().getQ().equals(dsa2.getParams().getQ()) &&
                   dsa1.getParams().getG().equals(dsa2.getParams().getG());
        }
        
        // Fall back to standard equals method
        return key1.equals(key2);
    }
}
