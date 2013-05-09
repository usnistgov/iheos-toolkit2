package gov.nist.toolkit.soap.wsseToolkitAdapter;

import gov.nist.toolkit.wsseTool.api.config.KeystoreAccess;
import gov.nist.toolkit.wsseTool.api.config.SecurityContext;
import gov.nist.toolkit.wsseTool.api.config.SecurityContextFactory;
import gov.nist.toolkit.wsseTool.api.exceptions.GenerationException;
import gov.nist.toolkit.wsseTool.generation.opensaml.OpenSamlWsseSecurityGenerator;

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




public class WsseHeaderGeneratorAdapter {
	
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, GeneralSecurityException, MarshalException, XMLSignatureException, URISyntaxException, GenerationException {
		String store = "/Users/gerardin/IHE-Testing/xdstools2_environment/environment/AEGIS_env/keystore/keystore";
		String sPass = "changeit";
		String kPass = "changeit";
		String alias = "hit-testing.nist.gov";
		KeystoreAccess keystore = new KeystoreAccess(store , sPass, alias, kPass);
		SecurityContext context = SecurityContextFactory.getInstance();
		context.setKeystore(keystore);
		context.getParams().put("patientId", "D123401^^^&1.1&ISO");
		context.getParams().put("homeCommunityId", "urn:oid:2.2");
		buildHeader(context);
	}
	
	
	public static Element buildHeader(SecurityContext context) throws GenerationException, KeyStoreException {
		
		Document wsseHeader = new OpenSamlWsseSecurityGenerator().generateWsseHeader(context);
		return wsseHeader.getDocumentElement();
	}

}
