package gov.nist.toolkit.toolkitServicesCommon;

import java.util.List;

/**
 *
 */
public interface RegistryResponse {
    ResponseStatusType getStatus();
    List<RegistryError> getErrorList();
}
