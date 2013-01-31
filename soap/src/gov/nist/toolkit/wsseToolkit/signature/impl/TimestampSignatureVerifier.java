package gov.nist.toolkit.wsseToolkit.signature.impl;

import gov.nist.toolkit.wsseToolkit.signature.SignatureVerifier;
import gov.nist.toolkit.wsseToolkit.util.MyXmlUtils;

import java.security.Key;
import java.security.KeyException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;

import org.w3c.dom.Element;


/**
 * For SAML assertion, the only verification method accepted is public-key based
 * 
 * @author gerardin
 *
 */
public class TimestampSignatureVerifier extends SignatureVerifier {

	private Key key;
	
	public TimestampSignatureVerifier(Element xml) throws MarshalException, KeyException{
		
		MyXmlUtils.DomToStream(xml, System.out);
		
		Element signature = (Element) xml.getElementsByTagName("ds:Signature").item(1);
		
		
		System.out.println("-------------------------");
		MyXmlUtils.DomToStream(signature, System.out);
		System.out.println("-------------------------");
		
		Element keyIdentifier = (Element)signature.getElementsByTagName("wsse:KeyIdentifier").item(0);
		String value = keyIdentifier.getTextContent();
		
		System.out.println(value);
		
		Element samlAssertion = (Element) xml.getElementsByTagName("saml2:Assertion").item(0);
		
		String id = samlAssertion.getAttribute("ID");
		System.out.println(id);
		
		Element scd = (Element)xml.getElementsByTagName("saml2:SubjectConfirmationData").item(0);
		
		System.out.println("-------------------------");
		Element ki = (Element)scd.getElementsByTagName("ds:KeyInfo").item(0);
		MyXmlUtils.DomToStream(scd , System.out);
		System.out.println("-------------------------");
		
		DOMStructure keyInfoElt = new DOMStructure(ki);
		
		KeyInfo keyInfo = KeyInfoFactory.getInstance().unmarshalKeyInfo(keyInfoElt);
		
		KeyValue keyValue = (KeyValue)keyInfo.getContent().get(0);
		key = keyValue.getPublicKey();
		
		System.out.println(key.getEncoded());
		
	}

	@Override
	protected Key getKey() {
		return key;
	}
	
	

}
