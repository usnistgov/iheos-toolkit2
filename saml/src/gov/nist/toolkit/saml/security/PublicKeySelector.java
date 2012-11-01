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
			throw new KeySelectorException("Null KeyInfo object!");

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
		System.out.println("algURI =["+algURI+"],    algName = ["+algName+"]");
		if (algName.equalsIgnoreCase("DSA") && algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) 
		{
			System.out.println("return true for DSA_SHA1");
			return true;
		} 
		else if (algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)) 
		{
			System.out.println("return true for RSA_SHA1");
			return true;
		} 
		else 
		{
			System.out.println("return false..");
			return false;
		}
	}



}
