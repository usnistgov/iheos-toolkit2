package gov.nist.direct.validation.impl;

import gov.nist.direct.utils.ValidationUtils;
import gov.nist.direct.validation.MessageValidatorFacade;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class ProcessEnvelope {
	
	private String[] contentType_split = null;
	private String[] temp = null;
	private String content_split_right = "";
	@SuppressWarnings("unused")
	private String content_split_left = "";
	private ArrayList<String> searchRes = new ArrayList<String>();
	private MessageValidatorFacade msgValidator = new DirectMimeMessageValidatorFacade();

	
	public void validateMimeEntity(ErrorRecorder er, Part m, LinkedHashMap<String, Integer> summary, int partNumber) throws Exception {		
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
		msgValidator.validateBody(er, m, m.getContent().toString());
		
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
			summary.put(shift + "Content-type: "+m.getContentType(), er.getNbErrors());
		}
		
		// DTS 156 Validate Content Type Disposition
		msgValidator.validateContentTypeDisposition(er, m.getDisposition(), m.getContentType());
		
		// DTS 161-194 Validate Content-Disposition Filename
		if(m.getFileName() != null) {
			msgValidator.validateContentDispositionFilename(er, m.getFileName());
			summary.put(shift + "Content-Disposition: "+m.getDisposition(), er.getNbErrors());
		}
		
		
		/**************************/
		/** MUST BE THE LAST ONE **/
		/**************************/
		// DTS 199 Validate All non-MIME message headers
		msgValidator.validateMIMEEntity(er, "");
		
		
	}
	
	public void validateMessageHeader(ErrorRecorder er, Message m, LinkedHashMap<String, Integer> summary, int partNumber, boolean wrapped) throws Exception {
		er.sectionHeading("Message Header Checklist");
		
		String shift = "";
		if(partNumber==0) {
			shift = "-----";
		} else {
			shift = "---------------";
		}
		
		// DTS 196, Validate All Headers
		String[] header = ValidationUtils.getHeadersAndContent((MimeMessage) m).get(0);
		String[] headerContent =ValidationUtils.getHeadersAndContent((MimeMessage) m).get(1);
		if(wrapped) {
			msgValidator.validateWrappedAllHeaders(er, header, headerContent);
		} else {
			msgValidator.validateAllHeaders(er, header, headerContent);
		}
		
		// DTS 114 Validate Orig Date
		if(wrapped) {
			msgValidator.validateWrappedOrigDate(er, searchHeaderSimple(m, "date"));
		} else {
			msgValidator.validateOrigDate(er, searchHeaderSimple(m, "date"));
		}
		summary.put(shift + "Orig-Date: "+searchHeaderSimple(m, "date")+" (Part number: "+partNumber+")", er.getNbErrors());
		
		// DTS 115 Validate From
		String from = "";
		if(m.getFrom() != null) {
			from = m.getFrom()[0].toString();
		}
		if(wrapped) {
			msgValidator.validateWrappedFrom(er, from);
		} else {
			msgValidator.validateFrom(er, from);
		}
		summary.put(shift + "From: "+from+" (Part number: "+partNumber+")", er.getNbErrors());
		
		// DTS 118 Validate To
		String to = "";
		if(m.getRecipients(Message.RecipientType.TO) != null) {
			to = m.getRecipients(Message.RecipientType.TO)[0].toString();
		}
		if(wrapped) {
			msgValidator.validateWrappedTo(er, to);
		} else {
			msgValidator.validateTo(er, to);
		}
		summary.put(shift + "To: "+to+" (Part number: "+partNumber+")", er.getNbErrors());
			
		// DTS 121, Validate Message-Id
		if(wrapped) {
			msgValidator.validateWrappedMessageId(er, searchHeaderSimple(m, "message-id"));
		} else {
			msgValidator.validateMessageId(er, searchHeaderSimple(m, "message-id"));
		}
		summary.put(shift + "Message-Id: "+searchHeaderSimple(m, "message-id")+" (Part number: "+partNumber+")", er.getNbErrors());
		
		// DTS 102b Validate Mime Version
		// Searching for Mime Version Header and Value
		if(wrapped) {
			msgValidator.validateWrappedMIMEVersion(er, searchHeaderSimple(m, "mime-version"));
		} else {
			msgValidator.validateMIMEVersion(er, searchHeaderSimple(m, "mime-version"));
		}
		summary.put(shift + "MIME-Version: "+searchHeaderSimple(m, "mime-version")+" (Part number: "+partNumber+")", er.getNbErrors());
		
		// DTS 103-105 Validate Return Path
		searchRes = searchHeader(m, "return-path");
		String returnPath = "";
		for(int i=0;i<searchRes.size();i++) {
			returnPath = searchRes.get(i);
			if(wrapped) {
				msgValidator.validateWrappedReturnPath(er, returnPath);
			} else {
				msgValidator.validateReturnPath(er, returnPath);
			}
		}
		if(!returnPath.equals("")) {
			summary.put(shift + "Return-Path: "+returnPath+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 104-106 Validate Received
		searchRes = searchHeader(m, "received");
		String received = "";
		for(int i=0;i<searchRes.size();i++) {
			received = searchRes.get(i);
			received = received.replaceAll("\\s", "");
			if(wrapped) {
				msgValidator.validateWrappedReceived(er, received);
			} else {
				msgValidator.validateReceived(er, received);
			}
		}
		summary.put(shift + "Received: "+received+" (Part number: "+partNumber+")", er.getNbErrors());
		
		// DTS 107 Validate Resent-Date
		msgValidator.validateResentDate(er, searchHeaderSimple(m, "resent-date"));
		if(!searchHeaderSimple(m, "resent-date").equals("")) {
			summary.put(shift + "Resent-Date: "+searchHeaderSimple(m, "resent-date")+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 108 Validate Resent-From
		msgValidator.validateResentFrom(er, searchHeaderSimple(m, "resent-from"));
		if(!searchHeaderSimple(m, "resent-from").equals("")) {
			summary.put("----------Resent-From: "+searchHeaderSimple(m, "resent-from")+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 109 Validate Resent-Sender
		msgValidator.validateResentSender(er, searchHeaderSimple(m, "resent-sender"), searchHeaderSimple(m, "resent-from"));
		if(!searchHeaderSimple(m, "resent-sender").equals("")) {
			summary.put(shift + "Resent-Sender: "+searchHeaderSimple(m, "resent-sender")+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 113 Validate Resent-Msg-Id
		msgValidator.validateResentMsgId(er, searchHeaderSimple(m, "resent-msg-id"));
		if(!searchHeaderSimple(m, "resent-msg-id").equals("")) {
			summary.put(shift + "Resent-Msg-Id: "+searchHeaderSimple(m, "resent-msg-id")+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 116 Validate Sender
		if(m.getFrom() != null) {
			msgValidator.validateSender(er, searchHeaderSimple(m, "sender"), m.getFrom());
			if(!searchHeaderSimple(m, "sender").equals("")) {
				summary.put(shift + "Sender: "+searchHeaderSimple(m, "sender")+" (Part number: "+partNumber+")", er.getNbErrors());
			}
		}
		
		// DTS 117 Validate Reply To
		if(m.getReplyTo() != null) {
			msgValidator.validateReplyTo(er, m.getReplyTo()[0].toString());
			if(!m.getReplyTo()[0].toString().equals("")) {
				summary.put(shift + "Reply-To: "+m.getReplyTo()[0].toString()+" (Part number: "+partNumber+")", er.getNbErrors());
			}
		}
		
		// DTS 110 Validate Resent-To
		msgValidator.validateResentTo(er, searchHeaderSimple(m, "resent-to"));
		if(!searchHeaderSimple(m, "resent-to").equals("")) {
			summary.put(shift + "Resent-To: "+searchHeaderSimple(m, "resent-to")+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 111 Validate Resent-Cc
		msgValidator.validateResentCc(er, searchHeaderSimple(m, "resent-cc"));
		if(!searchHeaderSimple(m, "resent-cc").equals("")) {
			summary.put(shift + "Resent-Cc: "+searchHeaderSimple(m, "resent-cc")+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 112 Validate Resent-Bcc
		msgValidator.validateResentBcc(er, searchHeaderSimple(m, "resent-bcc"));
		if(!searchHeaderSimple(m, "resent-bcc").equals("")) {
			summary.put(shift + "Resent-Bcc: "+searchHeaderSimple(m, "resent-bcc")+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 197 Validate Resent Fields
		String[] resentField = null;
		resentField = ValidationUtils.getHeadersAndContent((MimeMessage) m).get(0);
		msgValidator.validateResentFields(er, resentField);
		
		// DTS 119 Validate Cc
		searchRes = searchHeader(m, "cc");
		String cc = "";
		for(int i=0;i<searchRes.size();i++) {
			cc = searchRes.get(i);
			cc = cc.replaceAll("\\s", "");
		}
		msgValidator.validateCc(er, cc);
		if(!cc.equals("")) {
			summary.put(shift + "Cc: "+cc+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 120 Validate Bcc
		msgValidator.validateBcc(er, searchHeaderSimple(m, "bcc"));
		if(!searchHeaderSimple(m, "bcc").equals("")) {
			summary.put(shift + "Bcc: "+searchHeaderSimple(m, "bcc")+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 122 Validate In-Reply-To
		msgValidator.validateInReplyTo(er, searchHeaderSimple(m, "in-reply-to"), searchHeaderSimple(m, "date"));
		if(!searchHeaderSimple(m, "in-reply-to").equals("")) {
			summary.put(shift + "In-Reply-To: "+searchHeaderSimple(m, "in-reply-to")+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 123 Validate Reference
		msgValidator.validateReferences(er, searchHeaderSimple(m, "references"));
		if(!searchHeaderSimple(m, "references").equals("")) {
			summary.put(shift + "References: "+searchHeaderSimple(m, "references")+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 124 Validate Subject
		msgValidator.validateSubject(er, m.getSubject(), m.getContentType());
		if(m.getSubject() != null) {
			summary.put(shift + "Subject: "+m.getSubject()+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 125 Validate Comment
		msgValidator.validateComments(er, searchHeaderSimple(m, "comments"));
		if(!searchHeaderSimple(m, "comments").equals("")) {
			summary.put(shift + "Comment: "+searchHeaderSimple(m, "comments")+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 126 Validate Keywords
		msgValidator.validateKeywords(er, searchHeaderSimple(m, "keywords"));
		if(!searchHeaderSimple(m, "keywords").equals("")) {
			summary.put(shift + "Keyword: "+searchHeaderSimple(m, "keywords")+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 127 Validate Optional Fields
		msgValidator.validateOptionalField(er, searchHeaderSimple(m, "optional-field"));
		if(!searchHeaderSimple(m, "optional-field").equals("")) {
			summary.put(shift + "Optional-Fields: "+searchHeaderSimple(m, "optional-field")+" (Part number: "+partNumber+")", er.getNbErrors());
		}
		
		// DTS 128 Validate Disposition-Notification-To
		if(wrapped) {
			msgValidator.validateWrappedDispositionNotificationTo(er, searchHeaderSimple(m, "disposition-notification-to"));
		} else {
			msgValidator.validateDispositionNotificationTo(er, searchHeaderSimple(m, "disposition-notification-to"));
		}
		if(!searchHeaderSimple(m, "disposition-notification-to").equals("")) {
			summary.put(shift + "Disposition-Notification-To: "+searchHeaderSimple(m, "disposition-notification-to")+" (Part number: "+partNumber+")", er.getNbErrors());
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
		this.validateMimeEntity(er, m, new LinkedHashMap<String, Integer>(), 0);
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

}
