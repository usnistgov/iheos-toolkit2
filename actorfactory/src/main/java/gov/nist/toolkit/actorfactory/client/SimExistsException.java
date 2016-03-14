package gov.nist.toolkit.actorfactory.client;

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
