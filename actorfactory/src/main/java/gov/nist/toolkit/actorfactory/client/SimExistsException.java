package gov.nist.toolkit.actorfactory.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created by bill on 10/10/15.
 */
public class SimExistsException extends Exception implements IsSerializable {

    public SimExistsException(String msg) {
        super(msg);
    }

    public SimExistsException() {}
}
