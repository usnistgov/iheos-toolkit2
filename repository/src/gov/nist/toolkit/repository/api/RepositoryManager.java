package gov.nist.toolkit.repository.api;

/**
 * <p>
 * The RepositoryManager supports creating and deleting Repositories and Assets
 * as well as getting the various Types used.
 * </p>
 * 
 * <p>
 * All implementations of OsidManager (manager) provide methods for accessing
 * and manipulating the various objects defined in the OSID package. A manager
 * defines an implementation of an OSID. All other OSID objects come either
 * directly or indirectly from the manager. New instances of the OSID objects
 * are created either directly or indirectly by the manager.  Because the OSID
 * objects are defined using interfaces, create methods must be used instead
 * of the new operator to create instances of the OSID objects. Create methods
 * are used both to instantiate and persist OSID objects.  Using the
 * OsidManager class to define an OSID's implementation allows the application
 * to change OSID implementations by changing the OsidManager package name
 * used to load an implementation. Applications developed using managers
 * permit OSID implementation substitution without changing the application
 * source code. As with all managers, use the OsidLoader to load an
 * implementation of this interface.
 * </p>
 * 
 * <p></p>
 * 
 * <p>
 * OSID Version: 2.0
 * </p>
 * 
 * <p>
 * Licensed under the {@link gov.nist.toolkit.repository.api.SidLicense MIT
 * O.K.I&#46; OSID Definition License}.
 * </p>
 */
public interface RepositoryManager extends gov.nist.toolkit.repository.api.OsidManager {
    /**
     * Create a new Repository of the specified Type.  The implementation of
     * this method sets the Id for the new object.
     *
     * @param displayName
     * @param description
     * @param repositoryType
     *
     * @return Repository
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
    Repository createRepository(String displayName, String description,
        gov.nist.toolkit.repository.api.Type repositoryType)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Delete a Repository.
     *
     * @param repositoryId
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
    void deleteRepository(gov.nist.toolkit.repository.api.Id repositoryId)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the Repositories.  Iterators return a set, one at a time.
     *
     * @return RepositoryIterator  The order of the objects returned by the
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
     *         gov.nist.toolkit.repository.api.RepositoryException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNIMPLEMENTED
     *         UNIMPLEMENTED}
     */
    RepositoryIterator getRepositories()
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the Repositories of the specified Type.  Iterators return a set,
     * one at a time.
     *
     * @param repositoryType
     *
     * @return RepositoryIterator  The order of the objects returned by the
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
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_TYPE
     *         UNKNOWN_TYPE}
     */
    RepositoryIterator getRepositoriesByType(
        gov.nist.toolkit.repository.api.Type repositoryType)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get the Repository with the specified unique Id.
     *
     * @param repositoryId
     *
     * @return Repository
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
    Repository getRepository(gov.nist.toolkit.repository.api.Id repositoryId)
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
     * @return gov.nist.toolkit.repository.api.LongValueIterator
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
     * satisfy the SearchCriteria.  The search is performed for all specified
     * Repositories.  Iterators return a set, one at a time.
     *
     * @param repositories
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
     *         UNKNOWN_TYPE}, {@link
     *         gov.nist.toolkit.repository.api.RepositoryException#UNKNOWN_REPOSITORY
     *         UNKNOWN_REPOSITORY}
     */
    public AssetIterator getAssetsBySearch(Repository[] repositories,
        java.io.Serializable searchCriteria, gov.nist.toolkit.repository.api.Type searchType,
        gov.nist.toolkit.repository.api.Properties searchProperties)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Create in a Repository a copy of an Asset.  The Id, AssetType, and
     * Repository for the new Asset is set by the implementation.  All Records
     * are similarly copied.
     *
     * @param repository
     * @param assetId
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
    gov.nist.toolkit.repository.api.Id copyAsset(Repository repository,
        gov.nist.toolkit.repository.api.Id assetId)
        throws gov.nist.toolkit.repository.api.RepositoryException;

    /**
     * Get all the RepositoryTypes in this RepositoryManager. RepositoryTypes
     * are used to categorize Repositories.  Iterators return a set, one at a
     * time.
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
    gov.nist.toolkit.repository.api.TypeIterator getRepositoryTypes()
        throws gov.nist.toolkit.repository.api.RepositoryException;

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
