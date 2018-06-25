package gov.nist.toolkit.xdsexception.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class ValidaterNotFoundException extends Exception implements IsSerializable, Serializable {
    private static final long serialVersionUID = 1L;

    public ValidaterNotFoundException() {}

    public ValidaterNotFoundException(String msg) {
        super(msg);
    }
}
