package gov.nist.toolkit.xdsexception;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class XdsInternalException extends XdsException /*implements Serializable, IsSerializable*/ {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    public XdsInternalException() { super("", ""); }

	public XdsInternalException(String reason) {
		super(reason, "Internal Error");
	}


	public XdsInternalException(String msg, Throwable cause) {
		super(msg,  "Internal Error", cause);
	}
}
