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
Authors: Frederic de Vaulx
		Diane Azais
		Julien Perugini
 */


package gov.nist.direct.validation.impl;

import junit.framework.Assert;
import gov.nist.direct.validation.MessageValidator;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

public class DirectMessageValidator implements MessageValidator {

	// ************************************************
	// *********** Message headers checks *************
	// ************************************************
	
	
	// xxxxxxxxxxxxxxx SMTP Commands  xxxxxxxxxxxxxxx
	
	// DTS 198, ?, Required
	public void validateDTS198(ErrorRecorder er, String dts198) {
		Assert.fail("Not Yet Implemented");
		
	}
	
	// DTS 100, MAIL FROM SMTP, Required
	public void validateMailFromSMTP(ErrorRecorder er, String mailFromSmtp) {
		Assert.fail("Not Yet Implemented");
		
	}
	
	// DTS 101, RCPT TO, Required
	public void validateRcptTo(ErrorRecorder er, String RcptTo) {
		Assert.fail("Not Yet Implemented");
		
	}
	
	
	// xxxxxxxxxxxxxxx Outer Enveloped Message  xxxxxxxxxxxxxxx
	
	
	// DTS 199, Non-MIME Message Headers, Required
	public void validateNonMIMEMessageHeaders(ErrorRecorder er, String nonMIMEHeader) {
		if(!er.hasErrors()) {
			er.detail("     Success:  DTS 199 - All Non-MIME Message Headers are valid");
		} else {
			er.err("199", "Some MIME Message Headers are not valid", "", "DTS 199", "");
		}
		
	}

	// DTS 200, MIME Entity, Required
	public void validateMIMEEntity(ErrorRecorder er, String MIMEEntity) {
		if(!er.hasErrors()) {
			er.detail("     Success:  DTS 200 - MIME Entity Headers are valid");
		} else {
			er.err("200", "Some MIME Entity Headers are not valid", "", "DTS 200", "");
		}
		
	}

	// DTS 133a, Content-Type, Required
	public void validateMessageContentTypeA(ErrorRecorder er, String messageContentTypeA) {
		messageContentTypeA = messageContentTypeA.split(";")[0];
		if (messageContentTypeA.equals("application/pkcs7-mime")){
			er.detail("     Success:  DTS 133a - Content-Type is valid. Value = 'application/pkcs7-mime'");
		} else {
			er.err("133a", "Content-Type is invalid.", "", "DTS 133a", "");
		}
		
	}

	// DTS 201, Content-Type Name, Optional
	public void validateContentTypeNameOptional(ErrorRecorder er, String contentTypeName) {
		if(contentTypeName.contains("name")) {
			contentTypeName = contentTypeName.split("name=")[1];
			contentTypeName = contentTypeName.split(";")[0];
			if (contentTypeName.equals("smime.p7m")) {
				er.detail("     Success:  DTS 201 - The parameter name is correct, equals 'smime.p7m'");
			} else {
				er.warning("201", "Warning: the parameter 'name' is present but its value should be 'smime.p7m'", "", "DTS 201");
			}
		} else {
			er.err("201", "The parameter 'name' is not present", "", "DTS 201", "");
		} 
		
	}
	
	// DTS 202, Content-Type S/MIME-Type, Optional
	public void validateContentTypeSMIMETypeOptional(ErrorRecorder er, String contentTypeSMIME) {
		if(contentTypeSMIME.contains("smime-type")) {
			contentTypeSMIME = contentTypeSMIME.split("smime-type=")[1];
		}
		if(contentTypeSMIME.contains(";")) {
			contentTypeSMIME = contentTypeSMIME.split(";")[0];
		}
		if (contentTypeSMIME.equals("enveloped-data")) {
			er.detail("     Success:  DTS 202 - The parameter S/MIME Type is correct, equals 'enveloped-data'");
		} else {
			er.err("202", "The parameter S/MIME Type is not correct", "", "DTS 202", "");
		}
		
	}
	
	// DTS 203, Content Disposition, Optional
	public void validateContentDispositionOptional(ErrorRecorder er, String contentDisposition) {
		if(!contentDisposition.equals("")) {
			if (contentDisposition.contains("smime.p7m")) {
				er.detail("     Success:  DTS 203 - The parameter Content-Disposition is correct, equals 'smime.p7m'");
			} else {
				er.warning("203", "Warning: the parameter Content-Disposition is present but its value should be 'smime.p7m'", "", "DTS 203");
			}
		} else {
			er.err("203", "The parameter Content Disposition is not present", "", "DTS 203", "");
		}
		
	}
	
	// DTS 129, Message Body, Required
	public void validateMessageBody(ErrorRecorder er, boolean decrypted) {
		if(decrypted) {
			er.detail("     Success:  DTS 129 - Message Body has been decrypted");
		} else {
			er.err("129", "Message Body has not been decrypted", "", "DTS 129", "");
		}
		
	}
	
	
	// xxxxxxxxxxxxxxx Inner Decrypted Message  xxxxxxxxxxxxxxx

	
	// DTS 204, MIME Entity, Required
	public void validateMIMEEntity2(ErrorRecorder er, boolean mimeEntity) {
		if(mimeEntity) {
			er.detail("     Success:  DTS 204 - MIME Entity verified");
		} else {
			er.err("204", "MIME Entity not verified", "", "DTS 204", "");
		}
		
	}
	
	// DTS 133b, Content-Type, Required
	public void validateMessageContentTypeB(ErrorRecorder er, String messageContentTypeB) {
		if (messageContentTypeB.contains("multipart/signed")){
			er.detail("     Success:  DTS 133b - Content-Type is valid. Value = 'multipart/signed'");
		} else {
			er.err("133b", "Content-Type is invalid.", "", "DTS 133b", "");
		}
		
	}

	// DTS 160, Content-Type micalg, Required
	public void validateContentTypeMicalg(ErrorRecorder er, String contentTypeMicalg) {
		if (contentTypeMicalg.equals("")) {
			er.err("160", "Content-Type Micalg is not present", "", "DTS 160", "");
		} else {
			// Validates the "micalg" parameter value
			if ((contentTypeMicalg.contains("sha")) && (contentTypeMicalg.contains("1") || contentTypeMicalg.contains("256"))) {
				er.detail("     Success:  DTS 160 - The content-type micalg parameter value is correct");
			} else {
				// error code 133-3
				er.err("160", "The content-type micalg parameter value is incorrect.", "", "DTS 160", "");
			}
		}
		
	}

	// DTS 205, Content-Type protocol, Required
	public void validateContentTypeProtocol(ErrorRecorder er, String contentTypeProtocol) {
		if (contentTypeProtocol.equals("\"application/pkcs7-signature\"")){
			er.detail("     Success:  DTS 205 - Content-Type Protocol is valid. Value = \"application/pkcs7-signature\"");
		} else {
			er.err("205", "Content-Type Protocol is invalid.", "", "DTS 205", "");
		}
		
	}

	// DTS 206, Content-Transfer-Encoding, Required
	public void validateContentTransferEncoding(ErrorRecorder er, String contentTransfertEncoding) {
		if (contentTransfertEncoding.equals("quoted-printable") || contentTransfertEncoding.equals("base64") || contentTransfertEncoding.equals("7-bit")){
			er.detail("     Success:  DTS 206 - Content-Transfer-Encoding is valid. Value = " + contentTransfertEncoding);
		} else if(contentTransfertEncoding.equals("")) {
			er.detail("     Info:  DTS 206 - Content-Transfer-Encoding is not present");
		} else {
			er.err("206", "Content-Tranfer-Encoding is invalid.", "", "DTS 206", "");
		}
		
	}

	// DTS ?, MIME Entity Body, Required
	public void validateMIMEEntityBody(ErrorRecorder er, int nbBody) {
		if (nbBody != 2){
        	er.err("???", "Mime Entity body is not valid. Once decrypted, the message should have exactly two MIME parts.", "", "DTS ???", "");
        } else {
        	er.detail("     Success:  DTS ??? - Mime Entity body is valid (has exactly two MIME parts).");
        }
		
	}
	
	
	// xxxxxxxxxxxxxxx Health Content Container  xxxxxxxxxxxxxxx
	

	// DTS 139, First MIME Part, Required
	public void validateFirstMIMEPart(ErrorRecorder er, boolean firstMIMEPart) {
		if(firstMIMEPart) {
			er.detail("     Success:  DTS 139 - First MIME Part verified");
		} else {
			er.err("139", "First MIME Part not verified", "", "DTS 139", "");
		}
		
	}
	
	// DTS 151, First MIME Part Body, Required
	public void validateFirstMIMEPartBody(ErrorRecorder er, boolean firstMIMEPartBody) {
		if(firstMIMEPartBody) {
			er.detail("     Success:  DTS 151 - First MIME Part Body verified");
		} else {
			er.err("151", "First MIME Part Body not verified", "", "DTS 151", "");
		}
	}
	
	
	// xxxxxxxxxxxxxxx Signature  xxxxxxxxxxxxxxx
	
	
	// DTS 152, Second MIME Part, Required
	public void validateSecondMIMEPart(ErrorRecorder er, boolean secondMIMEPart) {
		if(secondMIMEPart) {
			er.detail("     Success:  DTS 152 - Second MIME Part verified");
		} else {
			er.err("152", "Second MIME Part not verified", "", "DTS 152", "");
		}
	
	}
	
	// DTS ?, All Non-MIME Message Headers
	public void validateAllNonMIMEMessageHeaders(ErrorRecorder er, String nonMIMEHeader) {
		if(er.hasErrors()) {
			er.err("?", "Some Non MIME Message Headers are not valid", "", "DTS ?", "");
		} else {
			er.detail("All Non MIME Message Headers are valids");
		}
		
	}
	
	// DTS 155, Content-Type, Required
	public void validateContentType2(ErrorRecorder er, String contentType) {
		if(contentType.contains("multipart/signed")) {
			er.detail("     Success:  DTS 155 - Content-Type verified");
		} else {
			er.err("155", "Content-Type is not equal to multipart/signed", "", "DTS 151", "");
		}
	
	}
	
	// DTS 158, Second MIME Part Body, Required
	public void validateSecondMIMEPartBody(ErrorRecorder er, String secondMIMEPartBody) {
		er.detail("     Success:  DTS 158 - Second MIME part Body verified");
	
	}
	
	// DTS 163, ?, Required
	public void validateDTS163(ErrorRecorder er, String dts163) {
		Assert.fail("Not Yet Implemented");
	
	}

	
	// DTS 165, DigestAlgorithm, Required
	public void validateDigestAlgorithm(ErrorRecorder er, String digestAlgorithm) {
		Assert.fail("Not Yet Implemented");
		
	}

	// DTS 166, EncapsuledContentInfo, Required
	public void validateEncapsuledInfo(ErrorRecorder er, String encapsulatedInfo) {
		Assert.fail("Not Yet Implemented");
		
	}

	// DTS 182, EncapsuledContentInfo.eContentInfo, Required
	public void validateEncapsuledInfo2(ErrorRecorder er, String encapsulatedInfo) {
		if(encapsulatedInfo.equals("")) {
			er.detail("Success:  DTS 182 - EncapsulatedContentInfo is not present");
		} else {
			er.err("182", "EncapsulatedContentInfo must be absent", "", "DTS 182", "");
		}
		
	}
	
	// DTS 183, EncapsuledContentInfo.eContent, Optional
	public void validateEncapsuledInfo3(ErrorRecorder er, String encapsulatedInfo) {
		Assert.fail("Not Yet Implemented");
		
	}
	
	// DTS 167, Certificates
	public void validateCertificates(ErrorRecorder er, String certificates) {
		Assert.fail("Not Yet Implemented");
		
	}
	
	// DTS 168, Crls
	public void validateCrls(ErrorRecorder er, String crls) {
		Assert.fail("Not Yet Implemented");
		
	}
	
	// DTS 169, SignerInfos, Optional
	public void validateSignerInfos(ErrorRecorder er, String signerInfos) {
		Assert.fail("Not Yet Implemented");
		
	}
	
	// DTS 173, SignerInfos.sid, Optional
	public void validateSignerInfosSid(ErrorRecorder er, String signerInfosSid) {
		Assert.fail("Not Yet Implemented");
		
	}

	// DTS 174, SignerInfos.signerIdentifier, Required
	public void validateSignerIdentifier(ErrorRecorder er, String signerIdentifier) {
		Assert.fail("Not Yet Implemented");
		
	}

	// DTS 175, SignerInfos.signerIdentifier.issuerAndSerialNumber, Conditional
	public void validateSignerIdentifierIssueAndSerialNumber(ErrorRecorder er, String signerInfos) {
		Assert.fail("Not Yet Implemented");
		
	}
	
	// DTS 176, SignerInfos.signerIdentifier.subjectKeyIdentifier, Condtional
	public void validateSignerIdentifierSubjectKeyIdentifier(ErrorRecorder er, String signerInfos) {
		Assert.fail("Not Yet Implemented");
		
	}
	
	// DTS 177, SignerInfos.digestAlgorithm, Required
	public void validateSignerInfosDigestAlgorithm(ErrorRecorder er, String signerInfosDigestAlgorithm) {
		Assert.fail("Not Yet Implemented");
		
	}

	// DTS 178, SignerInfos.signedAttrs, Conditional
	public void validateSignedAttrs(ErrorRecorder er, String signerInfos) {
		Assert.fail("Not Yet Implemented");
		
	}
	
	// DTS 179, SignerInfos.signedAttrs.messageDigest, Conditional
	public void validateSignedAttrsMessageDigest(ErrorRecorder er, String signerInfos) {
		Assert.fail("Not Yet Implemented");
		
	}
	
	// DTS 180, SignerInfos.signedAttrs.contentType, Conditional
	public void validateSignedAttrsContentType(ErrorRecorder er, String signerInfos) {
		Assert.fail("Not Yet Implemented");
		
	}
	
	// DTS 170, SignerInfos.SignatureAlgorithm, Required
	public void validateSignerInfosSignatureAlgorithm(ErrorRecorder er, String signerInfosSignatureAlgorithm) {
		Assert.fail("Not Yet Implemented");
		
	}

	// DTS 171, SignerInfos.Signature, Required
	public void validateSignerInfosSignature(ErrorRecorder er, String signerInfosSignature) {
		Assert.fail("Not Yet Implemented");
		
	}

	// DTS 181, SignerInfos.unsignedAttrs, Optional
	public void validateSignerInfosUnsignedAttrs(ErrorRecorder er,
			String signerInfosUnsignedAttrs) {
		Assert.fail("Not Yet Implemented");
		
	}
	
	// DTS 172, Boundary, Required
	public void validateBoundary(ErrorRecorder er, String boundary) {
		Assert.fail("Not Yet Implemented");
		
	}
	

	
}
