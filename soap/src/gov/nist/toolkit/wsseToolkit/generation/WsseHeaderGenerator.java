package gov.nist.toolkit.wsseToolkit.generation;

import gov.nist.toolkit.wsseToolkit.api.KeystoreAccess;
import gov.nist.toolkit.wsseToolkit.signature.SignatureGenerator;
import gov.nist.toolkit.wsseToolkit.util.DateUtil;
import gov.nist.toolkit.wsseToolkit.util.MyXmlUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Date;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class WsseHeaderGenerator {
	
	public Document generateWsseHeader(KeystoreAccess access) throws SAXException, IOException, ParserConfigurationException, GeneralSecurityException, MarshalException, XMLSignatureException {
		
		//first document - saml assertion updated and signed
		Document assertion = MyXmlUtils.getDocumentWithResourcePath("/samlAssertionTemplate.xml");
	
		MyXmlUtils.DomToStream(assertion, System.out);
		
		generateSamlAssertionKeyInfo(assertion, access.certificate, access.publicKey);

		Element samlAssertion = (Element) assertion.getElementsByTagName("saml2:Assertion").item(0);
		
		MyXmlUtils.DomToStream(assertion, System.out);
		
		String samlId = samlAssertion.getAttribute("ID");
		String samlIdRef = "#" + samlId;
		
		System.out.println("before signing \n ====================");
		
		Node subject = assertion.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "Subject").item(0);
		System.out.println("*******start******");
		MyXmlUtils.DomToStream(subject, System.out);
		System.out.println("*******end********");
		sign(assertion, samlIdRef, access.keyPair, subject); 
		
		System.out.println("after signing \n ====================");
		
		//first doc ready
		MyXmlUtils.DomToStream(assertion.getDocumentElement(), System.out);
		
		//2nd document -get timestamp ready
		
		Document timestamp = MyXmlUtils.getDocumentWithResourcePath("/timestamp.xml");
		
		updateTimestamp(timestamp);
		
		//3rd document - assemble the document
		//the top document
		Document xml = MyXmlUtils.getDocumentWithResourcePath("/wsseHeaderTemplate-Chunk.xml");
		Node timestampNode = xml.importNode(timestamp.getDocumentElement(),true);
		xml.getDocumentElement().appendChild(timestampNode);
		Node assertionNode = xml.importNode(assertion.getDocumentElement(),true);
		xml.getDocumentElement().appendChild(assertionNode);
		
		MyXmlUtils.DomToStream(xml, System.out);
		
		// 4 sign the timestamp
		Node placeholder = assertion.getLastChild();
		MyXmlUtils.DomToStream(placeholder, System.out);
		
		String timestampRef = "#" + timestamp.getDocumentElement().getAttribute("Id");
		sign(xml, timestampRef, access.keyPair, null); 
		
		MyXmlUtils.DomToStream(xml, System.out);
		
		//5 document
		Document timestampSignatureKeyinfo = MyXmlUtils.getDocumentWithResourcePath("/keyInfo_TimestampSignature.xml");
		updateTimestampSignatureKeyInfoWithSamlId(timestampSignatureKeyinfo, samlId);
		
		//6 replace the keyInfo in doc
		//we could get the signature directly
		Node signature = xml.getDocumentElement().getLastChild();
		Node oldKeyInfo = signature.getLastChild();
		Node newKeyInfo = xml.importNode(timestampSignatureKeyinfo.getDocumentElement(),true);
		
		MyXmlUtils.DomToStream(oldKeyInfo, System.out);
		
		signature.replaceChild(newKeyInfo, oldKeyInfo);
		
		//the END!!!
		
		return xml;
	}
	
	
	private Document updateTimestamp(Document xml) {
		
		Node created = xml.getElementsByTagName("wsu:Created").item(0);
		Node expires = xml.getElementsByTagName("wsu:Expires").item(0);
		
		created.setTextContent(DateUtil.toUTCDateFormat(new Date()));
		
		String expirationDate = DateUtil.toUTCDateFormat( DateUtil.getDateAfterDays(365) );
		expires.setTextContent(expirationDate);
		
		MyXmlUtils.DomToStream(xml, System.out);
		
		
		return xml;
	}


	private Document sign(Document xml, String id, KeyPair keyPair, Node nextSibling) throws GeneralSecurityException, MarshalException, XMLSignatureException {
		
		XMLSignature signature = SignatureGenerator.generateSignature(id, keyPair.getPublic());
		
		DOMSignContext dsc =  null;
		if(nextSibling != null){
			dsc = new DOMSignContext(keyPair.getPrivate(), xml.getDocumentElement(), nextSibling);
		}
		else{
			dsc = new DOMSignContext(keyPair.getPrivate(), xml.getDocumentElement());
		}
		
		dsc.putNamespacePrefix(javax.xml.crypto.dsig.XMLSignature.XMLNS, NamespaceDeclarations.DS_PREFIX);
		signature.sign(dsc);
		
		return xml;
	}


	private Document updateTimestampSignatureKeyInfoWithSamlId(Document xml, String id) throws SAXException, IOException, ParserConfigurationException{
		
		
		Node identifier = xml.getElementsByTagName("wsse:KeyIdentifier").item(0);
		
		System.out.println(identifier.getTextContent());
		
		identifier.setTextContent(id);
		
		System.out.println(identifier.getTextContent());
		
		MyXmlUtils.DomToStream(xml, System.out);
		
		return xml; 
	}

	private Document generateSamlAssertionKeyInfo(Document xml, Certificate certificate, PublicKey publicKey) throws GeneralSecurityException, MarshalException{
		
		KeyInfo keyinfo = SignatureGenerator.generateKeyInfoForSubjectConfirmationData(certificate, publicKey);
		
		Node subjectConfirmationData = xml.getElementsByTagName("saml2:SubjectConfirmationData").item(0);
		
		subjectConfirmationData.setTextContent(null);
		
		//need to switch representation to be compatible with java.security interfaces
		DOMStructure d = new DOMStructure(subjectConfirmationData);
		
		DOMCryptoContext context = new DOMSignContext(publicKey, subjectConfirmationData);
		context.putNamespacePrefix(javax.xml.crypto.dsig.XMLSignature.XMLNS, NamespaceDeclarations.DS_PREFIX);
		keyinfo.marshal(d, context);
		
		
		return xml;
	}
}
