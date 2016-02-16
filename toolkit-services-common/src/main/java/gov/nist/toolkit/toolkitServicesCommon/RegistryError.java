package gov.nist.toolkit.toolkitServicesCommon;

/**
 *
 */
public interface RegistryError {
    String getErrorCode();

    String getErrorContext();

    String getLocation();

    ResponseStatusType getStatus();
}
