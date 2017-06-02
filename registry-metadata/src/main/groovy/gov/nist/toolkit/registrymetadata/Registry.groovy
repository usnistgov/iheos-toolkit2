package gov.nist.toolkit.registrymetadata

/**
 * Model the Registry
 * This is an interface so that there can be two versions,
 * a local Registry and a Remote one
 */
interface Registry {

    /**
     * Does the object exist in the Registry
     * @param id
     * @return
     */
    boolean exists(UUID id)

    /**
     * is the object a DocumentEntry
     * @param id
     * @return
     */
    boolean isDE(UUID id)

    /**
     * is the object a SubmissionSet
     * @param id
     * @return
     */
    boolean isSS(UUID id)

    /**
     * is the object a Folder
     * @param uuid
     * @return
     */
    boolean isFol(UUID id)

    /**
     * is the object an Association
     * @param id
     * @return
     */
    boolean isASSN(UUID id)

    /**
     * is the object a HasMember Association
     * @param id
     * @return
     */
    boolean isHasMember(UUID id)

    /**
     * is the object an RPLC Association
     * @param id
     * @return
     */
    boolean isRPLC(UUID id)

    /**
     * is the object an APND Assocation
     * @param id
     * @return
     */
    boolean isAPND(UUID id)

    /**
     * is the object an XFRM Assocation
     * @param id
     * @return
     */
    boolean isXFRM(UUID id)

    /**
     * is the object an IsSnapshotOf Assocation
     * @param id
     * @return
     */
    boolean isIsSnapshotOf(UUID id)

    /**
     * is the object a SIGNS Assocation
     * @param id
     * @return
     */
    boolean isSigns(UUID id)

    /**
     * get the sourceObject UUID from the Association
     * @param id of the Assocation
     * @return sourceObject UUID
     */
    UUID source(UUID id)

    /**
     * get the targetObject UUID from the Association
     * @param id of the Assocation
     * @return targetObject UUID
     */
    UUID target(UUID id)

    /**
     * get the Association Type of the Assocation
     * @param id
     * @return
     */
    AssnType assnType(UUID id)

    /**
     * is the Association the only one of its type referencing the object
     * @param obj - object in question
     * @param id - UUID of an Assocation
     * @param type - Association type
     * @return
     */
    boolean onlyAssn(UUID obj, UUID id, AssnType type)

    /**
     * get the set of Associations that are linked to a Document Entry by their
     * sourceObject or targetObject attribute
     * @param de
     * @return
     */
    List<UUID> assnLinkedToDE(UUID de)

    /**
     * add the error (referencing the object with this id) to the RegistryErrorList
     * @param error - error name
     * @param id - UUID of offending object
     */
    void error(def error, UUID id)
}
