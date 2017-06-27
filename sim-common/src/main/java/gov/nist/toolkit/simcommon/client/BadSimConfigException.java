package gov.nist.toolkit.simcommon.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created by bill on 10/10/15.
 */
public class BadSimConfigException extends Exception implements IsSerializable {
    public BadSimConfigException(String msg) {
        super(msg);
    }
}
