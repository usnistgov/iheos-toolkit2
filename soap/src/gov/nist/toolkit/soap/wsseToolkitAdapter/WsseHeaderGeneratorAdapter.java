package gov.nist.toolkit.soap.wsseToolkitAdapter;


import gov.nist.hit.ds.wsseTool.api.config.ContextFactory;
import gov.nist.hit.ds.wsseTool.api.config.GenContext;
import gov.nist.hit.ds.wsseTool.api.config.KeystoreAccess;
import gov.nist.hit.ds.wsseTool.api.exceptions.GenerationException;
import gov.nist.hit.ds.wsseTool.generation.opensaml.OpenSamlWsseSecurityGenerator;

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
		String store = System.getProperty("user.dir") + "/soap/test/resources/keystore/keystore";
		String sPass = "changeit";
		String kPass = "changeit";
		String alias = "1";
		KeystoreAccess keystore = new KeystoreAccess(store , sPass, alias, kPass);
		GenContext context = ContextFactory.getInstance();
		context.setKeystore(keystore);
		context.getParams().put("patientId", "D123401^^^&1.1&ISO");
		context.getParams().put("homeCommunityId", "urn:oid:2.2");
		buildHeader(context);
	}
	
	
	public static Element buildHeader(GenContext context) throws GenerationException, KeyStoreException {
		
		Document wsseHeader = new OpenSamlWsseSecurityGenerator().generateWsseHeader(context);
		return wsseHeader.getDocumentElement();
	}

}
