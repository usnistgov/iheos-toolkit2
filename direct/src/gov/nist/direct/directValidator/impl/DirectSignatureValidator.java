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

package gov.nist.direct.directValidator.impl;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;

import gov.nist.direct.directValidator.interfaces.SignatureValidator;
import gov.nist.direct.utils.ValidationUtils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

public class DirectSignatureValidator implements SignatureValidator {

	// ************************************************
	// ************** Signature checks ****************
	// ************************************************
	

	// DTS 166, SignedData.encapContentInfo, Required
	public void validateSignedDataEncapContentInfo(ErrorRecorder er, String SignedDataEncapContentInfo) {
		String rfc = "RFC 5652: 5.1, 5.2;http://tools.ietf.org/html/rfc5652#section-5.1";
		if(!SignedDataEncapContentInfo.equals("")) {
			er.success("166", "SignedData.encapContentInfo", SignedDataEncapContentInfo.substring(0, 50) + "...", "SignedData.encapContentInfo (signed content + content type identifier) must be present" , rfc);
		} else {
			er.error("166", "SignedData.encapContentInfo", SignedDataEncapContentInfo.substring(0, 50) + "...", "SignedData.encapContentInfo (signed content + content type identifier) must be present" , rfc);
		}
		
	}

	// DTS 222, tbsCertificate.signature.algorithm, Required
	public void validateTbsCertificateSA(ErrorRecorder er, String tbsCertSA) {
		String rfc = "RFC 5280: 4.1.2.3;http://tools.ietf.org/html/rfc5280#section-4.1.2.3";
		if(!tbsCertSA.equals("")) {
			er.success("222", "tbsCertificate.signature.algorithm", tbsCertSA,  "tbsCertificate.signature.algorithm (name of the algorithm) must be present", rfc);
		} else {
			er.error("222", "tbsCertificate.signature.algorithm", tbsCertSA,  "tbsCertificate.signature.algorithm (name of the algorithm) must be present", rfc);
		}
		
	}

	// DTS 225, tbsCertificate.subject, Required
	public void validateTbsCertificateSubject(ErrorRecorder er, String tbsCertSubject) {
		String rfc = "RFC 5280: 4.1.2.6, 4.1.2.4;http://tools.ietf.org/html/rfc5280#section-4.1.2.4";
		if(!tbsCertSubject.equals("")) {
			er.success("225", "tbsCertificate.subject", tbsCertSubject,  "tbsCertificate.subject (subject name) must be present", rfc);
		} else {
			er.error("225", "tbsCertificate.subject", tbsCertSubject,  "tbsCertificate.subject (subject name) must be present", rfc);
		}
	}
	
	// DTS 240, Extensions.subjectAltName, Conditional
	public void validateExtensionsSubjectAltName(ErrorRecorder er, Collection<List<?>> ExtensionSubjectAltName) {
		String rfc = "RFC 5280: 4.1.2.6;http://tools.ietf.org/html/rfc5280#section-4.1.2.6";
		//System.out.println(ExtensionSubjectAltName);
		if(ExtensionSubjectAltName != null) {
			Iterator it = null;
			
			if (!ExtensionSubjectAltName.isEmpty()){
				it = ExtensionSubjectAltName.iterator();
				er.success("240", "Extensions.subjectAltName", ExtensionSubjectAltName.toString(),  "Must be present", rfc);
			} else {
				er.error("240", "Extensions.subjectAltName", ExtensionSubjectAltName.toString(),  "Must be present", rfc);
			}
			
			
			// C-4 - cert/subjectAltName must contain either rfc822Name (email address without
			// comments between parenthesis or <>) or dNSName extension (IA5String - which means a
			// random string of ASCII chars including control characters, of any length except zero
			// based on rfc 5280)
			// Required
		
			
			// each List contains two items. 1st item is an integer (type of name), second item is the name itself as a String. 
			while (it.hasNext()){
				List<?> currentAltName = (List<String>) it.next();
				int currentNameType = (Integer) currentAltName.get(0);
				
				
				if (currentNameType == 1){   // integer type for rfc822Name
					
					if (ValidationUtils.validateEmailAddressFormatRFC822((String)currentAltName.get(1))){	
						er.success("C4", "Extensions.subjectAltName", currentAltName.get(1).toString(),  "cert/subjectAltName must be an email address", rfc);
						
						
						// C-5 cert/subjectAltName/rfc822Name must be an email address (if present)
						
						er.success("C5", "Extensions.subjectAltName", currentAltName.get(1).toString(),  "cert/subjectAltName/rfc822Name must be an email address", rfc);
					} else {
						er.error("C4", "Extensions.subjectAltName", currentAltName.get(1).toString(),  "cert/subjectAltName must be an email address", rfc);
						er.error("C5", "Extensions.subjectAltName", currentAltName.get(1).toString(),  "cert/subjectAltName/rfc822Name must be an email address", rfc);
					}
					
				} else if (currentNameType == 2){   // integer type for dnsName.
					String dnsName = (String)currentAltName.get(1);
					
					if (ValidationUtils.isAscii(dnsName)){
						er.success("C4", "Extensions.subjectAltName", currentAltName.get(1).toString(),  "cert/subjectAltName must be ASCII", rfc);
					
						// C-7 - check 1 - cert/subjectAltName/dnsName must contain domain name (if present)
						Pattern pattern = Pattern.compile(ValidationUtils.domainNameFormat, Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(dnsName);
						
						if (matcher.matches()){
							er.success("C7", "Extensions.subjectAltName", dnsName,  "cert/subjectAltName/dnsName must contain a domain name. (example: test.validation.com)", rfc);
						} else {
							er.error("C7", "Extensions.subjectAltName", dnsName,  "cert/subjectAltName/dnsName must contain a domain name. (example: test.validation.com)", rfc);
						}
						
						// C-7 - check 3 - cert/subjectAltName/dnsName domain name must match the domain name from cert/subject/emailAddr (if present)
						
						
						
					} else {
						er.error("C4", "Extensions.subjectAltName", currentAltName.get(1).toString(),  "cert/subjectAltName must be an email address", rfc);
					}
					}
				}
		}
	
			

	}

	

	// DTS-165	DigestAlgorithm	Direct Message	Required
	public void validateDigestAlgorithmDirectMessage(ErrorRecorder er, String digestAlgo, String micalg) {
		String rfc = "RFC 5280: 4.1.1.2;http://tools.ietf.org/html/rfc5280#section-4.1.1.2";
		String textDigestAlgo = "";
		// Convert the digest algorithm OID into a string
		if(digestAlgo.equals("1.3.14.3.2.26")) {
			textDigestAlgo = "sha1";
		} else if(digestAlgo.equals("2.16.840.1.101.3.4.2.1")) {
			textDigestAlgo = "sha256";
		}
		
		if(textDigestAlgo.contains("sha1") || textDigestAlgo.contains("sha256")) {
			micalg = micalg.replaceAll("-", "");
			micalg = micalg.replaceAll("\"", "");
			micalg = micalg.toLowerCase();
			if(textDigestAlgo.equals(micalg)) {
				er.success("165", "DigestAlgorithm", "Digest: " + digestAlgo + ", micalg: "+ micalg,  "Digest algorithm must match micalg value", rfc);
			} else {
				er.error("165", "DigestAlgorithm", "Digest: " + digestAlgo + ", micalg: "+ micalg,  "Digest algorithm must match micalg value", rfc);
			}
		} else {
			er.error("165", "DigestAlgorithm", "Digest: " + digestAlgo + ", micalg: "+ micalg,  "Digest algorithm must contain either value \"sha1\" or \"sha256\"", rfc);
		}
		
	}

	@Override
	public void validateSecondMIMEPart(ErrorRecorder er, boolean secondMIMEPart) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateAllNonMIMEMessageHeaders(ErrorRecorder er,
			String nonMIMEHeader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateContentType2(ErrorRecorder er, String contentType) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void validateDTS163(ErrorRecorder er, String dts163) {
		// TODO Auto-generated method stub
		
	}

	/**
	 *  DTS 164, Signed Data, Required
	 */
	@Override
	public void validateSignedData(ErrorRecorder er, CMSProcessable cmsProcessable){
		String rfc = "RFC 5652: 5.1;http://tools.ietf.org/html/rfc5652#section-5.1";	
		if(cmsProcessable.getContent() != null) {
			er.success("164", "Signed Data", cmsProcessable.toString(),  "Must be present", rfc);
		} else {
			er.error("164", "Signed Data", "Not present",  "Must be present", rfc);
		}
	}

	
	@Override
	public void validateDigestAlgorithm(ErrorRecorder er, String digestAlgorithm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateEncapsuledInfo2(ErrorRecorder er,
			String encapsulatedInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateEncapsuledInfo3(ErrorRecorder er,
			String encapsulatedInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateCertificates(ErrorRecorder er, String certificates) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateCrls(ErrorRecorder er, String crls) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateSignerInfos(ErrorRecorder er, String signerInfos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateSignerInfosSid(ErrorRecorder er, String signerInfosSid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateSignerIdentifier(ErrorRecorder er,
			String signerIdentifier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateSignerIdentifierIssueAndSerialNumber(ErrorRecorder er,
			String signerInfos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateSignerInfosDigestAlgorithm(ErrorRecorder er,
			String signerInfosDigestAlgorithm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateSignerIdentifierSubjectKeyIdentifier(ErrorRecorder er,
			String signerInfos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateSignedAttrs(ErrorRecorder er, String signerInfos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateSignedAttrsMessageDigest(ErrorRecorder er,
			String signerInfos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateSignedAttrsContentType(ErrorRecorder er,
			String signerInfos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateSignerInfosSignatureAlgorithm(ErrorRecorder er,
			String signerInfosSignatureAlgorithm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateSignerInfosSignature(ErrorRecorder er,
			String signerInfosSignature) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateSignerInfosUnsignedAttrs(ErrorRecorder er,
			String signerInfosUnsignedAttrs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateBoundary(ErrorRecorder er, String boundary) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateSignedDataAtLeastOneCertificate(ErrorRecorder er, Collection c) {
		String rfc = "RFC 5652: 5.1, 10.2.3;http://tools.ietf.org/html/rfc5652#section-5.1";
		if(!c.isEmpty()) {
			er.success("167", "Signed Data", c.toString(),  "Must be present, Message with at least one certificate", rfc);
		} else {
			er.error("167", "Signed Data", "No signed data",  "Must be present, Message with at least one certificate", rfc);
		}
		
	}

	@Override
	public void validateSignature(ErrorRecorder er, X509Certificate cert, SignerInformation signer, String BC) {
		String rfc = "RFC 5652: 5.1, 10.2.3;http://tools.ietf.org/html/rfc5652#section-5.1";
		try {
			if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(BC).build(cert))) {
				// C-1 - Certificate has not expired - Required
				er.success("C1", "Signature", "The certificate has not expired",  "The certificate must not be expired", rfc);
				er.success("C1", "Signature", "Signature verified",  "", rfc);
			} else {
				er.error("C1", "Signature", "The certificate has expired",  "The certificate must not be expired", rfc);
			}
		} catch (OperatorCreationException e) {
			er.error("C1", "Signature", e.getMessage(),  "The certificate must not be expired", rfc);
			e.printStackTrace();
		} catch (CMSException e) {
			er.error("C1", "Signature", e.getMessage(),  "The certificate must not be expired", rfc);
			e.printStackTrace();
		}

	}
	
	// C2 - Key size <=2048
	public void validateKeySize(ErrorRecorder er, String key){
//		byte[] c = null;
//			try {
//				c = Base64.decode(key);
//			} catch (Base64DecodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			System.out.println(key.getBytes().length);
		
	}

	@Override
	public void validateSecondMIMEPartBody(ErrorRecorder er,
			String secondMIMEPartBody) {
		// TODO Auto-generated method stub
		
	}

	
}
