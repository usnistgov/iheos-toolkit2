package gov.nist.toolkit.simcommon.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 */
public class NoContentException extends Exception implements IsSerializable {
    public NoContentException(String string) {
        super(string);
    }

    public NoContentException() {}

}
