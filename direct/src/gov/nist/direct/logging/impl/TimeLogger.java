package gov.nist.direct.logging.impl;

import java.io.File;
import java.io.IOException;

import javax.mail.internet.MimeMessage;

import gov.nist.direct.logging.Logger;
import gov.nist.direct.logging.utils.LogWriter;
import gov.nist.timer.MessageTimestamp;
import gov.nist.timer.SendHistorySingleton;
import gov.nist.timer.impl.DirectMessageTimestamp;


// 3) Logging expiration times (how long to wait for MDN before chaning status from WAITING to NO-RESPONSE)


public class TimeLogger implements Logger {
	public int MDN_EXPIRATION_TIME;

	@Override
	public boolean log(Object o, File f) throws IOException {
	
		// Get Direct Message send history
		//SendHistorySingleton directHistory = SendHistorySingleton.getSendHistory();
		DirectMessageTimestamp directStamp = new DirectMessageTimestamp(null, null);
		LogWriter.write(f, directStamp.toString());
		
		return false;
	}




}
