package gov.nist.toolkit.xdsexception;

public class NoSimulatorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NoSimulatorException(String msg) {
		super(msg);
	}

	public NoSimulatorException(String msg, Throwable e) {
		super(msg, e);
	}

}
