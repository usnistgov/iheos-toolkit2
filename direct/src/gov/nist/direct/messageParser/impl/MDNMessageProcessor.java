package gov.nist.direct.messageParser.impl;

import gov.nist.direct.utils.ParseUtils;
import gov.nist.direct.utils.ValidationUtils;
import gov.nist.direct.validation.impl.ProcessEnvelope;
import gov.nist.timer.SendHistorySingleton;
import gov.nist.timer.TimerUtils;
import gov.nist.timer.impl.DirectMessageTimestamp;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

import java.text.ParseException;
import java.util.Date;

import javax.mail.Part;

/**
 * Parses an MDN message for a message ID. It looks up the ID in the list of timestamps and calculates the time offset between sending and reception.
 * If the offset is less than a predetermined limit AND the MDN is valid (send it to the MDNvalidator), then the test is successful.
 * 
 * @author dazais
 *
 */
public class MDNMessageProcessor {
	public final int ACCEPTED_DELAY_FOR_MDN_RECEPTION = 1000000; // (400ms max of RTT + processing time by other system) x 3
	
	

	private void processMDNMessage(ErrorRecorder er, Part p) throws ParseException {
		
		// Get MDN message ID
		String messageID = ParseUtils.searchHeaderSimple(p, "message-id");
		
		// Get MDN reception time
		String date = ParseUtils.searchHeaderSimple(p, "date");
		Date receiveDate = ValidationUtils.parseDate(date);
		
		// Compares reception time for the MDN to send time for the original Direct message.
		SendHistorySingleton sendHistory = SendHistorySingleton.getSendHistory();
		Date sendDate = sendHistory.getMessageSendTime(messageID);
		
		int timeOffset = TimerUtils.getTimeDifference(receiveDate, sendDate);
		if (timeOffset <= ACCEPTED_DELAY_FOR_MDN_RECEPTION){
			// send to mdn validation
		} else {
			// message an mdn was received but delay was too long
		}
		
		// need to delete regularly outdated message logs from the singleton.
		
	}

}
