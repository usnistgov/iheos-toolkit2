package gov.nist.toolkit.wsseToolkit.signature;

import gov.nist.toolkit.wsseToolkit.generation.NamespaceDeclarations;

import java.security.Key;

import javax.xml.crypto.KeySelector;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;

import org.w3c.dom.Element;

/**
 * Template for XML signature verification.
 * Subclasses must provide the key selection mechanism
 * 
 * @author gerardin
 *
 */

public abstract class SignatureVerifier {
	
	// One factory is used for all signing jobs.
	// DOM is the only concrete factory shipped with the RI.
	static XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
			new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
	
	public boolean verify(Element element) throws MarshalException, XMLSignatureException{
		
		DOMValidateContext valContext = new DOMValidateContext(KeySelector.singletonKeySelector(getKey()), element);
		valContext.putNamespacePrefix(javax.xml.crypto.dsig.XMLSignature.XMLNS, NamespaceDeclarations.DS_PREFIX);
		XMLSignature signature = fac.unmarshalXMLSignature(valContext);
		
		boolean isValid = signature.validate(valContext);
		
		return isValid;
	}
	
	/*
	 * Visible only to subclasses
	 */
	protected abstract Key getKey();

}
