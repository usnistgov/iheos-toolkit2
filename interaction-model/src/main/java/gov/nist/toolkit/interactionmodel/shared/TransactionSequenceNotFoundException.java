package gov.nist.toolkit.interactionmodel.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class TransactionSequenceNotFoundException extends Exception implements Serializable, IsSerializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String resource = null;   // pointer back into documentation

	public TransactionSequenceNotFoundException() {}

	public TransactionSequenceNotFoundException(String msg, String resource) {
		super(msg);
		this.resource = resource;
	}

	public TransactionSequenceNotFoundException(String msg, String resource, Throwable cause) {
		super(msg, cause);
		this.resource = resource;
	}

	public String getResource() {
		return resource;
	}

	public String getDetails() {

		return ""; //ExceptionUtil.exception_details(this);
	}
}
