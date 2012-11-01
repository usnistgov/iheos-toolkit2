package gov.nist.toolkit.saml.builder.bean;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.w3c.dom.Element;


/**
 * @author Srinivasarao.Eadara
 *
 */
public class KeyInfoBean {
    
    public enum CERT_IDENTIFIER {
        X509_CERT, X509_ISSUER_SERIAL, KEY_VALUE
    }
    
    private X509Certificate cert;
    private CERT_IDENTIFIER certIdentifier = CERT_IDENTIFIER.KEY_VALUE;
    private PublicKey publicKey;
    private Element keyInfoElement;

    /**
     * Constructor KeyInfoBean creates a new KeyInfoBean instance.
     */
    public KeyInfoBean() {
    }

    /**
     * Method getCertificate returns the certificate of this KeyInfoBean object.
     *
     * @return the cert (type X509Certificate) of this KeyInfoBean object.
     */
    public X509Certificate getCertificate() {
        return cert;
    }

    /**
     * Method setCertificate sets the cert of this KeyInfoBean object.
     *
     * @param cert the cert of this KeyInfoBean object.
     */
    public void setCertificate(X509Certificate cert) {
        this.cert = cert;
    }
    
    /**
     * Method getPublicKey returns the public key of this KeyInfoBean object.
     *
     * @return the publicKey (type PublicKey) of this KeyInfoBean object.
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Method setPublicKey sets the publicKey of this KeyInfoBean object.
     *
     * @param publicKey the publicKey of this KeyInfoBean object.
     */
    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
    
    /**
     * Method getCertIdentifer returns the cert identifer of this KeyInfoBean object.
     *
     * @return the certIdentifier (type CERT_IDENTIFIER) of this KeyInfoBean object.
     */
    public CERT_IDENTIFIER getCertIdentifer() {
        return certIdentifier;
    }

    /**
     * Method setCertIdentifer sets the cert identifier of this KeyInfoBean object.
     *
     * @param certIdentifier the certIdentifier of this KeyInfoBean object.
     */
    public void setCertIdentifer(CERT_IDENTIFIER certIdentifier) {
        this.certIdentifier = certIdentifier;
    }
    
    /**
     * Method getElement returns the DOM Element of this KeyInfoBean object.
     *
     * @return the keyInfoElement (type Element) of this KeyInfoBean object.
     */
    public Element getElement() {
        return keyInfoElement;
    }

    /**
     * Method setElement sets the DOM Element of this KeyInfoBean object.
     *
     * @param keyInfoElement the DOM Element of this KeyInfoBean object.
     */
    public void setElement(Element keyInfoElement) {
        this.keyInfoElement = keyInfoElement;
    }
    
    /**
     * Method equals ...
     *
     * @param o of type Object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyInfoBean)) return false;

        KeyInfoBean that = (KeyInfoBean) o;

        if (certIdentifier != that.certIdentifier) return false;
        if (cert == null && that.cert != null) {
            return false;
        } else if (cert != null && !cert.equals(that.cert)) {
            return false;
        }
        
        if (publicKey == null && that.publicKey != null) {
            return false;
        } else if (publicKey != null && !publicKey.equals(that.publicKey)) {
            return false;
        }
        
        if (keyInfoElement == null && that.keyInfoElement != null) {
            return false;
        } else if (keyInfoElement != null && !keyInfoElement.equals(that.keyInfoElement)) {
            return false;
        }

        return true;
    }

    /**
     * @return the hashCode of this object
     */
    @Override
    public int hashCode() {
        int result = certIdentifier.hashCode();
        if (cert != null) {
            result = 31 * result + cert.hashCode();
        }
        if (publicKey != null) {
            result = 31 * result + publicKey.hashCode();
        }
        if (keyInfoElement != null) {
            result = 31 * result + keyInfoElement.hashCode();
        }
        return result;
    }
}
