package gov.nist.toolkit.toolkitServicesCommon;

/**
 * Access to the collectiob of values that define a Simulator ID
 */
public interface SimId {
    /**
     * Simulator user (same as TestSession in Toolkit UI)
     * @return value
     */
    String getUser();

    /**
     * Simulator ID
     * @return value
     */
    String getId();

    /**
     * Name of actor type that defines this simulator.
     * @return value
     */
    String getActorType();

    /**
     * Name of environment defined for this simulator.
     * @return value
     */
    String getEnvironmentName();

    /**
     * Internal representation of Simulator ID.  This may change at any time.
     * This is used as the Site name
     * @return value
     */
    String getFullId();

    boolean isFhir();
}
