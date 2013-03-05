package gov.nist.direct.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MessageLog  implements IsSerializable {
	// general attributes
	public String testReference;
	public String transactionType; // DirectSend or DirectReceive
	public String messageType; // Direct or MDN

	// attributes relevant to the Direct message sent
	public String messageId;
	public String expirationDate; // delay after which MDN is considered as arriving too late
	public String directSendDate;
	
	// MDN message, received
	public String mdnReceivedDate;
	public String mdnMessageID;
	public String status;
	public String origDirectMsgStatus;
	public String label;

	public MessageLog( String _transactionType, String _messageType, String _messageId, String _directSendDate, String expirationDate2, String mdnReceivedDate2, String _mdnMessageID, String _status, String _origDirectMsgStatus, String _label){
		transactionType = _transactionType;
		messageType = _messageType;
		messageId = _messageId;
		directSendDate = _directSendDate;
		expirationDate = expirationDate2;
		mdnReceivedDate = mdnReceivedDate2;
		mdnMessageID = _mdnMessageID;
		status = _status;
		origDirectMsgStatus = _origDirectMsgStatus;
		label = _label;

	}
	
	public MessageLog() {}

	@Override
	public String toString(){
		String str = "label" + " " + this.label + "\n" +
	"messageId" + " " +  this.messageId + "\n" +
	"transactionType" + " " + this.transactionType  + "\n" +	
	"messageType" + " " +  this.messageType + "\n"+
	"directSendDate" + " " + this.directSendDate + "\n" +
	"mdnMessageID" + " " + this.mdnMessageID + "\n" +
	"expiration date" + " " + this.expirationDate + "\n" +
	"mdnReceivedDate " + " " + this.mdnReceivedDate + "\n"+
	"origDirectMsgStatus" + " " + this.origDirectMsgStatus + "\n" +
	"status" + " " + this.status + "\n";
	
		return str;
		
	}
//	
//	public static String toString(MessageLog log){
//		String str = "label" + log.getLabel() + "/n" +
//	"messageId" + log.getMessageId() + "/n" +
//	"transactionType" + log.getTransactionType() + "/n" +	
//	"messageType" + log.getMessageType() + "/n" +
//	"expiration date" + log.getExpirationDate() + "/n" +
//	"mdnReceivedDate " + log.getMdnReceivedDate() + "/n"+
//	"status" + log.getStatus() + "/n";
//	
//		return str;
//		
//	}

	public String getTestReference() {
		return testReference;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public String getMessageType() {
		return messageType;
	}

	public String getMessageId() {
		return messageId;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public String getMdnReceivedDate() {
		return mdnReceivedDate;
	}
	
	public String getDirectSendDate() {
		return directSendDate;
	}

	public String getStatus() {
		return status;
	}

	public String getOriginalDirectMessageStatus(){
		return origDirectMsgStatus;
	}
	
	public String getLabel() {
		return label;
	}
	
	
}
