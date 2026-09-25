/**
 * 
 */
package gov.nist.toolkit.saml.security;

import java.security.KeyException;
import java.security.PublicKey;
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

/**
 * @author vbeera
 *
 */
public class PublicKeySelector extends KeySelector 
{

	public KeySelectorResult select(KeyInfo keyInfo, KeySelector.Purpose purpose, AlgorithmMethod method, XMLCryptoContext context) throws KeySelectorException 
	{
		if (keyInfo == null)
			throw new KeySelectorException("Null KeyInfo model!");

		SignatureMethod signMethod = (SignatureMethod) method;
		List list = keyInfo.getContent();

		for (int i = 0; i < list.size(); i++) 
		{
			XMLStructure xmlStructure = (XMLStructure) list.get(i);
			if (xmlStructure instanceof KeyValue) 
			{
				PublicKey pk = null;
				try {
					pk = ((KeyValue)xmlStructure).getPublicKey();
				} catch (KeyException ke) {
					throw new KeySelectorException(ke);
				}

				// Check if algorithm is compatible with Signature method
				if (algEquals(signMethod.getAlgorithm(), pk.getAlgorithm())) 
					return new KeySelectorResultImpl(pk);
			}
		}
		throw new KeySelectorException("No KeyValue element found!");
	}

	static boolean algEquals(String algURI, String algName)
	{
		if (algName.equalsIgnoreCase("DSA"))
		{
			return algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)
				|| algURI.equalsIgnoreCase("http://www.w3.org/2009/xmldsig11#dsa-sha256");
		}
		else if (algName.equalsIgnoreCase("RSA"))
		{
			return algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)
				|| algURI.equalsIgnoreCase("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256")
				|| algURI.equalsIgnoreCase("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384")
				|| algURI.equalsIgnoreCase("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512");
		}
		return false;
	}



}
