package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SmtpMessageStatus implements IsSerializable {
	public String msg_id;
	public String msg_time_sent;
	public String mdn_expiration_date;
	public String mdn_received_date;
	public String mdn_status;
	public String mdn_id;
	
	public SmtpMessageStatus() {};
	
	public SmtpMessageStatus(String id, String time_sent, String expiration, String received, String status, String mdnid) {
		msg_id = id;
		msg_time_sent = time_sent;
		mdn_expiration_date = expiration;
		mdn_received_date = received;
		mdn_status = status;
		mdn_id = mdnid;
	}
}
