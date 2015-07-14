package gov.nist.toolkit.saml.util;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

/**
 * @author Srinivasarao.Eadara
 *
 */
public class SAMLKeyInfo {

    /**
     * Certificates
     */
    private X509Certificate[] certs;
    
    /**
     * Key bytes (e.g.: held in an encrypted key)
     */
    private byte[] secret;
    
    /**
     * The public key {e.g.: held in a ds:KeyInfo).
     */
    private PublicKey publicKey;
    
    public SAMLKeyInfo(X509Certificate[] certs) {
        this.certs = certs;
    }
    
    public SAMLKeyInfo(byte[] secret) {
        this.secret = secret;
    }
    
    public SAMLKeyInfo(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public X509Certificate[] getCerts() {
        return certs;
    }
    
    public void setCerts(X509Certificate[] certs) {
        this.certs = certs;
    }
    
    public byte[] getSecret() {
        return secret;
    }
    
    public void setSecret(byte[] secret) {
        this.secret = secret;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }
    
    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

}
