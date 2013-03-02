package gov.nist.direct.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MessageLog  implements IsSerializable {
	// general attributes
	public String testReference;
	public String transactionType; // DirectSend or DirectReceive
	public String messageType; // Direct or MDN

	// attributes relevant to the Direct message sent
	public String messageId;
	public String expirationDate; // delay after which MDN is considered as arriving too late

	// MDN message, received
	public String mdnReceivedDate;
	public String status;
	public String label;

	public MessageLog( String _transactionType, String _messageType, String _messageId, String expirationDate2, String mdnReceivedDate2, String _status, String _label){
		transactionType = _transactionType;
		messageType = _messageType;
		messageId = _messageId;
		expirationDate = expirationDate2;
		mdnReceivedDate = mdnReceivedDate2;
		status = _status;
		label = _label;

	}
	
	public MessageLog() {}

}
