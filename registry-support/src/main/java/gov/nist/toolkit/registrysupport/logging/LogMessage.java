package gov.nist.toolkit.registrysupport.logging;


import java.sql.Timestamp;

public interface LogMessage {
	public void setTimeStamp ( Timestamp timestamp  );
	public void setSecure ( boolean isSecure  );
	public void setTestMessage ( String testMessage );
	public void setPass ( boolean pass );
	public void setIP ( String ip ) throws LoggerException;
	public void setCompany ( String companyName ) throws LoggerException;
	public void addParam ( String tableName , String name , String value ) throws LoggerException;
	public void addHTTPParam( String name , String value ) throws LoggerException;
	public void addSoapParam( String name , String value ) throws LoggerException;
	public void addErrorParam( String name , String value ) throws LoggerException;
	public void addOtherParam ( String name , String value ) throws LoggerException;
	public String getMessageID();
	public void writeMessage()  throws LoggerException;
}
