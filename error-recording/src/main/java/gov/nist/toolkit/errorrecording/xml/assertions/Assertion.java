package gov.nist.toolkit.errorrecording.xml.assertions;

/**
 * Created by diane on 10/14/2016.
 */
public class Assertion {
    private String assertionID, errorMessage, location, gazelleScheme, gazelleAssertionID;

    // TODO might have to implement 2 first fields using a Map


    public Assertion(String _assertionID, String _errorMessage, String _location, String _gazelleScheme, String _gazelleAssertionID){
        assertionID = _assertionID;
        errorMessage = _errorMessage;
        location = _location;
        gazelleScheme = _gazelleScheme;
        gazelleAssertionID = _gazelleAssertionID;
    }

    public String getAssertionID() {
        return assertionID;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getLocation() {
        return location;
    }

    public String getGazelleScheme() {
        return gazelleScheme;
    }

    public String getGazelleAssertionID() {
        return gazelleAssertionID;
    }
}
