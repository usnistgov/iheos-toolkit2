/**
 * 
 */
package gov.nist.toolkit.saml.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import java.util.Iterator;

/**
 * @author vbeera
 *
 */
public class XMLSignatureValidatorUtil 
{
	private static Log log = LogFactory.getLog(XMLSignatureValidatorUtil.class);
	
	/**
	 * This method is used to verify the Signature of the XML message - Document object
	 * @param doc
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean verifySignatureForXMLDocument(Document doc) throws Exception
	{
		NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
		if (nl.getLength() == 0) {
			throw new Exception("Cannot find Signature element");
		}
		else
			System.out.println("Signature element Found* ");
		return verifySignature(nl);
	}
	
	public static boolean verifySignatureForXMLDocument(Element element) {
		boolean validity = true;
		// System.out.println("*** verifySignatureForXMLDocument ***");
		NodeList nl = element.getElementsByTagName("ds:Signature");
		if (nl != null) {
			// validity = verifySignature(nl);
			System.out.println("Node List Length: " +nl.getLength());
			try {
				DOMValidateContext valContext = new DOMValidateContext(new PublicKeySelector(), nl.item(0));
				if (! verifySignature(nl)) 
					validity = false;
			} catch (Exception e) {
				System.out.println("DOMValidateContext Error: " + e.getMessage());
			}
		} else
			validity = false;
		
		return validity;
	}
	
	private static boolean verifySignature(NodeList nl) {
		
		DOMValidateContext valContext = new DOMValidateContext(new PublicKeySelector(), nl.item(0));
		XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI()); 
		XMLSignature signature;
		boolean coreValidity = true;
		try {
			signature = factory.unmarshalXMLSignature(valContext);
			
			coreValidity = signature.validate(valContext);

			
			if(coreValidity == true)
				return coreValidity;
			
			//Below code is for further debugging if validation is failed
			boolean signVal = signature.getSignatureValue().validate(valContext);
			
			Iterator<Reference> i = signature.getSignedInfo().getReferences().iterator();
			for (int j=0; i.hasNext(); j++) 
			{
				boolean refValid = ((Reference) i.next()).validate(valContext);
				
			}
		} catch (MarshalException e) {
			System.out.println(e.getMessage());
			// e.printStackTrace();
		} catch (XMLSignatureException e) {
			System.out.println(e.getMessage());
			// e.printStackTrace();
		} 
		
		return coreValidity;
	}
	
	
	

}
