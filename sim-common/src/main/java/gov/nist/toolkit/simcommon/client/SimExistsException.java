package gov.nist.toolkit.simcommon.client;

import java.io.Serializable;

/**
 *
 */
public class SimExistsException extends Exception implements Serializable {

    public SimExistsException(String msg) {
        super(msg);
    }

    public SimExistsException() {}
}
