package gov.nist.toolkit.registrysupport.logging;



public interface ErrorLogger {

//	public void add_warning(String code, String msg, String location, LogMessage log_message);
	public void add_error(String code, String msg, String location, String resource, LogMessage log_message);

	
}
