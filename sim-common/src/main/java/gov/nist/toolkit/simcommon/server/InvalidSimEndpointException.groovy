package gov.nist.toolkit.simcommon.server;

/**
 *
 */
public class InvalidSimEndpointException extends Exception {

    InvalidSimEndpointException(String endpoint, String msg) {
        super("Not a valid Sim Endpoint ${endpoint} - ${msg}");
    }
}
