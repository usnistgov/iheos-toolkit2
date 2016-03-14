package gov.nist.toolkit.xdstools2.server.smtptools;

import gov.nist.toolkit.xdstools2.client.SmtpMessageStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogAccessMock {
	static Map<String, List<SmtpMessageStatus>> data; 
	
	static {
		SmtpMessageStatus msg1 = new SmtpMessageStatus("msg1", "yesterday1", "tomorrow1", "today1", "Ok",      "mdn1");
		SmtpMessageStatus msg2 = new SmtpMessageStatus("msg2", "yesterday2", "today2",    "----",   "Timeout", "----");
		SmtpMessageStatus msg3 = new SmtpMessageStatus("msg3", "today1", "today3", "yesterday3", "Error", "mdn3");
		SmtpMessageStatus msg4 = new SmtpMessageStatus("msg4", "today1", "today4", "yesterday4", "Ok", "mdn4");
				
		List<SmtpMessageStatus> alist = new ArrayList<SmtpMessageStatus>();
		alist.add(msg1);
		alist.add(msg2);
		List<SmtpMessageStatus> blist = new ArrayList<SmtpMessageStatus>();
		blist.add(msg3);
		blist.add(msg4);

		data = new HashMap<String, List<SmtpMessageStatus>>();
		data.put("bill", alist);
		data.put("lynn", blist);
	}
	
	public List<SmtpMessageStatus> getOutgoingMsgStatus(String user, List<String> msg_ids) {
		if (msg_ids == null)
			return null;
		if (user == null)
			return null;
		List<SmtpMessageStatus> values = new ArrayList<SmtpMessageStatus>();
		List<SmtpMessageStatus> l = LogAccessMock.data.get(user);
		if (l == null)
			return null;
		for (SmtpMessageStatus sms : l) {
			if (msg_ids.contains(sms.msg_id))
				values.add(sms);
		}
		return values;
	}
	
	public List<String> getMsgIds(String user) {
		if (user == null)
			return null;
		List<SmtpMessageStatus> l = LogAccessMock.data.get(user);
		if (l == null)
			return null;
		List<String> ids = new ArrayList<String>();
		for (SmtpMessageStatus sms : l) {
			ids.add(sms.msg_id);
		}
		return ids;
		
	}
	
}
