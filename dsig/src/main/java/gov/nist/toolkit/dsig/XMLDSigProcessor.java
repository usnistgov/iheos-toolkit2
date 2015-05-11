package gov.nist.toolkit.dsig;


import gov.nist.toolkit.utilities.io.Io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.utils.IdResolver;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class XMLDSigProcessor {

	/**
	 * This is a simple example of generating an Enveloped XML 
	 * Signature using the JSR 105 API. The resulting signature will look 
	 * like (key and signature values will be different):
	 *
	 * <pre><code>
	 *<Envelope xmlns="urn:envelope">
	 * <Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
	 *   <SignedInfo>
	 *     <CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n
	-20010315"/>
	 *     <SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#dsa-sha1"/>
	 *     <Reference URI="">
	 *       <Transforms>
	 *         <Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
	 *       </Transforms>
	 *       <DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
	 *       <DigestValue>K8M/lPbKnuMDsO0Uzuj75lQtzQI=<DigestValue>
	 *     </Reference>
	 *   </SignedInfo>
	 *   <SignatureValue>
	 *     DpEylhQoiUKBoKWmYfajXO7LZxiDYgVtUtCNyTgwZgoChzorA2nhkQ==
	 *   </SignatureValue>
	 *   <KeyInfo>
	 *     <KeyValue>
	 *       <DSAKeyValue>
	 *         <P>
	 *           rFto8uPQM6y34FLPmDh40BLJ1rVrC8VeRquuhPZ6jYNFkQuwxnu/wCvIAMhukPBL
	 *           FET8bJf/b2ef+oqxZajEb+88zlZoyG8g/wMfDBHTxz+CnowLahnCCTYBp5kt7G8q
	 *           UobJuvjylwj1st7V9Lsu03iXMXtbiriUjFa5gURasN8=
	 *         </P>
	 *         <Q>
	 *           kEjAFpCe4lcUOdwphpzf+tBaUds=
	 *         </Q>
	 *         <G>
	 *           oe14R2OtyKx+s+60O5BRNMOYpIg2TU/f15N3bsDErKOWtKXeNK9FS7dWStreDxo2
	 *           SSgOonqAd4FuJ/4uva7GgNL4ULIqY7E+mW5iwJ7n/WTELh98mEocsLXkNh24HcH4
	 *           BZfSCTruuzmCyjdV1KSqX/Eux04HfCWYmdxN3SQ/qqw=
	 *         </G>
	 *         <Y>
	 *           pA5NnZvcd574WRXuOA7ZfC/7Lqt4cB0MRLWtHubtJoVOao9ib5ry4rTk0r6ddnOv
	 *           AIGKktutzK3ymvKleS3DOrwZQgJ+/BDWDW8kO9R66o6rdjiSobBi/0c2V1+dkqOg
	 *           jFmKz395mvCOZGhC7fqAVhHat2EjGPMfgSZyABa7+1k=
	 *         </Y>
	 *       </DSAKeyValue>
	 *     </KeyValue>
	 *   </KeyInfo>
	 * </Signature>
	 *</Envelope>
	 * </code></pre>
	 */

	private static Log logger = LogFactory.getLog(XMLDSigProcessor.class);

	/**
	 * Signs the xml document where "document" is the name of a file containing the XML document
	 * to be signed
	 */
	public byte[] signSAMLAssertionsEnveloped(byte[] xmldocument) throws Exception {

		// Create a DOM XMLSignatureFactory that will be used to generate the 
		// enveloped signature
		String providerName = System.getProperty
		("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
				(Provider) Class.forName(providerName).newInstance());


		// Instantiate the document to be signed
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = 
			dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xmldocument));

		// Create a Reference to the enveloped document and
		// also specify the SHA1 digest algorithm and the ENVELOPED Transform.
		//TODO the reference URI?
		String referenceURI = "";
		
		NodeList nl = doc.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "Assertion");
		if (nl.getLength() == 0)
			throw new Exception("No Assertion found - nothing to sign.\nHeader is \n" + new String(xmldocument));
		Node aNode = nl.item(0);
		Element rootElement = (Element)nl.item(0);
		NamedNodeMap attributes = aNode.getAttributes();

		for (int a = 0; a < attributes.getLength(); a++) {
			Node theAttribute = attributes.item(a);
			if (theAttribute!=null && theAttribute.getNodeName().equalsIgnoreCase("ID")){
				referenceURI = "#" + theAttribute.getNodeValue();
				//System.out.println("Reference URI = " + referenceURI);
			}
		}
		
		ArrayList<Transform> sl = new ArrayList<Transform>();
		sl.add(fac.newTransform
				(Transform.ENVELOPED, (TransformParameterSpec) null));
		sl.add(fac.newTransform
				(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null));
		
		Reference ref = fac.newReference
		(referenceURI, fac.newDigestMethod(DigestMethod.SHA1, null),
				sl,	null, null);


		// Create the SignedInfo
		SignedInfo si = fac.newSignedInfo
		(fac.newCanonicalizationMethod
				(CanonicalizationMethod.EXCLUSIVE, 
						(C14NMethodParameterSpec) null), 
						fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
						Collections.singletonList(ref));

		// Create a RSA KeyPair
		// Get a KeyPair
		KeyStoreAccessObject ksao = KeyStoreAccessObject.getInstance(null);
		PublicKey pubK = ksao.getPublicKey();
		PrivateKey pvtK = ksao.getPrivateKey(); 

		// Create a KeyValue containing the RSA PublicKey that was generated
		KeyInfoFactory kif = fac.getKeyInfoFactory();
		KeyValue kv = kif.newKeyValue(pubK);

		// Create a KeyInfo and add the KeyValue to it
		KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));


		// Create a DOMSignContext and specify the RSA PrivateKey and
		// location of the resulting XMLSignature's parent element
		DOMSignContext dsc = new DOMSignContext
		(pvtK, rootElement);

		// Create the XMLSignature (but don't sign it yet)
		XMLSignature signature = fac.newXMLSignature(si, ki);

		// Marshal, generate (and sign) the enveloped signature
		signature.sign(dsc);

		// output the resulting document
		//OutputStream os =System.out;
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		//trans.transform(new DOMSource(doc), new StreamResult(os));
		trans.transform(new DOMSource(doc), new StreamResult(out));
		return out.toByteArray();

	}

	/**
	 * Signs the xml document where "document" is the name of a file containing the XML document
	 * to be signed
	 */
	public byte[] signTimestampDetached(byte[] xmldocument) throws Exception {

		// Create a DOM XMLSignatureFactory that will be used to generate the 
		// detached signature
		String providerName = System.getProperty
		("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
				(Provider) Class.forName(providerName).newInstance());

		// Instantiate the document to be signed
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc1 = 
			dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xmldocument));

		// Create a Reference to the enveloped document and
		// also specify the SHA1 digest algorithm and the ENVELOPED Transform.
		String referenceURI = "";
		
		NodeList nl0 = doc1.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
		Element rootElement = (Element)nl0.item(0);
		
		NodeList nl = doc1.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Timestamp");
		Element tsElem = (Element)nl.item(0);
		if (tsElem!=null) {
			referenceURI = "#" + tsElem.getAttributeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
			System.out.println("Reference URI = " + referenceURI);
			Attr id = tsElem.getAttributeNodeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
			IdResolver.registerElementById(tsElem, id);
		}
		  
		Reference ref = fac.newReference
		(referenceURI, fac.newDigestMethod(DigestMethod.SHA1, null),
				Collections.singletonList(fac.newTransform
						(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null)),null,null);


		// Create the SignedInfo
		SignedInfo si = fac.newSignedInfo
		(fac.newCanonicalizationMethod
				(CanonicalizationMethod.EXCLUSIVE, 
						(C14NMethodParameterSpec) null), 
						fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
						Collections.singletonList(ref));

		// Get a KeyPair
		KeyStoreAccessObject ksao = KeyStoreAccessObject.getInstance(null);
		PublicKey pubK = ksao.getPublicKey();
		PrivateKey pvtK = ksao.getPrivateKey(); 

		// Create a KeyValue containing the RSA PublicKey that was generated
		KeyInfoFactory kif = fac.getKeyInfoFactory();
		KeyValue kv = kif.newKeyValue(pubK);

		// Create a KeyInfo and add the KeyValue to it
		KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

		// Create a DOMSignContext and specify the RSA PrivateKey and
		// location of the resulting XMLSignature's parent element
		DOMSignContext dsc = new DOMSignContext
		(pvtK, rootElement);

		// Create the XMLSignature (but don't sign it yet)
		XMLSignature signature = fac.newXMLSignature(si, ki);

		// Marshal, generate (and sign) the detached signature
		signature.sign(dsc);

		// output the resulting document
		OutputStream os =System.out;
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		trans.transform(new DOMSource(doc1), new StreamResult(os));
		trans.transform(new DOMSource(doc1), new StreamResult(out));
		return out.toByteArray();

	}

	//
	// Synopsis: java Validate [document]
	//
	//	  where "document" is the name of a file containing the XML document
	//	  to be validated.
	//
	public boolean validate(byte[] xmldocument) throws Exception {

		// Instantiate the document to be validated
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc =
			dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xmldocument));

		// Find Signature element
		NodeList nl = 
			doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
		if (nl.getLength() == 0) {
			throw new Exception("Cannot find Signature element");
		}

		// Create a DOM XMLSignatureFactory that will be used to unmarshal the 
		// document containing the XMLSignature 
		String providerName = System.getProperty
		("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
				(Provider) Class.forName(providerName).newInstance());

		// Create a DOMValidateContext and specify a KeyValue KeySelector
		// and document context
		DOMValidateContext valContext = new DOMValidateContext
		(new KeyValueKeySelector(), nl.item(0));

		// unmarshal the XMLSignature
		XMLSignature signature = fac.unmarshalXMLSignature(valContext);
		
		NodeList nl2 = doc.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Timestamp");
		Element tsElem = (Element)nl2.item(0);
		if (tsElem!=null) {
			Attr id = tsElem.getAttributeNodeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
			IdResolver.registerElementById(tsElem, id);
		}
		
		// Validate the XMLSignature (generated above)
		boolean coreValidity = signature.validate(valContext); 

		// Check core validation status
		if (coreValidity == false) {
			System.err.println("Signature failed core validation"); 
			boolean sv = signature.getSignatureValue().validate(valContext);
			System.out.println("signature validation status: " + sv);
			// check the validation status of each Reference
			Iterator i = signature.getSignedInfo().getReferences().iterator();
			for (int j=0; i.hasNext(); j++) {
				boolean refValid = 
					((Reference) i.next()).validate(valContext);
				System.out.println("ref["+j+"] validity status: " + refValid);
			}
			return false;
		} else {
			
			System.out.println("Signature passed core validation");
			return true;
		}
	}
	
	public static void main(String[] args) {
		String assertion_file = "/Users/bill/Downloads/nhin/assertion.xml";
		
		byte[] assertion_bytes = null;
		try {
			assertion_bytes = Io.getBytesFromInputStream(new FileInputStream(assertion_file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
		XMLDSigProcessor xsp = new XMLDSigProcessor();
		byte[] signedAssertion = null;
		try {
			signedAssertion = xsp.signSAMLAssertionsEnveloped(assertion_bytes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			boolean status = xsp.validate(signedAssertion);
			if (status)
				System.out.println("Congratulations");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
    

	
}
