package gov.nist.toolkit.simcommon.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created by bill on 10/25/15.
 */
public class SimPropertyTypeConflictException extends Exception implements IsSerializable {
    String expected;
    String found;
    String propertyName;

    public SimPropertyTypeConflictException(String propertyName, String expected, String found) {
        super(String.format("Property %s is of wrong type - expected %s found %s", propertyName, expected, found));
        this.expected = expected;
        this.found = found;
        this.propertyName = propertyName;
    }

    public SimPropertyTypeConflictException() {}
}
