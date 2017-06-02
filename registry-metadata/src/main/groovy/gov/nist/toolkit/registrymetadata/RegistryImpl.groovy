package gov.nist.toolkit.registrymetadata

/**
 *
 */
class RegistryImpl implements Registry {
    @Override
    boolean exists(UUID id) {
        return false
    }

    @Override
    boolean isDE(UUID id) {
        return false
    }

    @Override
    boolean isSS(UUID id) {
        return false
    }

    @Override
    boolean isFol(UUID id) {
        return false
    }

    @Override
    boolean isASSN(UUID id) {
        return false
    }

    @Override
    boolean isHasMember(UUID id) {
        return false
    }

    @Override
    boolean isRPLC(UUID id) {
        return false
    }

    @Override
    boolean isAPND(UUID id) {
        return false
    }

    @Override
    boolean isXFRM(UUID id) {
        return false
    }

    @Override
    boolean isIsSnapshotOf(UUID id) {
        return false
    }

    @Override
    boolean isSigns(UUID id) {
        return false
    }

    @Override
    UUID source(UUID id) {
        return null
    }

    @Override
    UUID target(UUID id) {
        return null
    }

    @Override
    AssnType assnType(UUID id) {
        return null
    }

    @Override
    boolean onlyAssn(UUID obj, UUID id, AssnType type) {
        return false
    }

    @Override
    List<UUID> assnLinkedToDE(UUID de) {
        return null
    }

    @Override
    void error(Object error, UUID id) {

    }
}
