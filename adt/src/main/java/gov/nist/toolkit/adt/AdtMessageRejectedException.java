package gov.nist.toolkit.adt;

public class AdtMessageRejectedException extends Exception {

	private static final long serialVersionUID = 2822367025321122064L;

	public AdtMessageRejectedException() {
		super();
	}

	public AdtMessageRejectedException(String message) {
		super(message);
	}

	public AdtMessageRejectedException(Throwable cause) {
		super(cause);
	}

	public AdtMessageRejectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AdtMessageRejectedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
