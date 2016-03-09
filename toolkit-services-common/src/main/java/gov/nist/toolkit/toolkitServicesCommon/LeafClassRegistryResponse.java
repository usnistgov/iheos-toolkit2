package gov.nist.toolkit.toolkitServicesCommon;

/**
 *
 */
public interface LeafClassRegistryResponse extends RegistryResponse, LeafClassList {
    ResponseStatusType getStatus();
}
