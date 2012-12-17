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

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import javax.mail.Address;
import javax.mail.Part;

import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.SignerInformation;

import gov.nist.direct.validation.MessageValidatorFacade;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

public class DirectMimeMessageValidatorFacade implements MessageValidatorFacade {

	private DirectMimeEntityValidator mimeEntityValidator = new DirectMimeEntityValidator();
	private DirectMessageHeadersValidator messageHeadersValidator = new DirectMessageHeadersValidator();
	private DirectMessageValidator directMessageValidator = new DirectMessageValidator();
	private DirectSignatureValidator signatureValidator = new DirectSignatureValidator();
	private DirectWrappedMessageHeadersValidator wrappedMessageHeaderValidator = new DirectWrappedMessageHeadersValidator();

	// ************************************************
	// *********** MIME Entity checks *****************
	// ************************************************
	
	// xxxxxxxxxxxxxx MIME Headers xxxxxxxxxxxxxxxxxxxx
	
	// DTS 190, All Mime Header Fields, Required
	public void validateAllMimeHeaderFields(ErrorRecorder er, String header) {
		mimeEntityValidator.validateAllMimeHeaderFields(er, header);
		
	}
	
	// DTS 102a, MIME-Version
	public void validateAllMIMEVersion(ErrorRecorder er, String mimeVersion) {
		mimeEntityValidator.validateAllMIMEVersion(er, mimeVersion);
		
	}

	// DTS 133-145-146, Content-Type, Required
	public void validateContentType(ErrorRecorder er, String contentType) {
		mimeEntityValidator.validateContentType(er, contentType);
		
	}

	// DTS 191, Content-Type Subtype, Required
	public void validateContentTypeSubtype(ErrorRecorder er, String subtype) {
		mimeEntityValidator.validateContentTypeSubtype(er, subtype);
		
	}

	// DTS 192, Content-Type name, Conditional
	public void validateContentTypeName(ErrorRecorder er, String contentTypeName) {
		mimeEntityValidator.validateContentTypeName(er, contentTypeName);
		
	}

	// DTS 193, Content-Type S/MIME-Type, Conditional
	public void validateContentTypeSMIMEType(ErrorRecorder er, String contentTypeSMIMEType) {
		mimeEntityValidator.validateContentTypeSMIMEType(er, contentTypeSMIMEType);
		
	}

	// DTS 137-140, Content-Type Boundary, Conditional
	public void validateContentTypeBoundary(ErrorRecorder er, String contentTypeBoundary) {
		mimeEntityValidator.validateContentTypeBoundary(er, contentTypeBoundary);
		
	}

	// DTS 156, Content-type Disposition, Conditional
	public void validateContentTypeDisposition(ErrorRecorder er, String contentTypeDisposition, String contentType) {
		mimeEntityValidator.validateContentTypeDisposition(er, contentTypeDisposition, contentType);
		
	}
	
	// DTS 161-194, Content-Disposition filename, Optional
	public void validateContentDispositionFilename(ErrorRecorder er, String content) {
		mimeEntityValidator.validateContentDispositionFilename(er, content);
		
	}
	
	// DTS 134-143, Content-Id, Optional
	public void validateContentId(ErrorRecorder er, String content) {
		mimeEntityValidator.validateContentId(er, content);
		
	}
	
	// DTS 135-142-144, Content-Description, Optional
	public void validateContentDescription(ErrorRecorder er, String content) {
		mimeEntityValidator.validateContentDescription(er, content);
		
	}
	
	// DTS 136-148-157, Content-Transfer-Encoding, Optional
	public void validateContentTransferEncodingOptional(ErrorRecorder er, String content) {
		mimeEntityValidator.validateContentTransferEncodingOptional(er, content);
		
	}
	
	// DTS 138-149, Content-*, Optional
	public void validateContentAll(ErrorRecorder er, String content) {
		mimeEntityValidator.validateContentAll(er, content);
		
	}
	
	// xxxxxxxxxxxxxxx MIME Body  xxxxxxxxxxxxxxx
	
	// DTS 195, Body, Required
	public void validateBody(ErrorRecorder er, Part p, String body) {
		mimeEntityValidator.validateBody(er, p, body);
		
	}
	
	
	
	// ************************************************
	// *********** Message headers checks *************
	// ************************************************
	

	// DTS 196, All Headers, Required
	public void validateAllHeaders(ErrorRecorder er, String[] header, String[] headerContent) {
		messageHeadersValidator.validateAllHeaders(er, header, headerContent);
		
	}
	
	// DTS 103-105, Return Path, Conditional
	public void validateReturnPath(ErrorRecorder er, String returnPath) {
		messageHeadersValidator.validateReturnPath(er, returnPath);
		
	}
	
	// DTS 104-106, Received, Conditional
	public void validateReceived(ErrorRecorder er, String received) {
		messageHeadersValidator.validateReceived(er, received);
		
	}

	// DTS 197, Resent Fields, Required
	public void validateResentFields(ErrorRecorder er, String[] resentField) {
		messageHeadersValidator.validateResentFields(er, resentField);
		
	}
	
	// DTS 107, Resent-Date, Conditional
	public void validateResentDate(ErrorRecorder er, String resentDate) {
		messageHeadersValidator.validateResentDate(er, resentDate);
		
	}
	
	// DTS 108, Resent-From, Conditional
	public void validateResentFrom(ErrorRecorder er, String resentFrom) {
		messageHeadersValidator.validateResentFrom(er, resentFrom);
		
	}
	
	// DTS 109, Resent-Sender, Conditional
	public void validateResentSender(ErrorRecorder er, String resentSender, String resentFrom) {
		messageHeadersValidator.validateResentSender(er, resentSender, resentFrom);
		
	}

	// DTS 110, Resent-to, Optional
	public void validateResentTo(ErrorRecorder er, String resentTo) {
		messageHeadersValidator.validateResentTo(er, resentTo);
		
	}

	// DTS 111, Resent-cc, Optional
	public void validateResentCc(ErrorRecorder er, String resentCc) {
		messageHeadersValidator.validateResentCc(er, resentCc);
		
	}

	// DTS 112, Resent-bcc, Optional
	public void validateResentBcc(ErrorRecorder er, String resentBcc) {
		messageHeadersValidator.validateResentBcc(er, resentBcc);
		
	}

	// DTS 113, Resent-Msg-Id, Conditional
	public void validateResentMsgId(ErrorRecorder er, String resentMsgId) {
		messageHeadersValidator.validateResentMsgId(er, resentMsgId);
		
	}
	
	// DTS 114, Orig-Date, Required
	public void validateOrigDate(ErrorRecorder er, String origDate) {
		messageHeadersValidator.validateOrigDate(er, origDate);
	}

	// DTS 115, From, Required
	public void validateFrom(ErrorRecorder er, String from) {
		messageHeadersValidator.validateFrom(er, from);
		
	}
	
	// DTS 116, Sender, Conditional
	public void validateSender(ErrorRecorder er, String sender, Address[] from) {
		messageHeadersValidator.validateSender(er, sender, from);
		
	}
	
	// DTS 117, Reply-To, Optional
	public void validateReplyTo(ErrorRecorder er, String replyTo) {
		messageHeadersValidator.validateReplyTo(er, replyTo);
		
	}

	// DTS 118, To, Required
	public void validateTo(ErrorRecorder er, String to) {
		messageHeadersValidator.validateTo(er, to);
		
	}
	
	// DTS 119, cc, Optional
	public void validateCc(ErrorRecorder er, String cc) {
		messageHeadersValidator.validateCc(er, cc);
		
	}
	
	// DTS 120, Bcc, Optional
	public void validateBcc(ErrorRecorder er, String bcc) {
		messageHeadersValidator.validateBcc(er, bcc);
		
	}

	// DTS 121, Message-Id, Required
	public void validateMessageId(ErrorRecorder er, String messageId) {
		messageHeadersValidator.validateMessageId(er, messageId);
	}

	// DTS 122, In-reply-to, Optional
	public void validateInReplyTo(ErrorRecorder er, String inReplyTo, String date) {
		messageHeadersValidator.validateInReplyTo(er, inReplyTo, date);
		
	}
	
	// DTS 123, References, Optional
	public void validateReferences(ErrorRecorder er, String references) {
		messageHeadersValidator.validateReferences(er, references);
		
	}
	
	// DTS 124, Subject, Optional
	public void validateSubject(ErrorRecorder er, String subject, String filename) {
		messageHeadersValidator.validateSubject(er, subject, filename);
		
	}
	
	// DTS 125, Comments, Optional
	public void validateComments(ErrorRecorder er, String comments) {
		messageHeadersValidator.validateComments(er, comments);
		
	}
	
	// DTS 126, Keywords, Optional
	public void validateKeywords(ErrorRecorder er, String keyword) {
		messageHeadersValidator.validateKeywords(er, keyword);
		
	}
	
	// DTS 127, Optional-field, Optional
	public void validateOptionalField(ErrorRecorder er, String optionalField) {
		messageHeadersValidator.validateOptionalField(er, optionalField);
		
	}
	
	// DTS 128, Disposition-Notification-To, Optional
	public void validateDispositionNotificationTo(ErrorRecorder er, String dispositionNotificationTo) {
		messageHeadersValidator.validateDispositionNotificationTo(er, dispositionNotificationTo);
		
	}
	
	// DTS 102b, MIME-Version, Required
	public void validateMIMEVersion(ErrorRecorder er, String MIMEVersion) {
		messageHeadersValidator.validateMIMEVersion(er, MIMEVersion);
		
	}

	
	// ************************************************
	// *********** Message headers checks *************
	// ************************************************
	
	
	// xxxxxxxxxxxxxxx SMTP Commands  xxxxxxxxxxxxxxx
	
	// DTS 198, ?, Required
	public void validateDTS198(ErrorRecorder er, String dts198) {
		directMessageValidator.validateDTS198(er, dts198);
		
	}
	
	// DTS 100, MAIL FROM SMTP, Required
	public void validateMailFromSMTP(ErrorRecorder er, String mailFromSmtp) {
		directMessageValidator.validateMailFromSMTP(er, mailFromSmtp);
		
	}
	
	// DTS 101, RCPT TO, Required
	public void validateRcptTo(ErrorRecorder er, String RcptTo) {
		directMessageValidator.validateRcptTo(er, RcptTo);
		
	}
	
	
	// xxxxxxxxxxxxxxx Outer Enveloped Message  xxxxxxxxxxxxxxx
	
	
	// DTS 199, Non-MIME Message Headers, Required
	public void validateNonMIMEMessageHeaders(ErrorRecorder er, String nonMIMEHeader) {
		directMessageValidator.validateNonMIMEMessageHeaders(er, nonMIMEHeader);
		
	}

	// DTS 200, MIME Entity, Required
	public void validateMIMEEntity(ErrorRecorder er, String MIMEEntity) {
		directMessageValidator.validateMIMEEntity(er, MIMEEntity);
		
	}

	// DTS 133a, Content-Type, Required
	public void validateMessageContentTypeA(ErrorRecorder er, String messageContentTypeA) {
		directMessageValidator.validateMessageContentTypeA(er, messageContentTypeA);
		
	}

	// DTS 201, Content-Type Name, Optional
	public void validateContentTypeNameOptional(ErrorRecorder er, String contentTypeName) {
		directMessageValidator.validateContentTypeNameOptional(er, contentTypeName);
		
	}
	
	// DTS 202, Content-Type S/MIME-Type, Optional
	public void validateContentTypeSMIMETypeOptional(ErrorRecorder er, String contentTypeSMIME) {
		directMessageValidator.validateContentTypeSMIMETypeOptional(er, contentTypeSMIME);
		
	}
	
	// DTS 203, Content Disposition, Optional
	public void validateContentDispositionOptional(ErrorRecorder er, String contentDisposition) {
		directMessageValidator.validateContentDispositionOptional(er, contentDisposition);
		
	}
	
	// DTS 129, Message Body, Required
	public void validateMessageBody(ErrorRecorder er, boolean decrypted) {
		directMessageValidator.validateMessageBody(er, decrypted);
		
	}
	
	
	// xxxxxxxxxxxxxxx Inner Decrypted Message  xxxxxxxxxxxxxxx

	
	// DTS 204, MIME Entity, Required
	public void validateMIMEEntity2(ErrorRecorder er, boolean mimeEntity) {
		 directMessageValidator.validateMIMEEntity2(er, mimeEntity);
		
	}
	
	// DTS 133b, Content-Type, Required
	public void validateMessageContentTypeB(ErrorRecorder er, String messageContentTypeB) {
		directMessageValidator.validateMessageContentTypeB(er, messageContentTypeB);
		
	}

	// DTS 160, Content-Type micalg, Required
	public void validateContentTypeMicalg(ErrorRecorder er, String contentTypeMicalg) {
		directMessageValidator.validateContentTypeMicalg(er, contentTypeMicalg);
		
	}

	// DTS 205, Content-Type protocol, Required
	public void validateContentTypeProtocol(ErrorRecorder er, String contentTypeProtocol) {
		directMessageValidator.validateContentTypeProtocol(er, contentTypeProtocol);
		
	}

	// DTS 206, Content-Transfer-Encoding, Required
	public void validateContentTransferEncoding(ErrorRecorder er, String contentTransfertEncoding) {
		directMessageValidator.validateContentTransferEncoding(er, contentTransfertEncoding);
		
	}

	// DTS ?, MIME Entity Body, Required
	public void validateMIMEEntityBody(ErrorRecorder er, int nbBody) {
		directMessageValidator.validateMIMEEntityBody(er, nbBody);
		
	}
	
	
	// xxxxxxxxxxxxxxx Health Content Container  xxxxxxxxxxxxxxx
	

	// DTS 139, First MIME Part, Required
	public void validateFirstMIMEPart(ErrorRecorder er, boolean firstMIMEPart) {
		directMessageValidator.validateFirstMIMEPart(er, firstMIMEPart);
		
	}
	
	// DTS 151, First MIME Part Body, Required
	public void validateFirstMIMEPartBody(ErrorRecorder er, boolean firstMIMEPartBody) {
		directMessageValidator.validateFirstMIMEPartBody(er, firstMIMEPartBody);
	
	}
	
	
	// xxxxxxxxxxxxxxx Signature  xxxxxxxxxxxxxxx
	
	
	// DTS 152, Second MIME Part, Required
	public void validateSecondMIMEPart(ErrorRecorder er, boolean secondMIMEPart) {
		directMessageValidator.validateSecondMIMEPart(er, secondMIMEPart);
	
	}
	
	// DTS ?, All Non-MIME Message Headers
	public void validateAllNonMIMEMessageHeaders(ErrorRecorder er, String nonMIMEHeader) {
		directMessageValidator.validateAllNonMIMEMessageHeaders(er, nonMIMEHeader);
		
	}
	
	// DTS 155, Content-Type, Required
	public void validateContentType2(ErrorRecorder er, String contentType) {
		directMessageValidator.validateContentType2(er, contentType);
	
	}
	
	// DTS 158, Second MIME Part Body, Required
	public void validateSecondMIMEPartBody(ErrorRecorder er, String secondMIMEPartBody) {
		directMessageValidator.validateSecondMIMEPartBody(er, secondMIMEPartBody);
	
	}
	
	// DTS 163, ?, Required
	public void validateDTS163(ErrorRecorder er, String dts163) {
		directMessageValidator.validateDTS163(er, dts163);
	
	}

	// DTS 164, Signed Data, Required
	public void validateSignedData(ErrorRecorder er, CMSProcessable signedData) {
		signatureValidator.validateSignedData(er, signedData);
		
	}
	
	// DTS 165, DigestAlgorithm, Required
	public void validateDigestAlgorithm(ErrorRecorder er, String digestAlgorithm) {
		directMessageValidator.validateDigestAlgorithm(er, digestAlgorithm);
		
	}

	// DTS 166, EncapsuledContentInfo, Required
	public void validateEncapsuledInfo(ErrorRecorder er, String encapsulatedInfo) {
		directMessageValidator.validateEncapsuledInfo(er, encapsulatedInfo);
		
	}

	// DTS 182, EncapsuledContentInfo.eContentInfo, Required
	public void validateEncapsuledInfo2(ErrorRecorder er, String encapsulatedInfo) {
		directMessageValidator.validateEncapsuledInfo2(er, encapsulatedInfo);
		
	}
	
	// DTS 183, EncapsuledContentInfo.eContent, Optional
	public void validateEncapsuledInfo3(ErrorRecorder er, String encapsulatedInfo) {
		directMessageValidator.validateEncapsuledInfo3(er, encapsulatedInfo);
		
	}
	
	// DTS 167, Certificates
	public void validateCertificates(ErrorRecorder er, String certificates) {
		directMessageValidator.validateCertificates(er, certificates);
		
	}
	
	// DTS 168, Crls
	public void validateCrls(ErrorRecorder er, String crls) {
		directMessageValidator.validateCrls(er, crls);
		
	}
	
	// DTS 169, SignerInfos, Optional
	public void validateSignerInfos(ErrorRecorder er, String signerInfos) {
		directMessageValidator.validateSignerInfos(er, signerInfos);
		
	}
	
	// DTS 173, SignerInfos.sid, Optional
	public void validateSignerInfosSid(ErrorRecorder er, String signerInfosSid) {
		directMessageValidator.validateSignerInfosSid(er, signerInfosSid);
		
	}

	// DTS 174, SignerInfos.signerIdentifier, Required
	public void validateSignerIdentifier(ErrorRecorder er, String signerIdentifier) {
		directMessageValidator.validateSignerIdentifier(er, signerIdentifier);
		
	}

	// DTS 175, SignerInfos.signerIdentifier.issuerAndSerialNumber, Conditional
	public void validateSignerIdentifierIssueAndSerialNumber(ErrorRecorder er, String signerInfos) {
		directMessageValidator.validateSignerIdentifierIssueAndSerialNumber(er, signerInfos);
		
	}
	
	// DTS 176, SignerInfos.signerIdentifier.subjectKeyIdentifier, Condtional
	public void validateSignerIdentifierSubjectKeyIdentifier(ErrorRecorder er, String signerInfos) {
		directMessageValidator.validateSignerIdentifierSubjectKeyIdentifier(er, signerInfos);
		
	}
	
	// DTS 177, SignerInfos.digestAlgorithm, Required
	public void validateSignerInfosDigestAlgorithm(ErrorRecorder er, String signerInfosDigestAlgorithm) {
		directMessageValidator.validateSignerInfosDigestAlgorithm(er, signerInfosDigestAlgorithm);
		
	}

	// DTS 178, SignerInfos.signedAttrs, Conditional
	public void validateSignedAttrs(ErrorRecorder er, String signerInfos) {
		directMessageValidator.validateSignedAttrs(er, signerInfos);
		
	}
	
	// DTS 179, SignerInfos.signedAttrs.messageDigest, Conditional
	public void validateSignedAttrsMessageDigest(ErrorRecorder er, String signerInfos) {
		directMessageValidator.validateSignedAttrsMessageDigest(er, signerInfos);
		
	}
	
	// DTS 180, SignerInfos.signedAttrs.contentType, Conditional
	public void validateSignedAttrsContentType(ErrorRecorder er, String signerInfos) {
		directMessageValidator.validateSignedAttrsContentType(er, signerInfos);
		
	}
	
	// DTS 170, SignerInfos.SignatureAlgorithm, Required
	public void validateSignerInfosSignatureAlgorithm(ErrorRecorder er, String signerInfosSignatureAlgorithm) {
		directMessageValidator.validateSignerInfosSignatureAlgorithm(er, signerInfosSignatureAlgorithm);
		
	}

	// DTS 171, SignerInfos.Signature, Required
	public void validateSignerInfosSignature(ErrorRecorder er, String signerInfosSignature) {
		directMessageValidator.validateSignerInfosSignature(er, signerInfosSignature);
		
	}

	// DTS 181, SignerInfos.unsignedAttrs, Optional
	public void validateSignerInfosUnsignedAttrs(ErrorRecorder er, String signerInfosUnsignedAttrs) {
		directMessageValidator.validateSignerInfosUnsignedAttrs(er, signerInfosUnsignedAttrs);
		
	}
	
	// DTS 172, Boundary, Required
	public void validateBoundary(ErrorRecorder er, String boundary) {
		directMessageValidator.validateBoundary(er, boundary);
		
	}

	// DTS 166, SignedData.encapContentInfo, Required
	public void validateSignedDataEncapContentInfo(ErrorRecorder er, String SignedDataEncapContentInfo) {
		signatureValidator.validateSignedDataEncapContentInfo(er, SignedDataEncapContentInfo);
		
	}

	// DTS 222, tbsCertificate.signature.algorithm, Required
	public void validateTbsCertificateSA(ErrorRecorder er, String tbsCertSA) {
		signatureValidator.validateTbsCertificateSA(er, tbsCertSA);
		
	}

	// DTS 225, tbsCertificate.subject, Required
	public void validateTbsCertificateSubject(ErrorRecorder er, String tbsCertSubject) {
		signatureValidator.validateTbsCertificateSubject(er, tbsCertSubject);
		
	}
	
	// DTS 240, Extensions.subjectAltName, Conditional
	public void validateExtensionsSubjectAltName(ErrorRecorder er, Collection<List<?>> ExtensionSubjectAltName) {
		signatureValidator.validateExtensionsSubjectAltName(er, ExtensionSubjectAltName);
		
	}



	// DTS-165	DigestAlgorithm	Direct Message	Required ???
	public void validateDigestAlgorithmDirectMessage(ErrorRecorder er, String digestAlgo, String micalg) {
		signatureValidator.validateDigestAlgorithmDirectMessage(er, digestAlgo, micalg);
	}

	// DTS 167, SignedData.certificates must contain at least one certificate
	public void validateSignedDataAtLeastOneCertificate(ErrorRecorder er,
			Collection c) {
		signatureValidator.validateSignedDataAtLeastOneCertificate(er, c);
		
	}

	@Override
	public void validateSignature(ErrorRecorder er, X509Certificate cert,
			SignerInformation signer, String bC) {
		signatureValidator.validateSignature(er, cert, signer, bC);
		
	}
	
	public void validateKeySize(ErrorRecorder er, String key){
		signatureValidator.validateKeySize(er, key);
	}
	
	
	/**
	 * Wrapped Message Validation
	 */
	
	// DTS 196, All Headers, Required
		public void validateWrappedAllHeaders(ErrorRecorder er, String[] header, String[] headerContent) {
			wrappedMessageHeaderValidator.validateWrappedAllHeaders(er, header, headerContent);
			
		}
		
		// DTS 103-105, Return Path, Conditional
		public void validateWrappedReturnPath(ErrorRecorder er, String returnPath) {
			wrappedMessageHeaderValidator.validateWrappedReturnPath(er, returnPath);
			
		}
		
		// DTS 104-106, Received, Conditional
		public void validateWrappedReceived(ErrorRecorder er, String received) {
			wrappedMessageHeaderValidator.validateWrappedReceived(er, received);
			
		}

		// DTS 197, Resent Fields, Required
		public void validateWrappedResentFields(ErrorRecorder er, String[] resentField) {
			wrappedMessageHeaderValidator.validateWrappedResentFields(er, resentField);
			
		}
		
		// DTS 107, Resent-Date, Conditional
		public void validateWrappedResentDate(ErrorRecorder er, String resentDate) {
			wrappedMessageHeaderValidator.validateWrappedResentDate(er, resentDate);
			
		}
		
		// DTS 108, Resent-From, Conditional
		public void validateWrappedResentFrom(ErrorRecorder er, String resentFrom) {
			wrappedMessageHeaderValidator.validateWrappedResentFrom(er, resentFrom);
			
		}
		
		// DTS 109, Resent-Sender, Conditional
		public void validateWrappedResentSender(ErrorRecorder er, String resentSender, String resentFrom) {
			wrappedMessageHeaderValidator.validateWrappedResentSender(er, resentSender, resentFrom);
			
		}

		// DTS 110, Resent-to, Optional
		public void validateWrappedResentTo(ErrorRecorder er, String resentTo) {
			wrappedMessageHeaderValidator.validateWrappedResentTo(er, resentTo);
			
		}

		// DTS 111, Resent-cc, Optional
		public void validateWrappedResentCc(ErrorRecorder er, String resentCc) {
			wrappedMessageHeaderValidator.validateWrappedResentCc(er, resentCc);
			
		}

		// DTS 112, Resent-bcc, Optional
		public void validateWrappedResentBcc(ErrorRecorder er, String resentBcc) {
			wrappedMessageHeaderValidator.validateWrappedResentBcc(er, resentBcc);
			
		}

		// DTS 113, Resent-Msg-Id, Conditional
		public void validateWrappedResentMsgId(ErrorRecorder er, String resentMsgId) {
			wrappedMessageHeaderValidator.validateWrappedResentMsgId(er, resentMsgId);
			
		}
		
		// DTS 114, Orig-Date, Required
		public void validateWrappedOrigDate(ErrorRecorder er, String origDate) {
			wrappedMessageHeaderValidator.validateWrappedOrigDate(er, origDate);
		}

		// DTS 115, From, Required
		public void validateWrappedFrom(ErrorRecorder er, String from) {
			wrappedMessageHeaderValidator.validateWrappedFrom(er, from);
			
		}
		
		// DTS 116, Sender, Conditional
		public void validateWrappedSender(ErrorRecorder er, String sender, Address[] from) {
			wrappedMessageHeaderValidator.validateWrappedSender(er, sender, from);
			
		}
		
		// DTS 117, Reply-To, Optional
		public void validateWrappedReplyTo(ErrorRecorder er, String replyTo) {
			wrappedMessageHeaderValidator.validateWrappedReplyTo(er, replyTo);
			
		}

		// DTS 118, To, Required
		public void validateWrappedTo(ErrorRecorder er, String to) {
			wrappedMessageHeaderValidator.validateWrappedTo(er, to);
			
		}
		
		// DTS 119, cc, Optional
		public void validateWrappedCc(ErrorRecorder er, String cc) {
			wrappedMessageHeaderValidator.validateWrappedCc(er, cc);
			
		}
		
		// DTS 120, Bcc, Optional
		public void validateWrappedBcc(ErrorRecorder er, String bcc) {
			wrappedMessageHeaderValidator.validateWrappedBcc(er, bcc);
			
		}

		// DTS 121, Message-Id, Required
		public void validateWrappedMessageId(ErrorRecorder er, String messageId) {
			wrappedMessageHeaderValidator.validateWrappedMessageId(er, messageId);
		}

		// DTS 122, In-reply-to, Optional
		public void validateWrappedInReplyTo(ErrorRecorder er, String inReplyTo, String date) {
			wrappedMessageHeaderValidator.validateWrappedInReplyTo(er, inReplyTo, date);
			
		}
		
		// DTS 123, References, Optional
		public void validateWrappedReferences(ErrorRecorder er, String references) {
			wrappedMessageHeaderValidator.validateWrappedReferences(er, references);
			
		}
		
		// DTS 124, Subject, Optional
		public void validateWrappedSubject(ErrorRecorder er, String subject, String filename) {
			wrappedMessageHeaderValidator.validateWrappedSubject(er, subject, filename);
			
		}
		
		// DTS 125, Comments, Optional
		public void validateWrappedComments(ErrorRecorder er, String comments) {
			wrappedMessageHeaderValidator.validateWrappedComments(er, comments);
			
		}
		
		// DTS 126, Keywords, Optional
		public void validateWrappedKeywords(ErrorRecorder er, String keyword) {
			wrappedMessageHeaderValidator.validateWrappedKeywords(er, keyword);
			
		}
		
		// DTS 127, Optional-field, Optional
		public void validateWrappedOptionalField(ErrorRecorder er, String optionalField) {
			wrappedMessageHeaderValidator.validateWrappedOptionalField(er, optionalField);
			
		}
		
		// DTS 128, Disposition-Notification-To, Optional
		public void validateWrappedDispositionNotificationTo(ErrorRecorder er, String dispositionNotificationTo) {
			wrappedMessageHeaderValidator.validateWrappedDispositionNotificationTo(er, dispositionNotificationTo);
			
		}
		
		// DTS 102b, MIME-Version, Required
		public void validateWrappedMIMEVersion(ErrorRecorder er, String MIMEVersion) {
			wrappedMessageHeaderValidator.validateWrappedMIMEVersion(er, MIMEVersion);
			
		}

	

}
