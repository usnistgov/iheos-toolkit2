package gov.nist.timer;

import gov.nist.timer.impl.DirectMessageTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * The SendHistory singleton keeps track of all sent messages.
 * @author dazais
 *
 */
public class SendHistorySingleton extends ArrayList<DirectMessageTimestamp>{
	ArrayList<DirectMessageTimestamp> history;

		private static SendHistorySingleton sendHistory;
		
		private SendHistorySingleton() {
			history = new ArrayList<DirectMessageTimestamp>();
		}
		
		public static synchronized SendHistorySingleton getSendHistory() {
			if (sendHistory == null) {
				sendHistory = new SendHistorySingleton();
			}
			return sendHistory;
		}

		/**
		 * Gets (from saved history) the date at which a given message was sent.
		 * @param messageID
		 * @return
		 */
		public Date getMessageSendTime(String messageID) {
			Iterator<DirectMessageTimestamp> it = history.iterator();
			Date date = null;
			DirectMessageTimestamp ts;
			while(it.hasNext()){
				ts = (DirectMessageTimestamp) it.next();
				if (ts.getMessageID() == messageID){
					date = ts.getTimestamp();
				}
			}
			return date;
			
		}
		
//		@Override
//		public Object clone() throws CloneNotSupportedException {
//			throw new CloneNotSupportedException();
//		}
		
	

}
