package gov.nist.toolkit.soap.wsseToolkitAdapter;

import gov.nist.toolkit.wsseToolkit.generation.GenerationException;
import gov.nist.toolkit.wsseToolkit.generation.opensaml.OpenSamlWsseSecurityGenerator;
import gov.nist.toolkit.wsseToolkit.keystore.KeystoreAccess;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;




public class WsseToolkitAdapter {
	
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, GeneralSecurityException, MarshalException, XMLSignatureException, URISyntaxException, GenerationException {
		String store = "/Users/gerardin/IHE-Testing/xdstools2_environment/environment/AEGIS_env/keystore/keystore";
		String sPass = "changeit";
		String kPass = "changeit";
		String alias = "hit-testing.nist.gov";
		buildHeader(store, sPass, alias, kPass);
	}
	
	
	public static Element buildHeader(String store, String sPass, String alias, String kPass) throws GenerationException, KeyStoreException {
		KeystoreAccess keystore = new KeystoreAccess(store , sPass, alias, kPass);
		Document wsseHeader = new OpenSamlWsseSecurityGenerator().generateWsseHeader(keystore);
		return wsseHeader.getDocumentElement();
	}

}
