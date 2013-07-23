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

import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Part;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import junit.framework.Assert;
import gov.nist.direct.directValidator.interfaces.MimeEntityValidator;
import gov.nist.direct.utils.ValidationUtils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

public class DirectMimeEntityValidator implements MimeEntityValidator {

	// ************************************************
	// *********** MIME Entity checks *****************
	// ************************************************
	
	// xxxxxxxxxxxxxx MIME Headers xxxxxxxxxxxxxxxxxxxx
	
	// DTS 190, All Mime Header Fields, Required
	public void validateAllMimeHeaderFields(ErrorRecorder er, String header) {
		String rfc = "RFC 2045: Section 1, 3, 5;http://tools.ietf.org/html/rfc2045;RFC 5322: Section 2.2, 3.2.2;http://tools.ietf.org/html/rfc5322";
		if(header.contains("\"")) {
			String[] splitHeader = null;
			splitHeader = header.split("\"");
			if(splitHeader.length==1) {
				if(splitHeader[0].contains("(")) {
					er.error("190", "All MIME Headers", header, "Content-Disposition must not contain comment", rfc);
				} else {
					er.success("190", "All MIME Headers", header, "Content-Disposition must not contain comment", rfc);
				}
			} else if(splitHeader.length>2) {
				if(splitHeader[2].contains("(")) {
					er.error("190", "All MIME Headers", header, "Content-Disposition must not contain comment", rfc);
				} else {
					er.success("190", "All MIME Headers", header, "Content-Disposition must not contain comment", rfc);
				}
			}
		} else {
			er.success("190", "All MIME Headers", header, "Content-Disposition must not contain comment", rfc);	
		}
	}
	
	// DTS 102a, MIME-Version, Optinal
	public void validateAllMIMEVersion(ErrorRecorder er, String mimeVersion) {
		Assert.fail("Not Yet Implemented");
		
	}

	// DTS 133-145-146, Content-Type, Required
	public void validateContentType(ErrorRecorder er, String contentType) {
		String rfc = "RFC 2045: Section 5, 5.2;http://tools.ietf.org/html/rfc2045#section-5;RFC 5751: Section 3.2, 3.2.1;http://tools.ietf.org/html/rfc5751#section-3.2";
		final String xContentType =  "^X-.*";
		Pattern pattern = Pattern.compile(xContentType, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(contentType);
		if(matcher.matches()) {
			er.success("133-145-146", "Content-Type", contentType, "Content-Type begin by X- and do not need to be verified", rfc);
		} else {
			if(contentType.contains("/")) {
				er.success("133-145-146", "Content-Type", contentType, "Content-Type must contain a subtype", rfc);
			} else {
				er.error("133-145-146", "Content-Type", contentType, "Content-Type must contain a subtype", rfc);
			}
		}
		
	}

	// DTS 191, Content-Type Subtype, Required
	public void validateContentTypeSubtype(ErrorRecorder er, String subtype) {
		String rfc = "RFC 2045: Section 1;http://tools.ietf.org/html/rfc2045#section-1";
		String[] typeAndSubtype = subtype.split("/"); // first one is the type (ex. "text"), second one is the subtype (ex. "plain").
		if (typeAndSubtype[1] != "") {
			er.success("191", "Content-Type Subtype", subtype, "Content Type Subtype must be present", rfc);
		} else {
			er.error("191", "Content-Type Subtype", "Not present", "Content Type Subtype must be present", rfc);
		}
	}

	// DTS 192, Content-Type name, Conditional
	public void validateContentTypeName(ErrorRecorder er, String contentTypeName) {
		String rfc = "RFC 5751: Section 3.2.1;http://tools.ietf.org/html/rfc5751#section-3.2.1";
		if(contentTypeName == null) {
			er.error("192", "Content-Type Name", contentTypeName, "Content Type Name must be present", rfc);
		} else {
			er.success("192", "Content-Type Name", contentTypeName, "Content Type Name must be present", rfc);
		}
	}

	// DTS 193, Content-Type S/MIME-Type, Conditional
	public void validateContentTypeSMIMEType(ErrorRecorder er, String contentTypeSMIMEType) {
		String rfc = "RFC 5751: Section 3.2.1;http://tools.ietf.org/html/rfc5751#section-3.2.1;RFC 5751: 3.2.2;http://tools.ietf.org/html/rfc5751#section-3.2.2";
		if(contentTypeSMIMEType == null) {
			er.error("192", "Content-Type S/MIME-Type", contentTypeSMIMEType, "Content Type S/MIME-Type must be present", rfc);
		} else {
			er.success("192", "Content-Type S/MIME-Type", contentTypeSMIMEType, "Content Type S/MIME-Type must be present", rfc);
		}
	}

	// DTS 137-140, Content-Type Boundary, Conditional
	public void validateContentTypeBoundary(ErrorRecorder er, String contentTypeBoundary) {
		String rfc = "RFC 2046: Section 5.1.1;http://tools.ietf.org/html/rfc2046#section-5.1.1;RFC 2045: Section 5;http://tools.ietf.org/html/rfc2045#section-5";
		// MUST be encapsulated by "" if it contains a colon (:)
		if (contentTypeBoundary.contains(":")) {
			if (!(contentTypeBoundary.charAt(0) == '"') || !(contentTypeBoundary.charAt(contentTypeBoundary.length()-1) == '"')) {
				er.error("137-140", "Content-Type Boundary", contentTypeBoundary, "MIME boundary must not include a colon (':') and should start with quotes ('\"')", rfc);
			}
		}
		
		// MUST be no longer than 70 characters, not counting the two leading hyphens
		else if (contentTypeBoundary.length() > 70) {
			er.error("137-140", "Content-Type Boundary", contentTypeBoundary, "MIME boundary should not be longer than 70 characters", rfc);
		}
		
		// MUST be represented as US-ASCII
		else if (!ValidationUtils.isAscii(contentTypeBoundary)) {
			er.error("137-140", "Content-Type Boundary", contentTypeBoundary, "MIME boundary must be represented as US-ASCII", rfc);
		}
		
		else {
			er.success("137-140", "Content-Type Boundary", contentTypeBoundary, "MIME Boundary is valid (US-ASCII, less than 70 characters)", rfc);
		}
		
	}

	// DTS 156, Content-type Disposition, Conditional
	public void validateContentTypeDisposition(ErrorRecorder er, String contentTypeDisposition, String contentType) {
		String rfc = "RFC 5751: Section 3.2.1;http://tools.ietf.org/html/rfc5751#section-3.2.1";
		if (contentType.contains("application/pkcs7-mime")) {
			if (contentTypeDisposition == null) {
				er.error("156", "Content-Type Disposition", "Not present", "Content-Type Disposition should be present", rfc);
			} else if(!contentTypeDisposition.equals("")) {
				er.success("156", "Content-Type Disposition", contentTypeDisposition, "Content-Type Disposition should be present", rfc);
			} else {
				er.error("156", "Content-Type Disposition", "Not present", "Content-Type Disposition should be present", rfc);
			}
		} else {
			er.success("156", "Content-Type Disposition", contentTypeDisposition, "Content Type is not equal to application/pkcs7-mime", rfc);
		}
		
	}
	
	// DTS 161-194, Content-Disposition filename, Optional
	public void validateContentDispositionFilename(ErrorRecorder er, String content) {
		String rfc = "RFC 5751: Section 3.2.1;http://tools.ietf.org/html/rfc5751#section-3.2.1";
		final String extension =  ".*\\.p7c$|.*\\.p7z$|.*\\.p7s$|.*\\.p7m$";
		Pattern pattern = Pattern.compile(extension, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		if(matcher.matches()) {
			String[] temp = content.split("\\.");
			String filename = temp[0];
			if(filename.length()<= 8) {
				final String smime =  "^smime\\..*" ;
				pattern = Pattern.compile(smime, Pattern.CASE_INSENSITIVE);
				matcher = pattern.matcher(content);
				if(matcher.matches()) {
					er.success("161-194", "Content-Disposition filename", content, "Filename is smime, has the good extension and is less than 8 characters", rfc);
				} else {
					er.warning("161-194", "Content-Disposition filename", content, "Content Type Disposition filename SHOULD be smime", rfc);
				}
			} else {
				er.warning("161-194", "Content-Disposition filename", content, "Content Type Disposition filename SHOULD be less than 8 characters", rfc);				
			}
		} else if(content.equals("")) {
			er.warning("161-194", "Content-Disposition filename", "Not present", "Content Type Disposition filename SHOULD be present", rfc);
		} else {
			er.warning("161-194", "Content-Disposition filename", content, "Content Type Disposition filename SHOULD have an extension in .p7c, .p7z or .p7s", rfc);
		}
		
	}
	
	// DTS 134-143, Content-Id, Optional
	public void validateContentId(ErrorRecorder er, String content) {
		String rfc = "RFC 2045: Section 4;http://tools.ietf.org/html/rfc2045#section-4;RFC 2045: Section 7;http://tools.ietf.org/html/rfc2045#section-7";
		if(content.equals("")) {
			er.info("134-143", "Content-Id", "Not present", "Content-Id should be present", rfc);
		} else {
			//handle display		
			Pattern pattern = Pattern.compile("<" + "[0-9,a-z,_,\\-,.]+" + "@" + "[0-9,a-z,_,\\-,.]+" + ">", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content);
			//er.detail(matcher.matches());
			if(matcher.matches()) {
				er.success("134-143", "Content-Id", content, "Must be syntactically identical to the Message-ID", rfc);
			} else {
				er.error("134-143", "Content-Id", content, "Must be syntactically identical to the Message-ID", rfc);
			}
		}
		
	}
	
	// DTS 135-142-144, Content-Description, Optional
	public void validateContentDescription(ErrorRecorder er, String content) {
		String rfc = "RFC 2045: Section 4;http://tools.ietf.org/html/rfc2045#section-4;RFC 2045: Section 8;http://tools.ietf.org/html/rfc2045#section-8";
		if(content.equals("")) {
			er.info("135-142-144", "Content-Description", "Not present", "", rfc);
		} else {
			er.success("135-142-144", "Content-Description", content, "No check needed", rfc);
		}
		
	}
	
	// DTS 136-148-157, Content-Transfer-Encoding, Optional
	public void validateContentTransferEncodingOptional(ErrorRecorder er, String contentTransfertEncoding, String contentType) {
		String rfc = "RFC 2045: Section 6, 6.1, 6.4, 6.7, 6.8;http://tools.ietf.org/html/rfc2045#section-6;RFC 5751: Section 3.1.2, 3.1.3;http://tools.ietf.org/html/rfc5751#section-3.2.1";
		if(contentType.contains("multipart") || contentType.contains("message")) {
			if(contentTransfertEncoding.contains("7bit") || contentTransfertEncoding.contains("8bit") || contentTransfertEncoding.contains("binary")) {
				er.success("136-148-157", "Content-Transfer-Encoding", contentTransfertEncoding, "Content-Transfer-Encoding must be either 7bit, 8bit or binary", rfc);
			} else {
				er.error("136-148-157", "Content-Transfer-Encoding", contentTransfertEncoding, "Content-Transfer-Encoding must be either 7bit, 8bit or binary", rfc);
			}
		} else {
			if(contentTransfertEncoding.contains("quoted-printable") || contentTransfertEncoding.contains("base-64") || contentTransfertEncoding.contains("7-bit")) {
				er.success("136-148-157", "Content-Transfer-Encoding", contentTransfertEncoding, "Content-Transfer-Encoding must be either quoted-printable, base64 or 7-bit", rfc);
			} else if(contentTransfertEncoding.startsWith("X-")) {
				er.success("136-148-157", "Content-Transfer-Encoding", contentTransfertEncoding, "Content-Transfer-Encoding start with X- and do not need to be checked", rfc);
			} else {
				er.error("136-148-157", "Content-Transfer-Encoding", contentTransfertEncoding, "Content-Transfer-Encoding must be either quoted-printable, base64 or 7-bit", rfc);
			}
		}
		
	}
	
	// DTS 138-149, Content-*, Optional
	public void validateContentAll(ErrorRecorder er, String content) {
		String rfc = "RFC 2045: Section 9;http://tools.ietf.org/html/rfc2045#section-9";
		if(content.startsWith("content-")) {
			er.success("138-149", "Content-*", content, "Should begin by content-*", rfc);
		} else {
			er.error("138-149", "Content-*", content, "Should begin by content-*", rfc);
		}
		
	}
	
	// xxxxxxxxxxxxxxx MIME Body  xxxxxxxxxxxxxxx
	
	// DTS 195, Body, Required
	public void validateBody(ErrorRecorder er, Part p, String body) {
		String rfc = "RFC 2046: Section 5.1.1;http://tools.ietf.org/html/rfc2046#section-5.1.1";
		String bodyTxt = SafeHtmlUtils.htmlEscape(body);
		if(body.length()>50) {
			bodyTxt = SafeHtmlUtils.htmlEscape(body.substring(0, 50) + "...");
		}
		
		if(ValidationUtils.isAscii(body) && ValidationUtils.isOnlyCRLF(body)) {
			String[] tab = {"Content-Transfer-Encoding"};
			String head = "";
			Enumeration e = null;
			try {
				e = p.getMatchingHeaders(tab);
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			while (e.hasMoreElements()) {
				Header hed = (Header)e.nextElement();
				head = hed.getValue();
			}
			
			// Check if Content-Transfer-Encoding="quoted-printable"
			if(head.contains("quoted-printable")) {
				// Check only CRLF and TAB control char
				if(ValidationUtils.controlCharAreOnlyCRLFAndTAB(body)) {
					/*if(body.contains("=")) {
						final String extension =  "(.*(=[0-9a-fA-f]\\r\\n)?)*";
						Pattern pattern = Pattern.compile(extension, Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(body);
						if(matcher.matches()) {*/
							er.success("195", "Body", bodyTxt, "Body does not contain illegal character", rfc);
						/*} else {
						}
					} else {
					}*/
				} else {
					er.error("195", "Body", bodyTxt, "Content-Transfer-Encoding = \"quoted-printable\", control characters other than TAB, or CR and LF as parts of CRLF pairs, MUST NOT appear", rfc);
				}
			} else if(head.contains("base64")) {
				if(ValidationUtils.isOnlyCRLF(body)) {
					er.success("195", "Body", bodyTxt, "Any linebreak must be represented as a CRLF", rfc);
				} else {
					er.error("195", "Body", bodyTxt, "Any linebreak must be represented as a CRLF", rfc);
				}
			} else {
				er.success("195", "Body", bodyTxt, "Any linebreak must be represented as a CRLF", rfc);
			}
		} else {
			er.error("195", "Body", bodyTxt, "Any linebreak must be represented as a CRLF", rfc);
		}
		
	}
	
	

}
