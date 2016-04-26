package gov.nist.toolkit.securityCommon;

/**
 *
 */
public class SecurityParamsFactory {

    static public SecurityParams getSecurityParams(String environName) {
        return new SecurityParamsImpl(environName);
    }
}
