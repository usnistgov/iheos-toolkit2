package gov.nist.toolkit.wsseToolkit.api;

import gov.nist.toolkit.wsseToolkit.generation.WsseHeaderGenerator;
import gov.nist.toolkit.wsseToolkit.signature.SignatureVerifier;
import gov.nist.toolkit.wsseToolkit.signature.impl.TimestampSignatureVerifier;
import gov.nist.toolkit.wsseToolkit.util.MyXmlUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * This is the api to the WsseToolkit module.
 * 
 * @author gerardin
 *
 */
public class WsseToolkit {
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, GeneralSecurityException, MarshalException, XMLSignatureException, URISyntaxException {
		String store = "src/test/resources/keystore/keystore";
		String sPass = "changeit";
		String kPass = "changeit";
		String alias = "hit-testing.nist.gov";
		new WsseToolkit().generateWsseHeader(new KeystoreAccess(store,sPass,alias,kPass));
	}
	
	public WsseToolkit(){}
	
	/**
	 * 
	 * @return a generated standard-compliant wsseHeader
	 */
	public Document generateWsseHeader(KeystoreAccess keystore) throws SAXException, IOException, ParserConfigurationException, GeneralSecurityException, MarshalException, XMLSignatureException, URISyntaxException {
		
		Document doc = new WsseHeaderGenerator().generateWsseHeader(keystore);
		
		return doc;
	}
	
	public boolean verifyTimeStamp(Document doc, Element signature) throws KeyException, MarshalException, XMLSignatureException{
		SignatureVerifier verifier = new TimestampSignatureVerifier(doc.getDocumentElement());
		return verifier.verify(signature);
	}
	
	public void ouioui(){};
	
	public boolean verifySamlAssertion(Element assertion){
		throw new RuntimeException("not implemented");
	}
}
