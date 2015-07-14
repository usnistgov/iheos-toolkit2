package gov.nist.toolkit.saml.exception;

public class ValidationException extends RuntimeException {


	public ValidationException(String msg) {
		super(msg);
	}

	public ValidationException(Exception e) {
		super(e);
	}
}

