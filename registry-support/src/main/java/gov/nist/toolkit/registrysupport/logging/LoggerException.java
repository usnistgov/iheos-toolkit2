package gov.nist.toolkit.registrysupport.logging;

import gov.nist.toolkit.xdsexception.XdsInternalException;

public class LoggerException extends XdsInternalException 
{
	
	public LoggerException(String string)
	{
		super(string);
	}
	
	public LoggerException(String string, Exception e) {
		super(string, e);
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

}
