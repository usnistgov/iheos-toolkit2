package gov.nist.toolkit.xdsexception.client;

public class NoResultsException extends XdsInternalException {
    private static final long serialVersionUID = 1L;

    public NoResultsException() { super(""); }

    public NoResultsException(String reason) {
        super("Internal Error");
    }


    public NoResultsException(String msg, Throwable cause) {
        super("No Results " + msg, cause);
    }

}
