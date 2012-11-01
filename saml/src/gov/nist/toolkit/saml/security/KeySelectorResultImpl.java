/**
 * 
 */
package gov.nist.toolkit.saml.security;

/**
 * @author vbeera
 *
 */
import java.security.Key;
import java.security.PublicKey;

import javax.xml.crypto.KeySelectorResult;

public class KeySelectorResultImpl implements KeySelectorResult {
    private PublicKey pk;

    KeySelectorResultImpl(PublicKey pk) {
        this.pk = pk;
    }

    public Key getKey() {
    	//System.out.println("getKey == ["+pk+"]");
        return pk;
    }
}
