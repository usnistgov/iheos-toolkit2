package gov.nist.direct.mdn.validate;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.Part;
import org.apache.commons.io.IOUtils;

import gov.nist.toolkit.errorrecording.ErrorRecorder;

/**
 * This class calls the full MDN message validation
 * @author jnp3
 * @author dazais
 *
 */
public class ProcessMDN {
	
	private String dispNotifTo;
	private String originalRecipient;
	private String reportingUA;
	private String mdnGateway;
	private String finalRecipient;
	private String originalMessageID;
	private String disposition;
	private String failure;
	private String error;
	private String warning;
	private String extension;
	
	private ArrayList<String> headerName;
	private ArrayList<String> headerField;
	
	public ProcessMDN(ErrorRecorder er, Part p){
		headerName = new ArrayList<String>();
		headerField = new ArrayList<String>();	
		
		InputStream mdnStream = null;

		try {
			mdnStream = (InputStream) p.getContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(mdnStream, writer, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String mdnPart = writer.toString();
		er.detail("-------------------- MDN Headers -------------------");
		String[] mdnHeaderSplit = mdnPart.split("\n");
		for(int i=0;i<mdnHeaderSplit.length;i++) {
			if(mdnHeaderSplit[i].contains("\r")) {
				mdnHeaderSplit[i] = mdnHeaderSplit[i].replaceAll("\\r", "");
			}
			er.detail(mdnHeaderSplit[i]);
			String[] splitHeader;
			if(mdnHeaderSplit[i].contains(": ")) {
				splitHeader = mdnHeaderSplit[i].split(":\\s");
				headerName.add(splitHeader[0].toLowerCase());
				headerField.add(splitHeader[1]);
			}
		}
		er.detail("-----------------------------------------------------------");
		mdnPart = mdnPart.toLowerCase();

		dispNotifTo = getMDNHeader("disposition-notification-to");
		originalRecipient = getMDNHeader("original-recipient");
		reportingUA = getMDNHeader("reporting-ua");
		mdnGateway = getMDNHeader("mdn-gateway");
		finalRecipient = getMDNHeader("final-recipient");
		originalMessageID = getMDNHeader("original-message-id");
		disposition = getMDNHeader("disposition");
		failure = getMDNHeader("failure");
		error = getMDNHeader("error");
		warning = getMDNHeader("warning");
		extension = getMDNHeader("extension");
	}

	public void validate(ErrorRecorder er){

		MDNValidator validator = new MDNValidatorImpl();

		// DTS 452, Disposition-Notification-To, Required
		validator.validateMDNRequestHeader(er, dispNotifTo);
		
		// DTS 454, Original-Recipient-Header, warning
		validator.validateOriginalRecipientHeader(er, originalRecipient);
		
		// DTS 456, Disposition-Notification-Content, warning
		validator.validateDispositionNotificationContent(er, reportingUA, mdnGateway, originalRecipient, finalRecipient, originalMessageID, disposition, failure, error, warning, extension);
		
		// DTS 457, Reporting-UA-Field, warning
		validator.validateReportingUAField(er, reportingUA);
		
		// DTS 458, mdn-gateway-field, Required
		validator.validateMDNGatewayField(er, mdnGateway);
		
		// DTS 459, original-recipient-field, Required
		validator.validateOriginalRecipientField(er, originalRecipient);
		
		// DTS 460, final-recipient-field, Required
		validator.validateFinalRecipientField(er, finalRecipient);
		
		// DTS 461, original-message-id-field, Required
		validator.validateOriginalMessageIdField(er, originalMessageID);
		
		// DTS 462, disposition-field, Required
		validator.validateDispositionField(er, disposition);
		
		// DTS 463, failure-field, Required
		validator.validateFailureField(er, failure);
		
		// DTS 464, error-field, Required
		validator.validateErrorField(er, error);
		
		// DTS 465, warning-field, Required
		validator.validateWarningField(er, warning);
		
		// DTS 466, extension-field, Required
		validator.validateExtensionField(er, extension);		
	}
	
	public String getMDNHeader(String header) {
		String res = "";
		if(!headerName.isEmpty()) {
			for(int i=0;i<headerName.size();i++) {
				if(headerName.get(i).equals(header)) {
					res = headerField.get(i);
				}
			}
		}
		return res;
	}
	
	
	public String getDispositionField() {
		return this.disposition;
	}
	
	public String getOriginalMessageId() {
		return this.originalMessageID;
	}

}
