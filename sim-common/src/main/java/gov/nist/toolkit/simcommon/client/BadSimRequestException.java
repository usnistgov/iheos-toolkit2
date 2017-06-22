package gov.nist.toolkit.simcommon.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created by bill on 10/28/15.
 */
public class BadSimRequestException  extends Exception implements IsSerializable {
    public BadSimRequestException(String msg) {
        super(msg);
    }
}
