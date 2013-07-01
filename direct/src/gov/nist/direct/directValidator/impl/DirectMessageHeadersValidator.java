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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import gov.nist.direct.directValidator.interfaces.MessageHeadersValidator;
import gov.nist.direct.utils.ValidationUtils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

public class DirectMessageHeadersValidator implements MessageHeadersValidator {

	// ************************************************
	// *********** Message headers checks *************
	// ************************************************
	

	// DTS 196, All Headers, Required
	public void validateAllHeaders(ErrorRecorder er, String[] header, String[] headerContent, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6;http://tools.ietf.org/html/rfc5322#section-3.6;RFC 5321: Section 2.3.1;http://tools.ietf.org/html/rfc5321.html#section-2.3.1";
		boolean isAscii = true;
		for(int i=0;i<header.length;i++) {
			if(!ValidationUtils.isAscii(header[i]) || !ValidationUtils.isAscii(headerContent[i])) {
				isAscii = false;
			}
		}
		if(isAscii) {
			er.success("196", "All Headers", "", "Must be ASCII encoding" , rfc);
		} else if(!isAscii && wrapped) {
			er.error("196", "All Headers", "Some headers are not ASCII encoded", "Must be ASCII encoding", rfc);
		} else {
			er.warning("196", "All headers", "Some headers are not ASCII encoded", "Must be ASCII encoding", rfc);
		}
		
	}
	
	// DTS 103-105, Return Path, Conditional
	public void validateReturnPath(ErrorRecorder er, String returnPath, boolean wrapped) {
		String rfc = "RFC 5321: Section 4.4;http://tools.ietf.org/html/rfc5321.html#section-4.4;RFC 5322: Section 3.6.7;http://tools.ietf.org/html/rfc5322#section-3.6.7";
		String txtReturnPath = SafeHtmlUtils.htmlEscape(returnPath);
		if(returnPath.equals("")) {
			er.warning("103-105", "Return Path", "Not present", "Should be present (addr-spec)", "RFC 5321: Section 4.4 - RFC 5322: Section 3.6.7");
			return;
		}
		
		if(ValidationUtils.validateAddrSpec(returnPath)) {
			er.success("103-105", "Return Path", txtReturnPath, "addr-spec", rfc);
		} else {
			er.error("103-105", "Return Path", txtReturnPath, "addr-spec", rfc);
		}
		
	}
	
	// DTS 104-106, Received, Conditional
	public void validateReceived(ErrorRecorder er, String received, boolean wrapped) {
	
		// String part variable
		String fromText = ValidationUtils.getReceivedPart(received, "from ");
		String byText = ValidationUtils.getReceivedPart(received, " by ");
		String viaText = ValidationUtils.getReceivedPart(received, " via ");
		String withText = ValidationUtils.getReceivedPart(received, " with ");
		String idText = ValidationUtils.getReceivedPart(received, " id ");
		String forText = ValidationUtils.getReceivedPart(received, " for ");
		String dateText = "";
		
		if(received.contains(";")) {
			dateText = received.split(";", 2)[1];
			dateText = dateText.replaceAll("\\r", "");
			dateText = dateText.replaceAll("\\n", "");
			while(dateText.startsWith(" ")) {
				dateText = dateText.substring(1);
			}
		}
		
		// Boolean validation
		boolean checkFrom = false;
		boolean checkBy = false;
		boolean checkVia = true;
		boolean checkWith = true;
		boolean checkId = true;
		boolean checkFor = true;
		boolean checkDate = false;
		
		final String from = "[0-9a-zA-Z]+([_, \\., \\-][0-9a-zA-Z]+)*" + "\\(\\[" + "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}" + "\\]\\)";
		final String by = "[0-9a-zA-Z]+([_, \\., \\-][0-9a-zA-Z]+)*(\\([0-9,a-z,A-Z,\\s]*\\))?";
		final String via = "[0-9a-zA-Z]*";
		final String with = "[a-zA-Z0-9]*";
		final String id = "[0-9a-zA-Z]+([_, \\., \\-][0-9a-zA-Z]+)*";
		final String fore =  "<" + "[0-9,a-z,_,\\-,.]+" + "@" + "[0-9,a-z,_,\\-,.]+" + ">;";
		
		final String datePattern = ValidationUtils.getDatePattern();
		
		// From clause validation
		checkFrom = ValidationUtils.validateReceivedPart(fromText, from, checkFrom);
		
		// By clause validation
		checkBy = ValidationUtils.validateReceivedPart(byText, by, checkBy);
		
		// Via clause validation
		checkVia = ValidationUtils.validateReceivedPart(viaText, via, checkVia);

		// With clause validation
		checkWith = ValidationUtils.validateReceivedPart(withText, with, checkWith);

		// Id clause validation
		checkId = ValidationUtils.validateReceivedPart(idText, id, checkId);

		// For field validation
		checkFor = ValidationUtils.validateReceivedPart(forText, fore, checkFor);

		// Date validation
		Pattern pattern = Pattern.compile(datePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(dateText);
		if(matcher.matches()) {
			checkDate = true;
		}

		String rfc = "RFC 5321: Section 4.4;http://tools.ietf.org/html/rfc5321.html#section-4.4;RFC 5322: Section 3.3;http://tools.ietf.org/html/rfc5322#section-3.3";
		if(checkFrom && checkBy && checkVia && checkWith && checkId && checkFor && checkDate) {
			er.success("104-106", "Received", received, "from clause by clause for clause; date", rfc);
		} else {
			er.warning("104-106", "Received", received, "from clause by clause for clause; date", rfc);
		}
		
	}

	// DTS 197, Resent Fields, Required
	public void validateResentFields(ErrorRecorder er, String[] resentField, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		int i = 0;
		boolean present = false;
		while(i<resentField.length && !resentField[i].contains("resent")) {
			i++;
		}
		while(i<resentField.length && resentField[i].contains("resent")) {
			i++;
			present = true;
		}
		boolean grouped = true;
		for(int k=i;k<resentField.length;k++) {
			if(resentField[k].contains("resent")) {
				er.error("197", "Resent fields", "", "Should be grouped together", rfc);
				grouped = false;
				break;
			}
		}
		
		if(grouped && present) {
			er.success("197", "Resent fields", "Grouped and present", "Should be grouped together", rfc);
		} else if(grouped && !present) {
			er.info("197", "Resent fields", "Not present", "Should be grouped together", rfc);
		}
		
	}
	
	// DTS 107, Resent-Date, Conditional
	public void validateResentDate(ErrorRecorder er, String resentDate, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		if(ValidationUtils.validateDate(resentDate)) {
			er.success("107", "Resent-Date", resentDate, "date-time", rfc);
		} else if (resentDate.equals("")) { 
			er.info("107", "Resent-Date", "Not present", "date-time", rfc);
		} else{
			er.error("107", "Resent-Date", resentDate, "date-time", rfc);
		}

	}

	// DTS 108, Resent-From, Conditional
	public void validateResentFrom(ErrorRecorder er, String resentFrom, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		if(ValidationUtils.validateEmail(resentFrom)) {
			er.success("108", "Resent-From", resentFrom, "mailbox-list", rfc);
		} else if (resentFrom.equals("")) { 
			er.info("108", "Resent-From", "Not present", "mailbox-list", rfc);
		} else {
			er.error("108", "Resent-From", resentFrom, "mailbox-list", rfc);
		}
		
	}
	
	// DTS 109, Resent-Sender, Conditional
	public void validateResentSender(ErrorRecorder er, String resentSender, String resentFrom, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		if(ValidationUtils.validateEmail(resentSender) && !resentSender.equals(resentFrom)) {
			er.success("109", "Resent-Sender", resentSender, "mailbox-list", rfc);
		} else if (resentFrom.equals("")) { 
			er.info("109", "Resent-Sender", "Not present", "mailbox-list", rfc);
		} else if(resentSender.equals(resentFrom)) {
			er.error("109", "Resent-Sender", resentSender, "Resent-Sender should not be equal to Resent-From", rfc);
		} else {
			er.error("109", "Resent-Sender", resentSender, "mailbox-list", rfc);
		}
		
	}

	// DTS 110, Resent-to, Optional
	public void validateResentTo(ErrorRecorder er, String resentTo, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		if(ValidationUtils.validateEmail(resentTo)) {
			er.success("110", "Resent-To", resentTo, "address-list", rfc);
		} else if (resentTo.equals("")) { 
			er.info("110", "Resent-To", "Not present", "address-list", rfc);
		} else {
			er.error("110", "Resent-To", resentTo, "address-list", rfc);
		}
		
	}

	// DTS 111, Resent-cc, Optional
	public void validateResentCc(ErrorRecorder er, String resentCc, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		if(ValidationUtils.validateEmail(resentCc)) {
			er.success("111", "Resent-Cc", resentCc, "address-list", rfc);
		} else if (resentCc.equals("")) { 
			er.info("111", "Resent-Cc", "Not present", "address-list", rfc);
		} else {
			er.error("111", "Resent-Cc", resentCc, "address-list", rfc);
		}
		
	}

	// DTS 112, Resent-bcc, Optional
	public void validateResentBcc(ErrorRecorder er, String resentBcc, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		if(ValidationUtils.validateEmail(resentBcc)) {
			er.success("112", "Resent-Bcc", resentBcc, "address-list", rfc);
		} else if (resentBcc.equals("")) { 
			er.info("112", "Resent-Bcc", "Not present", "address-list", rfc);
		} else {
			er.error("112", "Resent-Bcc", resentBcc, "address-list", rfc);
		}
		
	}

	// DTS 113, Resent-Msg-Id, Conditional
	public void validateResentMsgId(ErrorRecorder er, String resentMsgId, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		if(ValidationUtils.validateAddrSpec(resentMsgId)) {
			er.success("113", "Resent-Msg-Id", SafeHtmlUtils.htmlEscape(resentMsgId), "msg-id", rfc);
		} else if (resentMsgId.equals("")) { 
			er.info("113", "Resent-Msg-Id", "Not present", "msg-id", rfc);
		} else {
			er.error("113", "Resent-Msg-Id", SafeHtmlUtils.htmlEscape(resentMsgId), "msg-id", rfc);
		}
		
	}
	
	// DTS 114, Orig-Date, Required
	public void validateOrigDate(ErrorRecorder er, String origDate, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.1;http://tools.ietf.org/html/rfc5322#section-3.6.1";
		if(origDate.equals("") && !wrapped) {
			er.info("114", "Orig-Date", "Not present", "Wrapped Message: Orig-Date is not present on the outer (encrypted) message", rfc);
		} else if(origDate.equals("") && wrapped) {
			er.error("114", "Orig-Date", "Not present", "Unwrapped Message: Orig-Date should be present", rfc);
		} else {
			if(ValidationUtils.validateDate(origDate)) {
				er.success("114", "Orig-Date", origDate, "[ day-of-week \",\" ] date time", rfc);
			} else {
				er.error("114", "Orig-Date", origDate, "[ day-of-week \",\" ] date time", rfc);
			}
		}
	}

	// DTS 115, From, Required
	public void validateFrom(ErrorRecorder er, String from, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.2;http://tools.ietf.org/html/rfc5322#section-3.6.2";
		if(from.equals("") && !wrapped) {
			er.info("115", "From", "Not present", "Wrapped Message: From is not present on the outer (encrypted) message", rfc);
		}  else if(from.equals("") && wrapped) {
			er.error("115", "From", "Not present", "Unwrapped Message: From should be present", rfc);
		} else {
			if (ValidationUtils.validateEmail(from)){
				er.success("115", "From", SafeHtmlUtils.htmlEscape(from), "mailbox-list", rfc);
			} else {
				er.error("115", "From", SafeHtmlUtils.htmlEscape(from), "mailbox-list", rfc);
			}
		}
		
	}
	
	// DTS 116, Sender, Conditional
	public void validateSender(ErrorRecorder er, String sender, Address[] from, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.2;http://tools.ietf.org/html/rfc5322#section-3.6.2";
		if(from.length>1) {
			if(ValidationUtils.validateEmail(sender)) {
				er.success("116", "Sender", sender, "mailbox", rfc);
			} else {
				er.error("116", "Sender", sender, "mailbox", rfc);
			}
		} else {
			if(sender.equals("")) {
				er.success("116", "Sender", "Not present", "Sender field not used if from address contains only one mailbox", rfc);
			} else {
				er.error("116", "Sender", sender, "Sender field should not be present, from address contains only one mailbox", rfc);
			}
		}
		
	}
	
	// DTS 117, Reply-To, Optional
	public void validateReplyTo(ErrorRecorder er, String replyTo, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.2;http://tools.ietf.org/html/rfc5322#section-3.6.2";
		if(replyTo.equals("") && !wrapped) {
			er.info("117", "Reply-To", "Not present", "Wrapped Message: Reply-To is not present on the outer (encrypted) message", rfc);
		} else if(replyTo.equals("") && wrapped) {
			er.warning("117", "Reply-To", "Not present", "Unwrapped Message: Reply-To should be present", rfc);
		} else {
			if(ValidationUtils.validateEmail(replyTo)) {
				er.success("117", "Reply-To", SafeHtmlUtils.htmlEscape(replyTo), "address-list", rfc);
			} else {
				er.error("117", "Reply-To", SafeHtmlUtils.htmlEscape(replyTo), "address-list", rfc);
			}
		}
		
	}

	// DTS 118, To, Required
	public void validateTo(ErrorRecorder er, String to, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.3;http://tools.ietf.org/html/rfc5322#section-3.6.3";
		if(to.equals("") && !wrapped) {
			er.info("118", "To", "Not present", "Wrapped Message: To is not present on the outer (encrypted) message", rfc);
		} else if(to.equals("") && wrapped) {
			er.error("118", "To", "Not present", "Unwrapped Message: To must be present", rfc);
		} else {			
			if(ValidationUtils.validateEmail(to)) {
				er.success("118", "To", SafeHtmlUtils.htmlEscape(to), "mailbox-list", rfc);
			} else {
				er.error("118", "To", SafeHtmlUtils.htmlEscape(to), "mailbox-list", rfc);
			}
		}
		
	}
	
	// DTS 119, cc, Optional
	public void validateCc(ErrorRecorder er, String cc, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.3;http://tools.ietf.org/html/rfc5322#section-3.6.3";
		if(cc.equals("")) {
			er.info("119", "Cc", "Not present", "address-list", rfc);
		} else {
			if(ValidationUtils.validateEmail(cc)) {
				er.success("119", "Cc", SafeHtmlUtils.htmlEscape(cc), "address-list", rfc);
			} else {
				er.error("119", "Cc", SafeHtmlUtils.htmlEscape(cc), "address-list", rfc);
			}
		}

	}
	
	// DTS 120, Bcc, Optional
	public void validateBcc(ErrorRecorder er, String bcc, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.3;http://tools.ietf.org/html/rfc5322#section-3.6.3";
		if(bcc.equals("")) {
			er.success("120", "Bcc", "Not present", "Should not be present", rfc);
		} else {
			if(ValidationUtils.validateEmail(bcc)) {
				er.success("120", "Bcc", SafeHtmlUtils.htmlEscape(bcc), "address-list", rfc);
			} else {
				er.error("120", "Bcc", SafeHtmlUtils.htmlEscape(bcc), "address-list", rfc);
			}
		}
		
	}

	// DTS 121, Message-Id, Required
	public void validateMessageId(ErrorRecorder er, String messageId, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.4;http://tools.ietf.org/html/rfc5322#section-3.6.4";
		if(messageId.equals("") && !wrapped) {
			er.warning("121", "Message-Id", "Not present", "Wrapped Message: Message-Id is not present on the outer (encrypted) message", rfc);
		} else if(messageId.equals("") && wrapped) {
			er.error("121", "Message-Id", "Not present", "Unwrapped Message: Message-Id must be present", rfc);
		} else {
			if(ValidationUtils.validateAddrSpec(messageId)) {
				er.success("121", "Message-Id", SafeHtmlUtils.htmlEscape(messageId), "<string with no spaces\"@\"string with no spaces>", rfc);
			} else {
				er.error("121", "Message-Id", SafeHtmlUtils.htmlEscape(messageId), "<string with no spaces\"@\"string with no spaces>", rfc);
			}
		}
	}

	// DTS 122, In-reply-to, Optional
	public void validateInReplyTo(ErrorRecorder er, String inReplyTo, String date, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.4;http://tools.ietf.org/html/rfc5322#section-3.6.4";
		// Check 1: Must be formatted as one or more <randomstringwithoutspaces@randomstringwithoutspaces>
		if(ValidationUtils.validateAddrSpec(inReplyTo)) {
			er.success("122", "In-reply-to", SafeHtmlUtils.htmlEscape(inReplyTo), "<string with no spaces\"@\"string with no spaces>", rfc);
		} else if(inReplyTo.equals("")) {
			er.info("122", "In-reply-to", "Not present", "<string with no spaces\"@\"string with no spaces>", rfc);
		} else {
			er.error("122", "In-reply-to", SafeHtmlUtils.htmlEscape(inReplyTo), "<string with no spaces\"@\"string with no spaces>", rfc);
		}
		
	}
	
	// DTS 123, References, Optional
	public void validateReferences(ErrorRecorder er, String references, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.4;http://tools.ietf.org/html/rfc5322#section-3.6.4";
		if(ValidationUtils.validateAddrSpec(references)) {
			er.success("123", "References", references, "<string with no spaces\"@\"string with no spaces>", rfc);
		} else if(references.equals("")) {
			er.info("123", "References", "Not present", "<string with no spaces\"@\"string with no spaces>", rfc);
		} else {
			er.error("123", "References", references, "<string with no spaces\"@\"string with no spaces>", rfc);
		}
		
	}
	
	// DTS 124, Subject, Optional
	public void validateSubject(ErrorRecorder er, String subject, String filename, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.5;http://tools.ietf.org/html/rfc5322#section-3.6.5";
		if(subject == null && !wrapped) {
			er.warning("124", "Subject", "Not present", "Wrapped Message: Subject is not present on the outer (encrypted) message", rfc);
		} else if(subject == null && !wrapped) {
			er.error("124", "Subject", "Not present", "Unwrapped Message: Subject must be present", rfc);
		}
		
		if(filename.contains("zip")) {	
			if(subject.contains("XDM/1.0/DDM")) {
				er.success("124", "Subject", subject, "Filename is ZIP: Subject must contain XDM/1.0/DDM", rfc);
			} else {
				er.err("124", "Subject", subject, "Filename is ZIP: Subject must contain XDM/1.0/DDM", rfc);
			}
		}
		
	}
	
	// DTS 125, Comments, Optional
	public void validateComments(ErrorRecorder er, String comments, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.5;http://tools.ietf.org/html/rfc5322#section-3.6.5";
		if(comments.equals("")) {
			er.info("125", "Comments", "Not present", "May not be present", rfc);
		} else {
			er.success("125", "Comments", comments, "Unstructured CRLF", rfc);
		}
		
	}
	
	// DTS 126, Keywords, Optional
	public void validateKeywords(ErrorRecorder er, String keyword, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.5;http://tools.ietf.org/html/rfc5322#section-3.6.5";
		if(keyword.equals("")) {
			er.info("126", "Keywords", "Not present", "May not be present", rfc);
		} else {
			er.success("126", "Keywords", keyword, "Unstructured CRLF", rfc);
		}
		
	}
	
	// DTS 127, Optional-field, Optional
	public void validateOptionalField(ErrorRecorder er, String optionalField, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.8;http://tools.ietf.org/html/rfc5322#section-3.6.8";
		if(optionalField.equals("")) {
			er.info("127", "Optional-field", "Not present", "May not be present", rfc);
		} else {
			er.success("127", "Optional-field", optionalField, "text", rfc);
		}
		
	}
	
	// DTS 128, Disposition-Notification-To, Optional
	public void validateDispositionNotificationTo(ErrorRecorder er, String dispositionNotificationTo, boolean wrapped) {
		String rfc = "IHE Vol2b: Section 3.32.4.1.3";
		if(dispositionNotificationTo.equals("")) {
			er.info("128", "Disposition-Notification-To", "Not present", "May not be present", rfc);
		} else {
			if(ValidationUtils.validateEmail(dispositionNotificationTo)) {
				er.success("128", "Disposition-Notification-To", dispositionNotificationTo, "Email address", rfc);
			} else {
				er.error("128", "Disposition-Notification-To", dispositionNotificationTo, "Email address", rfc);
			}
		}
		
	}
	
	// DTS 102b, MIME-Version, Required
	public void validateMIMEVersion(ErrorRecorder er, String MIMEVersion, boolean wrapped) {
		String rfc = "RFC 2045: Section 4;http://tools.ietf.org/html/rfc2045#section-4";
		if(MIMEVersion.equals("") && !wrapped) {
			er.warning("102b", "MIME-Version", "Not present", "Wrapped Message: MIME-Version is not present on the outer (encrypted) message", rfc);
		} else if(MIMEVersion.equals("") && wrapped) {
			er.error("102b", "MIME-Version", "Not present", "Unwrapped Message: MIME-Version must be present", rfc);
		} else {
			final String mimeFormat = "[0-9]\\.[0-9].*";
			Pattern pattern = Pattern.compile(mimeFormat);
			Matcher matcher = pattern.matcher(MIMEVersion);
			if(matcher.matches()) {
				er.success("102b", "MIME-Version", MIMEVersion, "1*DIGIT \".\" 1*DIGIT", rfc);
			} else {
				er.err("102b", "MIME-Version", MIMEVersion, "1*DIGIT \".\" 1*DIGIT", rfc);
			}
		}
		
	}
}
