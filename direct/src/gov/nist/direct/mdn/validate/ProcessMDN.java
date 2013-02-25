package gov.nist.direct.mdn.validate;

import java.io.IOException;
import java.io.StringWriter;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.SharedByteArrayInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.mailet.base.mail.MimeMultipartReport;

import gov.nist.direct.directValidator.impl.ProcessEnvelope;
import gov.nist.direct.mdn.MDNValidator;
import gov.nist.direct.mdn.impl.MDNValidatorImpl;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

/**
 * This class calls the full MDN message validation
 * @author jnp3
 * @author dazais
 *
 */
public class ProcessMDN {
	
	public ProcessMDN(){
		
	}
	
	public void validate(ErrorRecorder er, Part p){
		
		MDNValidator validator = new MDNValidatorImpl();
		
		// Process envelope for header searching function
		ProcessEnvelope procEnv = new ProcessEnvelope();
		
		String dispNotifTo = procEnv.searchHeaderSimple(p, "disposition-notification-to");
		String originalRecipient = procEnv.searchHeaderSimple(p, "original-recipient");
		String reportingUA = procEnv.searchHeaderSimple(p, "reporting-ua");
		String mdnGateway = procEnv.searchHeaderSimple(p, "mdn-gateway");
		String finalRecipient = procEnv.searchHeaderSimple(p, "final-recipient");
		String originalMessageID = procEnv.searchHeaderSimple(p, "original-message-id");
		String disposition = procEnv.searchHeaderSimple(p, "disposition");
		String failure = procEnv.searchHeaderSimple(p, "failure");
		String error = procEnv.searchHeaderSimple(p, "error");
		String warning = procEnv.searchHeaderSimple(p, "warning");
		String extension = procEnv.searchHeaderSimple(p, "extension");
		
		SharedByteArrayInputStream test = null;
		try {
			test = (SharedByteArrayInputStream) p.getContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(test, writer, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String theString = writer.toString();
		System.out.println("Reporting-UA: " + getMDNHeader(theString, "reporting-ua"));
		
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
	
	public String getMDNHeader(String part, String header) {
		String res = "";
		if(checkPresent(part, header)) {
			String[] partSplit = part.split(header + ": ");
			String[] partSplitRight = partSplit[1].split("\n");
			res = partSplitRight[0];
			return res;
		}
		return res;
	}
	
	public boolean checkPresent(String part, String header) {
		if(part.contains(header)) {
			return true;
		} else {
			return false;
		}
	}

}
