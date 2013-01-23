package gov.nist.toolkit.osid.repository;

import gov.nist.toolkit.osid.repository.file.Asset;
import gov.nist.toolkit.osid.repository.file.AssetIterator;
import gov.nist.toolkit.osid.repository.file.PropertiesIterator;
import gov.nist.toolkit.osid.repository.file.TypeIterator;
import gov.nist.toolkit.osid.shared.Id;
import gov.nist.toolkit.osid.shared.LongValueIterator;
import gov.nist.toolkit.osid.shared.Properties;
import gov.nist.toolkit.osid.shared.RepositoryException;

import java.io.Serializable;
import java.util.Calendar;

public interface IRepository {

	public  Id copyAsset(Asset asset) throws RepositoryException;

	public  Asset createAsset(String displayName, String description,
			IType assetType) throws RepositoryException;

	public  void deleteAsset(Id assetId) throws RepositoryException;

	public  Asset getAsset(Id assetId) throws RepositoryException;

	public  Asset getAssetByDate(Id assetId, Calendar date)
			throws RepositoryException;

	public  LongValueIterator getAssetDates(Id asset)
			throws RepositoryException;

	public  AssetIterator getAssets() throws RepositoryException;

	public  AssetIterator getAssetsBySearch(
			Serializable searchCriteria, IType searchType,
			Properties searchProperties) throws RepositoryException;

	public  AssetIterator getAssetsByType(IType searchType)
			throws RepositoryException;

	public  TypeIterator getAssetTypes() throws RepositoryException;

	public  String getDescription() throws RepositoryException;

	public  String getDisplayName() throws RepositoryException;

	public  Id getId() throws RepositoryException;

	public  PropertiesIterator getProperties()
			throws RepositoryException;

	public  Properties getPropertiesByType(IType propertiesType)
			throws RepositoryException;

	public  TypeIterator getPropertyTypes() throws RepositoryException;

	public  TypeIterator getSearchTypes() throws RepositoryException;

	public  IType getStatus(Id assetId) throws RepositoryException;

	public  TypeIterator getStatusTypes() throws RepositoryException;

	public  IType getType() throws RepositoryException;

	public  void invalidateAsset(Id assetId) throws RepositoryException;

	public  boolean validateAsset(Id assetId)
			throws RepositoryException;

}