package gov.nist.toolkit.registrymetadata.deletion

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
    boolean exists(Uuid id)

    /**
     * is the object a DocumentEntry
     * @param id
     * @return
     */
    boolean isDE(Uuid id)

    /**
     * is the object a SubmissionSet
     * @param id
     * @return
     */
    boolean isSS(Uuid id)

    /**
     * is the object a Folder
     * @param Uuid
     * @return
     */
    boolean isFol(Uuid id)

    /**
     * is the object an Association
     * @param id
     * @return
     */
    boolean isASSN(Uuid id)

    /**
     * is the object a HasMember Association
     * @param id
     * @return
     */
    boolean isHasMember(Uuid id)

    /**
     * is the object an RPLC Association
     * @param id
     * @return
     */
    boolean isRPLC(Uuid id)

    /**
     * is the object an APND Assocation
     * @param id
     * @return
     */
    boolean isAPND(Uuid id)

    /**
     * is the object an XFRM Assocation
     * @param id
     * @return
     */
    boolean isXFRM(Uuid id)

    /**
     * is the object an IsSnapshotOf Assocation
     * @param id
     * @return
     */
    boolean isIsSnapshotOf(Uuid id)

    /**
     * is the object a SIGNS Assocation
     * @param id
     * @return
     */
    boolean isSigns(Uuid id)

    /**
     * get the sourceObject Uuid from the Association
     * @param id of the Assocation
     * @return sourceObject Uuid
     */
    Uuid source(Uuid id)

    /**
     * get the targetObject Uuid from the Association
     * @param id of the Assocation
     * @return targetObject Uuid
     */
    Uuid target(Uuid id)

    /**
     * get the Association Type of the Assocation
     * @param id
     * @return
     */
    AssnType assnType(Uuid id)

    /**
     * is the Association the only one of its type referencing the object
     * @param obj - object in question
     * @param id - Uuid of an Assocation
     * @param type - Association type
     * @return
     */
    boolean onlyAssn(Uuid obj, Uuid id, AssnType type)

    /**
     * get the set of Associations that are linked to a Document Entry by their
     * sourceObject or targetObject attribute
     * @param de
     * @return
     */
    List<Uuid> assnLinkedToDE(Uuid de)

    /**
     * get the set of Associations that are linked to a SubmissionSet by their
     * sourceObject or targetObject attribute
     * @param de
     * @return
     */
    List<Uuid> assnLinkedToSS(Uuid de)

    /**
     * get the set of Associations that are linked to a Folder by their
     * sourceObject or targetObject attribute
     * @param de
     * @return
     */
    List<Uuid> assnLinkedToFol(Uuid de)

}
