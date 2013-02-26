/**
 This software was developed at the National Institute of Standards and Technology by employees
of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
United States Code this software is not subject to copyright protection and is in the public domain.
This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
modified freely provided that any derivative works bear some notice that they are derived from it, and any
modified versions bear some notice that they have been modified.

Project: NWHIN-DIRECT
Authors: William Majurski
		 Frederic de Vaulx
		 Diane Azais
		 Julien Perugini
		 Antoine Gerardin
		
 */

package gov.nist.direct.directValidator.interfaces;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.SignerInformation;

import gov.nist.toolkit.errorrecording.ErrorRecorder;

public interface SignatureValidator  {
	
	
	// ************************************************
	// ************* Signature checks *****************
	// ************************************************
	
	
	/**
	 *  DTS-165, DigestAlgorithm	Direct Message, Required ???
	 * @param er
	 * @param digestalgo
	 * @param micalg
	 */
	public void validateDigestAlgorithmDirectMessage(ErrorRecorder er, String digestalgo, String micalg);
	

	/**
	 *  DTS 166, SignedData.encapContentInfo, Required
	 * @param er
	 * @param SignedDataEncapContentInfo
	 */
	public void validateSignedDataEncapContentInfo(ErrorRecorder er, String SignedDataEncapContentInfo);

	/**
	 *  DTS 222, tbsCertificate.signature.algorithm, Required
	 * @param er
	 * @param tbsCertSA
	 */
	public void validateTbsCertificateSA(ErrorRecorder er, String tbsCertSA);

	/**
	 *  DTS 225, tbsCertificate.subject, Required
	 * @param er
	 * @param tbsCertSubject
	 */
	public void validateTbsCertificateSubject(ErrorRecorder er, String tbsCertSubject);
	
	/**
	 *  DTS 240, Extensions.subjectAltName, Conditional
	 *  C-4 Extensions.subjectAltName format, Required
	 * @param er
	 * @param collection
	 */
	public void validateExtensionsSubjectAltName(ErrorRecorder er, Collection<List<?>> collection);
	
	/**
	 *  DTS 167, SignedData.certificates must contain at least one certificate
	 * @param er
	 * @param c
	 */
	public void validateSignedDataAtLeastOneCertificate(ErrorRecorder er, Collection c);
	

	
	// xxxxxxxxxxxxxxx Signature  xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	
	
	/**
	 *  DTS 152, Second MIME Part, Required
	 * @param er
	 */
	public void validateSecondMIMEPart(ErrorRecorder er, boolean secondMIMEPart);
		
	/**
	 *  DTS ?, All Non-MIME Message Headers
	 * @param er
	 * @param nonMIMEHeader
	 */
	public void validateAllNonMIMEMessageHeaders(ErrorRecorder er, String nonMIMEHeader);
		
	/**
	 *  DTS 155, Content-Type, Required
	 * @param er
	 * @param contentType
	 */
	public void validateContentType2(ErrorRecorder er, String contentType);
	
	/**
	 *  DTS 158, Second MIME Part Body, Required
	 */
	public void validateSecondMIMEPartBody(ErrorRecorder er, String secondMIMEPartBody);
	
	/**
	 *  DTS 163, ?, Required
	 * @param er
	 * @param dts163
	 */
	public void validateDTS163(ErrorRecorder er, String dts163);
	
	/**
	 *  DTS 164, Signed Data, Required
	 */
	public void validateSignedData(ErrorRecorder er, CMSProcessable cmsProcessable);
	
	/**
	 *  DTS 165, DigestAlgorithm, Required
	 */
	public void validateDigestAlgorithm(ErrorRecorder er, String digestAlgorithm);
	
	/**
	 *  DTS 166, EncapsuledContentInfo, Required
	 */
	//public void validateEncapsuledInfo(ErrorRecorder er, String encapsulatedInfo);
	
	/**
	 *  DTS 182, EncapsuledContentInfo.eContentInfo, Required
	 */
	public void validateEncapsuledInfo2(ErrorRecorder er, String encapsulatedInfo);
	
	/**
	 *  DTS 183, EncapsuledContentInfo.eContent, Optional
	 * @param er
	 * @param encapsulatedInfo
	 */
	public void validateEncapsuledInfo3(ErrorRecorder er, String encapsulatedInfo);
	
	/**
	 *  DTS 167, Certificates
	 */
	public void validateCertificates(ErrorRecorder er, String certificates);
	
	/**
	 *  DTS 168, Crls
	 */
	public void validateCrls(ErrorRecorder er, String crls);
	
	/**
	 *  DTS 169, SignerInfos, Optional
	 * @param signerInfos
	 */
	public void validateSignerInfos(ErrorRecorder er, String signerInfos);
	
	/**
	 *  DTS 173, SignerInfos.sid, Optional
	 */
	public void validateSignerInfosSid(ErrorRecorder er, String signerInfosSid);
	
	/**
	 *  DTS 174, SignerInfos.signerIdentifier, Required
	 */
	public void validateSignerIdentifier(ErrorRecorder er, String signerIdentifier);
	
	/**
	 *  DTS 175, SignerInfos.signerIdentifier.issuerAndSerialNumber, Conditional
	 */
	public void validateSignerIdentifierIssueAndSerialNumber(ErrorRecorder er, String signerInfos);
	
	/**
	 *  DTS 177, SignerInfos.digestAlgorithm, Required
	 */
	public void validateSignerInfosDigestAlgorithm(ErrorRecorder er, String signerInfosDigestAlgorithm);
	
	/**
	 *  DTS 176, SignerInfos.signerIdentifier.subjectKeyIdentifier, Condtional
	 */
	public void validateSignerIdentifierSubjectKeyIdentifier(ErrorRecorder er, String signerInfos);
	
	/**
	 *  DTS 178, SignerInfos.signedAttrs, Conditional
	 */
	public void validateSignedAttrs(ErrorRecorder er, String signerInfos);
	
	/**
	 *  DTS 179, SignerInfos.signedAttrs.messageDigest, Conditional
	 */
	public void validateSignedAttrsMessageDigest(ErrorRecorder er, String signerInfos);
	
	/**
	 *  DTS 180, SignerInfos.signedAttrs.contentType, Conditional
	 */
	public void validateSignedAttrsContentType(ErrorRecorder er, String signerInfos);
	
	/**
	 *  DTS 170, SignerInfos.SignatureAlgorithm, Required
	 */
	public void validateSignerInfosSignatureAlgorithm(ErrorRecorder er, String signerInfosSignatureAlgorithm);
	
	/**
	 *  DTS 171, SignerInfos.Signature, Required
	 */
	public void validateSignerInfosSignature(ErrorRecorder er, String signerInfosSignature);	
	
	/**
	 *  DTS 181, SignerInfos.unsignedAttrs, Optional
	 */
	public void validateSignerInfosUnsignedAttrs(ErrorRecorder er, String signerInfosUnsignedAttrs);
	
	/**
	 *  DTS 172, Boundary, Required
	 */
	public void validateBoundary(ErrorRecorder er, String boundary);
	
	
	/**
	 *  Signature validation (cert and date are valid)
	 *  C-1, Certificate has not expired, Required
	 * @param bC 
	 */
	public void validateSignature(ErrorRecorder er, X509Certificate cert,
			SignerInformation signer, String bC);

	
	/**
	 *  C2 - Key size <=2048
	 * @param er
	 * @param key
	 */
	public void validateKeySize(ErrorRecorder er, String key);
	
}
