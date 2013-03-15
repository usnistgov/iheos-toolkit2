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

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import junit.framework.Assert;
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
			er.success("196", "All Headers", "", "Must be ASCII encoding" , rfc, "Success");
		} else if(!isAscii && wrapped) {
			er.error("196", "All Headers", "Some headers are not ASCII encoded", "Must be ASCII encoding", rfc, "Error");
		} else {
			er.warning("196", "All headers", "Some headers are not ASCII encoded", "Must be ASCII encoding", rfc, "Warning");
		}
		
	}
	
	// DTS 103-105, Return Path, Conditional
	public void validateReturnPath(ErrorRecorder er, String returnPath, boolean wrapped) {
		String rfc = "RFC 5321: Section 4.4;http://tools.ietf.org/html/rfc5321.html#section-4.4;RFC 5322: Section 3.6.7;http://tools.ietf.org/html/rfc5322#section-3.6.7";
		if(returnPath.equals("")) {
			er.warning("103-105", "Return Path", "Not Present", "Should be present (addr-spec)", "RFC 5321: Section 4.4 - RFC 5322: Section 3.6.7", "Warning");
			return;
		}
		
		if(ValidationUtils.validateAddrSpec(returnPath)) {
			er.success("103-105", "Return Path", returnPath, "addr-spec", rfc, "Success");
		} else {
			er.error("103-105", "Return Path", returnPath, "addr-spec", rfc, "Error");
		}
		
	}
	
	// DTS 104-106, Received, Conditional
	public void validateReceived(ErrorRecorder er, String received, boolean wrapped) {
		String[] content_split = null;
		String content_split_right = "";
		String content_split_left = "";
		boolean checkFrom = false;
		boolean checkBy = false;
		boolean checkFor = false;
		boolean checkDate = false;
		
		final String from = "from[0-9a-zA-Z]+([_, \\., \\-]?[0-9a-zA-Z]+)*" + "\\(\\[" + "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}" + "\\]\\)";
		final String by = "by[0-9a-zA-Z]+([_, \\., \\-]?[0-9a-zA-Z]+)*(\\([0-9,a-z,A-Z,\\s]*\\))?with([A-Za-z])*(id|ID)[0-9a-zA-Z]+([_, \\., \\-]?[0-9a-zA-Z]+)*";
		final String fore =  "for" + "<" + "[0-9,a-z,_,\\-,.]+" + "@" + "[0-9,a-z,_,\\-,.]+" + ">;";
		
		final String datePattern = ValidationUtils.getDatePattern();
		
		// From field validation
		if(received.contains("from") && received.contains("by")) {
			content_split = received.split("by");
			content_split_left = content_split[0];
			content_split_right = "by" + content_split[1];
			Pattern pattern = Pattern.compile(from, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content_split_left);
			if(matcher.matches()) {
				checkFrom = true;
			}
		}
		
		// By field validation
		if(content_split_right.contains("for")) {
			content_split = content_split_right.split("for");
			content_split_left = content_split[0];
			content_split_right = "for" + content_split[1];
			Pattern pattern = Pattern.compile(by, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content_split_left);
			if(matcher.matches()) {
				checkBy = true;
			}
		}
		
		// For field validation
		if(content_split_right.contains(";")) {
			content_split = content_split_right.split(";");
			content_split_left = content_split[0] + ";";
			content_split_right = content_split[1];
			Pattern pattern = Pattern.compile(fore, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content_split_left);
			if(matcher.matches()) {
				checkFor = true;
			}
			
			// Date validation
			pattern = Pattern.compile(datePattern, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(content_split_right);
			if(matcher.matches()) {
				checkDate = true;
			}
		}
		
		String rfc = "RFC 5321: Section 4.4;http://tools.ietf.org/html/rfc5321.html#section-4.4;RFC 5322: Section 3.3;http://tools.ietf.org/html/rfc5322#section-3.3";
		if(checkFrom && checkBy && checkFor && checkDate) {
			er.success("104-106", "Received", received, "", rfc, "Success");
		} else {
			er.warning("104-106", "Received", received, "", rfc, "Error");
		}
		
	}

	// DTS 197, Resent Fields, Required
	public void validateResentFields(ErrorRecorder er, String[] resentField, boolean wrapped) {
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
				er.err("197", "Resent fields are not grouped together", "", "DTS 197", "");
				grouped = false;
				break;
			}
		}
		
		if(grouped && present) {
			er.detail("     Success:  DTS 197 - Resent field are grouped together");
		} else if(grouped && !present) {
			er.detail("     Info:  DTS 197 - Resent-fields are not used");
		}
		
	}
	
	// DTS 107, Resent-Date, Conditional
	public void validateResentDate(ErrorRecorder er, String resentDate, boolean wrapped) {
		if(ValidationUtils.validateDate(resentDate)) {
			er.detail("     Success:  DTS 107 - Resent-Date is valid");
		} else if (resentDate.equals("")) { 
			er.detail("     Info:  DTS 107 - Resent-Date is not present");
		} else{
			er.err("107", "Resent-Date is invalid.", "", "DTS 107", "");
		}

	}

	// DTS 108, Resent-From, Conditional
	public void validateResentFrom(ErrorRecorder er, String resentFrom, boolean wrapped) {
		if(ValidationUtils.validateEmail(resentFrom)) {
			er.detail("     Success:  DTS 108 - Resent-From field is valid");
		} else if (resentFrom.equals("")) { 
			er.detail("     Info:  DTS 108 - Resent-From is not present");
		} else {
			er.err("108", "Resent-From field is invalid.", "", "DTS 108", "");
		}
		
	}
	
	// DTS 109, Resent-Sender, Conditional
	public void validateResentSender(ErrorRecorder er, String resentSender, String resentFrom, boolean wrapped) {
		if(ValidationUtils.validateEmail(resentSender) && !resentSender.equals(resentFrom)) {
			er.detail("     Success:  DTS 109 - Resent-Sender field is valid");
		} else if (resentFrom.equals("")) { 
			er.detail("     Info:  DTS 109 - Resent-Sender is not present");
		} else if(resentSender.equals(resentFrom)) {
			er.err("109", "Resent-From field is equal to Resent-Sender field", "", "DTS 108", "");
		} else {
			er.err("109", "Resent-Sender field is invalid.", "", "DTS 108", "");
		}
		
	}

	// DTS 110, Resent-to, Optional
	public void validateResentTo(ErrorRecorder er, String resentTo, boolean wrapped) {
		if(ValidationUtils.validateEmail(resentTo)) {
			er.detail("     Success:  DTS 110 - Resent-To field is valid");
		} else if (resentTo.equals("")) { 
			er.detail("     Info:  DTS 110 - Resent-To is not present");
		} else {
			er.err("110", "Resent-To field is invalid.", "", "DTS 110", "");
		}
		
	}

	// DTS 111, Resent-cc, Optional
	public void validateResentCc(ErrorRecorder er, String resentCc, boolean wrapped) {
		if(ValidationUtils.validateEmail(resentCc)) {
			er.detail("     Success:  DTS 111 - Resent-Cc field is valid");
		} else if (resentCc.equals("")) { 
			er.detail("     Info:  DTS 111 - Resent-Cc is not present");
		} else {
			er.err("111", "Resent-Cc field is invalid.", "", "DTS 111", "");
		}
		
	}

	// DTS 112, Resent-bcc, Optional
	public void validateResentBcc(ErrorRecorder er, String resentBcc, boolean wrapped) {
		if(ValidationUtils.validateEmail(resentBcc)) {
			er.detail("     Success:  DTS 112 - Resent-Bcc field is valid");
		} else if (resentBcc.equals("")) { 
			er.detail("     Info:  DTS 112 - Resent-Bcc is not present");
		} else {
			er.err("112", "Resent-Bcc field is invalid.", "", "DTS 112", "");
		}
		
	}

	// DTS 113, Resent-Msg-Id, Conditional
	public void validateResentMsgId(ErrorRecorder er, String resentMsgId, boolean wrapped) {
		if(ValidationUtils.validateAddrSpec(resentMsgId)) {
			er.detail("     Success:  DTS 113 - Resent-Msg-Id field is valid");
		} else if (resentMsgId.equals("")) { 
			er.detail("     Info:  DTS 113 - Resent-Msg-Id is not present");
		} else {
			er.err("113", "Resent-Msg-Id field is invalid.", "", "DTS 113", "");
		}
		
	}
	
	// DTS 114, Orig-Date, Required
	public void validateOrigDate(ErrorRecorder er, String origDate, boolean wrapped) {
		if(origDate.equals("") && !wrapped) {
			er.detail("Info:  DTS - 114  - Wrapped Message: Date is not present on the outer (encrypted) message");
		} else if(origDate.equals("") && wrapped) {
			er.err("114", "Date is not present", "", "", "DTS 114");
		} else {
			if(ValidationUtils.validateDate(origDate)) {
				er.detail("     Success:  DTS 114 - Orig Date is valid");
			} else {
				er.err("114", "Orig Date is invalid.", "", "DTS 114", "");
			}
		}
	}

	// DTS 115, From, Required
	public void validateFrom(ErrorRecorder er, String from, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.2;http://tools.ietf.org/html/rfc5322#section-3.6.2";
		if(from.equals("") && !wrapped) {
			er.warning("115", "From", "", "mailbox-list", rfc, "Warning");
		} else {
			if (ValidationUtils.validateEmail(from)){
				er.success("115", "From", from, "mailbox-list", rfc, "Success");
			} else {
				er.error("115", "From", from, "mailbox-list", rfc, "Error");
			}
		}
		
	}
	
	// DTS 116, Sender, Conditional
	public void validateSender(ErrorRecorder er, String sender, Address[] from, boolean wrapped) {
		if(from.length>1) {
			if(ValidationUtils.validateEmail(sender)) {
				er.detail("     Success:  DTS 116 - Sender field is valid");
			} else {
				er.err("116", "Sender field is invalid.", "", "DTS 116", "");
			}
		} else {
			if(sender.equals("")) {
				er.detail("     Success:  DTS 116 - Sender field is not present because From field contains only one mailbox");
			} else {
				er.err("116", "Sender field shouldn't be present", "", "DTS 116", "");
			}
		}
		
	}
	
	// DTS 117, Reply-To, Optional
	public void validateReplyTo(ErrorRecorder er, String replyTo, boolean wrapped) {
		if(replyTo.equals("")) {
			er.warning("117", "Reply-To field is not present", "", "DTS 117");
		} else {
			if(ValidationUtils.validateEmail(replyTo)) {
				er.detail("     Success:  DTS 117 - Reply-To field is valid");
			} else if(replyTo.equals("")) {
				er.detail("     Info:  DTS 117 - Reply-To field is not present");
			} else {
				er.err("117", "Reply-To field is invalid.", "", "DTS 117", "");
			}
		}
		
	}

	// DTS 118, To, Required
	public void validateTo(ErrorRecorder er, String to, boolean wrapped) {
		if(to.equals("") && !wrapped) {
			er.warning("118", "To field is not present", "", "DTS 118");
		} else {			
			if(ValidationUtils.validateEmail(to)) {
				er.detail("     Success:  DTS 118 - To field is valid");
			} else {
				er.err("118", "To field is invalid.", "", "DTS 118", "");
			}
		}
		
	}
	
	// DTS 119, cc, Optional
	public void validateCc(ErrorRecorder er, String cc, boolean wrapped) {
		if(cc.equals("")) {
			er.detail("     Info:  DTS 119 - Cc is not present");
		} else {
			if(ValidationUtils.validateEmail(cc)) {
				er.detail("     Success:  DTS 119 - Cc field is valid");
			} else {
				er.err("119", "Cc field is invalid.", "", "DTS 119", "");
			}
		}

	}
	
	// DTS 120, Bcc, Optional
	public void validateBcc(ErrorRecorder er, String bcc, boolean wrapped) {
		if(bcc.equals("")) {
			er.detail("     Info:  DTS 120 - Bcc is not present");
		} else {
			if(ValidationUtils.validateEmail(bcc)) {
				er.detail("     Success:  DTS 120 - Bcc field is valid");
			} else {
				er.err("120", "Bcc field is invalid.", "", "DTS 120", "");
			}
		}
		
	}

	// DTS 121, Message-Id, Required
	public void validateMessageId(ErrorRecorder er, String messageId, boolean wrapped) {
		if(messageId.equals("") && !wrapped) {
			er.warning("121", "Message-Id field is not present", "", "DTS 121");
		} else {
			if(ValidationUtils.validateAddrSpec(messageId)) {
				er.detail("     Success:  DTS 121 - Message Id is valid");
			} else {
				er.err("121", "Message Id field is invalid.", "", "DTS 121", "");
			}
		}
	}

	// DTS 122, In-reply-to, Optional
	public void validateInReplyTo(ErrorRecorder er, String inReplyTo, String date, boolean wrapped) {
		// Check 1: Must be formatted as one or more <randomstringwithoutspaces@randomstringwithoutspaces>
		if(ValidationUtils.validateAddrSpec(inReplyTo)) {
			er.detail("     Success:  DTS 122 - In-Reply-To field is valid");
		} else if(inReplyTo.equals("")) {
			er.detail("     Info:  DTS 122 - In-Reply-To is not present");
		} else {
			er.err("122", "In-Reply-To field is invalid", "", "DTS 122", "");
		}
		
	}
	
	// DTS 123, References, Optional
	public void validateReferences(ErrorRecorder er, String references, boolean wrapped) {
		if(ValidationUtils.validateAddrSpec(references)) {
			er.detail("     Success:  DTS 123 - Reference field is valid");
		} else if(references.equals("")) {
			er.detail("     Info:  DTS 123 - Reference is not present");
		} else {
			er.err("123", "Reference field is invalid", "", "DTS 123", "");
		}
		
	}
	
	// DTS 124, Subject, Optional
	public void validateSubject(ErrorRecorder er, String subject, String filename, boolean wrapped) {
		if(filename.contains("zip")) {
			if(subject == null) {
				er.warning("124", "Subject field is not present", "", "DTS 124");
			} else if(subject.contains("XDM/1.0/DDM")) {
				er.detail("     Success:  DTS 124 - Subject field is valid");
			} else {
				er.err("124", "Subject field is invalid", "", "DTS 124", "");
			} 
		} else {
			if(subject != null) {
				er.detail("     Success:  DTS 124 - Subject field is valid");
			} else {
				er.warning("124", "Subject field is not present", "", "DTS 124");
			}
		}
		
	}
	
	// DTS 125, Comments, Optional
	public void validateComments(ErrorRecorder er, String comments, boolean wrapped) {
		if(comments.equals("")) {
			er.detail("     Info:  DTS 125 - Comments is not present");
		} else {
			er.detail("     Success:  DTS 125 - Comments field is valid");
		}
		
	}
	
	// DTS 126, Keywords, Optional
	public void validateKeywords(ErrorRecorder er, String keyword, boolean wrapped) {
		if(keyword.equals("")) {
			er.detail("     Info:  DTS 126 - Keywords is not present");
		} else {
			er.detail("     Success:  DTS 126 - Keywords field is valid");
		}
		
	}
	
	// DTS 127, Optional-field, Optional
	public void validateOptionalField(ErrorRecorder er, String optionalField, boolean wrapped) {
		if(optionalField.equals("")) {
			er.detail("     Info:  DTS 127 - Optional-field is not present");
		} else {
			er.detail("     Success:  DTS 127 - Optional-field field is valid"); 
		}
		
	}
	
	// DTS 128, Disposition-Notification-To, Optional
	public void validateDispositionNotificationTo(ErrorRecorder er, String dispositionNotificationTo, boolean wrapped) {
		if(dispositionNotificationTo.equals("")) {
			er.detail("     Info:  DTS 128 - Disposition-Notification-To field is not present");
		} else {
			if(ValidationUtils.validateEmail(dispositionNotificationTo)) {
				er.detail("     Success:  DTS 128 - Disposition-Notification-To field is valid");
			} else {
				er.err("128", "Disposition-Notification-To field is invalid.", "", "DTS 128", "");
			}
		}
		
	}
	
	// DTS 102b, MIME-Version, Required
	public void validateMIMEVersion(ErrorRecorder er, String MIMEVersion, boolean wrapped) {
		if(MIMEVersion.equals("") && !wrapped) {
			er.warning("102b", "MIME-Version field is not present", "", "DTS 102b");
		} else {
			final String mimeFormat = "[0-9]\\.[0-9].*";
			Pattern pattern = Pattern.compile(mimeFormat);
			Matcher matcher = pattern.matcher(MIMEVersion);
			if(matcher.matches()) {
				er.detail("     Success:  DTS 102b - MIME Version is valid");
			} else {
				er.err("102b", "MIME Version is invalid.", "", "DTS 102b", "");
			}
		}
		
	}
}
