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

import gov.nist.direct.directValidator.MessageValidatorFacade;
import gov.nist.direct.utils.ValidationSummary;
import gov.nist.direct.utils.ValidationUtils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorder;

import java.util.ArrayList;
import java.util.Enumeration;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class ProcessEnvelope {
	
	private String[] contentType_split = null;
	private String[] temp = null;
	private String content_split_right = "";
	@SuppressWarnings("unused")
	private String content_split_left = "";
	private ArrayList<String> searchRes = new ArrayList<String>();
	private MessageValidatorFacade msgValidator = new DirectMimeMessageValidatorFacade();

	
	public void validateMimeEntity(ErrorRecorder er, Part m, ValidationSummary validationSummary, int partNumber) throws Exception {		
		// Calculate the shift
		String shift = "";
		for(int i=0;i<partNumber;i++) {
			shift += "-----";
		}
		
		er.sectionHeading("MIME Entity Checklist");
		
		// DTS 133-145-146 Validate Content Type
		msgValidator.validateContentType(er, m.getContentType());
		
		// DTS 191 Validate Content Type Subtype
		msgValidator.validateContentTypeSubtype(er, m.getContentType());
		
		// DTS 195, Validate Body
		//if(m.getContentType().contains("text/plain"))
		//	msgValidator.validateBody(er, m, m.getContent().toString());
		
		// DTS 192 Validate Content Type Name
		msgValidator.validateContentTypeName(er, m.getContentType());
		
		// DTS 193 Validate Content Type SMIMEType
		msgValidator.validateContentTypeSMIMEType(er, m.getContentType());
		
		// DTS 137-140 Validate Content Type Boundary
		// Find the boundary
		String boundary = "";
		contentType_split = m.getContentType().split("boundary=");
		if(contentType_split.length > 1) {
			content_split_right = contentType_split[1];
			temp = content_split_right.split("\"", -2); // splits again anything that is after the boundary expression, just in case
			for (int j = 0; j<temp.length ; j++){
				if (temp[j].contains("--")) {
					boundary = temp[j];
				}
			}
		}
		msgValidator.validateContentTypeBoundary(er, boundary);
		
		// Update summary
		if(m.getContentType() != null) {
			validationSummary.recordKey(shift + "Content-type: "+m.getContentType(), er.hasErrors(), true);
		}
		
		
		String contentTypeDisposition = "";
		if(m.getDisposition() != null) {
			contentTypeDisposition = m.getDisposition();
		}
		
		// DTS 156 Validate Content Type Disposition
		msgValidator.validateContentTypeDisposition(er, contentTypeDisposition, m.getContentType());
		
		/*
		// DTS 161-194 Validate Content-Disposition Filename
		if(m.getFileName() != null) {
			msgValidator.validateContentDispositionFilename(er, m.getFileName());
			validationSummary.recordKey(shift + "Content-Disposition: "+m.getDisposition(), er.hasErrors(), true);
			
		}
		*/
		
		// DTS 190, All Mime Header Fields, Required
		msgValidator.validateAllMimeHeaderFields(er, contentTypeDisposition);
		
		
		/**************************/
		/** MUST BE THE LAST ONE **/
		/**************************/
		// DTS 199 Validate All non-MIME message headers
		msgValidator.validateMIMEEntity(er, "");
		
		
	}
	
	public void validateMessageHeader(ErrorRecorder er, Message m, ValidationSummary validationSummary, int partNumber, boolean wrapped) throws Exception {
		er.sectionHeading("Message Header Checklist");
		
		// Separate ErrorRecorder for the summary
		ErrorRecorder separate = new GwtErrorRecorder();
		
		String shift = "";
		if(partNumber==0) {
			shift = "-----";
		} else {
			shift = "---------------";
		}
		
		// DTS 196, Validate All Headers
		String[] header = ValidationUtils.getHeadersAndContent((MimeMessage) m).get(0);
		String[] headerContent =ValidationUtils.getHeadersAndContent((MimeMessage) m).get(1);
		msgValidator.validateAllHeaders(er, header, headerContent, wrapped);
		
		// DTS 114 Validate Orig Date
		msgValidator.validateOrigDate(separate, searchHeaderSimple(m, "date"), wrapped);
		validationSummary.recordKey(shift + "Orig-Date: "+searchHeaderSimple(m, "date"), separate.hasErrors(), true);
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 115 Validate From
		String from = "";
		if(m.getFrom() != null) {
			for(int i=0;i<m.getFrom().length;i++) {
				from = m.getFrom()[i].toString();
				msgValidator.validateFrom(separate, from, wrapped);
				from = SafeHtmlUtils.htmlEscape(from);
				validationSummary.recordKey(shift + "From: "+ from, separate.hasErrors(), true);
			}
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 118 Validate To
		String to = "";
		if(m.getRecipients(Message.RecipientType.TO) != null) {
			to = m.getRecipients(Message.RecipientType.TO)[0].toString();
		}
		msgValidator.validateTo(separate, to, wrapped);
		to = SafeHtmlUtils.htmlEscape(to);
		validationSummary.recordKey(shift + "To: "+to, separate.hasErrors(), true);
			
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 121, Validate Message-Id
		String messageID = searchHeaderSimple(m, "message-id");
		msgValidator.validateMessageId(separate, messageID, wrapped);
		validationSummary.recordKey(shift + "Message-Id: "+ SafeHtmlUtils.htmlEscape(messageID), separate.hasErrors(), true);
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 102b Validate Mime Version
		// Searching for Mime Version Header and Value
		msgValidator.validateMIMEVersion(separate, searchHeaderSimple(m, "mime-version"), wrapped);
		validationSummary.recordKey(shift + "MIME-Version: "+searchHeaderSimple(m, "mime-version"), separate.hasErrors(), true);
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 103-105 Validate Return Path
		searchRes = searchHeader(m, "return-path");
		String returnPath = "";
		for(int i=0;i<searchRes.size();i++) {
			returnPath = searchRes.get(i);
			msgValidator.validateReturnPath(separate, returnPath, wrapped);
		}
		if(!returnPath.equals("")) {
			validationSummary.recordKey(shift + "Return-Path: "+SafeHtmlUtils.htmlEscape(returnPath), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 104-106 Validate Received
		searchRes = searchHeader(m, "received");
		String received = "";
		for(int i=0;i<searchRes.size();i++) {
			received = searchRes.get(i);
			msgValidator.validateReceived(separate, received, wrapped);
		}
		validationSummary.recordKey(shift + "Received: "+received, separate.hasErrors(), true);
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 107 Validate Resent-Date
		msgValidator.validateResentDate(separate, searchHeaderSimple(m, "resent-date"), wrapped);
		if(!searchHeaderSimple(m, "resent-date").equals("")) {
			validationSummary.recordKey(shift + "Resent-Date: "+searchHeaderSimple(m, "resent-date"), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 108 Validate Resent-From
		msgValidator.validateResentFrom(separate, searchHeaderSimple(m, "resent-from"), wrapped);
		if(!searchHeaderSimple(m, "resent-from").equals("")) {
			validationSummary.recordKey(shift + "Resent-From: "+searchHeaderSimple(m, "resent-from"), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 109 Validate Resent-Sender
		msgValidator.validateResentSender(separate, searchHeaderSimple(m, "resent-sender"), searchHeaderSimple(m, "resent-from"), wrapped);
		if(!searchHeaderSimple(m, "resent-sender").equals("")) {
			validationSummary.recordKey(shift + "Resent-Sender: "+searchHeaderSimple(m, "resent-sender"), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 113 Validate Resent-Msg-Id
		msgValidator.validateResentMsgId(separate, searchHeaderSimple(m, "resent-msg-id"), wrapped);
		if(!searchHeaderSimple(m, "resent-msg-id").equals("")) {
			validationSummary.recordKey(shift + "Resent-Msg-Id: "+searchHeaderSimple(m, "resent-msg-id"), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 116 Validate Sender
		if(m.getFrom() != null) {
			msgValidator.validateSender(er, searchHeaderSimple(m, "sender"), m.getFrom(), wrapped);
			if(!searchHeaderSimple(m, "sender").equals("")) {
				validationSummary.recordKey(shift + "Sender: "+searchHeaderSimple(m, "sender"), separate.hasErrors(), true);
			}
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 117 Validate Reply To
		if(m.getReplyTo() != null) {
			msgValidator.validateReplyTo(separate, m.getReplyTo()[0].toString(), wrapped);
			if(!m.getReplyTo()[0].toString().equals("")) {
				String replyTo = SafeHtmlUtils.htmlEscape(m.getReplyTo()[0].toString());
				validationSummary.recordKey(shift + "Reply-To: "+ replyTo, separate.hasErrors(), true);
			}
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 110 Validate Resent-To
		msgValidator.validateResentTo(separate, searchHeaderSimple(m, "resent-to"), wrapped);
		if(!searchHeaderSimple(m, "resent-to").equals("")) {
			validationSummary.recordKey(shift + "Resent-To: "+searchHeaderSimple(m, "resent-to"), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 111 Validate Resent-Cc
		msgValidator.validateResentCc(separate, searchHeaderSimple(m, "resent-cc"), wrapped);
		if(!searchHeaderSimple(m, "resent-cc").equals("")) {
			validationSummary.recordKey(shift + "Resent-Cc: "+searchHeaderSimple(m, "resent-cc"), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 112 Validate Resent-Bcc
		msgValidator.validateResentBcc(separate, searchHeaderSimple(m, "resent-bcc"), wrapped);
		if(!searchHeaderSimple(m, "resent-bcc").equals("")) {
			validationSummary.recordKey(shift + "Resent-Bcc: "+searchHeaderSimple(m, "resent-bcc"), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 197 Validate Resent Fields
		String[] resentField = null;
		resentField = ValidationUtils.getHeadersAndContent((MimeMessage) m).get(0);
		msgValidator.validateResentFields(separate, resentField, wrapped);
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 119 Validate Cc
		searchRes = searchHeader(m, "cc");
		String cc = "";
		for(int i=0;i<searchRes.size();i++) {
			cc = searchRes.get(i);
			cc = cc.replaceAll("\\s", "");
		}
		msgValidator.validateCc(separate, cc, wrapped);
		if(!cc.equals("")) {
			validationSummary.recordKey(shift + "Cc: "+cc, separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 120 Validate Bcc
		msgValidator.validateBcc(er, searchHeaderSimple(m, "bcc"), wrapped);
		if(!searchHeaderSimple(m, "bcc").equals("")) {
			validationSummary.recordKey(shift + "Bcc: "+searchHeaderSimple(m, "bcc"), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 122 Validate In-Reply-To
		msgValidator.validateInReplyTo(separate, searchHeaderSimple(m, "in-reply-to"), searchHeaderSimple(m, "date"), wrapped);
		if(!searchHeaderSimple(m, "in-reply-to").equals("")) {
			validationSummary.recordKey(shift + "In-Reply-To: "+searchHeaderSimple(m, "in-reply-to"), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 123 Validate Reference
		msgValidator.validateReferences(separate, searchHeaderSimple(m, "references"), wrapped);
		if(!searchHeaderSimple(m, "references").equals("")) {
			validationSummary.recordKey(shift + "References: "+searchHeaderSimple(m, "references"), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 124 Validate Subject
		msgValidator.validateSubject(separate, m.getSubject(), m.getContentType(), wrapped);
		if(m.getSubject() != null) {
			validationSummary.recordKey(shift + "Subject: "+m.getSubject(), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 125 Validate Comment
		msgValidator.validateComments(separate, searchHeaderSimple(m, "comments"), wrapped);
		if(!searchHeaderSimple(m, "comments").equals("")) {
			validationSummary.recordKey(shift + "Comment: "+searchHeaderSimple(m, "comments"), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 126 Validate Keywords
		msgValidator.validateKeywords(separate, searchHeaderSimple(m, "keywords"), wrapped);
		if(!searchHeaderSimple(m, "keywords").equals("")) {
			validationSummary.recordKey(shift + "Keyword: "+searchHeaderSimple(m, "keywords"), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 127 Validate Optional Fields
		msgValidator.validateOptionalField(separate, searchHeaderSimple(m, "optional-field"), wrapped);
		if(!searchHeaderSimple(m, "optional-field").equals("")) {
			validationSummary.recordKey(shift + "Optional-Fields: "+searchHeaderSimple(m, "optional-field"), separate.hasErrors(), true);
		}
		
		er.concat(separate);
		separate = new GwtErrorRecorder();
		
		// DTS 128 Validate Disposition-Notification-To
		msgValidator.validateDispositionNotificationTo(separate, searchHeaderSimple(m, "disposition-notification-to"), wrapped);
		if(!searchHeaderSimple(m, "disposition-notification-to").equals("")) {
			validationSummary.recordKey(shift + "Disposition-Notification-To: "+searchHeaderSimple(m, "disposition-notification-to"), separate.hasErrors(), true);
		}
		
		/**************************/
		/** MUST BE THE LAST ONE **/
		/**************************/
		// DTS 199 Validate All non-MIME message headers
		msgValidator.validateNonMIMEMessageHeaders(er, "");

		
	}
	
	public void validateDirectMessageInnerDecryptedMessage(ErrorRecorder er, Part m) throws Exception {
		// DTS 133b, Validate Content-Type
		msgValidator.validateMessageContentTypeB(er, m.getContentType());
		
		// DTS 160, Validate Content-Type micalg
		// Find micalg
		String contentTypeMicalg = "";
		contentType_split = m.getContentType().split("micalg=");
		if(contentType_split.length > 1) {
			content_split_right = contentType_split[1];
			temp = content_split_right.split(";", -2);
			contentTypeMicalg = temp[0];
			contentTypeMicalg = contentTypeMicalg.toLowerCase();
		}
		msgValidator.validateContentTypeMicalg(er, contentTypeMicalg);
		
		// DTS 205, Validate Content-Type protocol
		// Find protocol
		String contentTypeProtocol = "";
		contentType_split = m.getContentType().split("protocol=");
		if(contentType_split.length > 1) {
			content_split_right = contentType_split[1];
			temp = content_split_right.split(";", -2);
			contentTypeProtocol = temp[0];
		}
		msgValidator.validateContentTypeProtocol(er, contentTypeProtocol);
		
		// DTS 206, Validate Content-Transfer-Encoding
		msgValidator.validateContentTransferEncoding(er, searchHeaderSimple(m, "content-transfer-encoding"));
		
		// DTS ??? - Mime Entity body - Required
		MimeMultipart mimeEntityBody = (MimeMultipart) m.getContent();
        msgValidator.validateMIMEEntityBody(er, mimeEntityBody.getCount());
        
        // DTS 204, MIME Entity
		this.validateMimeEntity(er, m, new ValidationSummary(), 0);
		msgValidator.validateMIMEEntity2(er, true);
		
	}


	
	/**
	 * Looking for header in a Message
	 * 
	 * @param m Message
	 * @param header Targeted header
	 * @return Header value
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<String> searchHeader(Part m, String header) {
		String[] tab = {header};
		String head = "";
		ArrayList<String> res = new ArrayList<String>();
		Enumeration e = null;
		try {
			e = m.getMatchingHeaders(tab);
		} catch (MessagingException e1) {
			e1.printStackTrace();
		}
		while (e.hasMoreElements()) {
			Header hed = (Header)e.nextElement();
			head = hed.getValue();
			res.add(head);
		}
		return res;
	}
	
	@SuppressWarnings("rawtypes")
	public String searchHeaderSimple(Part m, String header) {
		String[] tab = {header};
		String head = "";
		Enumeration e = null;
		try {
			e = m.getMatchingHeaders(tab);
		} catch (MessagingException e1) {
			e1.printStackTrace();
		}
		while (e.hasMoreElements()) {
			Header hed = (Header)e.nextElement();
			head = hed.getValue();
		}
		return head;
	}
	
	public String removeHtmlEntities(String header) {
		String res = header;
		if(header.contains("<") && header.contains(">")) {
			res = res.replace("<", "");
			res = res.replace(">", "");
		}
		return res;
	}

}
