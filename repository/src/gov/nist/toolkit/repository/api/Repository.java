package gov.nist.toolkit.repository.api;


/**
 * Repository manages Assets of various Types and information about the Assets.
 * Assets are created, persisted, and validated by the Repository.  When
 * initially created, an Asset has an immutable Type and unique Id and its
 * validation status is false.  In this state, all methods can be called, but
 * integrity checks are not enforced.  When the Asset and its Records are
 * ready to be validated, the validateAsset method checks the Asset and sets
 * the validation status.  When working with a valid Asset, all methods
 * include integrity checks and an exception is thrown if the activity would
 * result in an inappropriate state.  Optionally, the invalidateAsset method
 * can be called to release the requirement for integrity checks, but the
 * Asset will not become valid again, until validateAsset is called and the
 * entire Asset is checked.
 * 
 * <p>
 * OSID Version: 2.0
 * </p>
 * 
 * <p>
 * Licensed under the {@link org.osid.SidLicense MIT
 * O.K.I&#46; OSID Definition License}.
 * </p>
 */
public interface Repository extends java.io.Serializable {
    /**
     * Update the display name for this Repository.
     *
     * @param displayName
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}
     */
    void updateDisplayName(String displayName)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the display name for this Repository.
     *
     * @return String the display name
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
    String getDisplayName() throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the unique Id for this Repository.
     *
     * @return gov.nist.toolkit.repository.api.Id A unique Id that is usually set by a create
     *         method's implementation.
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
    gov.nist.toolkit.repository.api.Id getId() throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the RepositoryType of this Repository.
     *
     * @return gov.nist.toolkit.repository.api.Type
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
    gov.nist.toolkit.repository.api.Type getType()
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the description for this Repository.
     *
     * @return String the description
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
    public String getDescription()
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Update the description for this Repository.
     *
     * @param description
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}
     */
    public void updateDescription(String description)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Create a new Asset of this AssetType in this Repository.  The
     * implementation of this method sets the Id for the new object.
     *
     * @return Asset
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_TYPE
     *         UNKNOWN_TYPE}
     */
    Asset createAsset(String displayName, String description,
        gov.nist.toolkit.repository.api.Type assetType)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Delete an Asset from this Repository.
     *
     * @param assetId
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_ID UNKNOWN_ID}
     */
    void deleteAsset(gov.nist.toolkit.repository.api.Id assetId)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the Assets in this Repository.  Iterators return a set, one at a
     * time.
     *
     * @return AssetIterator  The order of the objects returned by the Iterator
     *         is not guaranteed.
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
    AssetIterator getAssets() throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the Assets of the specified AssetType in this Asset.  Iterators
     * return a set, one at a time.
     *
     * @return AssetIterator  The order of the objects returned by the Iterator
     *         is not guaranteed.
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_TYPE
     *         UNKNOWN_TYPE}
     */
    public AssetIterator getAssetsByType(gov.nist.toolkit.repository.api.Type assetType)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the AssetTypes in this Repository.  AssetTypes are used to
     * categorize Assets.  Iterators return a set, one at a time.
     *
     * @return gov.nist.toolkit.repository.api.TypeIterator  The order of the objects returned
     *         by the Iterator is not guaranteed.
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
    gov.nist.toolkit.repository.api.TypeIterator getAssetTypes()
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the Properties of this Type associated with this Repository.
     *
     * @return gov.nist.toolkit.repository.api.Properties
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_TYPE
     *         UNKNOWN_TYPE}
     */
    gov.nist.toolkit.repository.api.Properties getPropertiesByType(
        gov.nist.toolkit.repository.api.Type propertiesType)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the Property Types for  Repository.
     *
     * @return gov.nist.toolkit.repository.api.TypeIterator
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
    gov.nist.toolkit.repository.api.TypeIterator getPropertyTypes()
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the Properties associated with this Repository.
     *
     * @return gov.nist.toolkit.repository.api.PropertiesIterator
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
    gov.nist.toolkit.repository.api.PropertiesIterator getProperties()
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the RecordStructures in this Repository.  RecordStructures are
     * used to categorize information about Assets.  Iterators return a set,
     * one at a time.
     *
     * @return RecordStructureIterator  The order of the objects returned by
     *         the Iterator is not guaranteed.
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
//    RecordStructureIterator getRecordStructures()
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the RecordStructures with the specified RecordStructureType in
     * this Repository.  RecordStructures are used to categorize information
     * about Assets.  Iterators return a set, one at a time.
     *
     * @param recordStructureType
     *
     * @return RecordStructureIterator  The order of the objects returned by
     *         the Iterator is not guaranteed.
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
//    RecordStructureIterator getRecordStructuresByType(
//        gov.nist.toolkit.repository.api.Type recordStructureType)
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the RecordStructures that this AssetType must support.
     * RecordStructures are used to categorize information about Assets.
     * Iterators return a set, one at a time.
     *
     * @param assetType
     *
     * @return RecordStructureIterator  The order of the objects returned by
     *         the Iterator is not guaranteed.
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_TYPE
     *         UNKNOWN_TYPE}
     */
//    RecordStructureIterator getMandatoryRecordStructures(
//        gov.nist.toolkit.repository.api.Type assetType)
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the SearchTypes supported by this Repository.  Iterators return
     * a set, one at a time.
     *
     * @return gov.nist.toolkit.repository.api.TypeIterator  The order of the objects returned
     *         by the Iterator is not guaranteed.
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
    gov.nist.toolkit.repository.api.TypeIterator getSearchTypes()
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the StatusTypes supported by this Repository.  Iterators return
     * a set, one at a time.
     *
     * @return gov.nist.toolkit.repository.api.TypeIterator  The order of the objects returned
     *         by the Iterator is not guaranteed.
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
    gov.nist.toolkit.repository.api.TypeIterator getStatusTypes()
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the StatusType of the Asset with the specified unique Id.
     *
     * @return gov.nist.toolkit.repository.api.Type
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_ID UNKNOWN_ID}
     */
    gov.nist.toolkit.repository.api.Type getStatus(gov.nist.toolkit.repository.api.Id assetId)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Validate all the Records for an Asset and set its status Type
     * accordingly.  If the Asset is valid, return true; otherwise return
     * false.  The implementation may throw an Exception for any validation
     * failures and use the Exception's message to identify specific causes.
     *
     * @param assetId
     *
     * @return boolean
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_ID UNKNOWN_ID}
     */
    public boolean validateAsset(gov.nist.toolkit.repository.api.Id assetId)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Set the Asset's status Type accordingly and relax validation checking
     * when creating Records and Parts or updating Parts' values.
     *
     * @param assetId
     *
     * @return boolean
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_ID UNKNOWN_ID}
     */
    public void invalidateAsset(gov.nist.toolkit.repository.api.Id assetId)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the Asset with the specified unique Id.
     *
     * @param assetId
     *
     * @return Asset
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_ID UNKNOWN_ID}
     */
    public Asset getAsset(gov.nist.toolkit.repository.api.Id assetId)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the Asset with the specified unique Id that is appropriate for the
     * date specified.  The specified date allows a Repository implementation
     * to support Asset versioning.
     *
     * @param assetId
     * @param date the number of milliseconds since January 1, 1970, 00:00:00
     *        GMT
     *
     * @return Asset
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NO_OBJECT_WITH_THIS_DATE
     *         NO_OBJECT_WITH_THIS_DATE}
     */
    public Asset getAssetByDate(gov.nist.toolkit.repository.api.Id assetId, long date)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the dates for the Asset with the specified unique Id.  These
     * dates allows a Repository implementation to support Asset versioning.
     *
     * @param assetId
     *
     * @return gov.nist.toolkit.repository.api.LongValueIterator (a date is the number of
     *         milliseconds since January 1, 1970, 00:00:00 GMT)
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}
     */
    public gov.nist.toolkit.repository.api.LongValueIterator getAssetDates(
        gov.nist.toolkit.repository.api.Id assetId)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Perform a search of the specified Type and get all the Assets that
     * satisfy the SearchCriteria.  Iterators return a set, one at a time.
     *
     * @param searchCriteria
     * @param searchType
     * @param searchProperties
     *
     * @return AssetIterator  The order of the objects returned by the Iterator
     *         is not guaranteed.
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_TYPE
     *         UNKNOWN_TYPE}
     */
    public AssetIterator getAssetsBySearch(
        java.io.Serializable searchCriteria, gov.nist.toolkit.repository.api.Type searchType,
        gov.nist.toolkit.repository.api.Properties searchProperties)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Create a copy of an Asset.  The Id, AssetType, and Repository for the
     * new Asset is set by the implementation.  
     *
     * @param asset
     *
     * @return gov.nist.toolkit.repository.api.Id
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#NULL_ARGUMENT
     *         NULL_ARGUMENT}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_ID UNKNOWN_ID}
     */
    gov.nist.toolkit.repository.api.Id copyAsset(gov.nist.toolkit.repository.api.Asset asset)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * This method indicates whether this implementation supports Repository
     * methods getAssetsDates() and getAssetByDate()
     *
     * @return boolean false indicates that these methods will throw {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, true indicates this implementation supports
     *         Repository methods getAssetsDates() and getAssetByDate()
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
    boolean supportsVersioning() throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * This method indicates whether this implementation supports Repository
     * methods: copyAsset, deleteAsset, invalidateAsset, updateDescription,
     * updateDisplayName. Asset methods: addAsset, copyRecordStructure,
     * createRecord, deleteRecord, inheritRecordStructure, removeAsset,
     * updateContent, updateDescription, updateDisplayName,
     * updateEffectiveDate, updateExpirationDate. Part methods: createPart,
     * deletePart, updateDisplayName, updateValue. PartStructure methods:
     * updateDisplayName, validatePart. Record methods: createPart,
     * deletePart, updateDisplayName. RecordStructure methods:
     * updateDisplayName, validateRecord.
     *
     * @return boolean false indicates that these methods will throw {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}, true indicates this implementation supports
     *         Repository methods: copyAsset, deleteAsset, invalidateAsset,
     *         updateDescription, updateDisplayName. Asset methods: addAsset,
     *         copyRecordStructure, createRecord, deleteRecord,
     *         inheritRecordStructure, removeAsset, updateContent,
     *         updateDescription, updateDisplayName, updateEffectiveDate,
     *         updateExpirationDate. Part methods: createPart, deletePart,
     *         updateDisplayName, updateValue. PartStructure methods:
     *         updateDisplayName, validatePart. Record methods: createPart,
     *         deletePart, updateDisplayName. RecordStructure methods:
     *         updateDisplayName, validateRecord.
     *
     * @throws gov.nist.toolkit.repository.api.RepositoryException An exception with one of
     *         the following messages defined in
     *         gov.nist.toolkit.repository.api.RepositoryException may be thrown: {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#OPERATION_FAILED
     *         OPERATION_FAILED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
    boolean supportsUpdate() throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * <p>
     * MIT O.K.I&#46; SID Definition License.
     * </p>
     * 
     * <p>
     * <b>Copyright and license statement:</b>
     * </p>
     * 
     * <p>
     * Copyright &copy; 2003 Massachusetts Institute of     Technology &lt;or
     * copyright holder&gt;
     * </p>
     * 
     * <p>
     * This work is being provided by the copyright holder(s)     subject to
     * the terms of the O.K.I&#46; SID Definition     License. By obtaining,
     * using and/or copying this Work,     you agree that you have read,
     * understand, and will comply     with the O.K.I&#46; SID Definition
     * License.
     * </p>
     * 
     * <p>
     * THE WORK IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY     KIND, EXPRESS
     * OR IMPLIED, INCLUDING BUT NOT LIMITED TO     THE WARRANTIES OF
     * MERCHANTABILITY, FITNESS FOR A     PARTICULAR PURPOSE AND
     * NONINFRINGEMENT. IN NO EVENT SHALL     MASSACHUSETTS INSTITUTE OF
     * TECHNOLOGY, THE AUTHORS, OR     COPYRIGHT HOLDERS BE LIABLE FOR ANY
     * CLAIM, DAMAGES OR     OTHER LIABILITY, WHETHER IN AN ACTION OF
     * CONTRACT, TORT     OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
     * WITH     THE WORK OR THE USE OR OTHER DEALINGS IN THE WORK.
     * </p>
     * 
     * <p>
     * <b>O.K.I&#46; SID Definition License</b>
     * </p>
     * 
     * <p>
     * This work (the &ldquo;Work&rdquo;), including any     software,
     * documents, or other items related to O.K.I&#46;     SID definitions, is
     * being provided by the copyright     holder(s) subject to the terms of
     * the O.K.I&#46; SID     Definition License. By obtaining, using and/or
     * copying     this Work, you agree that you have read, understand, and
     * will comply with the following terms and conditions of     the
     * O.K.I&#46; SID Definition License:
     * </p>
     * 
     * <p>
     * You may use, copy, and distribute unmodified versions of     this Work
     * for any purpose, without fee or royalty,     provided that you include
     * the following on ALL copies of     the Work that you make or
     * distribute:
     * </p>
     * 
     * <ul>
     * <li>
     * The full text of the O.K.I&#46; SID Definition License in a location
     * viewable to users of the redistributed Work.
     * </li>
     * </ul>
     * 
     * 
     * <ul>
     * <li>
     * Any pre-existing intellectual property disclaimers, notices, or terms
     * and conditions. If none exist, a short notice similar to the following
     * should be used within the body of any redistributed Work:
     * &ldquo;Copyright &copy; 2003 Massachusetts Institute of Technology. All
     * Rights Reserved.&rdquo;
     * </li>
     * </ul>
     * 
     * <p>
     * You may modify or create Derivatives of this Work only     for your
     * internal purposes. You shall not distribute or     transfer any such
     * Derivative of this Work to any location     or any other third party.
     * For purposes of this license,     &ldquo;Derivative&rdquo; shall mean
     * any derivative of the     Work as defined in the United States
     * Copyright Act of     1976, such as a translation or modification.
     * </p>
     * 
     * <p>
     * THE WORK PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,     EXPRESS OR
     * IMPLIED, INCLUDING BUT NOT LIMITED TO THE     WARRANTIES OF
     * MERCHANTABILITY, FITNESS FOR A PARTICULAR     PURPOSE AND
     * NONINFRINGEMENT. IN NO EVENT SHALL     MASSACHUSETTS INSTITUTE OF
     * TECHNOLOGY, THE AUTHORS, OR     COPYRIGHT HOLDERS BE LIABLE FOR ANY
     * CLAIM, DAMAGES OR     OTHER LIABILITY, WHETHER IN AN ACTION OF
     * CONTRACT, TORT     OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
     * WITH     THE WORK OR THE USE OR OTHER DEALINGS IN THE WORK.
     * </p>
     * 
     * <p>
     * The name and trademarks of copyright holder(s) and/or     O.K.I&#46; may
     * NOT be used in advertising or publicity     pertaining to the Work
     * without specific, written prior     permission. Title to copyright in
     * the Work and any     associated documentation will at all times remain
     * with     the copyright holders.
     * </p>
     * 
     * <p>
     * The export of software employing encryption technology     may require a
     * specific license from the United States     Government. It is the
     * responsibility of any person or     organization contemplating export
     * to obtain such a     license before exporting this Work.
     * </p>
     */
}
