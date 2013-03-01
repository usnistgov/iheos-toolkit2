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
	public Date expirationDate; // delay after which MDN is considered as arriving too late

	// MDN message, received
	public Date mdnReceivedDate;
	public String status;
	public String label;

	public MessageLog(String _testReference, String _transactionType, String _messageType, String _messageId, Date _expirationDate, Date _mdnReceivedDate, String _status, String _label){
		testReference = _testReference;
		transactionType = _transactionType;
		messageType = _messageType;
		messageId = _messageId;
		expirationDate = _expirationDate;
		mdnReceivedDate = _mdnReceivedDate;
		status = _status;
		label = _label;

	}
	
	public MessageLog() {}

}
