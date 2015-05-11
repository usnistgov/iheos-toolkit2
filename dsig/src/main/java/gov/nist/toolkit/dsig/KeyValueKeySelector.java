package gov.nist.toolkit.dsig;

import java.security.Key;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;

/**
 * KeySelector which retrieves the public key out of the
 * KeyValue element and returns it.
 * NOTE: If the key algorithm doesn't match signature algorithm,
 * then the public key will be ignored.
 */
public class KeyValueKeySelector extends KeySelector {

	@Override
	public KeySelectorResult select(KeyInfo keyInfo,
			KeySelector.Purpose purpose,
			AlgorithmMethod method,
			XMLCryptoContext context)
	throws KeySelectorException {
		if (keyInfo == null) {
			throw new KeySelectorException("Null KeyInfo object!");
		}
		SignatureMethod sm = (SignatureMethod) method;
		List list = keyInfo.getContent();

		for (int i = 0; i < list.size(); i++) {
			XMLStructure xmlStructure = (XMLStructure) list.get(i);
			if (xmlStructure instanceof KeyValue) {
				PublicKey pk = null;
				try {
					pk = ((KeyValue)xmlStructure).getPublicKey();
				} catch (KeyException ke) {
					throw new KeySelectorException(ke);
				}
				// make sure algorithm is compatible with method
				if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
					return new SimpleKeySelectorResult(pk);
				}
			} else if ((xmlStructure instanceof X509Data)){
	            X509Data x509Data = (X509Data) xmlStructure;
	            Iterator xi = x509Data.getContent().iterator();
	            while (xi.hasNext()) {
	                Object o = xi.next();
	                if (!(o instanceof X509Certificate))
	                    continue;
	                final PublicKey pk = ((X509Certificate)o).getPublicKey();
	                // Make sure the algorithm is compatible
	                // with the method.
	                if (algEquals(method.getAlgorithm(), pk.getAlgorithm())) {
	                	return new SimpleKeySelectorResult(pk);
	                }
	            }
			}
		}
		throw new KeySelectorException("No KeyValue element found!");	}
	
    private static class SimpleKeySelectorResult implements KeySelectorResult {
        private Key pk;
        SimpleKeySelectorResult(Key pk) {
            this.pk = pk;
        }
        public Key getKey() { return pk; }
    }
    


	private boolean algEquals(String algURI, String algName) {
		if (algName.equalsIgnoreCase("DSA") &&
				algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) {
			return true;
		} else if (algName.equalsIgnoreCase("RSA") &&
				algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)) {
			return true;
		} else {
			return false;
		}
	}	

}
