package gov.nist.toolkit.xdsexception;

public class EnvironmentNotSelectedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EnvironmentNotSelectedException(String msg) {
		super(msg);
	}

	public EnvironmentNotSelectedException(String msg, Exception e) {
		super(msg, e);
	}

}
