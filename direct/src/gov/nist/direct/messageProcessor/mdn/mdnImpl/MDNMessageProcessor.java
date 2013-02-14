package gov.nist.direct.messageProcessor.mdn.mdnImpl;

import gov.nist.direct.directValidator.impl.ProcessEnvelope;
import gov.nist.direct.mdn.validate.ProcessMDN;
import gov.nist.direct.messageProcessor.direct.directImpl.DirectMimeMessageProcessor;
import gov.nist.direct.messageProcessor.direct.directImpl.MimeMessageParser;
import gov.nist.direct.messageProcessor.direct.directImpl.WrappedMessageProcessor;
import gov.nist.direct.utils.ParseUtils;
import gov.nist.direct.utils.ValidationSummary;
import gov.nist.direct.utils.ValidationUtils;
import gov.nist.timer.SendHistorySingleton;
import gov.nist.timer.TimerUtils;
import gov.nist.timer.impl.DirectMessageTimestamp;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorder;

import java.text.ParseException;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.apache.mailet.base.mail.MimeMultipartReport;

/**
 * Parses an MDN message for a message ID. It looks up the ID in the list of timestamps and calculates the time offset between sending and reception.
 * If the offset is less than a predetermined limit AND the MDN is valid (send it to the MDNvalidator), then the test is successful.
 * 
 * @author dazais
 *
 */
public class MDNMessageProcessor {
	static Logger logger = Logger.getLogger(DirectMimeMessageProcessor.class);
	
	private byte[] directCertificate;
	private String password;
	ValidationContext vc = new ValidationContext();
	private ValidationSummary validationSummary = new ValidationSummary();
	WrappedMessageProcessor wrappedParser = new WrappedMessageProcessor();
	private int partNumber;
	ErrorRecorder mainEr;
	
	public MDNMessageProcessor(){
		
		// New ErrorRecorder for the MDN validation summary
		mainEr = new GwtErrorRecorder();
		
		
	}
	
	// message is parsed multiple times in following calls. TODO
	public void processMDNMessage(ErrorRecorder er, byte[] inputDirectMessage, byte[] _directCertificate, String _password, ValidationContext vc) {
		directCertificate = _directCertificate;
		password = _password;
		this.vc = vc;
		
		// Check if MDN is encrypted
		
		
		
		// Convert to Java type MultipartReport
		 MimeMultipartReport m = new MimeMultipartReport(inputDirectMessage.toString());
		System.out.println("MimeMultipartReport");
		 
		// Check MDN properties (Date received, Sender, compare to original Direct message)
		 checkMdnMessageProperties(er, inputDirectMessage, _directCertificate, _password, vc);
		 System.out.println("checkMdnMessageProperties");
		 
		 // Validate MDN
		 ProcessMDN mdnv = new ProcessMDN();
		try {
			for (int i=0;i<m.getCount();i++){
					try {
						m.getBodyPart(i);
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mdnv.validate(er, (Part)m);
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		// need to delete regularly outdated message logs from the singleton.
			
	
	}
	
	
	
	
	/**
	 * Checks MDN message properties and coherence compared to the initial Direct Message
	 * (Date received, Sender, compare to original Direct message)
	 */
public void checkMdnMessageProperties(ErrorRecorder er, byte[] inputDirectMessage, byte[] _directCertificate, String _password, ValidationContext vc){


		// Set the part number to 1
		partNumber = 1;
		
		// Parse the message to see if it is wrapped
		wrappedParser.messageParser(er, inputDirectMessage, _directCertificate, _password);
		
		logger.debug("ValidationContext is " + vc.toString());
		
		 MimeMultipartReport m = new MimeMultipartReport(inputDirectMessage.toString());
		Multipart mm = (Multipart)m;
		
		// Get MDN message ID and compare to existing logs
		String messageID = null;
		try {
			/***
			 * issue is here
			 */
			messageID = ParseUtils.searchHeaderSimple(mm.getBodyPart(1), "message-id");
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//look for log
		
		// Get MDN reception time
		String date = null;
		try {
			date = ParseUtils.searchHeaderSimple(mm.getBodyPart(0), "date");
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Date receiveDate = null;
		
		// Compares reception time for the MDN to send time for the original Direct message.
		try {
			receiveDate = ValidationUtils.parseDate(date);
			
			SendHistorySingleton sendHistory = SendHistorySingleton.getSendHistory();
			Date sendDate = sendHistory.getMessageSendTime(messageID);
			
			int timeOffset = TimerUtils.getTimeDifference(receiveDate, sendDate);
			if (timeOffset <= TimerUtils.getACCEPTED_DELAY_FOR_MDN_RECEPTION()){
			} else {
				// message that an mdn was received but delay was too long
				//er.err(null, "MDN processing", "The MDN was received after the authorized delay had expired. The delay is "+ TimerUtils.getACCEPTED_DELAY_FOR_MDN_RECEPTION(),  timeOffset);
			}
				
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



}
