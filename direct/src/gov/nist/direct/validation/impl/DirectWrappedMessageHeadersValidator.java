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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;

import gov.nist.direct.utils.ValidationUtils;
import gov.nist.direct.validation.MessageHeadersValidator;
import gov.nist.direct.validation.WrappedMessageHeadersValidator;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

public class DirectWrappedMessageHeadersValidator implements WrappedMessageHeadersValidator {

	// ************************************************
	// *********** Message headers checks *************
	// ************************************************
	

	// DTS 196, All Headers, Required
	public void validateWrappedAllHeaders(ErrorRecorder er, String[] header, String[] headerContent) {
		boolean isAscii = true;
		for(int i=0;i<header.length;i++) {
			if(!ValidationUtils.isAscii(header[i]) || !ValidationUtils.isAscii(headerContent[i])) {
				isAscii = false;
			}
		}
		if(isAscii) {
			er.detail("     Success:  DTS 196 - All headers are valid");
		} else {
			er.err("196", "All headers check is invalid.", "", "DTS 196", "");
		}
		
	}
	
	// DTS 103-105, Return Path, Conditional
	public void validateWrappedReturnPath(ErrorRecorder er, String returnPath) {
		if(returnPath.equals("")) {
			er.warning("103-105", "DTS 103-105 - Return Path field is not present", "", "");
			return;
		}
		
		Pattern pattern = Pattern.compile("<" + "[0-9,a-z,_,\\-,.]+" + "@" + "[0-9,a-z,_,\\-,.]+" + ">", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(returnPath);
		if(matcher.matches()) {
			er.detail("     Success:  DTS 103-105 - Return Path field is valid");
		} else {
			er.err("103-105", "Return Path field is invalid.", "", "DTS 103-105", "");
		}
		
	}
	
	// DTS 104-106, Received, Conditional
	public void validateWrappedReceived(ErrorRecorder er, String received) {
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
		
		final String dayOfWeek = "(Mon|Tue|Wed|Thu|Fri|Sat|Sun)";
		final String time = "([01]?[0-9]|2[0-3])(:[0-5][0-9]){1,2}";
		final String timezone = "[-+]((0[0-9]|1[0-3])([03]0|45)|1400)";
		//final String whitespace = "\\s";
		final String date = "((31(Jan|Mar|May|Jul|Aug|Oct|Dec))" +    			// 31th of each month
		"|(30(Jan|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec))" +      		// 30th of each month except Feb
		"|([0-2]?\\d(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec))" +  // days: 01-29th or 1-29th for each month
		"((19|20)(\\d{2})))";												// years: 1900 to 2099.
		// handle bissextile years?
		final String timezoneLetter = "\\([A-Za-z]*\\)";
		final String datePattern = dayOfWeek + "," + date + time + timezone + timezoneLetter;
		
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
		
		if(checkFrom && checkBy && checkFor && checkDate) {
			er.detail("     Success:  DTS 104-106 - Received field is valid");
		} else {
			er.warning("104-106", "Received field is invalid.", "", "DTS 104-106");
		}
		
	}

	// DTS 197, Resent Fields, Required
	public void validateWrappedResentFields(ErrorRecorder er, String[] resentField) {
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
	public void validateWrappedResentDate(ErrorRecorder er, String resentDate) {
		final String dayOfWeek = "(Mon|Tue|Wed|Thu|Fri|Sat|Sun)";
		final String time = "([01]?[0-9]|2[0-3])(:[0-5][0-9]){1,2}";
		final String timezone = "[-+]((0[0-9]|1[0-3])([03]0|45)|1400)";
		final String whitespace = "\\s";
		final String date = "((31 (Jan|Mar|May|Jul|Aug|Oct|Dec))" +    			// 31th of each month
		"|(30 (Jan|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec))" +      		// 30th of each month except Feb
		"|([0-2]?\\d (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec))" +  // days: 01-29th or 1-29th for each month
		" " +
		"((19|20)(\\d{2})))";												// years: 1900 to 2099.
		// handle bissextile years?
		final String datePattern = dayOfWeek + "," + whitespace + date + whitespace + time + whitespace + timezone;

		Pattern pattern = Pattern.compile(datePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(resentDate);

		if(matcher.matches()) {
			er.detail("     Success:  DTS 107 - Resent-Date is valid");
		} else if (resentDate.equals("")) { 
			er.detail("     Info:  DTS 107 - Resent-Date is not present");
		} else{
			er.err("107", "Resent-Date is invalid.", "", "DTS 107", "");
		}
		
	}
	
	// DTS 108, Resent-From, Conditional
	public void validateWrappedResentFrom(ErrorRecorder er, String resentFrom) {
		final String email = "([0-9a-zA-Z]+([_.-]?[0-9a-zA-Z]+)*@[0-9a-zA-Z]+[0-9,a-z,A-Z,.,-]*(.){1}[a-zA-Z]{2,4})+";  // source: http://tools.netshiftmedia.com/regexlibrary/
		final String emailWithName = "([0-9,a-z,_,-]+ )*<" + email + ">";
		final String fromFormat =  email + "|" + emailWithName;
		Pattern pattern = Pattern.compile(fromFormat, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(resentFrom);
		if(matcher.matches()) {
			er.detail("     Success:  DTS 108 - Resent-From field is valid");
		} else if (resentFrom.equals("")) { 
			er.detail("     Info:  DTS 108 - Resent-From is not present");
		} else {
			er.err("108", "Resent-From field is invalid.", "", "DTS 108", "");
		}
		
	}
	
	// DTS 109, Resent-Sender, Conditional
	public void validateWrappedResentSender(ErrorRecorder er, String resentSender, String resentFrom) {
		final String email = "([0-9a-zA-Z]+([_.-]?[0-9a-zA-Z]+)*@[0-9a-zA-Z]+[0-9,a-z,A-Z,.,-]*(.){1}[a-zA-Z]{2,4})+";
		final String emailWithName = "([0-9,a-z,_,-]+ )*<" + email + ">";
		final String fromFormat =  email + "|" + emailWithName;
		Pattern pattern = Pattern.compile(fromFormat, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(resentFrom);
		if(matcher.matches() && !resentSender.equals(resentFrom)) {
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
	public void validateWrappedResentTo(ErrorRecorder er, String resentTo) {
		final String email = "([0-9a-zA-Z]+([_.-]?[0-9a-zA-Z]+)*@[0-9a-zA-Z]+[0-9,a-z,A-Z,.,-]*(.){1}[a-zA-Z]{2,4})+";
		final String emailWithName = "([0-9,a-z,_,-]+ )*<" + email + ">";
		final String fromFormat =  email + "|" + emailWithName;
		Pattern pattern = Pattern.compile(fromFormat, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(resentTo);
		if(matcher.matches()) {
			er.detail("     Success:  DTS 110 - Resent-To field is valid");
		} else if (resentTo.equals("")) { 
			er.detail("     Info:  DTS 110 - Resent-To is not present");
		} else {
			er.err("110", "Resent-To field is invalid.", "", "DTS 110", "");
		}
		
	}

	// DTS 111, Resent-cc, Optional
	public void validateWrappedResentCc(ErrorRecorder er, String resentCc) {
		final String email = "([0-9a-zA-Z]+([_.-]?[0-9a-zA-Z]+)*@[0-9a-zA-Z]+[0-9,a-z,A-Z,.,-]*(.){1}[a-zA-Z]{2,4})+";
		final String emailWithName = "([0-9,a-z,_,-]+ )*<" + email + ">";
		final String fromFormat =  email + "|" + emailWithName;
		Pattern pattern = Pattern.compile(fromFormat, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(resentCc);
		if(matcher.matches()) {
			er.detail("     Success:  DTS 111 - Resent-Cc field is valid");
		} else if (resentCc.equals("")) { 
			er.detail("     Info:  DTS 111 - Resent-Cc is not present");
		} else {
			er.err("111", "Resent-Cc field is invalid.", "", "DTS 111", "");
		}
		
	}

	// DTS 112, Resent-bcc, Optional
	public void validateWrappedResentBcc(ErrorRecorder er, String resentBcc) {
		final String email = "([0-9a-zA-Z]+([_.-]?[0-9a-zA-Z]+)*@[0-9a-zA-Z]+[0-9,a-z,A-Z,.,-]*(.){1}[a-zA-Z]{2,4})+";
		final String emailWithName = "([0-9,a-z,_,-]+ )*<" + email + ">";
		final String fromFormat =  email + "|" + emailWithName;
		Pattern pattern = Pattern.compile(fromFormat, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(resentBcc);
		if(matcher.matches()) {
			er.detail("     Success:  DTS 112 - Resent-Bcc field is valid");
		} else if (resentBcc.equals("")) { 
			er.detail("     Info:  DTS 112 - Resent-Bcc is not present");
		} else {
			er.err("112", "Resent-Bcc field is invalid.", "", "DTS 112", "");
		}
		
	}

	// DTS 113, Resent-Msg-Id, Conditional
	public void validateWrappedResentMsgId(ErrorRecorder er, String resentMsgId) {
		Pattern pattern = Pattern.compile("<" + "[0-9,a-z,_,\\-,.]+" + "@" + "[0-9,a-z,_,\\-,.]+" + ">", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(resentMsgId);
		//er.detail(matcher.matches());
		if(matcher.matches()) {
			er.detail("     Success:  DTS 113 - Resent-Msg-Id field is valid");
		} else if (resentMsgId.equals("")) { 
			er.detail("     Info:  DTS 113 - Resent-Msg-Id is not present");
		} else {
			er.err("113", "Resent-Msg-Id field is invalid.", "", "DTS 113", "");
		}
		
	}
	
	// DTS 114, Orig-Date, Required
	public void validateWrappedOrigDate(ErrorRecorder er, String origDate) {
		final String dayOfWeek = "(Mon|Tue|Wed|Thu|Fri|Sat|Sun)";
		final String time = "([01]?[0-9]|2[0-3])(:[0-5][0-9]){1,2}";
		final String timezone = "[-+]((0[0-9]|1[0-3])([03]0|45)|1400)";
		final String letterTimezone = "\\([A-Z]*\\)";
		final String whitespace = "\\s";
		final String date = "((31 (Jan|Mar|May|Jul|Aug|Oct|Dec))" +    			// 31th of each month
		"|(30 (Jan|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec))" +      		// 30th of each month except Feb
		"|([0-2]?\\d (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec))" +  // days: 01-29th or 1-29th for each month
		" " +
		"((19|20)(\\d{2})))";												// years: 1900 to 2099.
		// handle bissextile years?
		final String datePattern = dayOfWeek + "," + whitespace + date + whitespace + time + whitespace + timezone + "(" + whitespace + letterTimezone + ")" + "?";

		Pattern pattern = Pattern.compile(datePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(origDate);

		/*
		Date formattedDate = null;
		try {
			formattedDate = ValidationUtils.parseDate(origDate);            // parses the date string using all available formats
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		} 
		
		ValidationUtils.setDate(formattedDate);
		*/

		if(matcher.matches()) {
			er.detail("     Success:  DTS 114 - Orig Date is valid");
		} else {
			er.err("114", "Orig Date is invalid.", "", "DTS 114", "");
		}
	}

	// DTS 115, From, Required
	public void validateWrappedFrom(ErrorRecorder er, String from) {
		if (ValidationUtils.validateEmailAddressFormatRFC2822(from)){
			er.detail("     Success:  DTS 115 - From field is valid");
		} else {
			er.err("115", "From field is invalid.", "", "DTS 115", "");
		}
		
	}
	
	// DTS 116, Sender, Conditional
	public void validateWrappedSender(ErrorRecorder er, String sender, Address[] from) {
		if(from.length>1) {
			final String email = "([0-9a-zA-Z]+([_.-]?[0-9a-zA-Z]+)*@[0-9a-zA-Z]+[0-9,a-z,A-Z,.,-]*(.){1}[a-zA-Z]{2,4})+";  // source: http://tools.netshiftmedia.com/regexlibrary/
			final String emailWithName = "([0-9,a-z,_,-]+ )*<" + email + ">";
			final String fromFormat =  email + "|" + emailWithName;
			Pattern pattern = Pattern.compile(fromFormat, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(sender);
			if(matcher.matches()) {
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
	public void validateWrappedReplyTo(ErrorRecorder er, String replyTo) {
		final String email = "([0-9a-zA-Z]+([_.-]?[0-9a-zA-Z]+)*@[0-9a-zA-Z]+[0-9,a-z,A-Z,.,-]*(.){1}[a-zA-Z]{2,4})+";
		final String emailWithName = "([0-9,a-z,_,-]+ )*<" + email + ">" + "(;([0-9,a-z,_,-]+ )*<" + email + ">)*";
		final String replyToFormat =  email + "|" + emailWithName;
		Pattern pattern = Pattern.compile(replyToFormat, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(replyTo);
		if(matcher.matches()) {
			er.detail("     Success:  DTS 117 - Reply-To field is valid");
		} else if(replyTo.equals("")) {
			er.detail("     Info:  DTS 117 - Reply-To field is not present");
		} else {
			er.err("117", "Reply-To field is invalid.", "", "DTS 117", "");
		}
		
	}

	// DTS 118, To, Required
	public void validateWrappedTo(ErrorRecorder er, String to) {
		// labels must start with a letter, end with a letter or digit, and have as interior characters only letters, digits, and hyphen
		// each label MUST be zero to 63 octets in length
		final String label = "([a-z]([-]?[0-9a-z]+){0,61}[0-9a-z])";
		// labels are separated by dots
		// Each Node (email address) MUST have a label 
		//final String domainName = "(" + label + ".)+";
		final String email = "([0-9a-zA-Z]+([_.-]?[0-9a-zA-Z]+)*@[0-9a-zA-Z]+[0-9,a-z,A-Z,.,-]*(.){1}[a-zA-Z]{2,4})+";
		final String emailWithName = "([0-9,a-z,_,-]+ )*<" + email + ">";
		final String toFormat =  email + "|" + emailWithName;
		Pattern pattern = Pattern.compile(toFormat, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(to);
		if(matcher.matches()) {
			er.detail("     Success:  DTS 118 - To field is valid");
		} else {
			er.err("118", "To field is invalid.", "", "DTS 118", "");
		}
		
	}
	
	// DTS 119, cc, Optional
	public void validateWrappedCc(ErrorRecorder er, String cc) {
		final String email = "([0-9a-zA-Z]+([_.-]?[0-9a-zA-Z]+)*@[0-9a-zA-Z]+[0-9,a-z,A-Z,.,-]*(.){1}[a-zA-Z]{2,4})+";
		final String emailWithName = "([0-9,a-z,_,-]+ )*<" + email + ">" + "(;([0-9,a-z,_,-]+ )*<" + email + ">)*";
		final String ccFormat =  email + "|" + emailWithName;
		Pattern pattern = Pattern.compile(ccFormat, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cc);
		if(matcher.matches()) {
			er.detail("     Success:  DTS 119 - Cc field is valid");
		} else if(cc.equals("")) {
			er.detail("     Info:  DTS 119 - Cc is not present");
		} else {
			er.err("119", "Cc field is invalid.", "", "DTS 119", "");
		}
		
	}
	
	// DTS 120, Bcc, Optional
	public void validateWrappedBcc(ErrorRecorder er, String bcc) {
		if(bcc.equals("")) {
			er.detail("     Success:  DTS 120 - Bcc field is valid");
		} else {
			er.err("120", "Bcc field is invalid.", "", "DTS 120", "");
		}
		
	}

	// DTS 121, Message-Id, Required
	public void validateWrappedMessageId(ErrorRecorder er, String messageId) {
		//handle display		
		Pattern pattern = Pattern.compile("<" + "[0-9,a-z,_,\\-,.]+" + "@" + "[0-9,a-z,_,\\-,.]+" + ">", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(messageId);
		//er.detail(matcher.matches());
		if(matcher.matches()) {
			er.detail("     Success:  DTS 121 - Message Id is valid");
		} else {
			er.err("121", "Message Id field is invalid.", "", "DTS 121", "");
		}
	}

	// DTS 122, In-reply-to, Optional
	public void validateWrappedInReplyTo(ErrorRecorder er, String inReplyTo, String date) {
		// Check 1: Must be formatted as one or more <randomstringwithoutspaces@randomstringwithoutspaces>
		Pattern pattern = Pattern.compile("<" + "[0-9,a-z,_,\\-,.]+" + "@" + "[0-9,a-z,_,\\-,.]+" + ">", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inReplyTo);
		if(matcher.matches()) {
			er.detail("     Success:  DTS 122 - In-Reply-To field is valid");
		} else if(inReplyTo.equals("")) {
			er.detail("     Info:  DTS 122 - In-Reply-To is not present");
		} else {
			er.err("122", "In-Reply-To field is invalid", "", "DTS 122", "");
		}
		
	}
	
	// DTS 123, References, Optional
	public void validateWrappedReferences(ErrorRecorder er, String references) {
		Pattern pattern = Pattern.compile("<" + "[0-9,a-z,_,\\-,.]+" + "@" + "[0-9,a-z,_,\\-,.]+" + ">", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(references);
		if(matcher.matches()) {
			er.detail("     Success:  DTS 123 - Reference field is valid");
		} else if(references.equals("")) {
			er.detail("     Info:  DTS 123 - Reference is not present");
		} else {
			er.err("123", "Reference field is invalid", "", "DTS 123", "");
		}
		
	}
	
	// DTS 124, Subject, Optional
	public void validateWrappedSubject(ErrorRecorder er, String subject, String filename) {
		if(filename.contains("zip")) {
			if(subject.contains("XDM/1.0/DDM")) {
				er.detail("     Success:  DTS 124 - Subject field is valid");
			} else if(subject == null) {
				er.warning("124", "Subject field is not present", "", "DTS 124");
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
	public void validateWrappedComments(ErrorRecorder er, String comments) {
		if(comments.equals("")) {
			er.detail("     Info:  DTS 125 - Comments is not present");
		} else {
			er.detail("     Success:  DTS 125 - Comments field is valid");
		}
		
	}
	
	// DTS 126, Keywords, Optional
	public void validateWrappedKeywords(ErrorRecorder er, String keyword) {
		if(keyword.equals("")) {
			er.detail("     Info:  DTS 126 - Keywords is not present");
		} else {
			er.detail("     Success:  DTS 126 - Keywords field is valid");
		}
		
	}
	
	// DTS 127, Optional-field, Optional
	public void validateWrappedOptionalField(ErrorRecorder er, String optionalField) {
		if(optionalField.equals("")) {
			er.detail("     Info:  DTS 127 - Optional-field is not present");
		} else {
			er.detail("     Success:  DTS 127 - Optional-field field is valid"); 
		}
		
	}
	
	// DTS 128, Disposition-Notification-To, Optional
	public void validateWrappedDispositionNotificationTo(ErrorRecorder er, String dispositionNotificationTo) {
		if(dispositionNotificationTo.equals("")) {
			er.detail("     Info:  DTS 128 - Disposition-Notification-To field is not present");
		} else {
			final String email = "([0-9a-zA-Z]+([_.-]?[0-9a-zA-Z]+)*@[0-9a-zA-Z]+[0-9,a-z,A-Z,.,-]*(.){1}[a-zA-Z]{2,4})+";
			final String emailWithName = "([0-9,a-z,_,-]+ )*<" + email + ">" + "(;([0-9,a-z,_,-]+ )*<" + email + ">)*";
			final String ccFormat =  email + "|" + emailWithName;
			Pattern pattern = Pattern.compile(ccFormat, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(dispositionNotificationTo);
			if(matcher.matches()) {
				er.detail("     Success:  DTS 128 - Disposition-Notification-To field is valid");
			} else {
				er.err("128", "Disposition-Notification-To field is invalid.", "", "DTS 128", "");
			}
		}
		
	}
	
	// DTS 102b, MIME-Version, Required
	public void validateWrappedMIMEVersion(ErrorRecorder er, String MIMEVersion) {
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
