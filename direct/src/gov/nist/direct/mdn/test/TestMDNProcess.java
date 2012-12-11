package gov.nist.direct.mdn.test;

import java.util.ArrayList;
import java.util.Date;

import gov.nist.timer.SendHistorySingleton;
import gov.nist.timer.impl.DirectMessageTimestamp;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;

import org.junit.Test;

/**
 * Tests class MDNTestingProcess. This class sends out a Direct message and expects an MDN back after a given time out, then validates the MDN.
 * @author dazais
 *
 */
public class TestMDNProcess {
	
	ErrorRecorder er = new TextErrorRecorder();
	
	
	@Test
	public void testMDNProcess(){
		
		// create and send Direct message --> what fn to call?
		
		// logs time at which the message is sent + message ID
		String id = null; // field message-id
		try {
			registerDirectMessageSent(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		// have a separate mdn listener that always listens
		// upon reception of an mdn, the listener forwards the message to the MDN processor.
		
		// the MDN processor parses the MDN for a message ID. It looks up the ID in the list of timestamps and calculates the time offset between sending and reception.
		// If the offset is less than a predetermined limit AND the MDN is valid (send it to the MDNvalidator), then the test is successful.
		
	}
	
	

	/**
	 * To move in class direct send or another class in TestingProcess
	 * 
	 * Logs the time at which a message is sent + matching message ID
	 */
	public void registerDirectMessageSent(String id) throws Exception {
		DirectMessageTimestamp timestamp = new DirectMessageTimestamp(new Date(), id);
		SendHistorySingleton sendHistory = SendHistorySingleton.getSendHistory();
		sendHistory.add(timestamp);
	}
	
	
	
	
}
	
	
	