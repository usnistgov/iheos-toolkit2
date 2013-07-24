package gov.nist.toolkit.repository.api;

import java.io.File;

/**
 * Asset manages the Asset itself.  Assets have content as well as Records
 * appropriate to the AssetType and RecordStructures for the Asset.  Assets
 * may also contain other Assets.
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
public interface Asset {
    /**
     * Update the display name for this Asset.
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
     * Update the date at which this Asset is effective.
     *
     * @param effectiveDate (the number of milliseconds since January 1, 1970,
     *        00:00:00 GMT)
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
     *         gov.nist.toolkit.repository.api.RepositoryException#EFFECTIVE_PRECEDE_EXPIRATION}
     * 
     *         
     *         06/24/13 
     *         Removed 'effectiveDate'.
     *         
     *             void updateEffectiveDate(long effectiveDate)
        throws gov.nist.toolkit.repository.api.RepositoryException;
     *         
     *              
     */


    /**
     * Update the date at which this Asset expires.
     *
     * @param expirationDate (the number of milliseconds since January 1, 1970,
     *        00:00:00 GMT)
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
     *         gov.nist.toolkit.repository.api.RepositoryException#EFFECTIVE_PRECEDE_EXPIRATION}
     *         

    void updateExpirationDate(long expirationDate)
            throws gov.nist.toolkit.repository.api.RepositoryException;
     */
    
    /**
     * 
     * Update the date at which this Asset expires.
     * 
     * @param expirationDate in HL7 2.4 format: "YYYY[MM[DD]]"
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
     *         gov.nist.toolkit.repository.api.RepositoryException#EFFECTIVE_PRECEDE_EXPIRATION}
     *         
     *         06/24/13 Updated return type.
     *         
     */
    
    void updateExpirationDate(String expirationDate)
            throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * 
     * Update the date when this Asset was created.
     * 
     * @param createdDate in HL7 2.4 format: "YYYY[MM[DD]]"
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
     *         gov.nist.toolkit.repository.api.RepositoryException#EFFECTIVE_PRECEDE_EXPIRATION}
     * 
     *         (NIST)
     *         06/24/13 Added method
     */
    void setCreatedDate(String createdDate)
            throws gov.nist.toolkit.repository.api.RepositoryException;
    
    /**
     * Get the display name for this Asset.
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
     * Get the description for this Asset.
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
    String getDescription() throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the unique Id for this Asset.
     *
     * @return org.osid.shared.Id A unique Id that is usually set by a create
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
     * Get the AssetType of this Asset.  AssetTypes are used to categorize
     * Assets.
     *
     * @return org.osid.shared.Type
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
    gov.nist.toolkit.repository.api.Type getAssetType()
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the date at which this Asset is effective.
     *
     * @return long the number of milliseconds since January 1, 1970, 00:00:00
     *         GMT
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
     *         
     *         06/24/13 
     *         Removed 'effectiveDate'.
		
	 * 			long getEffectiveDate() throws gov.nist.toolkit.repository.api.RepositoryException;	
     */


    /**
     * Get the date at which this Asset expires.
     *
     * @return long the number of milliseconds since January 1, 1970, 00:00:00
     *         GMT
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
     *         
     *         
     *             long getExpirationDate() throws gov.nist.toolkit.repository.api.RepositoryException;
     */


    /**
     * Get the date at which this Asset expires.
     *
     * @return String 
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
     *         
     *         06/24/13 Updated to the String return type.
     *             
     */
    String getExpirationDate() throws gov.nist.toolkit.repository.api.RepositoryException;
    
    /**
     * Get the date when the Asset was created. 
     *
     * @return String 
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
     *         
     *         06/24/13 Updated to the String return type.
     *             
     */
    String getCreatedDate() throws gov.nist.toolkit.repository.api.RepositoryException;
    
    /**
     * Update the description for this Asset.
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
    void updateDescription(String description)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the Id of the Repository in which this Asset resides.  This is set
     * by the Repository's createAsset method.
     *
     * @return org.osid.shared.Id A unique Id that is usually set by a create
     *         method's implementation. repositoryId
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
    public gov.nist.toolkit.repository.api.Id getRepository()
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get an Asset's mimeType.
     * @return
     * @throws gov.nist.toolkit.repository.api.RepositoryException
     */
    public String getMimeType() 
    		throws gov.nist.toolkit.repository.api.RepositoryException;
    /**
     * Get an Asset's content.  This method can be a convenience if one is not
     * interested in all the structure of the Records.
     *
     * @return java.io.Serializable
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
    public byte[] getContent()
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * This method is used to determine the file extension based on the mimeType (if applicable)
     * API Extension
     * @return
     */
    public String[] getContentExtension();
    
    /**
     * Update an Asset's content, saving in the specified mimeType.
     *
     * @param content
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
	public void updateContent(String content, String mimeType)
	        throws gov.nist.toolkit.repository.api.RepositoryException;
	
	
    /**
     * Update an Asset's content.
     *
     * @param content
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
    public void updateContent(byte[] content)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Add an Asset to this Asset.
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
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_ID UNKNOWN_ID},
     *         {@link gov.nist.toolkit.repository.api.RepositoryException#ALREADY_ADDED
     *         ALREADY_ADDED}
     */
    public void addAsset(gov.nist.toolkit.repository.api.Id assetId)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Remove an Asset from this Asset.  This method does not delete the Asset
     * from the Repository.
     *
     * @param assetId
     * @param includeChildren
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
    public void removeAsset(gov.nist.toolkit.repository.api.Id assetId, boolean includeChildren)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the Assets in this Asset.  Iterators return a set, one at a
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
     * Get all the Assets of the specified AssetType in this Repository.
     * Iterators return a set, one at a time.
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

	public File getPropFile() 
			throws RepositoryException;
	
    public String getProperty(String key)
            throws gov.nist.toolkit.repository.api.RepositoryException;
    
    public void setProperty(String key, String value)
            throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Create a new Asset Record of the specified RecordStructure.   The
     * implementation of this method sets the Id for the new object.
     *
     * @param recordStructureId
     *
     * @return Record
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
//    public Record createRecord(org.osid.shared.Id recordStructureId)
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Add the specified RecordStructure and all the related Records from the
     * specified asset.  The current and future content of the specified
     * Record is synchronized automatically.
     *
     * @param assetId
     * @param recordStructureId
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
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_ID UNKNOWN_ID},
     *         {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#ALREADY_INHERITING_STRUCTURE
     *         ALREADY_INHERITING_STRUCTURE}
     */
//    public void inheritRecordStructure(org.osid.shared.Id assetId,
//        org.osid.shared.Id recordStructureId)
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Add the specified RecordStructure and all the related Records from the
     * specified asset.
     *
     * @param assetId
     * @param recordStructureId
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
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_ID UNKNOWN_ID},
     *         {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#CANNOT_COPY_OR_INHERIT_SELF
     *         CANNOT_COPY_OR_INHERIT_SELF}
     */
//    public void copyRecordStructure(org.osid.shared.Id assetId,
//        org.osid.shared.Id recordStructureId)
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Delete a Record.  If the specified Record has content that is inherited
     * by other Records, those other Records will not be deleted, but they
     * will no longer have a source from which to inherit value changes.
     *
     * @param recordId
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
//    public void deleteRecord(org.osid.shared.Id recordId)
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the Records for this Asset.  Iterators return a set, one at a
     * time.
     *
     * @return RecordIterator  The order of the objects returned by the
     *         Iterator is not guaranteed.
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
//    public RecordIterator getRecords()
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the Records of the specified RecordStructure for this Asset.
     * Iterators return a set, one at a time.
     *
     * @param recordStructureId
     *
     * @return RecordIterator  The order of the objects returned by the
     *         Iterator is not guaranteed.
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
//    public RecordIterator getRecordsByRecordStructure(
//        org.osid.shared.Id recordStructureId)
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the Records of the specified RecordStructureType for this Asset.
     * Iterators return a set, one at a time.
     *
     * @param recordStructureType
     *
     * @return RecordIterator  The order of the objects returned by the
     *         Iterator is not guaranteed.
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
//    public RecordIterator getRecordsByRecordStructureType(
//        org.osid.shared.Type recordStructureType)
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the RecordStructures for this Asset.  RecordStructures are used
     * to categorize information about Assets.  Iterators return a set, one at
     * a time.
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
//    public RecordStructureIterator getRecordStructures()
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the RecordStructure associated with this Asset's content.
     *
     * @return RecordStructure
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
//    public RecordStructure getContentRecordStructure()
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the Record for this Asset that matches this Record's unique Id.
     *
     * @param recordId
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
//    Record getRecord(org.osid.shared.Id recordId)
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the Part for a Record for this Asset that matches this Part's unique
     * Id.
     *
     * @param partId
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
//    Part getPart(org.osid.shared.Id partId)
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the Value of the Part of the Record for this Asset that matches this
     * Part's unique Id.
     *
     * @param partId
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
//    java.io.Serializable getPartValue(org.osid.shared.Id partId)
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the Parts of the Records for this Asset that are based on this
     * RecordStructure PartStructure's unique Id.
     *
     * @return partStructureId
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
//    PartIterator getPartsByPartStructure(org.osid.shared.Id partStructureId)
//        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the Values of the Parts of the Records for this Asset that are based
     * on this RecordStructure PartStructure's unique Id.
     *
     * @return partStructureId
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
//    org.osid.shared.ObjectIterator getPartValuesByPartStructure(
//        org.osid.shared.Id partStructureId)
//        throws gov.nist.toolkit.repository.api.RepositoryException;

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
