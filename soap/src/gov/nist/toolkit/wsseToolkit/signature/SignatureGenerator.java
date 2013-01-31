package gov.nist.toolkit.wsseToolkit.signature;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

public class SignatureGenerator {

	// One factory is used for all signing jobs.
	// DOM is the only concrete factory shipped with the RI.
	static XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
			new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
	
	/**
	 * Create a signature
	 * @param eltToSignId : id of the element to sign
	 * @param publicKey : public signing key (for reference in keyinfo)
	 * @return a XMLSignature to sign the elt
	 * @throws GeneralSecurityException
	 */
	public static XMLSignature generateSignature(String eltToSignId, PublicKey publicKey)
			throws GeneralSecurityException {
		try {
			// 1 - Reference

			// The referenceURI must be the saml assertion id
			// (MA 1043) @URI = <value of saml:Assertion/@ID>
			String referenceURI = eltToSignId;

			List<Transform> transformList = new ArrayList<Transform>();
			transformList.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
			transformList.add(fac.newTransform(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null));

			Reference ref = fac.newReference(referenceURI, fac.newDigestMethod(DigestMethod.SHA1, null), transformList,
					null, null);

			// 2- SignedInfo
			SignedInfo signInfo = fac.newSignedInfo(
					fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null),
					fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));

			// 3 - KeyInfo
			KeyInfoFactory keyInfoFac = fac.getKeyInfoFactory();
			KeyValue keyVal = keyInfoFac.newKeyValue(publicKey);
			KeyInfo keyInf = keyInfoFac.newKeyInfo(Collections.singletonList(keyVal));
			XMLSignature signature = fac.newXMLSignature(signInfo, keyInf);

			return signature;
		} catch (Exception e) {
			throw new GeneralSecurityException("cannot sign the document", e);
		}
	}
	
	
	
	public static KeyInfo generateKeyInfoForSubjectConfirmationData(Certificate certificate, PublicKey publicKey) throws GeneralSecurityException{
		KeyInfoFactory keyInfoFac = fac.getKeyInfoFactory();
		KeyValue keyVal = keyInfoFac.newKeyValue(publicKey);
		KeyInfo keyInfo = keyInfoFac.newKeyInfo(Collections.singletonList(keyVal));
		return keyInfo;
	}
	
	


}
