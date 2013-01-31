package gov.nist.toolkit.soap.wsseToolkitAdapter;

import gov.nist.toolkit.wsseToolkit.api.KeystoreAccess;
import gov.nist.toolkit.wsseToolkit.api.WsseToolkit;
import gov.nist.toolkit.wsseToolkit.util.MyXmlUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;




public class WsseToolkitAdapter {
	
	static String store = "/Users/gerardin/IHE-Testing/xdstools2_environment/environment/AEGIS_env/keystore/keystore";
	static String sPass = "changeit";
	static String kPass = "changeit";
	static String alias = "hit-testing.nist.gov";
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, GeneralSecurityException, MarshalException, XMLSignatureException, URISyntaxException {
		buildHeader();
	}
	
	public static Element buildHeader() throws SAXException, IOException, ParserConfigurationException, GeneralSecurityException, MarshalException, XMLSignatureException, URISyntaxException{
		WsseToolkit wsse  = new WsseToolkit();
		KeystoreAccess keystore = new KeystoreAccess(store , sPass, alias, kPass);
		Document header = wsse.generateWsseHeader(keystore);
		
		Element signature = (Element) header.getElementsByTagName("ds:Signature").item(1); 
		
		
		boolean valid = wsse.verifyTimeStamp(header, signature);
		System.out.println(valid);
		
		System.out.println("********final header*************");
		MyXmlUtils.DomToStream(header, System.out);
		System.out.println("********final header*************");
		
		
		WsseToolkit toolkit = new WsseToolkit();
		toolkit.ouioui();
		
		
		return header.getDocumentElement();
	}

}
