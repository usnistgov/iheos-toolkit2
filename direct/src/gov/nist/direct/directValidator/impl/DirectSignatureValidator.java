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

import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;

import junit.framework.Assert;
import gov.nist.direct.directValidator.interfaces.MessageContentValidator;
import gov.nist.direct.directValidator.interfaces.SignatureValidator;
import gov.nist.direct.utils.ValidationUtils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

public class DirectSignatureValidator implements SignatureValidator {

	// ************************************************
	// ************** Signature checks ****************
	// ************************************************
	

	// DTS 166, SignedData.encapContentInfo, Required
	public void validateSignedDataEncapContentInfo(ErrorRecorder er, String SignedDataEncapContentInfo) {
		if(!SignedDataEncapContentInfo.equals("")) {
			er.detail("     Success:  DTS 166 - The field SignedData.encapContentInfo (signed content + content type identifier) is present");
		} else {
			er.err("166", "The field SignedData.encapContentInfo (signed content + content type identifier) is missing.", "", "DTS 166", "");
		}
		
	}

	// DTS 222, tbsCertificate.signature.algorithm, Required
	public void validateTbsCertificateSA(ErrorRecorder er, String tbsCertSA) {
		if(!tbsCertSA.equals("")) {
			er.detail("     Success:  DTS 222 - The field tbsCertificate.signature.algorithm (name of the algorithm) is present");
		} else {
			er.err("222", "The field tbsCertificate.signature.algorithm (name of the algorithm) is missing.", "", "DTS 222", "");
		}
		
	}

	// DTS 225, tbsCertificate.subject, Required
	public void validateTbsCertificateSubject(ErrorRecorder er, String tbsCertSubject) {
		if(!tbsCertSubject.equals("")) {
			er.detail("     Success:  DTS 225 - The field tbsCertificate.subject (subject name) is present.");
		} else {
			er.err("225", "The field tbsCertificate.subject (subject name) is missing.", "", "DTS 225", "");
		}
	}
	
	// DTS 240, Extensions.subjectAltName, Conditional
	public void validateExtensionsSubjectAltName(ErrorRecorder er, Collection<List<?>> ExtensionSubjectAltName) {
		//System.out.println(ExtensionSubjectAltName);
		if(ExtensionSubjectAltName != null) {
			Iterator it = null;
			
			if (!ExtensionSubjectAltName.isEmpty()){
				it = ExtensionSubjectAltName.iterator();
				er.detail("     Success:  DTS 240 - The field Extensions.subjectAltName is present. Detected " + ExtensionSubjectAltName.size() + " subjectAltName extension(s).");
			} else {
				er.err("240", "The field Extensions.subjectAltName is missing.", "", "DTS 240", "");
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
						er.detail("     Success:  C-4 - The field cert/subjectAltName " + currentAltName.get(1) + " is correct.");
						
						
						// C-5 cert/subjectAltName/rfc822Name must be an email address (if present)
						
						er.detail("     Success:  C-5 - The field cert/subjectAltName/rfc822Name is an email address.");
					} else {
						er.err("C-4", "The field cert/subjectAltName " + currentAltName.get(1) + " is not valid.", "", "C-4", "");
						er.err("C-5", "The field cert/subjectAltName/rfc822Name, if present, must be an email address.", "", "C-5", "");
					}
					
				} else if (currentNameType == 2){   // integer type for dnsName.
					String dnsName = (String)currentAltName.get(1);
					
					if (ValidationUtils.isAscii(dnsName)){
						er.detail("     Success:  C-4 - The field cert/subjectAltName " + currentAltName.get(1) + " is correct.");
					
						// C-7 - check 1 - cert/subjectAltName/dnsName must contain domain name (if present)
						Pattern pattern = Pattern.compile(ValidationUtils.domainNameFormat, Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(dnsName);
						
						if (matcher.matches()){
							er.detail("     Success:  C-7 - The field cert/subjectAltName/dnsName contains a domain name.");
						} else {
							er.err("C-7", "The field cert/subjectAltName/dnsName, if present: " + dnsName + ", must contain a domain name. (example: test.validation.com)", "", "C-7", "");
						}
						
						// C-7 - check 3 - cert/subjectAltName/dnsName domain name must match the domain name from cert/subject/emailAddr (if present)
						
						
						
					} else {
						er.err("C-4", "The field cert/subjectAltName " + currentAltName.get(1) + " is not valid.", "", "C-4", "");
					}
					}
				}
		}
	
			

	}

	

	// DTS-165	DigestAlgorithm	Direct Message	Required
	public void validateDigestAlgorithmDirectMessage(ErrorRecorder er, String digestAlgo, String micalg) {
		if(digestAlgo.contains("sha1") || digestAlgo.contains("sha256")) {
			digestAlgo = digestAlgo.split("with")[0];
			digestAlgo = digestAlgo.replaceAll("-", "");
			micalg = micalg.replaceAll("-", "");
			micalg = micalg.replaceAll("\"", "");
			micalg = micalg.toLowerCase();
			if(digestAlgo.equals(micalg)) {
				er.detail("     Success:  DTS 165 - Digest Algorithm is valid");
			} else {
				er.err("165", "Digest Algorithm does not equal the S/MIME content-type micalg value.", "", "", "DTS 165");
			}
		} else {
			er.err("165", "Digest Algorithm is not valid. It MUST contain either value \"sha1\" or \"sha256\".", "", "", "DTS 165");
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
			if(cmsProcessable.getContent() != null) {
				er.detail("     Success:  DTS 164 - Signed data is present.");
			} else {
				er.err("164", "There is no signed data in the message.", "", "DTS 164", "");
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
	public void validateSignedDataAtLeastOneCertificate(ErrorRecorder er,
			Collection c) {
		if(!c.isEmpty()) {
			er.detail("     Success:  DTS 167 - The message is signed with at least one certificate.");
		} else {
			er.err("167", "The signature does not contain at least one signing certificate.", "", "DTS 167", "");
		}
		
	}

	@Override
	public void validateSignature(ErrorRecorder er, X509Certificate cert, SignerInformation signer, String BC) {
		try {
			if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(BC).build(cert)))
			{
				// C-1 - Certificate has not expired - Required
				er.detail("     Success:  C-1 - The certificate has not expired.");
				er.detail("Signature Verified");
			}
			else
			{
				er.err("C1", "Signature Failed! The certificate has expired.", "", "C1", "C1");
			}
		} catch (OperatorCreationException e) {
			er.err("C1", "Signature Failed! The certificate has expired.", "", "C1", "C1");
			e.printStackTrace();
		} catch (CMSException e) {
			er.err("C1", "Signature Failed! The certificate has expired.", "", "C1", "C1");
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
