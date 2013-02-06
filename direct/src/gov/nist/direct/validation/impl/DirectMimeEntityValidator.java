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

import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Part;

import junit.framework.Assert;
import gov.nist.direct.utils.ValidationUtils;
import gov.nist.direct.validation.MimeEntityValidator;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

public class DirectMimeEntityValidator implements MimeEntityValidator {

	// ************************************************
	// *********** MIME Entity checks *****************
	// ************************************************
	
	// xxxxxxxxxxxxxx MIME Headers xxxxxxxxxxxxxxxxxxxx
	
	// DTS 190, All Mime Header Fields, Required
	public void validateAllMimeHeaderFields(ErrorRecorder er, String header) {
		if(header.contains("\"")) {
			String[] splitHeader = null;
			splitHeader = header.split("\"");
			if(splitHeader.length==1) {
				if(splitHeader[0].contains("(")) {
					er.err("190", "Mime Header Fields is valid: Content-Disposition contains comment", "", "", "DTS 190");
				} else {
					er.detail("Success:  DTS 190 - Mime Header Fields is valid");
				}
			} else if(splitHeader.length>2) {
				if(splitHeader[2].contains("(")) {
					er.err("190", "Mime Header Fields is valid: Content-Disposition contains comment", "", "", "DTS 190");
				} else {
					er.detail("Success:  DTS 190 - Mime Header Fields is valid");
				}
			}
		} else {
			er.detail("Success:  DTS 190 - Mime Header Fields is valid");	
		}
	}
	
	// DTS 102a, MIME-Version, Optinal
	public void validateAllMIMEVersion(ErrorRecorder er, String mimeVersion) {
		Assert.fail("Not Yet Implemented");
		
	}

	// DTS 133-145-146, Content-Type, Required
	public void validateContentType(ErrorRecorder er, String contentType) {
		final String xContentType =  "^X-.*";
		Pattern pattern = Pattern.compile(xContentType, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(contentType);
		if(matcher.matches()) {
			er.detail("     Info:  DTS 133-145-146 - Content-Type begin by X- and do not need to be verified");
		} else {
			if(contentType.contains("/")) {
				er.detail("     Success:  DTS 133-145-146 - Content-Type contains a subtype");
			} else {
				er.err("133-145-146", "Content-Type does not contain a subtype", "", "DTS 133-145-146", "");
			}
		}
		
	}

	// DTS 191, Content-Type Subtype, Required
	public void validateContentTypeSubtype(ErrorRecorder er, String subtype) {
		String[] typeAndSubtype = subtype.split("/"); // first one is the type (ex. "text"), second one is the subtype (ex. "plain").
		if (typeAndSubtype[1] != "") {
			er.detail("     Success:  DTS 191 - Content Type Subtype is present");
		} else {
			er.err("191", "Content Type SubType is not present", "", "DTS 191", "");
		}
	}

	// DTS 192, Content-Type name, Conditional
	public void validateContentTypeName(ErrorRecorder er, String contentTypeName) {
		if(contentTypeName == null) {
			er.err("192", "The conditional parameter 'name' is missing", "", "DTS 192", "");
		} else {
			er.detail("     Success:  DTS 192 - The conditional parameter 'name' is present");
		}
	}

	// DTS 193, Content-Type S/MIME-Type, Conditional
	public void validateContentTypeSMIMEType(ErrorRecorder er, String contentTypeSMIMEType) {
		if(contentTypeSMIMEType == null) {
			 er.err("193", "The parameter 'smime-type' is missing", "", "DTS 193", "");
		} else {
			er.detail("     Success:  DTS 193 - Info: the parameter 'smime-type' is present");
		}
	}

	// DTS 137-140, Content-Type Boundary, Conditional
	public void validateContentTypeBoundary(ErrorRecorder er, String contentTypeBoundary) {
		// MUST be encapsulated by "" if it contains a colon (:)
		if (contentTypeBoundary.contains(":")) {
			if (!(contentTypeBoundary.charAt(0) == '"') || !(contentTypeBoundary.charAt(contentTypeBoundary.length()-1) == '"')) {
				er.err("137-140", "Invalid format: the MIME boundary includes a colon (':') and should start with quotes ('\"').", "", "DTS 137-140", "");
			}
		}
		
		// MUST be no longer than 70 characters, not counting the two leading hyphens
		else if (contentTypeBoundary.length() > 70) {
			er.err("137-140", "Invalid format: the MIME boundary should not be longer than 70 characters.", "", "DTS 137-140", "");
		}
		
		// MUST be represented as US-ASCII
		else if (!ValidationUtils.isAscii(contentTypeBoundary)) {
			er.err("137-140", "The boundary MUST be represented as US-ASCII", "", "DTS 137-140", "");
		}
		
		else {
			er.detail("     Success:  DTS 137-140 - Boundary detected");
		}
		
	}

	// DTS 156, Content-type Disposition, Conditional
	public void validateContentTypeDisposition(ErrorRecorder er, String contentTypeDisposition, String contentType) {
		if (contentType.contains("application/pkcs7-mime")) {
			if (contentTypeDisposition == null) {
				er.err("156", "DTS 156 Content Type Disposition SHOULD be present", "", "DTS 156", "");
			} else if(!contentTypeDisposition.equals("")) {
				er.detail("     Success:  DTS 156 - Content Type Disposition is present");
			} else {
				er.err("156", "DTS 156 Content Type Disposition SHOULD be present", "", "DTS 156", "");
			}
		} else {
			er.detail("     Success:  DTS 156 - Content Type is not equal to application/pkcs7-mime");
		}
		
	}
	
	// DTS 161-194, Content-Disposition filename, Optional
	public void validateContentDispositionFilename(ErrorRecorder er, String content) {
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
					er.detail("     Success:  DTS 161-194 - Filename is smime, has the good extension and is less than 8 characters");
				} else {
					er.warning("161-194", "Content Type Disposition filename SHOULD be smime", "", "DTS 161-194");
				}
			} else {
				er.warning("161-194", "Content Type Disposition filename SHOULD be less than 8 characters", "", "DTS 161-194");				
			}
		} else if(content.equals("")) {
			er.warning("161-194", "Content Type Disposition filename SHOULD be present", "", "DTS 161-194");
		} else {
			er.warning("161-194", "Content Type Disposition filename SHOULD have an extension in .p7c, .p7z or .p7s", "", "DTS 161-194");
		}
		
	}
	
	// DTS 134-143, Content-Id, Optional
	public void validateContentId(ErrorRecorder er, String content) {
		if(content.equals("")) {
			er.detail("Info: DTS 134-143 - Content-Id field is not present");
		} else {
			//handle display		
			Pattern pattern = Pattern.compile("<" + "[0-9,a-z,_,\\-,.]+" + "@" + "[0-9,a-z,_,\\-,.]+" + ">", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content);
			//er.detail(matcher.matches());
			if(matcher.matches()) {
				er.detail("     Success:  DTS 134-143 - Content-Id is valid");
			} else {
				er.err("134-143", "Content-Id field is invalid.", "", "DTS 134-143", "");
			}
		}
		
	}
	
	// DTS 135-142-144, Content-Description, Optional
	public void validateContentDescription(ErrorRecorder er, String content) {
		if(content.equals("")) {
			er.detail("Info: DTS 135-142-144 - Content-Description field is not present");
		} else {
			er.detail("Success: DTS 135-142-144 - Content-Description field is valid");
		}
		
	}
	
	// DTS 136-148-157, Content-Transfer-Encoding, Optional
	public void validateContentTransferEncodingOptional(ErrorRecorder er, String contentTransfertEncoding, String contentType) {
		if(contentType.contains("multipart") || contentType.contains("message")) {
			if(contentTransfertEncoding.contains("7bit") || contentTransfertEncoding.contains("8bit") || contentTransfertEncoding.contains("binary")) {
				er.detail("Success: DTS 136-148-157 - Content-Transfer-Encoding is valid");
			} else {
				er.err("136-148-157", "Content-Transfer-Encoding is not valid", "", "DTS 136-148-157", "");
			}
		} else {
			if(contentTransfertEncoding.contains("quoted-printable") || contentTransfertEncoding.contains("base-64") || contentTransfertEncoding.contains("7-bit")) {
				er.detail("Success: DTS 136-148-157 - Content-Transfer-Encoding is valid");
			} else if(contentTransfertEncoding.startsWith("X-")) {
				er.detail("Info: DTS 136-148-157 - Content-Transfer-Encoding start with X- and do not need to be checked");
			} else {
				er.err("136-148-157", "Content-Transfer-Encoding is not valid", "", "DTS 136-148-157", "");
			}
		}
		
	}
	
	// DTS 138-149, Content-*, Optional
	public void validateContentAll(ErrorRecorder er, String content) {
		if(content.startsWith("content-")) {
			er.detail("Success: DTS 138-149 - Content-* is valid");
		} else {
			er.err("138-149", "Content-* is not valid", "", "DTS 138-149", "");
		}
		
	}
	
	// xxxxxxxxxxxxxxx MIME Body  xxxxxxxxxxxxxxx
	
	// DTS 195, Body, Required
	public void validateBody(ErrorRecorder er, Part p, String body) {
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
					if(body.contains("=")) {
						final String extension =  "(.*(=[0-9a-fA-f]\\r\\n)?)*";
						Pattern pattern = Pattern.compile(extension, Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(body);
						if(matcher.matches()) {
							er.detail("     Success:  DTS 195 - Body is valid");
						} else {
							er.err("195", "DTS 195 Body, \"=\" followed by a character that is neither a hexadecimal digit (including \"abcdef\") nor the CR character of a CRLF pair is illegal", "", "DTS 195", "");
						}
					} else {
						er.detail("     Success:  DTS 195 - Body is valid");
					}
				} else {
					er.err("195", "DTS 195 Body, Content-Transfer-Encoding = \"quoted-printable\", control characters other than TAB, or CR and LF as parts of CRLF pairs, MUST NOT appear", "", "DTS 195", "");
				}
			} else if(head.contains("base64")) {
				if(ValidationUtils.isOnlyCRLF(body)) {
					er.detail("     Success:  DTS 195 - Body is valid");
				} else {
					er.err("195", "DTS 195 Body, Any linebreak MUST be represented as a CRLF", "", "DTS 195", "");
				}
			} else {
				er.detail("     Success:  DTS 195 - Body is valid");
			}
		} else {
			er.err("195", "DTS 195 Body, Any linebreak MUST be represented as a CRLF", "", "DTS 195", "");
		}
		
	}
	
	

}
